package org.philimone.hds.explorer.openhds.model

class Outcome {
	//static mapWith = "none"

	String uuid
	String childextId
	String type
	Individual individual
	Membership membership

	static hasMany = [pregnancyoutcomeOutcomes: PregnancyoutcomeOutcome]
	static belongsTo = [Individual, Membership]

	static mapping = {
		datasource 'openhds'
		table 'outcome'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		childextId column:'childextId'
		type column:'type'
		individual column: "child_uuid"
		membership column: "childMembership_uuid"
	}

	static constraints = {
		uuid maxSize: 32
		childextId nullable: true
		type nullable: true
	}
}
