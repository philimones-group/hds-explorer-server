databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1677059320514-1") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_DEATHCOLLECTED_UUID_COL", tableName: "death")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-2") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_ENUMERATIONCOLLECTED_UUID_COL", tableName: "enumeration")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-3") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_HOUSEHOLDCOLLECTED_UUID_COL", tableName: "household")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-4") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_INCOMPLETE_VISITCOLLECTED_UUID_COL", tableName: "incomplete_visit")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-5") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_IN_MIGRATIONCOLLECTED_UUID_COL", tableName: "in_migration")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-6") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_MARITAL_RELATIONSHIPCOLLECTED_UUID_COL", tableName: "marital_relationship")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-7") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_MEMBERCOLLECTED_UUID_COL", tableName: "member")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-8") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_OUT_MIGRATIONCOLLECTED_UUID_COL", tableName: "out_migration")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-9") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_PREGNANCY_OUTCOMECOLLECTED_UUID_COL", tableName: "pregnancy_outcome")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-10") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_PREGNANCY_REGISTRATIONCOLLECTED_UUID_COL", tableName: "pregnancy_registration")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-11") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_REGIONCOLLECTED_UUID_COL", tableName: "region")
    }

    changeSet(author: "paul (generated)", id: "1677059320514-12") {
        addUniqueConstraint(columnNames: "collected_uuid", constraintName: "UC_VISITCOLLECTED_UUID_COL", tableName: "visit")
    }
}
