databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "add_col_script-20261007") {
        addColumn(tableName: "data_export_report") {
            column(name: "script_file_name", type: "VARCHAR(255)", afterColumn: "dataset_file_name")
            column(name: "script_file_size", type: "BIGINT", afterColumn: "dataset_file_size")
        }
    }
}
