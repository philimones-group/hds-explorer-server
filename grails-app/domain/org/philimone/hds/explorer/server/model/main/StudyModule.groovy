package org.philimone.hds.explorer.server.model.main

import org.grails.datastore.mapping.core.connections.ConnectionSource
import org.philimone.hds.explorer.server.model.audit.AuditableEntity

/**
 * A Study Module represents a specific module that can have its own users and access its own information
 */
class StudyModule extends AuditableEntity {

    static String DSS_SURVEY_MODULE = "DSS-SURVEY" /* Default Module */

    String id
    String code
    String name
    String description

    String toString(){
        name
    }

    static constraints = {
        id maxSize: 32
        code unique: true
        name blank: false
        description blank: true
    }

    static mapping = {
        table 'study_module'

        id column: "id", generator: 'uuid'

        code column: "code"
        name column: "name"
        description column: "description"
    }

}
