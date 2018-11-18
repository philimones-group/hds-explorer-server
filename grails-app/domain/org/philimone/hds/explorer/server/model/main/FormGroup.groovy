package org.philimone.hds.explorer.server.model.main

/**
 * Represents a Group of Forms, can be used to specify different forms can will be collected for an specific situation
 */
class FormGroup {

    String code
    String name
    String description

    static constraints = {
        code blank: true, unique: true
        name blank: false
        description nullable: true, blank: true
    }

    static mapping = {
        table 'form_group'

        code column: 'code'
        name column: 'name'
        description column: 'description'
    }

}
