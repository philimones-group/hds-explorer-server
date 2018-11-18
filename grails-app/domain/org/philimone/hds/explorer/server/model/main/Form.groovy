package org.philimone.hds.explorer.server.model.main

/**
 * Represents a Form that will be opened for Data Collection, this table store the details of the Form and how it should be used and who will use it
 * Supported apps for now: ODK
 */
class Form {

    FormGroup group
    RedcapApi redcapApi
    String formId
    String formName
    String formDescription
    String gender           /*  M, F, ALL, if HouseholdForm is true, gender should be ALL */
    int minAge              //0
    int maxAge              //Default - 120
    boolean isHouseholdForm
    boolean isMemberForm
    boolean isHouseholdHeadForm
    boolean isFollowUpForm

    boolean enabled

    static hasMany = [
            modules:StudyModule, /* Modules that have access to the Form - this data will be converted to comma separated names */
            dependencies:Form,   /* The list of forms that must be collected first before this Form */
            mappings:FormMapping,  /* The list of all variables that will be filled automatically on a Form in Mobile App */
            redcapMappings:RedcapMapping  /* The list of all variables that will be uploaded to a REDCap Form */
    ]

    static constraints = {
        group nullable: true
        redcapApi nullable: true
        formId blank: false, unique: true
        formName blank: false
        formDescription blank: true
        gender blank: false
        minAge min: 0, max: 150
        maxAge min: 0, max: 150

        isHouseholdForm()
        isHouseholdHeadForm()
        isMemberForm()
        isFollowUpForm()

        enabled nullable:false
    }

    static mapping = {
        table 'form'

        group column: 'form_group_id'
        redcapApi column: 'redcap_api'
        formId column: 'form_id'
        formName column: 'form_name'
        formDescription column: 'form_description'
        gender column: 'gender'
        minAge column: 'min_age'
        maxAge column: 'max_age'

        isHouseholdForm column: 'is_household'
        isMemberForm column: 'is_member'
        isHouseholdHeadForm column: 'is_household_head'
        isFollowUpForm column: 'is_followup_only'

        enabled column: 'enabled'

    }
}
