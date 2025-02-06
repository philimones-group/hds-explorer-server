package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.NoVisitReason
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
    String visitReasonOther;
    Boolean visitPossible
    String visitNotPossibleReason
    String otherNotPossibleReason
    Boolean respondentResident
    String respondentRelationship
    String respondentCode
    String respondentName
    Boolean hasInterpreter
    String interpreterName

    String nonVisitedMembers

    String gpsLat
    String gpsLon
    String gpsAlt
    String gpsAcc

    byte[] extensionForm

    String collectedBy //fieldWorkerId
    String collectedDeviceId
    String collectedHouseholdId
    String collectedMemberId
    LocalDateTime collectedStart
    LocalDateTime collectedEnd
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        code nullable: false

        householdCode nullable: false
        roundNumber min: 0
        visitDate nullable: false
        visitLocation blank: true, nullable: true
        visitLocationOther blank: true, nullable: true
        visitReason blank: true, nullable: true
        visitReasonOther blank: true, nullable: true

        visitPossible nullable: true
        visitNotPossibleReason nullable: true
        otherNotPossibleReason nullable: true
        respondentResident nullable: true
        respondentRelationship blank: true, nullable: true
        respondentCode blank: true, nullable: true
        respondentName blank: true, nullable: true

        hasInterpreter nullable: true
        interpreterName blank: true, nullable: true

        nonVisitedMembers blank: true, nullable: true, maxSize: 1000

        gpsAcc nullable: true
        gpsAlt nullable: true
        gpsLat nullable: true
        gpsLon nullable: true

        extensionForm nullable: true

        collectedBy blank: true
        collectedDeviceId nullable:true
        collectedHouseholdId nullable:true
        collectedMemberId nullable:true
        collectedStart nullable:true
        collectedEnd nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_visit'

        id column: "id", generator: 'assigned'

        version false

        code column: 'code', index: "idx_code"

        householdCode column: "household_code", index: "idx_household_code"
        visitDate column: "visit_date"
        visitLocation column: "visit_location"
        visitLocationOther column: "visit_location_other"
        visitReason column: "visit_reason"
        visitReasonOther column: "visit_reason_other"
        roundNumber column: "round_number"

        visitPossible column: "visit_possible"
        visitNotPossibleReason column: "visit_not_possible_reason", enumType: "identity", index: "idx_novisit_reason"
        otherNotPossibleReason column: "other_not_possible_reason"
        respondentResident column: "respondent_resident"
        respondentRelationship column: "respondent_relationship", index: "idx_respondent_relat"
        respondentCode column: "respondent_code", index: "idx_respondent_code"
        respondentName column: "respondent_name"

        hasInterpreter column: "has_interpreter"
        interpreterName column: "interpreter_name"

        nonVisitedMembers column: "non_visited_members"

        gpsAcc column: "gps_accuracy"
        gpsAlt column: "gps_altitude"
        gpsLat column: "gps_latitude"
        gpsLon column: "gps_longitude"

        extensionForm column: "extension_form", sqlType: "mediumblob"

        collectedBy column: "collected_by", index: "idx_collected_by"
        collectedDeviceId column: "collected_device_id", index: "idx_cdeviceid"
        collectedHouseholdId column: "collected_household_id", index: "idx_chouseid"
        collectedMemberId column: "collected_member_id", index: "idx_cmemberid"
        collectedStart column: "collected_start"
        collectedEnd column: "collected_end"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}


