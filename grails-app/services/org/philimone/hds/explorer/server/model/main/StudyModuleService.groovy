package org.philimone.hds.explorer.server.model.main

import grails.gorm.services.Service

@Service(StudyModule)
interface StudyModuleService {

    StudyModule get(Serializable id)

    List<StudyModule> list(Map args)

    Long count()

    void delete(Serializable id)

    StudyModule save(StudyModule studyModule)

}