package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity

import java.time.LocalDate

class Death extends CollectableEntity {

    String id
    Member member
    String memberCode
    LocalDate deathDate
    Integer ageAtDeath
    String deathCause
    String deathPlace
    Visit visit
    String visitCode

    static constraints = {
        id maxSize: 32

        member nullable: false
        memberCode blank: false, nullable: false
        deathDate nullable: false
        ageAtDeath nullable: false, min: 0, max: 150
        deathCause nullable: true, blank: true
        deathPlace nullable: true, blank: true

        visit nullable: true
        visitCode blank: true, nullable: true
    }

    static mapping = {
        table 'death'

        id column: "id", generator: 'uuid'

        member column: "member_id"
        memberCode column: "member_code"
        deathDate column: "death_date"
        ageAtDeath column: "age_at_death"
        deathCause column: "death_cause"
        deathPlace column: "death_place"

        visit column: "visit_id"
        visitCode column: "visit_code"
    }
}
