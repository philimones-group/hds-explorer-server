package org.philimone.hds.explorer.server.model.types

import groovy.transform.CompileStatic
import org.philimone.hds.explorer.server.model.enums.IllnessSymptoms

@CompileStatic
class IllnessSymptomsUserType extends EnumSetUserType {
    IllnessSymptomsUserType() {
        super(IllnessSymptoms)
    }
}
