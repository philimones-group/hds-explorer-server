package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity

/**
 * Represents a Group of Forms, can be used to specify different forms can will be collected for an specific situation
 */
class FormGroup extends AuditableEntity {

    String id
    String code
    String name
    String description

    static constraints = {
        id maxSize: 32
        code blank: true, unique: true
        name blank: false
        description nullable: true, blank: true
    }

    static mapping = {
        table 'form_group'

        id column: "uuid", generator: 'uuid'

        code column: 'code'
        name column: 'name'
        description column: 'description'
    }

}
