package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawMaritalRelationship {

    String id
    String memberA
    String memberB
    String startStatus
    LocalDate startDate
    String endStatus
    LocalDate endDate

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

        memberA nullable: false
        memberB nullable: false

        startStatus nullable: true, blank:false, enumType: "identity"
        startDate nullable: true
        endStatus nullable: true, blank:true, enumType: "identity"
        endDate nullable: true

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
        table '_raw_marital_relationship'

        id column: "id", generator: 'assigned'

        memberA column: "member_a_code", index: "idx_member_a_code"
        memberB column: "member_b_code", index: "idx_member_b_code"
        startStatus column: "start_status"
        startDate column: "start_date"
        endStatus column: "end_status"
        endDate column: "end_date"

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


