package org.philimone.hds.explorer.server.model.main

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class RoundServiceSpec extends Specification {

    RoundService roundService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Round(...).save(flush: true, failOnError: true)
        //new Round(...).save(flush: true, failOnError: true)
        //Round round = new Round(...).save(flush: true, failOnError: true)
        //new Round(...).save(flush: true, failOnError: true)
        //new Round(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //round.id
    }

    void "test get"() {
        setupData()

        expect:
        roundService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Round> roundList = roundService.list(max: 2, offset: 2)

        then:
        roundList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        roundService.count() == 5
    }

    void "test delete"() {
        Long roundId = setupData()

        expect:
        roundService.count() == 5

        when:
        roundService.delete(roundId)
        sessionFactory.currentSession.flush()

        then:
        roundService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Round round = new Round()
        roundService.save(round)

        then:
        round.id != null
    }
}
