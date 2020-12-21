package org.philimone.hds.explorer.server.model.main

class Round {

    String id
    Integer roundNumber
    Date startDate
    Date endDate
    String description

    static constraints = {
        id maxSize: 32

        roundNumber unique: true, min: 0
        startDate nullable: false
        endDate nullable: false
        description blank: true, nullable: true

    }

    static mapping = {
        id column: "id", generator: 'uuid'

        roundNumber column: "round_number"
        startDate column: "start_date"
        endDate column: "end_date"
        description column: "description"
    }
}

