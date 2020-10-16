package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity

/**
 * Represents a Form that will be opened for Data Collection, this table store the details of the Form and how it should be used and who will use it
 * Supported apps for now: ODK
 */
class Form extends AuditableEntity {

    String id
    String formId
    String formName
    String formDescription
    String regionLevel      /* Should be hierahcyLevel */
    String gender           /*  M, F, ALL, if HouseholdForm is true, gender should be ALL */
    int minAge              //0
    int maxAge              //Default - 120
    boolean isRegionForm    //Form for regions / location hierarchies (with this true - the variables gender,minAge,maxAge dont apply anymore)
    boolean isHouseholdForm
    boolean isMemberForm
    boolean isHouseholdHeadForm
    boolean isFollowUpForm

    boolean multiCollPerSession

    boolean enabled

    //FormGroup group
    RedcapApi redcapApi

    static hasMany = [
            modules:StudyModule, /* Modules that have access to the Form - this data will be converted to comma separated names */
            dependencies:Form,   /* The list of forms that must be collected first before this Form */
            mappings:FormMapping,  /* The list of all variables that will be filled automatically on a Form in Mobile App */
            redcapMappings:RedcapMapping  /* The list of all variables that will be uploaded to a REDCap Form */
    ]

    String toString(){
        formName
    }

    String getModulesAsText() {
        String mds = ""
        modules.each {
            mds += (mds.empty ? "":",") + it.code
        }
        return mds
    }

    String getDependenciesAsText() {
        String deps = ""
        dependencies.each {
            deps += (deps.empty ? "":",") + it.formId
        }
        return deps
    }

    static constraints = {
        //group nullable: true
        id maxSize: 32
        redcapApi nullable: true
        formId blank: false, unique: true
        formName blank: false
        formDescription blank: true
        gender blank: false
        minAge min: 0, max: 150
        maxAge min: 0, max: 150

        isRegionForm()
        regionLevel nullable: true
        isHouseholdForm()
        isHouseholdHeadForm()
        isMemberForm()
        isFollowUpForm()

        multiCollPerSession()

        enabled nullable:false
    }

    static mapping = {
        table 'form'

        id column: "id", generator: 'uuid'

        //group column: 'form_group_id'
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

        multiCollPerSession column: "multi_coll_per_session"

        enabled column: 'enabled'

    }
}
