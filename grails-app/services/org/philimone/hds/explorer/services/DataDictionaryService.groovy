package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.philimone.hds.explorer.server.model.enums.DataExportItem
import org.philimone.hds.explorer.server.model.enums.extensions.FormColumnType
import org.philimone.hds.explorer.server.model.main.CoreFormExtension
import org.philimone.hds.explorer.server.model.main.CoreFormExtensionModel
import org.philimone.hds.explorer.server.model.main.extension.ExtensionDatabaseColumns
import org.philimone.hds.forms.model.ColumnGroup
import org.philimone.hds.forms.model.ColumnRepeatGroup
import org.philimone.hds.forms.model.Column
import org.philimone.hds.forms.model.ColumnValue
import org.philimone.hds.forms.model.HForm
import org.philimone.hds.forms.model.enums.SensitiveType
import org.philimone.hds.forms.model.parsers.ExcelFormParser

import javax.annotation.PostConstruct
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

@Transactional(readOnly = true)
class DataDictionaryService {

    private final Map<String, TableMetadata> tablesMap = new ConcurrentHashMap<>()
    private final Map<String, EnumTypeMetadata> enumsMap = new ConcurrentHashMap<>()

    private static final Pattern ENUM_PATTERN = Pattern.compile(".*\\(([^\\)]+)\\)\$")

    public static def finalTableNames = [
            'death'                 : 'Death',
            'enumeration'           : 'Enumeration',
            'household'             : 'Household',
            'head_relationship'      : 'HeadRelationship',
            'in_migration'           : 'InMigration',
            'incomplete_visit'       : 'IncompleteVisit',
            'household_relocation'   : 'HouseholdRelocation',
            'household_proxy_head'    : 'HouseholdProxyHead',
            'marital_relationship'   : 'MaritalRelationship',
            'member'                : 'Member',
            'out_migration'          : 'OutMigration',
            'pregnancy_child'        : 'PregnancyChild',
            'pregnancy_outcome'      : 'PregnancyOutcome',
            'pregnancy_registration' : 'PregnancyRegistration',
            'pregnancy_visit'        : 'PregnancyVisit',
            'pregnancy_visit_child'   : 'PregnancyVisitChild',
            'region'                : 'Region',
            'region_head_relationship': 'RegionHeadRelationship',
            'residency'             : 'Residency',
            'round'                 : 'Round',
            'visit'                 : 'Visit'/*,
            '_user'                 : 'User'*/ //Exclude user table
    ]




    @PostConstruct
    void init() {
        try {
            loadDefaultDataDictionary()
            loadAllExtensionForms()
            registerPrejoinedViewsMetadata()
        } catch (Exception e) {
            log.error("Failed to initialize DataDictionaryService: ${e.message}", e)
        }
    }

    /**
     * Pre-loads all enabled XLS HForm extension forms at startup
     */
    synchronized void loadAllExtensionForms() {
        try {
            List<CoreFormExtension> customForms = CoreFormExtension.findAllByEnabled(true)
            int count = 0
            customForms.each { ext ->
                if (loadExtensionFormMetadata(ext) != null) {
                    count++
                }
            }
            log.info("Loaded ${count} extension forms into Data Dictionary successfully.")
        } catch (Exception e) {
            log.error("Failed loading extension forms into Data Dictionary: ${e.message}", e)
        }
    }

