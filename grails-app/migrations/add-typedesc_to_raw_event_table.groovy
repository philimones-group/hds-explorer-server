databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1711222679112-30") {
        addColumn(tableName: "__raw_event") {
            column(name: "event_type_desc", type: "varchar(255)", afterColumn: "event_type")
        }
    }

    changeSet(author: "paul (generated)", id: "1711222679112-31") {
        createIndex(indexName: "idx_type_desc", tableName: "__raw_event") {
            column(name: "event_type_desc")
        }
    }
}
