import net.betainteractive.utilities.GeneralUtil

databaseChangeLog = {

    //I have inserted ordinal=3, HEAD_OUTMIGRATING, and others starting from 3 moved
    changeSet(author: "paul (generated)", id: "1755344266736-33") {

        sql("update core_form_column_options set ordinal=8 where ordinal=7 and option_value='OTHER' and column_code='rawChangeHead.reason' and column_name='reason'")
        sql("update core_form_column_options set ordinal=7 where ordinal=6 and option_value='DEATH' and column_code='rawChangeHead.reason' and column_name='reason'")
        sql("update core_form_column_options set ordinal=6 where ordinal=5 and option_value='HEALTH_REASONS' and column_code='rawChangeHead.reason' and column_name='reason'")
        sql("update core_form_column_options set ordinal=5 where ordinal=4 and option_value='WORK' and column_code='rawChangeHead.reason' and column_name='reason'")
        sql("update core_form_column_options set ordinal=4 where ordinal=3 and option_value='DIVORCE' and column_code='rawChangeHead.reason' and column_name='reason'")

        insert(tableName: "core_form_column_options") {
            column(name: "id",              value: "27911bad1a10445aaf38b79d43958518")
            column(name: "version",         valueNumeric: 0)
            column(name: "column_code",     value: "rawChangeHead.reason")
            column(name: "column_name",     value: "reason")
            column(name: "ordinal",         valueNumeric: 3)
            column(name: "option_value",    value: "HEAD_OUTMIGRATING")
            column(name: "option_label",    value: "The Head is Moving out")
            column(name: "option_label_code", value: "changeHeadReason.head_outmigrating")
            column(name: "readonly",        valueBoolean: false)
        }
    }
}
