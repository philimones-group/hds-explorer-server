package org.philimone.hds.explorer.server.model.logs

class LogReportFile {

    Date creationDate
    String fileName
    int processedCount = 0
    int errorsCount = 0

    String getOnlyFileName(){

        if (fileName != null)
            return new File(fileName).name //fileName.substring(fileName.lastIndexOf("/")+1)

        return ""
    }

    String toString(){
        getOnlyFileName()
    }

    static belongsTo = [logReport : LogReport]

    static constraints = {
        creationDate nullable: false
        fileName blank: false
        processedCount min: 0
        errorsCount min: 0
    }
}
