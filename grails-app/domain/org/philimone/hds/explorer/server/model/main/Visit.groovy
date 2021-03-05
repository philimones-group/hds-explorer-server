package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.VisitLocationItem

class Visit extends CollectableEntity {

    String id
    String code

    Household household
    String householdCode

    Date visitDate
    VisitLocationItem visitLocation
    String visitLocationOther

    Integer roundNumber

    Member respondent
    String respondentCode

    Boolean hasInterpreter
    String interpreterName

    Double gpsAccuracy;
    Double gpsAltitude;
    Double gpsLatitude;
    Double gpsLongitude;

    static constraints = {
        id maxSize: 32

        code unique: true, nullable: false

        household nullable: false
        householdCode nullable: false

        visitDate nullable: false
        visitLocation blank: true, nullable: true, enumType: "identity"
        visitLocationOther blank: true, nullable: true

        roundNumber min: 0

        respondent nullable: true
        respondentCode blank: true, nullable: true

        hasInterpreter nullable: true
        interpreterName blank: true, nullable: true

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
        householdCode column: "household_code"

        visitDate column: "visit_date"
        visitLocation column: "visit_location"
        visitLocationOther column: "visit_location_other"

        roundNumber column: "round_number"

        respondent column: "respondent_id"
        respondentCode column: "respondent_code"

        hasInterpreter column: "has_interpreter"
        interpreterName column: "interpreter_name"

        gpsAccuracy column: "gps_accuracy"
        gpsAltitude column: "gps_altitude"
        gpsLatitude column: "gps_latitude"
        gpsLongitude column: "gps_longitude"

    }
}
