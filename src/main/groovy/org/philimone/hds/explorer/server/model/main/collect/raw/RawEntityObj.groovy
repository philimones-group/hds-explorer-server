package org.philimone.hds.explorer.server.model.main.collect.raw

import groovy.transform.CompileStatic
import org.philimone.hds.explorer.server.model.enums.RawEntity

@CompileStatic
class RawEntityObj {
    RawEntity entity
    RawDomainObj domainObj

    RawEntityObj(RawEntity rawEntity, RawDomainObj domainObj) {
        this.entity = rawEntity
        this.domainObj = domainObj
    }
}
