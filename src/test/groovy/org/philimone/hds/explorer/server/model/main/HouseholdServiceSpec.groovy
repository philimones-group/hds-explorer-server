package org.philimone.hds.explorer.server.model.main

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.testing.spring.AutowiredTest
import org.philimone.hds.explorer.server.model.authentication.Role
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.authentication.UserRole
import org.philimone.hds.explorer.server.model.authentication.UserService
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.generator.CodeGeneratorService
import org.philimone.hds.explorer.services.errors.ErrorMessageService
import org.springframework.context.MessageSource
import org.springframework.context.support.ResourceBundleMessageSource
import spock.lang.Specification

class HouseholdServiceSpec extends Specification implements ServiceUnitTest<HouseholdService>, DataTest, AutowiredTest{

    /* Injecting Needed Services */
    Closure doWithSpring() {{ ->
        errorMessageService ErrorMessageService
        memberService MemberService
        regionService RegionService
        userService UserService
        codeGeneratorService CodeGeneratorService
    }}

    ErrorMessageService errorMessageService
    MemberService memberService
    RegionService regionService
    UserService userService
    CodeGeneratorService codeGeneratorService

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

        service.errorMessageService.messageSource = getI18n()
        service.userService = userService
        service.codeGeneratorService = codeGeneratorService

        userService.codeGeneratorService = codeGeneratorService

        setupRegions()
        //inject message source

    }

    def setupRegions(){
        def rg1 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Matola"), regionName: "Matola", parentCode: "")
        def rg2 = new RawRegion(regionCode: codeGeneratorService.generateRegionCode("Txumene"), regionName: "Txumene", parentCode: "MAT")
        regionService.userService = userService
        regionService.codeGeneratorService = codeGeneratorService
        regionService.errorMessageService = errorMessageService
        regionService.errorMessageService.messageSource = getI18n()
        regionService.createRegion(rg1)
        regionService.createRegion(rg2)
    }

    def cleanup() {
    }

    /*
    def printErrors(GormEntity domain){
        //println("status: ${result.status}")

        domain.errors.fieldErrors.each { obj ->
            def msg = messageSource.getMessage(obj, LocaleContextHolder.getLocale())
            //err = removeClassDefFromErrDetails(err)
            msg = net.betainteractive.utilities.StringUtil.removePackageNames(msg)

            println "[${obj}, ${obj.field}] ->:: ${msg}"
        }
    }
    */

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

    void "Testing Household Creation"() {
        setup:
        //println Region.list()

        def rw = new RawHousehold(
                id: "uuuid1",
                regionCode: "TXU",
                regionName: "Txumene",
                householdCode: "TXUDF1001",
                householdName: "Macandza House",
                headCode: "",
                headName: "",
                gpsLng: "ASDASD",
                gpsAlt: "10.1000",
                gpsLat: "10.1000",
                gpsAcc: "10.1000",

                collectedBy: "",
                collectedDate: new Date(),
                uploadedDate: new Date()
        )

        rw.save()

        println "Raw Household Errors:"
        printRawMessages(errorMessageService.getRawMessages(rw))
        println()
        //printErrors(rw)

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result = service.createHousehold(rw)
        printResults(result)

        //Test duplicate

        expect:
        Household.count() == 1
    }

    void "Testing Household Duplicates Creation"() {
        setup:

        def admin = new Role(name: Role.LABEL_ADMINISTRATOR, authority: Role.ROLE_ADMINISTRATOR).save(flush: true)
        def region = Region.findByCode("TXU")
        def user = new User(code: "DF1", firstName: "Dragon", lastName: "Fire", username: "dragon", password: "fire").save(flush: true)

        user = userService.addUser(user, [admin])

        println ("admin user - hasErrors: "+user.hasErrors())
        println ("admin user - saving: " + user.id)


        def rw1 = new RawHousehold(
                id: "uuuid1",
                regionCode: "TXU",
                regionName: "Txumene",
                householdCode: service.generateCode(region, user),
                householdName: "Macandza House",
                headCode: "",
                headName: "",
                gpsLng: "ASDASD",
                gpsAlt: "10.1000",
                gpsLat: "10.1000",
                gpsAcc: "10.1000",

                collectedBy: "dragon",
                collectedDate: new Date(),
                uploadedDate: new Date()
        )

        def rw2 = new RawHousehold(
                id: "uuuid1",
                regionCode: "TXU",
                regionName: "Txumene",
                householdCode: "TXUDF1001",
                householdName: "Macandza House",
                headCode: "",
                headName: "",
                gpsLng: "ASDASD",
                gpsAlt: "10.1000",
                gpsLat: "10.1000",
                gpsAcc: "10.1000",

                collectedBy: "dragon",
                collectedDate: new Date(),
                uploadedDate: new Date()
        )

        println "Duplicate Tests:"

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result = service.createHousehold(rw1)
        printResults(result)

        println "Testing new Household code=${rw1.householdCode}, collectedBy=${result.domainInstance.collectedBy}"

        rw2.householdCode = service.generateCode(region, user).replaceAll("2", "3") //jump 2
        result = service.createHousehold(rw2)
        printResults(result)

        println "Test duplicate Household code=${rw2.householdCode}, collectedBy=${result.domainInstance.collectedBy}"

        result = service.createHousehold(rw2)
        printResults(result)

        println "Test New Code=${service.generateCode(region, user)}"

        expect:
        Household.count() == 2
    }
}
