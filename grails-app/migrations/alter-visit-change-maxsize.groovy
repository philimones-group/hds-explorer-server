databaseChangeLog = {


    //remove indexes first
    changeSet(author: "paul (generated)", id: "1715255078473-30") {
        dropIndex(indexName: "idx_nonv", tableName: "_raw_visit")
    }

    changeSet(author: "paul (generated)", id: "1715255078473-31") {
        dropIndex(indexName: "idx_nonv", tableName: "visit")
    }


    changeSet(author: "paul (generated)", id: "1715255078473-32") {
        modifyDataType(tableName: "_raw_visit", columnName: "non_visited_members", newDataType:"varchar(1000)")
    }

    changeSet(author: "paul (generated)", id: "1715255078473-33") {
        modifyDataType(tableName: "visit", columnName: "non_visited_members", newDataType:"varchar(1000)")
    }
}
