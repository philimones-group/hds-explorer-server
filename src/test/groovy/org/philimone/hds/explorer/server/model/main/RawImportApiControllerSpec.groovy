package org.philimone.hds.explorer.server.model.main

import grails.testing.gorm.DataTest
import grails.testing.spring.AutowiredTest
import grails.testing.web.controllers.ControllerUnitTest
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.authentication.UserService
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.collect.raw.aggregate.RawEvent
import org.philimone.hds.explorer.server.model.collect.raw.api.RawImportApiController
import org.philimone.hds.explorer.server.model.collect.raw.api.RawImportApiService
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorService
import org.philimone.hds.explorer.services.errors.ErrorMessageService
import org.springframework.context.MessageSource
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.HttpStatus
import spock.lang.Ignore
import spock.lang.Specification

class RawImportApiControllerSpec extends Specification implements ControllerUnitTest<RawImportApiController>, DataTest, AutowiredTest {

    //@Shared RawImportApiController controller = new RawImportApiController()

    /* Injecting Needed Services */
    Closure doWithSpring() {{ ->
        errorMessageService ErrorMessageService
        rawImportApiService RawImportApiService
        rawExecutionService RawExecutionService
        regionService RegionService
        codeGeneratorService CodeGeneratorService
        userService UserService
    }}

    MessageSource getI18n() {
        // assuming the test cwd is the project dir (where application.properties is)
        URL url = new File('grails-app/i18n').toURI().toURL()
        def messageSource = new ResourceBundleMessageSource()
        messageSource.bundleClassLoader = new URLClassLoader(url)
        messageSource.basename = 'messages'
        messageSource
    }

    RawImportApiService rawImportApiService
    RawExecutionService rawExecutionService
    ErrorMessageService errorMessageService
    RegionService regionService
    CodeGeneratorService codeGeneratorService
    UserService userService

    def setup() {
        mockDomains RawMemberEnu, RawPregnancyOutcome, RawPregnancyChild, RawPregnancyRegistration, RawResidency, RawRegion, RawVisit, RawOutMigration, RawMember, RawMaritalRelationship, RawInMigration, RawHousehold, RawHeadRelationship, RawExternalInMigration, RawErrorLog, RawDeath, RawChangeHead, RawEvent, User, Region

        errorMessageService.messageSource = getI18n()
        rawImportApiService.errorMessageService = errorMessageService

        regionService.userService = userService
        regionService.errorMessageService = errorMessageService
        regionService.codeGeneratorService = codeGeneratorService

        rawExecutionService.regionService = regionService

        controller.rawImportApiService = rawImportApiService
        controller.rawExecutionService = rawExecutionService
        controller.errorMessageService = errorMessageService

    }

    def cleanup() {

    }

    //@Ignore
    void "test creating a regions with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<rawRegion>\n" +
                "  <regionName>Maputo</regionName>\n" +
                "  <parentCode/>\n" +
                "  <collectedDate>2020-03-12 01:10:01</collectedDate>\n" +
                "  <postExecution>true</postExecution>\n" +
                "  <collectedBy>admin</collectedBy>\n" +
                "  <uploadedDate>2020-03-12 01:10:01</uploadedDate>\n" +
                "  <regionCode>MAP</regionCode>\n" +
                "</rawRegion>"


        //"<RawRegion><collectedDate>2020-03-12 01:10:01</collectedDate></RawRegion>"
        controller.regions()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        Region.count == 1
    }

    @Ignore
    void "test creating a households with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawHousehold><collectedDate>2020-03-12 01:10:01</collectedDate></RawHousehold>"
        controller.households()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        Household.count == 1
    }

    @Ignore
    void "test creating a members with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawMember><collectedDate>2020-03-12 01:10:01</collectedDate></RawMember>"
        controller.members()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        Member.count == 1
    }

    @Ignore
    void "test creating a visits with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawVisit><collectedDate>2020-03-12 01:10:01</collectedDate></RawVisit>"
        controller.visits()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        Visit.count == 1
    }

    @Ignore
    void "test creating a memberenus with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawMemberEnu><collectedDate>2020-03-12 01:10:01</collectedDate></RawMemberEnu>"
        controller.memberenus()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        MemberEnu.count == 1
    }

    @Ignore
    void "test creating a externalinmigrations with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawExternalInMigration><collectedDate>2020-03-12 01:10:01</collectedDate></RawExternalInMigration>"
        controller.externalinmigrations()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        ExternalInMigration.count == 1
    }

    @Ignore
    void "test creating a inmigrations with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawInMigration><collectedDate>2020-03-12 01:10:01</collectedDate></RawInMigration>"
        controller.inmigrations()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        InMigration.count == 1
    }

    @Ignore
    void "test creating a outmigrations with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawOutMigration><collectedDate>2020-03-12 01:10:01</collectedDate></RawOutMigration>"
        controller.outmigrations()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        OutMigration.count == 1
    }

    @Ignore
    void "test creating a headrelationships with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawHeadRelationship><collectedDate>2020-03-12 01:10:01</collectedDate></RawHeadRelationship>"
        controller.headrelationships()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        HeadRelationship.count == 1
    }

    @Ignore
    void "test creating a maritalrelationships with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawMaritalRelationship><collectedDate>2020-03-12 01:10:01</collectedDate></RawMaritalRelationship>"
        controller.maritalrelationships()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        MaritalRelationship.count == 1
    }

    @Ignore
    void "test creating a pregnancyregistrations with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawPregnancyRegistration><collectedDate>2020-03-12 01:10:01</collectedDate></RawPregnancyRegistration>"
        controller.pregnancyregistrations()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        PregnancyRegistration.count == 1
    }

    @Ignore
    void "test creating a pregnancyoutcomes with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawPregnancyOutcome><collectedDate>2020-03-12 01:10:01</collectedDate></RawPregnancyOutcome>"
        controller.pregnancyoutcomes()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        PregnancyOutcome.count == 1
    }

    @Ignore
    void "test creating a pregnancychilds with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawPregnancyChild><collectedDate>2020-03-12 01:10:01</collectedDate></RawPregnancyChild>"
        controller.pregnancychilds()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        PregnancyChild.count == 1
    }

    @Ignore
    void "test creating a deaths with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawDeath><collectedDate>2020-03-12 01:10:01</collectedDate></RawDeath>"
        controller.deaths()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        Death.count == 1
    }

    @Ignore
    void "test creating a changeheads with xml"() {
        when:
        // Set request XML body
        controller.request.method = "POST"
        controller.request.xml = "<RawChangeHead><collectedDate>2020-03-12 01:10:01</collectedDate></RawChangeHead>"
        controller.changeheads()

        def response = controller.response
        println("status="+response.status +", text: "+response.text)

        then:
        status == HttpStatus.OK
        HeadRelationship.count == 1
    }

}
