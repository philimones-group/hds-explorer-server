package org.philimone.hds.explorer.server.model.main

import net.betainteractive.utilities.GeneralUtil
import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.MemberStatus
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType
import org.philimone.hds.explorer.server.model.types.StringCollectionType

import java.time.LocalDate

/**
 * This table represents Individuals or Household Members stored in the system, identified by code/extId, name and others
 */
class Member extends CollectableEntity {

    String id
    /**
     * The Household Member unique code identification
     */
    String code
    /**
     * The name of the Individual
     */
    String name
    /**
     * Gender of the Individual
     */
    Gender gender
    /*
     * Date of birth of the Individual
     */
    LocalDate dob
    /*
     * Current Age of the Individual (calculated on every synchronization)
     */
    Integer getAge(){
        return GeneralUtil.getAge(this.dob)
    }
    /*
     * Age of the Individual when he died
     */
    Integer ageAtDeath
    /*
     * The Mother of the Individual (other variables like motherCode/motherName for quick access)
     */
    Member mother
    String motherCode
    String motherName
    /*
     * The Mother of the Individual (other variables like motherCode/motherName for quick access)
     */
    Member father
    String fatherCode
    String fatherName
    /*
     * Marital Status of the Individual
     */
    MaritalStatus maritalStatus
    /*
     * The Spouser of the Individual (other variables like spouseCode/spouseName for quick access)
     */
    Member spouse
    String spouseCode
    String spouseName
    //String spouseType /*Will no longer be used for the sake of maritalStatus*/

    String education
    String religion

    String phonePrimary
    String phoneAlternative

    /**
     * Current Household where the Individual is living
     */
    Household household
    /*
     * Current Household Code (for quick access)
     */
    String householdCode
    /*
     * Current Household Name (for quick access)
     */
    String householdName
    /*
     * Residency Start Event Type (ENU/BIR/ENT/XEN) on Current Household
     */
    ResidencyStartType startType
    /*
     * The Date the Individual Started Living on Current Household
     */
    LocalDate startDate
    /*
     * Residency End Event Type (NA/CHG/EXT/DTH) on Current Household
     */
    ResidencyEndType endType
    /*
     * The Date the Individual Ended Living on Current Household
     */
    LocalDate endDate
    /*
     * The Type of Relationship with the Head of the Current Household
     */
    HeadRelationshipType headRelationshipType

    /*
     * The first Household that of the Individual in the study area
     */
    String entryHousehold
    /*
     * Residency Start Event Type (ENU/BIR/ENT/XEN) on the first Household that of the Individual in the study area
     */
    ResidencyStartType entryType
    /*
     * The Date the Individual Started Living on the first Household that in the study area
     */
    LocalDate entryDate

    //Boolean isHouseholdHead = false
    //Boolean isSecHouseholdHead = false

    /** GPS Coordinates Columns **/
    Boolean gpsNull = true
    Double gpsAccuracy
    Double gpsAltitude
    Double gpsLatitude
    Double gpsLongitude
    /** Cosine of GPS Coordinates */
    Double cosLatitude
    Double sinLatitude
    Double cosLongitude
    Double sinLongitude

    MemberStatus status

    static hasMany = [modules:String]

    boolean isHouseholdHead(){
        headRelationshipType==HeadRelationshipType.HEAD_OF_HOUSEHOLD
    }

    static transients = ['householdHead', 'age']

    static constraints = {
        id maxSize: 32
        code unique: true, blank: false
        name blank: false
        gender nullable: false
        dob nullable: false
        //age min: 0
        ageAtDeath nullable: true

        maritalStatus nullable: false

        spouse nullable: true
        spouseCode blank: true, nullable: true
        spouseName blank: true, nullable: true
        //spouseType blank: true, nullable: true

        mother nullable: true
        motherCode blank: true, nullable: true
        motherName blank: true, nullable: true

        father nullable: true
        fatherCode blank: true, nullable: true
        fatherName blank: true, nullable: true

        education blank: true, nullable: true
        religion blank: true, nullable: true

        phonePrimary blank: true, nullable: true
        phoneAlternative blank: true, nullable: true

        household nullable: true
        householdCode blank: true, nullable: true
        householdName blank: true, nullable: true
        entryHousehold nullable: true
        entryType nullable: true
        entryDate nullable: true
        startType nullable: true
        startDate nullable: true
        endType nullable: true
        endDate nullable: true

        headRelationshipType nullable: true

        //isHouseholdHead nullable: false
        //isSecHouseholdHead nullable: false

        gpsNull nullable: false
        gpsAccuracy nullable: true
        gpsAltitude nullable: true
        gpsLatitude nullable: true
        gpsLongitude nullable: true

        cosLatitude nullable: true
        sinLatitude nullable: true
        cosLongitude nullable: true
        sinLongitude nullable: true

        status nullable: true

        modules nullable: true
    }

    static mapping = {
        table 'member'

        id column: "id", generator: 'uuid'

        code column: 'code'
        name column: 'name'
        gender column: 'gender', enumType: 'identity'
        dob column: 'dob'
        //age column: 'age'
        ageAtDeath column: 'age_at_death'

        maritalStatus column: 'marital_status', enumType: 'identity'

        spouse column: 'spouse_id'
        spouseCode column: 'spouse_code', index: "idx_spouse_code"
        spouseName column: 'spouse_name'
        //spouseType column: 'spouse_type'

        mother column: 'mother_id'
        motherCode column: 'mother_code', index: "idx_mother_code"
        motherName column: 'mother_name'

        father column: 'father_id'
        fatherCode column: 'father_code', index: "idx_father_code"
        fatherName column: 'father_name'

        education column: "education"
        religion column: "religion"

        phonePrimary column: "phone_primary"
        phoneAlternative column: "phone_alternative"

        household column: 'household_id'
        householdCode column: 'household_code', index: "idx_household_code"
        householdName column: 'household_name'

        entryHousehold column: 'entry_household'
        entryType column: 'entry_type', enumType: "identity"
        entryDate column: 'entry_date'
        startType column: 'start_type', enumType: "identity"
        startDate column: 'start_date'
        endType column: 'end_type', enumType: "identity"
        endDate column: 'end_date'

        headRelationshipType column: 'head_relationship_type', enumType: 'identity'

        //isHouseholdHead column: 'is_household_head'
        //isSecHouseholdHead column: 'is_sec_household_head'

        gpsNull column: 'gps_is_null'
        gpsAccuracy column: 'gps_accuracy'
        gpsAltitude column: 'gps_altitude'
        gpsLatitude column: 'gps_latitude'
        gpsLongitude column: 'gps_longitude'

        cosLatitude column: 'cos_latitude'
        sinLatitude column: 'sin_latitude'
        cosLongitude column: 'cos_longitude'
        sinLongitude column: 'sin_longitude'

        status column: 'status'

        modules column: "modules", type: StringCollectionType, index: "idx_modules"
    }

    def static ALL_COLUMNS = ['code', 'name', 'gender', 'dob', 'age', 'ageAtDeath', 'motherCode', 'motherName', 'fatherCode', 'fatherName', 'maritalStatus', 'spouseCode', 'spouseName', 'spouseType',
                              'householdCode', 'householdName', 'isHouseholdHead', 'isSecHouseholdHead', 'startType', 'startDate', 'endType', 'endDate', 'entryHousehold', 'entryType', 'entryDate',
                              'headRelationshipType', 'gpsAccuracy', 'gpsAltitude', 'gpsLatitude', 'gpsLongitude', 'collectedId']
}
