package org.philimone.hds.explorer.server.model.main

import grails.gorm.services.Service

@Service(FormMapping)
interface FormMappingService {

    FormMapping get(Serializable id)

    List<FormMapping> list(Map args)

    Long count()

    void delete(Serializable id)

    FormMapping save(FormMapping formMapping)

}