databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1768278392217-37") {
        addColumn(tableName: "pregnancy_registration") {
            column(name: "summary_delivery_date", type: "date", afterColumn: "summary_has_pregnancy_outcome")
        }
    }

    changeSet(author: "paul (generated)", id: "1768278392217-38") {
        sql("update pregnancy_registration pr, pregnancy_outcome po set pr.summary_delivery_date=po.outcome_date where pr.code = po.code")
    }

}
