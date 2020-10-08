package org.philimone.hds.explorer.server.model.logs

/**
 * Log Group represents a collection of Reports that belongs to a specific procedure or category of functions
 */
class LogGroup {

    String id
    int groupId
    String name
    String description

    String toString(){
        name
    }

    static constraints = {
        id maxSize: 32
        groupId min: 1, max: 5000, unique:true
        name blank: false, unique: true
        description nullable: true, blank: true
    }

    static mapping = {
        table '_log_group'

        id column: "uuid", generator: 'uuid'
    }

}
