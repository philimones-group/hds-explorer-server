package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawVisit {

    String id

    String code
    String householdCode
    Integer roundNumber
    LocalDate visitDate
    String visitLocation
    String visitLocationOther
    String visitReason
    String respondentCode
    Boolean hasInterpreter
    String interpreterName

    String nonVisitedMembers

    String gpsLat;
    String gpsLon;
    String gpsAlt;
    String gpsAcc;

    String collectedBy //fieldWorkerId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        code unique: true, nullable: false

        householdCode nullable: false
        roundNumber min: 0
        visitDate nullable: false
        visitLocation blank: true, nullable: true
        visitLocationOther blank: true, nullable: true
        visitReason blank: true, nullable: true
        respondentCode blank: true, nullable: true
        hasInterpreter nullable: true
        interpreterName blank: true, nullable: true

        nonVisitedMembers blank: true, nullable: true

        gpsAcc nullable: true
        gpsAlt nullable: true
        gpsLat nullable: true
        gpsLon nullable: true

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_visit'

        id column: "id", generator: 'assigned'

        version false

        code column: 'code'

        householdCode column: "household_code"
        visitDate column: "visit_date"
        visitLocation column: "visit_location"
        visitLocationOther column: "visit_location_other"
        visitReason column: "visit_reason"
        roundNumber column: "round_number"
        respondentCode column: "respondent_code"
        hasInterpreter column: "has_interpreter"
        interpreterName column: "interpreter_name"

        nonVisitedMembers column: "non_visited_members"

        gpsAcc column: "gps_accuracy"
        gpsAlt column: "gps_altitude"
        gpsLat column: "gps_latitude"
        gpsLon column: "gps_longitude"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        processedStatus column: "processed", enumType: "identity"
    }
}
