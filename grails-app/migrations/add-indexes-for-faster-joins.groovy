databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1788461006534-39") {
        createIndex(indexName: "idx_headrel_member_start_stat", tableName: "head_relationship") {
            column(name: "start_date")
            column(name: "member_id")
            column(name: "status")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-40") {
        createIndex(indexName: "idx_hh_hierarchies", tableName: "household") {
            column(name: "hierarchy1")
            column(name: "hierarchy2")
            column(name: "hierarchy3")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-41") {
        createIndex(indexName: "idx_inmig_member_date", tableName: "in_migration") {
            column(name: "migration_date")
            column(name: "member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-42") {
        createIndex(indexName: "idx_inmig_origin_dest", tableName: "in_migration") {
            column(name: "origin_id")
            column(name: "destination_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-43") {
        createIndex(indexName: "idx_marrel_member_a", tableName: "marital_relationship") {
            column(name: "start_date")
            column(name: "status")
            column(name: "member_a_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-44") {
        createIndex(indexName: "idx_marrel_member_b", tableName: "marital_relationship") {
            column(name: "start_date")
            column(name: "member_b_id")
            column(name: "status")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-45") {
        createIndex(indexName: "idx_member_dob_gender", tableName: "member") {
            column(name: "gender")
            column(name: "dob")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-46") {
        createIndex(indexName: "idx_member_status", tableName: "member") {
            column(name: "status")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-47") {
        createIndex(indexName: "idx_outmig_member_date", tableName: "out_migration") {
            column(name: "migration_date")
            column(name: "member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-48") {
        createIndex(indexName: "idx_outmig_origin_dest", tableName: "out_migration") {
            column(name: "origin_id")
            column(name: "destination_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-49") {
        createIndex(indexName: "idx_pregchild_outcome_child", tableName: "pregnancy_child") {
            column(name: "child_id")
            column(name: "pregnancy_outcome_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-50") {
        createIndex(indexName: "idx_pregout_mother_date", tableName: "pregnancy_outcome") {
            column(name: "mother_id")
            column(name: "outcome_date")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-51") {
        createIndex(indexName: "idx_res_dates_status", tableName: "residency") {
            column(name: "end_date")
            column(name: "start_date")
            column(name: "status")
        }
    }

    changeSet(author: "paul (generated)", id: "1788461006534-52") {
        createIndex(indexName: "idx_res_member_start_stat", tableName: "residency") {
            column(name: "start_date")
            column(name: "member_id")
            column(name: "status")
        }
    }

}
