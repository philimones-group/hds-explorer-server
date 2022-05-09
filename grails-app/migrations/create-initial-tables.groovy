databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1652104231980-1") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-2") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-3") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-4") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-5") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-6") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-7") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-8") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-9") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-10") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-11") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-12") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-13") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-14") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-15") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-16") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-17") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-18") {
        createTable(tableName: "_raw_member_enu") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_member_enuPK")
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

    changeSet(author: "paul (generated)", id: "1652104231980-19") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-20") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-21") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-22") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-23") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-24") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-25") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-26") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-27") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-28") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-29") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-30") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-31") {
        createTable(tableName: "dataset_label") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "dataset_labelPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

null

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "label", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1652104231980-32") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-33") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-34") {
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

            column(name: "dependencies", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "created_by", type: "VARCHAR(32)")

            column(name: "created_date", type: "datetime")

            column(name: "updated_by", type: "VARCHAR(32)")

            column(name: "updated_date", type: "datetime")

        }
    }

    changeSet(author: "paul (generated)", id: "1652104231980-35") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-36") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-37") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-38") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-39") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-40") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-41") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-42") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-43") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-44") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-45") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-46") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-47") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-48") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-49") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-50") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-51") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-52") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-53") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-54") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-55") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-56") {
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

    changeSet(author: "paul (generated)", id: "1652104231980-57") {
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

}
