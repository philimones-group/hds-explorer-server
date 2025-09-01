package org.philimone.hds.explorer.controllers


import net.betainteractive.utilities.StringUtil
import grails.converters.JSON
import org.philimone.hds.explorer.server.model.collect.raw.RawErrorLog
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.enums.RawEventType
import org.philimone.hds.explorer.server.model.enums.settings.LogGroupCode
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDateTime

class EventSyncController {

    def eventSyncService
    def dataModelsService
    def eventSyncRawDomainService
    def generalUtilitiesService

    def index = {
        //def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.group.groupId=?0 order by lr.reportId", [LogGroupCode.GROUP_SYNC_DSS_DATA_FROM_CLIENT])
        def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.reportId in (:list)", [list:[LogReportCode.REPORT_DSS_EVENTS_SYNC, LogReportCode.REPORT_SYNC_MANAGER_EXECUTE_EVENTS, LogReportCode.REPORT_SYNC_MANAGER_EXECUTE_RESTORE_TEMP_DISABLED]]) //Get only execute all events

        def syncProcesses = eventSyncService.mainProcessedStatus()

        render view: "index", model: [logReports : logReports, syncProcesses:syncProcesses, advanced: false]
    }

    def advancedindex = {
        def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.group.groupId=?0 order by lr.reportId", [LogGroupCode.GROUP_SYNC_MANAGER])

        def syncProcesses = eventSyncService.mainProcessedStatus()

        render view: "index", model: [logReports : logReports, syncProcesses:syncProcesses, advanced: true]
    }

