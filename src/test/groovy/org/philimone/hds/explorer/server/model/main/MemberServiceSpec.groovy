package org.philimone.hds.explorer.server.model.main

import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import grails.testing.spring.AutowiredTest
import org.philimone.hds.explorer.server.model.authentication.Role
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.authentication.UserRole
import org.philimone.hds.explorer.server.model.authentication.UserService
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
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
        service.householdService = householdService
        service.userService = userService

        service.errorMessageService.messageSource = getI18n()

        setupRegions()
        setupHouseholds()
        //inject message source

    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}
