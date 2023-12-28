databaseChangeLog = {
    changeSet(author: "paul (generated)", id: "1703761230594-1") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "ext_migration_type", tableName: "in_migration_ext")
    }
}
