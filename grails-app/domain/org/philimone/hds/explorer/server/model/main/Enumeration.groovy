package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity

import java.time.LocalDate

/*
 * This class represents an Member Enumeration that occured within a Visit
 */
class Enumeration extends CollectableEntity {

    String id

    Visit visit
    String visitCode
    Household household
    String householdCode
    Member member
    String memberCode
    LocalDate eventDate
    String education
    String religion
    String phonePrimary
    String phoneAlternative

    static constraints = {
        id maxSize: 32

        visit nullable: false
        visitCode nullable: false, blank: false
        household nullable: false
        householdCode nullable: false, blank: false
        member unique: true, nullable: false
        memberCode unique: true, nullable: false
        eventDate nullable: false
        education nullable: true
        religion nullable: true
        phonePrimary blank: true, nullable: true
        phoneAlternative blank: true, nullable: true
    }

    static mapping = {
        table "enumeration"

        id column: "id", generator: 'uuid'

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"
        household column: "household_id"
        householdCode column: "household_code"
        member column: "member_id"
        memberCode column: "member_code"
        eventDate column: "event_date"
        education column: "education"
        religion column: "religion"
        phonePrimary column: "phone_primary"
        phoneAlternative column: "phone_alternative"
    }
}
