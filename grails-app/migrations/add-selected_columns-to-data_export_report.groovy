databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1790609350257-40") {
        addColumn(tableName: "data_export_report") {
            column(name: "selected_columns", type: "text", afterColumn: "random_sample_percent")
        }
    }
}
