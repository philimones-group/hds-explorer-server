databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1724399970364-32") {
        renameColumn(tableName: "pregnancy_child", oldColumnName: "outcomde_type", newColumnName: "outcome_type", columnDataType: "VARCHAR(255)")
    }

    changeSet(author: "paul (generated)", id: "1724399970364-33") {
        renameColumn(tableName: "_raw_pregnancy_child", oldColumnName: "outcomde_type", newColumnName: "outcome_type", columnDataType: "VARCHAR(255)")
    }

    changeSet(author: "paul (generated)", id: "1724399970364-34") {

        sql("update pregnancy_registration set edd_type='ULTRASOUND' where edd_type='ULTRASOUND'")
        sql("update pregnancy_registration set edd_type='LMP' where edd_type='LAST_MENSTRUAL_PERIOD'")
        sql("update pregnancy_registration set edd_type='SFH' where edd_type='SYMPHISIS_FUNDAL_EIGHT'")
        sql("update pregnancy_registration set edd_type='DONT_KNOW' where edd_type='DONT_KNOW'")

        sql("update pregnancy_child set outcome_type='LBR' where outcome_type='LIVEBIRTH'")
        sql("update pregnancy_child set outcome_type='SBR' where outcome_type='STILLBIRTH'")
        sql("update pregnancy_child set outcome_type='MIS' where outcome_type='MISCARRIAGE'")
        sql("update pregnancy_child set outcome_type='ABT' where outcome_type='ABORTION'")

        sql("update out_migration set type='CHG' where type='INTERNAL'")
        sql("update out_migration set type='EXT' where type='EXTERNAL'")

        sql("update in_migration set type='ENT' where type='INTERNAL'")
        sql("update in_migration set type='XEN' where type='EXTERNAL'")
    }
}
