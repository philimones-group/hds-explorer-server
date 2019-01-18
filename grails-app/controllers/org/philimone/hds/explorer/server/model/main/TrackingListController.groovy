package org.philimone.hds.explorer.server.model.main

import grails.transaction.Transactional
import org.philimone.hds.explorer.io.SystemPath

import static org.springframework.http.HttpStatus.*

class TrackingListController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def trackingListService

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

        def max = 9

        params.max = Math.min(max ?: 10, 100)
        respond new TrackingList(params), model: [tableList:  tableList, trackingListInstanceList: TrackingList.list(params)]
    }

    def uploadFile = {

        def file = request.getFile('fileUpload')
        def fileName = file.originalFilename
        def newFile = SystemPath.externalDocsPath + File.separator + fileName

        println "test2 ${file}"
        println "test3 ${file.originalFilename}"

        file.transferTo(new File(newFile))

        //read xls file and read tracking lists
        //validate tracking lists first

        def trackingList = trackingListService.getFirstTrackingList(newFile)
        trackingList.filename = newFile

        render view: "add", model: [trackingListInstance:trackingList, tableList:  tableList]
    }

    def save(TrackingList trackingListInstance) {
        if (trackingListInstance == null) {
            notFound()
            return
        }

        if (trackingListInstance.hasErrors()) {
            respond trackingListInstance.errors, view:'create'
            return
        }

        if (trackingListInstance.save(flush:true)){
            //
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'trackingList.label', default: 'TrackingList'), trackingListInstance.id])
                redirect trackingListInstance
            }
            '*' { respond trackingListInstance, [status: CREATED] }
        }
    }

    def edit(TrackingList trackingListInstance) {
        respond trackingListInstance
    }

    def update(TrackingList trackingListInstance) {
        if (trackingListInstance == null) {
            notFound()
            return
        }

        if (trackingListInstance.hasErrors()) {
            respond trackingListInstance.errors, view:'edit'
            return
        }

        trackingListInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'TrackingList.label', default: 'TrackingList'), trackingListInstance.id])
                redirect trackingListInstance
            }
            '*'{ respond trackingListInstance, [status: OK] }
        }
    }

    def delete(TrackingList trackingListInstance) {

        if (trackingListInstance == null) {
            notFound()
            return
        }

        trackingListInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'TrackingList.label', default: 'TrackingList'), trackingListInstance.id])
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
}
