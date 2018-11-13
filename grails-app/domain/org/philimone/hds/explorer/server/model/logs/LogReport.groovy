package org.philimone.hds.explorer.server.model.logs

class LogReport {

    int reportId
    LogGroup group
    LogStatus status
    String description
    Date start
    Date end

    static hasMany = [logFiles:LogReportFile]

    static constraints = {
        reportId unique: true
        group nullable: true
        description blank: true
        status nullable: false
        start nullable: true
        end nullable: true
    }

}
