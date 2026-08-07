databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1785914626496-38") {
        addColumn(tableName: "core_form_extension") {
            column(name: "ext_form_path", type: "varchar(255)", afterColumn: "ext_form_definition")
        }
    }
}
