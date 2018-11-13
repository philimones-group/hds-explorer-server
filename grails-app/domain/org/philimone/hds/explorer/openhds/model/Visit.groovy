package org.philimone.hds.explorer.openhds.model

class Visit {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	String extId
	Integer roundNumber
	Date visitDate
	User userByVoidByUuid
	Location location
	Fieldworker fieldworker
	User userByInsertByUuid

	static hasMany = [deaths: Death,
	                  inmigrations: Inmigration,
	                  outmigrations: Outmigration,
	                  pregnancyobservations: Pregnancyobservation,
	                  pregnancyoutcomes: Pregnancyoutcome]
	static belongsTo = [Fieldworker, Location, User]

	static mapping = {
		datasource 'openhds'
		table 'visit'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		extId column:'extId'
		roundNumber column:'roundNumber'
		visitDate column:'visitDate'

        fieldworker column: 'collectedBy_uuid'
        location column: 'visitLocation_uuid'
        userByVoidByUuid column: 'voidBy_uuid'
        userByInsertByUuid column: 'insertBy_uuid'
	}

	static constraints = {
		uuid maxSize: 32
		insertDate nullable: true
		voidDate nullable: true
		voidReason nullable: true
		status nullable: true
		statusMessage nullable: true
		extId nullable: true
		roundNumber nullable: true
	}
}
