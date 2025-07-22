package org.philimone.hds.explorer.server.model.main.extension

class HouseholdRelocationExt {
    
    String collected_id
    String visit_code
    String origin_code
    String destination_code
    String head_code
    String head_name

    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        origin_code nullable: false
        destination_code nullable: false
        head_code nullable: false
        head_name nullable: false
    }
    
    static mapping = {
        table 'household_relocation_ext'

        version false

        collected_id column: 'collected_id'

        visit_code column: 'visit_code', index: 'idx_visit_code'
        origin_code column: 'origin_code', index: 'idx_origin_code'
        destination_code column: 'destination_code', index: 'idx_destination_code'
        head_code column: 'head_code', index: 'idx_head_code'
        head_name column: 'head_name'
    }

}
