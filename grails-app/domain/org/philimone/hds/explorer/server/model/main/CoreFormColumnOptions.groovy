package org.philimone.hds.explorer.server.model.main

class CoreFormColumnOptions {

    String id
    String columnCode
    String columnName
    int ordinal
    String optionValue
    String optionLabel
    String optionLabelCode
    Boolean readonly = false

    static constraints = {
        id maxSize: 32
        columnCode nullable: false, unique: ["ordinal", "optionValue"]
        columnName nullable: false
        ordinal min: 1, unique: "columnCode"
        optionValue nullable: false, unique: "columnCode"
        optionLabel nullable: true
        optionLabelCode nullable: true
        readonly nullable: false
    }

    static mapping = {
        table "core_form_column_options"

        id column: "id", generator: 'uuid'

        columnCode column: 'column_code'
        columnName column: 'column_name'
        ordinal column: "ordinal"
        optionValue column: 'option_value'
        optionLabel column: 'option_label'
        optionLabelCode column: 'option_label_code'
        readonly column: 'readonly'
    }
}
