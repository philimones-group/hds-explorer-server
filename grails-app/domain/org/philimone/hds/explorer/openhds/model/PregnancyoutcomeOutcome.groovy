package org.philimone.hds.explorer.openhds.model

class PregnancyoutcomeOutcome implements Serializable {
	//static mapWith = "none"

	//String pregnancyoutcomeUuid
	//String outcomesUuid
	Pregnancyoutcome pregnancyoutcome
	Outcome outcome

	/*
	int hashCode() {
		def builder = new HashCodeBuilder()
		builder.append pregnancyoutcomeUuid
		builder.append outcomesUuid
		builder.toHashCode()
	}*/
/*
	boolean equals(other) {
		if (other == null) return false
		def builder = new EqualsBuilder()
		builder.append pregnancyoutcomeUuid, other.pregnancyoutcomeUuid
		builder.append outcomesUuid, other.outcomesUuid
		builder.isEquals()
	}*/

	static belongsTo = [Outcome, Pregnancyoutcome]

	static mapping = {
		datasource 'openhds'
		table 'pregnancyoutcome_outcome'

		id composite: ["pregnancyoutcome", "outcome"]
		version false
		//pregnancyoutcomeUuid column:'pregnancyoutcome_uuid'
		//outcomesUuid column:'outcomes_uuid'
		pregnancyoutcome column: 'pregnancyoutcome_uuid'
		outcome column: 'outcomes_uuid'
	}

	static constraints = {
		//pregnancyoutcomeUuid maxSize: 32
		//outcomesUuid maxSize: 32, unique: true
	}
}
