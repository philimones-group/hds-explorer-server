package org.philimone.hds.explorer.server.model.main

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.testing.spring.AutowiredTest
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.authentication.UserService
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.services.errors.ErrorMessageService
import org.springframework.context.MessageSource
import org.springframework.context.support.ResourceBundleMessageSource
import spock.lang.Specification

class RegionServiceSpec extends Specification implements ServiceUnitTest<RegionService>, DataTest, AutowiredTest{

    /* Injecting Needed Services */
    Closure doWithSpring() {{ ->
        errorMessageService ErrorMessageService
        userService UserService
    }}

    ErrorMessageService errorMessageService
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
        mockDomains User, Region

        errorMessageService.messageSource = getI18n()
        service.errorMessageService = errorMessageService
        service.userService = userService
    }

    def cleanup() {
    }

    def printResults(RawExecutionResult result){
        println("status: ${result.status}")
        printRawMessages(result.errorMessages)
    }

    def printRawMessages(List<RawMessage> errorMessages){
        errorMessages.each { err ->
            println "${err.columns} -> ${err.text}"
        }
    }

    void "Test Region Creation"() {
        setup:
        def rg1 = new RawRegion(regionCode: "MAT", regionName: "Matola", parentCode: "")
        def rg2 = new RawRegion(regionCode: "TXU", regionName: "Txumene", parentCode: "MAT", collectedBy: "FWPF1")
        def rg3 = new RawRegion(regionCode: "TX2", regionName: "Txumene II", parentCode: "TXU")

        println "Region Errors:"

        //This methods validates data twice, first through strict rules of demographics and then through domain model constraints
        def result1 = service.createRegion(rg1)
        printResults(result1)
        def result2 = service.createRegion(rg2)
        printResults(result2)
        def result3 = service.createRegion(rg2)
        printResults(result3)
        def result4 = service.createRegion(rg3)
        printResults(result4)


        expect:
        Region.count()==2
    }
}
