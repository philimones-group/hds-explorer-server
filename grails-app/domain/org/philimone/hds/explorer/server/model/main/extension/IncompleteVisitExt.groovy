package org.philimone.hds.explorer.server.model.main.extension

class IncompleteVisitExt {

    String collected_id
    String visit_code
    String household_code
    String member_code
    String member_name

    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        household_code nullable: false
        member_code nullable: false
        member_name nullable: false
    }

    static mapping = {
        table 'incomplete_visit_ext'

        version false

        collected_id column: 'collected_id'
        visit_code column: 'visit_code', index: 'idx_visit_code'
        household_code column: 'household_code', index: 'idx_household_code'
        member_code column: 'member_code', index: 'idx_member_code'
        member_name column: 'member_name'
    }

}
