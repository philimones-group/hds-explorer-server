databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1752589572310-35") {
        addColumn(tableName: "head_relationship") {
            column(name: "residency_id", type: "varchar(32)", afterColumn: "updated_date")
        }
    }

    changeSet(author: "paul (generated)", id: "1752589572310-36") {
        addForeignKeyConstraint(baseColumnNames: "residency_id", baseTableName: "head_relationship", constraintName: "FKrmh26k0grc36sc7ykgp7c657s", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "residency", validate: "true")
    }

    changeSet(author: "paul (generated)", id: "1752589572310-37") {
        sql("UPDATE head_relationship hr SET residency_id = (SELECT r.id FROM residency r WHERE r.household_code = hr.household_code AND r.member_code = hr.member_code AND r.start_date <= hr.start_date AND (r.end_date IS NULL OR r.end_date >= hr.start_date) AND (r.status <> 1 or r.status is null) ORDER BY r.start_date DESC LIMIT 1) WHERE hr.residency_id IS NULL")
    }
}
