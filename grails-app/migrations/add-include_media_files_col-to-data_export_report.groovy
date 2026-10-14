databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "add_media_files_col-20261013") {
        addColumn(tableName: "data_export_report") {
            column(name: "include_media_files", type: "BIT", afterColumn: "include_dictionary") {
                constraints(nullable: "false")
            }
        }
    }
}
