package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.server.model.authentication.User

/**
 * A ApplicationParam represents default variabless/parameters that will be used in the whole system
 */
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
        type blank:true, nullable: true
        value blank:true, nullable: true

        createdBy nullable: true
        creationDate nullable: true
        updatedBy nullable: true
        updatedDate nullable:true
    }

}
