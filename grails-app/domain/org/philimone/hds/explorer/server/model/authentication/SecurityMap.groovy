package org.philimone.hds.explorer.server.model.authentication

import org.springframework.http.HttpMethod

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

/**
 * A Security Map defines which pages users can have access to: uses URL to define pages/controllers methods that you want to give access to
 */
@GrailsCompileStatic
@EqualsAndHashCode(includes=['configAttribute', 'httpMethod', 'url'])
@ToString(includes=['configAttribute', 'httpMethod', 'url'], cache=true, includeNames=true, includePackage=false)
class SecurityMap implements Serializable {

	private static final long serialVersionUID = 1

	String configAttribute
	HttpMethod httpMethod
	String url

	static constraints = {
		configAttribute nullable: false, blank: false
		httpMethod nullable: true
		url nullable: false, blank: false, unique: 'httpMethod'
	}

	static mapping = {
		datasource 'main'
		cache true
	}
}
