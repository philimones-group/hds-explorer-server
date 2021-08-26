package org.philimone.hds.explorer.server.model.settings.generator

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.PregnancyRegistration
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.server.model.main.Visit

@Transactional
class CodeGeneratorService {

    def codeGenerator = CodeGeneratorFactory.newInstance()

    boolean isRegionCodeValid(String code) {
        return codeGenerator.isRegionCodeValid(code)
    }

    boolean isHouseholdCodeValid(String code) {
        return codeGenerator.isHouseholdCodeValid(code)
    }

    boolean isMemberCodeValid(String code) {
        return codeGenerator.isMemberCodeValid(code)
    }

    boolean isVisitCodeValid(String code) {
        return codeGenerator.isVisitCodeValid(code)
    }

    boolean isUserCodeValid(String code) {
        return codeGenerator.isUserCodeValid(code)
    }

    boolean isPregnancyCodeValid(String code){
        return codeGenerator.isPregnancyCodeValid(code)
    }

    String generateModuleCode(String moduleName) {
        def codes = Module.list().collect { t -> t.code}
        return codeGenerator.generateModuleCode(moduleName, codes)
    }

    String generateRegionCode(String regionName) {

        def codes = Region.list().collect { t -> t.code}
        return codeGenerator.generateRegionCode(regionName, codes)
    }

    String generateHouseholdCode(Region region, User user) {

        def cbase = "${region.code}${user.code}"
        def codes = Household.findAllByCodeLike("${cbase}%", [sort:'code', order: 'asc']).collect{ t -> t.code}

        return codeGenerator.generateHouseholdCode(cbase, codes)
    }

    String generateMemberCode(Household household) {

        def cbase = "${household.code}"
        def codes = Member.findAllByCodeLike("${cbase}%", [sort:'code', order: 'asc']).collect{ t -> t.code}

        return codeGenerator.generateMemberCode(cbase, codes)
    }

    String generateVisitCode(Household household) {

        def cbase = "${household.code}"
        def codes = Visit.findAllByCodeLike("${cbase}%", [sort:'code', order: 'asc']).collect{ t -> t.code}

        return codeGenerator.generateVisitCode(cbase, codes)
    }

    String generateUserCode(User user) {

        def codes = User.list().collect{ t -> t.code}

        return codeGenerator.generateUserCode(user, codes)
    }

    String generatePregnancyCode(Member mother) {
        def cbase = "${mother.code}"
        def codes = PregnancyRegistration.findAllByCodeLike("${cbase}%", [sort:'code', order: 'asc']).collect{ t -> t.code}

        return codeGenerator.generatePregnancyCode(cbase, codes)
    }
}
