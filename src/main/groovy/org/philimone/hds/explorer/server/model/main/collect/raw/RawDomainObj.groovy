package org.philimone.hds.explorer.server.model.main.collect.raw

class RawDomainObj<D> {
    D domainInstance

    RawDomainObj(D domainInstance){
        this.domainInstance = domainInstance
    }

    static RawDomainObj attach(D domainInstance){
        new RawDomainObj(domainInstance)
    }
}
