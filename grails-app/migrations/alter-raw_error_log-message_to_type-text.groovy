databaseChangeLog = {
    changeSet(author: "paul (generated)", id: "1731419500183-2") {
        modifyDataType(tableName: "_raw_error_log", columnName: "message", newDataType:"text")
    }
}