    def execute = {
        LogReport logReport = null

        LogReport.withTransaction {
            logReport = LogReport.get(params.id)
            logReport.keyTimestamp = System.currentTimeMillis() //CREATE timestamp code
            logReport.status = LogStatus.STARTED
            logReport.start = LocalDateTime.now()
            logReport.end = null
            logReport.save(flush: true)
        }

        def id = logReport.reportId
        def advanced = params.advanced
        def executionLimit = 0
        println "exec limit ${params.executionLimit}"
        if (params.executionLimit) {
            def str = params.executionLimit as String
            if (StringUtil.isInteger(str)){
                executionLimit = Integer.parseInt(str)
            }
        }
        println "set executionLimit=${executionLimit}"

        if (logReport.reportId==LogReportCode.REPORT_DSS_EVENTS_SYNC || logReport.reportId==LogReportCode.REPORT_SYNC_MANAGER_EXECUTE_ALL_EVENTS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from raw data to HDS - compile and execute"
                    eventSyncService.executeAll(id, executionLimit)
                }
            }).start();
        }

        if (logReport.reportId==LogReportCode.REPORT_SYNC_MANAGER_EXECUTE_COMPILE_EVENTS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from raw data to HDS - execute compilation of events"
                    eventSyncService.executeCompileEvents(id) //- we will not execute this
                }
            }).start();
        }

        if (logReport.reportId==LogReportCode.REPORT_DSS_EVENTS_EXECUTE_SYNC || logReport.reportId==LogReportCode.REPORT_SYNC_MANAGER_EXECUTE_EVENTS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from raw data to HDS - execute events"
                    eventSyncService.executeEvents(id, executionLimit)
                }
            }).start();
        }

        if (logReport.reportId==LogReportCode.REPORT_DSS_EVENTS_RESET_ERRORS || logReport.reportId==LogReportCode.REPORT_SYNC_MANAGER_EXECUTE_RESET_ERRORS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing reset errors to not processed - errors events"
                    eventSyncService.executeResetErrors(id)
                }
            }).start();
        }

        if (logReport.reportId==LogReportCode.REPORT_SYNC_MANAGER_EXECUTE_RESTORE_TEMP_DISABLED){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing restore temporarily disabled records"
                    eventSyncService.restoreTemporarilyDisabledResidenciesHeadRelationships(id)
                }
            }).start();
        }

        if (advanced == true) {
            redirect action: "advancedindex"
            return
        }

        redirect (action: "index")
    }

    def showSyncReport(){
        def logReportInstance = LogReport.get(params.id)

        def logFiles = LogReportFile.executeQuery("select f from LogReportFile f where f.logReport=?0 order by f.keyTimestamp desc, f.start desc", [logReportInstance])

        render view:"showSyncReport", model : [logReportInstance: logReportInstance, logFiles: logFiles]
    }

    def showSyncReportDetails() {
        def logReportFile = LogReportFile.get(params.id)

        //get error logs
        //def errorLogs = RawErrorLog.findAllByLogReportFile(logReportFile, [sort: "createdDate", order: "asc"])

        //println "${logReportFile}, ${errorLogs.size()}"

        render view:"showSyncReportDetails", model: [logReportFileInstance: logReportFile]
    }

    def showLastReportDetails = {
        LogReport logReport = LogReport.get(params.id)

        if (logReport == null) {
            flash.message = "LogReport not available"
            redirect (action: "index")
            return
        }

        def result = LogReportFile.executeQuery("select f from LogReportFile f where f.logReport=?0 order by f.keyTimestamp desc, f.start desc", [logReport], [offset: 0, max: 1])

        if (result == null || result.empty) {
            flash.message = "Report Details for the LogReport[${message(code: logReport.description)}] are not available"
            redirect (action: "index")
            return
        }

        render view:"showSyncReportDetails", model: [logReportFileInstance: result.first()]
    }

    def showRawDataDetails = {
        def selectedRawEvent = RawEventType.getFrom(Integer.parseInt(params.id))

        [selectedRawEvent: selectedRawEvent]
    }

    def editRawDomain = {
        //def errorLog = RawErrorLog.findByUuid(params.id)
        redirect controller:"rawDomain", action: "editDomain", model: [id:params.id]
    }

    def downloadLogFile = {

        def logReportFile = LogReportFile.get(params.id)

        File file = new File(logReportFile.fileName)

        if (file.exists()) {
            response.setContentType("text/plain") // or or image/JPEG or text/xml or whatever type the file is
            response.setHeader("Content-disposition", "attachment;filename=\"${file.name}\"")
            response.outputStream << file.bytes
        } else {
            render "${message(code: "default.file.not.found")} - ${logReportFile.fileName}"
        }

    }

    def errorLogList = {
        def dateUtil = generalUtilitiesService.getDateUtil()

        //convert datatables params to gorm params
        def jqdtParams = [:]
        params.each { key, value ->
            def keyFields = key.replace(']','').split(/\[/)
            def table = jqdtParams
            for (int f = 0; f < keyFields.size() - 1; f++) {
                def keyField = keyFields[f]
                if (!table.containsKey(keyField))
                    table[keyField] = [:]
                table = table[keyField]
            }
            table[keyFields[-1]] = value
        }

        def params_search = jqdtParams.search?.value
        def columnsList = jqdtParams.columns.collect { k, v -> v.data }
        def orderList = jqdtParams.order.collect { k, v -> [columnsList[v.column as Integer], v.dir] } //v.dir is asc or desc
        def logReportFileInstance = LogReportFile.get(params.id)

        //event, uuid, column, code, creationDate, errorMessage

        //FILTERS - if not null will filter
        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def date_search_filter = StringUtil.isBlank(search_filter) ? null : search_filter.replace(":", "%")
        def formattedEthDate = Codes.SYSTEM_USE_ETHIOPIAN_CALENDAR ? eventSyncService.toGregorianSearchDate(params_search) : null //gets the possible date range
        def entitiesList = dataModelsService.findRawEntitiesLike("${params_search}")

        if (formattedEthDate != null) {
            date_search_filter = formattedEthDate.replace(":", "%") + "%"
        }

        def filterer = {
            eq ('logReportFile', logReportFileInstance)
            or {
                if (search_filter) 'in'('entity', entitiesList)
                if (search_filter) ilike 'uuid', search_filter
                if (search_filter) ilike 'code', search_filter
                if (search_filter) ilike 'columnName', search_filter
                if (date_search_filter) sqlRestriction("DATE_FORMAT(collected_date, '%Y-%m-%d %H:%i:%s') like '${date_search_filter}'")
                if (search_filter) ilike 'message', search_filter
            }
            //def errorLogs = RawErrorLog.findAllByLogReportFile(logReportFile, [sort: "createdDate", order: "asc"])
        }

        //ORDERS
        def orderer = RawErrorLog.withCriteria {
            filterer.delegate = delegate
            filterer()
            if (orderList.empty) order 'id', 'asc'
            orderList.each { oi ->
                switch (oi[0]) {
                    case 'id':    order 'id', oi[1]; break
                    case 'event': order 'entity', oi[1]; break
                    case 'code':  order 'code', oi[1]; break
                    case 'collectedDate': order 'collectedDate', oi[1]; break
                    case 'createdDate':  order 'createdDate', oi[1]; break
                }
            }
            maxResults (jqdtParams.length as Integer)
            firstResult (jqdtParams.start as Integer)
        }


        //Display records
        def errorLogs = orderer.collect { errorLog ->

            ['event':        "<a href='${createLink(controller: 'rawDomain', action: 'editDomain', id: errorLog.uuid)}'>${message(code: errorLog.entity.name)}</a>",
             'uuid':         errorLog.uuid,
             'column':       errorLog.columnName,
             'code':         errorLog.code,
             'collectedDate': dateUtil.formatYMDHMS(errorLog.collectedDate), // StringUtil.format(errorLog.collectedDate),
             'createdDate': dateUtil.formatYMDHMS(errorLog.createdDate), //StringUtil.format(errorLog.createdDate),
             'errorMessage': "<td style=\"word-wrap: break-word;\">${errorLog.collapsedMessage}</td>"
            ]
        }


        //FINAL PARAMETERS
        def recordsTotal = RawErrorLog.count()
        def errorLogCriteria = RawErrorLog.createCriteria()
        def recordsFiltered = errorLogCriteria.count {
            filterer.delegate = delegate
            filterer()
        }


        //println "errorLog recordsTotal $recordsTotal"
        //println "errorLog recordsFiltered $recordsFiltered"
        //println "errorLogs ${errorLogs.size()}"


        def result = [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: errorLogs]

        render result as JSON
    }

    def rawDomainsList = {

        def selectedRawEvent = RawEventType.getFrom(Integer.parseInt(params.id))

        def domainDataResult = eventSyncRawDomainService.getRawDomainData(selectedRawEvent, params)

        render domainDataResult as JSON
    }
}
