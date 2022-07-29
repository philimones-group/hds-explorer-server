package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.enums.FormSubjectType
import org.philimone.hds.explorer.server.model.enums.FormType
import org.philimone.hds.explorer.server.model.types.StringCollectionType

/**
 * Represents a Form that will be opened for Data Collection, this table store the details of the Form and how it should be used and who will use it
 * Supported apps for now: ODK
 */
class Form extends AuditableEntity {

    String id

    FormType formType = FormType.REGULAR

    String formId
    String formName
    String formDescription

    FormSubjectType formSubjectType

    String regionLevel      /* Should be hierahcyLevel */
    String gender           /*  M, F, ALL, if HouseholdForm is true, gender should be ALL */
    int minAge              //0
    int maxAge              //Default - 120


    boolean isRegionForm    //Form for regions / location hierarchies (with this true - the variables gender,minAge,maxAge dont apply anymore)
    boolean isHouseholdForm
    boolean isMemberForm
    boolean isHouseholdHeadForm

    boolean isFollowUpExclusive
    boolean isFormGroupExclusive
    boolean multiCollPerSession

    RedcapApi redcapApi

    boolean enabled

    String toString(){
        formName
    }

    static hasMany = [
            modules:String, /* Modules that have access to the Form - this data will be converted to comma separated names */
            mappings:FormMapping,  /* The list of all variables that will be filled automatically on a Form in Mobile App */
            redcapMappings:RedcapMapping,  /* The list of all variables that will be uploaded to a REDCap Form */
            groupMappings:FormGroupMapping
    ]

    static mappedBy = [groupMappings: "groupForm"]

    static constraints = {
        //group nullable: true
        id maxSize: 32

        formType nullable:false

        formId blank: false, unique: true
        formName blank: false
        formDescription blank: true

        gender blank: false
        minAge min: 0, max: 150
        maxAge min: 0, max: 150

        formSubjectType nullable: false

        isRegionForm()
        regionLevel nullable: true
        isHouseholdForm()
        isHouseholdHeadForm()
        isMemberForm()

        isFollowUpExclusive()
        isFormGroupExclusive()
        multiCollPerSession()

        redcapApi nullable: true

        enabled nullable:false
    }

    static mapping = {
        table 'form'

        id column: "id", generator: 'uuid'

        formType column: "form_type", enumType: "identity"

        formId column: 'form_id', index: "idx_form_id"
        formName column: 'form_name'
        formDescription column: 'form_description'

        formSubjectType column: "form_subject_type", enumType: "identity"

        regionLevel column: "region_level"
        gender column: 'gender'
        minAge column: 'min_age'
        maxAge column: 'max_age'

        isRegionForm column: "is_region"
        isHouseholdForm column: 'is_household'
        isMemberForm column: 'is_member'
        isHouseholdHeadForm column: 'is_household_head'
        isFollowUpExclusive column: 'is_followup_only'
        isFormGroupExclusive column: 'is_form_group_exclusive'
        multiCollPerSession column: "multi_coll_per_session"

        redcapApi column: 'redcap_api'

        enabled column: 'enabled'

        modules column: "modules", type: StringCollectionType, index: "idx_modules"
    }
}
