databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1726035455894-4") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "old_head_code", tableName: "_raw_change_head")
    }
}
