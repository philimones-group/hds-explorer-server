databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "add_ai_columns-20261019") {
        addColumn(tableName: "data_export_report") {
            column(name: "ai_prompt_mode", type: "BIT", afterColumn: "active_residents_only") {
                constraints(nullable: "false")
            }
            column(name: "ai_prompt_text", type: "text", afterColumn: "ai_prompt_mode")
            column(name: "executed_sql", type: "text", afterColumn: "ai_prompt_text")
            column(name: "sql_param_values", type: "text", afterColumn: "executed_sql")
        }
    }
}
