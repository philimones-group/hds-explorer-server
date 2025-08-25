databaseChangeLog = {

    //create table household_proxy_head
    changeSet(author: "paul (generated)", id: "1755673888289-38") {
        createTable(tableName: "household_proxy_head") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "household_proxy_headPK")
            }
            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "visit_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }
            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "household_id", type: "VARCHAR(32)")
            column(name: "household_code", type: "VARCHAR(32)")
            column(name: "proxy_head_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "proxy_head_member_id", type: "VARCHAR(32)")
            column(name: "proxy_head_code", type: "VARCHAR(255)")
            column(name: "proxy_head_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "proxy_head_role", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "start_date", type: "date") {
                constraints(nullable: "false")
            }
            column(name: "end_date", type: "date")

            column(name: "reason", type: "VARCHAR(255)")
            column(name: "reason_other", type: "VARCHAR(255)")

            column(name: "status", type: "INT")

            column(name: "collected_uuid", type: "VARCHAR(255)")
            column(name: "collected_by", type: "VARCHAR(32)")
            column(name: "collected_member_id", type: "VARCHAR(255)")
            column(name: "collected_household_id", type: "VARCHAR(255)")
            column(name: "collected_device_id", type: "VARCHAR(255)")
            column(name: "collected_start", type: "datetime(6)")
            column(name: "collected_end", type: "datetime(6)")
            column(name: "collected_date", type: "datetime(6)")
            column(name: "created_by", type: "VARCHAR(32)")
            column(name: "created_date", type: "datetime(6)")
            column(name: "updated_by", type: "VARCHAR(32)")
            column(name: "updated_date", type: "datetime(6)")
        }

        createIndex(indexName: "idx_hh_household_code", tableName: "household_proxy_head") {
            column(name: "household_code")
        }

        createIndex(indexName: "idx_hh_proxy_code", tableName: "household_proxy_head") {
            column(name: "proxy_head_code")
        }
        createIndex(indexName: "idx_hh_proxy_role", tableName: "household_proxy_head") {
            column(name: "proxy_head_role")
        }
        createIndex(indexName: "idx_hh_proxy_type", tableName: "household_proxy_head") {
            column(name: "proxy_head_type")
        }
        createIndex(indexName: "idx_hh_start_reason", tableName: "household_proxy_head") {
            column(name: "reason")
        }
        createIndex(indexName: "idx_visit_code", tableName: "household_proxy_head") {
            column(name: "visit_code")
        }

        createIndex(indexName: "idx_cdeviceid", tableName: "household_proxy_head") {
            column(name: "collected_device_id")
        }
        createIndex(indexName: "idx_chouseid", tableName: "household_proxy_head") {
            column(name: "collected_household_id")
        }
        createIndex(indexName: "idx_cmemberid", tableName: "household_proxy_head") {
            column(name: "collected_member_id")
        }

        addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "household_proxy_head", constraintName: "FK19s5v5ps3xgegomcuq49ybo7k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "household_proxy_head", constraintName: "FKchbo1nnsq9vyd6knj66j6d080", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "proxy_head_member_id", baseTableName: "household_proxy_head", constraintName: "FKhykddlex03a7yn4bkvff4ya4a", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "household_proxy_head", constraintName: "FK5963gy97pumdrwau036k0ggui", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "household_proxy_head", constraintName: "FKngsnj5ldncx6j9h6520a8o6vh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")

        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "household_proxy_head", constraintName: "FK3PBGAXxvmsGVOi1vXz5Ygja61", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_HHPH_COLLECTED_UUID_COL", tableName: "household_proxy_head")
    }

    //create table raw_household_proxy_head
    changeSet(author: "paul (generated)", id: "1755673888289-40") {
        createTable(tableName: "_raw_household_proxy_head") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "raw_household_proxy_headPK")
            }
            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "proxy_head_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "proxy_head_code", type: "VARCHAR(255)")
            column(name: "proxy_head_name", type: "VARCHAR(255)")
            column(name: "proxy_head_role", type: "VARCHAR(255)")

            column(name: "event_date", type: "date")
            column(name: "reason", type: "VARCHAR(255)")
            column(name: "reason_other", type: "VARCHAR(255)")

            column(name: "modules", type: "VARCHAR(255)")
            column(name: "extension_form", type: "MEDIUMBLOB")

            column(name: "collected_by", type: "VARCHAR(255)")
            column(name: "collected_device_id", type: "VARCHAR(255)")
            column(name: "collected_start", type: "datetime(6)")
            column(name: "collected_end", type: "datetime(6)")
            column(name: "collected_date", type: "datetime(6)")
            column(name: "uploaded_date", type: "datetime(6)")

            column(name: "post_execution", type: "BIT") {
                constraints(nullable: "false")
            }
            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }
        }

        createIndex(indexName: "idx_raw_hhph_household_code", tableName: "_raw_household_proxy_head") {
            column(name: "household_code")
        }
        createIndex(indexName: "idx_raw_hhph_proxy_code", tableName: "_raw_household_proxy_head") {
            column(name: "proxy_head_code")
        }
        createIndex(indexName: "idx_raw_hhph_proxy_role", tableName: "_raw_household_proxy_head") {
            column(name: "proxy_head_role")
        }
        createIndex(indexName: "idx_raw_hhph_type", tableName: "_raw_household_proxy_head") {
            column(name: "proxy_head_type")
        }
        createIndex(indexName: "idx_hh_start_reason", tableName: "_raw_household_proxy_head") {
            column(name: "reason")
        }
    }

    ///create extension table
    changeSet(author: "paul (generated)", id: "1755673888289-42") {
        createTable(tableName: "household_proxy_head_ext") {
            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false", unique: "true", primaryKey: "true", primaryKeyName: "pk_household_proxy_head_ext")
            }
            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "household_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "proxy_head_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "proxy_head_code", type: "VARCHAR(255)")
            column(name: "proxy_head_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }

        // Indexes
        createIndex(indexName: "idx_visit_code", tableName: "household_proxy_head_ext") {
            column(name: "visit_code")
        }
        createIndex(indexName: "idx_household_code", tableName: "household_proxy_head_ext") {
            column(name: "household_code")
        }
        createIndex(indexName: "idx_proxy_head_type", tableName: "household_proxy_head_ext") {
            column(name: "proxy_head_type")
        }
        createIndex(indexName: "idx_proxy_head_code", tableName: "household_proxy_head_ext") {
            column(name: "proxy_head_code")
        }
    }

    //remove column on household table
    changeSet(author: "paul (generated)", id: "1755673888289-33") {
        dropColumn(columnName: "sec_head_code", tableName: "household")
    }

    //add new columns to household
    changeSet(author: "paul (generated)", id: "1755673888289-34") {
        addColumn(tableName: "household") {
            column(name: "type", type: "varchar(255)", defaultValue: "REGULAR", afterColumn: "region") {
                constraints(nullable: "false")
            }
            column(name: "institution_type", type: "varchar(255)", afterColumn: "type")
            column(name: "institution_type_other", type: "varchar(255)", afterColumn: "institution_type")
            column(name: "proxy_head_id", type: "varchar(32)", afterColumn: "head_name")
        }

        createIndex(indexName: "idx_hh_type", tableName: "household") {
            column(name: "type")
        }
        createIndex(indexName: "idx_inst_type", tableName: "household") {
            column(name: "institution_type")
        }
        addForeignKeyConstraint(baseColumnNames: "proxy_head_id", baseTableName: "household", constraintName: "FK32liqj81c8fxh57nwwk8h9a2e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household_proxy_head", validate: "true")
    }

    //add new columns to _raw_household
    changeSet(author: "paul (generated)", id: "1755673888289-36") {
        addColumn(tableName: "_raw_household") {
            column(name: "household_type", type: "varchar(255)", defaultValue: "REGULAR", afterColumn: "household_name") {
                constraints(nullable: "false")
            }
            column(name: "institution_type", type: "varchar(255)", afterColumn: "household_type")
            column(name: "institution_type_other", type: "varchar(255)", afterColumn: "institution_type")
        }

        createIndex(indexName: "idx_hh_type", tableName: "_raw_household") {
            column(name: "household_type")
        }
        createIndex(indexName: "idx_inst_type", tableName: "_raw_household") {
            column(name: "institution_type")
        }
    }

    changeSet(author: "paul (generated)", id: "1755673888289-44") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "head_relationship_type", tableName: "_raw_ext_inmigration")
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "head_relationship_type", tableName: "_raw_member_enu")
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "head_relationship_type", tableName: "_raw_inmigration")
    }

}
