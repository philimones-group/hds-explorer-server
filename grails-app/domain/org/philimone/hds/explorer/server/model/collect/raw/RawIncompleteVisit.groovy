package org.philimone.hds.explorer.server.model.collect.raw


import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDateTime

class RawIncompleteVisit {

    String id

    String visitCode
    String householdCode
    String memberCode
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

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        visitCode nullable: true
        householdCode nullable: true
        memberCode nullable: true
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
        table "_raw_incomplete_visit"

        id column: "id", generator: "assigned"

        visitCode column: "visit_code", index: "idx_visit_code"
        householdCode column: "household_code", index: "idx_household_code"
        memberCode column: "member_code", index: "idx_member_code"
        reason column: "visit_reason", enumType: "identity"
        reasonOther column: "other_visit_reason"

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


