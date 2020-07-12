package org.philimone.hds.explorer.server.model.settings

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.services.GeneralUtilitiesService

@Transactional
class ApplicationParamService {

    def ApplicationParam addApplicationParam(ApplicationParam param) {
        param.save(flush: true)
    }

    def ApplicationParam updateApplicationParam(ApplicationParam param){
        param.save(flush: true)
    }
}
