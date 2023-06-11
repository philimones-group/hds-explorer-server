databaseChangeLog = {
    /* add collected_start after collected_date */
    changeSet(author: "paul (generated)", id: "1685963409166-52") {
        addColumn(tableName: "_raw_change_head") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-53") {
        addColumn(tableName: "_raw_death") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-54") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-55") {
        addColumn(tableName: "_raw_household") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-56") {
        addColumn(tableName: "_raw_incomplete_visit") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-57") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-58") {
        addColumn(tableName: "_raw_marital_relationship") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-59") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-60") {
        addColumn(tableName: "_raw_outmigration") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-61") {
        addColumn(tableName: "_raw_pregnancy_outcome") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-62") {
        addColumn(tableName: "_raw_pregnancy_registration") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-63") {
        addColumn(tableName: "_raw_region") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-64") {
        addColumn(tableName: "_raw_visit") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-65") {
        addColumn(tableName: "death") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-66") {
        addColumn(tableName: "enumeration") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-67") {
        addColumn(tableName: "household") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-68") {
        addColumn(tableName: "in_migration") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-69") {
        addColumn(tableName: "incomplete_visit") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-70") {
        addColumn(tableName: "marital_relationship") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-71") {
        addColumn(tableName: "member") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-72") {
        addColumn(tableName: "out_migration") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-73") {
        addColumn(tableName: "pregnancy_outcome") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-74") {
        addColumn(tableName: "pregnancy_registration") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-75") {
        addColumn(tableName: "region") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-76") {
        addColumn(tableName: "visit") {
            column(name: "collected_start", type: "datetime(6)", afterColumn: "collected_member_id")
        }
    }

    /* add collected_end after collected_start*/
    changeSet(author: "paul (generated)", id: "1685963409166-27") {
        addColumn(tableName: "_raw_change_head") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-28") {
        addColumn(tableName: "_raw_death") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-29") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-30") {
        addColumn(tableName: "_raw_household") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-31") {
        addColumn(tableName: "_raw_incomplete_visit") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-32") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-33") {
        addColumn(tableName: "_raw_marital_relationship") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-34") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-35") {
        addColumn(tableName: "_raw_outmigration") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-36") {
        addColumn(tableName: "_raw_pregnancy_outcome") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-37") {
        addColumn(tableName: "_raw_pregnancy_registration") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-38") {
        addColumn(tableName: "_raw_region") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-39") {
        addColumn(tableName: "_raw_visit") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-40") {
        addColumn(tableName: "death") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-41") {
        addColumn(tableName: "enumeration") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-42") {
        addColumn(tableName: "household") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-43") {
        addColumn(tableName: "in_migration") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-44") {
        addColumn(tableName: "incomplete_visit") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-45") {
        addColumn(tableName: "marital_relationship") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-46") {
        addColumn(tableName: "member") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-47") {
        addColumn(tableName: "out_migration") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-48") {
        addColumn(tableName: "pregnancy_outcome") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-49") {
        addColumn(tableName: "pregnancy_registration") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-50") {
        addColumn(tableName: "region") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }

    changeSet(author: "paul (generated)", id: "1685963409166-51") {
        addColumn(tableName: "visit") {
            column(name: "collected_end", type: "datetime(6)", afterColumn: "collected_start")
        }
    }


}
