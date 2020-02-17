package org.philimone.hds.explorer.odk.model

class CensusMemberCore {
	//static mapWith = "none"

	String uri
	String creatorUriUser
	Date creationDate
	String lastUpdateUriUser
	Date lastUpdateDate
	Integer modelVersion
	Integer uiVersion
	Character isComplete
	Date submissionDate
	Date markedAsCompleteDate
	Date start
	Date end
	String deviceid
	Date visitDate
	String spouseName
	String metaInstanceId
	String motherName
	Date dob
	String relationshipWithHead
	String gender
	String metaInstanceName
	String fatherName
	String spouseId
	String fieldWorkerId
	String fatherId
	Date spouseDate
	String spouseType
	String householdNo
	String name
	String code
	Date residencyStartDate
	String isHouseholdHead
	String householdId
	String motherId

	Integer individualProcessed
	Integer residencyProcessed
	Integer membershipProcessed
	Integer relationshipProcessed

	static mapping = {
		datasource 'odk'
		table 'CENSUS_MEMBER_CORE'

		id name: "uri", generator: "assigned"

		version false

		uri column:'_URI'
		creatorUriUser column:'_CREATOR_URI_USER'
		creationDate column:'_CREATION_DATE'
		lastUpdateUriUser column:'_LAST_UPDATE_URI_USER'
		lastUpdateDate column:'_LAST_UPDATE_DATE'
		modelVersion column:'_MODEL_VERSION'
		uiVersion column:'_UI_VERSION'
		isComplete column:'_IS_COMPLETE'
		submissionDate column:'_SUBMISSION_DATE'
		markedAsCompleteDate column:'_MARKED_AS_COMPLETE_DATE'
		visitDate column:'VISIT_DATE'
		spouseName column:'SPOUSE_NAME'
		residencyProcessed column:'RESIDENCY_PROCESSED'
		metaInstanceId column:'META_INSTANCE_ID'
		motherName column:'MOTHER_NAME'
		dob column:'DOB'
		relationshipWithHead column:'RELATIONSHIP_WITH_HEAD'
		gender column:'GENDER'
		metaInstanceName column:'META_INSTANCE_NAME'
		fatherName column:'FATHER_NAME'
		membershipProcessed column:'MEMBERSHIP_PROCESSED'
		spouseId column:'SPOUSE_ID'
		individualProcessed column:'INDIVIDUAL_PROCESSED'
		fieldWorkerId column:'FIELD_WORKER_ID'
		fatherId column:'FATHER_ID'
		spouseDate column:'SPOUSE_DATE'
		relationshipProcessed column:'RELATIONSHIP_PROCESSED'
		spouseType column:'SPOUSE_TYPE'
		householdNo column:'HOUSEHOLD_NO'
		name column:'NAME'
		code column:'CODE'
		residencyStartDate column:'RESIDENCY_START_DATE'
		deviceid column:'DEVICEID'
		isHouseholdHead column:'IS_HOUSEHOLD_HEAD'
		householdId column:'HOUSEHOLD_ID'
		start column:'START'
		end column:'END'
		motherId column:'MOTHER_ID'
	}

	static constraints = {
		uri maxSize: 80
		creatorUriUser maxSize: 80
		lastUpdateUriUser nullable: true, maxSize: 80
		modelVersion nullable: true
		uiVersion nullable: true
		isComplete nullable: true, maxSize: 1
		submissionDate nullable: true
		markedAsCompleteDate nullable: true
		visitDate nullable: true
		spouseName nullable: true
		residencyProcessed nullable: true
		metaInstanceId nullable: true
		motherName nullable: true
		dob nullable: true
		relationshipWithHead nullable: true
		gender nullable: true
		metaInstanceName nullable: true
		fatherName nullable: true
		membershipProcessed nullable: true
		spouseId nullable: true
		individualProcessed nullable: true
		fieldWorkerId nullable: true
		fatherId nullable: true
		spouseDate nullable: true
		relationshipProcessed nullable: true
		spouseType nullable: true
		householdNo nullable: true
		name nullable: true
		code nullable: true
		residencyStartDate nullable: true
		deviceid nullable: true
		isHouseholdHead nullable: true
		householdId nullable: true
		start nullable: true
		end nullable: true
		motherId nullable: true
	}
}
