package org.philimone.hds.explorer.server.model.main

import grails.converters.JSON
import grails.validation.ValidationException
import org.philimone.hds.explorer.authentication.User
import org.springframework.dao.DataIntegrityViolationException

import static org.springframework.http.HttpStatus.*

class FormController {

    FormService formService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        def module_id = params['modules.id']

        StudyModule module = (module_id==null || module_id.empty) ? null : StudyModule.get(module_id)
        List<Form> list = null

        if (module != null){
            list = Form.createCriteria().list(params){
                modules{
                    eq('id', module.id)
                }
            }
        }else{
            list = Form.list(params)
        }


        respond list, model:[formCount: formService.count(), currentModule: module]
    }

    def show(Long id) {
        respond Form.get(id)
    }

    def create() {
        //render view: "create", model: [formInstance : new Form(params)]
        def form = new Form(params)

        form.minAge = 0
        form.maxAge = 120

        respond form
    }

    def save(Form formInstance) {
        if (formInstance == null) {
            notFound()
            return
        }

        try {

            def modules = StudyModule.getAll(params.list("all_modules.id"))
            modules.each {
                formInstance.addToModules(it)
            }

            formInstance.save(flush:true)


        } catch (ValidationException e) {
            respond formInstance.errors, view:'create'
            return
        }

        println "${formInstance.errors}"

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'form.label', default: 'Form'), formInstance.id])
                redirect formInstance
            }
            '*' { respond formInstance, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond formService.get(id)
    }

    def update(Form formInstance) {
        if (formInstance == null) {
            notFound()
            return
        }

        try {

            def modules = StudyModule.getAll(params.list("all_modules.id"))

            modules.each {
                formInstance.addToModules(it)
            }


            formService.save(formInstance)
        } catch (ValidationException e) {
            respond formInstance.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'form.label', default: 'Form'), formInstance.id])
                redirect formInstance
            }
            '*'{ respond formInstance, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        formService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'form.label', default: 'Form'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'form.label', default: 'Form'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }


    /* Form Mapping Variables */

    def formMapping(Form formInstance){
        def mappings = FormMapping.executeQuery("select m from FormMapping m where m.form=? order by m.id", [formInstance]) //FormMapping.findAllByForm(formInstance)

        def tableList = ["Household","Member","User"]

        def formMapping = new FormMapping(form: formInstance)

        def formsList = Form.list()

        [formInstance: formInstance, formMappingInstance:formMapping, formMappingList: mappings, tableList: tableList, formsList: formsList]
    }

    def copyFrom = {
        def formMapping = new FormMapping(params)
        def formId = params.formToCopy
        def formCopiedFrom = Form.get(formId)

        def formMappings = FormMapping.findAllByForm(formCopiedFrom)

        println "VALUE ${formId}, current Form: ${formMapping.form}, ${formMappings}"

        formMappings.each{ FormMapping fm ->
            def mapping = new FormMapping()
            mapping.form = formMapping.form
            mapping.formVariableName = fm.formVariableName
            mapping.tableName = fm.tableName
            mapping.columnName = fm.columnName
            mapping.columnFormat = fm.columnFormat

            mapping.save(flush: true)

            //println "errors: ${mapping.errors}"
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'formMapping.label', default: 'Form Mapping'), " copied from "])
        redirect action: "formMapping", id: formMapping.form.id
    }

    def saveFormMapping = {
        def formMapping = new FormMapping(params)
        //println "prs: "+params
        //println "obj: "+formMapping

        def splitIndex = params.columnSplitIndex
        def String constValue = params.columnConstantValue

        println "ftype: ${formMapping.columnFormat}, format: ${formMapping.columnFormat?.format}"

        if (formMapping == null) {
            notFound()
            return
        }

        /*
        if (constValue.length()>0){
            formMapping.tableName = "#"
            formMapping.columnName = constValue

            if (constValue.startsWith("\$")){
                formMapping.tableName = "\$"
                formMapping.columnName = constValue.replace("\$","")
            }
        }
        */

        /*
        if (splitIndex != null && splitIndex.matches("[0-9]+")){
            int index = Integer.parseInt(splitIndex)
            formMapping.columnName += "[${index}]"
        }
        */

        def formatType = formMapping.columnFormat
        if (formatType != null){

            if (formMapping.columnFormatValue == null || formMapping.columnFormatValue.isEmpty()){
                formMapping.columnFormatValue = formatType.format
            }

            //formMapping.columnFormat = formatType.getValue()
            //formatType.discard()
        }

        if (formMapping.hasErrors()) {
            respond formMapping.errors, view:'formMapping'
            return
        }

        if (!formMapping.save(flush:true)){
            flash.message = message(code: 'default.created.message', args: [message(code: 'formMapping.label', default: 'Form Mapping'), formMapping.id])
            redirect action: "varBinder", id: formMapping.form.id
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'formMapping.label', default: 'Form Mapping'), formMapping.formVariableName])
        redirect action: "formMapping", id: formMapping.form.id
    }

    def deleteFormMapping(Long id) {
        def formMapping = FormMapping.get(id)

        def form = formMapping?.form

        if (!formMapping) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'formMapping.label', default: 'Form Mapping'), id])

            if (form){
                redirect action: "formMapping", id: form.id
            }else
                redirect(action: "index")

            return
        }

        try {

            formMapping.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'formMapping.label', default: 'Form Mapping'), id])

            redirect action: "formMapping", id: form.id

        } catch (DataIntegrityViolationException e) {

            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'formMapping.label', default: 'Form Mapping'), id])
            redirect action: "formMapping", id: form.id
        }
    }

    def modelVariables = {
        def modelName = params.name

        def list = []

        if (modelName.equals("Household")){
            list = Household.ALL_COLUMNS
        }

        if (modelName.equals("Member")){
            list = Member.ALL_COLUMNS
        }

        if (modelName.equals("User")){
            list = User.ALL_COLUMNS
        }

        //println "list ${list}"

        render g.select(id: 'columnName', name: "columnName", from: list)
    }

    def modelFormatTypes = {
        Long id = params.id=="" ? null : Long.parseLong(params.id)
        def format = ""

        //println "id = ${id}, ${params.id}"

        def formatType = MappingFormatType.get(id)

        if (formatType != null){
            format = formatType.format
        }

        render format
    }

}
