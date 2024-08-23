databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1723557476626-30") {
        createTable(tableName: "_partially_disabled") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_partially_disabledPK")
            }

            column(name: "entity", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "household_id", type: "VARCHAR(32)")

            column(name: "household_code", type: "VARCHAR(255)")

            column(name: "member_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "spouse_id", type: "VARCHAR(32)")

            column(name: "spouse_code", type: "VARCHAR(255)")

            column(name: "end_type", type: "VARCHAR(255)")

            column(name: "end_date", type: "date")

            column(name: "enabled", type: "bit") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1723557476626-31") {
        createIndex(indexName: "idx_entity", tableName: "_partially_disabled") {
            column(name: "entity")
        }
    }

    changeSet(author: "paul (generated)", id: "1723557476626-32") {
        createIndex(indexName: "idx_house_code", tableName: "_partially_disabled") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1723557476626-33") {
        createIndex(indexName: "idx_member_code", tableName: "_partially_disabled") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1723557476626-36") {
        createIndex(indexName: "idx_spouse_code", tableName: "_partially_disabled") {
            column(name: "spouse_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1723557476626-34") {
        addForeignKeyConstraint(baseColumnNames: "member_id", baseTableName: "_partially_disabled", constraintName: "FKbalya2lxg9a66i038sfm9vtf1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1723557476626-35") {
        addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "_partially_disabled", constraintName: "FKrld4wakjeg6ajqbgjtjn40gg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1723557476626-37") {
        addForeignKeyConstraint(baseColumnNames: "spouse_id", baseTableName: "_partially_disabled", constraintName: "FK5rmvre2pyyljwkcxrd6xgspcc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

}
