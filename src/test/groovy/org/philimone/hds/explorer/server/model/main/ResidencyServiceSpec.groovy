package org.philimone.hds.explorer.server.model.main

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.testing.spring.AutowiredTest
import net.betainteractive.utilities.GeneralUtil
import org.philimone.hds.explorer.server.model.authentication.Role
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.authentication.UserRole
import org.philimone.hds.explorer.server.model.authentication.UserService
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.collect.raw.RawMember
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.collect.raw.RawResidency
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.services.errors.ErrorMessageService
import org.springframework.context.MessageSource
import org.springframework.context.support.ResourceBundleMessageSource
import spock.lang.Specification

class ResidencyServiceSpec extends Specification implements ServiceUnitTest<ResidencyService>, DataTest, AutowiredTest{

    /* Injecting Needed Services */
    Closure doWithSpring() {{ ->
        errorMessageService ErrorMessageService
        memberService MemberService
        householdService HouseholdService
        regionService RegionService
        userService UserService
    }}

    ErrorMessageService errorMessageService
    MemberService memberService
    HouseholdService householdService
    RegionService regionService
    UserService userService

    MessageSource getI18n() {
        // assuming the test cwd is the project dir (where application.properties is)
        URL url = new File('grails-app/i18n').toURI().toURL()
        def messageSource = new ResourceBundleMessageSource()
        messageSource.bundleClassLoader = new URLClassLoader(url)
        messageSource.basename = 'messages'
        messageSource
    }
    /* Injecting Needed Services - ends here*/

    def setup() {
        mockDomains Role, User, UserRole, Region, Household, Member, Residency, RawHousehold, RawMember, RawResidency

        service.errorMessageService = errorMessageService
        service.errorMessageService.messageSource = getI18n()
        service.memberService = memberService
        service.householdService = householdService
        service.userService = userService

        regionService.userService = userService
        regionService.errorMessageService = errorMessageService
        regionService.errorMessageService.messageSource = getI18n()

        householdService.userService = userService
        householdService.regionService = regionService
        householdService.errorMessageService = errorMessageService
        householdService.errorMessageService.messageSource = getI18n()

        memberService.userService = userService
        memberService.householdService = householdService
        memberService.errorMessageService = errorMessageService
        memberService.errorMessageService.messageSource = getI18n()

        setupUsers()
        setupRegions()
        setupHouseholds()
        setupMembers()
        //inject message source

    }

    def setupUsers(){
        def admin = new Role(name: Role.LABEL_ADMINISTRATOR, authority: Role.ROLE_ADMINISTRATOR).save(flush: true)
        def user = new User(code: "DF1", firstName: "Dragon", lastName: "Fire", username: "dragon", password: "fire")

        userService.addUser(user, [admin])
    }

    def setupRegions(){
        def rg1 = new RawRegion(regionCode: "MAP", regionName: "Maputo", parentCode: "")
        def rg11 = new RawRegion(regionCode: "MAT", regionName: "Matola", parentCode: "MAP")
        def rg111 = new RawRegion(regionCode: "TXU", regionName: "Txumene", parentCode: "MAT")
        def rg112 = new RawRegion(regionCode: "FOM", regionName: "Fomento", parentCode: "MAT")
        def rg2 = new RawRegion(regionCode: "GAZ", regionName: "Gaza", parentCode: "")
        def rg21 = new RawRegion(regionCode: "XAI", regionName: "Xai-Xai", parentCode: "GAZ")
        def rg211 = new RawRegion(regionCode: "ZON", regionName: "Zongoene", parentCode: "XAI")
        def rg212 = new RawRegion(regionCode: "LIM", regionName: "Limpopo", parentCode: "XAI")

        regionService.createRegion(rg1)
        regionService.createRegion(rg11)
        regionService.createRegion(rg111)
        regionService.createRegion(rg112)
        regionService.createRegion(rg2)
        regionService.createRegion(rg21)
        regionService.createRegion(rg211)
        regionService.createRegion(rg212)
    }

    def setupHouseholds(){

        def user = User.findByUsername("dragon")
        def region = Region.findByCode("TXU")

        def rw1 = new RawHousehold(id: "u1", regionCode: region.code, householdCode: householdService.generateCode(region, user), householdName: "Macandza House", headCode: "", headName: "", collectedBy: user.username, collectedDate: new Date(), uploadedDate: new Date())

        householdService.createHousehold(rw1)

        def rw2 = new RawHousehold(id: "u2", regionCode: region.code, householdCode: householdService.generateCode(region, user), householdName: "George Benson", headCode: "", headName: "", collectedBy: user.username, collectedDate: new Date(), uploadedDate: new Date())

        householdService.createHousehold(rw2)
    }

