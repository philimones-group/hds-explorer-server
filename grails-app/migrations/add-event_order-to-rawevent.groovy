databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1652883947558-1") {
        addColumn(tableName: "__raw_event") {
            column(name: "event_order", type: "integer")
        }
    }

    changeSet(author: "paul (generated)", id: "1652883947558-2") {
        createIndex(indexName: "idx_event_order", tableName: "__raw_event") {
            column(name: "event_order")
        }
    }
}
