package org.philimone.hds.explorer.server.model.audit

import grails.gorm.dirty.checking.DirtyCheck
import org.philimone.hds.explorer.server.model.authentication.User

import java.time.LocalDateTime

@DirtyCheck
abstract class CollectableEntity extends AuditableEntity {

    String collectedId
    User collectedBy
    String collectedDeviceId
    String collectedHouseholdId
    String collectedMemberId
    LocalDateTime collectedStart
    LocalDateTime collectedEnd
    LocalDateTime collectedDate

    static constraints = {
        collectedId nullable:true, unique:true
        collectedBy nullable:true
        collectedDeviceId nullable:true
        collectedHouseholdId nullable:true
        collectedMemberId nullable:true
        collectedStart nullable:true
        collectedEnd nullable:true
        collectedDate nullable:true
    }

    static mapping = {
        collectedId column: "collected_uuid"
        collectedBy column: "collected_by"
        collectedDeviceId column: "collected_device_id", index: "idx_cdeviceid"
        collectedHouseholdId column: "collected_household_id", index: "idx_chouseid"
        collectedMemberId column: "collected_member_id", index: "idx_cmemberid"
        collectedStart column: "collected_start"
        collectedEnd column: "collected_end"
        collectedDate column: "collected_date"
    }

}