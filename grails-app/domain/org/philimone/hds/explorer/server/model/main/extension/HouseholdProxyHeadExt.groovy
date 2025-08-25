package org.philimone.hds.explorer.server.model.main.extension

class HouseholdProxyHeadExt {
    
    String collected_id
    String visit_code
    String household_code
    String household_name
    String proxy_head_type
    String proxy_head_code
    String proxy_head_name
    
    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        household_code nullable: false
        household_name nullable: false
        proxy_head_type nullable: false
        proxy_head_code nullable: true
        proxy_head_name nullable: false
    }
    
    static mapping = {
        table 'household_proxy_head_ext'

        version false

        collected_id column: 'collected_id'

        visit_code column: 'visit_code', index: 'idx_visit_code'
        household_code column: 'household_code', index: 'idx_household_code'
        household_name column: 'household_name'
        proxy_head_type column: 'proxy_head_type', index: 'idx_proxy_head_type'
        proxy_head_code column: 'proxy_head_code', index: 'idx_proxy_head_code'
        proxy_head_name column: 'proxy_head_name'
    }

}
