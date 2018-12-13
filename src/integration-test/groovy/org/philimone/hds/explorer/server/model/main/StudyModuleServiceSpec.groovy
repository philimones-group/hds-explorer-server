package org.philimone.hds.explorer.server.model.main

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class StudyModuleServiceSpec extends Specification {

    StudyModuleService studyModuleService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new StudyModule(...).save(flush: true, failOnError: true)
        //new StudyModule(...).save(flush: true, failOnError: true)
        //StudyModule studyModule = new StudyModule(...).save(flush: true, failOnError: true)
        //new StudyModule(...).save(flush: true, failOnError: true)
        //new StudyModule(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //studyModule.id
    }

    void "test get"() {
        setupData()

        expect:
        studyModuleService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<StudyModule> studyModuleList = studyModuleService.list(max: 2, offset: 2)

        then:
        studyModuleList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        studyModuleService.count() == 5
    }

    void "test delete"() {
        Long studyModuleId = setupData()

        expect:
        studyModuleService.count() == 5

        when:
        studyModuleService.delete(studyModuleId)
        sessionFactory.currentSession.flush()

        then:
        studyModuleService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        StudyModule studyModule = new StudyModule()
        studyModuleService.save(studyModule)

        then:
        studyModule.id != null
    }
}
