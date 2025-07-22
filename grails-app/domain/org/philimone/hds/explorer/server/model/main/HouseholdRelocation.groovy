package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.HouseholdRelocationReason

import java.time.LocalDate

/*
 * This class represents an Household Relocation that is essentially a Internal Inmigration of all members of the Household
 */

class HouseholdRelocation extends CollectableEntity {

    String id

    Visit visit
    String visitCode
    Household origin
    String originCode
    Household destination
    String destinationCode
    LocalDate eventDate
    HouseholdRelocationReason reason
    String reasonOther

    static constraints = {
        id maxSize: 32

        visit nullable: false
        visitCode nullable: false, blank: false
        origin nullable: false
        originCode nullable: false, blank: false
        destination nullable: false
        destinationCode nullable: false, blank: false
        eventDate nullable: false
        reason nullable: true
        reasonOther nullable: true
    }

    static mapping = {
        table "household_relocation"

        id column: "id", generator: 'uuid'

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"
        origin column: "origin_id"
        originCode column: "origin_code", index: "idx_origin_code"
        destination column: "destination_id"
        destinationCode column: "destination_code", index: "idx_destination_code"
        eventDate column: "event_date"
        reason column: "reason", enumType: "identity"
        reasonOther column: "reasonOther"
    }
}
