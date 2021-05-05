package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.authentication.User

class UserStudyModule {

    User user
    StudyModule module

    //static belongsTo = [user:User]

    static constraints = {
        user nullable: false
        module nullable: false
    }

    static mapping = {
        table '_user_study_module'

        version false

        user column: 'user_id'
        module column: 'module_id'
    }
}
