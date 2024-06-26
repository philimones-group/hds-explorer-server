package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.main.Visit

@Transactional
class RawDomainService {

    def visitService
    def codeGeneratorService

    String getHouseholdCode(String code) {
        if (codeGeneratorService.isVisitCodeValid(code)) {
            def household_code = code

            def result = Visit.executeQuery("select v.householdCode from Visit v where v.code=?0", [code])

            if (result?.size() > 0) {
                return result.first()
            }
        }


        return code
    }
}
