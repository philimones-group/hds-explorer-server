databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1700242513583-30") {
        createTable(tableName: "changehead_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "changehead_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
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

    changeSet(author: "paul (generated)", id: "1700242513583-31") {
        createTable(tableName: "death_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "death_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-32") {
        createTable(tableName: "household_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "household_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "household_name", type: "VARCHAR(255)") {
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

    changeSet(author: "paul (generated)", id: "1700242513583-33") {
        createTable(tableName: "in_migration_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "in_migration_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "migration_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ext_migration_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-34") {
        createTable(tableName: "incomplete_visit_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "incomplete_visit_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
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

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-35") {
        createTable(tableName: "marital_relationship_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "marital_relationship_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_a", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_a_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_b", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_b_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-36") {
        createTable(tableName: "member_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "member_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
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

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-37") {
        createTable(tableName: "out_migration_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "out_migration_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "migration_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-39") {
        createTable(tableName: "pregnancy_outcome_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_outcome_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
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

            column(name: "father_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "father_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-38") {
        createTable(tableName: "pregnancy_child_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_child_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pregnancy_outcome_ext_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "child_outcome_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "child_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "child_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "child_gender", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-40") {
        createTable(tableName: "pregnancy_registration_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_registration_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
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

            column(name: "pregnancy_status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-41") {
        createTable(tableName: "region_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "region_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "collected_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "parent_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "parent_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "region_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "region_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-42") {
        createTable(tableName: "visit_ext") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "visit_extPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
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

            column(name: "round_number", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-43") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_CHANGEHEAD_EXTCOLLECTED_ID_COL", tableName: "changehead_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-44") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_DEATH_EXTCOLLECTED_ID_COL", tableName: "death_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-45") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_HOUSEHOLD_EXTCOLLECTED_ID_COL", tableName: "household_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-46") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_INCOMPLETE_VISIT_EXTCOLLECTED_ID_COL", tableName: "incomplete_visit_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-47") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_IN_MIGRATION_EXTCOLLECTED_ID_COL", tableName: "in_migration_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-48") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_MARITAL_RELATIONSHIP_EXTCOLLECTED_ID_COL", tableName: "marital_relationship_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-49") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_MEMBER_EXTCOLLECTED_ID_COL", tableName: "member_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-50") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_OUT_MIGRATION_EXTCOLLECTED_ID_COL", tableName: "out_migration_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-51") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_PREGNANCY_OUTCOME_EXTCOLLECTED_ID_COL", tableName: "pregnancy_outcome_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-52") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_PREGNANCY_REGISTRATION_EXTCOLLECTED_ID_COL", tableName: "pregnancy_registration_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-53") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_REGION_EXTCOLLECTED_ID_COL", tableName: "region_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-54") {
        addUniqueConstraint(columnNames: "collected_id", constraintName: "UC_VISIT_EXTCOLLECTED_ID_COL", tableName: "visit_ext")
    }

    changeSet(author: "paul (generated)", id: "1700242513583-55") {
        createIndex(indexName: "idx_child_code", tableName: "pregnancy_child_ext") {
            column(name: "child_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-56") {
        createIndex(indexName: "idx_father_code", tableName: "pregnancy_outcome_ext") {
            column(name: "father_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-57") {
        createIndex(indexName: "idx_head_code", tableName: "household_ext") {
            column(name: "head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-58") {
        createIndex(indexName: "idx_household_code", tableName: "changehead_ext") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-59") {
        createIndex(indexName: "idx_household_code", tableName: "household_ext") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-60") {
        createIndex(indexName: "idx_household_code", tableName: "incomplete_visit_ext") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-61") {
        createIndex(indexName: "idx_household_code", tableName: "member_ext") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-62") {
        createIndex(indexName: "idx_household_code", tableName: "visit_ext") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-63") {
        createIndex(indexName: "idx_member_code", tableName: "death_ext") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-64") {
        createIndex(indexName: "idx_member_code", tableName: "in_migration_ext") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-65") {
        createIndex(indexName: "idx_member_code", tableName: "incomplete_visit_ext") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-66") {
        createIndex(indexName: "idx_member_code", tableName: "member_ext") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-67") {
        createIndex(indexName: "idx_member_code", tableName: "out_migration_ext") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-68") {
        createIndex(indexName: "idx_mother_code", tableName: "pregnancy_outcome_ext") {
            column(name: "mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-69") {
        createIndex(indexName: "idx_mother_code", tableName: "pregnancy_registration_ext") {
            column(name: "mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-70") {
        createIndex(indexName: "idx_new_head_code", tableName: "changehead_ext") {
            column(name: "new_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-71") {
        createIndex(indexName: "idx_old_head_code", tableName: "changehead_ext") {
            column(name: "old_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-72") {
        createIndex(indexName: "idx_parent_code", tableName: "region_ext") {
            column(name: "parent_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-73") {
        createIndex(indexName: "idx_pregnancy_code", tableName: "pregnancy_outcome_ext") {
            column(name: "pregnancy_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-74") {
        createIndex(indexName: "idx_pregnancy_code", tableName: "pregnancy_registration_ext") {
            column(name: "pregnancy_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-75") {
        createIndex(indexName: "idx_region_code", tableName: "region_ext") {
            column(name: "region_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-76") {
        createIndex(indexName: "idx_visit_code", tableName: "changehead_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-77") {
        createIndex(indexName: "idx_visit_code", tableName: "death_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-78") {
        createIndex(indexName: "idx_visit_code", tableName: "in_migration_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-79") {
        createIndex(indexName: "idx_visit_code", tableName: "incomplete_visit_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-80") {
        createIndex(indexName: "idx_visit_code", tableName: "marital_relationship_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-81") {
        createIndex(indexName: "idx_visit_code", tableName: "member_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-82") {
        createIndex(indexName: "idx_visit_code", tableName: "out_migration_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-83") {
        createIndex(indexName: "idx_visit_code", tableName: "pregnancy_outcome_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-84") {
        createIndex(indexName: "idx_visit_code", tableName: "pregnancy_registration_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-85") {
        createIndex(indexName: "idx_visit_code", tableName: "visit_ext") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700242513583-86") {
        addForeignKeyConstraint(baseColumnNames: "pregnancy_outcome_ext_id", baseTableName: "pregnancy_child_ext", constraintName: "FKrwxuw7yvua1jxnnjo6hp6v132", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "pregnancy_outcome_ext", validate: "true")
    }

}