package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType

/**
 * Residency stores the information about a Member/Individual thats lives
 */
class Residency extends AuditableEntity {

    Household household
    Member member
    String householdCode
    String memberCode
    Integer order
    ResidencyStartType startType
    Date startDate
    ResidencyEndType endType
    Date endDate

    static constraints = {
        household nullable: false
        member nullable: false
        householdCode blank: false
        memberCode blank: false
        order min: 1
        startType nullable: false
        startDate nullable: false
        endType nullable: false, blank:true
        endDate nullable: true
    }

    static mapping = {
        table 'residency'

        household column: "household_id"
        member column: "member_id"
        householdCode column: "household_code"
        memberCode column: "member_code"
        order column: "order"
        startType column: "start_type", enumType: "identity"
        startDate column: "start_date"
        endType column: "end_type", enumType: "identity"
        endDate column: "end_date"
    }

}
