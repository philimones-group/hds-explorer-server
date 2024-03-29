package org.philimone.hds.explorer.server.model.logs

import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode

import java.time.LocalDateTime

/**
 * A Log Report represents a register/report of the status of an specific execution that started and ended at an specific time
 * Used to control the execution and report of a specific task
 */
class LogReport {

    String id
    LogReportCode reportId
    LogGroup group
    LogStatus status
    String description
    long keyTimestamp /* temporary - every time its executed it creates a code with the timestamp */
    LocalDateTime start
    LocalDateTime end

    boolean enabled = true

    static hasMany = [logFiles:LogReportFile]

    static constraints = {
        id maxSize: 32
        reportId unique: true
        group nullable: true
        description blank: true
        status nullable: false
        keyTimestamp nullable:false
        start nullable: true
        end nullable: true

        enabled nullable:false
    }

    static mapping = {
        table '_log_report'

        id column: "id", generator: 'uuid'

        reportId column: "report_id", enumType: "identity", index: "idx_report_id"
        group column: "group_id", index: "idx_group_id"
        description column: "description"
        status column: "status_id", enumType: "string"
        keyTimestamp column: "key_timestamp", index: "idx_key_timestamp"
        start column: "start_time"
        end column: "end_time"

        enabled column: 'enabled'
    }

}
