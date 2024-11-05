databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1730727621439-3") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "old_head_code", tableName: "change_region_head_ext")
    }

    changeSet(author: "paul (generated)", id: "1730727621439-4") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "old_head_name", tableName: "change_region_head_ext")
    }
}
