package org.philimone.hds.explorer.server.model.main

import com.sun.org.apache.xpath.internal.operations.Bool
import grails.validation.ValidationException
import org.philimone.hds.explorer.io.SystemPath

import static org.springframework.http.HttpStatus.*

class CoreFormExtensionController {

    CoreFormExtensionService coreFormExtensionService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        //params.max = Math.min(max ?: 25, 100)
        respond coreFormExtensionService.list(params), model:[coreFormExtensionCount: coreFormExtensionService.count()]
    }

    def updateForms = {
        def mapr = params.list("required").get(0)
        def mape = params.list("enabled").get(0)

        def formsEnabled = new LinkedHashMap<String, Boolean>()
        def formsRequired = ["household_ext":false,  "visit_ext":false, "member_ext":false, "marital_relationship_ext":false,
                     "inmigration_ext":false, "outmigration_ext":false, "pregnancy_registration_ext":false, "pregnancy_outcome_ext":false,
                     "death_ext":false, "change_head_ext":false, "incomplete_visit_ext":false
        ]
        formsEnabled.putAll(formsRequired)


        mapr.each { r ->
            //println "requiredx: ${r}, ${r.key} -> ${r.value}"

            if (formsRequired.containsKey(r.key)){
                formsRequired.put(r.key, r.value.equals("on"))
            }

        }

        mape.each { e ->
            //println "enabledx: ${e}"

            if (formsEnabled.containsKey(e.key)){
                formsEnabled.put(e.key, e.value.equals("on"))
            }
        }

        formsRequired.each { key, value ->
            boolean enabled = formsEnabled.get(key)

            def formExt = CoreFormExtension.findByExtFormId(key)

            formExt.required = value
            formExt.enabled = enabled

            formExt.save(flush:true)
        }

        redirect action: "index"
    }

    def downloadFormXLS = {

        def coreFormExt = CoreFormExtension.get(params.id)

        def file = coreFormExtensionService.getFormXLS(coreFormExt)
        render file: file, fileName: file.getName(), contentType:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    }

    /*
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
    */
}
