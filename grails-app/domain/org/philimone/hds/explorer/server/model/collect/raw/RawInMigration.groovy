package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawInMigration {

    String id

    String visitCode
    String memberCode

    String education
    String religion
    String phonePrimary
    String phoneAlternative

    String headRelationshipType
    String migrationType
    String extMigrationType
    String originCode
    String originOther
    String destinationCode
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

    boolean isHouseholdRelocation = false

    static transients = ['isHouseholdRelocation']

    static constraints = {
        id maxSize: 32

        visitCode blank: false, nullable: false
        memberCode blank: false, nullable: false

        headRelationshipType nullable: true
        education blank: true, nullable: true
        religion blank: true, nullable: true
        phonePrimary blank: true, nullable: true
        phoneAlternative blank: true, nullable: true

        migrationType nullable: false, blank: false
        extMigrationType nullable: true, blank: true
        originCode nullable: true, blank: true
        originOther nullable: true, blank: true
        destinationCode nullable: false, blank: false
        migrationDate nullable: false
        migrationReason nullable: true, blank: true

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
        table '_raw_inmigration'

        id column: "id", generator: 'assigned'

        version false

        visitCode column: "visit_code", index: "idx_visit_code"
        memberCode column: "member_code", index: "idx_member_code"

        education column: "education"
        religion column: "religion"
        phonePrimary column: "phone_primary"
        phoneAlternative column: "phone_alternative"

        headRelationshipType column: "head_relationship_type"
        migrationType       column: "migration_type"
        extMigrationType column: "ext_migration_type"
        originCode  column: "origin_code", index: "idx_origin_code"
        originOther column: "origin_other"
        destinationCode      column: "destination_code", index: "idx_destination_code"
        migrationDate   column: "migration_date"
        migrationReason column: "migration_reason"

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


