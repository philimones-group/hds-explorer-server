package org.philimone.hds.explorer.openhds.model

class Locationhierarchylevel {
	//static mapWith = "none"

	String uuid
	Integer keyIdentifier
	String name

	static hasMany = [locationhierarchies: Locationhierarchy]

	static mapping = {
		datasource 'openhds'
		table 'locationhierarchylevel'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		keyIdentifier column:'keyIdentifier'
		name column:'name'
	}
}
