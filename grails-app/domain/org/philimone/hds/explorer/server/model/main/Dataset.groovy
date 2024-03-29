package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.types.StringCollectionType

/**
 * Represents an External Dataset that can be linked with our core domain tables (Household, Member, User)
 * to provide dynamic datasets to the tablet application
 */
class Dataset extends AuditableEntity {

    String id
    String name
    String label
    String keyColumn
    String tableName
    String tableColumn
    String tableColumnLabels
    String filename

    boolean enabled = false

    static hasMany = [modules:String]

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
        name nullable: true, unique: true
        label nullable: true, unique: true
        keyColumn nullable: false, blank: false
        tableName nullable: false, blank: false
        tableColumn nullable: false, blank: false
        tableColumnLabels nullable: true, maxSize: 2000

        enabled nullable:false

        modules nullable: true
    }

    static mapping = {
        table 'ext_dataset'

        id column: "id", generator: 'uuid'

        name column: 'name', index: "idx_name"
        label column: 'label'
        keyColumn column: 'key_column', index: "idx_key_column"
        tableName column: 'table_name'
        tableColumn column: 'table_column'
        tableColumnLabels column: 'table_column_labels'
        filename column: 'filename'

        enabled column: 'enabled'

        modules column: "modules", type: StringCollectionType
    }
}
