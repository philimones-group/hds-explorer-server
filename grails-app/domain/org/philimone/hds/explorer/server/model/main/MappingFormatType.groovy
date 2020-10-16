package org.philimone.hds.explorer.server.model.main

/**
 * Represents a variable value format, to give the ability to format an specific value that is being mapped to be visualized on Form in the Mobile Application
 */
class MappingFormatType {

    String id
    String description
    String type
    String format

    String toString(){
        "${description}"
    }

    String getValue(){
        "${type}[${format}]"
    }

    String getValue(String customFormat){
        "${type}[${customFormat==null ? format : customFormat}]"
    }

    static constraints = {
        id maxSize: 32
        description nullable: false
        type nullable: false
        format nullable: false, unique: true
    }

    static mapping = {
        table 'form_mapping_format'

        id column: "id", generator: 'uuid'

        description column: 'description'
        type column: 'type'
        format column: 'format'
    }

}
