databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1726222100867-31") {
        addColumn(tableName: "core_form_column_map") {
            column(name: "column_code", type: "varchar(255)", afterColumn: "form_name") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1726222100867-32") {
        addColumn(tableName: "core_form_column_options") {
            column(name: "column_code", type: "varchar(255)", afterColumn: "version") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1726222100867-33") {
        addColumn(tableName: "core_form_column_options") {
            column(name: "readonly", type: "bit") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1726222100867-34") {
        dropUniqueConstraint(constraintName: "UK577303c51e5ac91b89a27c9577dc", tableName: "core_form_column_options")
    }

    changeSet(author: "paul (generated)", id: "1726222100867-35") {
        dropUniqueConstraint(constraintName: "UKe0a2d7f333f56f209eae906547f1", tableName: "core_form_column_options")
    }

    changeSet(author: "paul (generated)", id: "1726222100867-36") {
        dropUniqueConstraint(constraintName: "UKd2cb66bd860f533b309d0cb4250a", tableName: "core_form_column_map")
    }

    /* Update database tables */
    changeSet(author: "paul (generated)", id: "1726222100867-37") {

        //update Raw/Outmigration.migrationReason with WENT_WITH_RELATIVES where CAME_WITH_RELATIVES
        sql("update _raw_outmigration set migration_reason='WENT_WITH_RELATIVES' where migration_reason='CAME_WITH_RELATIVES'")
        sql("update out_migration set migration_reason='WENT_WITH_RELATIVES' where migration_reason='CAME_WITH_RELATIVES'")

        sql("update core_form_column_map set column_code='#.education' where column_name='education'")
        sql("update core_form_column_map set column_code='#.religion' where column_name='religion'")

        sql("update core_form_column_options set column_code='#.education' where column_name='education'")
        sql("update core_form_column_options set column_code='#.religion' where column_name='religion'")

        //set readonly options
        sql("update core_form_column_options set readonly=true where option_value='OTHER'")
        sql("update core_form_column_options set readonly=true where option_value='UNK'")
        sql("update core_form_column_options set readonly=true where option_value='DNK'")
        sql("update core_form_column_options set readonly=true where option_value='DONT_KNOW'")
        sql("update core_form_column_options set readonly=true where option_value='OTHERPLACE'")
    }

    /* at the end add unique combined constraints because we have updated data now */
    changeSet(author: "paul (generated)", id: "1726222100867-38") {
        addUniqueConstraint(columnNames: "form_name, column_code", constraintName: "UC_COLUMN_MAP_FORMNAME_COLUMN_CODE_COL", tableName: "core_form_column_map")
    }

    changeSet(author: "paul (generated)", id: "1726222100867-39") {
        addUniqueConstraint(columnNames: "column_code, ordinal", constraintName: "UK_column_options_code_ordinal", tableName: "core_form_column_options")
        addUniqueConstraint(columnNames: "column_code, option_value", constraintName: "UK_column_options_code_value", tableName: "core_form_column_options")
        addUniqueConstraint(columnNames: "column_code, ordinal, option_value", constraintName: "UK_column_options_code_ordinal_value", tableName: "core_form_column_options")
    }

}
