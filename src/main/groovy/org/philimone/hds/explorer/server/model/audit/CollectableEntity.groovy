package org.philimone.hds.explorer.server.model.audit

import grails.gorm.dirty.checking.DirtyCheck
import org.philimone.hds.explorer.server.model.authentication.User

@DirtyCheck
abstract class CollectableEntity extends AuditableEntity {

    User collectedBy
    Date collectedDate

    static constraints = {
        collectedBy nullable:true
        collectedDate nullable:true
    }

    static mapping = {
        datasource 'main'

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
    }

}
