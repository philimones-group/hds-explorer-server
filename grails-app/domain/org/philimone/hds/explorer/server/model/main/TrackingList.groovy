package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.authentication.User

class TrackingList {

    String code
    String name
    String filename
    StudyModule module

    Boolean hasExtraData = false

    Boolean enabled = true

    User createdBy
    Date creationDate
    User updatedBy
    Date updatedDate

    static hasMany = [mappings:TrackingListMapping]

    static constraints = {
        code unique: true
        name blank: false
        filename unique: true
        module nullable: false

        hasExtraData nullable: false
        enabled nullable: false

        createdBy nullable: true
        creationDate nullable: true
        updatedBy nullable: true
        updatedDate nullable: true
    }

    static mapping = {
        table 'tracking_list'

        code column: 'code'
        name column: 'name'
        filename column: 'filename'
        module column: 'module'
        hasExtraData column: 'has_extra_data'
        enabled column: 'enabled'

        createdBy column: 'created_by'
        creationDate column: 'creation_date'
        updatedBy column: 'updated_by'
        updatedDate column: 'updated_date'
    }
}
