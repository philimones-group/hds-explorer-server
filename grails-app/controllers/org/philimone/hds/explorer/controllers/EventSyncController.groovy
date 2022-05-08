package org.philimone.hds.explorer.controllers

import org.philimone.hds.explorer.server.model.collect.raw.RawErrorLog
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.enums.settings.LogGroupCode
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile

import java.time.LocalDateTime

class EventSyncController {

    def eventSyncService

    def index = {
        def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.group.groupId=? order by lr.reportId", [LogGroupCode.GROUP_SYNC_DSS_DATA_FROM_CLIENT])

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

        if (logReport.reportId== LogReportCode.REPORT_DSS_EVENTS_COMPILE_SYNC){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from raw data to HDS - compile events"
                    eventSyncService.executeCompileEvents(id)
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

        redirect (action: "index")
    }

    def showSyncReport(){
        def logReportInstance = LogReport.get(params.id)

        def logFiles = LogReportFile.executeQuery("select f from LogReportFile f where f.logReport=? order by f.keyTimestamp desc, f.start desc", [logReportInstance])

        render view:"showSyncReport", model : [logReportInstance: logReportInstance, logFiles: logFiles]
    }

    def showSyncReportDetails() {
        def logReportFile = LogReportFile.get(params.id)

        //get error logs



        def errorLogs = RawErrorLog.findAllByLogReportFile(logReportFile, [sort: "createdDate", order: "asc"])

        println "${logReportFile}, ${errorLogs.size()}"

        render view:"showSyncReportDetails", model: [logReportFileInstance: logReportFile, errorLogsCount: errorLogs.size(), errorLogs: errorLogs]
    }

    def editRawDomain = {
        def errorLog = RawErrorLog.get(params.id)
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
}