    /**
     * Registers column metadata and statistical labels for all 6 Pre-Joined Views
     */
    synchronized void registerPrejoinedViewsMetadata() {
        // 1. INDEPTH_CORE_RESIDENCY
        TableMetadata indepthCoreMeta = new TableMetadata(
                tableName: DataExportItem.INDEPTH_RESIDENCY_PERSON_TIME.code,
                description: "INDEPTH Standard Core Event History File (EHA / Long Format)"
        )
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "IndividualId", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Individual ID / Member Code"))
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "LocationId", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Location ID / Household Code"))
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "DoB", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Date of Birth"))
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "Sex", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Gender (M/F)", enumTypeName: "Gender"))
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "EventCode", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "INDEPTH Event Code (ENU, BTH, IMG, OMG, EXT, ENT, DTH, OBE)"))
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "EventDate", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Date of Event Occurrence"))
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "residence", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Binary Exposure Indicator (1=Resident, 0=Non-resident/Censored)"))
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "age_at_event", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Age at Event Occurrence (Years)"))
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "region_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Code"))
        indepthCoreMeta.addColumn(new ColumnMetadata(tableName: indepthCoreMeta.tableName, columnName: "region_name", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Name"))
        tablesMap.put(DataExportItem.INDEPTH_RESIDENCY_PERSON_TIME.code, indepthCoreMeta)

        // 2. RESIDENCY_PERSON_TIME
        TableMetadata personTimeMeta = new TableMetadata(
                tableName: DataExportItem.RESIDENCY_PERSON_TIME.code,
                description: "Residency Episodes Dataset (Person-Years / Exposure Format)"
        )
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "member_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Member Code"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "member_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Member Full Name"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "member_gender", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Gender", enumTypeName: "Gender"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "member_dob", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Date of Birth"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "member_age_at_ref_date", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Age at Baseline Reference Date"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "household_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Household Code"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "household_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Household Name"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "start_type", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Residency Start Event Type", enumTypeName: "ResidencyStartType"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "start_date", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Residency Start Date"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "end_type", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Residency End Event Type", enumTypeName: "ResidencyEndType"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "end_date", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Residency End Date"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "exposure_days", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Total Resident Exposure Days"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "person_years", dataType: "DOUBLE", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Total Resident Person-Years"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "region_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Code"))
        personTimeMeta.addColumn(new ColumnMetadata(tableName: personTimeMeta.tableName, columnName: "region_name", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Name"))
        tablesMap.put(DataExportItem.RESIDENCY_PERSON_TIME.code, personTimeMeta)

        // 3. INDEPTH_MATERNAL_PREGNANCY
        TableMetadata indepthMaternalMeta = new TableMetadata(
                tableName: DataExportItem.INDEPTH_MATERNAL_PREGNANCY.code,
                description: "INDEPTH Standard Delivery & Reproductive Dataset"
        )
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "IndividualId", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Mother Individual ID"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "FatherId", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Father Individual ID"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "ChildId", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Child Individual ID"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "EventCode", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "INDEPTH Delivery Event Code (DLV)"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "EventDate", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Date of Delivery / Pregnancy Outcome"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "ObservationDate", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Surveillance Visit Date Recorded"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "ChildSex", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Child Sex / Gender", enumTypeName: "Gender"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "MultiBirth", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Multiple Birth Count (Total Outcomes)"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "LiveBirths", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Live Birth Count"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "StillBirths", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Stillbirth Count"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "LocationId", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Location ID / Household Code"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "mother_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Mother Full Name"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "father_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Father Full Name"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "child_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Child Full Name"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "child_outcome_type", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Child Outcome Type", enumTypeName: "PregnancyOutcomeType"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "mother_age_at_outcome", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Mother Age at Outcome Date"))
        indepthMaternalMeta.addColumn(new ColumnMetadata(tableName: indepthMaternalMeta.tableName, columnName: "region_name", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Name"))
        tablesMap.put(DataExportItem.INDEPTH_MATERNAL_PREGNANCY.code, indepthMaternalMeta)

        // 4. MATERNAL_PREGNANCY
        TableMetadata maternalMeta = new TableMetadata(
                tableName: DataExportItem.MATERNAL_PREGNANCY.code,
                description: "Reproductive History / Maternal Dataset"
        )
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "pregnancy_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Pregnancy Code"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "outcome_date", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Pregnancy Outcome Date"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "mother_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Mother Code"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "mother_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Mother Full Name"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "mother_age_at_outcome", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Mother Age at Outcome Date"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "father_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Father Code"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "father_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Father Full Name"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "number_of_outcomes", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Total Outcomes Count"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "number_of_livebirths", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Live Births Count"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "child_outcome_type", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Child Outcome Type", enumTypeName: "PregnancyOutcomeType"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "child_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Child Code"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "child_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Child Full Name"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "child_gender", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Child Gender", enumTypeName: "Gender"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "household_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Household Code"))
        maternalMeta.addColumn(new ColumnMetadata(tableName: maternalMeta.tableName, columnName: "region_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Code"))
        tablesMap.put(DataExportItem.MATERNAL_PREGNANCY.code, maternalMeta)

        // 5. INDEPTH_HOUSEHOLD_COMPOSITION
        TableMetadata indepthHhCompMeta = new TableMetadata(
                tableName: DataExportItem.INDEPTH_HOUSEHOLD_COMPOSITION.code,
                description: "INDEPTH Standard Household Composition Dataset"
        )
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "LocationId", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Location ID / Household Code"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "IndividualId", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Individual ID / Member Code"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "DoB", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Date of Birth"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "Sex", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Member Gender (M/F)", enumTypeName: "Gender"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "HeadId", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Head of Household ID"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "HeadSex", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Head of Household Gender (M/F)", enumTypeName: "Gender"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "RelationshipToHead", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Relationship to Household Head", enumTypeName: "HeadRelationshipType"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "household_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Household Name"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "household_type", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Household Type", enumTypeName: "HouseholdType"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "household_institution_type", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Institution Type", enumTypeName: "HouseholdInstitutionType"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "member_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Member Full Name"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "head_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Head Full Name"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "age_at_ref_date", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Age at Baseline Reference Date"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "resident_since", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Residency Start Date"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "ProxyHeadName", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Proxy Head Name"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "ProxyHeadRole", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Proxy Head Role", enumTypeName: "ProxyHeadRole"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "region_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Code"))
        indepthHhCompMeta.addColumn(new ColumnMetadata(tableName: indepthHhCompMeta.tableName, columnName: "region_name", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Name"))
        tablesMap.put(DataExportItem.INDEPTH_HOUSEHOLD_COMPOSITION.code, indepthHhCompMeta)

        // 6. HOUSEHOLD_COMPOSITION
        TableMetadata hhCompMeta = new TableMetadata(
                tableName: DataExportItem.HOUSEHOLD_COMPOSITION.code,
                description: "Household Composition Dataset"
        )
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "household_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Household Code"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "household_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Household Name"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "household_type", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Household Type", enumTypeName: "HouseholdType"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "household_institution_type", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Institution Type", enumTypeName: "HouseholdInstitutionType"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "head_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Head of Household Code"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "head_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Head Full Name"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "head_gender", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Head Gender", enumTypeName: "Gender"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "member_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Member Code"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "member_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Member Full Name"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "member_gender", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Member Gender", enumTypeName: "Gender"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "member_dob", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Member Date of Birth"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "member_age_at_ref_date", dataType: "INT", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Member Age at Reference Date"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "relationship_type", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Relationship to Head", enumTypeName: "HeadRelationshipType"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "resident_since", dataType: "DATE", sensitiveType: SensitiveType.DATE, label: "Residency Start Date"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "proxy_head_name", dataType: "VARCHAR", sensitiveType: SensitiveType.PERSON_NAME, label: "Proxy Head Name"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "proxy_head_role", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Proxy Head Role", enumTypeName: "ProxyHeadRole"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "region_code", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Code"))
        hhCompMeta.addColumn(new ColumnMetadata(tableName: hhCompMeta.tableName, columnName: "region_name", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, label: "Region Name"))
        tablesMap.put(DataExportItem.HOUSEHOLD_COMPOSITION.code, hhCompMeta)

        log.info("Successfully registered metadata for all 6 Pre-Joined Views.")
    }

    /**
     * Loads the core database data dictionary from src/main/resources/docs/hds_explorer-database_data_dictionary-v1.2.0.xlsx
     */
    synchronized void loadDefaultDataDictionary() {
        String resourcePath = "docs/hds_explorer-database_data_dictionary-v1.2.0.xlsx"
        try {
            def fileStream = getClass().classLoader.getResourceAsStream(resourcePath)

            if (fileStream != null) {
                parseExcelDictionary(fileStream)
                println("Loaded Data Dictionary successfully from ${resourcePath}. Tables: ${tablesMap.size()}, Enums: ${enumsMap.size()}")
                return
            }
        } catch (Exception e) {
            log.warn("Failed loading dictionary via ResourceLoader: ${e.message}")
        }
    }

    void parseExcelDictionary(InputStream inputStream) {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        try {
            def tablesSheet = workbook.getSheet("all-tables")
            def typesSheet = workbook.getSheet("all-types")

            if (typesSheet != null) {
                parseAllTypesSheet(typesSheet as XSSFSheet)
            }
            if (tablesSheet != null) {
                parseAllTablesSheet(tablesSheet)
            }

            inputStream.close()
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    private void parseAllTypesSheet(XSSFSheet sheet) {
        Iterator<Row> rowIterator = sheet.iterator()
        if (!rowIterator.hasNext()) return

        rowIterator.next() // Skip header row (Type, Code, Label, Description)

        String currentTypeName = null

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next()
            String type = getCellValue(row.getCell(0))
            String code = getCellValue(row.getCell(1))
            String label = getCellValue(row.getCell(2))
            String description = getCellValue(row.getCell(3))

            if (!type) continue

            boolean isHeaderRow = (type.equalsIgnoreCase(code) && (description != null || isSolidFillCell(row.getCell(0))))

            if (isHeaderRow) {
                currentTypeName = type
                EnumTypeMetadata enumType = new EnumTypeMetadata(enumTypeName: currentTypeName, description: description)
                enumsMap.put(currentTypeName, enumType)
            } else if (currentTypeName != null) {
                EnumOptionMetadata option = new EnumOptionMetadata(
                        enumTypeName: currentTypeName,
                        code: code,
                        label: label,
                        description: description
                )
                enumsMap.get(currentTypeName)?.addOption(option)
            }
        }
    }

    private void parseAllTablesSheet(XSSFSheet sheet) {

        Iterator<Row> rowIterator = sheet.iterator()
        if (!rowIterator.hasNext()) return
        rowIterator.next() // Skip header row (table, columnName, dataType, description, label)

        String currentTableName = null

        def finalTables = finalTableNames.keySet()

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next()
            String tableName = getCellValue(row.getCell(0))
            String columnName = getCellValue(row.getCell(1))
            String dataType = getCellValue(row.getCell(2))
            String sensitiveType = getCellValue(row.getCell(3))
            String description = getCellValue(row.getCell(4))
            String label = getCellValue(row.getCell(5))

            if (!tableName || !finalTables.contains(tableName)) continue

            boolean isTableHeader = (tableName.equalsIgnoreCase(columnName) && tableName.equalsIgnoreCase(dataType))

            if (isTableHeader) {
                currentTableName = tableName
                TableMetadata tableMeta = new TableMetadata(tableName: currentTableName, description: description, domainName: finalTableNames.get(tableName))
                tablesMap.put(currentTableName, tableMeta)
            } else if (currentTableName != null && columnName != null) {
                String enumTypeName = extractEnumTypeName(dataType)
                SensitiveType sensType = SensitiveType.getFrom(sensitiveType) ?: SensitiveType.NOT_APPLICABLE

                ColumnMetadata colMeta = new ColumnMetadata(
                        tableName: currentTableName,
                        columnName: columnName,
                        dataType: dataType,
                        sensitiveType: sensType,
                        description: description,
                        label: label ?: columnName,
                        enumTypeName: enumTypeName
                )
                tablesMap.get(currentTableName)?.addColumn(colMeta)
            }
        }
    }

    private void collectColumnsFromGroup(ColumnGroup group, List<Column> acc) {
        if (group == null) return
        if (group instanceof ColumnRepeatGroup) {
            def rgroup = group as ColumnRepeatGroup
            for (def childGroup : rgroup.getColumnsGroups()) {
                collectColumnsFromGroup(childGroup, acc)
            }
        } else {
            for (def col : group.getColumns()) {
                acc.add(col)
            }
        }
    }

    /**
     * Parses custom dynamic extension forms (XLS HForms) using hds-forms-model-lib ExcelFormParser
     */
    TableMetadata loadExtensionFormMetadata(CoreFormExtension coreFormExt) {
        if (!coreFormExt || !coreFormExt.extFormPath) return null

        File hformFile = coreFormExt.getExtFile()
        if (!hformFile.exists()) return null

        try {
            ExcelFormParser parser = new ExcelFormParser(hformFile)
            HForm hform = parser.getForm()
            if (!hform) return null

            List<CoreFormExtensionModel> mappings = CoreFormExtensionModel.findAllByCoreForm(coreFormExt, [sort: "dbColumnIndex", order: "asc"])
            if (!mappings || mappings.isEmpty()) return null

            String extTableName = coreFormExt.extFormId ?: hform.getFormId()

            // 1. Collect all columns recursively from HForm
            List<Column> allFormColumns = []
            if (hform.getColumns()) {
                for (def colGroup : hform.getColumns()) {
                    collectColumnsFromGroup(colGroup, allFormColumns)
                }
            }

            Map<String, TableMetadata> formTablesMap = [:]

            // 2. Build Main Table Metadata
            TableMetadata extTableMeta = new TableMetadata(
                    tableName: extTableName,
                    description: hform.getFormName() ?: "Custom Extension Form ${extTableName}"
            )
            // Manually add implicit system columns for main table
            extTableMeta.addColumn(new ColumnMetadata(tableName: extTableName, columnName: "id", dataType: "BIGINT", sensitiveType: SensitiveType.NOT_APPLICABLE, description: "Primary Key", label: "ID"))
            extTableMeta.addColumn(new ColumnMetadata(tableName: extTableName, columnName: "collected_id", dataType: "VARCHAR", sensitiveType: SensitiveType.NOT_APPLICABLE, description: "Collected ID", label: "Collected ID"))
            formTablesMap.put(extTableName, extTableMeta)

            // 3. Process main table mapped columns
            List<CoreFormExtensionModel> mainTableCols = mappings.findAll {
                it.dbColumnTable == extTableName && it.formColumnType != FormColumnType.REPEAT_GROUP
            }.sort { it.dbColumnIndex }
            for (CoreFormExtensionModel modelMap : mainTableCols) {
                String dbColName = modelMap.dbColumnName
                String formColName = modelMap.formColumnName
                String labelSuffix = ""

                Column col = allFormColumns.find { it.getName() == formColName }
                if (col == null) {
                    if (modelMap.formColumnType == FormColumnType.GEOPOINT) {
                        def lastIndexOfSuffix = formColName.lastIndexOf(modelMap.formChoiceValue)
                        def realFormColName = formColName.substring(0, lastIndexOfSuffix)
                        col = allFormColumns.find { it.getName() == realFormColName }
                        labelSuffix = modelMap.formChoiceValue
                    } else if (modelMap.formColumnType == FormColumnType.MULTIPLE_ITEMS) {
                        def lastIndexOfSuffix = formColName.lastIndexOf("_")
                        def realFormColName = formColName.substring(0, lastIndexOfSuffix)
                        col = allFormColumns.find { it.getName() == realFormColName }
                        labelSuffix = modelMap.formChoiceValue
                    }
                }

                ColumnMetadata colMeta = new ColumnMetadata(
                        tableName: extTableName,
                        columnName: dbColName,
                        formColumnName: formColName,
                        dataType: modelMap.dbColumnType?.name() ?: (col?.getType()?.toString() ?: "STRING"),
                        sensitiveType: col?.getSensitiveType() ?: SensitiveType.NOT_APPLICABLE,
                        isMediaColumn: col?.mediaColumn,
                        description: col?.getLabel() + (labelSuffix ? " (${labelSuffix})" : "") ?: formColName,
                        label: col?.getLabel() + (labelSuffix ? " (${labelSuffix})" : "") ?: formColName
                )

                if (col != null && col.getTypeOptions() != null && !col.getTypeOptions().isEmpty()) {
                    def validOptionsMap = col.getTypeOptions().entrySet().findAll { entry ->
                        entry.key != null && !entry.key.toString().trim().isEmpty()
                    }
                    if (!validOptionsMap.isEmpty()) {
                        String enumTypeName = "EXT_${extTableName}_${dbColName}"
                        EnumTypeMetadata enumType = new EnumTypeMetadata(
                                enumTypeName: enumTypeName,
                                description: "Choices for ${formColName}"
                        )
                        for (def toption : validOptionsMap) {
                            String code = toption.key.toString().trim()
                            String label = toption.value?.label ? toption.value.label.trim() : code
                            enumType.addOption(new EnumOptionMetadata(
                                    enumTypeName: enumTypeName,
                                    code: code,
                                    label: label,
                                    description: label
                            ))
                        }
                        enumsMap.put(enumTypeName, enumType)
                        colMeta.enumTypeName = enumTypeName
                    }
                }

                extTableMeta.addColumn(colMeta)
            }

            // 4. Build Child Repeat Tables Metadata
            List<CoreFormExtensionModel> repeatGroupMappings = mappings.findAll {
                it.formColumnType == FormColumnType.REPEAT_GROUP
            }.sort { it.dbColumnIndex }
            for (CoreFormExtensionModel repeatMap : repeatGroupMappings) {
                String childTableName = repeatMap.dbColumnTable
                if (!childTableName || formTablesMap.containsKey(childTableName)) continue

                String immediateParent = repeatMap.parentGroup?.dbColumnTable ?: extTableName

                TableMetadata childMeta = new TableMetadata(
                        tableName: childTableName,
                        description: "Repeat Group Table for ${extTableName} (${childTableName})",
                        isRepeatTable: true,
                        parentTableName: immediateParent
                )

                List<CoreFormExtensionModel> childCols = mappings.findAll {
                    it.dbColumnTable == childTableName && it.formColumnType != FormColumnType.REPEAT_GROUP
                }.sort { it.dbColumnIndex }
                for (CoreFormExtensionModel childModelMap : childCols) {
                    String cDbColName = childModelMap.dbColumnName
                    String cFormColName = childModelMap.formColumnName
                    String labelSuffix = ""

                    Column col = allFormColumns.find { it.getName() == cFormColName }
                    if (col == null) {
                        if (childModelMap.formColumnType == FormColumnType.GEOPOINT) {
                            def lastIndexOfSuffix = cFormColName.lastIndexOf(childModelMap.formChoiceValue)
                            def realFormColName = cFormColName.substring(0, lastIndexOfSuffix)
                            col = allFormColumns.find { it.getName() == realFormColName }
                            labelSuffix = childModelMap.formChoiceValue
                        } else if (childModelMap.formColumnType == FormColumnType.MULTIPLE_ITEMS) {
                            def lastIndexOfSuffix = cFormColName.lastIndexOf("_")
                            def realFormColName = cFormColName.substring(0, lastIndexOfSuffix)
                            col = allFormColumns.find { it.getName() == realFormColName }
                            labelSuffix = childModelMap.formChoiceValue
                        }
                    }

                    ColumnMetadata colMeta = new ColumnMetadata(
                            tableName: childTableName,
                            columnName: cDbColName,
                            formColumnName: cFormColName,
                            dataType: childModelMap.dbColumnType?.name() ?: (col?.getType()?.toString() ?: "STRING"),
                            sensitiveType: col?.getSensitiveType() ?: SensitiveType.NOT_APPLICABLE,
                            isMediaColumn: col?.mediaColumn,
                            description: col?.getLabel() + (labelSuffix ? " (${labelSuffix})" : "")  ?: cFormColName,
                            label: col?.getLabel() + (labelSuffix ? " (${labelSuffix})" : "")  ?: cFormColName
                    )

                    //handle special columns labels
                    if (childModelMap.formColumnName == ExtensionDatabaseColumns.ID) {
                        colMeta.label = "Child Table ID"
                        colMeta.description = colMeta.label
                    } else if (childModelMap.formColumnName == ExtensionDatabaseColumns.COLLECTED_ID) {
                        colMeta.label = "Child Table Parent - Collected ID"
                        colMeta.description = colMeta.label
                    } else if (childModelMap.formColumnName == ExtensionDatabaseColumns.FORM_PARENT_ID) {
                        colMeta.label = "Child Table Parent - ID"
                        colMeta.description = colMeta.label
                    } else if (childModelMap.formColumnName == ExtensionDatabaseColumns.ORDINAL_NUMBER) {
                        colMeta.label = "Child Table Ordinal Index"
                        colMeta.description = colMeta.label
                    }

                    if (col != null && col.getTypeOptions() != null && !col.getTypeOptions().isEmpty()) {
                        def validOptionsMap = col.getTypeOptions().entrySet().findAll { entry ->
                            entry.key != null && !entry.key.toString().trim().isEmpty()
                        }
                        if (!validOptionsMap.isEmpty()) {
                            String enumTypeName = "EXT_${childTableName}_${cDbColName}"
                            EnumTypeMetadata enumType = new EnumTypeMetadata(
                                    enumTypeName: enumTypeName,
                                    description: "Choices for ${cFormColName}"
                            )
                            for (def toption : validOptionsMap) {
                                String code = toption.key.toString().trim()
                                String label = toption.value?.label ? toption.value.label.trim() : code
                                enumType.addOption(new EnumOptionMetadata(
                                        enumTypeName: enumTypeName,
                                        code: code,
                                        label: label,
                                        description: label
                                ))
                            }
                            enumsMap.put(enumTypeName, enumType)
                            colMeta.enumTypeName = enumTypeName
                        }
                    }

                    childMeta.addColumn(colMeta)
                }

                formTablesMap.put(childTableName, childMeta)
            }

            // 5. Link child repeat tables to immediate parent TableMetadata
            formTablesMap.each { tName, tMeta ->
                if (tMeta.parentTableName != null) {
                    TableMetadata parentMeta = formTablesMap.get(tMeta.parentTableName)
                    if (parentMeta && !parentMeta.childRepeatTables.contains(tName)) {
                        parentMeta.childRepeatTables.add(tName)
                        parentMeta.hasRepeatTables = true
                    }
                }
            }

            // 6. Propagate hasMediaColumns bottom-up
            formTablesMap.each { tName, tMeta ->
                if (tMeta.hasMediaColumns) {
                    extTableMeta.hasMediaColumns = true
                    String pName = tMeta.parentTableName
                    while (pName != null) {
                        TableMetadata pMeta = formTablesMap.get(pName)
                        if (pMeta) {
                            pMeta.hasMediaColumns = true
                            pName = pMeta.parentTableName
                        } else {
                            break
                        }
                    }
                }
            }

            // Register all tables in master tablesMap
            formTablesMap.each { tName, tMeta ->
                tablesMap.put(tName, tMeta)
            }

            return extTableMeta
        } catch (Exception e) {
            log.error("Failed to parse XLS HForm extension for ${coreFormExt.extFormId}: ${e.message}", e)
            return null
        }
    }

    String extractEnumTypeName(String dataType) {
        if (!dataType) return null
        Matcher matcher = ENUM_PATTERN.matcher(dataType.trim())
        if (matcher.matches()) {
            return matcher.group(1).trim()
        }
        return null
    }

    private boolean isSolidFillCell(Cell cell) {
        if (cell == null) return false
        CellStyle style = cell.getCellStyle()
        return style != null && style.getFillPattern() == FillPatternType.SOLID_FOREGROUND
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null
        switch (cell.getCellType()) {
            case CellType.STRING:
                return cell.getStringCellValue()?.trim()
            case CellType.NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue()?.toString()
                }
                double num = cell.getNumericCellValue()
                return (num == (long) num) ? String.valueOf((long) num) : String.valueOf(num)
            case CellType.BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue())
            case CellType.FORMULA:
                try {
                    return cell.getStringCellValue()?.trim()
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue())
                }
            default:
                return null
        }
    }

    // --- Query Methods ---

    TableMetadata getTableMetadata(String tableName) {
        TableMetadata meta = tablesMap.get(tableName)
        if (meta == null && tableName != null) {
            CoreFormExtension ext = CoreFormExtension.findByExtFormId(tableName)
            if (ext) {
                meta = loadExtensionFormMetadata(ext)
            }
        }
        return meta
    }

    ColumnMetadata getColumnMetadata(String tableName, String columnName) {
        return getTableMetadata(tableName)?.getColumn(columnName)
    }

    /**
     * Reloads or updates a single CoreFormExtension metadata entry in memory at runtime
     */
    synchronized TableMetadata reloadExtensionForm(CoreFormExtension coreFormExt) {
        if (!coreFormExt || !coreFormExt.extFormId) return null
        removeExtensionForm(coreFormExt.extFormId)
        if (!coreFormExt.enabled) {
            return null
        }
        return loadExtensionFormMetadata(coreFormExt)
    }

    /**
     * Performs breadth-first traversal returning all descendant repeat tables in top-down execution order
     */
    List<TableMetadata> getAllDescendantRepeatTables(String mainTableName) {
        List<TableMetadata> descendants = []
        TableMetadata mainMeta = getTableMetadata(mainTableName)
        if (!mainMeta || !mainMeta.childRepeatTables) return descendants

        Queue<String> queue = new LinkedList<>()
        queue.addAll(mainMeta.childRepeatTables)

        while (!queue.isEmpty()) {
            String currentName = queue.poll()
            TableMetadata currentMeta = getTableMetadata(currentName)
            if (currentMeta) {
                if (!descendants.any { it.tableName == currentName }) {
                    descendants.add(currentMeta)
                }
                if (currentMeta.childRepeatTables) {
                    queue.addAll(currentMeta.childRepeatTables)
                }
            }
        }

        return descendants
    }

    /**
     * Removes an Extension Form and all its nested descendant repeat tables from the in-memory Data Dictionary
     */
    synchronized void removeExtensionForm(String extFormId) {
        if (extFormId) {
            List<TableMetadata> descendants = getAllDescendantRepeatTables(extFormId)
            descendants.each { dMeta ->
                tablesMap.remove(dMeta.tableName)
            }
            tablesMap.remove(extFormId)
        }
    }

    EnumTypeMetadata getEnumTypeMetadata(String enumTypeName) {
        return enumsMap.get(enumTypeName)
    }

    List<TableMetadata> getAllTables() {
        return new ArrayList<>(tablesMap.values())
    }

    List<TableMetadata> getFinalTables() {
        return allTables.findAll { finalTableNames.containsKey(it.tableName) }
    }

    Map<String, TableMetadata> getTablesMapReadOnly() {
        return Collections.unmodifiableMap(tablesMap)
    }

    /**
     * Resolves column metadata across all loaded tables in tablesMap without heuristic guessing.
     */
    ColumnMetadata findColumnMetadata(String columnName, String preferredTableName = null) {
        if (!columnName) return null

        if (preferredTableName) {
            TableMetadata prefTable = getTableMetadata(preferredTableName)
            if (prefTable && prefTable.getColumn(columnName)) {
                return prefTable.getColumn(columnName)
            }
        }

        // Search across all tables in tablesMap
        for (TableMetadata table : tablesMap.values()) {
            ColumnMetadata col = table.getColumn(columnName)
            if (col != null) {
                return col
            }
        }

        return null
    }

    /**
     * Constructs a synthetic TableMetadata instance for AI-generated export datasets.
     */
    TableMetadata buildAiExportTableMetadata(List<Map<String, Object>> extractedCols) {
        TableMetadata meta = new TableMetadata(
                tableName: "AI_EXPORT",
                description: "AI Natural Language Export Dataset"
        )

        extractedCols?.each { raw ->
            String cName = raw.columnName ? raw.columnName.toString() : ""
            if (!cName) return

            ColumnMetadata matched = findColumnMetadata(cName)

            ColumnMetadata col = new ColumnMetadata(
                    tableName: "AI_EXPORT",
                    columnName: cName,
                    dataType: raw.dataType ? raw.dataType.toString() : "VARCHAR",
                    sensitiveType: matched?.sensitiveType ?: SensitiveType.NOT_APPLICABLE,
                    label: matched?.label ?: cName,
                    description: matched?.description ?: matched?.label ?: cName,
                    enumTypeName: matched?.enumTypeName
            )
            meta.addColumn(col)
        }

        return meta
    }

    // --- Inner Metadata Classes ---

    static class TableMetadata {
        String tableName
        String domainName
        String description
        boolean isRepeatTable = false
        String parentTableName
        List<String> childRepeatTables = new ArrayList<>()
        boolean hasMediaColumns = false
        boolean hasRepeatTables = false
        Map<String, ColumnMetadata> columns = new LinkedHashMap<>()
        List<String> columnNames = []

        void addColumn(ColumnMetadata col) {
            columns.put(col.columnName, col)
            columnNames.add(col.columnName)
            if (col.isMediaColumn) {
                hasMediaColumns = true
            }
        }

        ColumnMetadata getColumn(String columnName) {
            return columns.get(columnName)
        }

        List<String> getColumnNamesList() {
            return columnNames
        }
    }

    static class ColumnMetadata {
        String tableName
        String columnName
        String formColumnName
        String dataType
        SensitiveType sensitiveType = SensitiveType.NOT_APPLICABLE
        Boolean isMediaColumn = false
        String description
        String label
        String enumTypeName
    }

    static class EnumTypeMetadata {
        String enumTypeName
        String description
        List<EnumOptionMetadata> options = new ArrayList<>()

        void addOption(EnumOptionMetadata option) {
            options.add(option)
        }
    }

    static class EnumOptionMetadata {
        String enumTypeName
        String code
        String label
        String description
    }
}
