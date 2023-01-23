databaseChangeLog = {

    changeSet(author: "paul (generated)", id: "1674459818476-1") {
        addColumn(tableName: "_raw_household") {
            column(name: "pre_registered", type: "bit", afterColumn: "modules") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "paul (generated)", id: "1674459818476-2") {
        addColumn(tableName: "household") {
            column(name: "pre_registered", type: "bit", afterColumn: "modules") {
                constraints(nullable: "false")
            }
        }
    }
}
