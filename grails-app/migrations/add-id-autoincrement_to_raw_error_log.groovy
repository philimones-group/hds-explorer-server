databaseChangeLog = {
    changeSet(author: "paul (coded)", id: "1711460610755-32") {
        dropPrimaryKey(tableName: "_raw_error_log")
    }

    changeSet(author: "paul (generated)", id: "1711460610755-33") {
        addColumn(tableName: "_raw_error_log") {
            column(autoIncrement: "true", name: "id", type: "bigint") {
                constraints(nullable: "false", primaryKey: "true")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1711460610755-34") {
        createIndex(indexName: "idx_raw_error_log_PK", tableName: "_raw_error_log", unique: "true") {
            column(name: "id")
        }
    }


}
