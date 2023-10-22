package org.philimone.hds.explorer.controllers

import net.betainteractive.utilities.StringUtil
import grails.converters.JSON
import org.philimone.hds.explorer.server.model.collect.raw.RawErrorLog
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.enums.settings.LogGroupCode
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.main.Household

import java.time.LocalDateTime

class EventSyncController {

    def eventSyncService

    def index = {
        def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.group.groupId=?0 order by lr.reportId", [LogGroupCode.GROUP_SYNC_DSS_DATA_FROM_CLIENT])

        def syncProcesses = eventSyncService.mainProcessedStatus()

        render view: "index", model: [logReports : logReports, syncProcesses:syncProcesses]
    }

    def execute = {
        LogReport logReport = null

        LogReport.withTransaction {
            logReport = LogReport.get(params.id)
            logReport.keyTimestamp = System.currentTimeMillis() //CREATE timestamp code
            logReport.status = LogStatus.STARTED
            logReport.start = LocalDateTime.now()
            logReport.save(flush: true)
        }

        def id = logReport.reportId

        if (logReport.reportId== LogReportCode.REPORT_DSS_EVENTS_SYNC){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from raw data to HDS - compile and execute"
                    eventSyncService.executeAll(id)
                }
            }).start();
        }

        if (logReport.reportId== LogReportCode.REPORT_DSS_EVENTS_EXECUTE_SYNC){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from raw data to HDS - execute events"
                    eventSyncService.executeEvents(id)
                }
            }).start();
        }

        if (logReport.reportId== LogReportCode.REPORT_DSS_EVENTS_RESET_ERRORS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing reset errors to not processed - errors events"
                    eventSyncService.executeResetErrors(id)
                }
            }).start();
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

        render view:"showSyncReportDetails", model: [logReportFileInstance: logReportFile /*, errorLogsCount: errorLogs.size(), errorLogs: errorLogs*/]
    }

    def editRawDomain = {
        //def errorLog = RawErrorLog.get(params.id)
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
        def orderList = jqdtParams.order.collect { k, v -> [columnsList[v.column as Integer], v.dir] }
        def logReportFileInstance = LogReportFile.get(params.id)

        //event, uuid, column, code, creationDate, errorMessage

        println(params)
        //println()
        println "errorLog file $logReportFileInstance"
        //println()
        //println "errorLog orderList $orderList"


        //FILTERS - if not null will filter
        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def filterer = {
            eq ('logReportFile', logReportFileInstance)
            or {
                //if (search_filter) ilike 'entity.name', search_filter
                if (search_filter) ilike 'uuid', search_filter
                if (search_filter) ilike 'columnName', search_filter
                if (search_filter) ilike 'message', search_filter
            }
            //def errorLogs = RawErrorLog.findAllByLogReportFile(logReportFile, [sort: "createdDate", order: "asc"])
        }

        //ORDERS
        def orderer = RawErrorLog.withCriteria {
            filterer.delegate = delegate
            filterer()
            orderList.each { oi ->
                switch (oi[0]) {
                    case 'creationDate': order 'createdDate', oi[1]; break
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
             'creationDate': StringUtil.format(errorLog.createdDate),
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


        println "errorLog recordsTotal $recordsTotal"
        println "errorLog recordsFiltered $recordsFiltered"
        println "errorLogs ${errorLogs.size()}"


        def result = [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: errorLogs]

        render result as JSON
    }
}
