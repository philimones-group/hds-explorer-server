package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType

import java.time.LocalDate

/**
 * Residency stores the information about a Member/Individual thats lives
 */
class Residency extends AuditableEntity {

    String id
    Household household
    Member member
    String householdCode
    String memberCode
    ResidencyStartType startType
    LocalDate startDate
    ResidencyEndType endType
    LocalDate endDate

    static constraints = {
        id maxSize: 32
        household nullable: false
        member nullable: false
        householdCode blank: false
        memberCode blank: false
        startType nullable: false
        startDate nullable: false
        endType nullable: false, blank:true
        endDate nullable: true
    }

    static mapping = {
        table 'residency'

        id column: "id", generator: 'uuid'

        household column: "household_id"
        member column: "member_id"
        householdCode column: "household_code", index: "idx_household_code"
        memberCode column: "member_code", index: "idx_member_code"
        startType column: "start_type", enumType: "identity"
        startDate column: "start_date"
        endType column: "end_type", enumType: "identity"
        endDate column: "end_date"
    }

}
