package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType

import java.time.LocalDate

class HeadRelationship extends AuditableEntity {

    String id
    Household household
    Member member
    Member head
    String householdCode
    String memberCode
    String headCode
    HeadRelationshipType relationshipType
    HeadRelationshipStartType startType
    LocalDate startDate
    HeadRelationshipEndType endType
    LocalDate endDate

    static constraints = {
        id maxSize: 32
        household nullable: false
        member nullable: false
        householdCode nullable: false
        memberCode nullable: false
        headCode nullable: false
        relationshipType nullable: false
        startType nullable: false, blank:false
        startDate nullable: false
        endType nullable: false, blank:true
        endDate nullable: true
    }

    static mapping = {
        table 'head_relationship'

        id column: "id", generator: 'uuid'

        household column: "household_id"
        member column: "member_id"
        head column: "head_id"
        householdCode column: "household_code", index: "idx_household_code"
        memberCode column: "member_code", index: "idx_member_code"
        headCode column: "head_code", index: "idx_head_code"
        relationshipType column: "relationship_type", enumType: "identity"
        startType column: "start_type", enumType: "identity"
        startDate column: "start_date"
        endType column: "end_type", enumType: "identity"
        endDate column: "end_date"
    }
}
