databaseChangeLog = {
    changeSet(author: "paul (coded)", id: "1787341305176-32") {
        dropPrimaryKey(tableName: "household_proxy_head_ext")
    }

    changeSet(author: "paul (generated)", id: "1787341305176-33") {
        addColumn(tableName: "household_proxy_head_ext") {
            column(autoIncrement: "true", name: "id", type: "bigint", afterColumn: "collected_id") {
                constraints(nullable: "false", primaryKey: "true")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1787341305176-34") {
        createIndex(indexName: "idx_household_proxy_head_ext_PK", tableName: "household_proxy_head_ext", unique: "true") {
            column(name: "id")
        }
    }


}
