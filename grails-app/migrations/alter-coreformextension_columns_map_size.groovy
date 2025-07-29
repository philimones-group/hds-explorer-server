databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1753776755373-33") {
        modifyDataType(tableName: "core_form_extension", columnName: "columns_mapping", newDataType:"VARCHAR(1000)")
    }

}
