package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.AuditableEntity

/**
 * The REDCap API table stores credentials to access a REDCap Project Form (API Token) and relates it to an Mobile Form (eg. ODK) by whom the data will be collected and sent to REDCap
 */
class RedcapApi extends AuditableEntity {

    String id
    String name
    String url
    String token

    static hasMany = [
            forms:Form
    ]

    String toString(){
        "${name} -> ${url}/${token}"
    }

    String toText(){
        "${name};${url};${token}"
    }

    static constraints = {
        id maxSize: 32
        name unique: true
        url blank: false
        token blank: false, unique: true
    }

    static mapping = {
        table 'redcap_api'

        id column: "uuid", generator: 'uuid'

        name column: "name"
        url column: "url"
        token column: "token"
    }

}
