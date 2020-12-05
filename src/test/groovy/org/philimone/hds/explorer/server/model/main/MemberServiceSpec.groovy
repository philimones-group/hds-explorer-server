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
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.services.errors.ErrorMessageService
import org.springframework.context.MessageSource
import org.springframework.context.support.ResourceBundleMessageSource
import spock.lang.Specification

class MemberServiceSpec extends Specification implements ServiceUnitTest<MemberService>, DataTest, AutowiredTest{

    /* Injecting Needed Services */
    Closure doWithSpring() {{ ->
        errorMessageService ErrorMessageService
        householdService HouseholdService
        regionService RegionService
        userService UserService
    }}

    ErrorMessageService errorMessageService
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
        mockDomains Role, User, UserRole, Region, Household, Member, RawHousehold

        service.errorMessageService = errorMessageService
        service.errorMessageService.messageSource = getI18n()
        service.householdService = householdService
        service.userService = userService

        regionService.userService = userService
        regionService.errorMessageService = errorMessageService
        regionService.errorMessageService.messageSource = getI18n()

        householdService.userService = userService
        householdService.regionService = regionService
        householdService.errorMessageService = errorMessageService
        householdService.errorMessageService.messageSource = getI18n()

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
        def m = new Member(id: "Unknown Member", code: Codes.MEMBER_UNKNOWN_CODE, name: "member.unknown.label", gender: Gender.MALE, dob: GeneralUtil.getDate(1900,1,1), maritalStatus: MaritalStatus.SINGLE)

        m.save(flush: true)

        println("member: "+m.errors)
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

    void "Test Member Creation"() {
        setup:
        //println Region.list()

        def household = Household.findByName("Macandza House")

        def rw = new RawMember(
                id: "uuuid1",
                code: service.generateCode(household),
                name: "John Benedit Macandza",
                gender: "M",
                dob: GeneralUtil.getDate(1988,10,10),
                motherCode: "UNK",
                motherName: "",
                fatherCode: "UNK",
                fatherName: "",

                maritalStatus: "SIN",

                householdCode: household.code,

                collectedBy: "dragon",
                collectedDate: new Date(),
                uploadedDate: new Date()
        )


        println "Raw Member Errors:"
        printRawMessages(errorMessageService.getRawMessages(rw))
        println()
        //printErrors(rw)

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result = service.createMember(rw)
        printResults(result)

        expect:
        Member.count() == 2
    }

    void "Test Member Duplicates"() {
        setup:
        //println Region.list()

        def household = Household.findByName("Macandza House")

        def rw = new RawMember(
                id: "uuuid1",
                code: service.generateCode(household),
                name: "John Benedit Macandza",
                gender: "M",
                dob: GeneralUtil.getDate(1988,10,10),
                motherCode: "UNK",
                motherName: "",
                fatherCode: "UNK",
                fatherName: "",

                maritalStatus: "SIN",

                householdCode: household.code,

                collectedBy: "dragon",
                collectedDate: new Date(),
                uploadedDate: new Date()
        )

        def rw2 = new RawMember(
                id: "uuuid1",
                code: service.generateCode(household),
                name: "John Benedit Macandza",
                gender: "M",
                dob: GeneralUtil.getDate(1988,10,10),
                motherCode: "UNK",
                motherName: "",
                fatherCode: "UNK",
                fatherName: "",

                maritalStatus: "SIN",

                householdCode: household.code,

                collectedBy: "dragon",
                collectedDate: new Date(),
                uploadedDate: new Date()
        )


        println "Raw Member Errors:"
        printRawMessages(errorMessageService.getRawMessages(rw))
        println()
        //printErrors(rw)

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result1 = service.createMember(rw)
        printResults(result1)

        def result2 = service.createMember(rw2)
        printResults(result2)

        expect:
        Member.count() == 2
    }
}
