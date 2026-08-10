databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1786341304190-38") {
        addColumn(tableName: "pregnancy_child_ext") {
            column(defaultValueNumeric: "0", name: "ordinal_number", type: "integer", afterColumn: "pregnancy_outcome_ext_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1786341304190-39") {
        addColumn(tableName: "pregnancy_visit_child_ext") {
            column(name: "collected_id", type: "varchar(255)", afterColumn: "id")
        }
    }

    changeSet(author: "paul (generated)", id: "1786341304190-40") {
        addColumn(tableName: "pregnancy_visit_child_ext") {
            column(defaultValueNumeric: "0", name: "ordinal_number", type: "integer", afterColumn: "pregnancy_visit_ext_id")
        }
    }

}
