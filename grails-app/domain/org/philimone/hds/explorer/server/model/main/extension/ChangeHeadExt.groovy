package org.philimone.hds.explorer.server.model.main.extension

class ChangeHeadExt {
    
    String collected_id
    String visit_code
    String household_code
    String old_head_code
    String old_head_name
    String new_head_code
    String new_head_name
    
    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        household_code nullable: false
        old_head_code nullable: false
        old_head_name nullable: false
        new_head_code nullable: false
        new_head_name nullable: false
    }
    
    static mapping = {
        table 'changehead_ext'

        version false

        collected_id column: 'collected_id'

        visit_code column: 'visit_code', index: 'idx_visit_code'
        household_code column: 'household_code', index: 'idx_household_code'
        old_head_code column: 'old_head_code', index: 'idx_old_head_code'
        old_head_name column: 'old_head_name'
        new_head_code column: 'new_head_code', index: 'idx_new_head_code'
        new_head_name column: 'new_head_name'
    }

}
