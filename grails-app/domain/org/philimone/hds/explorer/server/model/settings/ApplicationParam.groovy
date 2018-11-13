package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.authentication.User

class ApplicationParam {

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

}
