package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.audit.CollectableEntity

/**
 * Region represents an Administrative division in a country or study area, the Administrative Division or Location Hieararchy it stored here
 */
class Region extends CollectableEntity {

    String id
    String code
    String name
    String hierarchyLevel
    Region parent

    String getParentCode(){
        parent==null ? "" : parent.code
    }

    static constraints = {
        id maxSize: 32
        code(unique: true, maxSize: 32)
        name blank: false
        hierarchyLevel blank: false
        parent nullable: true
    }

    static mapping = {
        table 'region'

        id column: "uuid", generator: 'uuid'
        version false

        code column: 'code'
        name column: 'name'
        hierarchyLevel column: 'level'
        parent column: 'parent_region_code'

    }

    def static ALL_COLUMNS = ['code', 'name', 'hierarchyLevel']
}
