databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1738593670910-35") {
        addColumn(tableName: "_raw_change_head_relationship") {
            column(defaultValueNumeric: "0", name: "ordinal", type: "integer", afterColumn: "new_relationship_type") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1738593670910-36") {
        addColumn(tableName: "_raw_change_head_relationship") {
            column(name: "created_date", type: "datetime(6)", afterColumn: "ordinal")
        }
    }

    changeSet(author: "paul (generated)", id: "1738593670910-37") {
        addColumn(tableName: "_raw_death_relationships") {
            column(defaultValueNumeric: "0", name: "ordinal", type: "integer", afterColumn: "new_relationship_type") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1738593670910-38") {
        addColumn(tableName: "_raw_death_relationships") {
            column(name: "created_date", type: "datetime(6)", afterColumn: "ordinal")
        }
    }
}
