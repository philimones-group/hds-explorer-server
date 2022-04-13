package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawChangeHead {

    String id

    String visitCode
    String householdCode
    String oldHeadCode
    String newHeadCode
    LocalDate eventDate
    String reason

    String collectedBy //fieldWorkerId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED /* General Status, 1-All Processed, 0-Not All Processed*/

    boolean postExecution = false

    static hasMany = [relationships:RawChangeHeadRelationship]

    static constraints = {
        id maxSize: 32

        visitCode nullable: false
        householdCode nullable: false
        oldHeadCode nullable: false, blank:false
        newHeadCode nullable: false, blank:false
        eventDate nullable: false

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_change_head'

        id column: "id", generator: 'uuid'

        visitCode column: "visit_code"
        householdCode column: "household_code"
        oldHeadCode column: "old_head_code"
        newHeadCode column: "new_head_code"
        eventDate column: "event_date"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        processedStatus column: "processed", enumType: "identity"
    }
}

class RawChangeHeadRelationship {

    String id

    RawChangeHead changeHead
    String newMemberCode
    String newRelationshipType

    static belongsTo = [changeHead:RawChangeHead]

    static constraints = {
        id maxSize: 32

        changeHead nullable: false
        newMemberCode nullable: false
        newRelationshipType nullable: false
    }

    static mapping = {
        table '_raw_change_head_relationship'

        id column: "id", generator: 'uuid'

        changeHead column: "change_head_id"
        newMemberCode column: "new_member_code"
        newRelationshipType column: "new_relationship_type"
    }
}