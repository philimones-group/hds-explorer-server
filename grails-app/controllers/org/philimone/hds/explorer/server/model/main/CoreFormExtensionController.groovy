package org.philimone.hds.explorer.server.model.main

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class CoreFormExtensionController {

    CoreFormExtensionService coreFormExtensionService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond coreFormExtensionService.list(params), model:[coreFormExtensionCount: coreFormExtensionService.count()]
    }

    def show(Long id) {
        respond coreFormExtensionService.get(id)
    }

    def create() {
        respond new CoreFormExtension(params)
    }

    def save(CoreFormExtension coreFormExtension) {
        if (coreFormExtension == null) {
            notFound()
            return
        }

        try {
            coreFormExtensionService.save(coreFormExtension)
        } catch (ValidationException e) {
            respond coreFormExtension.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'coreFormExtension.label', default: 'CoreFormExtension'), coreFormExtension.id])
                redirect coreFormExtension
            }
            '*' { respond coreFormExtension, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond coreFormExtensionService.get(id)
    }

    def update(CoreFormExtension coreFormExtension) {
        if (coreFormExtension == null) {
            notFound()
            return
        }

        try {
            coreFormExtensionService.save(coreFormExtension)
        } catch (ValidationException e) {
            respond coreFormExtension.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'coreFormExtension.label', default: 'CoreFormExtension'), coreFormExtension.id])
                redirect coreFormExtension
            }
            '*'{ respond coreFormExtension, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        coreFormExtensionService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'coreFormExtension.label', default: 'CoreFormExtension'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'coreFormExtension.label', default: 'CoreFormExtension'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
