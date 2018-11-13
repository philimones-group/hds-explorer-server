package org.philimone.hds.explorer.openhds.model

class Outmigration {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	String destination
	String reason
	Date recordedDate
	Individual individual
	Residency residency
	User userByVoidByUuid
	Fieldworker fieldworker
	Visit visit
	User userByInsertByUuid

	static belongsTo = [Fieldworker, Individual, Residency, User, Visit]

	static mapping = {
		datasource 'openhds'
		table 'outmigration'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		destination column:'destination'
		reason column:'reason'
		recordedDate column:'recordedDate'

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
		destination nullable: true
		reason nullable: true
	}
}
