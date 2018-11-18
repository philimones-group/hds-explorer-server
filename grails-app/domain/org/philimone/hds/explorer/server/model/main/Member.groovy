package org.philimone.hds.explorer.server.model.main

/**
 * This table represents Individuals or Household Members stored in the system, identified by code/extId, name and others
 */
class Member {

    String code
    String name
    String gender
    String dob
    Integer age
    Integer ageAtDeath

    String spouseCode
    String spouseName

    String motherCode
    String motherName
    String fatherCode
    String fatherName
    /**
     * Current HouseHold Status
     */
    String householdCode
    String householdName
    String entryType
    String entryDate
    String startType
    String startDate
    String endType
    String endDate

    Boolean isHouseholdHead = false
    Boolean isSecHouseholdHead = false

    /** GPS Status */
    Boolean gpsNull
    Double gpsAccuracy
    Double gpsAltitude
    Double gpsLatitude
    Double gpsLongitude

    Double cosLatitude
    Double sinLatitude
    Double cosLongitude
    Double sinLongitude

    static constraints = {
        code unique: true, blank: false
        name blank: false
        gender blank: false
        dob nullable: false
        age min: 0
        ageAtDeath nullable: true

        spouseCode blank: true, nullable: true
        spouseName blank: true, nullable: true

        motherCode blank: true, nullable: true
        motherName blank: true, nullable: true
        fatherCode blank: true, nullable: true
        fatherName blank: true, nullable: true

        householdCode blank: false, nullable: false
        householdName blank: false, nullable: false
        entryType nullable: true
        entryDate nullable: true
        startType nullable: true
        startDate nullable: true
        endType nullable: true
        endDate nullable: true

        isHouseholdHead nullable: false
        isSecHouseholdHead nullable: false

        gpsNull nullable: false
        gpsAccuracy nullable: true
        gpsAltitude nullable: true
        gpsLatitude nullable: true
        gpsLongitude nullable: true

        cosLatitude nullable: true
        sinLatitude nullable: true
        cosLongitude nullable: true
        sinLongitude nullable: true
    }

    static mapping = {
        code column: 'code'
        name column: 'name'
        gender column: 'gender'
        dob column: 'dob'
        age column: 'age'
        ageAtDeath column: 'age_at_death'

        spouseCode column: 'spouse_code'
        spouseName column: 'spouse_name'

        motherCode column: 'mother_code'
        motherName column: 'mother_name'
        fatherCode column: 'father_code'
        fatherName column: 'father_name'

        householdCode column: 'household_code'
        householdName column: 'household_name'
        entryType column: 'entry_type'
        entryDate column: 'entry_date'
        startType column: 'start_type'
        startDate column: 'start_date'
        endType column: 'end_type'
        endDate column: 'end_date'

        isHouseholdHead column: 'is_household_head'
        isSecHouseholdHead column: 'is_sec_household_head'

        gpsNull column: 'gps_is_null'
        gpsAccuracy column: 'gps_accuracy'
        gpsAltitude column: 'gps_altitude'
        gpsLatitude column: 'gps_latitude'
        gpsLongitude column: 'gps_longitude'

        cosLatitude column: 'cos_latitude'
        sinLatitude column: 'sin_latitude'
        cosLongitude column: 'cos_longitude'
        sinLongitude column: 'sin_longitude'
    }
}
