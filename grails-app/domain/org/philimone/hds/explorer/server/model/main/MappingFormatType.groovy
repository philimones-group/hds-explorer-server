package org.philimone.hds.explorer.server.model.main

/**
 * Represents a variable value format, to give the ability to format an specific value that is being mapped to be visualized on Form in the Mobile Application
 */
class MappingFormatType {

    String description
    String type
    String format

    String toString(){
        "${description}"
    }

    String getValue(){
        "${type}[${format}]"
    }

    static constraints = {
        description nullable: false
        type nullable: false
        format nullable: false, unique: true
    }

    static mapping = {
        table 'form_mapping_format'

        description column: 'description'
        type column: 'type'
        format column: 'format'
    }

}