databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1790604350249-43") {
        dropIndex(indexName: "idx_hh_hierarchies", tableName: "household")
    }

    changeSet(author: "paul (generated)", id: "1790604350249-44") {
        createIndex(indexName: "idx_hh_region", tableName: "household") {
            column(name: "region")
        }
        createIndex(indexName: "idx_hh_h1", tableName: "household") {
            column(name: "hierarchy1")
        }
        createIndex(indexName: "idx_hh_h2", tableName: "household") {
            column(name: "hierarchy2")
        }
        createIndex(indexName: "idx_hh_h3", tableName: "household") {
            column(name: "hierarchy3")
        }
        createIndex(indexName: "idx_hh_h4", tableName: "household") {
            column(name: "hierarchy4")
        }
        createIndex(indexName: "idx_hh_h5", tableName: "household") {
            column(name: "hierarchy5")
        }
        createIndex(indexName: "idx_hh_h6", tableName: "household") {
            column(name: "hierarchy6")
        }
        createIndex(indexName: "idx_hh_h7", tableName: "household") {
            column(name: "hierarchy7")
        }
        createIndex(indexName: "idx_hh_h8", tableName: "household") {
            column(name: "hierarchy8")
        }
    }



}
