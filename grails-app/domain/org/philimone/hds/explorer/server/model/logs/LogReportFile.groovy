package org.philimone.hds.explorer.server.model.logs

/**
 * A Log Report File represents the link to a file that contains details/logs of the execution of an specific task, generally the file can be downloaded from the application
 */
class LogReportFile {

    String id
    long keyTimestamp /* key that associate to a group of logs */
    Date start /* start time of the log */
    Date end   /* end time of the log */

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
        id maxSize: 32
        keyTimestamp nullable: false
        start nullable: true
        end nullable: true

        creationDate nullable: false
        fileName blank: false
        processedCount min: 0
        errorsCount min: 0
    }

    static mapping = {
        table '_log_report_file'

        id column: "id", generator: 'uuid'

        logReport column: "log_report_id"
        keyTimestamp column: "key_timestamp"
        start column: "start_time"
        end column: "end_time"

        creationDate column: "creation_date"
        fileName column: "filename"
        processedCount column: "processed_count"
        errorsCount column: "errors_count"
    }

}
