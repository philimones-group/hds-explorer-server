databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1730816918108-33") {
        createTable(tableName: "_raw_edit_household") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_edit_householdPK")
            }

            column(name: "region_code", type: "VARCHAR(255)")

            column(name: "region_name", type: "VARCHAR(255)")

            column(name: "household_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "household_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "head_code", type: "VARCHAR(255)")

            column(name: "head_name", type: "VARCHAR(255)")

            column(name: "gps_latitude", type: "VARCHAR(255)")

            column(name: "gps_longitude", type: "VARCHAR(255)")

            column(name: "gps_altitude", type: "VARCHAR(255)")

            column(name: "gps_accuracy", type: "VARCHAR(255)")

            column(name: "collected_by", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "collected_device_id", type: "VARCHAR(255)")

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

    changeSet(author: "paul (generated)", id: "1730818234407-34") {
        createTable(tableName: "_raw_edit_member") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_edit_memberPK")
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

            column(name: "mother_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "father_code", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "father_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "education", type: "VARCHAR(255)")

            column(name: "religion", type: "VARCHAR(255)")

            column(name: "household_code", type: "VARCHAR(255)")

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

    changeSet(author: "paul (generated)", id: "1730816918108-35") {
        createTable(tableName: "_raw_edit_region") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "_raw_edit_regionPK")
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

            column(name: "collected_device_id", type: "VARCHAR(255)")

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

    changeSet(author: "paul (generated)", id: "1730818234407-38") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_edit_household") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-39") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_edit_region") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-40") {
        createIndex(indexName: "idx_code", tableName: "_raw_edit_member") {
            column(name: "code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-41") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_edit_household") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-42") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_edit_member") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-43") {
        createIndex(indexName: "idx_collected_by", tableName: "_raw_edit_region") {
            column(name: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-44") {
        createIndex(indexName: "idx_father_code", tableName: "_raw_edit_member") {
            column(name: "father_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-45") {
        createIndex(indexName: "idx_head_code", tableName: "_raw_edit_household") {
            column(name: "head_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-46") {
        createIndex(indexName: "idx_household_code", tableName: "_raw_edit_household") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-47") {
        createIndex(indexName: "idx_household_code", tableName: "_raw_edit_member") {
            column(name: "household_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-48") {
        createIndex(indexName: "idx_mother_code", tableName: "_raw_edit_member") {
            column(name: "mother_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-49") {
        createIndex(indexName: "idx_parent_code", tableName: "_raw_edit_region") {
            column(name: "parent_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-50") {
        createIndex(indexName: "idx_region_code", tableName: "_raw_edit_household") {
            column(name: "region_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1730818234407-51") {
        createIndex(indexName: "idx_region_code", tableName: "_raw_edit_region") {
            column(name: "region_code")
        }
    }

}
