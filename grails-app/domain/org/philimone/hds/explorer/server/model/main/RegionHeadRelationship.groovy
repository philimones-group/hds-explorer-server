package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.enums.ValidatableStatus
import org.philimone.hds.explorer.server.model.enums.temporal.RegionHeadEndType
import org.philimone.hds.explorer.server.model.enums.temporal.RegionHeadStartType

import java.time.LocalDate

class RegionHeadRelationship extends AuditableEntity {

    String id
    Region region
    String regionCode
    Member head
    String headCode
    RegionHeadStartType startType
    LocalDate startDate
    RegionHeadEndType endType = RegionHeadEndType.NOT_APPLICABLE
    LocalDate endDate
    String reason

    ValidatableStatus status

    static constraints = {
        id maxSize: 32
        region nullable: false
        regionCode nullable: false
        head nullable: false
        headCode nullable: false
        startType nullable: false
        startDate nullable: false
        endType nullable: false
        endDate nullable: true
        reason nullable: true

        status nullable: true
    }

    static mapping = {
        table 'region_head_relationship'

        id column: "id", generator: 'uuid'

        region column: "region_id"
        regionCode column: "region_code", index: "idx_region_code"
        head column: "head_id"
        headCode column: "head_code", index: "idx_head_code"
        startType column: "start_type", enumType: "identity"
        startDate column: "start_date"
        endType column: "end_type", enumType: "identity"
        endDate column: "end_date"
        reason column: "reason"

        status column: "status", enumType: "identity"
    }
}
