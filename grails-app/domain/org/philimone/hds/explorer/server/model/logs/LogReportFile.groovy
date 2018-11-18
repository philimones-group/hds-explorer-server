package org.philimone.hds.explorer.server.model.logs

/**
 * A Log Report File represents the link to a file that contains details/logs of the execution of an specific task, generally the file can be downloaded from the application
 */
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
