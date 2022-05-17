databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1652812844803-1") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "collected_by", tableName: "_raw_marital_relationship", validate: "true")
    }
}
