databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1788527026623-35") {
        createTable(tableName: "data_export_report") {
            // Framework Core / Technical Columns
            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "data_export_reportPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            // Core Fields
            column(name: "export_item", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "dataset_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
            column(name: "dataset_label", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "format", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            // Progress Monitoring
            column(name: "progress_percent", type: "INT")

            column(name: "current_step", type: "VARCHAR(255)")

            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            // Filter Parameters Snapshot
            column(name: "reference_date", type: "date")

            column(name: "gender", type: "VARCHAR(255)")

            column(name: "age_min", type: "INT")

            column(name: "age_max", type: "INT")

            column(name: "region_code", type: "VARCHAR(255)")

            column(name: "random_sample_percent", type: "DOUBLE PRECISION")

            column(name: "include_dictionary", type: "BIT") {
                constraints(nullable: "false")
            }

            column(name: "active_residents_only", type: "BIT") {
                constraints(nullable: "false")
            }

            // Granular Anonymization & Privacy Enums
            column(name: 'name_anonymization_mode', type: "VARCHAR(255)")
            column(name: 'phone_anonymization_mode', type: "VARCHAR(255)")
            column(name: 'gps_anonymization_mode', type: "VARCHAR(255)")
            column(name: 'date_anonymization_mode', type: "VARCHAR(255)")

            // File Names in SystemPath.externalDocsPath
            column(name: "dataset_file_name", type: "VARCHAR(255)")

            column(name: "codebook_file_name", type: "VARCHAR(255)")

            column(name: "zip_file_name", type: "VARCHAR(255)")

            column(name: "dataset_file_size", type: "BIGINT")

            column(name: "codebook_file_size", type: "BIGINT")

            column(name: "zip_file_size", type: "BIGINT")

            // Performance & Audit Metrics
            column(name: "total_records", type: "BIGINT")

            column(name: "total_columns", type: "BIGINT")

            column(name: "execution_time_ms", type: "BIGINT")

            column(name: "error_message", type: "VARCHAR(2000)")

            // Creation Metadata
            column(name: "created_by", type: "VARCHAR(255)")

            column(name: "created_date", type: "datetime(6)") {
                constraints(nullable: "false")
            }

        }
    }

    changeSet(author: "paul (generated)", id: "1788527026623-40") {
        createIndex(indexName: "idx_export_status", tableName: "data_export_report") {
            column(name: "status")
        }
    }
}
