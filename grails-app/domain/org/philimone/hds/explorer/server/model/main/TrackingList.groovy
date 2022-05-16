package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.types.StringCollectionType

class TrackingList extends AuditableEntity {

    String id
    String code
    String name
    String filename
    Boolean hasExtraData = false
    Boolean enabled = true

    static hasMany = [mappings:TrackingListMapping,
                      modules:String]

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

    static constraints = {
        id maxSize: 32
        code unique: true
        name blank: false
        filename unique: true

        hasExtraData nullable: false
        enabled nullable: false

        modules nullable: true
    }

    static mapping = {
        table 'tracking_list'

        id column: "id", generator: 'uuid'

        code column: 'code'
        name column: 'name'
        filename column: 'filename'
        hasExtraData column: 'has_extra_data'
        enabled column: 'enabled'
        modules column: 'modules', type: StringCollectionType, index: "idx_modules"
    }
}
