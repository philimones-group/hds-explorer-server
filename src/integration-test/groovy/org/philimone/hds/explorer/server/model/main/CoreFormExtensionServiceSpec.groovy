package org.philimone.hds.explorer.server.model.main

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class CoreFormExtensionServiceSpec extends Specification {

    CoreFormExtensionService coreFormExtensionService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new CoreFormExtension(...).save(flush: true, failOnError: true)
        //new CoreFormExtension(...).save(flush: true, failOnError: true)
        //CoreFormExtension coreFormExtension = new CoreFormExtension(...).save(flush: true, failOnError: true)
        //new CoreFormExtension(...).save(flush: true, failOnError: true)
        //new CoreFormExtension(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //coreFormExtension.id
    }

    void "test get"() {
        setupData()

        expect:
        coreFormExtensionService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<CoreFormExtension> coreFormExtensionList = coreFormExtensionService.list(max: 2, offset: 2)

        then:
        coreFormExtensionList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        coreFormExtensionService.count() == 5
    }

    void "test delete"() {
        Long coreFormExtensionId = setupData()

        expect:
        coreFormExtensionService.count() == 5

        when:
        coreFormExtensionService.delete(coreFormExtensionId)
        sessionFactory.currentSession.flush()

        then:
        coreFormExtensionService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        CoreFormExtension coreFormExtension = new CoreFormExtension()
        coreFormExtensionService.save(coreFormExtension)

        then:
        coreFormExtension.id != null
    }
}
