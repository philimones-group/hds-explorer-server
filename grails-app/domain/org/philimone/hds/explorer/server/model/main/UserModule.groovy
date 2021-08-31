package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.authentication.User

@Deprecated
class UserModule {
    static mapWith = "none"

    User user
    Module module

    //static belongsTo = [user:User]

    static constraints = {
        user nullable: false
        module nullable: false
    }

    static mapping = {
        table '_user_module'

        version false

        user column: 'user_id'
        module column: 'module_id'
    }
}
