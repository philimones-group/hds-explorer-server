package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawChangeRegionHead {

    String id

    String visitCode
    String regionCode
    String oldHeadCode
    String newHeadCode
    LocalDate eventDate
    String reason

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

        visitCode nullable: true
        regionCode nullable: false
        oldHeadCode nullable: true, blank:true
        newHeadCode nullable: false, blank:false
        eventDate nullable: false

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
        table '_raw_change_region_head'

        id column: "id", generator: 'assigned'

        visitCode column: "visit_code", index: "idx_visit_code"
        regionCode column: "region_code", index: "idx_region_code"
        oldHeadCode column: "old_head_code", index: "idx_old_headcode"
        newHeadCode column: "new_head_code", index: "idx_new_headcode"
        eventDate column: "event_date"
        reason column: "reason"

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


