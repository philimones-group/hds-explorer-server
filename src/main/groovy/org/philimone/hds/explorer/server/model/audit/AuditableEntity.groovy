package org.philimone.hds.explorer.server.model.audit

import grails.gorm.dirty.checking.DirtyCheck
import org.philimone.hds.explorer.server.model.authentication.User

import java.time.LocalDateTime

@DirtyCheck
abstract class AuditableEntity {

    //User collectedBy
    //Date collectedDate

    abstract String getId()

    User createdBy
    LocalDateTime createdDate
    User updatedBy
    LocalDateTime updatedDate

    static constraints = {
        createdBy nullable: true
        createdDate nullable: true
        updatedBy nullable: true
        updatedDate nullable: true
    }

    static mapping = {
        //version false

        createdBy column:"created_by"
        createdDate column: "created_date"
        updatedBy column: "updated_by"
        updatedDate column: "updated_date"
    }
}
