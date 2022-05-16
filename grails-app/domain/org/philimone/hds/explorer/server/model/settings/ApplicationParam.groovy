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

    String getI18nName(){
        if (name.equalsIgnoreCase("hierarchy1")) return "applicationParams.hierachylevel.level1.label"
        if (name.equalsIgnoreCase("hierarchy2")) return "applicationParams.hierachylevel.level2.label"
        if (name.equalsIgnoreCase("hierarchy3")) return "applicationParams.hierachylevel.level3.label"
        if (name.equalsIgnoreCase("hierarchy4")) return "applicationParams.hierachylevel.level4.label"
        if (name.equalsIgnoreCase("hierarchy5")) return "applicationParams.hierachylevel.level5.label"
        if (name.equalsIgnoreCase("hierarchy6")) return "applicationParams.hierachylevel.level6.label"
        if (name.equalsIgnoreCase("hierarchy7")) return "applicationParams.hierachylevel.level7.label"
        if (name.equalsIgnoreCase("hierarchy8")) return "applicationParams.hierachylevel.level8.label"
        if (name.equalsIgnoreCase("hierarchy9")) return "applicationParams.hierachylevel.level9.label"
        if (name.equalsIgnoreCase("hierarchy10")) return "applicationParams.hierachylevel.level10.label"

        return "Hierarchy Level"
    }

    static constraints = {
        id maxSize: 32
        name blank:false, unique: true
        type blank:true, nullable: true
        value blank:true, nullable: true
    }

    static mapping = {
        table '_application_param'

        id column: "id", generator: 'uuid'

        name column: "name", index: "idx_name"
        type column: "type", enumType: "string"
        value column: "value"
    }
}
