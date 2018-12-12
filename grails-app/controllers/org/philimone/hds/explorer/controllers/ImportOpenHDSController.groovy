package org.philimone.hds.explorer.controllers

import org.philimone.hds.explorer.server.Codes
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogStatus

class ImportOpenHDSController {

    def importDataFromOpenHDSService

    def index = {
        def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.group.groupId=? order by lr.reportId", [Codes.GROUP_IMPORT_DATA_OPENHDS])

        render view: "index", model: [logReports : logReports]
    }

    def importFrom = {
        LogReport logReport = null

        LogReport.withTransaction {
            logReport = LogReport.get(params.id)
            logReport.status = LogStatus.findByName(LogStatus.STARTED)
            logReport.start = new Date();
            logReport.save(flush: true)
        }

        def id = logReport.reportId

        if (logReport.reportId==Codes.REPORT_IMPORT_FROM_OPENHDS_FIELDWORKERS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from openhds fieldworkers to user final table"
                    importDataFromOpenHDSService.importFieldWorkers(id)
                }
            }).start()
        }

        if (logReport.reportId==Codes.REPORT_IMPORT_FROM_OPENHDS_HOUSEHOLDS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from openhds locations/socialgroups to household table"
                    importDataFromOpenHDSService.importHouseholds(id)
                }
            }).start()
        }

        if (logReport.reportId==Codes.REPORT_IMPORT_FROM_OPENHDS_INDIVIDUALS){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing transfer from openhds individuals to member table"
                    importDataFromOpenHDSService.importIndividuals(id)
                }
            }).start()
        }

        if (logReport.reportId==Codes.REPORT_IMPORT_FROM_OPENHDS_RESIDENCIES){
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
