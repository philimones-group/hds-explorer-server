package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawDeath {

    String id

    String visitCode
    String memberCode
    LocalDate deathDate
    String deathCause
    String deathPlace

    String collectedBy //fieldWorkerId
    String collectedDeviceId
    String collectedHouseholdId
    String collectedMemberId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false

    static hasMany = [relationships:RawDeathRelationship]

    static constraints = {
        id maxSize: 32

        memberCode blank: false, nullable: false
        deathDate nullable: false
        deathCause nullable: true, blank: true
        deathPlace nullable: true, blank: true
        visitCode nullable: false, blank: false

        collectedBy blank: true
        collectedDeviceId nullable:true
        collectedHouseholdId nullable:true
        collectedMemberId nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_death'

        id column: "id", generator: 'assigned'

        version false

        visitCode column: "visit_code", index: "idx_visit_code"
        memberCode column: "member_code", index: "idx_member_code"
        deathDate column: "death_date"
        deathCause column: "death_cause"
        deathPlace column: "death_place"

        collectedBy column: "collected_by", index: "idx_collby"
        collectedDeviceId column: "collected_device_id", index: "idx_cdeviceid"
        collectedHouseholdId column: "collected_household_id", index: "idx_chouseid"
        collectedMemberId column: "collected_member_id", index: "idx_cmemberid"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}

