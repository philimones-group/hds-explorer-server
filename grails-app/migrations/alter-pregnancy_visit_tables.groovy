databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1753538677983-35") {
        createTable(tableName: "_raw_pregnancy_visit") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_pregnancy_visitPK")
            }
            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "mother_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "visit_number", type: "INT") {
                constraints(nullable: "false")
            }
            column(name: "visit_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "visit_date", type: "date") {
                constraints(nullable: "false")
            }
            column(name: "weeks_gestation", type: "INT")
            column(name: "prenatal_care_received", type: "BIT")
            column(name: "prenatal_care_provider", type: "VARCHAR(255)")
            column(name: "complications_reported", type: "BIT")
            column(name: "complication_details", type: "VARCHAR(255)")
            column(name: "has_birth_plan", type: "BIT")
            column(name: "expected_birth_place", type: "VARCHAR(255)")
            column(name: "birth_place_other", type: "VARCHAR(255)")
            column(name: "transportation_plan", type: "BIT")
            column(name: "financial_preparedness", type: "BIT")

            column(name: "postpartum_complications", type: "BIT")
            column(name: "postpartum_complication_details", type: "VARCHAR(255)")
            column(name: "breastfeeding_status", type: "VARCHAR(255)")
            column(name: "resumed_daily_activities", type: "BIT")
            column(name: "attended_postpartum_checkup", type: "BIT")

            column(name: "modules", type: "VARCHAR(255)")
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


    changeSet(author: "paul (generated)", id: "1753538677983-36") {
        createTable(tableName: "_raw_pregnancy_visit_child") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_pregnancy_visit_childPK")
            }
            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pregnancy_visit_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }
            column(name: "pregnancy_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "outcome_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "child_code", type: "VARCHAR(255)")
            column(name: "child_status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "child_weight", type: "DOUBLE PRECISION")
            column(name: "had_illness_symptoms", type: "bit")
            column(name: "child_illness_symptoms", type: "VARCHAR(255)")
            column(name: "child_breastfeeding_status", type: "VARCHAR(255)")
            column(name: "child_immunization_status", type: "VARCHAR(255)")
            column(name: "notes", type: "VARCHAR(1000)")
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-37") {
        createTable(tableName: "pregnancy_visit") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_visitPK")
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
            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "mother_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }
            column(name: "mother_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "visit_number", type: "INT") {
                constraints(nullable: "false")
            }
            column(name: "visit_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "visit_date", type: "date") {
                constraints(nullable: "false")
            }
            column(name: "weeks_gestation", type: "INT")
            column(name: "prenatal_care_received", type: "BIT")
            column(name: "prenatal_care_provider", type: "VARCHAR(255)")
            column(name: "complications_reported", type: "BIT")
            column(name: "complication_details", type: "VARCHAR(255)")
            column(name: "has_birth_plan", type: "BIT")
            column(name: "expected_birth_place", type: "VARCHAR(255)")
            column(name: "birth_place_other", type: "VARCHAR(255)")
            column(name: "transportation_plan", type: "BIT")
            column(name: "financial_preparedness", type: "BIT")

            column(name: "postpartum_complications", type: "BIT")
            column(name: "postpartum_complication_details", type: "VARCHAR(255)")
            column(name: "breastfeeding_status", type: "VARCHAR(255)")
            column(name: "resumed_daily_activities", type: "BIT")
            column(name: "attended_postpartum_checkup", type: "BIT")

            column(name: "collected_uuid", type: "VARCHAR(255)")
            column(name: "collected_member_id", type: "VARCHAR(255)")
            column(name: "collected_household_id", type: "VARCHAR(255)")
            column(name: "collected_device_id", type: "VARCHAR(255)")
            column(name: "collected_start", type: "datetime(6)")
            column(name: "collected_end", type: "datetime(6)")
            column(name: "collected_date", type: "datetime(6)")
            column(name: "created_date", type: "datetime(6)")
            column(name: "updated_date", type: "datetime(6)")
            column(name: "created_by", type: "VARCHAR(32)")
            column(name: "updated_by", type: "VARCHAR(32)")
            column(name: "collected_by", type: "VARCHAR(32)")
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-38") {
        createTable(tableName: "pregnancy_visit_child") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_visit_childPK")
            }
            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }
            column(name: "pregnancy_visit_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }
            column(name: "pregnancy_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "outcome_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "child_id", type: "VARCHAR(32)")
            column(name: "child_code", type: "VARCHAR(255)")
            column(name: "child_status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "child_weight", type: "DOUBLE PRECISION")
            column(name: "had_illness_symptoms", type: "bit")
            column(name: "child_illness_symptoms", type: "VARCHAR(255)")
            column(name: "child_breastfeeding_status", type: "VARCHAR(255)")
            column(name: "child_immunization_status", type: "VARCHAR(255)")
            column(name: "notes", type: "VARCHAR(1000)")
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-39") {
        createTable(tableName: "pregnancy_visit_ext") {
            column(name: "id", autoIncrement: "true", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_visit_extPK")
            }
            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "pregnancy_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "mother_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "mother_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "visit_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "visit_number", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-40") {
        createTable(tableName: "pregnancy_visit_child_ext") {
            column(name: "id", autoIncrement: "true", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_visit_child_extPK")
            }
            column(name: "pregnancy_visit_ext_id", type: "BIGINT") {
                constraints(nullable: "false")
            }
            column(name: "child_outcome_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "child_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "child_gender", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "child_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "child_status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-41") {
        addColumn(tableName: "pregnancy_registration") {
            column(name: "summary_antepartum_count", type: "integer", afterColumn: "visit_code")
            column(name: "summary_postpartum_count", type: "integer", afterColumn: "summary_antepartum_count")
            column(name: "summary_last_visit_status", type: "varchar(255)", afterColumn: "summary_postpartum_count")
            column(name: "summary_last_visit_type", type: "varchar(255)", afterColumn: "summary_last_visit_status")
            column(name: "summary_last_visit_date", type: "date", afterColumn: "summary_last_visit_type")
            column(name: "summary_first_visit_date", type: "date", afterColumn: "summary_last_visit_date")
            column(name: "summary_has_pregnancy_outcome", type: "bit", afterColumn: "summary_first_visit_date")
            column(name: "summary_nr_outcomes", type: "integer", afterColumn: "summary_has_pregnancy_outcome")
            column(name: "summary_followup_completed", type: "bit", afterColumn: "summary_nr_outcomes")
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-50") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_PREGNANCY_VISITCOLLECTED_UUID_COL", tableName: "pregnancy_visit")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-52") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_PREGNANCY_VISIT_EXTCOLLECTED_ID_COL", tableName: "pregnancy_visit_ext")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-54") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "outcome_type", tableName: "_raw_pregnancy_child", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-55") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "outcome_type", tableName: "pregnancy_child", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-56") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_pregnancy_visit") {
            column(name: "collected_device_id")
        }
        createIndex(indexName: "idx_chouseid", tableName: "_raw_pregnancy_visit") {
            column(name: "collected_household_id")
        }
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_pregnancy_visit") {
            column(name: "collected_member_id")
        }
        createIndex(indexName: "idx_collected_by", tableName: "_raw_pregnancy_visit") {
            column(name: "collected_by")
        }
        createIndex(indexName: "idx_code", tableName: "_raw_pregnancy_visit") {
            column(name: "code")
        }
        createIndex(indexName: "idx_mother_code", tableName: "_raw_pregnancy_visit") {
            column(name: "mother_code")
        }
        createIndex(indexName: "idx_status", tableName: "_raw_pregnancy_visit") {
            column(name: "status")
        }
        createIndex(indexName: "idx_visit_code", tableName: "_raw_pregnancy_visit") {
            column(name: "visit_code")
        }
        createIndex(indexName: "idx_visit_type", tableName: "_raw_pregnancy_visit") {
            column(name: "visit_type")
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-57") {
        createIndex(indexName: "idx_child_code", tableName: "_raw_pregnancy_visit_child") {
            column(name: "child_code")
        }
        createIndex(indexName: "idx_child_status", tableName: "_raw_pregnancy_visit_child") {
            column(name: "child_status")
        }
        createIndex(indexName: "idx_outcome_type", tableName: "_raw_pregnancy_visit_child") {
            column(name: "outcome_type")
        }
        createIndex(indexName: "idx_preg_code", tableName: "_raw_pregnancy_visit_child") {
            column(name: "pregnancy_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-58") {
        createIndex(indexName: "idx_cdeviceid", tableName: "pregnancy_visit") {
            column(name: "collected_device_id")
        }
        createIndex(indexName: "idx_chouseid", tableName: "pregnancy_visit") {
            column(name: "collected_household_id")
        }
        createIndex(indexName: "idx_cmemberid", tableName: "pregnancy_visit") {
            column(name: "collected_member_id")
        }
        createIndex(indexName: "idx_code", tableName: "pregnancy_visit") {
            column(name: "code")
        }
        createIndex(indexName: "idx_mother_code", tableName: "pregnancy_visit") {
            column(name: "mother_code")
        }
        createIndex(indexName: "idx_visit_code", tableName: "pregnancy_visit") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-59") {
        createIndex(indexName: "idx_child_code", tableName: "pregnancy_visit_child") {
            column(name: "child_code")
        }
        createIndex(indexName: "idx_preg_code", tableName: "pregnancy_visit_child") {
            column(name: "pregnancy_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-60") {
        createIndex(indexName: "idx_mother_code", tableName: "pregnancy_visit_ext") {
            column(name: "mother_code")
        }
        createIndex(indexName: "idx_pregnancy_code", tableName: "pregnancy_visit_ext") {
            column(name: "pregnancy_code")
        }
        createIndex(indexName: "idx_visit_code", tableName: "pregnancy_visit_ext") {
            column(name: "visit_code")
        }
        createIndex(indexName: "idx_visit_type", tableName: "pregnancy_visit_ext") {
            column(name: "visit_type")
        }
    }

    changeSet(author: "paul (generated)", id: "1753538677983-61") {
        createIndex(indexName: "idx_child_code", tableName: "pregnancy_visit_child_ext") {
            column(name: "child_code")
        }
    }


    changeSet(author: "paul (generated)", id: "1753538677983-80") {
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "pregnancy_visit", constraintName: "FK12qwvmtue1w4t94pblwwgw375", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-81") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "pregnancy_visit", constraintName: "FK3frj3nxwe74ch3aa5ag04wh49", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-82") {
        addForeignKeyConstraint(baseColumnNames: "pregnancy_visit_ext_id", baseTableName: "pregnancy_visit_child_ext", constraintName: "FK62stc87nebkuptltsagoxmnt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "pregnancy_visit_ext", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-83") {
        addForeignKeyConstraint(baseColumnNames: "mother_id", baseTableName: "pregnancy_visit", constraintName: "FK9v56u8s6jhhrbktiuk66mvcfd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-84") {
        addForeignKeyConstraint(baseColumnNames: "child_id", baseTableName: "pregnancy_visit_child", constraintName: "FKgb87ahnuss2gc4lcxrhytitko", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-85") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "pregnancy_visit", constraintName: "FKlor2mx6352ocn7lp4e4406v9h", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-86") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "pregnancy_visit", constraintName: "FKp6hv3k6aj69cdwyt5oxa5pf3o", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-87") {
        addForeignKeyConstraint(baseColumnNames: "pregnancy_visit_id", baseTableName: "pregnancy_visit_child", constraintName: "FKqogvyh5qw8bf82meh2hw77kv9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "pregnancy_visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1753538677983-88") {
        addForeignKeyConstraint(baseColumnNames: "pregnancy_visit_id", baseTableName: "_raw_pregnancy_visit_child", constraintName: "FKsb46mp6il51prvrsaddymfsjk", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_raw_pregnancy_visit", validate: "true")
    }


}
