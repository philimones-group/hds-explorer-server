package org.philimone.hds.explorer.openhds.model

class Individual {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	Date dob
	String dobAspect
	String extId
	String firstName
	String gender
	String lastName
	String middleName

    Individual mother
    User userByVoidByUuid
    Fieldworker fieldworker
    Individual father
    User userByInsertByUuid

	static hasMany = [deaths: Death,
	                  individualsForFatherUuid: Individual,
	                  individualsForMotherUuid: Individual,
	                  inmigrations: Inmigration,
	                  memberships: Membership,
	                  outcomes: Outcome,
	                  outmigrations: Outmigration,
	                  pregnancyobservations: Pregnancyobservation,
	                  pregnancyoutcomesForFatherUuid: Pregnancyoutcome,
	                  pregnancyoutcomesForMotherUuid: Pregnancyoutcome,
	                  relationshipsForIndividualAUuid: Relationship,
	                  relationshipsForIndividualBUuid: Relationship,
	                  residencies: Residency,
	                  socialgroups: Socialgroup]
	static belongsTo = [Fieldworker, User]

	// TODO you have multiple hasMany references for class(es) [Pregnancyoutcome, Relationship, Individual] 
	//      so you'll need to disambiguate them with the 'mappedBy' property:
	static mappedBy = [individualsForFatherUuid: "father",
	                   individualsForMotherUuid: "mother",
	                   pregnancyoutcomesForFatherUuid: "father",
	                   pregnancyoutcomesForMotherUuid: "mother",
	                   relationshipsForIndividualAUuid: "individualA",
	                   relationshipsForIndividualBUuid: "individualB"]

	static mapping = {
		datasource 'openhds'
		table 'individual'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		dob column:'dob'
		dobAspect column:'dobAspect'
		extId column:'extId'
		firstName column:'firstName'
		gender column:'gender'
		lastName column:'lastName'
		middleName column:'middleName'

        fieldworker column: 'collectedBy_uuid'
        mother column: 'mother_uuid'
        father column: 'father_uuid'
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
		dob nullable: true
		dobAspect nullable: true
		extId nullable: true
		firstName nullable: true
		gender nullable: true
		lastName nullable: true
		middleName nullable: true
	}
}
