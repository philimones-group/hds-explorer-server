package org.philimone.hds.explorer.server.settings.generator

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.PregnancyRegistration
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.server.model.main.Round
import org.philimone.hds.explorer.server.model.main.TrackingList
import org.philimone.hds.explorer.server.model.main.Visit

@Transactional
class CodeGeneratorService {

    def codeGenerator = CodeGeneratorFactory.newInstance()
    def regionService

    boolean isModuleCodeValid(String code) {
        return codeGenerator.isModuleCodeValid(code)
    }

    boolean isTrackingListCodeValid(String code) {
        return codeGenerator.isTrackingListCodeValid(code)
    }

    boolean isRegionCodeValid(RegionLevel regionLevel, String code) {
        def lowestLevel = regionService.getLowestRegionLevel()
        return codeGenerator.isRegionCodeValid(lowestLevel, regionLevel, code)
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
        def codes = Module.listOrderByCode().collect { t -> t.code}
        return codeGenerator.generateModuleCode(moduleName, codes)
    }

    String generateTrackingListCode() {
        def codes = TrackingList.listOrderByCode().collect { t -> t.code}
        return codeGenerator.generateTrackingListCode(codes)
    }

    String generateRegionCode(Region parentRegion, String regionName) {
        def lowestLevel = regionService.lowestRegionLevel
        def codes = Region.listOrderByCode().collect { t -> t.code}

        return codeGenerator.generateRegionCode(lowestLevel, parentRegion, regionName, codes)
    }

    String generateHouseholdCode(Region region, User user) {

        def cbase = codeGenerator.getHouseholdBaseCode(region, user)
        def codes = Household.findAllByCodeLike("${cbase}%", [sort:'code', order: 'asc']).collect{ t -> t.code}

        return codeGenerator.generateHouseholdCode(cbase, codes)
    }

    String generateMemberCode(Household household) {

        def cbase = "${household.code}"
        def codes = Member.findAllByCodeLike("${cbase}%", [sort:'code', order: 'asc']).collect{ t -> t.code}

        return codeGenerator.generateMemberCode(cbase, codes)
    }

    String generateVisitCode(Household household) {
        //use new pattern, household + round number + ordinal
        def maxround = Round.executeQuery("select max(roundNumber) from Round")
        long roundNumber = maxround.size()>0 ? maxround[0] : 0
        def currentRound = Round.findByRoundNumber(maxround)

        def cbase = codeGenerator.getVisitBaseCode(household, currentRound)
        def codes = Visit.findAllByCodeLike("${cbase}%", [sort:'code', order: 'asc']).collect{ t -> t.code}

        return codeGenerator.generateVisitCode(cbase, codes)
    }

    String generateUserCode(User user) {

        def codes = User.listOrderByCode().collect{ t -> t.code}

        return codeGenerator.generateUserCode(user, codes)
    }

    String generatePregnancyCode(Member mother) {
        def cbase = "${mother.code}"
        def codes = PregnancyRegistration.findAllByCodeLike("${cbase}%", [sort:'code', order: 'asc']).collect{ t -> t.code}

        return codeGenerator.generatePregnancyCode(cbase, codes)
    }

    String getModuleSampleCode() {
        return codeGenerator.getModuleSampleCode()
    }

    String getTrackingListSampleCode() {
        return codeGenerator.getTrackingListSampleCode()
    }

    String getRegionSampleCode(RegionLevel regionLevel) {
        def lowestLevel = regionService.getLowestRegionLevel()
        return codeGenerator.getRegionSampleCode(lowestLevel, regionLevel)
    }

    String getHouseholdSampleCode() {
        return codeGenerator.getHouseholdSampleCode()
    }

    String getMemberSampleCode() {
        return codeGenerator.getMemberSampleCode()
    }

    String getVisitSampleCode() {
        return codeGenerator.getVisitSampleCode()
    }

    String getUserSampleCode() {
        return codeGenerator.getUserSampleCode()
    }

    String getPregnancySampleCode() {
        return codeGenerator.getPregnancySampleCode()
    }

}
