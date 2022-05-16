package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.VisitLocationItem
import org.philimone.hds.explorer.server.model.enums.VisitReason

import java.time.LocalDate

class Visit extends CollectableEntity {

    String id
    String code

    Household household
    String householdCode

    LocalDate visitDate
    VisitReason visitReason
    VisitLocationItem visitLocation
    String visitLocationOther

    Integer roundNumber

    Member respondent
    String respondentCode

    Boolean hasInterpreter
    String interpreterName

    String nonVisitedMembers

    Double gpsAccuracy
    Double gpsAltitude
    Double gpsLatitude
    Double gpsLongitude

    static constraints = {
        id maxSize: 32

        code unique: true, nullable: false

        household nullable: false
        householdCode nullable: false

        visitDate nullable: false
        visitLocation blank: true, nullable: true, enumType: "identity"
        visitLocationOther blank: true, nullable: true
        visitReason blank:true, nullable: true, enumType: "identity"

        roundNumber min: 0

        respondent nullable: true
        respondentCode blank: true, nullable: true

        hasInterpreter nullable: true
        interpreterName blank: true, nullable: true

        nonVisitedMembers blank: true, nullable: true

        gpsAccuracy nullable: true
        gpsAltitude nullable: true
        gpsLatitude nullable: true
        gpsLongitude nullable: true

    }

    static mapping = {
        table 'visit'

        id column: "id", generator: 'uuid'

        code column: 'code'

        household column: "household_id"
        householdCode column: "household_code", index: "idx_household_code"

        visitDate column: "visit_date"
        visitLocation column: "visit_location"
        visitLocationOther column: "visit_location_other"
        visitReason column: "visit_reason"

        roundNumber column: "round_number", index: "idx_round_number"

        respondent column: "respondent_id"
        respondentCode column: "respondent_code", index: "idx_respondent_code"

        hasInterpreter column: "has_interpreter"
        interpreterName column: "interpreter_name"

        nonVisitedMembers column: "non_visited_members", index: "idx_nonv"

        gpsAccuracy column: "gps_accuracy"
        gpsAltitude column: "gps_altitude"
        gpsLatitude column: "gps_latitude"
        gpsLongitude column: "gps_longitude"

    }
}
