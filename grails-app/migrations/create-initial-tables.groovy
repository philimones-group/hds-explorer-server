databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1652717035723-1") {
        createTable(tableName: "__raw_event") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "__raw_eventPK")
            }

            column(name: "key_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "event_type", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "event_uuid", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "event_code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "child_codes", type: "VARCHAR(255)")

            column(name: "processed", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-2") {
        createTable(tableName: "_application_param") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_application_paramPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "type", type: "VARCHAR(255)")

            column(name: "value", type: "VARCHAR(255)")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-3") {
        createTable(tableName: "_log_group") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_log_groupPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "group_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "VARCHAR(255)")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-4") {
        createTable(tableName: "_log_report") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_log_reportPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "report_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "group_id", type: "VARCHAR(32)")

            column(name: "description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "status_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "key_timestamp", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "start_time", type: "datetime")

            column(name: "end_time", type: "datetime")

            column(name: "enabled", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-5") {
        createTable(tableName: "_log_report_file") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_log_report_filePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "log_report_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "key_timestamp", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "start_time", type: "datetime")

            column(name: "end_time", type: "datetime")

            column(name: "creation_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "filename", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "processed_count", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "errors_count", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-6") {
        createTable(tableName: "_notification") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_notificationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "user_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "subject", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "message", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "readed", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-7") {
        createTable(tableName: "_raw_change_head") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_change_headPK")
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

            column(name: "old_head_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "new_head_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "event_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "reason", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-8") {
        createTable(tableName: "_raw_change_head_relationship") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_change_head_relationshipPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "change_head_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "new_member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "new_relationship_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-9") {
        createTable(tableName: "_raw_death") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_deathPK")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "death_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "death_cause", type: "VARCHAR(255)")

            column(name: "death_place", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-10") {
        createTable(tableName: "_raw_death_relationships") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_death_relationshipsPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "death_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "new_member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "new_relationship_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-11") {
        createTable(tableName: "_raw_error_log") {
            column(name: "uuid", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_error_logPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "log_report_file_uuid", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "entity", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "column_name", type: "VARCHAR(255)")

            column(name: "code", type: "VARCHAR(255)")

            column(name: "message", type: "VARCHAR(1000)") {
                constraints(nullable: "false")
            }

            column(name: "created_date", type: "datetime") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-12") {
        createTable(tableName: "_raw_ext_inmigration") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_ext_inmigrationPK")
            }

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_name", type: "VARCHAR(255)")

            column(name: "member_gender", type: "VARCHAR(255)")

            column(name: "member_dob", type: "date")

            column(name: "member_mother_code", type: "VARCHAR(255)")

            column(name: "member_father_code", type: "VARCHAR(255)")

            column(name: "head_relationship_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "migration_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ext_migration_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "origin_code", type: "VARCHAR(255)")

            column(name: "origin_other", type: "VARCHAR(255)")

            column(name: "destination_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "migration_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "migration_reason", type: "VARCHAR(255)")

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-13") {
        createTable(tableName: "_raw_head_relationship") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_head_relationshipPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "relationship_type", type: "VARCHAR(255)") {
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

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-14") {
        createTable(tableName: "_raw_household") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_householdPK")
            }

            column(name: "region_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "region_name", type: "VARCHAR(255)")

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "household_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "head_code", type: "VARCHAR(255)")

            column(name: "head_name", type: "VARCHAR(255)")

            column(name: "gps_longitude", type: "VARCHAR(255)")

            column(name: "gps_altitude", type: "VARCHAR(255)")

            column(name: "gps_latitude", type: "VARCHAR(255)")

            column(name: "gps_accuracy", type: "VARCHAR(255)")

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-15") {
        createTable(tableName: "_raw_incomplete_visit") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_incomplete_visitPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)")

            column(name: "household_code", type: "VARCHAR(255)")

            column(name: "member_code", type: "VARCHAR(255)")

            column(name: "visit_reason", type: "VARCHAR(255)")

            column(name: "other_visit_reason", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-16") {
        createTable(tableName: "_raw_inmigration") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_inmigrationPK")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "head_relationship_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "migration_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ext_migration_type", type: "VARCHAR(255)")

            column(name: "origin_code", type: "VARCHAR(255)")

            column(name: "origin_other", type: "VARCHAR(255)")

            column(name: "destination_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "migration_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "migration_reason", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-17") {
        createTable(tableName: "_raw_marital_relationship") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_marital_relationshipPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "member_a_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_b_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "start_status", type: "VARCHAR(255)")

            column(name: "start_date", type: "date")

            column(name: "end_status", type: "VARCHAR(255)")

            column(name: "end_date", type: "date")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "uploaded_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-18") {
        createTable(tableName: "_raw_member_enu") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_member_enuPK")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "gender", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "dob", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "mother_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "mother_name", type: "VARCHAR(255)")

            column(name: "father_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "father_name", type: "VARCHAR(255)")

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "household_name", type: "VARCHAR(255)")

            column(name: "head_relationship_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "residency_start_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-19") {
        createTable(tableName: "_raw_outmigration") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_outmigrationPK")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "migration_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "origin_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "destination_code", type: "VARCHAR(255)")

            column(name: "destination_other", type: "VARCHAR(255)")

            column(name: "migration_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "migration_reason", type: "VARCHAR(255)")

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-20") {
        createTable(tableName: "_raw_pregnancy_child") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_pregnancy_childPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pregnancy_outcome_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "outcomde_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "child_code", type: "VARCHAR(255)")

            column(name: "child_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "child_gender", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "child_ordinal_pos", type: "INT")

            column(name: "head_relationship_type", type: "VARCHAR(255)")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-21") {
        createTable(tableName: "_raw_pregnancy_outcome") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_pregnancy_outcomePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "mother_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "father_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "number_of_outcomes", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "outcome_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "birthplace", type: "VARCHAR(255)")

            column(name: "birthplace_other", type: "VARCHAR(255)")

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "uploaded_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-22") {
        createTable(tableName: "_raw_pregnancy_registration") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_pregnancy_registrationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "mother_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "recorded_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "preg_months", type: "INT")

            column(name: "edd_known", type: "BOOLEAN")

            column(name: "has_precord", type: "BOOLEAN")

            column(name: "edd_date", type: "date")

            column(name: "edd_type", type: "VARCHAR(255)")

            column(name: "lmp_known", type: "BOOLEAN")

            column(name: "lmp_date", type: "date")

            column(name: "expected_delivery_date", type: "date")

            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "uploaded_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-23") {
        createTable(tableName: "_raw_region") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_regionPK")
            }

            column(name: "region_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "region_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "parent_code", type: "VARCHAR(255)")

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-24") {
        createTable(tableName: "_raw_residency") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_residencyPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "start_type", type: "VARCHAR(255)")

            column(name: "start_date", type: "date")

            column(name: "end_type", type: "VARCHAR(255)")

            column(name: "end_date", type: "date")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-25") {
        createTable(tableName: "_raw_visit") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_visitPK")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "visit_location", type: "VARCHAR(255)")

            column(name: "visit_location_other", type: "VARCHAR(255)")

            column(name: "visit_reason", type: "VARCHAR(255)")

            column(name: "round_number", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "respondent_code", type: "VARCHAR(255)")

            column(name: "has_interpreter", type: "BOOLEAN")

            column(name: "interpreter_name", type: "VARCHAR(255)")

            column(name: "non_visited_members", type: "VARCHAR(255)")

            column(name: "gps_accuracy", type: "VARCHAR(255)")

            column(name: "gps_altitude", type: "VARCHAR(255)")

            column(name: "gps_latitude", type: "VARCHAR(255)")

            column(name: "gps_longitude", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_date", type: "datetime")

            column(name: "uploaded_date", type: "datetime")

            column(name: "post_execution", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "processed", type: "INT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-26") {
        createTable(tableName: "_role") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_rolePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "authority", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-27") {
        createTable(tableName: "_security_map") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_security_mapPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "config_attribute", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "http_method", type: "VARCHAR(255)")

            column(name: "url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-28") {
        createTable(tableName: "_user") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_userPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "firstname", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "lastname", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "username", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "password", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "email", type: "VARCHAR(255)")

            column(name: "enabled", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "account_expired", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "account_locked", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "password_expired", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-29") {
        createTable(tableName: "_user_role") {
            column(name: "uuid", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_user_rolePK")
            }

            column(name: "user_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "role_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-30") {
        createTable(tableName: "core_form_extension") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "core_form_extensionPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "form_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "form_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ext_form_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collection_required", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "enabled", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "columns_mapping", type: "VARCHAR(500)")

            column(name: "core_form", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-31") {
        createTable(tableName: "dataset_label") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "dataset_labelPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "dataset_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "label", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-32") {
        createTable(tableName: "death") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "deathPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "member_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "death_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "age_at_death", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "death_cause", type: "VARCHAR(255)")

            column(name: "death_place", type: "VARCHAR(255)")

            column(name: "visit_id", type: "VARCHAR(32)")

            column(name: "visit_code", type: "VARCHAR(255)")

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-33") {
        createTable(tableName: "enumeration") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "enumerationPK")
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

            column(name: "household_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_reason", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-34") {
        createTable(tableName: "ext_dataset") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "ext_datasetPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)")

            column(name: "label", type: "VARCHAR(255)")

            column(name: "key_column", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "table_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "table_column", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "table_column_labels", type: "VARCHAR(2000)")

            column(name: "filename", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "enabled", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-35") {
        createTable(tableName: "form") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "formPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "form_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "form_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "form_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "region_level", type: "VARCHAR(255)")

            column(name: "gender", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "min_age", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "max_age", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "is_region", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "is_household", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "is_member", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "is_household_head", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "is_followup_only", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "multi_coll_per_session", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "redcap_api", type: "VARCHAR(32)")

            column(name: "enabled", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "modules", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "dependencies", type: "VARCHAR(255)")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-36") {
        createTable(tableName: "form_group") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "form_groupPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "VARCHAR(255)")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-37") {
        createTable(tableName: "form_mapping") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "form_mappingPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "form_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "form_variable_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "form_repeat_group", type: "VARCHAR(255)")

            column(name: "table_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "column_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "column_format_type", type: "VARCHAR(32)")

            column(name: "column_format_value", type: "VARCHAR(255)")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-38") {
        createTable(tableName: "form_mapping_format") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "form_mapping_formatPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "format", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-39") {
        createTable(tableName: "head_relationship") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "head_relationshipPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "household_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "head_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "head_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "relationship_type", type: "VARCHAR(255)") {
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

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-40") {
        createTable(tableName: "household") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "householdPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "region", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "head_code", type: "VARCHAR(255)")

            column(name: "head_name", type: "VARCHAR(255)")

            column(name: "sec_head_code", type: "VARCHAR(255)")

            column(name: "region_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "head_id", type: "VARCHAR(32)")

            column(name: "hierarchy1", type: "VARCHAR(255)")

            column(name: "hierarchy2", type: "VARCHAR(255)")

            column(name: "hierarchy3", type: "VARCHAR(255)")

            column(name: "hierarchy4", type: "VARCHAR(255)")

            column(name: "hierarchy5", type: "VARCHAR(255)")

            column(name: "hierarchy6", type: "VARCHAR(255)")

            column(name: "hierarchy7", type: "VARCHAR(255)")

            column(name: "hierarchy8", type: "VARCHAR(255)")

            column(name: "gps_is_null", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "gps_accuracy", type: "DOUBLE precision")

            column(name: "gps_altitude", type: "DOUBLE precision")

            column(name: "gps_latitude", type: "DOUBLE precision")

            column(name: "gps_longitude", type: "DOUBLE precision")

            column(name: "cos_latitude", type: "DOUBLE precision")

            column(name: "sin_latitude", type: "DOUBLE precision")

            column(name: "cos_longitude", type: "DOUBLE precision")

            column(name: "sin_longitude", type: "DOUBLE precision")

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-41") {
        createTable(tableName: "in_migration") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "in_migrationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "member_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ext_migtype", type: "VARCHAR(255)")

            column(name: "origin_id", type: "VARCHAR(32)")

            column(name: "origin_code", type: "VARCHAR(255)")

            column(name: "origin_other", type: "VARCHAR(255)")

            column(name: "destination_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "destination_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "destination_residency", type: "VARCHAR(32)")

            column(name: "migration_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "migration_reason", type: "VARCHAR(255)")

            column(name: "visit_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-42") {
        createTable(tableName: "incomplete_visit") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "incomplete_visitPK")
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

            column(name: "member_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_reason", type: "VARCHAR(255)")

            column(name: "other_visit_reason", type: "VARCHAR(255)")

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-43") {
        createTable(tableName: "marital_relationship") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "marital_relationshipPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "member_a_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_b_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_a_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_b_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "start_status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "start_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "end_status", type: "VARCHAR(255)")

            column(name: "end_date", type: "date")

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-44") {
        createTable(tableName: "member") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "memberPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "gender", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "dob", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "age_at_death", type: "INT")

            column(name: "marital_status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "spouse_id", type: "VARCHAR(32)")

            column(name: "spouse_code", type: "VARCHAR(255)")

            column(name: "spouse_name", type: "VARCHAR(255)")

            column(name: "mother_id", type: "VARCHAR(32)")

            column(name: "mother_code", type: "VARCHAR(255)")

            column(name: "mother_name", type: "VARCHAR(255)")

            column(name: "father_id", type: "VARCHAR(32)")

            column(name: "father_code", type: "VARCHAR(255)")

            column(name: "father_name", type: "VARCHAR(255)")

            column(name: "household_id", type: "VARCHAR(32)")

            column(name: "household_code", type: "VARCHAR(255)")

            column(name: "household_name", type: "VARCHAR(255)")

            column(name: "entry_household", type: "VARCHAR(255)")

            column(name: "entry_type", type: "VARCHAR(255)")

            column(name: "entry_date", type: "date")

            column(name: "start_type", type: "VARCHAR(255)")

            column(name: "start_date", type: "date")

            column(name: "end_type", type: "VARCHAR(255)")

            column(name: "end_date", type: "date")

            column(name: "head_relationship_type", type: "VARCHAR(255)")

            column(name: "gps_is_null", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "gps_accuracy", type: "DOUBLE precision")

            column(name: "gps_altitude", type: "DOUBLE precision")

            column(name: "gps_latitude", type: "DOUBLE precision")

            column(name: "gps_longitude", type: "DOUBLE precision")

            column(name: "cos_latitude", type: "DOUBLE precision")

            column(name: "sin_latitude", type: "DOUBLE precision")

            column(name: "cos_longitude", type: "DOUBLE precision")

            column(name: "sin_longitude", type: "DOUBLE precision")

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-45") {
        createTable(tableName: "module") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "modulePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-46") {
        createTable(tableName: "out_migration") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "out_migrationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "member_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "origin_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "origin_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "origin_residency", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "destination_id", type: "VARCHAR(32)")

            column(name: "destination_code", type: "VARCHAR(255)")

            column(name: "destination_other", type: "VARCHAR(255)")

            column(name: "migration_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "migration_reason", type: "VARCHAR(255)")

            column(name: "visit_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-47") {
        createTable(tableName: "pregnancy_child") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_childPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pregnancy_outcome_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "pregnancy_outcome_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "outcomde_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "child_id", type: "VARCHAR(32)")

            column(name: "child_code", type: "VARCHAR(255)")

            column(name: "child_ordinal_pos", type: "INT")

            column(name: "child_head_relationship_id", type: "VARCHAR(32)")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-48") {
        createTable(tableName: "pregnancy_outcome") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_outcomePK")
            }

            column(name: "version", type: "BIGINT") {
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

            column(name: "father_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "father_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "number_of_outcomes", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "number_of_livebirths", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "outcome_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "birthplace", type: "VARCHAR(255)")

            column(name: "birthplace_other", type: "VARCHAR(255)")

            column(name: "visit_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-49") {
        createTable(tableName: "pregnancy_registration") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "pregnancy_registrationPK")
            }

            column(name: "version", type: "BIGINT") {
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

            column(name: "recorded_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "preg_months", type: "INT")

            column(name: "edd_known", type: "BOOLEAN")

            column(name: "has_precord", type: "BOOLEAN")

            column(name: "edd_date", type: "date")

            column(name: "edd_type", type: "VARCHAR(255)")

            column(name: "lmp_known", type: "BOOLEAN")

            column(name: "lmp_date", type: "date")

            column(name: "expected_delivery_date", type: "date")

            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "visit_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-50") {
        createTable(tableName: "redcap_api") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "redcap_apiPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "token", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-51") {
        createTable(tableName: "redcap_mapping") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "redcap_mappingPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "form_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "odk_col_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "redcap_col_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "redcap_col_format", type: "VARCHAR(32)")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-52") {
        createTable(tableName: "region") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "regionPK")
            }

            column(name: "code", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "level", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "parent_region_code", type: "VARCHAR(32)")

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-53") {
        createTable(tableName: "residency") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "residencyPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "household_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "member_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "member_code", type: "VARCHAR(255)") {
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

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-54") {
        createTable(tableName: "round") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "roundPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "round_number", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "start_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "end_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "VARCHAR(255)")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-55") {
        createTable(tableName: "sync_report") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "sync_reportPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(defaultValueNumeric: "0", name: "records", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "sync_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-56") {
        createTable(tableName: "tracking_list") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tracking_listPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "filename", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "has_extra_data", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "enabled", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "modules", type: "VARCHAR(255)")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-57") {
        createTable(tableName: "tracking_list_mapping") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tracking_list_mappingPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "trackinglist_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "label", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-58") {
        createTable(tableName: "visit") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "visitPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "household_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "visit_date", type: "date") {
                constraints(nullable: "false")
            }

            column(name: "visit_location", type: "VARCHAR(255)")

            column(name: "visit_location_other", type: "VARCHAR(255)")

            column(name: "visit_reason", type: "VARCHAR(255)")

            column(name: "round_number", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "respondent_id", type: "VARCHAR(32)")

            column(name: "respondent_code", type: "VARCHAR(255)")

            column(name: "has_interpreter", type: "BOOLEAN")

            column(name: "interpreter_name", type: "VARCHAR(255)")

            column(name: "non_visited_members", type: "VARCHAR(255)")

            column(name: "gps_accuracy", type: "DOUBLE precision")

            column(name: "gps_altitude", type: "DOUBLE precision")

            column(name: "gps_latitude", type: "DOUBLE precision")

            column(name: "gps_longitude", type: "DOUBLE precision")

            column(name: "collected_uuid", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(32)")

            column(name: "collected_date", type: "datetime")

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-59") {
        addUniqueConstraint(columnNames: "form_id", constraintName: "UC_CORE_FORM_EXTENSIONFORM_ID_COL", tableName: "core_form_extension")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-60") {
        addUniqueConstraint(columnNames: "member_code", constraintName: "UC_ENUMERATIONMEMBER_CODE_COL", tableName: "enumeration")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-61") {
        addUniqueConstraint(columnNames: "member_id", constraintName: "UC_ENUMERATIONMEMBER_ID_COL", tableName: "enumeration")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-62") {
        addUniqueConstraint(columnNames: "label", constraintName: "UC_EXT_DATASETLABEL_COL", tableName: "ext_dataset")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-63") {
        addUniqueConstraint(columnNames: "name", constraintName: "UC_EXT_DATASETNAME_COL", tableName: "ext_dataset")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-64") {
        addUniqueConstraint(columnNames: "form_id", constraintName: "UC_FORMFORM_ID_COL", tableName: "form")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-65") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_FORM_GROUPCODE_COL", tableName: "form_group")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-66") {
        addUniqueConstraint(columnNames: "format", constraintName: "UC_FORM_MAPPING_FORMATFORMAT_COL", tableName: "form_mapping_format")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-67") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_HOUSEHOLDCODE_COL", tableName: "household")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-68") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_MEMBERCODE_COL", tableName: "member")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-69") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_MODULECODE_COL", tableName: "module")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-70") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_PREGNANCY_OUTCOMECODE_COL", tableName: "pregnancy_outcome")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-71") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_PREGNANCY_REGISTRATIONCODE_COL", tableName: "pregnancy_registration")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-72") {
        addUniqueConstraint(columnNames: "name", constraintName: "UC_REDCAP_APINAME_COL", tableName: "redcap_api")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-73") {
        addUniqueConstraint(columnNames: "token", constraintName: "UC_REDCAP_APITOKEN_COL", tableName: "redcap_api")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-74") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_REGIONCODE_COL", tableName: "region")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-75") {
        addUniqueConstraint(columnNames: "round_number", constraintName: "UC_ROUNDROUND_NUMBER_COL", tableName: "round")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-76") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_SYNC_REPORTCODE_COL", tableName: "sync_report")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-77") {
        addUniqueConstraint(columnNames: "name", constraintName: "UC_SYNC_REPORTNAME_COL", tableName: "sync_report")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-78") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_TRACKING_LISTCODE_COL", tableName: "tracking_list")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-79") {
        addUniqueConstraint(columnNames: "filename", constraintName: "UC_TRACKING_LISTFILENAME_COL", tableName: "tracking_list")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-80") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC_VISITCODE_COL", tableName: "visit")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-81") {
        addUniqueConstraint(columnNames: "name", constraintName: "UC__APPLICATION_PARAMNAME_COL", tableName: "_application_param")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-82") {
        addUniqueConstraint(columnNames: "group_id", constraintName: "UC__LOG_GROUPGROUP_ID_COL", tableName: "_log_group")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-83") {
        addUniqueConstraint(columnNames: "name", constraintName: "UC__LOG_GROUPNAME_COL", tableName: "_log_group")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-84") {
        addUniqueConstraint(columnNames: "report_id", constraintName: "UC__LOG_REPORTREPORT_ID_COL", tableName: "_log_report")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-85") {
        addUniqueConstraint(columnNames: "uuid", constraintName: "UC__RAW_ERROR_LOGUUID_COL", tableName: "_raw_error_log")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-86") {
        addUniqueConstraint(columnNames: "household_code", constraintName: "UC__RAW_HOUSEHOLDHOUSEHOLD_CODE_COL", tableName: "_raw_household")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-87") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC__RAW_MEMBER_ENUCODE_COL", tableName: "_raw_member_enu")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-88") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC__RAW_PREGNANCY_OUTCOMECODE_COL", tableName: "_raw_pregnancy_outcome")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-89") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC__RAW_PREGNANCY_REGISTRATIONCODE_COL", tableName: "_raw_pregnancy_registration")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-90") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC__RAW_VISITCODE_COL", tableName: "_raw_visit")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-91") {
        addUniqueConstraint(columnNames: "authority", constraintName: "UC__ROLEAUTHORITY_COL", tableName: "_role")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-92") {
        addUniqueConstraint(columnNames: "code", constraintName: "UC__USERCODE_COL", tableName: "_user")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-93") {
        addUniqueConstraint(columnNames: "email", constraintName: "UC__USEREMAIL_COL", tableName: "_user")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-94") {
        addUniqueConstraint(columnNames: "username", constraintName: "UC__USERUSERNAME_COL", tableName: "_user")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-95") {
        addUniqueConstraint(columnNames: "form_id, odk_col_name", constraintName: "UK134832422b2162e87e59006f3c07", tableName: "redcap_mapping")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-96") {
        addUniqueConstraint(columnNames: "http_method, url", constraintName: "UK8ab8d98ecade2ffcf71693034f6a", tableName: "_security_map")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-97") {
        addUniqueConstraint(columnNames: "trackinglist_id, name", constraintName: "UK920cc4b3b31885a1070998c3388c", tableName: "tracking_list_mapping")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-98") {
        addUniqueConstraint(columnNames: "dataset_id, name", constraintName: "UKbbb10f1034b1692bd2b10566323e", tableName: "dataset_label")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-99") {
        addUniqueConstraint(columnNames: "code, head_code", constraintName: "UKe0e652078b1bac3639a717cba5a2", tableName: "household")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-100") {
        addUniqueConstraint(columnNames: "form_id, form_variable_name", constraintName: "UKfc77a6af0c68f3b310d934daf45d", tableName: "form_mapping")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-101") {
        createIndex(indexName: "idx_child_code", tableName: "_raw_pregnancy_child") {
            column(name: "child_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-102") {
        createIndex(indexName: "idx_child_code", tableName: "pregnancy_child") {
            column(name: "child_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-103") {
        createIndex(indexName: "idx_childs", tableName: "__raw_event") {
            column(name: "child_codes")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-104") {
        createIndex(indexName: "idx_code", tableName: "__raw_event") {
            column(name: "event_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-105") {
        createIndex(indexName: "idx_code", tableName: "_raw_error_log") {
            column(name: "code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-106") {
        createIndex(indexName: "idx_coll_by", tableName: "_raw_change_head") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-107") {
        createIndex(indexName: "idx_collby", tableName: "_raw_death") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-108") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_ext_inmigration") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-109") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_household") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-110") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_incomplete_visit") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-111") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_inmigration") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-112") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_marital_relationship") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-113") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_member_enu") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-114") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_outmigration") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-115") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_pregnancy_outcome") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-116") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_pregnancy_registration") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-117") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_region") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-118") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_visit") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-119") {
        createIndex(indexName: "idx_destination_code", tableName: "_raw_ext_inmigration") {
            column(name: "destination_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-120") {
        createIndex(indexName: "idx_destination_code", tableName: "_raw_inmigration") {
            column(name: "destination_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-121") {
        createIndex(indexName: "idx_destination_code", tableName: "_raw_outmigration") {
            column(name: "destination_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-122") {
        createIndex(indexName: "idx_destination_code", tableName: "in_migration") {
            column(name: "destination_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-123") {
        createIndex(indexName: "idx_destination_code", tableName: "out_migration") {
            column(name: "destination_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-124") {
        createIndex(indexName: "idx_entity", tableName: "_raw_error_log") {
            column(name: "entity")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-125") {
        createIndex(indexName: "idx_father_code", tableName: "_raw_member_enu") {
            column(name: "father_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-126") {
        createIndex(indexName: "idx_father_code", tableName: "_raw_pregnancy_outcome") {
            column(name: "father_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-127") {
        createIndex(indexName: "idx_father_code", tableName: "member") {
            column(name: "father_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-128") {
        createIndex(indexName: "idx_father_code", tableName: "pregnancy_outcome") {
            column(name: "father_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-129") {
        createIndex(indexName: "idx_group_id", tableName: "_log_report") {
            column(name: "group_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-130") {
        createIndex(indexName: "idx_head_code", tableName: "_raw_household") {
            column(name: "head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-131") {
        createIndex(indexName: "idx_head_code", tableName: "head_relationship") {
            column(name: "head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-132") {
        createIndex(indexName: "idx_household_code", tableName: "_raw_change_head") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-133") {
        createIndex(indexName: "idx_household_code", tableName: "_raw_head_relationship") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-134") {
        createIndex(indexName: "idx_household_code", tableName: "_raw_incomplete_visit") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-135") {
        createIndex(indexName: "idx_household_code", tableName: "_raw_member_enu") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-136") {
        createIndex(indexName: "idx_household_code", tableName: "_raw_residency") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-137") {
        createIndex(indexName: "idx_household_code", tableName: "_raw_visit") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-138") {
        createIndex(indexName: "idx_household_code", tableName: "head_relationship") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-139") {
        createIndex(indexName: "idx_household_code", tableName: "member") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-140") {
        createIndex(indexName: "idx_household_code", tableName: "residency") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-141") {
        createIndex(indexName: "idx_household_code", tableName: "visit") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-142") {
        createIndex(indexName: "idx_id", tableName: "__raw_event") {
            column(name: "event_uuid")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-143") {
        createIndex(indexName: "idx_key_column", tableName: "ext_dataset") {
            column(name: "key_column")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-144") {
        createIndex(indexName: "idx_key_timestamp", tableName: "_log_report") {
            column(name: "key_timestamp")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-145") {
        createIndex(indexName: "idx_key_timestamp", tableName: "_log_report_file") {
            column(name: "key_timestamp")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-146") {
        createIndex(indexName: "idx_kydate", tableName: "__raw_event") {
            column(name: "key_date")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-147") {
        createIndex(indexName: "idx_level", tableName: "region") {
            column(name: "level")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-148") {
        createIndex(indexName: "idx_member_a_code", tableName: "_raw_marital_relationship") {
            column(name: "member_a_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-149") {
        createIndex(indexName: "idx_member_a_code", tableName: "marital_relationship") {
            column(name: "member_a_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-150") {
        createIndex(indexName: "idx_member_b_code", tableName: "_raw_marital_relationship") {
            column(name: "member_b_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-151") {
        createIndex(indexName: "idx_member_b_code", tableName: "marital_relationship") {
            column(name: "member_b_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-152") {
        createIndex(indexName: "idx_member_code", tableName: "_raw_death") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-153") {
        createIndex(indexName: "idx_member_code", tableName: "_raw_ext_inmigration") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-154") {
        createIndex(indexName: "idx_member_code", tableName: "_raw_head_relationship") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-155") {
        createIndex(indexName: "idx_member_code", tableName: "_raw_incomplete_visit") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-156") {
        createIndex(indexName: "idx_member_code", tableName: "_raw_inmigration") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-157") {
        createIndex(indexName: "idx_member_code", tableName: "_raw_outmigration") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-158") {
        createIndex(indexName: "idx_member_code", tableName: "_raw_residency") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-159") {
        createIndex(indexName: "idx_member_code", tableName: "death") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-160") {
        createIndex(indexName: "idx_member_code", tableName: "head_relationship") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-161") {
        createIndex(indexName: "idx_member_code", tableName: "in_migration") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-162") {
        createIndex(indexName: "idx_member_code", tableName: "incomplete_visit") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-163") {
        createIndex(indexName: "idx_member_code", tableName: "out_migration") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-164") {
        createIndex(indexName: "idx_member_code", tableName: "residency") {
            column(name: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-165") {
        createIndex(indexName: "idx_member_father_code", tableName: "_raw_ext_inmigration") {
            column(name: "member_father_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-166") {
        createIndex(indexName: "idx_member_mother_code", tableName: "_raw_ext_inmigration") {
            column(name: "member_mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-167") {
        createIndex(indexName: "idx_modules", tableName: "form") {
            column(name: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-168") {
        createIndex(indexName: "idx_modules", tableName: "household") {
            column(name: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-169") {
        createIndex(indexName: "idx_modules", tableName: "member") {
            column(name: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-170") {
        createIndex(indexName: "idx_modules", tableName: "region") {
            column(name: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-171") {
        createIndex(indexName: "idx_modules", tableName: "tracking_list") {
            column(name: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-172") {
        createIndex(indexName: "idx_mother_code", tableName: "_raw_member_enu") {
            column(name: "mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-173") {
        createIndex(indexName: "idx_mother_code", tableName: "_raw_pregnancy_outcome") {
            column(name: "mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-174") {
        createIndex(indexName: "idx_mother_code", tableName: "_raw_pregnancy_registration") {
            column(name: "mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-175") {
        createIndex(indexName: "idx_mother_code", tableName: "member") {
            column(name: "mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-176") {
        createIndex(indexName: "idx_mother_code", tableName: "pregnancy_outcome") {
            column(name: "mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-177") {
        createIndex(indexName: "idx_mother_code", tableName: "pregnancy_registration") {
            column(name: "mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-178") {
        createIndex(indexName: "idx_name", tableName: "_role") {
            column(name: "name")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-179") {
        createIndex(indexName: "idx_new_headcode", tableName: "_raw_change_head") {
            column(name: "new_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-180") {
        createIndex(indexName: "idx_new_member_code", tableName: "_raw_death_relationships") {
            column(name: "new_member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-181") {
        createIndex(indexName: "idx_new_membercode", tableName: "_raw_change_head_relationship") {
            column(name: "new_member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-182") {
        createIndex(indexName: "idx_nonv", tableName: "_raw_visit") {
            column(name: "non_visited_members")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-183") {
        createIndex(indexName: "idx_nonv", tableName: "visit") {
            column(name: "non_visited_members")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-184") {
        createIndex(indexName: "idx_old_headcode", tableName: "_raw_change_head") {
            column(name: "old_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-185") {
        createIndex(indexName: "idx_origin_code", tableName: "_raw_ext_inmigration") {
            column(name: "origin_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-186") {
        createIndex(indexName: "idx_origin_code", tableName: "_raw_inmigration") {
            column(name: "origin_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-187") {
        createIndex(indexName: "idx_origin_code", tableName: "_raw_outmigration") {
            column(name: "origin_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-188") {
        createIndex(indexName: "idx_origin_code", tableName: "in_migration") {
            column(name: "origin_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-189") {
        createIndex(indexName: "idx_origin_code", tableName: "out_migration") {
            column(name: "origin_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-190") {
        createIndex(indexName: "idx_outcome_code", tableName: "pregnancy_child") {
            column(name: "pregnancy_outcome_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-191") {
        createIndex(indexName: "idx_parent_code", tableName: "_raw_region") {
            column(name: "parent_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-192") {
        createIndex(indexName: "idx_region_code", tableName: "_raw_household") {
            column(name: "region_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-193") {
        createIndex(indexName: "idx_region_code", tableName: "_raw_region") {
            column(name: "region_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-194") {
        createIndex(indexName: "idx_respondent_code", tableName: "_raw_visit") {
            column(name: "respondent_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-195") {
        createIndex(indexName: "idx_respondent_code", tableName: "visit") {
            column(name: "respondent_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-196") {
        createIndex(indexName: "idx_round_number", tableName: "visit") {
            column(name: "round_number")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-197") {
        createIndex(indexName: "idx_sec_head_code", tableName: "household") {
            column(name: "sec_head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-198") {
        createIndex(indexName: "idx_spouse_code", tableName: "member") {
            column(name: "spouse_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-199") {
        createIndex(indexName: "idx_type", tableName: "__raw_event") {
            column(name: "event_type")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-200") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_change_head") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-201") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_death") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-202") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_ext_inmigration") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-203") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_incomplete_visit") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-204") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_inmigration") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-205") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_member_enu") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-206") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_outmigration") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-207") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_pregnancy_outcome") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-208") {
        createIndex(indexName: "idx_visit_code", tableName: "_raw_pregnancy_registration") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-209") {
        createIndex(indexName: "idx_visit_code", tableName: "death") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-210") {
        createIndex(indexName: "idx_visit_code", tableName: "enumeration") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-211") {
        createIndex(indexName: "idx_visit_code", tableName: "in_migration") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-212") {
        createIndex(indexName: "idx_visit_code", tableName: "incomplete_visit") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-213") {
        createIndex(indexName: "idx_visit_code", tableName: "out_migration") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-214") {
        createIndex(indexName: "idx_visit_code", tableName: "pregnancy_outcome") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-215") {
        createIndex(indexName: "idx_visit_code", tableName: "pregnancy_registration") {
            column(name: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1652717035723-216") {
        addForeignKeyConstraint(baseColumnNames: "head_id", baseTableName: "household", constraintName: "FK17k1dregfyt5ljfql7uj0q2ec", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-217") {
        addForeignKeyConstraint(baseColumnNames: "parent_region_code", baseTableName: "region", constraintName: "FK1c2dnsuws0v5incjjfe1w9o8r", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "region", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-218") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "pregnancy_registration", constraintName: "FK1c5ru4ffdu0545q1u2pahw5hs", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-219") {
        addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "member", constraintName: "FK1hmt0vnyraefeh3qj8mlqr3bo", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-220") {
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "death", constraintName: "FK2273ehsjc2ylltg26gclawy3r", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-221") {
        addForeignKeyConstraint(baseColumnNames: "log_report_file_uuid", baseTableName: "_raw_error_log", constraintName: "FK2cpn9wgoxc0ar50kilj0e17ms", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_log_report_file", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-222") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "out_migration", constraintName: "FK2d6823lfqcmw6fi7fa9x3jjku", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-223") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "in_migration", constraintName: "FK2t4odwof20vbnkvgrmw0owchs", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-224") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "region", constraintName: "FK37g9yoccmhfkkekw4b5oyf9y4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-225") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "round", constraintName: "FK3epdk3uyre6rnuo3t81oyb8pl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-226") {
        addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "enumeration", constraintName: "FK3pa9e0heul1vdeuxstpdqryu9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-227") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "tracking_list", constraintName: "FK41xryn1j90x8gvdrtql6gatj8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-228") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "tracking_list", constraintName: "FK479olpybkyfov5d2dcolf2fkh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-229") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "out_migration", constraintName: "FK4kehrpfyl1rdmdoac6l6mpgrs", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-230") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "_user", constraintName: "FK4qbg83cirguwh8rkuyrhdb0af", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-231") {
        addForeignKeyConstraint(baseColumnNames: "member_id", baseTableName: "residency", constraintName: "FK5c7m16eeb2ahaxt8bh9dvg6d6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-232") {
        addForeignKeyConstraint(baseColumnNames: "mother_id", baseTableName: "member", constraintName: "FK5lgwtt6tvfbkxg4o4bi06ogdx", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-233") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "redcap_api", constraintName: "FK5qejbhd7veuna9omokq5n0ed4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-234") {
        addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "_notification", constraintName: "FK5r37di0n5j56yhx09ii1siw73", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-235") {
        addForeignKeyConstraint(baseColumnNames: "destination_residency", baseTableName: "in_migration", constraintName: "FK6e33nic5kr5rol26earfnrq34", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "residency", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-236") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "form", constraintName: "FK6yj4x2tfa0kkvy5a0amu58gyo", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-237") {
        addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "head_relationship", constraintName: "FK7183h2v047wounhh28mrbpn8e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-238") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "_application_param", constraintName: "FK7ghey7p7aqkiangus73fpxu2b", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-239") {
        addForeignKeyConstraint(baseColumnNames: "child_head_relationship_id", baseTableName: "pregnancy_child", constraintName: "FK7hfyc551ts5m9auy2gqnlrjal", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "head_relationship", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-240") {
        addForeignKeyConstraint(baseColumnNames: "member_id", baseTableName: "in_migration", constraintName: "FK7isesi62oi6gbw3qs2v8v7lc5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-241") {
        addForeignKeyConstraint(baseColumnNames: "pregnancy_outcome_id", baseTableName: "_raw_pregnancy_child", constraintName: "FK7jr8csenqf151phdijsciigh3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_raw_pregnancy_outcome", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-242") {
        addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "residency", constraintName: "FK8c94im6g1vhtu6h2isti1wp3g", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-243") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "form_group", constraintName: "FK8f3592kw42oddxnjosh370bs6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-244") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "marital_relationship", constraintName: "FK8fyri5bame2ql27b6pn00fkfg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-245") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "visit", constraintName: "FK8w4x3259fksde4sukdbam1wi7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-246") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "pregnancy_outcome", constraintName: "FK8x4j24n36d5wojclone1yvxa8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-247") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "enumeration", constraintName: "FK915s0o10crhxwcyji6uhl5nbc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-248") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "death", constraintName: "FK983i3hb6jyo2cay1h72epb5ok", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-249") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "form_group", constraintName: "FK99879dr5cwo335eeaqec2cqut", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-250") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "member", constraintName: "FKa84k7k3ckkoydq6poun17lk9a", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-251") {
        addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "out_migration", constraintName: "FKaruwkikesa0fkoldgom3wyd3w", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-252") {
        addForeignKeyConstraint(baseColumnNames: "destination_id", baseTableName: "in_migration", constraintName: "FKavn2ls44nk7o1kfprlxy3pu0l", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-253") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "head_relationship", constraintName: "FKb38skrcrf2ymvk45cl254id1n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-254") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "incomplete_visit", constraintName: "FKbek7huj5jhh5p5wmbqxp2aquy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-255") {
        addForeignKeyConstraint(baseColumnNames: "origin_residency", baseTableName: "out_migration", constraintName: "FKbmm7vwxohrmy1sjpvmocg6jpj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "residency", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-256") {
        addForeignKeyConstraint(baseColumnNames: "group_id", baseTableName: "_log_report", constraintName: "FKckcp7wve12fyi5fvdec9kw9k7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_log_group", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-257") {
        addForeignKeyConstraint(baseColumnNames: "mother_id", baseTableName: "pregnancy_registration", constraintName: "FKcr563n8hvr5i7udwdsdtltxng", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-258") {
        addForeignKeyConstraint(baseColumnNames: "origin_id", baseTableName: "out_migration", constraintName: "FKd3u6e0tep5df992kf8gkw8jgv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-259") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "death", constraintName: "FKdff2lqkbvikagg6pra5kv9njv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-260") {
        addForeignKeyConstraint(baseColumnNames: "form_id", baseTableName: "form_mapping", constraintName: "FKdkq8jskabhct19hd1yt8fqw6j", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "form", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-261") {
        addForeignKeyConstraint(baseColumnNames: "member_b_id", baseTableName: "marital_relationship", constraintName: "FKdrnhqv864addlaoqlbft5wlye", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-262") {
        addForeignKeyConstraint(baseColumnNames: "father_id", baseTableName: "pregnancy_outcome", constraintName: "FKdt55v8umiunw7q8m1l7gjdlkw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-263") {
        addForeignKeyConstraint(baseColumnNames: "change_head_id", baseTableName: "_raw_change_head_relationship", constraintName: "FKdx30habqimclay6biyjtjpdfc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_raw_change_head", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-264") {
        addForeignKeyConstraint(baseColumnNames: "pregnancy_outcome_id", baseTableName: "pregnancy_child", constraintName: "FKdx5o2dya0k75op54mkkyygw81", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "pregnancy_outcome", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-265") {
        addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "_user_role", constraintName: "FKdx8tn9uhur0t4yx3awxc99q1k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_role", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-266") {
        addForeignKeyConstraint(baseColumnNames: "trackinglist_id", baseTableName: "tracking_list_mapping", constraintName: "FKe1iophruxw6tjym8x9ysobke0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tracking_list", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-267") {
        addForeignKeyConstraint(baseColumnNames: "spouse_id", baseTableName: "member", constraintName: "FKe3awkwouqu4ejmeax4p0cjmeh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-268") {
        addForeignKeyConstraint(baseColumnNames: "household_id", baseTableName: "visit", constraintName: "FKelqj4l3nlee86jfdft2y01qv0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-269") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "visit", constraintName: "FKely7s9akst1v5in5r70nsdfdf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-270") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "household", constraintName: "FKf5c2xh0db1yblt9i4spuqer1r", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-271") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "enumeration", constraintName: "FKf95t6ryhva6gtjsrxsou1p9lw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-272") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "redcap_api", constraintName: "FKfav2gql3jpctd7n6gtxb4arec", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-273") {
        addForeignKeyConstraint(baseColumnNames: "father_id", baseTableName: "member", constraintName: "FKfg27iyknppsvrr1njilm6mrl3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-274") {
        addForeignKeyConstraint(baseColumnNames: "member_id", baseTableName: "death", constraintName: "FKfmdgpkb1fjny4sdvl8jb25888", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-275") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "visit", constraintName: "FKftvptny0521qpij2ui7uig9uj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-276") {
        addForeignKeyConstraint(baseColumnNames: "respondent_id", baseTableName: "visit", constraintName: "FKfwqg3xwnuvrpviqqbmmobr52d", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-277") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "out_migration", constraintName: "FKfwvuon2956ufxgdx57t6mh7uc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-278") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "head_relationship", constraintName: "FKg2swi5fgcei2r0ywoliwt9n4n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-279") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "incomplete_visit", constraintName: "FKg9pow9grnvxg1t2wy6qn7mjih", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-280") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "region", constraintName: "FKgewi7hf97wy0412c8umds2fxx", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-281") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "ext_dataset", constraintName: "FKh5e30wuysk2o6cao3kg6qwrm7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-282") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "death", constraintName: "FKhdxpeqx5w8kp700nh70yuhgd3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-283") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "_application_param", constraintName: "FKhkgij5i87cisbwxpe5lly65i7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-284") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "pregnancy_registration", constraintName: "FKhlkx2lf93bp8n0877osmjsp28", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-285") {
        addForeignKeyConstraint(baseColumnNames: "mother_id", baseTableName: "pregnancy_outcome", constraintName: "FKhmq3cgkm7tek348cm2oawi3on", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-286") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "form", constraintName: "FKhnxl1lpanjhqohx7sro87l71q", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-287") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "member", constraintName: "FKhs1oe1k0lgtoob9swtddq7bqc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-288") {
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "pregnancy_outcome", constraintName: "FKi853tkruo2snbk3rqf94ksj5y", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-289") {
        addForeignKeyConstraint(baseColumnNames: "death_id", baseTableName: "_raw_death_relationships", constraintName: "FKi97fxl3bh0igtewtwamfhrw40", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_raw_death", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-290") {
        addForeignKeyConstraint(baseColumnNames: "member_id", baseTableName: "head_relationship", constraintName: "FKik8ele4wedf3oct3q0tbbaqq8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-291") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "enumeration", constraintName: "FKj2uk4lnj8bgusgl5d4mh7sj1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-292") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "household", constraintName: "FKj3mm51fif0bqjtyuyc0l2ne7v", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-293") {
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "pregnancy_registration", constraintName: "FKj9mw06qhqw2774cjb85o24oju", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-294") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "residency", constraintName: "FKjktl3evu7pqt90r8h220x6n9f", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-295") {
        addForeignKeyConstraint(baseColumnNames: "log_report_id", baseTableName: "_log_report_file", constraintName: "FKjr5opoikde2ld40w68d37gy87", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_log_report", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-296") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "round", constraintName: "FKjvqx9ocet4eucjwnae2acujgr", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-297") {
        addForeignKeyConstraint(baseColumnNames: "member_a_id", baseTableName: "marital_relationship", constraintName: "FKk09g0noyedxf0y30swl4kweam", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-298") {
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "in_migration", constraintName: "FKkxr24a54yyf3wn9tt7w8w5pkl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-299") {
        addForeignKeyConstraint(baseColumnNames: "dataset_id", baseTableName: "dataset_label", constraintName: "FKl46kn3aussfmdngjkfe02i3r4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "ext_dataset", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-300") {
        addForeignKeyConstraint(baseColumnNames: "region_id", baseTableName: "household", constraintName: "FKl47l7ytswq6xan69l1yc0jux", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "region", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-301") {
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "enumeration", constraintName: "FKl5tv0irqwlc8onptopf8k8trx", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-302") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "region", constraintName: "FKla2ftx51lhp2juv4wayg6h1qs", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-303") {
        addForeignKeyConstraint(baseColumnNames: "origin_id", baseTableName: "in_migration", constraintName: "FKlighel59xkyit160p70odjor5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "household", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-304") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "marital_relationship", constraintName: "FKliqjmrl9pyjiao8srd19lv7y6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-305") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "member", constraintName: "FKmlglbsmxidvtt9p1aokhngfdt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-306") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "in_migration", constraintName: "FKmmd3y7umslslru6la34sonwu2", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-307") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "marital_relationship", constraintName: "FKmxwmlep4lvdqnvp2tcxi2aaw5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-308") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "household", constraintName: "FKmxxmj51qugwepd3j5pmn6pe60", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-309") {
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "incomplete_visit", constraintName: "FKnehtrn9vrcvj8ais67xdm1onm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-310") {
        addForeignKeyConstraint(baseColumnNames: "redcap_col_format", baseTableName: "redcap_mapping", constraintName: "FKnexgbuxj08754e9j2p0dq5v0q", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "form_mapping_format", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-311") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "pregnancy_registration", constraintName: "FKnqnsob2su90orb3bcbll3w0nb", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-312") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "in_migration", constraintName: "FKnyu246xuesjkdfgr61clb4df6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-313") {
        addForeignKeyConstraint(baseColumnNames: "column_format_type", baseTableName: "form_mapping", constraintName: "FKo4ias6dtk5ibudug81me79d9v", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "form_mapping_format", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-314") {
        addForeignKeyConstraint(baseColumnNames: "collected_by", baseTableName: "pregnancy_outcome", constraintName: "FKolb5mgupc5dwi23sn68jnrfcm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-315") {
        addForeignKeyConstraint(baseColumnNames: "visit_id", baseTableName: "out_migration", constraintName: "FKov6w47j4ksv8628ie2wr0pcic", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "visit", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-316") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "pregnancy_outcome", constraintName: "FKowf4ssajdba1vqs48b8f04oow", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-317") {
        addForeignKeyConstraint(baseColumnNames: "head_id", baseTableName: "head_relationship", constraintName: "FKposuvi5i45yh17714s1100vcb", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-318") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "residency", constraintName: "FKpso3jivyaje2hqpeh5ce7b79q", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-319") {
        addForeignKeyConstraint(baseColumnNames: "child_id", baseTableName: "pregnancy_child", constraintName: "FKpyspddt8m4m9cejjlf92hobj1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-320") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "ext_dataset", constraintName: "FKq2vjijmvgt04vg23mas9tskgk", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-321") {
        addForeignKeyConstraint(baseColumnNames: "member_id", baseTableName: "out_migration", constraintName: "FKq7qx66gtdqc36ym4svsdl3egf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-322") {
        addForeignKeyConstraint(baseColumnNames: "redcap_api", baseTableName: "form", constraintName: "FKqhkel8je0j1rp002jrtlec2hv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "redcap_api", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-323") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "_user", constraintName: "FKr33f4vi8pg6p0xxv4nyevuv5k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-324") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "incomplete_visit", constraintName: "FKracibau4akg1yoo5ds75ymisk", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-325") {
        addForeignKeyConstraint(baseColumnNames: "updated_by", baseTableName: "module", constraintName: "FKrgreiirygb22etggcqn98ymp8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-326") {
        addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "_user_role", constraintName: "FKsa99se25erlgqmbdawypj2fiw", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-327") {
        addForeignKeyConstraint(baseColumnNames: "created_by", baseTableName: "module", constraintName: "FKsuhoukh127b943qsbq3cje7eg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "_user", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-328") {
        addForeignKeyConstraint(baseColumnNames: "member_id", baseTableName: "enumeration", constraintName: "FKtajw1xkguajp6l7tyrbhi4jwu", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-329") {
        addForeignKeyConstraint(baseColumnNames: "form_id", baseTableName: "redcap_mapping", constraintName: "FKtao15qhqrwmfso2l6nhim9hd8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "form", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1652717035723-330") {
        addForeignKeyConstraint(baseColumnNames: "member_id", baseTableName: "incomplete_visit", constraintName: "FKtllxx34nj23fma1ji9wg5hoin", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member", validate: "true")
    }
}