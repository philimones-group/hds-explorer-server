package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawVisit {

    String id

    String code
    String householdCode
    LocalDate visitDate
    String visitLocation
    String visitLocationOther
    Integer roundNumber
    String respondentCode
    Boolean hasInterpreter
    String interpreterName
    String gpsAccuracy;
    String gpsAltitude;
    String gpsLatitude;
    String gpsLongitude;

    String collectedBy //fieldWorkerId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        code unique: true, nullable: false

        householdCode nullable: false
        visitDate nullable: false
        visitLocation blank: true, nullable: true
        visitLocationOther blank: true, nullable: true
        roundNumber min: 0
        respondentCode blank: true, nullable: true
        hasInterpreter nullable: true
        interpreterName blank: true, nullable: true
        gpsAccuracy nullable: true
        gpsAltitude nullable: true
        gpsLatitude nullable: true
        gpsLongitude nullable: true

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_visit'

        id column: "id", generator: 'uuid'

        version false

        code column: 'code'

        householdCode column: "household_code"
        visitDate column: "visit_date"
        visitLocation column: "visit_location"
        visitLocationOther column: "visit_location_other"
        roundNumber column: "round_number"
        respondentCode column: "respondent_code"
        hasInterpreter column: "has_interpreter"
        interpreterName column: "interpreter_name"
        gpsAccuracy column: "gps_accuracy"
        gpsAltitude column: "gps_altitude"
        gpsLatitude column: "gps_latitude"
        gpsLongitude column: "gps_longitude"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        processedStatus column: "processed", enumType: "identity"
    }
}
