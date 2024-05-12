databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1715255078472-30") {
        addColumn(tableName: "_raw_marital_relationship") {
            column(name: "is_polygamic", type: "bit", afterColumn: "member_b_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1715255078472-31") {
        addColumn(tableName: "_raw_marital_relationship") {
            column(name: "polygamic_id", type: "varchar(255)", afterColumn: "is_polygamic")
        }
    }

    changeSet(author: "paul (generated)", id: "1715255078472-32") {
        addColumn(tableName: "marital_relationship") {
            column(name: "is_polygamic", type: "bit", afterColumn: "member_b_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1715255078472-33") {
        addColumn(tableName: "marital_relationship") {
            column(name: "polygamic_id", type: "varchar(255)", afterColumn: "is_polygamic")
        }
    }

    changeSet(author: "paul (generated)", id: "1715255078472-34") {
        createIndex(indexName: "idx_polygamic_id", tableName: "_raw_marital_relationship") {
            column(name: "polygamic_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1715255078472-35") {
        createIndex(indexName: "idx_polygamic_id", tableName: "marital_relationship") {
            column(name: "polygamic_id")
        }
    }

}