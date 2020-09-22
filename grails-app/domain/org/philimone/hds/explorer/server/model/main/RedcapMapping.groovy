package org.philimone.hds.explorer.server.model.main

/**
 * The Form Mapping represents an association between variables of a specific Mobile Form and Variables of a REDCap Project Form
 * This mapping will be used to upload the values to a REDCap Form in the Mobile App
 */
class RedcapMapping {

    Form form
    String formColumnName     /* ODK Variable Name */
    String redcapColumnName   /* REDCap mapped variable */
    MappingFormatType redcapColumnFormat /* REDCap mapped variable format (date and time format) */

    String toString(){
        "${formColumnName}:${redcapColumnName}:${redcapColumnFormat==null ? 'None' : redcapColumnFormat}"
    }

    static constraints = {
        form nullable: false
        formColumnName blank: false, unique: 'form'
        redcapColumnName blank: false
        redcapColumnFormat blank: true, nullable: true
    }

    static mapping = {
        datasource 'main'
        table 'redcap_mapping'

        form column: "form_id"
        formColumnName column: "odk_col_name"
        redcapColumnName column: "redcap_col_name"
        redcapColumnFormat column: "redcap_col_format"
    }

}
