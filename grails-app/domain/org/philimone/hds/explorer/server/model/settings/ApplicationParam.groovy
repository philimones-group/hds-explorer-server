package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.authentication.User

class ApplicationParam {

    transient generalUtilitiesService

    String name
    String type
    String value

    User createdBy
    Date creationDate
    User updatedBy
    Date updatedDate

    static constraints = {
        name blank:false, unique: true
        type blank:true
        value blank:true

        createdBy nullable: true
        creationDate nullable: true
        updatedBy nullable: true
        updatedDate nullable:true
    }

    def beforeInsert() {
        createdBy  =  generalUtilitiesService.currentUser()
        this.creationDate = new Date()
    }

    def beforeUpdate() {
        this.updatedBy = generalUtilitiesService.currentUser()
        this.updatedDate = new Date()
    }

    static mapping = {
        autowire true
    }
}
