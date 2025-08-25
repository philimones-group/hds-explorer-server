package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.ProxyHeadRole
import org.philimone.hds.explorer.server.model.enums.ProxyHeadChangeReason
import org.philimone.hds.explorer.server.model.enums.ProxyHeadType
import org.philimone.hds.explorer.server.model.enums.ValidatableStatus

import java.time.LocalDate

class HouseholdProxyHead extends CollectableEntity {

    String id
    Visit visit
    String visitCode
    Household household
    String householdCode
    ProxyHeadType proxyHeadType
    Member proxyHead
    String proxyHeadCode
    String proxyHeadName
    ProxyHeadRole proxyHeadRole

    LocalDate startDate
    LocalDate endDate

    ProxyHeadChangeReason reason
    String reasonOther

    ValidatableStatus status

    static constraints = {
        id maxSize: 32

        visit nullable: false
        visitCode nullable: false, blank: false

        household nullable: false
        householdCode nullable: false
        proxyHeadType nullable: false
        proxyHead nullable: true
        proxyHeadCode nullable: true
        proxyHeadName nullable: false
        proxyHeadRole nullable: false

        startDate nullable: false
        endDate nullable: true
        reason nullable: true
        reasonOther nullable: true

        status nullable: true
    }

    static mapping = {
        table 'household_proxy_head'

        id column: "id", generator: 'uuid'

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"

        household column: "household_id"
        householdCode column: "household_code", index: "idx_hh_household_code"
        proxyHeadType column: 'proxy_head_type', enumType: 'identity', index: "idx_hh_proxy_type"
        proxyHead column: 'proxy_head_member_id'
        proxyHeadCode column: 'proxy_head_code', index: 'idx_hh_proxy_code'
        proxyHeadName column: 'proxy_head_name'

        proxyHeadRole column: 'proxy_head_role', enumType: 'identity', index: 'idx_hh_proxy_role'

        startDate column: 'start_date'
        endDate column: 'end_date'
        reason column: 'reason', enumType: 'identity', index: 'idx_hh_start_reason'
        reasonOther column: 'reason_other'

        status column: 'status', enumType: 'identity'

        // Helpful composite index to fetch the current proxy quickly (end_date IS NULL)
        // (MySQL will still use it effectively for end_date conditions)
        index 'idx_hh_proxy_current', ['household_id', 'end_date']
    }
}
