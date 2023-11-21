package org.philimone.hds.explorer.server.model.main.extension

class RegionExt {

    String collected_id
    String parent_code
    String parent_name
    String region_code
    String region_name

    static constraints = {
        collected_id unique: true, nullable: false
        parent_code nullable: false
        parent_name nullable: false
        region_code nullable: false
        region_name nullable: false
    }

    static mapping = {
        table 'region_ext'

        collected_id column: 'collected_id'
        parent_code column: 'parent_code', index: 'idx_parent_code'
        parent_name column: 'parent_name'
        region_code column: 'region_code', index: 'idx_region_code'
        region_name column: 'region_name'
    }

}
