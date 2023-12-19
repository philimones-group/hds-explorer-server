package org.philimone.hds.explorer.server.model.main.extension

class VisitExt {

    String collected_id
    String visit_code
    String household_code
    Integer round_number

    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        household_code nullable: false
        round_number nullable: false
    }

    static mapping = {
        table 'visit_ext'

        version false

        collected_id column: 'collected_id'
        visit_code column: 'visit_code', index: 'idx_visit_code'
        household_code column: 'household_code', index: 'idx_household_code'
        round_number column: 'round_number'
    }

}
