package org.philimone.hds.explorer.server.model.main

/**
 * The Form Mapping represents an association between variables of a specific Form and Variables within the system (Household, Members)
 * This mapping will be used to fill automatically questions in a Mobile Form (eg. ODK)
 */
class FormMapping {

    String id
    Form form
    String formVariableName
    String formRepeatGroup /* If has any value it belongs to an Repeat Group within ODK */
    String tableName
    String columnName
    MappingFormatType columnFormat
    String columnFormatValue

    String getRepeatGroup(){
        formRepeatGroup==null ? "" : "${formRepeatGroup}."
    }

    String getRepeatGroupAsConstant(){
        formRepeatGroup==null ? "" : "\$."
    }

    String toString(){
        "${getRepeatGroupAsConstant()}${tableName}.${columnName}:${getRepeatGroup()}${formVariableName}->${columnFormat==null ? 'None': columnFormat.getValue(columnFormatValue) }"
    }

    static constraints = {
        id maxSize: 32
        form nullable: false
        formVariableName blank: false, unique: 'form'
        formRepeatGroup nullable: true, blank: true
        tableName blank: false
        columnName blank: false
        columnFormat nullable: true
        columnFormatValue nullable: true
    }

    static mapping = {
        table 'form_mapping'

        id column: "uuid", generator: 'uuid'

        form column: "form_id"
        formVariableName column: "form_variable_name"
        formRepeatGroup column: "form_repeat_group"
        tableName column: "table_name"
        columnName column: "column_name"
        columnFormat column: "column_format_type"
        columnFormatValue column: "column_format_value"
    }

}
