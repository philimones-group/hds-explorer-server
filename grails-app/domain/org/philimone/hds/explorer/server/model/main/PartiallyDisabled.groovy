package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.enums.ValidatableEntity
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.PartiallyDisabledEndType

import java.time.LocalDate

/**
 * This domain class is used to store partially disabled (end event ignored) records as a backup to be restored later
 * It store data from HeadRelationship, Residency or MaritalRelationship
 * If column enabled = false, this partially disabled record wont be restored
 */
class PartiallyDisabled {

    String id
    ValidatableEntity entity
    Household household
    String householdCode
    Member member
    String memberCode
    Member spouse //if is dealing with MaritalRelationship
    String spouseCode
    PartiallyDisabledEndType endType
    LocalDate endDate

    Boolean enabled = true

    static constraints = {
        id maxSize: 32

        entity nullable: false
        household nullable: true
        householdCode nullable: true
        member nullable: false
        memberCode nullable: false
        spouse nullable: true
        spouseCode nullable: true
        endType nullable: true
        endDate nullable: true

        enabled nullable: false
    }

    static mapping = {
        table '_partially_disabled'

        id column: "id", generator: 'assigned'
        version false

        entity column: 'entity', enumType: "identity", index: "idx_entity"
        household column: 'household_id'
        householdCode column: 'household_code', index: "idx_house_code"
        member column: 'member_id'
        memberCode column: 'member_code', index: "idx_member_code"
        spouse column: 'spouse_id'
        spouseCode column: 'spouse_code', index: "idx_spouse_code"
        endType column: 'end_type', enumType: "identity"
        endDate column: 'end_date'

        enabled column: 'enabled'
    }
}
