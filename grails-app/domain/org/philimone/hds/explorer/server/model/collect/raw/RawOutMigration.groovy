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

    String collectedBy //fieldWorkerId
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

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_outmigration'

        id column: "id", generator: 'assigned'

        version false

        memberCode column: "member_code"
        migrationType column: "migration_type"
        originCode      column: "origin_code"
        destinationCode  column: "destination_code"
        destinationOther column: "destination_other"
        migrationDate   column: "migration_date"
        migrationReason column: "migration_reason"
        visitCode column: "visit_code"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        processedStatus column: "processed", enumType: "identity"
    }
}
