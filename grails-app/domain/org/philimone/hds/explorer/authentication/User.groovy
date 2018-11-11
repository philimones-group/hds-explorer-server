package org.philimone.hds.explorer.authentication

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

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

    String toString(){
        return firstName + " " + lastName
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

    static transients = ['isPasswordEncoded']

    static constraints = {
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true
    }

    static mapping = {
	    password column: '`password`'
    }
}
