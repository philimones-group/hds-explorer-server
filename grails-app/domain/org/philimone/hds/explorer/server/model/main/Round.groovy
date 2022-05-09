package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity

import java.time.LocalDate

class Round extends AuditableEntity {

    String id
    Integer roundNumber
    LocalDate startDate
    LocalDate endDate
    String description

    static constraints = {
        id maxSize: 32

        roundNumber unique: true, min: 0
        startDate nullable: false
        endDate nullable: false
        description blank: true, nullable: true

    }

    static mapping = {
        table 'round'

        id column: "id", generator: 'uuid'

        roundNumber column: "round_number"
        startDate column: "start_date"
        endDate column: "end_date"
        description column: "description"
    }
}

