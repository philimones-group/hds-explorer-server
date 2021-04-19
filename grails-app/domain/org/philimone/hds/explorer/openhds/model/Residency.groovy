package org.philimone.hds.explorer.openhds.model

class Residency {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	Date endDate
	String endType
	Date startDate
	String startType
	Individual individual
	User voidBy
	Fieldworker fieldworker
	Location location
	User insertBy

	static hasMany = [inmigrations: Inmigration,
	                  outmigrations: Outmigration]
	static belongsTo = [Fieldworker, Individual, Location, User]

	static mapping = {
		datasource 'openhds'
		table 'residency'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		endDate column:'endDate'
		endType column:'endType'
		startDate column:'startDate'
		startType column:'startType'

        fieldworker column: 'collectedBy_uuid'
        individual column: 'individual_uuid'
        location column: 'location_uuid'
        voidBy column: 'voidBy_uuid'
        insertBy column: 'insertBy_uuid'
	}

	static constraints = {
		uuid maxSize: 32
		insertDate nullable: true
		voidDate nullable: true
		voidReason nullable: true
		status nullable: true
		statusMessage nullable: true
		endDate nullable: true
		endType nullable: true
		startType nullable: true
	}
}
