package org.philimone.hds.explorer.server.model.authentication

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic
import org.philimone.hds.explorer.services.GeneralUtilitiesService

/**
 * Role represents a user role within the system with specific system security acces
 */
@GrailsCompileStatic
@EqualsAndHashCode(includes='authority')
@ToString(includes='authority', includeNames=true, includePackage=false)
class Role implements Serializable {

	private static final long serialVersionUID = 1

	transient GeneralUtilitiesService generalUtilitiesService

	static String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR"
	static String ROLE_DATA_MANAGER = "ROLE_DATA_MANAGER"
	static String ROLE_FIELD_WORKER = "ROLE_FIELD_WORKER"

	static String LABEL_ADMINISTRATOR = "role.administrator.label"
	static String LABEL_DATA_MANAGER = "role.datamanager.label"
	static String LABEL_FIELD_WORKER = "role.fieldworker.label"

	String name
	String authority

	String toString(){
		generalUtilitiesService.getMessage(name, "")
	}

	static constraints = {
		name nullable: false, blank: false
		authority nullable: false, blank: false, unique: true
	}

	static mapping = {
		cache true
		autowire true
	}
}
