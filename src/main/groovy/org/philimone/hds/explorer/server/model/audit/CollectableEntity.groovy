package org.philimone.hds.explorer.server.model.audit

import grails.gorm.dirty.checking.DirtyCheck
import org.philimone.hds.explorer.server.model.authentication.User

import java.time.LocalDateTime

@DirtyCheck
abstract class CollectableEntity extends AuditableEntity {

    String collectedId
    User collectedBy
    LocalDateTime collectedDate

    static constraints = {
        collectedId nullable:true
        collectedBy nullable:true
        collectedDate nullable:true
    }

    static mapping = {
        collectedId column: "collected_uuid"
        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
    }

}
