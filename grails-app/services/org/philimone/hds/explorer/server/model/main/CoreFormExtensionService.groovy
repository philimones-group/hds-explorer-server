package org.philimone.hds.explorer.server.model.main


import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.enums.CoreForm

@Transactional
class CoreFormExtensionService {

    CoreFormExtension get(Serializable id){
        CoreFormExtension.get(id)
    }

    List<CoreFormExtension> list(Map args){
        CoreFormExtension.list(args)
    }

    Long count(){
        CoreFormExtension.count()
    }

    void delete(Serializable id){
        get(id).delete()
    }

    CoreFormExtension save(CoreFormExtension coreFormExtension){
        coreFormExtension.save(flush:true)
    }

    File getFormXLS(CoreFormExtension coreFormExtension){
        try {
            def url = getClass().classLoader.getResource("samples/extension-forms/xls/${coreFormExtension.extFormId}.xlsx")

            return new File(url.toURI())
        }catch (Exception ex){
            //ex.printStackTrace()

            return null
        }

    }

    String getColumnMapping(CoreForm form) {
        if (form == CoreForm.REGION_FORM) return getRegionMapping();
        if (form == CoreForm.HOUSEHOLD_FORM) return getHouseholdMapping();
        if (form == CoreForm.VISIT_FORM) return getVisitMapping();
        if (form == CoreForm.MEMBER_ENU_FORM) return getMemberEnuMapping();
        if (form == CoreForm.MARITAL_RELATIONSHIP_FORM) return getMaritalRelationshipMapping();
        if (form == CoreForm.INMIGRATION_FORM) return getInmigrationMapping();
        if (form == CoreForm.OUTMIGRATION_FORM) return getOutmigrationMapping();
        if (form == CoreForm.PREGNANCY_REGISTRATION_FORM) return getPregRegistrationMapping();
        if (form == CoreForm.PREGNANCY_OUTCOME_FORM) return getPregOutcomeMapping();
        if (form == CoreForm.DEATH_FORM) return getDeathMapping();
        if (form == CoreForm.CHANGE_HEAD_FORM) return getChangeHeadMapping();
        if (form == CoreForm.INCOMPLETE_VISIT_FORM) return getIncVisitMapping();

        return ""
    }

    String getRegionMapping(){
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.REGION_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("parent_code", "#parentCode")
        columnsMap.put("parent_name", "#parentName")
        columnsMap.put("region_code", "#regionCode")
        columnsMap.put("region_name", "#regionName")

        return mapToString(columnsMap)
    }

    String getHouseholdMapping(){
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.HOUSEHOLD_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("household_code", "#householdCode")
        columnsMap.put("household_name", "#householdName")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("head_code", "#headCode")
        columnsMap.put("head_name", "#headName")

        return mapToString(columnsMap)
    }

    String getVisitMapping(){
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.VISIT_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("household_code", "#householdCode")
        columnsMap.put("visit_code", "#code")
        columnsMap.put("round_number", "#roundNumber")


        return mapToString(columnsMap)
    }

    String getMemberEnuMapping(){
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.MEMBER_ENU_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("household_code", "#householdCode")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("member_code", "#code")
        columnsMap.put("member_name", "#name")


        return mapToString(columnsMap)
    }

    String getMaritalRelationshipMapping() {
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.MARITAL_RELATIONSHIP_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("member_a", "#memberA")
        columnsMap.put("member_b", "#memberB")
        columnsMap.put("member_a_name", "#memberA_name")
        columnsMap.put("member_b_name", "#memberB_name")

        return mapToString(columnsMap)
    }

    String getInmigrationMapping() {
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.INMIGRATION_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("member_code", "#memberCode")
        columnsMap.put("member_name", "#memberName")
        columnsMap.put("migration_type", "#migrationType")
        columnsMap.put("ext_migration_type", "#extMigrationType")


        return mapToString(columnsMap)
    }

    String getOutmigrationMapping() {
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.OUTMIGRATION_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("member_code", "#memberCode")
        columnsMap.put("member_name", "#memberName")
        columnsMap.put("migration_type", "#migrationType")


        return mapToString(columnsMap)
    }

    String getPregRegistrationMapping() {
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.PREGNANCY_REGISTRATION_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("mother_code", "#motherCode")
        columnsMap.put("mother_name", "#motherName")
        columnsMap.put("pregnancy_code", "#code")
        columnsMap.put("pregnancy_status", "#status")


        return mapToString(columnsMap)
    }

    String getPregOutcomeMapping() {
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.PREGNANCY_OUTCOME_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("mother_code", "#motherCode")
        columnsMap.put("mother_name", "#motherName")
        columnsMap.put("father_code", "#fatherCode")
        columnsMap.put("father_name", "#fatherName")
        columnsMap.put("pregnancy_code", "#code")
        columnsMap.put("number_of_outcomes", "#numberOfOutcomes")
        //columnsMap.put("childs", "\$load:childs")
        columnsMap.put("childs.child_outcome_type", "\$childs.outcomeType")
        columnsMap.put("childs.child_code", "\$childs.childCode")
        columnsMap.put("childs.child_name", "\$childs.childName")
        columnsMap.put("childs.child_gender", "\$childs.childGender")


        return mapToString(columnsMap)
    }

    String getDeathMapping() {
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.DEATH_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("member_code", "#memberCode")
        columnsMap.put("member_name", "#memberName")


        return mapToString(columnsMap)
    }

    String getChangeHeadMapping() {
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.CHANGE_HEAD_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("household_code", "#householdCode")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("old_head_code", "#oldHeadCode")
        columnsMap.put("old_head_name","#oldHeadName")
        columnsMap.put("new_head_code", "#newHeadCode")
        columnsMap.put("new_head_name", "#newHeadName")

        return mapToString(columnsMap)
    }

    String getIncVisitMapping() {
        def columnsMap = new LinkedHashMap()

        columnsMap.put("core_form_id", CoreForm.INCOMPLETE_VISIT_FORM.code)
        columnsMap.put("collected_id", "#id")
        columnsMap.put("household_code", "#householdCode")
        columnsMap.put("visit_code", "#visitCode")
        columnsMap.put("member_code", "#memberCode")
        columnsMap.put("member_name", "#memberName")


        return mapToString(columnsMap)
    }

    String mapToString(Map<String, String> colsMap){
        String str = ""
        colsMap.each { k, v ->
            str += (str.empty ? "":";") + "${k}<#>${v}"
        }

        return str;
    }

}