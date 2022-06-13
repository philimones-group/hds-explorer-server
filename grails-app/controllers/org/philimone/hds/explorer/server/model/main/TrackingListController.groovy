package org.philimone.hds.explorer.server.model.main

import grails.validation.ValidationException
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.SubjectType
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile

import static org.springframework.http.HttpStatus.*

class TrackingListController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def trackingListService
    def codeGeneratorService
    def moduleService
    def errorMessageService

    def tableList = ["Household","Member","Region","User"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond TrackingList.list(params), model:[trackingListInstanceCount: TrackingList.count()]
    }

    def show(TrackingList trackingListInstance) {
        [trackingListInstance:  trackingListInstance]
    }

    def create() {
        redirect action: "add"
    }

    def add() {
        respond new TrackingList(params), model: [tableList:  tableList]
    }

    def uploadFile = {

        def file = request.getFile('fileUpload')
        def fileName = file.originalFilename
        def newFile = "/tmp/tracklist-web-${GeneralUtil.generateUUID()}" //SystemPath.externalDocsPath + File.separator + fileName


        file.transferTo(new File(newFile))

        //read xls file and read tracking lists
        //validate tracking lists first
        def validationResult = trackingListService.validateXls(newFile.toString())

        if (validationResult.status == TrackingListService.ValidationStatus.ERROR) {
            params.filename = fileName
            render view: "add", model: [trackingListInstance: new TrackingList(params), absoluteFilename: newFile, tableList:  tableList, errorMessages: validationResult.errorMessages]
            return
        }

        params.code = validationResult.isUpdating ? validationResult.xlsContent.code : codeGeneratorService.generateTrackingListCode()
        params.name = validationResult.xlsContent.name
        params.filename = fileName
        params.enabled = validationResult.xlsContent.enabled

        def modules = Module.findAllByCodeInList(validationResult.xlsContent.modules)

        render view: "add", model: [absoluteFilename: newFile, isUpdating: validationResult.isUpdating, trackingListInstance: new TrackingList(params), tableList:  tableList, modules: modules]
    }

    def save(TrackingList trackingListInstance) {
        if (trackingListInstance == null) {
            notFound()
            return
        }

        def modules = Module.getAll(params.list("all_modules.id"))

        modules.each {
            trackingListInstance.addToModules(it.code)
        }

        def validationResult = trackingListService.validateXls(params.absoluteFilename)

        if (validationResult.status == TrackingListService.ValidationStatus.ERROR) {
            render view:'add', model: [trackingListInstance: trackingListInstance, absoluteFilename: params.absoluteFilename, isUpdating: validationResult.isUpdating, tableList:  tableList, errorMessages: validationResult.errorMessages, modules: modules]
            return
        }

        //saving
        def saveResult = trackingListService.save(trackingListInstance, validationResult)

        if (saveResult.errors != null && saveResult.errors.size()>0){
            render view:'add', model: [absoluteFilename: params.absoluteFilename, isUpdating: validationResult.isUpdating, tableList:  tableList, modules: modules, errorMessages: saveResult.errors]
            return
        }

        trackingListInstance = saveResult.instance

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'trackingList.label', default: 'TrackingList'), trackingListInstance.name])
                redirect trackingListInstance
            }
            '*' { respond trackingListInstance, [status: CREATED] }
        }
    }

    def delete(TrackingList trackingListInstance) {

        if (trackingListInstance == null) {
            notFound()
            return
        }

        //delete all that belongs to that Tracking List
        trackingListService.delete(trackingListInstance)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'trackingList.label', default: 'TrackingList'), trackingListInstance.name])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'trackingList.label', default: 'TrackingList'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def downloadSampleXLS = {
        def file = trackingListService.getSampleFileXLS()
        render file: file, fileName: file.getName(), contentType:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    }

    def downloadTemplateXLS = {
        def file = trackingListService.getSampleFileEmptyXLS()
        render file: file, fileName: file.getName(), contentType:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    }

    def addxls = {
        println "type=${request.contentType}"

        def file = request.getFile('xls_file')
        def fileName = file.originalFilename
        def newFile = "/tmp/tracklist-web-${GeneralUtil.generateUUID()}" //SystemPath.externalDocsPath + File.separator + fileName

        file.transferTo(new File(newFile))


        println "name=${fileName}"
        //println("content=${request.inputStream==null}")

        if (new File(newFile).exists()) {
            def validationResult = trackingListService.validateXls(newFile)
            def xlsContent = validationResult.xlsContent

            if (validationResult.status == TrackingListService.ValidationStatus.ERROR) {
                render text: errorMessageService.getRawMessagesText(validationResult.errorMessages), status: BAD_REQUEST
                return
            }

            //saving
            def saveResult = trackingListService.save(null, validationResult)

            if (saveResult?.errors != null && saveResult?.errors.size()>0){
                render text: errorMessageService.getRawMessagesText(saveResult.errors), status: BAD_REQUEST
                return
            }

            render text: "${saveResult.instance.id}", status: CREATED
            return
        } else {

            render text: "Coulnd't read the XLS file ${fileName}", status: BAD_REQUEST
            return
        }
    }

    def get(String id) {
        //create XLS from sample and trackinglist

        def trackingList = TrackingList.get(id)

        if (trackingList == null) {
            render text: "Follow-up list with id=${id} was not found", status: BAD_REQUEST
            return
        }

        def file = trackingListService.createTempTrackingListXLS(trackingList)

        if (file == null) {
            render text: "Couldnt create Excel file for Follow-up list with id=${id}", status: BAD_REQUEST
            return
        }


        render file: file, fileName: trackingList.filename, contentType:"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    }
}
