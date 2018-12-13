package org.philimone.hds.explorer.server.model.main

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class FormMappingServiceSpec extends Specification {

    FormMappingService formMappingService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new FormMapping(...).save(flush: true, failOnError: true)
        //new FormMapping(...).save(flush: true, failOnError: true)
        //FormMapping formMapping = new FormMapping(...).save(flush: true, failOnError: true)
        //new FormMapping(...).save(flush: true, failOnError: true)
        //new FormMapping(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //formMapping.id
    }

    void "test get"() {
        setupData()

        expect:
        formMappingService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<FormMapping> formMappingList = formMappingService.list(max: 2, offset: 2)

        then:
        formMappingList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        formMappingService.count() == 5
    }

    void "test delete"() {
        Long formMappingId = setupData()

        expect:
        formMappingService.count() == 5

        when:
        formMappingService.delete(formMappingId)
        sessionFactory.currentSession.flush()

        then:
        formMappingService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        FormMapping formMapping = new FormMapping()
        formMappingService.save(formMapping)

        then:
        formMapping.id != null
    }
}
