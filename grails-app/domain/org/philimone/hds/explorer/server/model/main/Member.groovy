package org.philimone.hds.explorer.server.model.main

import com.lowagie.text.html.HtmlTagMap
import net.betainteractive.utilities.StringUtil

/**
 * This table represents Individuals or Household Members stored in the system, identified by code/extId, name and others
 */
class Member {

    String code
    String name
    String gender
    Date dob
    Integer age
    Integer ageAtDeath

    String motherCode
    String motherName
    String fatherCode
    String fatherName

    String spouseCode
    String spouseName
    String spouseType  /* Relationship Type */
                       /*
                          MAR = Married            = 2
                          SEP = Separated/Divorced = 3
                          WID = Widowed            = 4
                          LIV = Living Together    = 5  */

    /**
     * Current HouseHold Status
     */
    String householdCode
    String householdName
    String startType
    Date   startDate
    String endType
    Date   endDate

    String entryHousehold
    String entryType
    Date   entryDate


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

    String toXML(){
        return  ("<member>") +
                ((code==null || code.isEmpty()) ?                    "<code />" : "<code>${code}</code>") +
                ((name==null || name.isEmpty()) ?                    "<name />" : "<name>${name}</name>") +
                ((gender==null || gender.isEmpty()) ?                "<gender />" : "<gender>${gender}</gender>") +
                ((dob==null) ?                                       "<dob />" : "<dob>${StringUtil.format(dob, "yyyy-MM-dd")}</dob>") +
                ((age==null) ?                                       "<age />" : "<age>${age}</age>") +

                ((ageAtDeath==null) ?                                "<ageAtDeath />" : "<ageAtDeath>${ageAtDeath}</ageAtDeath>") +

                ((motherCode==null || motherCode.isEmpty()) ?        "<motherCode />" : "<motherCode>${motherCode}</motherCode>") +
                ((motherName==null || motherName.isEmpty()) ?        "<motherName />" : "<motherName>${motherName}</motherName>") +
                ((fatherCode==null || fatherCode.isEmpty()) ?        "<fatherCode />" : "<fatherCode>${fatherCode}</fatherCode>") +
                ((fatherName==null || fatherName.isEmpty()) ?        "<fatherName />" : "<fatherName>${fatherName}</fatherName>") +

                ((spouseCode==null || spouseCode.isEmpty()) ?        "<spouseCode />" : "<spouseCode>${spouseCode}</spouseCode>") +
                ((spouseName==null || spouseName.isEmpty()) ?        "<spouseName />" : "<spouseName>${spouseName}</spouseName>") +
                ((spouseType==null || spouseType.isEmpty()) ?        "<spouseType />" : "<spouseType>${spouseType}</spouseType>") +

                ((householdCode==null || householdCode.isEmpty()) ?  "<householdCode />" : "<householdCode>${householdCode}</householdCode>") +
                ((householdName==null || householdName.isEmpty()) ?  "<householdName />" : "<householdName>${householdName}</householdName>") +

                ((startType==null || startType.isEmpty()) ?          "<startType />" : "<startType>${startType}</startType>") +
                ((startDate==null)                        ?          "<startDate />" : "<startDate>${StringUtil.format(startDate, "yyyy-MM-dd")}</startDate>") +
                ((endType==null || endType.isEmpty())     ?          "<endType />"   : "<endType>${endType}</endType>") +
                ((endDate==null)                          ?          "<endDate />"   : "<endDate>${StringUtil.format(endDate, "yyyy-MM-dd")}</endDate>") +

                ((entryHousehold==null || entryHousehold.isEmpty()) ? "<entryHousehold />" : "<entryHousehold>${entryHousehold}</entryHousehold>") +
                ((entryType==null || entryType.isEmpty())           ? "<entryType />" : "<entryType>${entryType}</entryType>") +
                ((entryDate==null)                                  ? "<entryDate />" : "<entryDate>${StringUtil.format(entryDate, "yyyy-MM-dd")}</entryDate>") +

                ((isHouseholdHead==null)                            ? "<isHouseholdHead />" : "<isHouseholdHead>${isHouseholdHead}</isHouseholdHead>") +
                ((isSecHouseholdHead==null)                         ? "<isSecHouseholdHead />" : "<isSecHouseholdHead>${isSecHouseholdHead}</isSecHouseholdHead>") +

                ((gpsAccuracy == null)                              ? "<gpsAccuracy />" : "<gpsAccuracy>${gpsAccuracy}</gpsAccuracy>") +
                ((gpsAltitude == null)                              ? "<gpsAltitude />" : "<gpsAltitude>${gpsAltitude}</gpsAltitude>") +
                ((gpsLatitude == null)                              ? "<gpsLatitude />" : "<gpsLatitude>${gpsLatitude}</gpsLatitude>") +
                ((gpsLongitude == null)                             ? "<gpsLongitude />" : "<gpsLongitude>${gpsLongitude}</gpsLongitude>") +
                ("</member>")
    }

    static constraints = {
        code unique: true, blank: false
        name blank: false
        gender blank: false
        dob nullable: false
        age min: 0
        ageAtDeath nullable: true

        spouseCode blank: true, nullable: true
        spouseName blank: true, nullable: true
        spouseType blank: true, nullable: true

        motherCode blank: true, nullable: true
        motherName blank: true, nullable: true
        fatherCode blank: true, nullable: true
        fatherName blank: true, nullable: true

        householdCode blank: false, nullable: false
        householdName blank: false, nullable: false
        entryHousehold nullable: true
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
        spouseType column: 'spouse_type'

        motherCode column: 'mother_code'
        motherName column: 'mother_name'
        fatherCode column: 'father_code'
        fatherName column: 'father_name'

        householdCode column: 'household_code'
        householdName column: 'household_name'
        entryHousehold column: 'entry_household'
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

    def static ALL_COLUMNS = ['code', 'name', 'gender', 'dob', 'age', 'ageAtDeath', 'motherCode', 'motherName', 'fatherCode', 'fatherName', 'spouseCode', 'spouseName', 'spouseType',
                              'householdCode', 'householdName', 'isHouseholdHead', 'isSecHouseholdHead', 'startType', 'startDate', 'endType', 'endDate', 'entryHousehold', 'entryType', 'entryDate',
                              'gpsAccuracy', 'gpsAltitude', 'gpsLatitude', 'gpsLongitude']
}
