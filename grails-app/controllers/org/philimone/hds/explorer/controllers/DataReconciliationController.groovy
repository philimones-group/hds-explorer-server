package org.philimone.hds.explorer.controllers

import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.enums.settings.LogGroupCode
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.logs.LogReport

import java.time.LocalDateTime

class DataReconciliationController {

    def dataReconciliationService

    def index = {

        def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.group.groupId=?0 order by lr.reportId", [LogGroupCode.GROUP_DATA_RECONCILIATION])

        render view: "index", model: [logReports : logReports]
    }

    def execute = {
        LogReport logReport = null

        LogReport.withTransaction {
            logReport = LogReport.get(params.id)
            logReport.keyTimestamp = System.currentTimeMillis() //CREATE timestamp code
            logReport.status = LogStatus.STARTED
            logReport.start = LocalDateTime.now();
            logReport.save(flush: true)
        }


        println "log ${logReport.errors}"

        def id = logReport.reportId

        if (logReport.reportId== LogReportCode.REPORT_DATA_RECONCILIATION_HOUSEHOLDS_STATUSES){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing household reconciliation"
                    dataReconciliationService.executeHouseholdStatusReconciliation(id)
                }
            }).start()
        }

        if (logReport.reportId== LogReportCode.REPORT_DATA_RECONCILIATION_MEMBERS_STATUSES){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing member reconciliation"
                    dataReconciliationService.executeMemberStatusReconciliation(id)
                }
            }).start()
        }

        redirect action:'index'
    }

    def executeAll = {

        createLogStartup(LogReportCode.REPORT_DATA_RECONCILIATION_HOUSEHOLDS_STATUSES)

        new Thread(new Runnable() {
            @Override
            void run() {
                println "executing household reconciliation"
                dataReconciliationService.executeHouseholdStatusReconciliation(LogReportCode.REPORT_DATA_RECONCILIATION_HOUSEHOLDS_STATUSES)
            }
        }).start()

        createLogStartup(LogReportCode.REPORT_DATA_RECONCILIATION_MEMBERS_STATUSES)
        new Thread(new Runnable() {
            @Override
            void run() {
                println "executing member reconciliation"
                dataReconciliationService.executeMemberStatusReconciliation(LogReportCode.REPORT_DATA_RECONCILIATION_MEMBERS_STATUSES)
            }
        }).start()

        redirect action: "index"
    }

    void createLogStartup(LogReportCode logReportCode) {
        LogReport logReport = null

        LogReport.withTransaction {
            logReport = LogReport.findByReportId(logReportCode)
            logReport.keyTimestamp = System.currentTimeMillis() //CREATE timestamp code
            logReport.status = LogStatus.STARTED
            logReport.start = LocalDateTime.now();
            logReport.save(flush: true)
        }
    }
}
