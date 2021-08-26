package org.philimone.hds.explorer.server.model.main

import grails.gorm.services.Service

@Service(Module)
interface ModuleService {

    Module get(Serializable id)

    List<Module> list(Map args)

    Long count()

    void delete(Serializable id)

    Module save(Module module)

}