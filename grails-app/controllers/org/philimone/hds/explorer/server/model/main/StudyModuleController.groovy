package org.philimone.hds.explorer.server.model.main

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class StudyModuleController {

    StudyModuleService studyModuleService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond studyModuleService.list(params), model:[studyModuleCount: studyModuleService.count()]
    }

    def show(String id) {
        respond studyModuleService.get(id)
    }

    def create() {
        respond new StudyModule(params)
    }

    def save(StudyModule studyModule) {
        if (studyModule == null) {
            notFound()
            return
        }

        try {
            studyModuleService.save(studyModule)
        } catch (ValidationException e) {
            respond studyModule.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'studyModule.label', default: 'StudyModule'), studyModule.id])
                redirect studyModule
            }
            '*' { respond studyModule, [status: CREATED] }
        }
    }

    def edit(String id) {
        respond studyModuleService.get(id)
    }

    def update(StudyModule studyModule) {
        if (studyModule == null) {
            notFound()
            return
        }

        try {
            studyModuleService.save(studyModule)
        } catch (ValidationException e) {
            respond studyModule.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'studyModule.label', default: 'StudyModule'), studyModule.id])
                redirect studyModule
            }
            '*'{ respond studyModule, [status: OK] }
        }
    }

    def delete(String id) {
        if (id == null) {
            notFound()
            return
        }

        studyModuleService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'studyModule.label', default: 'StudyModule'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'studyModule.label', default: 'StudyModule'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
