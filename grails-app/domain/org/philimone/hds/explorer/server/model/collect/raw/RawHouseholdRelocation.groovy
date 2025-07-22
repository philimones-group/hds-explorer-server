package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawHouseholdRelocation {

    String id

    String visitCode
    String originCode
    String destinationCode
    String headCode
    LocalDate eventDate
    String reason
    String reasonOther

    byte[] extensionForm

    String collectedBy //fieldWorkerId
    String collectedDeviceId
    String collectedHouseholdId
    String collectedMemberId
    LocalDateTime collectedStart
    LocalDateTime collectedEnd
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED /* General Status, 1-All Processed, 0-Not All Processed*/

    boolean postExecution = false

    static constraints = {
        id maxSize: 32

        visitCode nullable: false
        originCode nullable: false
        destinationCode nullable: false
        headCode nullable: true, blank:true
        eventDate nullable: false
        reason nullable: true
        reasonOther nullable: true, blank: true

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
        table '_raw_household_relocation'

        id column: "id", generator: 'assigned'

        visitCode column: "visit_code", index: "idx_visit_code"
        originCode column: "origin_code", index: "idx_origin_hhcode"
        destinationCode column: "destination_code", index: "idx_destination_hhcode"
        headCode column: "head_code", index: "idx_headcode"

        eventDate column: "event_date"
        reason column: "reason"
        reasonOther column: "reason_other"

        extensionForm column: "extension_form", sqlType: "mediumblob"

        collectedBy column: "collected_by", index: "idx_coll_by"
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


