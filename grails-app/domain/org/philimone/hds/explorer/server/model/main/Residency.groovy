package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.ValidatableStatus
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

    ValidatableStatus status

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

        status nullable: true
    }

    static mapping = {
        table 'residency'

        id column: "id", generator: 'uuid'

        // Composite Index: idx_res_member_start_stat (member_id, start_date DESC, status) - Fast latest residency lookup
        // Composite Index: idx_res_dates_status (start_date, end_date, status) - Reference date active resident lookup
        household column: "household_id"
        member column: "member_id", index: "idx_res_member_start_stat"
        householdCode column: "household_code", index: "idx_household_code"
        memberCode column: "member_code", index: "idx_member_code"
        startType column: "start_type", enumType: "identity"
        startDate column: "start_date", index: "idx_res_member_start_stat,idx_res_dates_status"
        endType column: "end_type", enumType: "identity"
        endDate column: "end_date", index: "idx_res_dates_status"

        status column: "status", enumType: "identity", index: "idx_res_member_start_stat,idx_res_dates_status"
    }

}
