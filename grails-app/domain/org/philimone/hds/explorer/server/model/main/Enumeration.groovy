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

    static constraints = {
        id maxSize: 32

        visit nullable: false
        visitCode nullable: false, blank: false
        household nullable: false
        householdCode nullable: false, blank: false
        member unique: true, nullable: false
        memberCode unique: true, nullable: false, blank: false
        eventDate nullable: false
    }

    static mapping = {
        table "enumeration"

        id column: "id", generator: 'uuid'

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"
        household column: "household_id"
        householdCode column: "household_code"
        member column: "member_id"
        memberCode column: "member_code"//, index: "idx_member_code"
        eventDate column: "visit_reason"
    }
}
