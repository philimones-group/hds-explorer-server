package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType

class HeadRelationship extends CollectableEntity {

    Household household
    Member member
    Member head
    String householdCode
    String memberCode
    String headCode
    HeadRelationshipType relationshipType
    Integer order
    HeadRelationshipStartType startType
    Date startDate
    HeadRelationshipEndType endType
    Date endDate

    static constraints = {
        household nullable: false
        member nullable: false
        householdCode nullable: false
        memberCode nullable: false
        headCode nullable: false
        relationshipType nullable: false
        order min: 1
        startType nullable: false, blank:false
        startDate nullable: false
        endType nullable: false, blank:true
        endDate nullable: true
    }

    static mapping = {
        datasource 'main'
        table 'head_relationship'

        household column: "household_id"
        member column: "member_id"
        householdCode column: "household_code"
        memberCode column: "member_code"
        headCode column: "head_code"
        relationshipType column: "relationship_type", enumType: "identity"
        order column: "order"
        startType column: "start_type", enumType: "identity"
        startDate column: "start_date"
        endType column: "end_type", enumType: "identity"
        endDate column: "end_date"
    }
}
