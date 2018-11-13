package org.philimone.hds.explorer.openhds.model

class Death {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	Long ageAtDeath
	String deathCause
	Date deathDate
	String deathPlace
	Individual individual
	User userByVoidByUuid
	Fieldworker fieldworker
	Visit visit
	User userByInsertByUuid

	static belongsTo = [Fieldworker, Individual, User, Visit]

	static mapping = {
		datasource 'openhds'
		table 'death'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		ageAtDeath column:'ageAtDeath'
		deathCause column:'deathCause'
		deathDate column:'deathDate'
		deathPlace column:'deathPlace'

        fieldworker column: 'collectedBy_uuid'
        individual column: 'individual_uuid'
        visit column: 'visitDeath_uuid'
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
		ageAtDeath nullable: true
		deathCause nullable: true
		deathPlace nullable: true
	}
}
