databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1701939929978-29") {
        createTable(tableName: "core_form_extension_model") {

            column(name: "id", type: "VARCHAR(32)") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "core_form_extension_modelPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "core_form_id", type: "VARCHAR(32)") {
                constraints(nullable: "false")
            }

            column(name: "ext_form_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "db_column_index", type: "INT")

            column(name: "db_column_table", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "db_column_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "db_column_type", type: "VARCHAR(255)")

            column(name: "db_column_size", type: "INT")

            column(name: "form_column_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "form_column_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "form_repeat_group", type: "VARCHAR(255)")

            column(name: "form_repeat_length", type: "INT")

            column(name: "form_choidce_list", type: "BIT")

            column(name: "form_choice_value", type: "VARCHAR(255)")

            column(name: "repeat_per_table", type: "BIT") {
                constraints(nullable: "false")
            }

            column(name: "parent_group_id", type: "VARCHAR(32)")

        }
    }

    changeSet(author: "paul (generated)", id: "1701939929978-30") {
        addUniqueConstraint(columnNames: "db_column_name, db_column_table, core_form_id", constraintName: "UK74a9ec6e308dfbc6880205d48864", tableName: "core_form_extension_model")
    }

    changeSet(author: "paul (generated)", id: "1701939929978-31") {
        addForeignKeyConstraint(baseColumnNames: "parent_group_id", baseTableName: "core_form_extension_model", constraintName: "FK6byven1bbeiko6kfmom438tot", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "core_form_extension_model", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1701939929978-32") {
        addForeignKeyConstraint(baseColumnNames: "core_form_id", baseTableName: "core_form_extension_model", constraintName: "FKl4aodkfhn92nrextkx5jvcwqm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "core_form_extension", validate: "true")
    }

}
