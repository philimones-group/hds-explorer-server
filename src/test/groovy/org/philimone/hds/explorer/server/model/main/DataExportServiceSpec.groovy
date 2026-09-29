package org.philimone.hds.explorer.server.model.main

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import org.philimone.hds.explorer.server.model.settings.DataExportReport
import org.philimone.hds.explorer.services.DataDictionaryService
import org.philimone.hds.explorer.services.DataExportService
import spock.lang.Specification

class DataExportServiceSpec extends Specification implements ServiceUnitTest<DataExportService>, DataTest {

    def setup() {
        mockDomains(Region, CoreFormExtension, CoreFormExtensionModel, DataExportReport)

        //./gradlew test --tests "*DataExportServiceSpec" --tests "*DataDictionaryServiceSpec"
    }

    void "test PII name masking"() {
        expect:
        service.maskName("Paulo Filimone") == "P*** F***"
        service.maskName("John") == "J***"
        service.maskName("") == ""
        service.maskName(null) == ""
    }

    void "test CSV escaping"() {
        expect:
        service.escapeCsv("Simple Text") == "Simple Text"
        service.escapeCsv("Text, with comma") == "\"Text, with comma\""
        service.escapeCsv("Text with \"quotes\"") == "\"Text with \"\"quotes\"\"\""
        service.escapeCsv(null) == ""
    }

    void "test column name sanitization for statistical packages"() {
        expect:
        service.sanitizeColumnName("Household Member Primary Phone Number") == "household_member_primary_phone_n" // max 32 chars
        service.sanitizeColumnName("gender") == "gender"
        service.sanitizeColumnName("code-123!") == "code_123_"
        service.sanitizeColumnName(null) == "col"
    }

    // --- PRE-JOINED VIEWS TESTS (ALL 6 VIEWS) ---

