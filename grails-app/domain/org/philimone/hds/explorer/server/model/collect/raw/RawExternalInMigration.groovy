package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

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

    String migrationType
    String originCode
    String originOther
    String destinationCode
    LocalDate migrationDate
    String migrationReason

    String collectedBy //fieldWorkerId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    static constraints = {
        id maxSize: 32

        memberCode blank: false, nullable: false
        migrationType nullable: false, blank: false
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
        table '_raw_inmigration'

        id column: "id", generator: 'assigned'

        version false

        memberCode column: "member_code"
        migrationType       column: "migration_type"
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
