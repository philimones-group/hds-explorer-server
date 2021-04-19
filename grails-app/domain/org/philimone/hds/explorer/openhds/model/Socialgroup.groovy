package org.philimone.hds.explorer.openhds.model

class Socialgroup {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	String extId
	String groupName
	String groupType
	Individual individual
	User voidBy
	Fieldworker fieldworker
	User insertBy

	static hasMany = [memberships: Membership]
	static belongsTo = [Fieldworker, Individual, User]

	static mapping = {
		datasource 'openhds'
		table 'socialgroup'

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
		groupName column:'groupName'
		groupType column:'groupType'

        fieldworker column: 'collectedBy_uuid'
        individual column: 'groupHead_uuid'
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
		extId nullable: true
		groupName nullable: true
		groupType nullable: true
	}
}
