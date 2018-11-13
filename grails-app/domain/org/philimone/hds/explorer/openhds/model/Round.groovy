package org.philimone.hds.explorer.openhds.model

class Round {
	//static mapWith = "none"

	String uuid
	Date endDate
	String remarks
	Integer roundNumber
	Date startDate

	static mapping = {
		datasource 'openhds'
		table 'round'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		endDate column:'endDate'
		remarks column:'remarks'
		roundNumber column:'roundNumber'
		startDate column:'startDate'
	}

	static constraints = {
		uuid maxSize: 32
		endDate nullable: true
		remarks nullable: true
		roundNumber nullable: true
		startDate nullable: true
	}
}
