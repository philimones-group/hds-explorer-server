package org.philimone.hds.explorer.server.model.main

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class HouseholdServiceSpec extends Specification {

    HouseholdService householdService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Household(...).save(flush: true, failOnError: true)
        //new Household(...).save(flush: true, failOnError: true)
        //Household household = new Household(...).save(flush: true, failOnError: true)
        //new Household(...).save(flush: true, failOnError: true)
        //new Household(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //household.id
    }

    void "test get"() {
        setupData()

        expect:
        householdService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Household> householdList = householdService.list(max: 2, offset: 2)

        then:
        householdList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        householdService.count() == 5
    }

    void "test delete"() {
        Long householdId = setupData()

        expect:
        householdService.count() == 5

        when:
        householdService.delete(householdId)
        sessionFactory.currentSession.flush()

        then:
        householdService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Household household = new Household()
        householdService.save(household)

        then:
        household.id != null
    }
}
