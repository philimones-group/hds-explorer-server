package org.philimone.hds.explorer.openhds.model

class Fieldworker {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String extId
	String firstName
	String lastName
	String passwordHash
	User voidBy
	User insertBy

	static hasMany = [deaths: Death,
	                  individuals: Individual,
	                  inmigrations: Inmigration,
	                  locations: Location,
	                  memberships: Membership,
	                  outmigrations: Outmigration,
	                  pregnancyobservations: Pregnancyobservation,
	                  pregnancyoutcomes: Pregnancyoutcome,
	                  relationships: Relationship,
	                  residencies: Residency,
	                  socialgroups: Socialgroup,
	                  visits: Visit]
	static belongsTo = [User]

	static mapping = {
		datasource 'openhds'
		table 'fieldworker'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		extId column:'extId'
		firstName column:'firstName'
		lastName column:'lastName'
		passwordHash column:'passwordHash'

        voidBy column: 'voidBy_uuid'
        insertBy column: 'insertBy_uuid'
	}

	static constraints = {
		uuid maxSize: 32
		insertDate nullable: true
		voidDate nullable: true
		voidReason nullable: true
		firstName nullable: true
		lastName nullable: true
	}
}
