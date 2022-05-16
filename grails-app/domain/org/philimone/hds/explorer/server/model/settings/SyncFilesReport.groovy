package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.server.model.enums.SyncEntity

import java.time.LocalDateTime

class SyncFilesReport {

    String id
    int code
    SyncEntity name
    long records
    LocalDateTime syncDate

    def beforeInsert() {
        this.code = name.code
    }

    def beforeUpdate() {
        this.code = name.code
    }

    static constraints = {
        id maxSize: 32
        code unique: true
        name unique: true
        syncDate nullable: true
    }

    static mapping = {
        table 'sync_report'

        id column: "id", generator: 'uuid'

        code column: "code", index: "idx_code"
        name column: "name", enumType: "string"
        records column: "records", defaultValue: "'0'"
        syncDate column: "sync_date"
    }


}
