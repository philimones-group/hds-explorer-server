databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1729519755682-33") {
        createTable(tableName: "_raw_change_region_head") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_change_region_headPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)")

            column(name: "region_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "old_head_code", type: "VARCHAR(255)")

            column(name: "new_head_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "event_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "reason", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "extension_form", type: "MEDIUMBLOB")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_device_id", type: "VARCHAR(255)")

            column(name: "collected_household_id", type: "VARCHAR(255)")

            column(name: "collected_member_id", type: "VARCHAR(255)")

            column(name: "collected_start", type: "datetime(6)")

            column(name: "collected_end", type: "datetime(6)")

            column(name: "collected_date", type: "datetime(6)")

            column(name: "uploaded_date", type: "datetime(6)")

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "post_execution", type: "BIT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1729519755682-34") {
        createTable(tableName: "region_head_relationship") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "region_head_relationshipPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "region_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "region_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "head_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "head_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "start_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "start_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "end_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "end_date", type: "date")

            column(name: "reason", type: "VARCHAR(255)")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime(6)")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime(6)")

            column(name: "status", type: "INT")

        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-35") {
        addColumn(tableName: "region") {
            column(name: "head_id", type: "varchar(32)", afterColumn: "parent_region_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-36") {
        addColumn(tableName: "region") {
            column(name: "head_code", type: "varchar(255)", afterColumn: "head_id")
        }
    }


    changeSet(author: "paul (generated)", id: "1729543686290-39") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_change_region_head") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-40") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_change_region_head") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-41") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_change_region_head") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-42") {
        createIndex(indexName: "idx_coll_by", tableName: "_raw_change_region_head") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-43") {
        createIndex(indexName: "idx_head_code", tableName: "region") {
            column(name: "head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-44") {
        createIndex(indexName: "idx_head_code", tableName: "region_head_relationship") {
            column(name: "head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-45") {
        createIndex(indexName: "idx_new_headcode", tableName: "_raw_change_region_head") {
            column(name: "new_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-46") {
        createIndex(indexName: "idx_old_headcode", tableName: "_raw_change_region_head") {
            column(name: "old_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-47") {
        createIndex(indexName: "idx_region_code", tableName: "_raw_change_region_head") {
            column(name: "region_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-48") {
        createIndex(indexName: "idx_region_code", tableName: "region_head_relationship") {
            column(name: "region_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-49") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_change_region_head") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1729543686290-50") {
        addForeignKeyConstraint(baseColumnNames: "region_id", baseTableName: "region_head_relationship", constraintName: "FK350b2rdok1vqcknjy69j32esq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "region", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1729543686290-51") {
        addForeignKeyConstraint(baseColumnNames: "head_id", baseTableName: "region", constraintName: "FK4f2jlmmhrbn5791nph1gkhwdw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1729543686290-52") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "region_head_relationship", constraintName: "FK96ob7ato40v62wvfuwi4x9jti", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1729543686290-53") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "region_head_relationship", constraintName: "FKo556wdo2acwq8w0xsjevlm5i", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1729543686290-54") {
        addForeignKeyConstraint(baseColumnNames: "head_id", baseTableName: "region_head_relationship", constraintName: "FKrhi47wapo6kag6ioegi9qy8na", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

}
