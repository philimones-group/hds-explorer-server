package org.philimone.hds.explorer.server.model.authentication

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.audit.AuditableEntity
import org.philimone.hds.explorer.server.model.types.StringCollectionType

/**
 * This domain represents a Application User that can be an Administrator, Data Manager or a Field Worker, they can have access do the server or client app
 */
@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User extends AuditableEntity {

    private static final long serialVersionUID = 1

    String id
    String code
    String firstName
    String lastName
    String username
    String password
    String email

    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    boolean isPasswordEncoded

    static hasMany = [modules: String] /* Modules that the user has access */

    String toString(){
        return StringUtil.getFullname(firstName, "", lastName)
    }

    String getFullname(){
        return StringUtil.getFullname(firstName, "", lastName)
    }

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    String getAuthoritiesText(){
        def text = ""
        getAuthorities().eachWithIndex { it, index ->
            if (index==0){
                text = it.name
            }else{
                text += ", ${it.name}"
            }
        }
    }

    static transients = ['isPasswordEncoded', 'getFullname']

    static constraints = {
        id maxSize: 32
        code unique: true
        firstName blank: false, nullable: false
        lastName blank: false, nullable: false
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true
        email blank: true, nullable: true, unique: true

        passwordExpired()
        accountExpired()
        accountLocked()
        enabled()

        modules nullable: true
    }

    static mapping = {
        table '_user'

        id column: "id", generator: 'uuid'

        code column: 'code'
        firstName column: 'firstname'
        lastName column: 'lastname'
        username column: 'username'
        password column: '`password`'
        email column: 'email'

        enabled column: 'enabled'
        accountExpired column: 'account_expired'
        accountLocked column: 'account_locked'
        passwordExpired column: 'password_expired'

        modules column: "modules", type: StringCollectionType
    }

    def static ALL_COLUMNS = ['code','firstName', 'lastName', 'username', 'password', 'email']
}
