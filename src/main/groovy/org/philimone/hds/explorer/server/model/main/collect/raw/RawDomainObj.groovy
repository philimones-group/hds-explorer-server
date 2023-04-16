package org.philimone.hds.explorer.server.model.main.collect.raw

import groovy.transform.CompileStatic
import org.grails.datastore.gorm.GormEntity

@CompileStatic
class RawDomainObj<D> {
    D domainInstance

    RawDomainObj(D domainInstance){
        this.domainInstance = domainInstance
    }

    static <D> RawDomainObj<D> attach(D domainInstance){
        new RawDomainObj(domainInstance)
    }
}
