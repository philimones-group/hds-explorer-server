package org.philimone.hds.explorer.openhds.model

class User {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	String description
	String firstName
	String fullName
	Long lastLoginTime
	String lastName
	String password
	String sessionId
	String username

	static hasMany = [deathsForInsertByUuid: Death,
	                  deathsForVoidByUuid: Death,
	                  fieldworkersForInsertByUuid: Fieldworker,
	                  fieldworkersForVoidByUuid: Fieldworker,
	                  individualsForInsertByUuid: Individual,
	                  individualsForVoidByUuid: Individual,
	                  inmigrationsForInsertByUuid: Inmigration,
	                  inmigrationsForVoidByUuid: Inmigration,
	                  locationsForInsertByUuid: Location,
	                  locationsForVoidByUuid: Location,
	                  membershipsForInsertByUuid: Membership,
	                  membershipsForVoidByUuid: Membership,
	                  outmigrationsForInsertByUuid: Outmigration,
	                  outmigrationsForVoidByUuid: Outmigration,
	                  pregnancyobservationsForInsertByUuid: Pregnancyobservation,
	                  pregnancyobservationsForVoidByUuid: Pregnancyobservation,
	                  pregnancyoutcomesForInsertByUuid: Pregnancyoutcome,
	                  pregnancyoutcomesForVoidByUuid: Pregnancyoutcome,
	                  relationshipsForInsertByUuid: Relationship,
	                  relationshipsForVoidByUuid: Relationship,
	                  residenciesForInsertByUuid: Residency,
	                  residenciesForVoidByUuid: Residency,
	                  socialgroupsForInsertByUuid: Socialgroup,
	                  socialgroupsForVoidByUuid: Socialgroup,
	                  visitsForInsertByUuid: Visit,
	                  visitsForVoidByUuid: Visit]

	// TODO you have multiple hasMany references for class(es) [Outmigration, Death, Inmigration, Residency, Membership, Pregnancyoutcome, Location, Socialgroup, Visit, Individual, Pregnancyobservation, Fieldworker, Relationship] 
	//      so you'll need to disambiguate them with the 'mappedBy' property:
	static mappedBy = [deathsForInsertByUuid: 'insertBy',
	                   deathsForVoidByUuid: 'voidBy',
	                   fieldworkersForInsertByUuid: "insertBy",
	                   fieldworkersForVoidByUuid: "voidBy",
	                   individualsForInsertByUuid: "insertBy",
	                   individualsForVoidByUuid: "voidBy",
	                   inmigrationsForInsertByUuid: "insertBy",
	                   inmigrationsForVoidByUuid: "voidBy",
	                   locationsForInsertByUuid: "insertBy",
					   locationsForVoidByUuid: "voidBy",
					   membershipsForInsertByUuid: "insertBy",
					   membershipsForVoidByUuid: "voidBy",
					   outmigrationsForInsertByUuid: "insertBy",
					   outmigrationsForVoidByUuid: "voidBy",
					   pregnancyobservationsForInsertByUuid: "insertBy",
					   pregnancyobservationsForVoidByUuid: "voidBy",
					   pregnancyoutcomesForInsertByUuid: "insertBy",
					   pregnancyoutcomesForVoidByUuid: "voidBy",
					   relationshipsForInsertByUuid: "insertBy",
					   relationshipsForVoidByUuid: "voidBy",
					   residenciesForInsertByUuid: "insertBy",
					   residenciesForVoidByUuid: "voidBy",
					   socialgroupsForInsertByUuid: "insertBy",
					   socialgroupsForVoidByUuid: "voidBy",
					   visitsForInsertByUuid: "insertBy",
					   visitsForVoidByUuid: "voidBy"]

	static mapping = {
		datasource 'openhds'
		table 'user'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		description column:'description'
		firstName column:'firstName'
		fullName column:'fullName'
		lastLoginTime column:'lastLoginTime'
		lastName column:'lastName'
		password column:'password'
		sessionId column:'sessionId'
		username column:'username'
	}

	static constraints = {
		uuid maxSize: 32
		description nullable: true
		firstName nullable: true
		fullName nullable: true
		lastName nullable: true
		password nullable: true
		sessionId nullable: true
		username nullable: true
	}
}
