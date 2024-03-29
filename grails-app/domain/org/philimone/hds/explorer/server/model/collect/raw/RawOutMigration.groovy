package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawOutMigration {

    String id

    String visitCode
    String memberCode
    String migrationType
    String originCode
    String destinationCode
    String destinationOther
    LocalDate migrationDate
    String migrationReason

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

        memberCode blank: false, nullable: false
        migrationType nullable: false, blank: false
        originCode nullable: false, blank: false
        destinationCode nullable: true, blank: true
        destinationOther nullable: true, blank: true
        migrationDate nullable: false
        migrationReason nullable: true, blank: true
        visitCode blank: false, nullable: false

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
        table '_raw_outmigration'

        id column: "id", generator: 'assigned'

        version false

        memberCode column: "member_code", index: "idx_member_code"
        migrationType column: "migration_type"
        originCode      column: "origin_code", index: "idx_origin_code"
        destinationCode  column: "destination_code", index: "idx_destination_code"
        destinationOther column: "destination_other"
        migrationDate   column: "migration_date"
        migrationReason column: "migration_reason"
        visitCode column: "visit_code", index: "idx_visit_code"

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


