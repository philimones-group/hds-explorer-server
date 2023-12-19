package org.philimone.hds.explorer.server.model.main.extension

class HouseholdExt {

    String collected_id
    String household_code
    String household_name
    String head_code
    String head_name

    static constraints = {
        collected_id unique: true, nullable: false
        household_code nullable: false
        household_name nullable: false
        head_code nullable: true
        head_name nullable: true
    }

    static mapping = {
        table 'household_ext'

        collected_id column: 'collected_id'
        household_code column: 'household_code', index: 'idx_household_code'
        household_name column: 'household_name'
        head_code column: 'head_code', index: 'idx_head_code'
        head_name column: 'head_name'
    }

}
