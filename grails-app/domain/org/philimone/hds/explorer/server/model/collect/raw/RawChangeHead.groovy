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

        id column: "id", generator: 'assigned'

        visitCode column: "visit_code"
        householdCode column: "household_code"
        oldHeadCode column: "old_head_code"
        newHeadCode column: "new_head_code"
        eventDate column: "event_date"
        reason column: "reason"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}