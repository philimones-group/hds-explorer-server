package org.philimone.hds.explorer.server.model.main

import grails.converters.JSON
import grails.validation.ValidationException
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.enums.FormCollectType
import org.philimone.hds.explorer.server.model.enums.FormSubjectType
import org.philimone.hds.explorer.server.model.enums.FormType
import org.springframework.dao.DataIntegrityViolationException

import static org.springframework.http.HttpStatus.*

class FormController {

    FormService formService
    DatasetService datasetService
    ModuleService moduleService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        def module_id = params['modules.id']

        Module module = (module_id==null || module_id.empty) ? null : Module.get(module_id)
        List<Form> list = null

        if (module != null){
            list = Form.createCriteria().list(){ //Form.createCriteria().list(params){
                modules{
                    eq('id', module.id)
                }
            }
        }else{
            list = Form.list() //Form.list(params)
        }

        respond list, model:[formCount: formService.count(), currentModule: module]
    }

    def show(String id) {
        println "form id = ${id}"
        def form = Form.get(id)
        def modules = moduleService.findAllByCodes(form.modules)
        respond form, model: [formService: formService, modules: modules]
    }

    def create() {
        //render view: "create", model: [formInstance : new Form(params)]
        def form = new Form(params)

        form.minAge = 0
        form.maxAge = 120

        respond form, model: [regionLevels: formService.regionLevels]
    }

    def save(Form formInstance) {
        if (formInstance == null) {
            notFound()
            return
        }

        boolean isGroupForm = false

        try {

            def modules = Module.getAll(params.list("all_modules.id"))
            modules.each {
                formInstance.addToModules(it.code)
            }

            if (modules.empty){
                flash.message = message(code: 'form.modules.empy.error')
                respond formInstance.errors, view:'create'
                return
            }

            def formSubTypeCode = params.formSubjectType

            println "form type ${formSubTypeCode}, ${formInstance.formSubjectType}"

            FormSubjectType formSubjectType = FormSubjectType.getFrom(formSubTypeCode)


            isGroupForm = formInstance?.formType== FormType.FORM_GROUP
            formInstance.isRegionForm = formSubjectType == FormSubjectType.REGION
            formInstance.isHouseholdForm = formSubjectType == FormSubjectType.HOUSEHOLD
            formInstance.isMemberForm = formSubjectType == FormSubjectType.MEMBER
            formInstance.isHouseholdHeadForm = formSubjectType == FormSubjectType.HOUSEHOLD_HEAD

            def result = formInstance.save(flush:true)

            if (formInstance.hasErrors()) {
                respond formInstance.errors, view:'create'
                return
            } else {
                formInstance = result

            }


        } catch (ValidationException e) {
            respond formInstance.errors, view:'create'
            return
        }

        println "${formInstance.errors}, ${formInstance.id}"


        flash.message = message(code: 'default.created.message', args: [message(code: 'form.label', default: 'Form'), formInstance.formId])
        redirect action: "show", id: formInstance.id, model: [id: formInstance.id, status: CREATED]
    }

    def edit(String id) {
        def form = formService.get(id)
        def modules = moduleService.findAllByCodes(form.modules)
        respond form, model: [modules: modules]
    }

    def update(Form formInstance) {
        if (formInstance == null) {
            notFound()
            return
        }

        try {

            def modules = Module.getAll(params.list("all_modules.id"))
            formService.updateModules(formInstance, modules)
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

    def delete(String id) {
        if (id == null) {
            notFound()
            return
        }

        def formId = Form.get(id).formId


        /* delete dependencies*/
        formService.deleteMappings(id)
        formService.deleteGroupMappings(id)
        formService.delete(id)


        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'form.label', default: 'Form'), formId])
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

        def tableList = formService.getMappingTableList()

        datasetService.datasetNames.each {
            tableList.add("[${it}]")
        }


        def formMapping = new FormMapping(form: formInstance)

        def formsList = Form.list()

        //get form repeat group variables
        def repeatGroups = formService.getRegisteredRepeatGroupVar(formInstance)


        [formInstance: formInstance, repeatGroups: repeatGroups, formMappingInstance:formMapping, formMappingList: mappings, tableList: tableList, formsList: formsList, formService: formService]
    }

    def formGroupMapping(Form formInstance){
        def mappings = FormGroupMapping.findAllByGroupForm(formInstance, [sort:'ordinal', order: 'asc'])

        println "mappings ${mappings.size()}"

        [formInstance: formInstance, formGroupMappingInstance: new FormGroupMapping(), formGroupMappingList: mappings, formService: formService]
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



        if (constValue && constValue.length()>0){
            //formMapping.tableName = "#"
            //formMapping.columnName = constValue

            //if (constValue.startsWith("\$")){
            //    formMapping.tableName = "\$"
            //    formMapping.columnName = constValue.replace("\$","")
            //}

            /* We will deal only with special constants */
            formMapping.tableName = "\$"
            formMapping.columnName = constValue

        }


        /*
        if (splitIndex != null && splitIndex.matches("[0-9]+")){
            int index = Integer.parseInt(splitIndex)
            formMapping.columnName += "[${index}]"
        }
        */
        /*
        if (true){
            params.each{
                println(it)
            }

            render ""
            return
        }*/

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
            redirect action: "formMapping", id: formMapping.form.id
            return
        }


        flash.message = message(code: 'default.created.message', args: [message(code: 'formMapping.label', default: 'Form Mapping'), formMapping.formVariableName])
        redirect action: "formMapping", id: formMapping.form.id
    }

    def deleteFormMapping(String id) {
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

    def saveFormGroupMapping = {
        def formGroupInstance = new FormGroupMapping(params)

        def groupForm = Form.get(params.groupForm)
        def form = Form.findByFormId(params['form.name'])
        def groupOrdinalCount = FormGroupMapping.countByGroupForm(groupForm);

        formGroupInstance.groupForm = groupForm
        formGroupInstance.groupFormId = groupForm.formId
        formGroupInstance.form = form
        formGroupInstance.formId = form.formId
        formGroupInstance.ordinal = groupOrdinalCount
        //required,collectType,collectCondition,collectLabel -> comming from params



        if (formGroupInstance.formCollectType != null && formGroupInstance.formCollectType != FormCollectType.NORMAL_COLLECT && groupOrdinalCount==0) {
            println "must be normal collect because its the first form to be collected"

            flash.message = g.message(code: "form.groupMapping.formCollectType.first.error")

            def mappings = FormGroupMapping.findAllByGroupForm(groupForm, [sort:'ordinal', order: 'asc'])
            render view: "formGroupMapping", model: [formInstance: groupForm, formGroupMappingInstance: formGroupInstance, formGroupMappingList: mappings, formService: formService]
            return
        }



        if (!formGroupInstance.save(flush:true)){

            def mappings = FormGroupMapping.findAllByGroupForm(groupForm, [sort:'ordinal', order: 'asc'])

            respond formGroupInstance.errors, view:'formGroupMapping', model: [formInstance: groupForm, formGroupMappingInstance: formGroupInstance, formGroupMappingList: mappings, formService: formService]
            return
        } else {
            form.addToGroupMappings(formGroupInstance)
            form.save(flush:true)

            if (form.hasErrors()) {
                println("Uncaught Error:\n${form.errors}")
            }
        }



        flash.message = message(code: 'form.groupMapping.created.label', args: [form.formId, groupForm.formId])
        redirect action: "formGroupMapping", id: groupForm.id

    }

    def generateFormGroupIdGsp = {

        def formName = params.formName

        def code = formService.generateGroupId(formName)

        println "new generated code: ${code}"

        render "${code}"
    }

    def modelVariables = {
        def modelName = params.name

        def list = []

        if (modelName.equals("Household")){
            list = Household.ALL_COLUMNS
        } else if (modelName.equals("Member")){
            list = Member.ALL_COLUMNS
        } else if (modelName.equals("User")){
            list = User.ALL_COLUMNS
        } else if (modelName.equals("Region")){
            list = Region.ALL_COLUMNS
        } else if (modelName.equals("FollowUp-List")) {
            list = TrackingListMapping.ALL_COLUMNS
        } else if (modelName.equals("Form-Group")) {
            list = FormGroupMapping.ALL_COLUMNS
        } else if (datasetService.containsDatasetWith(modelName)){
            list = datasetService.getDatasetColumnsWith(modelName)
        }

        //println "list ${list}"

        render g.select(id: 'columnName', name: "columnName", from: list)
    }

    def modelMemberTable = {
        def tableList = ["Member"]

        render g.select(id: 'tableName', name: "tableName", from: tableList)
    }

    def modelTableList = {

        def modelName = params.name
        def tableList = ["Member"]

        println "model ${modelName}"

        if (modelName == "" || modelName==null){
            tableList = ["Household","Member","User","Region"]
            tableList.addAll(datasetService.datasetNames)
        }


        render g.select(id: 'tableName', name: "tableName", from: tableList, noSelection : ['': ''])
    }

    def modelFormatTypes = {
        String id = params.id
        def format = ""

        //println "id = ${id}, ${params.id}"

        def formatType = MappingFormatType.get(id)

        if (formatType != null){
            format = formatType.format
        }

        render format
    }

    def formsList = {
        render formService.formsList(params) as JSON
    }

}
