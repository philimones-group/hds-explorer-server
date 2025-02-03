databaseChangeLog = {
    /* raw edit member */
    changeSet(author: "paul (generated)", id: "1738415253319-35") {
        addColumn(tableName: "_raw_edit_member") {
            column(name: "phone_primary", type: "varchar(255)", afterColumn: "religion")
        }
    }

    changeSet(author: "paul (generated)", id: "1738415253319-36") {
        addColumn(tableName: "_raw_edit_member") {
            column(name: "phone_alternative", type: "varchar(255)", afterColumn: "phone_primary")
        }
    }

    /* raw ext inmigration */
    changeSet(author: "paul (generated)", id: "1738415253319-37") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "phone_primary", type: "varchar(255)", afterColumn: "religion")
        }
    }

    changeSet(author: "paul (generated)", id: "1738415253319-38") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "phone_alternative", type: "varchar(255)", afterColumn: "phone_primary")
        }
    }

    /* raw inmigration */
    changeSet(author: "paul (generated)", id: "1738415253319-39") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "phone_primary", type: "varchar(255)", afterColumn: "religion")
        }
    }

    changeSet(author: "paul (generated)", id: "1738415253319-40") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "phone_alternative", type: "varchar(255)", afterColumn: "phone_primary")
        }
    }

    /* raw member enu */
    changeSet(author: "paul (generated)", id: "1738415253319-41") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "phone_primary", type: "varchar(255)", afterColumn: "religion")
        }
    }

    changeSet(author: "paul (generated)", id: "1738415253319-42") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "phone_alternative", type: "varchar(255)", afterColumn: "phone_primary")
        }
    }

    /* enumeration */
    changeSet(author: "paul (generated)", id: "1738415253319-43") {
        addColumn(tableName: "enumeration") {
            column(name: "phone_primary", type: "varchar(255)", afterColumn: "religion")
        }
    }

    changeSet(author: "paul (generated)", id: "1738415253319-44") {
        addColumn(tableName: "enumeration") {
            column(name: "phone_alternative", type: "varchar(255)", afterColumn: "phone_primary")
        }
    }

    /* in migration */
    changeSet(author: "paul (generated)", id: "1738415253319-45") {
        addColumn(tableName: "in_migration") {
            column(name: "phone_primary", type: "varchar(255)", afterColumn: "religion")
        }
    }

    changeSet(author: "paul (generated)", id: "1738415253319-46") {
        addColumn(tableName: "in_migration") {
            column(name: "phone_alternative", type: "varchar(255)", afterColumn: "phone_primary")
        }
    }

    /* member */
    changeSet(author: "paul (generated)", id: "1738415253319-47") {
        addColumn(tableName: "member") {
            column(name: "phone_primary", type: "varchar(255)", afterColumn: "religion")
        }
    }

    changeSet(author: "paul (generated)", id: "1738415253319-48") {
        addColumn(tableName: "member") {
            column(name: "phone_alternative", type: "varchar(255)", afterColumn: "phone_primary")
        }
    }

}
