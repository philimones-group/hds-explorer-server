databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1730184397382-33") {
        createTable(tableName: "change_region_head_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "change_region_head_extPK")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)")

            column(name: "region_code", type: "VARCHAR(255)") {
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

    changeSet(author: "paul (generated)", id: "1730184397382-34") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_CHANGE_REGION_HEAD_EXTCOLLECTED_ID_COL", tableName: "change_region_head_ext")
    }

    changeSet(author: "paul (generated)", id: "1730184397382-37") {
        createIndex(indexName: "idx_new_head_code", tableName: "change_region_head_ext") {
            column(name: "new_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730184397382-38") {
        createIndex(indexName: "idx_old_head_code", tableName: "change_region_head_ext") {
            column(name: "old_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730184397382-39") {
        createIndex(indexName: "idx_region_code", tableName: "change_region_head_ext") {
            column(name: "region_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730184397382-40") {
        createIndex(indexName: "idx_visit_code", tableName: "change_region_head_ext") {
            column(name: "visit_code")
        }
    }

}
