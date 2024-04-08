databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1711631725598-30") {
        addColumn(tableName: "__raw_event") {
            column(name: "event_household_code", type: "varchar(40)", afterColumn: "event_uuid")
        }
    }

    changeSet(author: "paul (generated)", id: "1711631725598-31") {
        createIndex(indexName: "idx_household_code", tableName: "__raw_event") {
            column(name: "event_household_code")
        }
    }

}
