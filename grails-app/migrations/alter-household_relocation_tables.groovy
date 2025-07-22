databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1753170346504-35") {
        createTable(tableName: "_raw_household_relocation") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_household_relocationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "origin_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "destination_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "head_code", type: "VARCHAR(255)")

            column(name: "event_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "reason", type: "VARCHAR(255)")

            column(name: "reason_other", type: "VARCHAR(255)")

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

            column(name: "post_execution", type: "BIT") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-36") {
        createTable(tableName: "household_relocation") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "household_relocationPK")
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

            column(name: "origin_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "origin_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "destination_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "destination_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "event_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "reason", type: "VARCHAR(255)")

            column(name: "reasonOther", type: "VARCHAR(255)")

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_device_id", type: "VARCHAR(255)")

            column(name: "collected_household_id", type: "VARCHAR(255)")

            column(name: "collected_member_id", type: "VARCHAR(255)")

            column(name: "collected_start", type: "datetime(6)")

            column(name: "collected_end", type: "datetime(6)")

            column(name: "collected_date", type: "datetime(6)")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime(6)")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime(6)")

        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-37") {
        createTable(tableName: "household_relocation_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "household_relocation_extPK")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "origin_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "destination_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "head_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "head_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-38") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_HOUSEHOLD_RELOCATIONCOLLECTED_UUID_COL", tableName: "household_relocation")
    }

    changeSet(author: "paul (generated)", id: "1753170346504-39") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_HOUSEHOLD_RELOCATION_EXTCOLLECTED_ID_COL", tableName: "household_relocation_ext")
    }

    changeSet(author: "paul (generated)", id: "1753170346504-42") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_household_relocation") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-43") {
        createIndex(indexName: "idx_cdeviceid", tableName: "household_relocation") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-44") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_household_relocation") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-45") {
        createIndex(indexName: "idx_chouseid", tableName: "household_relocation") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-46") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_household_relocation") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-47") {
        createIndex(indexName: "idx_cmemberid", tableName: "household_relocation") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-48") {
        createIndex(indexName: "idx_coll_by", tableName: "_raw_household_relocation") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-49") {
        createIndex(indexName: "idx_destination_code", tableName: "household_relocation") {
            column(name: "destination_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-50") {
        createIndex(indexName: "idx_destination_code", tableName: "household_relocation_ext") {
            column(name: "destination_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-51") {
        createIndex(indexName: "idx_destination_hhcode", tableName: "_raw_household_relocation") {
            column(name: "destination_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-52") {
        createIndex(indexName: "idx_head_code", tableName: "household_relocation_ext") {
            column(name: "head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-53") {
        createIndex(indexName: "idx_headcode", tableName: "_raw_household_relocation") {
            column(name: "head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-54") {
        createIndex(indexName: "idx_origin_code", tableName: "household_relocation") {
            column(name: "origin_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-55") {
        createIndex(indexName: "idx_origin_code", tableName: "household_relocation_ext") {
            column(name: "origin_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-56") {
        createIndex(indexName: "idx_origin_hhcode", tableName: "_raw_household_relocation") {
            column(name: "origin_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-57") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_household_relocation") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-58") {
        createIndex(indexName: "idx_visit_code", tableName: "household_relocation") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-59") {
        createIndex(indexName: "idx_visit_code", tableName: "household_relocation_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753170346504-60") {
        addForeignKeyConstraint(baseColumnNames: "origin_id", baseTableName: "household_relocation", constraintName: "FK33dc34onid34n542cgx7kc0vd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753170346504-61") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "household_relocation", constraintName: "FK3uw75w3s815px68ijn8fp5t04", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753170346504-62") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "household_relocation", constraintName: "FK70fyvdhd8ccc6e0aw1akd0t5x", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753170346504-63") {
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "household_relocation", constraintName: "FK9alxvgyxmlyx1wyo0a13qf3vd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753170346504-64") {
        addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "household_relocation", constraintName: "FKd9xk3nq6or89c8o0inqtucqcq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753170346504-65") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "household_relocation", constraintName: "FKhhhs97l3ks36s99th4iab17hp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

}
