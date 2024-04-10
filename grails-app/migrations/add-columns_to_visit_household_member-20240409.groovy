databaseChangeLog = {

    /* raw_visit */

    changeSet(author: "paul (generated)", id: "1712649954745-30") {
        addColumn(tableName: "_raw_visit") {
            column(name: "visit_possible", type: "bit", afterColumn: "round_number")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-31") {
        addColumn(tableName: "_raw_visit") {
            column(name: "visit_not_possible_reason", type: "varchar(255)", afterColumn: "visit_possible")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-32") {
        addColumn(tableName: "_raw_visit") {
            column(name: "respondent_resident", type: "bit", afterColumn: "visit_not_possible_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-33") {
        addColumn(tableName: "_raw_visit") {
            column(name: "respondent_relationship", type: "varchar(255)", afterColumn: "respondent_resident")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-34") {
        addColumn(tableName: "_raw_visit") {
            column(name: "respondent_name", type: "varchar(255)", afterColumn: "respondent_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-35") {
        createIndex(indexName: "idx_novisit_reason", tableName: "_raw_visit") {
            column(name: "visit_not_possible_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-36") {
        createIndex(indexName: "idx_respondent_relat", tableName: "_raw_visit") {
            column(name: "respondent_relationship")
        }
    }

    /* visit */

    changeSet(author: "paul (generated)", id: "1712649954745-37") {
        addColumn(tableName: "visit") {
            column(name: "visit_possible", type: "bit", afterColumn: "round_number")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-38") {
        addColumn(tableName: "visit") {
            column(name: "visit_not_possible_reason", type: "varchar(255)", afterColumn: "visit_possible")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-39") {
        addColumn(tableName: "visit") {
            column(name: "respondent_resident", type: "bit", afterColumn: "visit_not_possible_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-40") {
        addColumn(tableName: "visit") {
            column(name: "respondent_relationship", type: "varchar(255)", afterColumn: "respondent_resident")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-41") {
        addColumn(tableName: "visit") {
            column(name: "respondent_name", type: "varchar(255)", afterColumn: "respondent_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-42") {
        createIndex(indexName: "idx_novisit_reason", tableName: "visit") {
            column(name: "visit_not_possible_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1712649954745-43") {
        createIndex(indexName: "idx_respondent_relat", tableName: "visit") {
            column(name: "respondent_relationship")
        }
    }

    /* household */
    changeSet(author: "paul (generated)", id: "1712649954745-44") {
        addColumn(tableName: "household") {
            column(name: "status", type: "varchar(255)", afterColumn: "sin_longitude")
        }
    }

    /* member */
    changeSet(author: "paul (generated)", id: "1712649954745-45") {
        addColumn(tableName: "member") {
            column(name: "status", type: "varchar(255)", afterColumn: "sin_longitude")
        }
    }

}
