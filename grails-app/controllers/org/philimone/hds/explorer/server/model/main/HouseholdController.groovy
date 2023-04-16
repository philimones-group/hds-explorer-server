package org.philimone.hds.explorer.server.model.main

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class HouseholdController {

    HouseholdService householdService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond householdService.list(params), model:[householdCount: householdService.count()]
    }

    def show(String id) {
        def household = householdService.get(id)
        def residentsList = householdService.getResidentMembers(household)
        respond household, model: [residentsList: residentsList]

        println "class name: " + household.class.name
    }

    def create() {
        respond new Household(params)
    }

    def save(Household household) {
        if (household == null) {
            notFound()
            return
        }

        try {
            householdService.save(household)
        } catch (ValidationException e) {
            respond household.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'household.label', default: 'Household'), household.id])
                redirect household
            }
            '*' { respond household, [status: CREATED] }
        }
    }

    def edit(String id) {
        respond householdService.get(id)
    }

    def update(Household household) {
        if (household == null) {
            notFound()
            return
        }

        try {
            householdService.save(household)
        } catch (ValidationException e) {
            respond household.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'household.label', default: 'Household'), household.id])
                redirect household
            }
            '*'{ respond household, [status: OK] }
        }
    }

    def delete(String id) {
        if (id == null) {
            notFound()
            return
        }

        householdService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'household.label', default: 'Household'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'household.label', default: 'Household'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
