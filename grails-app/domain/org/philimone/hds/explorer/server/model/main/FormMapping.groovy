package org.philimone.hds.explorer.server.model.main

/**
 * The Form Mapping represents an association between variables of a specific Form and Variables within the system (Household, Members)
 * This mapping will be used to fill automatically questions in a Mobile Form (eg. ODK)
 */
class FormMapping {

    Form form
    String formVariableName
    String tableName
    String columnName
    MappingFormatType columnFormat
    String columnFormatValue

    String toString(){
        "${tableName}.${columnName}:${formVariableName}->${columnFormat==null ? 'None': columnFormat.getValue(columnFormatValue) }"
    }

    static constraints = {
        form nullable: false
        formVariableName blank: false, unique: 'form'
        tableName blank: false
        columnName blank: false
        columnFormat nullable: true
        columnFormatValue nullable: true
    }

    static mapping = {
        table 'form_mapping'

        form column: "form_id"
        formVariableName column: "form_variable_name"
        tableName column: "table_name"
        columnName column: "column_name"
        columnFormat column: "column_format_type"
        columnFormatValue column: "column_format_value"
    }

}
