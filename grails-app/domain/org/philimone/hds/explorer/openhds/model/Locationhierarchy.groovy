package org.philimone.hds.explorer.openhds.model

class Locationhierarchy {
	//static mapWith = "none"

	String uuid
	String extId
	String name
	Locationhierarchy parent
	Locationhierarchylevel level

	static hasMany = [locationhierarchies: Locationhierarchy,
	                  locations: Location]
	static belongsTo = [Locationhierarchylevel]

	static mapping = {
		datasource 'openhds'
		table 'locationhierarchy'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		extId column:'extId'
		name column:'name'

        level column: 'level_uuid'
        parent column: 'parent_uuid'
	}

	static constraints = {
		uuid maxSize: 32
	}
}
