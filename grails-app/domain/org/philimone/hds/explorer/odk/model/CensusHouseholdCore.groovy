package org.philimone.hds.explorer.odk.model

class CensusHouseholdCore {
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
	String fieldWorkerId
	String regionId
	String headName
	String headCode
	Date visitDate
	BigDecimal gpsLng
	BigDecimal gpsAlt
	String metaInstanceId
	String householdName
	String deviceid
	String regionName
	BigDecimal gpsLat
	String householdId
	BigDecimal gpsAcc
	Date start
	Date end
	String metaInstanceName
	Integer locationProcessed
	Integer socialgroupProcessed

	static mapping = {
		datasource 'odk'
		table 'CENSUS_HOUSEHOLD_CORE'

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
		fieldWorkerId column:'FIELD_WORKER_ID'
		regionId column:'REGION_ID'
		headName column:'HEAD_NAME'
		headCode column:'HEAD_CODE'
		socialgroupProcessed column:'SOCIALGROUP_PROCESSED'
		visitDate column:'VISIT_DATE'
		gpsLng column:'GPS_LNG'
		gpsAlt column:'GPS_ALT'
		metaInstanceId column:'META_INSTANCE_ID'
		householdName column:'HOUSEHOLD_NAME'
		deviceid column:'DEVICEID'
		regionName column:'REGION_NAME'
		gpsLat column:'GPS_LAT'
		householdId column:'HOUSEHOLD_ID'
		gpsAcc column:'GPS_ACC'
		start column:'START'
		end column:'END'
		metaInstanceName column:'META_INSTANCE_NAME'
		locationProcessed column:'LOCATION_PROCESSED'
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
		fieldWorkerId nullable: true
		regionId nullable: true
		headName nullable: true
		headCode nullable: true
		socialgroupProcessed nullable: true
		visitDate nullable: true
		gpsLng nullable: true, scale: 10
		gpsAlt nullable: true, scale: 10
		metaInstanceId nullable: true
		householdName nullable: true
		deviceid nullable: true
		regionName nullable: true
		gpsLat nullable: true, scale: 10
		householdId nullable: true
		gpsAcc nullable: true, scale: 10
		start nullable: true
		end nullable: true
		metaInstanceName nullable: true
		locationProcessed nullable: true
	}
}
