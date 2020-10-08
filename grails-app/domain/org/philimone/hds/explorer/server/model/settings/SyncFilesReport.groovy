package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.server.model.enums.SyncEntity

class SyncFilesReport {

    String id
    int code
    SyncEntity name
    long records
    Date syncDate

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

        id column: "uuid", generator: 'uuid'

        code column: "code"
        name column: "name", enumType: "string"
        records column: "records", defaultValue: "'0'"
    }


}
