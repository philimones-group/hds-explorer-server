package org.philimone.hds.explorer.server.model.main

import grails.validation.ValidationException
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.io.SystemPath

import static org.springframework.http.HttpStatus.*

class DatasetController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def datasetService
    def moduleService

    def tableList = ["Household","Member","Region","User"]

    def index(Integer max) {
        //params.max = Math.min(max ?: 10, 100)
        respond Dataset.list(), model:[dataSetInstanceCount: Dataset.count()]
    }

    def show(String id) {

        def datasetInstance = Dataset.get(id)

        def modules = moduleService.findAllByCodes(datasetInstance.modules)

        [dataSetInstance: datasetInstance, modules: modules]
    }

    def create = {
        redirect action: "add"
    }

    def add() {

        def max = 9

        //params.max = Math.min(max ?: 10, 100)
        respond new Dataset(params), model: [tableList:  tableList, dataSetInstanceList: Dataset.list(params)]
    }

    def uploadFile = {

        def file = request.getFile('fileUpload')
        def fileName = file.originalFilename
        def newFile = SystemPath.externalDocsPath + File.separator + fileName

        println "test ${file.originalFilename}"

        file.transferTo(new File(newFile))

        //read csv file and get the list of columns
        def columnsMap = datasetService.getColumns(newFile)

        //retrive labels
        def labels = ""
        columnsMap.values().each {
            labels += (labels.empty ? "":",") + it
        }

        def dataset = new Dataset(params)
        dataset.name = datasetService.getDatasetName(fileName)
        dataset.filename = newFile
        dataset.tableColumnLabels = labels

        render view: "add", model: [dataSetInstance:dataset, dataSetColumns:columnsMap.keySet(), tableList:  tableList]
    }

    def changeUploadFile = {

        def file = request.getFile('fileUpload')
        def fileName = file.originalFilename
        def newFile = SystemPath.externalDocsPath + File.separator + fileName

        println "test3 ${file.originalFilename}"

        file.transferTo(new File(newFile))

        //read csv file and get the list of columns
        def columnsMap = datasetService.getColumns(newFile)

        //retrive labels
        def labels = ""
        columnsMap.values().each {
            labels += (labels.empty ? "":",") + it
        }

        def datasetInstance = Dataset.get(params.datasetId)
        def modules = moduleService.findAllByCodes(datasetInstance.modules)

        datasetInstance.name = datasetService.getDatasetName(fileName)
        datasetInstance.filename = newFile
        datasetInstance.tableColumnLabels = labels

        render view: "edit", model: [datasetInstance:datasetInstance, modules: modules, dataSetColumns:columnsMap.keySet(), tableList:  tableList, dataSetInstanceList: Dataset.list(params)]
    }

    def save(Dataset dataSetInstance) {

        println "testing"

        if (dataSetInstance == null) {
            notFound()
            return
        }
/*
        if (dataSetInstance.hasErrors()) {
            respond dataSetInstance.errors, view:'add'
            return
        }*/

        //check if the dataset name is valid
        if (!datasetService.isValidDatasetName(dataSetInstance.name)){
            def columnsMap = datasetService.getColumns(dataSetInstance.filename)
            flash.message = message(code: "dataset.name.invalid.error.label")
            render view: "add", model: [dataSetInstance: dataSetInstance, dataSetColumns:columnsMap.keySet(), tableList:  tableList]
            return
        }

        def modules = Module.getAll(params.list("allmodules.id"))
        modules.each {
            dataSetInstance.addToModules(it.code)
        }

        if (dataSetInstance.save(flush:true)){
            datasetService.createZipFile(dataSetInstance)
        }

        println "errors ${dataSetInstance.errors}"

        if (dataSetInstance.hasErrors()) {
            def columnsMap = datasetService.getColumns(dataSetInstance.filename)
            render view: "add", model: [dataSetInstance: dataSetInstance, dataSetColumns:columnsMap.keySet(), tableList:  tableList]
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'dataSet.label', default: 'Dataset'), dataSetInstance.name])
                redirect dataSetInstance
            }
            '*' { respond dataSetInstance, [status: CREATED] }
        }
    }

    def edit(Dataset datasetInstance) {
        def modules = moduleService.findAllByCodes(datasetInstance.modules)

        def columnsMap = datasetService.getColumns(datasetInstance.filename)

        render view: "edit", model: [datasetInstance: datasetInstance, modules: modules, dataSetColumns:columnsMap.keySet(), tableList:  tableList, dataSetInstanceList: Dataset.list(params)]
    }

    def update(Dataset dataSetInstance) {
        if (dataSetInstance == null) {
            notFound()
            return
        }

        if (dataSetInstance.hasErrors()) {
            respond dataSetInstance.errors, view:'edit'
            return
        }

        try {

            def modules = Module.getAll(params.list("allmodules.id"))
            datasetService.updateModules(dataSetInstance, modules)

            if (dataSetInstance.save(flush:true)){
                //updated the dataset zip file
                datasetService.createZipFile(dataSetInstance)
            }

        } catch (ValidationException e) {
            respond dataSetInstance.errors, view:'edit', model: [modules: moduleService.findAllByCodes(dataSetInstance.modules)]
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Dataset.label', default: 'Dataset'), dataSetInstance.name])
                redirect dataSetInstance
            }
            '*'{ respond dataSetInstance, [status: OK] }
        }
    }

    def delete(Dataset dataSetInstance) {

        if (dataSetInstance == null) {
            notFound()
            return
        }

        dataSetInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Dataset.label', default: 'Dataset'), dataSetInstance.name])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'dataSet.label', default: 'Dataset'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
