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
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_death'

        id column: "id", generator: 'assigned'

        version false

        memberCode column: "member_code"
        deathDate column: "death_date"
        deathCause column: "death_cause"
        deathPlace column: "death_place"
        visitCode column: "visit_code"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        processedStatus column: "processed", enumType: "identity"
    }
}

class RawDeathRelationship {

    String id

    RawDeath death
    String newMemberCode
    String newRelationshipType

    static belongsTo = [death:RawDeath]

    static constraints = {
        id maxSize: 32

        death nullable: false
        newMemberCode nullable: false
        newRelationshipType nullable: false
    }

    static mapping = {
        table '_raw_death_relationships'

        id column: "id", generator: 'uuid'

        death column: "death_id"
        newMemberCode column: "new_member_code"
        newRelationshipType column: "new_relationship_type"
    }
}
