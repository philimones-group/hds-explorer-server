package org.philimone.hds.explorer.server.model.settings

class SyncFilesReport {

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
        code unique: true
        name unique: true
        syncDate nullable: true
    }

    static mapping = {
        table 'sync_report'
        code column: "code"
        name column: "name", enumType: "string"
        records column: "records", defaultValue: "'0'"
    }


}
