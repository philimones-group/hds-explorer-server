package org.philimone.hds.explorer.openhds.model

class Pregnancyobservation {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	Date expectedDeliveryDate
	Date recordedDate
	Individual individual
	User voidBy
	Fieldworker fieldworker
	Visit visit
	User insertBy

	static belongsTo = [Fieldworker, Individual, User, Visit]

	static mapping = {
		datasource 'openhds'
		table 'pregnancyobservation'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		expectedDeliveryDate column:'expectedDeliveryDate'
		recordedDate column:'recordedDate'

		fieldworker column: 'collectedBy_uuid'
		individual column: 'mother_uuid'
		visit column: 'visit_uuid'
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
	}
}
