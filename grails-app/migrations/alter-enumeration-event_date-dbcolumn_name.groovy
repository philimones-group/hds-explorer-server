databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1686502896034-27") {
        renameColumn(tableName: "enumeration", oldColumnName: "visit_reason", newColumnName: "event_date", columnDataType: "date")
    }

}
