package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

class RawHeadRelationship {

    String id
    String householdCode
    String memberCode
    String headCode
    String relationshipType
    String startType
    Date startDate
    String endType
    Date endDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED /* General Status, 1-All Processed, 0-Not All Processed*/

    static constraints = {
        id maxSize: 32
        householdCode nullable: false
        memberCode nullable: false
        headCode nullable: false
        relationshipType nullable: false
        startType nullable: false, blank:false
        startDate nullable: false
        endType nullable: false, blank:true
        endDate nullable: true

        processedStatus nullable: true
    }

    static mapping = {
        table '_raw_head_relationship'

        id column: "id", generator: 'uuid'

        householdCode column: "household_code"
        memberCode column: "member_code"
        headCode column: "head_code"
        relationshipType column: "relationship_type"
        startType column: "start_type"
        startDate column: "start_date"
        endType column: "end_type"
        endDate column: "end_date"

        processedStatus column: "processed", enumType: "identity"
    }
}