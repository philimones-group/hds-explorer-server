package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity

/**
 * Represents an External DataSet that can be linked with our core domain tables (Household, Member, User)
 * to provide dynamic datasets to the tablet application
 */
class DataSet extends AuditableEntity {
    String name
    String keyColumn
    String tableName
    String tableColumn
    String filename

    boolean enabled = false

    static hasMany = [mappingLabels:DataSetLabel]

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
        name nullable: true, unique: true
        keyColumn nullable: false, blank: false
        tableName nullable: false, blank: false
        tableColumn nullable: false, blank: false

        enabled nullable:false
    }

    static mapping = {
        table 'ext_dataset'

        name column: 'name'
        keyColumn column: 'key_column'
        tableName column: 'table_name'
        tableColumn column: 'table_column'
        filename column: 'filename'

        enabled column: 'enabled'
    }
}
