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

    String headRelationshipType

    String migrationType = InMigrationType.EXTERNAL.code
    String extMigrationType //ENTRY OR REENTRY

    String originCode
    String originOther
    String destinationCode
    LocalDate migrationDate
    String migrationReason

    String collectedBy //fieldWorkerId
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

        migrationType nullable: false, blank: false
        extMigrationType nullable: false, blank: false

        originCode nullable: true, blank: true
        originOther nullable: true, blank: true
        destinationCode nullable: false, blank: false
        migrationDate nullable: false
        migrationReason nullable: true, blank: true
        visitCode blank: false, nullable: false

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_ext_inmigration'

        id column: "id", generator: 'uuid'

        version false

        memberCode column: "member_code"
        memberName column: "member_name"
        memberGender column: "member_gender"
        memberDob column: "member_dob"
        memberMotherCode column: "member_mother_code"
        memberFatherCode column: "member_father_code"

        migrationType       column: "migration_type"
        extMigrationType column: "ext_migration_type"

        originCode  column: "origin_code"
        originOther column: "origin_other"
        destinationCode      column: "destination_code"
        migrationDate   column: "migration_date"
        migrationReason column: "migration_reason"
        visitCode column: "visit_code"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        processedStatus column: "processed", enumType: "identity"
    }
}