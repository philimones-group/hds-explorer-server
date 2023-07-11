package org.philimone.hds.explorer.server.model.main

class CoreFormColumnMap {

    String id
    String formName
    String columnName
    boolean enabled

    static constraints = {
        id maxSize: 32
        formName nullable: false, unique: "columnName"
        columnName nullable: false
        enabled()
    }

    static mapping = {
        table "core_form_column_map"

        id column: "id", generator: 'uuid'

        formName column: 'form_name'
        columnName column: 'column_name'
        enabled column: 'enabled'
    }
}
