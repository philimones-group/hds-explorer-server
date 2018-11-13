package org.philimone.hds.explorer.server.model.settings

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.services.GeneralUtilitiesService

@Transactional
class ApplicationParamService {

    def GeneralUtilitiesService generalUtilitiesService

    def ApplicationParam addApplicationParam(ApplicationParam param) {
        //timestamp
        param.createdBy = generalUtilitiesService.currentUser()
        param.creationDate = new Date()

        param.save(flush: true)
    }

    def ApplicationParam updateApplicationParam(ApplicationParam param){
        //timestamp
        param.updatedBy = generalUtilitiesService.currentUser()
        param.updatedDate = new Date()

        param.save(flush: true)
    }
}
