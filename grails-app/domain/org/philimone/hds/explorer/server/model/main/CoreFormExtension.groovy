package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.enums.CoreForm


class CoreFormExtension {

    String id
    String formName
    String formId
    String extFormId
    byte[] extFormDefinition /* ODK Form Definition (The XML XFORM Definition file)*/
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


    static constraints = {
        id maxSize: 32
        formName nullable: false, blank: false
        formId unique: true
        extFormId nullable: false, blank: false
        extFormDefinition nullable: true
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
        required column: "collection_required"
        enabled column: "enabled"

        columnsMapping column: "columns_mapping"

        coreForm column: "core_form", enumType: 'identity'

    }
}
