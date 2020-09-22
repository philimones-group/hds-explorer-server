package org.philimone.hds.explorer.server.model.audit

import grails.gorm.dirty.checking.DirtyCheck
import groovy.transform.SelfType
import org.grails.datastore.gorm.GormEntity
import org.philimone.hds.explorer.server.model.authentication.User

@DirtyCheck
abstract class AuditableEntity {

    //User collectedBy
    //Date collectedDate

    User createdBy
    Date createdDate
    User updatedBy
    Date updatedDate

    static constraints = {
        createdBy nullable: true
        createdDate nullable: true
        updatedBy nullable: true
        updatedDate nullable: true
    }

    static mapping = {
        datasource 'main'
        //version false

        createdBy column:"created_by"
        createdDate column: "created_date"
        updatedBy column: "updated_by"
        updatedDate column: "updated_date"
    }
}
