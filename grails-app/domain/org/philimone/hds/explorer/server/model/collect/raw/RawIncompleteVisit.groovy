package org.philimone.hds.explorer.server.model.collect.raw


import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDateTime

class RawIncompleteVisit {

    String id

    String visitCode
    String householdCode
    String memberCode
    String reason
    String reasonOther

    String collectedBy //fieldWorkerId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        visitCode nullable: true
        householdCode nullable: true
        memberCode nullable: true
        reason nullable: true
        reasonOther nullable: true, blank: true

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table "_raw_incomplete_visit"

        id column: "id", generator: "assigned"

        visitCode column: "visit_code"
        householdCode column: "household_code"
        memberCode column: "member_code"
        reason column: "visit_reason", enumType: "identity"
        reasonOther column: "other_visit_reason"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        processedStatus column: "processed", enumType: "identity"
    }
}
