package org.philimone.hds.explorer.controllers

import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile

class LogReportController {

    def show(LogReport logReportInstance) {
        redirect action: "showLogReport", id: logReportInstance.id
    }

    def showLogReport(){
        def logReportInstance = LogReport.get(params.id)

        def logFiles = LogReportFile.executeQuery("select f from LogReportFile f where f.logReport=? order by f.creationDate desc", [logReportInstance])

        render view:"showLogReport", model : [logReportInstance : logReportInstance, logFiles: logFiles]
    }
}
