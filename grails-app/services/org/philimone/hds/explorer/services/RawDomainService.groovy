package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.json.JMember
import org.philimone.hds.explorer.server.model.main.Member
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

    JMember getBasicMember(String memberCode) {
        def result = Member.executeQuery("select m.code, m.name, m.gender, m.dob from Member m where m.code = ?0", [memberCode])
        def m = result.size()>0 ? result.first() : null
        return new JMember(m[0], m[1], m[2], m[3])
    }
}