    def setupMembers(){

        def user = User.findByUsername("dragon")
        def household1 = Household.findAll().first()
        def household2 = Household.findAll().last()

        def m = new Member(id: "Unknown Member", code: Codes.MEMBER_UNKNOWN_CODE, name: "member.unknown.label", gender: Gender.MALE, dob: GeneralUtil.getDate(1900,1,1), maritalStatus: MaritalStatus.SINGLE)
        m.save(flush: true)

        println("member: "+m.errors)

        def rw1 = new RawMember(id: "uuuid1", code: memberService.generateCode(household1), name: "John Benedit Macandza", gender: Gender.MALE.code, dob: GeneralUtil.getDate(1988,10,10),
                                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", maritalStatus: MaritalStatus.SINGLE.code, householdCode: household1.code,
                                collectedBy: "dragon", collectedDate: new Date(), uploadedDate: new Date())

        def res1 = memberService.createMember(rw1)

        def rw2 = new RawMember(id: "uuuid2", code: memberService.generateCode(household1), name: "Catarina Loyd Macandza", gender: Gender.FEMALE.code, dob: GeneralUtil.getDate(1993,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", maritalStatus: MaritalStatus.SINGLE.code, householdCode: household1.code,
                collectedBy: "dragon", collectedDate: new Date(), uploadedDate: new Date())

        def res2 = memberService.createMember(rw2)

        def rw3 = new RawMember(id: "uuuid3", code: memberService.generateCode(household2), name: "George Benson", gender: Gender.MALE.code, dob: GeneralUtil.getDate(1988,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", maritalStatus: MaritalStatus.SINGLE.code, householdCode: household1.code,
                collectedBy: "dragon", collectedDate: new Date(), uploadedDate: new Date())

        def res3 = memberService.createMember(rw3)

        def rw4 = new RawMember(id: "uuuid4", code: memberService.generateCode(household2), name: "Joyce Mary Benson", gender: Gender.FEMALE.code, dob: GeneralUtil.getDate(1993,10,10),
                motherCode: Codes.MEMBER_UNKNOWN_CODE, motherName: "", fatherCode: Codes.MEMBER_UNKNOWN_CODE, fatherName: "", maritalStatus: MaritalStatus.SINGLE.code, householdCode: household1.code,
                collectedBy: "dragon", collectedDate: new Date(), uploadedDate: new Date())

        def res4 = memberService.createMember(rw4)

        printResults(res1)
        printResults(res2)
        printResults(res3)
        printResults(res4)
    }

    def cleanup() {

    }

    def printResults(RawExecutionResult result){
        println("status: ${result.status}")
        printRawMessages(result.errorMessages)
        println()
    }

    def printRawMessages(List<RawMessage> errorMessages){
        errorMessages.each { err ->
            println "${err.columns} -> ${err.text}"
        }
    }

    def printResidency(Residency residency){
        if (residency==null) return null
        println "residency(id=${residency.id},m.code=${residency.memberCode},h.code=${residency.householdCode},starttype=${residency.startType},startdate=${residency.startDate},endtype=${residency.endType},enddate=${residency.endDate})"
    }

    def printResidency(RawExecutionResult<Residency> result){
        printResidency(result?.domainInstance)
    }

    /*
     * 1. Test Creation of Residency
     * 2. Test Closing the Residency
     * 3. Test Creating 2 Residencies of same member
     * 4. Test Closing a Closed Residency
     */
    void "Test Creation of Residency"() {
        setup:

        //create new residency
        def household1 = Household.findByName("Macandza House")
        def household2 = Household.findByName("George Benson")
        def member11 = Member.findByName("John Benedit Macandza")
        def member12 = Member.findByName("Catarina Loyd Macandza")
        def member21 = Member.findByName("George Benson")
        def member22 = Member.findByName("Joyce Mary Benson")

        println "household1: ${household1}, check: ${Household.count()}"
        println "member1: ${member11}, check: ${Member.count()}"
        println "household2: ${household2}, check: ${Household.count()}"
        println "member2: ${member21}, check: ${Member.count()}"

        def rw1 = new RawResidency(
                id: "uuuid1",
                memberCode: member11.code,
                householdCode: household1.code,
                startType: "EXT",
                startDate: GeneralUtil.getDate(2020,03,03),
                endType: "",
                endDate: "",
        )

        def rw2 = new RawResidency(
                id: "uuuid1",
                memberCode: member12.code,
                householdCode: household1.code,
                startType: "ENT",
                startDate: GeneralUtil.getDate(2020,03,04),
                endType: "",
                endDate: "",
        )

        rw1.save()
        rw2.save()

        println "Raw Member Errors:"
        printRawMessages(errorMessageService.getRawMessages(rw1))
        println()
        printRawMessages(errorMessageService.getRawMessages(rw2))
        //printErrors(rw)

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result1 = service.createResidency(rw1)
        printResults(result1)

        println()

        def result2 = service.createResidency(rw2)
        printResults(result1)

        printResidency(result1)
        printResidency(result2)

        expect:
        Residency.count()==2
    }
}
