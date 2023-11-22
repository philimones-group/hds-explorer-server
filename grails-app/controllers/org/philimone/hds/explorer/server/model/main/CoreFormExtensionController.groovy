package org.philimone.hds.explorer.server.model.main

import com.sun.org.apache.xpath.internal.operations.Bool
import grails.validation.ValidationException
import net.betainteractive.io.odk.util.XFormReader
import org.philimone.hds.explorer.io.SystemPath
import org.springframework.web.multipart.MultipartFile

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
        def formsRequired = ["region_ext":false, "household_ext":false,  "visit_ext":false, "member_ext":false, "marital_relationship_ext":false,
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

    def updateFormsTable = {
        def value = params.name as String

        //println params
        //println value

        if (value != null) {

            def spt = value.split(":")
            def names = spt[0].split("\\.")
            def name_var = names[0]
            def name_form = names[1]
            def checked = Boolean.parseBoolean(spt[1])

            def formExt = CoreFormExtension.findByExtFormId(name_form)

            if ("required".equalsIgnoreCase(name_var)) {
                formExt.required = checked
            } else if ("enabled".equalsIgnoreCase(name_var)) {
                formExt.enabled = checked
            }

            formExt.save(flush: true)
        }

        render "OK"
    }

    def downloadFormXLS = {

        def coreFormExt = CoreFormExtension.get(params.id)

        def file = coreFormExtensionService.getFormXLS(coreFormExt)
        render file: file, fileName: file.getName(), contentType:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    }

    def downloadFormDef = {
        def coreFormExt = CoreFormExtension.get(params.id)
        def filename = coreFormExt.extFormId + ".xml"
        def file = coreFormExtensionService.getFormXLS(coreFormExt)
        render file: coreFormExt.extFormDefinition, fileName: filename, contentType:"application/xml"
    }

    def uploadFormDef = {
        //println "uploaded "+fileName+ ", formID=${params.formId}"

        def formId = params.formId as String
        def file = request.getFile('fileUpload') as MultipartFile
        def fileName = file?.originalFilename
        def xmlBytes = file?.getBytes()

        //get form id, and compare to the coreext form id
        def xmlFormId = XFormReader.getFormId(xmlBytes)

        println "${xmlFormId}, ${formId}"

        if (xmlFormId == null) {
            def errorMessages = [g.message(code: "coreFormExtension.uploadForm.xmlform.error", args: [fileName])]
            render view: "index", model:[coreFormExtensionList: coreFormExtensionService.list(params), coreFormExtensionCount: coreFormExtensionService.count(), errorMessages : errorMessages]
            return
        }

        if (formId == null) {
            def errorMessages = [g.message(code: "coreFormExtension.uploadForm.formid.error", args: [])]
            render view: "index", model:[coreFormExtensionList: coreFormExtensionService.list(params), coreFormExtensionCount: coreFormExtensionService.count(), errorMessages : errorMessages]
            return
        }

        if (!formId.equals(xmlFormId)) {
            def errorMessages = [g.message(code: "coreFormExtension.uploadForm.form.not.match.error", args: [xmlFormId, formId])]
            render view: "index", model:[coreFormExtensionList: coreFormExtensionService.list(params), coreFormExtensionCount: coreFormExtensionService.count(), errorMessages : errorMessages]
            return
        }

        def coreFormExtension = CoreFormExtension.findByExtFormId(formId)
        if (coreFormExtension == null) {
            def errorMessages = [g.message(code: "coreFormExtension.uploadForm.coreext.not.found.error", args: [formId])]
            render view: "index", model:[coreFormExtensionList: coreFormExtensionService.list(params), coreFormExtensionCount: coreFormExtensionService.count(), errorMessages : errorMessages]
            return
        }

        coreFormExtension.extFormDefinition = xmlBytes
        coreFormExtension.save(flush:true)
        flash.message = g.message(code: "coreFormExtension.uploadForm.success.label", args: [fileName, formId])

        render view: "index", model:[coreFormExtensionList: coreFormExtensionService.list(params), coreFormExtensionCount: coreFormExtensionService.count()]
    }

}
