package org.philimone.hds.explorer.server.model.audit

import grails.gorm.dirty.checking.DirtyCheck
import org.philimone.hds.explorer.server.model.authentication.User

import java.time.LocalDateTime

@DirtyCheck
abstract class CollectableEntity extends AuditableEntity {

    User collectedBy
    LocalDateTime collectedDate

    static constraints = {
        collectedBy nullable:true
        collectedDate nullable:true
    }

    static mapping = {
        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
    }

}
