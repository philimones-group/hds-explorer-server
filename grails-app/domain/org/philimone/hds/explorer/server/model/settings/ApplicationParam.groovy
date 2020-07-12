package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.server.model.audit.AuditableEntity

/**
 * A ApplicationParam represents default variabless/parameters that will be used in the whole system
 */
class ApplicationParam extends AuditableEntity {

    String name
    String type
    String value

    static constraints = {
        name blank:false, unique: true
        type blank:true, nullable: true
        value blank:true, nullable: true
    }

}
