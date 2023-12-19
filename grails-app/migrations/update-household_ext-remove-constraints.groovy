databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1703017175298-1") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "head_code", tableName: "household_ext")
    }

    changeSet(author: "paul (generated)", id: "1703017175298-2") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "head_name", tableName: "household_ext")
    }

}
