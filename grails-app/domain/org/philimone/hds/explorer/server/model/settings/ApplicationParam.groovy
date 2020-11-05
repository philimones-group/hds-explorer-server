package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.enums.settings.ApplicationParamType

/**
 * A ApplicationParam represents default variabless/parameters that will be used in the whole system
 */
class ApplicationParam extends AuditableEntity {

    String id
    String name
    ApplicationParamType type = ApplicationParamType.STRING //default is string
    String value

    static constraints = {
        id maxSize: 32
        name blank:false, unique: true
        type blank:true, nullable: true
        value blank:true, nullable: true
    }

    static mapping = {
        table '_application_param'

        id column: "id", generator: 'uuid'

        name column: "name"
        type column: "type", enumType: "string"
        value column: "value"
    }
}
