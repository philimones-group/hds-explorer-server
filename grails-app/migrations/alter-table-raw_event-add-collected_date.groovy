databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1724935388488-32") {
        addColumn(tableName: "__raw_event") {
            column(defaultValue: "1900-01-01", name: "collected_date", type: "datetime(6)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1724935388488-33") {
        createIndex(indexName: "idx_coldate", tableName: "__raw_event") {
            column(name: "collected_date")
        }
    }

    changeSet(author: "paul (generated)", id: "1724935388488-34") {
        addColumn(tableName: "_raw_error_log") {
            column(defaultValue: "1900-01-01", name: "collected_date", type: "datetime(6)") {
                constraints(nullable: "false")
            }
        }
    }

}
