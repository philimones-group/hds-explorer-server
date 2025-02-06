databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1738788297367-35") {
        addColumn(tableName: "_raw_visit") {
            column(name: "visit_reason_other", type: "varchar(255)", afterColumn: "visit_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1738788297367-36") {
        addColumn(tableName: "_raw_visit") {
            column(name: "other_not_possible_reason", type: "varchar(255)", afterColumn: "visit_not_possible_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1738788297367-37") {
        addColumn(tableName: "visit") {
            column(name: "visit_reason_other", type: "varchar(255)", afterColumn: "visit_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1738788297367-38") {
        addColumn(tableName: "visit") {
            column(name: "other_not_possible_reason", type: "varchar(255)", afterColumn: "visit_not_possible_reason")
        }
    }

}
