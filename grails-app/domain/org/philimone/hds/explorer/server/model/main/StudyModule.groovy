package org.philimone.hds.explorer.server.model.main

/**
 * A Study Module represents a specific module that can have its own users and access its own information
 */
class StudyModule {

    static String DSS_SURVEY_MODULE = "DSS-SURVEY" /* Default Module */

    String code
    String name
    String description

    String toString(){
        name
    }

    static constraints = {
        code unique: true
        name blank: false
        description blank: true
    }

    static mapping = {
        table 'study_module'

        code column: "code"
        name column: "name"
        description column: "description"
    }

}
