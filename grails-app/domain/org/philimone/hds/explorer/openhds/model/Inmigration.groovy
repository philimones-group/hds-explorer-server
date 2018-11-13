package org.philimone.hds.explorer.openhds.model

class Inmigration {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	String migType
	String origin
	String reason
	Date recordedDate
	Boolean unknownIndividual
	Individual individual
	Residency residency
	User userByVoidByUuid
	Fieldworker fieldworker
	Visit visit
	User userByInsertByUuid

	static belongsTo = [Fieldworker, Individual, Residency, User, Visit]

	static mapping = {
		datasource 'openhds'
		table 'inmigration'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		migType column:'migType'
		origin column:'origin'
		reason column:'reason'
		recordedDate column:'recordedDate'
		unknownIndividual column:'unknownIndividual'

        fieldworker column: 'collectedBy_uuid'
        individual column: 'individual_uuid'
        visit column: 'visit_uuid'
        residency column: 'residency_uuid'
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
		migType nullable: true
		origin nullable: true
		reason nullable: true
		unknownIndividual nullable: true
	}
}
