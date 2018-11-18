package org.philimone.hds.explorer.server.model.logs

/**
 * A Log Report represents a register/report of the status of an specific execution that started and ended at an specific time
 * Used to control the execution and report of a specific task
 */
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
