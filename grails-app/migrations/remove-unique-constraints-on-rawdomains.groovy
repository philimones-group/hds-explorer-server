databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1676561463875-13") {
        dropUniqueConstraint(constraintName: "UC__RAW_HOUSEHOLDHOUSEHOLD_CODE_COL", tableName: "_raw_household")
    }

    changeSet(author: "paul (generated)", id: "1676561463875-14") {
        dropUniqueConstraint(constraintName: "UC__RAW_MEMBER_ENUCODE_COL", tableName: "_raw_member_enu")
    }

    changeSet(author: "paul (generated)", id: "1676561463875-15") {
        dropUniqueConstraint(constraintName: "UC__RAW_PREGNANCY_OUTCOMECODE_COL", tableName: "_raw_pregnancy_outcome")
    }

    changeSet(author: "paul (generated)", id: "1676561463875-16") {
        dropUniqueConstraint(constraintName: "UC__RAW_PREGNANCY_REGISTRATIONCODE_COL", tableName: "_raw_pregnancy_registration")
    }

    changeSet(author: "paul (generated)", id: "1676561463875-17") {
        dropUniqueConstraint(constraintName: "UC__RAW_VISITCODE_COL", tableName: "_raw_visit")
    }

    changeSet(author: "paul (generated)", id: "1676561463875-1") {
        dropNotNullConstraint(columnDataType: "datetime", columnName: "collected_date", tableName: "_raw_pregnancy_outcome")
    }

    changeSet(author: "paul (generated)", id: "1676561463875-2") {
        dropNotNullConstraint(columnDataType: "datetime", columnName: "uploaded_date", tableName: "_raw_pregnancy_outcome")
    }

    changeSet(author: "paul (generated)", id: "1676561463875-4") {
        createIndex(indexName: "idx_code", tableName: "_raw_member_enu", unique: "false") {
            column(name: "code")
        }
    }

    changeSet(author: "paul (generated)", id: "1676561463875-6") {
        createIndex(indexName: "idx_code", tableName: "_raw_pregnancy_outcome", unique: "false") {
            column(name: "code")
        }
    }

    changeSet(author: "paul (generated)", id: "1676561463875-8") {
        createIndex(indexName: "idx_code", tableName: "_raw_pregnancy_registration", unique: "false") {
            column(name: "code")
        }
    }

    changeSet(author: "paul (generated)", id: "1676561463875-10") {
        createIndex(indexName: "idx_code", tableName: "_raw_visit", unique: "false") {
            column(name: "code")
        }
    }

    changeSet(author: "paul (generated)", id: "1676561463875-12") {
        createIndex(indexName: "idx_household_code", tableName: "_raw_household", unique: "false") {
            column(name: "household_code")
        }
    }
}
