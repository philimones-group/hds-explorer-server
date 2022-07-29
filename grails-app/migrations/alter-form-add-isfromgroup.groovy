databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1659103065231-1") {
        addColumn(tableName: "form") {
            column(name: "is_form_group_exclusive", type: "bit") {
                constraints(nullable: "false")
            }
        }
    }
}
