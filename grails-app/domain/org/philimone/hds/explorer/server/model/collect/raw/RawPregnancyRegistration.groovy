package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.EstimatedDateOfDeliveryType
import org.philimone.hds.explorer.server.model.enums.PregnancyStatus
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.Visit

import java.time.LocalDate
import java.time.LocalDateTime

class RawPregnancyRegistration {

    String id
    String code
    String motherCode
    LocalDate recordedDate
    Integer pregMonths
    Boolean eddKnown
    Boolean hasPrenatalRecord
    LocalDate eddDate
    String eddType
    Boolean lmpKnown
    LocalDate lmpDate
    LocalDate expectedDeliveryDate
    String status
    String visitCode

    String collectedBy
    String collectedDeviceId
    String collectedHouseholdId
    String collectedMemberId
    LocalDateTime collectedStart
    LocalDateTime collectedEnd
    LocalDateTime collectedDate
    LocalDateTime uploadedDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        code nullable: false
        motherCode nullable: false, blank: false
        recordedDate nullable: false
        pregMonths nullable: true
        eddKnown nullable: true
        hasPrenatalRecord nullable: true
        eddDate nullable: true
        eddType nullable: true
        lmpKnown nullable: true
        lmpDate nullable: true
        expectedDeliveryDate nullable: true
        status nullable: false
        visitCode blank: false

        collectedBy blank: true
        collectedDeviceId nullable:true
        collectedHouseholdId nullable:true
        collectedMemberId nullable:true
        collectedStart nullable:true
        collectedEnd nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true
    }

    static mapping = {
        table '_raw_pregnancy_registration'

        id column: "id", generator: 'assigned'

        code column: "code", index: "idx_code"
        motherCode column: "mother_code", index: "idx_mother_code"
        recordedDate column: "recorded_date"

        pregMonths column: "preg_months"
        eddKnown column: "edd_known"
        hasPrenatalRecord column: "has_precord"
        eddDate column: "edd_date"
        eddType column: "edd_type"
        lmpKnown column: "lmp_known"
        lmpDate column: "lmp_date"
        expectedDeliveryDate column: "expected_delivery_date"
        status column: "status"

        visitCode column: "visit_code", index: "idx_visit_code"

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


