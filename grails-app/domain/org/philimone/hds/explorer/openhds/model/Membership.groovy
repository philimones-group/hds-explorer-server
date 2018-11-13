package org.philimone.hds.explorer.openhds.model

class Membership {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	String bisToA
	Date endDate
	String endType
	Date startDate
	String startType
	Individual individual
	User userByVoidByUuid
	Fieldworker fieldworker
	Socialgroup socialgroup
	User userByInsertByUuid

	static hasMany = [outcomes: Outcome]
	static belongsTo = [Fieldworker, Individual, Socialgroup, User]

	static mapping = {
		datasource 'openhds'
		table 'membership'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		bisToA column:'bIsToA'
		endDate column:'endDate'
		endType column:'endType'
		startDate column:'startDate'
		startType column:'startType'

        fieldworker column: 'collectedBy_uuid'
        individual column: 'individual_uuid'
        socialgroup column: 'socialGroup_uuid'
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
		bisToA nullable: true
		endDate nullable: true
		endType nullable: true
		startDate nullable: true
		startType nullable: true
	}
}
