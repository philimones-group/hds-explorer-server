databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1676648194061-3") {
        addColumn(tableName: "_raw_pregnancy_child") {
            column(name: "child_collected_id", type: "varchar(255)", afterColumn: "outcomde_type") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-4") {
        addColumn(tableName: "pregnancy_child") {
            column(name: "child_collected_id", type: "varchar(255)", afterColumn: "child_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-5") {
        addColumn(tableName: "_raw_change_head") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-6") {
        addColumn(tableName: "_raw_death") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-7") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-8") {
        addColumn(tableName: "_raw_household") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-9") {
        addColumn(tableName: "_raw_incomplete_visit") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-10") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-11") {
        addColumn(tableName: "_raw_marital_relationship") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-12") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-13") {
        addColumn(tableName: "_raw_outmigration") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-14") {
        addColumn(tableName: "_raw_pregnancy_outcome") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-15") {
        addColumn(tableName: "_raw_pregnancy_registration") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-16") {
        addColumn(tableName: "_raw_region") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-17") {
        addColumn(tableName: "_raw_visit") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-18") {
        addColumn(tableName: "death") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-19") {
        addColumn(tableName: "enumeration") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-20") {
        addColumn(tableName: "household") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-21") {
        addColumn(tableName: "in_migration") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-22") {
        addColumn(tableName: "incomplete_visit") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-23") {
        addColumn(tableName: "marital_relationship") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-24") {
        addColumn(tableName: "member") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-25") {
        addColumn(tableName: "out_migration") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-26") {
        addColumn(tableName: "pregnancy_outcome") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-27") {
        addColumn(tableName: "pregnancy_registration") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-28") {
        addColumn(tableName: "region") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-29") {
        addColumn(tableName: "visit") {
            column(name: "collected_device_id", type: "varchar(255)", afterColumn: "collected_by")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-30") {
        addColumn(tableName: "_raw_change_head") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-31") {
        addColumn(tableName: "_raw_death") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-32") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-33") {
        addColumn(tableName: "_raw_incomplete_visit") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-34") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-35") {
        addColumn(tableName: "_raw_marital_relationship") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-36") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-37") {
        addColumn(tableName: "_raw_outmigration") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-38") {
        addColumn(tableName: "_raw_pregnancy_outcome") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-39") {
        addColumn(tableName: "_raw_pregnancy_registration") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-40") {
        addColumn(tableName: "_raw_visit") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-41") {
        addColumn(tableName: "death") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-42") {
        addColumn(tableName: "enumeration") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-43") {
        addColumn(tableName: "household") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-44") {
        addColumn(tableName: "in_migration") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-45") {
        addColumn(tableName: "incomplete_visit") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-46") {
        addColumn(tableName: "marital_relationship") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-47") {
        addColumn(tableName: "member") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-48") {
        addColumn(tableName: "out_migration") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-49") {
        addColumn(tableName: "pregnancy_outcome") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-50") {
        addColumn(tableName: "pregnancy_registration") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-51") {
        addColumn(tableName: "region") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-52") {
        addColumn(tableName: "visit") {
            column(name: "collected_household_id", type: "varchar(255)", afterColumn: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-53") {
        addColumn(tableName: "_raw_change_head") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-54") {
        addColumn(tableName: "_raw_death") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-55") {
        addColumn(tableName: "_raw_ext_inmigration") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-56") {
        addColumn(tableName: "_raw_incomplete_visit") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-57") {
        addColumn(tableName: "_raw_inmigration") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-58") {
        addColumn(tableName: "_raw_marital_relationship") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-59") {
        addColumn(tableName: "_raw_member_enu") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-60") {
        addColumn(tableName: "_raw_outmigration") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-61") {
        addColumn(tableName: "_raw_pregnancy_outcome") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-62") {
        addColumn(tableName: "_raw_pregnancy_registration") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-63") {
        addColumn(tableName: "_raw_visit") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-64") {
        addColumn(tableName: "death") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-65") {
        addColumn(tableName: "enumeration") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-66") {
        addColumn(tableName: "household") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-67") {
        addColumn(tableName: "in_migration") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-68") {
        addColumn(tableName: "incomplete_visit") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-69") {
        addColumn(tableName: "marital_relationship") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-70") {
        addColumn(tableName: "member") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-71") {
        addColumn(tableName: "out_migration") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-72") {
        addColumn(tableName: "pregnancy_outcome") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-73") {
        addColumn(tableName: "pregnancy_registration") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-74") {
        addColumn(tableName: "region") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676648194061-75") {
        addColumn(tableName: "visit") {
            column(name: "collected_member_id", type: "varchar(255)", afterColumn: "collected_household_id")
        }
    }

    /*Indexes*/

    changeSet(author: "paul (generated)", id: "1676788504051-76") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_change_head") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-77") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_death") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-78") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_ext_inmigration") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-79") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_household") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-80") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_incomplete_visit") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-81") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_inmigration") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-82") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_marital_relationship") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-83") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_outmigration") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-84") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_pregnancy_outcome") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-85") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_pregnancy_registration") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-86") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_region") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-87") {
        createIndex(indexName: "idx_cdeviceid", tableName: "_raw_visit") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-88") {
        createIndex(indexName: "idx_cdeviceid", tableName: "death") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-89") {
        createIndex(indexName: "idx_cdeviceid", tableName: "enumeration") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-90") {
        createIndex(indexName: "idx_cdeviceid", tableName: "household") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-91") {
        createIndex(indexName: "idx_cdeviceid", tableName: "in_migration") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-92") {
        createIndex(indexName: "idx_cdeviceid", tableName: "incomplete_visit") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-93") {
        createIndex(indexName: "idx_cdeviceid", tableName: "marital_relationship") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-94") {
        createIndex(indexName: "idx_cdeviceid", tableName: "member") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-95") {
        createIndex(indexName: "idx_cdeviceid", tableName: "out_migration") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-96") {
        createIndex(indexName: "idx_cdeviceid", tableName: "pregnancy_outcome") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-97") {
        createIndex(indexName: "idx_cdeviceid", tableName: "pregnancy_registration") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-98") {
        createIndex(indexName: "idx_cdeviceid", tableName: "region") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-99") {
        createIndex(indexName: "idx_cdeviceid", tableName: "visit") {
            column(name: "collected_device_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-100") {
        createIndex(indexName: "idx_child_coll_id", tableName: "pregnancy_child") {
            column(name: "child_collected_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-101") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_change_head") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-102") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_death") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-103") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_ext_inmigration") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-104") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_incomplete_visit") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-105") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_inmigration") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-106") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_marital_relationship") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-107") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_outmigration") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-108") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_pregnancy_outcome") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-109") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_pregnancy_registration") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-110") {
        createIndex(indexName: "idx_chouseid", tableName: "_raw_visit") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-111") {
        createIndex(indexName: "idx_chouseid", tableName: "death") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-112") {
        createIndex(indexName: "idx_chouseid", tableName: "enumeration") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-113") {
        createIndex(indexName: "idx_chouseid", tableName: "household") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-114") {
        createIndex(indexName: "idx_chouseid", tableName: "in_migration") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-115") {
        createIndex(indexName: "idx_chouseid", tableName: "incomplete_visit") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-116") {
        createIndex(indexName: "idx_chouseid", tableName: "marital_relationship") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-117") {
        createIndex(indexName: "idx_chouseid", tableName: "member") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-118") {
        createIndex(indexName: "idx_chouseid", tableName: "out_migration") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-119") {
        createIndex(indexName: "idx_chouseid", tableName: "pregnancy_outcome") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-120") {
        createIndex(indexName: "idx_chouseid", tableName: "pregnancy_registration") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-121") {
        createIndex(indexName: "idx_chouseid", tableName: "region") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-122") {
        createIndex(indexName: "idx_chouseid", tableName: "visit") {
            column(name: "collected_household_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-123") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_change_head") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-124") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_death") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-125") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_ext_inmigration") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-126") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_incomplete_visit") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-127") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_inmigration") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-128") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_marital_relationship") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-129") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_outmigration") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-130") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_pregnancy_outcome") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-131") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_pregnancy_registration") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-132") {
        createIndex(indexName: "idx_cmemberid", tableName: "_raw_visit") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-133") {
        createIndex(indexName: "idx_cmemberid", tableName: "death") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-134") {
        createIndex(indexName: "idx_cmemberid", tableName: "enumeration") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-135") {
        createIndex(indexName: "idx_cmemberid", tableName: "household") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-136") {
        createIndex(indexName: "idx_cmemberid", tableName: "in_migration") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-137") {
        createIndex(indexName: "idx_cmemberid", tableName: "incomplete_visit") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-138") {
        createIndex(indexName: "idx_cmemberid", tableName: "marital_relationship") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-139") {
        createIndex(indexName: "idx_cmemberid", tableName: "member") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-140") {
        createIndex(indexName: "idx_cmemberid", tableName: "out_migration") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-141") {
        createIndex(indexName: "idx_cmemberid", tableName: "pregnancy_outcome") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-142") {
        createIndex(indexName: "idx_cmemberid", tableName: "pregnancy_registration") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-143") {
        createIndex(indexName: "idx_cmemberid", tableName: "region") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-144") {
        createIndex(indexName: "idx_cmemberid", tableName: "visit") {
            column(name: "collected_member_id")
        }
    }

    changeSet(author: "paul (generated)", id: "1676788504051-1") {
        dropNotNullConstraint(columnDataType: "datetime", columnName: "collected_date", tableName: "_raw_pregnancy_registration")
    }

    changeSet(author: "paul (generated)", id: "1676788504051-2") {
        dropNotNullConstraint(columnDataType: "datetime", columnName: "uploaded_date", tableName: "_raw_pregnancy_registration")
    }
}
