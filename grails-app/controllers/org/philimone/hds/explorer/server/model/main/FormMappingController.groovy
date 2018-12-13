package org.philimone.hds.explorer.server.model.main

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class FormMappingController {

    FormMappingService formMappingService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond formMappingService.list(params), model:[formMappingCount: formMappingService.count()]
    }

    def show(Long id) {
        respond formMappingService.get(id)
    }

    def create() {
        respond new FormMapping(params)
    }

    def save(FormMapping formMapping) {
        if (formMapping == null) {
            notFound()
            return
        }

        try {
            formMappingService.save(formMapping)
        } catch (ValidationException e) {
            respond formMapping.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'formMapping.label', default: 'FormMapping'), formMapping.id])
                redirect formMapping
            }
            '*' { respond formMapping, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond formMappingService.get(id)
    }

    def update(FormMapping formMapping) {
        if (formMapping == null) {
            notFound()
            return
        }

        try {
            formMappingService.save(formMapping)
        } catch (ValidationException e) {
            respond formMapping.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'formMapping.label', default: 'FormMapping'), formMapping.id])
                redirect formMapping
            }
            '*'{ respond formMapping, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        formMappingService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'formMapping.label', default: 'FormMapping'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'formMapping.label', default: 'FormMapping'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
