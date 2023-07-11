package org.philimone.hds.explorer.server.model.main

class CoreFormColumnOptions {

    String id
    String columnName
    int ordinal
    String optionValue
    String optionLabel
    String optionLabelCode

    static constraints = {
        id maxSize: 32
        columnName nullable: false, unique: "optionValue"
        ordinal min: 1, unique: "columnName"
        optionValue nullable: false
        optionLabel nullable: true
        optionLabelCode nullable: true
    }

    static mapping = {
        table "core_form_column_options"

        id column: "id", generator: 'uuid'

        columnName column: 'column_name'
        ordinal column: "ordinal"
        optionValue column: 'option_value'
        optionLabel column: 'option_label'
        optionLabelCode column: 'option_label_code'
    }
}
