package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity

class TrackingList extends AuditableEntity {

    String id
    String code
    String name
    String filename
    StudyModule module
    Boolean hasExtraData = false
    Boolean enabled = true

    String getFilenameOnly(){
        new File(filename).name
    }

    String getCompressedFilename(){
        def f = new File(filename)
        def fn = f.name
        int i = fn.lastIndexOf(".")
        def nfn = (i==-1 ? fn : fn.substring(0,i)) +".zip"

        return f.parent + File.separator + nfn
    }

    static hasMany = [mappings:TrackingListMapping]

    static constraints = {
        id maxSize: 32
        code unique: true
        name blank: false
        filename unique: true
        module nullable: false

        hasExtraData nullable: false
        enabled nullable: false
    }

    static mapping = {
        table 'tracking_list'

        id column: "uuid", generator: 'uuid'

        code column: 'code'
        name column: 'name'
        filename column: 'filename'
        module column: 'module'
        hasExtraData column: 'has_extra_data'
        enabled column: 'enabled'
    }
}
