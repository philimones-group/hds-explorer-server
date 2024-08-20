databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1723151976893-30") {
        addColumn(tableName: "head_relationship") {
            column(name: "status", type: "integer")
        }
    }

    changeSet(author: "paul (generated)", id: "1723151976893-31") {
        addColumn(tableName: "residency") {
            column(name: "status", type: "integer")
        }
    }

    changeSet(author: "paul (generated)", id: "1723151976893-32") {
        addColumn(tableName: "marital_relationship") {
            column(name: "status", type: "integer")
        }
    }

}
