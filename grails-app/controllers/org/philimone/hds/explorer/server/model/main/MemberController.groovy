package org.philimone.hds.explorer.server.model.main

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class MemberController {

    MemberService memberService
    HouseholdService householdService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond memberService.list(params), model:[memberCount: memberService.count()]
    }

    def show(String id) {
        respond memberService.get(id)
    }

    def create() {
        respond new Member(params)
    }

    def save(Member member) {
        if (member == null) {
            notFound()
            return
        }

        try {
            memberService.save(member)
        } catch (ValidationException e) {
            respond member.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'member.label', default: 'Member'), member.id])
                redirect member
            }
            '*' { respond member, [status: CREATED] }
        }
    }

    def edit(String id) {
        respond memberService.get(id)
    }

    def update(Member member) {
        if (member == null) {
            notFound()
            return
        }

        try {
            memberService.save(member)
        } catch (ValidationException e) {
            respond member.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'member.label', default: 'Member'), member.id])
                redirect member
            }
            '*'{ respond member, [status: OK] }
        }
    }

    def delete(String id) {
        if (id == null) {
            notFound()
            return
        }

        memberService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'member.label', default: 'Member'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'member.label', default: 'Member'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def showHousehold = {
        def household = householdService.getHousehold(params.id)

        redirect controller: "household", action: "show", id: household.id
    }
}
