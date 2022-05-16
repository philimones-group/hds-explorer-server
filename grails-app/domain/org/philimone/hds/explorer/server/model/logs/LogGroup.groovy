package org.philimone.hds.explorer.server.model.logs

import org.philimone.hds.explorer.server.model.enums.settings.LogGroupCode

/**
 * Log Group represents a collection of Reports that belongs to a specific procedure or category of functions
 */
class LogGroup {

    String id
    LogGroupCode groupId
    String name
    String description

    String toString(){
        name
    }

    static constraints = {
        id maxSize: 32
        groupId unique:true
        name blank: false, unique: true
        description nullable: true, blank: true
    }

    static mapping = {
        table '_log_group'

        id column: "id", generator: 'uuid'

        groupId column: "group_id", enumType: 'identity', index: "idx_group_id"
        name column: "name"
        description column: "description"
    }

}
