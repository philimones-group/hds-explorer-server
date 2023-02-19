package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawInMigration {

    String id

    String visitCode
    String memberCode
    String headRelationshipType
    String migrationType
    String extMigrationType
    String originCode
    String originOther
    String destinationCode
    LocalDate migrationDate
    String migrationReason

    String collectedBy //fieldWorkerId
    String collectedDeviceId
    String collectedHouseholdId
    String collectedMemberId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        memberCode blank: false, nullable: false
        migrationType nullable: false, blank: false
        extMigrationType nullable: true, blank: true
        originCode nullable: true, blank: true
        originOther nullable: true, blank: true
        destinationCode nullable: false, blank: false
        migrationDate nullable: false
        migrationReason nullable: true, blank: true
        visitCode blank: false, nullable: false

        collectedBy blank: true
        collectedDeviceId nullable:true
        collectedHouseholdId nullable:true
        collectedMemberId nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_inmigration'

        id column: "id", generator: 'assigned'

        version false

        visitCode column: "visit_code", index: "idx_visit_code"
        memberCode column: "member_code", index: "idx_member_code"
        headRelationshipType column: "head_relationship_type"
        migrationType       column: "migration_type"
        extMigrationType column: "ext_migration_type"
        originCode  column: "origin_code", index: "idx_origin_code"
        originOther column: "origin_other"
        destinationCode      column: "destination_code", index: "idx_destination_code"
        migrationDate   column: "migration_date"
        migrationReason column: "migration_reason"

        collectedBy column: "collected_by", index: "idx_collected_by"
        collectedDeviceId column: "collected_device_id", index: "idx_cdeviceid"
        collectedHouseholdId column: "collected_household_id", index: "idx_chouseid"
        collectedMemberId column: "collected_member_id", index: "idx_cmemberid"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}

