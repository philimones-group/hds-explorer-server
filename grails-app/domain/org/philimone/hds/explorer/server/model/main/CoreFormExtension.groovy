package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.CoreForm


class CoreFormExtension {

    String id
    String formName
    String formId
    String extFormId
    byte[] extFormDefinition /* ODK Form Definition (The XML XFORM Definition file)*/
    String extFormPath
    boolean required = false /*Collection REQUIRED OR OPTIONAL*/
    boolean enabled = false
    String columnsMapping
    CoreForm coreForm

    transient def addMapping(Map<String, String> colsMap){
        String str = ""
        colsMap.each { k, v ->
            str += (str.empty ? "":";") + "${k}:${v}"
        }

        this.columnsMapping = str
    }

    String getFilenameOnly() {
        if (extFormPath == null) return null
        return new File(extFormPath).name
    }

    File getExtFile() {
        if (extFormPath == null) return null
        File f = new File(extFormPath)
        if (f.isAbsolute() && f.exists()) {
            return f
        }
        return new File(SystemPath.externalDocsPath, getFilenameOnly())
    }

    String getCompressedExtFormPath(){

        if (extFormPath == null) return null

        def fn = getFilenameOnly()
        int i = fn.lastIndexOf(".")
        def nfn = (i==-1 ? fn : fn.substring(0,i)) +".zip"

        return SystemPath.externalDocsPath + File.separator + nfn
    }


    static constraints = {
        id maxSize: 32
        formName nullable: false, blank: false
        formId unique: true
        extFormId nullable: false, blank: false
        extFormDefinition nullable: true
        extFormPath nullable: true
        required nullable: false
        enabled nullable: false

        columnsMapping nullable: true, maxSize: 1000
    }

    static mapping = {
        table "core_form_extension"

        id column: "id", generator: 'uuid'

        formName column: "form_name"
        formId column: "form_id"
        extFormId column: "ext_form_id"
        extFormDefinition column: "ext_form_definition", sqlType: "mediumblob"
        extFormPath column: "ext_form_path"
        required column: "collection_required"
        enabled column: "enabled"

        columnsMapping column: "columns_mapping"

        coreForm column: "core_form", enumType: 'identity'

    }
}
