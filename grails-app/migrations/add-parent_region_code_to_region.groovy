databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1652819651045-1") {
        addColumn(tableName: "region") {
            column(name: "parent_region_id", type: "varchar(32)")
        }
    }

    changeSet(author: "paul (generated)", id: "1652819651045-2") {
        addForeignKeyConstraint(baseColumnNames: "parent_region_id", baseTableName: "region", constraintName: "FKeojo2oralh7nh9d3jkyxseuyj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "region", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652819651045-3") {
        dropForeignKeyConstraint(baseTableName: "region", constraintName: "FK1c2dnsuws0v5incjjfe1w9o8r")
    }
}
