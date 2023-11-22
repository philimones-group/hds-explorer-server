databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1700543123830-30") {
        addColumn(tableName: "core_form_extension") {
            column(name: "ext_form_definition", type: "mediumblob", afterColumn: "ext_form_id")
        }
    }
}