    void "test INDEPTH_CORE_RESIDENCY pre-joined view SQL construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "INDEPTH_CORE_RESIDENCY",
                gender: "MALE",
                ageMin: 18,
                ageMax: 65
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("IndividualId")
        sql.contains("LocationId")
        sql.contains("EventCode")
        sql.contains("EventDate")
        sql.contains("residence")
        sql.contains("UNION ALL")
        sql.contains("ORDER BY eha.IndividualId, eha.EventDate ASC")
    }

    void "test RESIDENCY_PERSON_TIME pre-joined view SQL construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "RESIDENCY_PERSON_TIME",
                gender: "FEMALE"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("member_code")
        sql.contains("exposure_days")
        sql.contains("person_years")
        sql.contains("m.gender = 'FEMALE'")
    }

    void "test INDEPTH_MATERNAL_PREGNANCY pre-joined view SQL construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "INDEPTH_MATERNAL_PREGNANCY",
                ageMin: 15,
                ageMax: 49
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("IndividualId")
        sql.contains("FatherId")
        sql.contains("ChildId")
        sql.contains("'DLV' AS EventCode")
        sql.contains("MultiBirth")
        sql.contains("LiveBirths")
        sql.contains("StillBirths")
        sql.contains("LocationId")
    }

    void "test MATERNAL_PREGNANCY pre-joined view SQL construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "MATERNAL_PREGNANCY",
                ageMin: 15,
                ageMax: 49
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("pregnancy_code")
        sql.contains("mother_code")
        sql.contains("mother_name")
        sql.contains("father_code")
        sql.contains("child_code")
    }

    void "test INDEPTH_HOUSEHOLD_COMPOSITION pre-joined view SQL construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "INDEPTH_HOUSEHOLD_COMPOSITION"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("LocationId")
        sql.contains("IndividualId")
        sql.contains("HeadId")
        sql.contains("RelationshipToHead")
        sql.contains("ORDER BY h.code, m.code")
    }

    void "test HOUSEHOLD_COMPOSITION pre-joined view SQL construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "HOUSEHOLD_COMPOSITION"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("household_code")
        sql.contains("member_code")
        sql.contains("head_code")
        sql.contains("relationship_type")
        sql.contains("ORDER BY h.code, m.code")
    }

    // --- CORE REGULAR TABLES TESTS ---

    void "test member table SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "member",
                gender: "FEMALE",
                ageMin: 15,
                ageMax: 49,
                activeResidentsOnly: true
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM member m")
        sql.contains("LEFT JOIN residency r")
        sql.contains("LEFT JOIN head_relationship hr")
        sql.contains("r.end_type = 'NA' OR r.end_date IS NULL")
        sql.contains("m.gender = 'FEMALE'")
        sql.contains(">= 15")
        sql.contains("<= 49")
    }

    void "test household table SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "household"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM household h")
    }

    void "test visit table SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "visit"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM visit v")
        sql.contains("LEFT JOIN household h")
    }

    void "test death table SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "death",
                gender: "MALE"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM `death` t")
        sql.contains("JOIN member m ON m.id = t.member_id")
        sql.contains("m.gender = 'MALE'")
    }

    void "test in_migration table SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "in_migration"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM `in_migration` t")
        sql.contains("JOIN member m ON m.id = t.member_id")
    }

    // --- DYNAMIC EXTENSION FORMS TESTS (XLS HFORMS) ---

    void "test member_ext extension form SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "member_ext",
                gender: "FEMALE"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM `member_ext` t JOIN member m ON m.code = t.member_code")
        sql.contains("m.gender = 'FEMALE'")
    }

    void "test household_ext extension form SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "household_ext"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM `household_ext` t JOIN household h ON h.code = t.household_code")
    }

    void "test visit_ext extension form SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "visit_ext"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM `visit_ext` t JOIN visit v ON v.code = t.visit_code")
    }

    void "test death_ext extension form SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "death_ext"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM `death_ext` t")
        sql.contains("JOIN member m ON m.code = t.member_code")
        sql.contains("LEFT JOIN visit v ON v.code = t.visit_code")
    }

    void "test pregnancy_visit_ext extension form SQL query construction"() {
        setup:
        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "pregnancy_visit_ext"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("FROM `pregnancy_visit_ext` t")
        sql.contains("JOIN member mother ON mother.code = t.mother_code")
        sql.contains("LEFT JOIN visit v ON v.code = t.visit_code")
    }

    // --- LEVEL-AWARE REGIONAL FILTERING TEST ---

    void "test level-aware region filtering SQL construction"() {
        setup:
        Region region = new Region(code: "PEMBA", name: "Pemba District", hierarchyLevel: org.philimone.hds.explorer.server.model.enums.RegionLevel.HIERARCHY_2)
        region.save(flush: true)

        DataExportService.ExportRequest request = new DataExportService.ExportRequest(
                datasetName: "household",
                regionCode: "PEMBA"
        )

        when:
        String sql = service.buildExportSql(request)

        then:
        sql.contains("h.hierarchy2 = 'PEMBA'")
    }

    void "test Stata do-script syntax generation"() {
        setup:
        DataDictionaryService mockDictService = Mock(DataDictionaryService)
        service.dataDictionaryService = mockDictService

        DataDictionaryService.TableMetadata meta = new DataDictionaryService.TableMetadata(tableName: "member")
        meta.addColumn(new DataDictionaryService.ColumnMetadata(
                tableName: "member",
                columnName: "gender",
                label: "Gender of Individual",
                enumTypeName: "Gender"
        ))

        DataDictionaryService.EnumTypeMetadata enumType = new DataDictionaryService.EnumTypeMetadata(enumTypeName: "Gender")
        enumType.addOption(new DataDictionaryService.EnumOptionMetadata(code: "MALE", label: "Male"))
        enumType.addOption(new DataDictionaryService.EnumOptionMetadata(code: "FEMALE", label: "Female"))

        mockDictService.getTableMetadata("member") >> meta
        mockDictService.getEnumTypeMetadata("Gender") >> enumType

        DataExportService.ExportRequest req = new DataExportService.ExportRequest(datasetName: "member", format: "STATA")

        when:
        String script = service.generateSyntaxScript(req)

        then:
        script.contains("import delimited using \"member_data.csv\"")
        script.contains("label variable gender \"Gender of Individual\"")
        script.contains("label define gender_lbl MALE \"Male\" FEMALE \"Female\"")
        script.contains("label values gender gender_lbl")
    }
}
