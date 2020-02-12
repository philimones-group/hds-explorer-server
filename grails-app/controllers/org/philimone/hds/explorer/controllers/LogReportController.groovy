package org.philimone.hds.explorer.controllers

import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile

class LogReportController {

    def show(LogReport logReportInstance) {
        redirect action: "showLogReport", id: logReportInstance.id
    }

    def showLogReport(){
        def logReportInstance = LogReport.get(params.id)

        def logFiles = LogReportFile.executeQuery("select f from LogReportFile f where f.logReport=? order by f.keyTimestamp desc, f.start desc", [logReportInstance])

        render view:"showLogReport", model : [logReportInstance : logReportInstance, logFiles: logFiles]
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
