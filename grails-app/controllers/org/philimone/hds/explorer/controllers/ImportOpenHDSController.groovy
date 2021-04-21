package org.philimone.hds.explorer.controllers

import org.philimone.hds.explorer.server.model.enums.settings.LogGroupCode
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.enums.LogStatus

@Deprecated
class ImportOpenHDSController {

    def importDataFromOpenHDSService

    def index = {
        def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.group.groupId=? order by lr.reportId", [LogGroupCode.GROUP_IMPORT_DATA_OPENHDS])

        render view: "index", model: [logReports : logReports]
    }

    def importFrom = {
        LogReport logReport = null

        LogReport.withTransaction {
            logReport = LogReport.get(params.id)
            logReport.keyTimestamp = System.currentTimeMillis() //CREATE timestamp code
            logReport.status = LogStatus.STARTED
            logReport.start = LocalDateTime.now();
            logReport.save(flush: true)
        }

        def id = logReport.reportId

        if (logReport.reportId==LogReportCode.REPORT_IMPORT_FROM_OPENHDS_FIELDWORKERS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from openhds fieldworkers to user final table"
                    importDataFromOpenHDSService.importFieldWorkers(id)
                }
            }).start()
        }

        if (logReport.reportId==LogReportCode.REPORT_IMPORT_FROM_OPENHDS_HOUSEHOLDS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from openhds hierarchies and locations/socialgroups to household table"
                    importDataFromOpenHDSService.importRegions(id)
                    importDataFromOpenHDSService.importHouseholds(id)
                }
            }).start()
        }

        if (logReport.reportId== LogReportCode.REPORT_IMPORT_FROM_OPENHDS_INDIVIDUALS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from openhds individuals to member table"
                    importDataFromOpenHDSService.importIndividuals(id)
                }
            }).start()
        }

        if (logReport.reportId==LogReportCode.REPORT_IMPORT_FROM_OPENHDS_RESIDENCIES){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from openhds residencies to residency_control table"
                    //importDataFromOpenHDSService.importResidencies(id)
                }
            }).start()
        }

        redirect (action: "index")
    }
}
