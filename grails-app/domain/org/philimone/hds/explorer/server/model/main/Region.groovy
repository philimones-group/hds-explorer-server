package org.philimone.hds.explorer.server.model.main

/**
 * Region represents an Administrative division in a country or study area, the Administrative Division or Location Hieararchy it stored here
 */
class Region {

    String code
    String name
    String hierarchyLevel
    Region parent

    String getParentCode(){
        parent==null ? "" : parent.code
    }

    static constraints = {
        code(unique: true, maxSize: 32)
        name blank: false
        hierarchyLevel blank: false
        parent nullable: true
    }

    static mapping = {
        table 'region'

        id name: 'code', column: 'code', generator: 'assigned'
        version false
        name column: 'name'
        hierarchyLevel column: 'level'
        parent column: 'parent_region_code'

    }

    def static ALL_COLUMNS = ['code', 'name', 'hierarchyLevel']
}
