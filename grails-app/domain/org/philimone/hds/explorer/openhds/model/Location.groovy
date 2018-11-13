package org.philimone.hds.explorer.openhds.model

class Location {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	String accuracy
	String altitude
	String extId
	String latitude
	String locationName
	String locationType
	String longitude
	User userByVoidByUuid
	Fieldworker fieldworker
	Locationhierarchy locationhierarchy
	User userByInsertByUuid

	static hasMany = [residencies: Residency,
	                  visits: Visit]
	static belongsTo = [Fieldworker, Locationhierarchy, User]

	static mapping = {
		datasource 'openhds'
		table 'location'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		accuracy column:'accuracy'
		altitude column:'altitude'
		extId column:'extId'
		latitude column:'latitude'
		locationName column:'locationName'
		locationType column:'locationType'
		longitude column:'longitude'

        fieldworker column: 'collectedBy_uuid'
        locationhierarchy column: 'locationLevel_uuid'
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
		accuracy nullable: true
		altitude nullable: true
		latitude nullable: true
		locationName nullable: true
		locationType nullable: true
		longitude nullable: true
	}
}
