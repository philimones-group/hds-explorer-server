package org.philimone.hds.explorer.openhds.model

class Pregnancyoutcome {
	//static mapWith = "none"

	String uuid
	Boolean deleted
	Date insertDate
	Date voidDate
	String voidReason
	String status
	String statusMessage
	Integer childEverBorn
	Integer numberOfLiveBirths
	Date outcomeDate
	Individual mother
	User userByVoidByUuid
	Fieldworker fieldworker
	Visit visit
	Individual father
	User userByInsertByUuid

	static hasMany = [pregnancyoutcomeOutcomes: PregnancyoutcomeOutcome]
	static belongsTo = [Fieldworker, Individual, User, Visit]

	static mapping = {
		datasource 'openhds'
		table 'pregnancyoutcome'

		id name: "uuid", generator: "assigned"
		version false
		uuid column:'uuid'
		deleted column:'deleted'
		insertDate column:'insertDate'
		voidDate column:'voidDate'
		voidReason column:'voidReason'
		status column:'status'
		statusMessage column:'statusMessage'
		childEverBorn column:'childEverBorn'
		numberOfLiveBirths column:'numberOfLiveBirths'
		outcomeDate column:'outcomeDate'

        fieldworker column: 'collectedBy_uuid'
        father column: 'father_uuid'
        mother column: 'mother_uuid'
		visit column: 'visit_uuid'
	}

	static constraints = {
		uuid maxSize: 32
		insertDate nullable: true
		voidDate nullable: true
		voidReason nullable: true
		status nullable: true
		statusMessage nullable: true
		childEverBorn nullable: true
		numberOfLiveBirths nullable: true
		outcomeDate nullable: true
	}
}
