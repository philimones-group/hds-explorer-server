package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate

class RawHeadRelationship {

    String id
    String householdCode
    String memberCode
    //String headCode /* We should not specify the head here, it will be specified automatically in main.HeadRelationship domain */
    String relationshipType
    String startType
    LocalDate startDate
    String endType
    LocalDate endDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED /* General Status, 1-All Processed, 0-Not All Processed*/

    boolean postExecution = false

    boolean isHouseholdRelocation = false

    static transients = ['isHouseholdRelocation']

    static constraints = {
        id maxSize: 32
        householdCode nullable: false
        memberCode nullable: false
        //headCode nullable: false
        relationshipType nullable: false
        startType nullable: false, blank:false
        startDate nullable: false
        endType nullable: false, blank:true
        endDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_head_relationship'

        id column: "id", generator: 'assigned'

        householdCode column: "household_code", index: "idx_household_code"
        memberCode column: "member_code", index: "idx_member_code"
        //headCode column: "head_code"
        relationshipType column: "relationship_type"
        startType column: "start_type"
        startDate column: "start_date"
        endType column: "end_type"
        endDate column: "end_date"

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}
