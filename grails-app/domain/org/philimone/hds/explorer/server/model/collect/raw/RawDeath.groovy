package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

class RawDeath {

    String id

    String visitCode
    String memberCode
    Date deathDate
    String deathCause
    String deathPlace

    String collectedBy //fieldWorkerId
    Date collectedDate //visitDate / creationDate
    Date uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

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
