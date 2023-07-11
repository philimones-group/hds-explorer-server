databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1688199220624-28") {
        createTable(tableName: "core_form_column_map") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "core_form_column_mapPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "form_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "column_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "enabled", type: "BIT") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1688199220624-29") {
        createTable(tableName: "core_form_column_options") {
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "core_form_column_optionsPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "column_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ordinal", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "option_value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "option_label", type: "VARCHAR(255)")

            column(name: "option_label_code", type: "VARCHAR(255)")

        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-30") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "education", type: "varchar(255)", afterColumn: "member_father_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-31") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "education", type: "varchar(255)", afterColumn: "member_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-32") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "education", type: "varchar(255)", afterColumn: "father_name")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-33") {
        addColumn(tableName: "enumeration") {
            column(name: "education", type: "varchar(255)", afterColumn: "event_date")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-34") {
        addColumn(tableName: "in_migration") {
            column(name: "education", type: "varchar(255)", afterColumn: "migration_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-35") {
        addColumn(tableName: "member") {
            column(name: "education", type: "varchar(255)", afterColumn: "father_name")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-36") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "religion", type: "varchar(255)", afterColumn: "education")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-37") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "religion", type: "varchar(255)", afterColumn: "education")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-38") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "religion", type: "varchar(255)", afterColumn: "education")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-39") {
        addColumn(tableName: "enumeration") {
            column(name: "religion", type: "varchar(255)", afterColumn: "education")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-40") {
        addColumn(tableName: "in_migration") {
            column(name: "religion", type: "varchar(255)", afterColumn: "education")
        }
    }

    changeSet(author: "paul (generated)", id: "1688141587200-41") {
        addColumn(tableName: "member") {
            column(name: "religion", type: "varchar(255)", afterColumn: "education")
        }
    }

    changeSet(author: "paul (generated)", id: "1688199220624-42") {
        addUniqueConstraint(columnNames: "option_value, column_name", constraintName: "UK577303c51e5ac91b89a27c9577dc", tableName: "core_form_column_options")
    }

    changeSet(author: "paul (generated)", id: "1688199220624-43") {
        addUniqueConstraint(columnNames: "column_name, form_name", constraintName: "UKd2cb66bd860f533b309d0cb4250a", tableName: "core_form_column_map")
    }

    changeSet(author: "paul (generated)", id: "1688199220624-44") {
        addUniqueConstraint(columnNames: "column_name, ordinal", constraintName: "UKe0a2d7f333f56f209eae906547f1", tableName: "core_form_column_options")
    }

}
