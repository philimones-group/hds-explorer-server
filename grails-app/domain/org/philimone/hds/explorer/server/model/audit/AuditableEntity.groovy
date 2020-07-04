package org.philimone.hds.explorer.server.model.audit

import org.philimone.hds.explorer.server.model.authentication.User

class AuditableEntity {

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
}
