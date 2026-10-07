package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.types.StringCollectionType

class TrackingList extends AuditableEntity {

    String id
    String code
    String name
    String filename
    Boolean hasExtraData = false
    Boolean enabled = true

    static hasMany = [groups:TrackingListGroup,
                      modules:String]

    String getFilenameOnly(){
        if (filename == null) return null
        return new File(filename).name
    }

    File getFile(){
        if (filename == null) return null
        File f = new File(filename)
        if (f.isAbsolute() && f.exists()) {
            return f
        }
        return new File(SystemPath.externalDocsPath, getFilenameOnly())
    }

    String getCompressedFilename(){
        if (filename == null) return null
        def fn = getFilenameOnly()
        int i = fn.lastIndexOf(".")
        def nfn = (i==-1 ? fn : fn.substring(0,i)) +".zip"

        return SystemPath.externalDocsPath + File.separator + nfn
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
