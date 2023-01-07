package org.philimone.hds.explorer.server.model.settings.generator

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.User
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

    boolean isModuleCodeValid(String code) {
        return codeGenerator.isModuleCodeValid(code)
    }

    boolean isTrackingListCodeValid(String code) {
        return codeGenerator.isTrackingListCodeValid(code)
    }

    boolean isRegionCodeValid(String code) {
        return codeGenerator.isRegionCodeValid(code)
    }

    boolean isLowestRegionCodeValid(String code) {
        return codeGenerator.isLowestRegionCodeValid(code)
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

    String generateTrackingListCode() {
        def codes = TrackingList.list().collect { t -> t.code}
        return codeGenerator.generateTrackingListCode(codes)
    }

    String generateRegionCode(Region parentRegion, String regionName) {

        def codes = Region.list().collect { t -> t.code}
        return codeGenerator.generateRegionCode(parentRegion, regionName, codes)
    }

    String generateLowestRegionCode(Region parentRegion, String regionName) {

        def codes = Region.list().collect { t -> t.code}
        return codeGenerator.generateLowestRegionCode(parentRegion, regionName, codes)
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

        def codes = User.list().collect{ t -> t.code}

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

    String getRegionSampleCode() {
        return codeGenerator.getRegionSampleCode()
    }

    String getLowestRegionSampleCode() {
        return codeGenerator.getLowestRegionSampleCode()
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
