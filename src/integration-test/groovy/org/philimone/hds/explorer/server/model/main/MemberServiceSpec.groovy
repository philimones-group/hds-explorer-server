package org.philimone.hds.explorer.server.model.main

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class MemberServiceSpec extends Specification {

    MemberService memberService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Member(...).save(flush: true, failOnError: true)
        //new Member(...).save(flush: true, failOnError: true)
        //Member member = new Member(...).save(flush: true, failOnError: true)
        //new Member(...).save(flush: true, failOnError: true)
        //new Member(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //member.id
    }

    void "test get"() {
        setupData()

        expect:
        memberService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Member> memberList = memberService.list(max: 2, offset: 2)

        then:
        memberList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        memberService.count() == 5
    }

    void "test delete"() {
        Long memberId = setupData()

        expect:
        memberService.count() == 5

        when:
        memberService.delete(memberId)
        sessionFactory.currentSession.flush()

        then:
        memberService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Member member = new Member()
        memberService.save(member)

        then:
        member.id != null
    }
}
