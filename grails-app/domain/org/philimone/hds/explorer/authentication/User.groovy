package org.philimone.hds.explorer.authentication

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.main.StudyModule

/**
 * This domain represents a Application User that can be an Administrator, Data Manager or a Field Worker, they can have access do the server or client app
 */
@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

    private static final long serialVersionUID = 1

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

    User createdBy
    Date creationDate
    User updatedBy
    Date updatedDate

    static hasMany = [modules:StudyModule] /* Modules that the user has access */

    String toString(){
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

    String getModulesAsText() {
        String mds = ""
        modules.each {
            mds += (mds.empty ? "":",") + it.code
        }
        return mds
    }

    static transients = ['isPasswordEncoded']

    static constraints = {

        firstName blank: false
        lastName blank: false
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true
        email blank: true, nullable: true

        createdBy nullable: true
        creationDate nullable: true
        updatedBy nullable: true
        updatedDate nullable:true

    }

    static mapping = {
	    password column: '`password`'
    }

    def static ALL_COLUMNS = ['firstName', 'lastName', 'username', 'password', 'email']
}
