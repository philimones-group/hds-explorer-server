package org.philimone.hds.explorer.controllers

import org.philimone.hds.explorer.server.model.enums.Codes
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.enums.LogStatus

class DssSynchronizationController {

    def dssSynchronizationService
    def dssSynchronizationStatusService

    def index = {
        def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.group.groupId=? order by lr.reportId", [Codes.GROUP_SYNC_DSS_DATA_FROM_CLIENT])

        def syncProcesses = dssSynchronizationStatusService.mainProcessedStatus()

        render view: "index", model: [logReports : logReports, syncProcesses:syncProcesses]
    }

    def execute = {
        LogReport logReport = null

        LogReport.withTransaction {
            logReport = LogReport.get(params.id)
            logReport.keyTimestamp = System.currentTimeMillis() //CREATE timestamp code
            logReport.status = LogStatus.STARTED
            logReport.start = new Date();
            logReport.save(flush: true)
        }

        def id = logReport.reportId

        if (logReport.reportId==Codes.REPORT_DSS_ODK_CENSUS_SYNC){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from odk census forms to OpenHDS Webservices"
                    dssSynchronizationService.readCensusDataAndExecute(id)
                }
            }).start();
        }

        redirect (action: "index")
    }
}
