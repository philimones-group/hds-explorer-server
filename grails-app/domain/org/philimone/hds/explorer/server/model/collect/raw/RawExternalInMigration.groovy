package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.temporal.InMigrationType

import java.time.LocalDate
import java.time.LocalDateTime

class RawExternalInMigration {

    String id

    String visitCode

    String memberCode
    String memberName
    String memberGender
    LocalDate memberDob
    String memberMotherCode
    String memberFatherCode

    String education
    String religion

    String headRelationshipType

    String migrationType = InMigrationType.EXTERNAL.code
    String extMigrationType //ENTRY OR REENTRY

    String originCode
    String originOther
    String destinationCode
    LocalDate migrationDate
    String migrationReason

    String modules

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
        memberName blank: true, nullable: true
        memberGender blank: true, nullable: true
        memberDob nullable: true
        memberMotherCode blank: true, nullable: true
        memberFatherCode blank: true, nullable: true

        education blank: true, nullable: true
        religion blank: true, nullable: true

        migrationType nullable: false, blank: false
        extMigrationType nullable: false, blank: false

        originCode nullable: true, blank: true
        originOther nullable: true, blank: true
        destinationCode nullable: false, blank: false
        migrationDate nullable: false
        migrationReason nullable: true, blank: true
        visitCode blank: false, nullable: false

        modules nullable: true

        collectedBy blank: true
        collectedDeviceId nullable:true
        collectedHouseholdId nullable:true
        collectedMemberId nullable:true
        collectedStart nullable:true
        collectedEnd nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true

        postExecution column: "post_execution"

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_ext_inmigration'

        id column: "id", generator: 'assigned'

        version false

        visitCode column: "visit_code", index: "idx_visit_code"

        memberCode column: "member_code", index: "idx_member_code"
        memberName column: "member_name"
        memberGender column: "member_gender"
        memberDob column: "member_dob"
        memberMotherCode column: "member_mother_code", index: "idx_member_mother_code"
        memberFatherCode column: "member_father_code", index: "idx_member_father_code"

        education column: "education"
        religion column: "religion"

        headRelationshipType column: "head_relationship_type"

        migrationType       column: "migration_type"
        extMigrationType column: "ext_migration_type"

        originCode  column: "origin_code", index: "idx_origin_code"
        originOther column: "origin_other"
        destinationCode      column: "destination_code", index: "idx_destination_code"
        migrationDate   column: "migration_date"
        migrationReason column: "migration_reason"

        modules column: "modules"

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


