databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1703789492144-29") {
        createTable(tableName: "change_head_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "change_head_extPK")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "old_head_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "old_head_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "new_head_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "new_head_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1703789492144-30") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_CHANGE_HEAD_EXTCOLLECTED_ID_COL", tableName: "change_head_ext")
    }

    changeSet(author: "paul (generated)", id: "1703789492144-31") {
        createIndex(indexName: "idx_household_code", tableName: "change_head_ext") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1703789492144-32") {
        createIndex(indexName: "idx_new_head_code", tableName: "change_head_ext") {
            column(name: "new_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1703789492144-33") {
        createIndex(indexName: "idx_old_head_code", tableName: "change_head_ext") {
            column(name: "old_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1703789492144-34") {
        createIndex(indexName: "idx_visit_code", tableName: "change_head_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1703789492144-36") {
        dropTable(tableName: "changehead_ext")
    }

}
