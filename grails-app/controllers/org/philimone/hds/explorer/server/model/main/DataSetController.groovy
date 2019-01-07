package org.philimone.hds.explorer.server.model.main

import grails.transaction.Transactional
import org.philimone.hds.explorer.io.SystemPath

import static org.springframework.http.HttpStatus.*

class DataSetController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def dataSetService

    def tableList = ["Household","Member","User"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond DataSet.list(params), model:[dataSetInstanceCount: DataSet.count()]
    }

    def show(Long id) {
        [dataSetInstance:  DataSet.get(id)]
    }

    def add() {

        def max = 9

        params.max = Math.min(max ?: 10, 100)
        respond new DataSet(params), model: [tableList:  tableList, dataSetInstanceList: DataSet.list(params)]
    }

    def uploadFile = {

        def file = request.getFile('fileUpload')
        def fileName = file.originalFilename
        def newFile = SystemPath.externalDocsPath + File.separator + fileName

        println "test2 ${file}"
        println "test3 ${file.originalFilename}"

        file.transferTo(new File(newFile))

        //read csv file and get the list of columns
        def columns = dataSetService.getColumns(newFile)

        def dataset = new DataSet(params)
        dataset.name = dataSetService.getDatasetName(fileName)
        dataset.filename = newFile

        render view: "add", model: [dataSetInstance:dataset, dataSetColumns:columns, tableList:  tableList]
    }

    def save(DataSet dataSetInstance) {

        println "testing"

        if (dataSetInstance == null) {
            notFound()
            return
        }

        if (dataSetInstance.hasErrors()) {
            respond dataSetInstance.errors, view:'add'
            return
        }

        if (dataSetInstance.save(flush:true)){
            dataSetService.createZipFile(dataSetInstance)
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'dataSet.label', default: 'DataSet'), dataSetInstance.id])
                redirect dataSetInstance
            }
            '*' { respond dataSetInstance, [status: CREATED] }
        }
    }

    def edit(DataSet dataSetInstance) {
        respond dataSetInstance
    }

    def update(DataSet dataSetInstance) {
        if (dataSetInstance == null) {
            notFound()
            return
        }

        if (dataSetInstance.hasErrors()) {
            respond dataSetInstance.errors, view:'edit'
            return
        }

        dataSetInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'DataSet.label', default: 'DataSet'), dataSetInstance.id])
                redirect dataSetInstance
            }
            '*'{ respond dataSetInstance, [status: OK] }
        }
    }

    def delete(DataSet dataSetInstance) {

        if (dataSetInstance == null) {
            notFound()
            return
        }

        dataSetInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'DataSet.label', default: 'DataSet'), dataSetInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'dataSet.label', default: 'DataSet'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
