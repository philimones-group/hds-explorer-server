package org.philimone.hds.explorer.server.model.main


class CoreFormExtension {

    String formName
    String formId
    String extFormId
    boolean required = false /*Collection REQUIRED OR OPTIONAL*/
    boolean enabled = false
    String columnsMapping

    transient def addMapping(Map<String, String> colsMap){
        String str = ""
        colsMap.each { k, v ->
            str += (str.empty ? "":";") + "${k}:${v}"
        }

        this.columnsMapping = str;
    }


    static constraints = {
        formName nullable: false, blank: false
        formId unique: true
        extFormId nullable: false, blank: false
        required nullable: false
        enabled nullable: false

        columnsMapping nullable: true, maxSize: 500
    }

    static mapping = {
        table "core_form_extension"

        formName column: "form_name"
        formId column: "form_id"
        extFormId column: "ext_form_id"
        required column: "collection_required"
        enabled column: "enabled"

        columnsMapping column: "columns_mapping", enumType: 'identity'

    }
}
