databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1700109949034-30") {
        addColumn(tableName: "_raw_change_head") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-31") {
        addColumn(tableName: "_raw_death") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "death_place")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-32") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-33") {
        addColumn(tableName: "_raw_household") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-34") {
        addColumn(tableName: "_raw_incomplete_visit") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "other_visit_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-35") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "migration_reason")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-36") {
        addColumn(tableName: "_raw_marital_relationship") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "end_date")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-37") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-38") {
        addColumn(tableName: "_raw_outmigration") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-39") {
        addColumn(tableName: "_raw_pregnancy_outcome") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-40") {
        addColumn(tableName: "_raw_pregnancy_registration") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "visit_code")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-41") {
        addColumn(tableName: "_raw_region") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "modules")
        }
    }

    changeSet(author: "paul (generated)", id: "1700109949034-42") {
        addColumn(tableName: "_raw_visit") {
            column(name: "extension_form", type: "mediumblob", afterColumn: "gps_longitude")
        }
    }
}
