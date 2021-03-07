package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDateTime

/**
 * Household model taken from Mobile Data Collect, essentially for Enumeration Cases
 */
class RawHousehold {

    String id

    String regionCode
    String regionName
    String householdCode
    String householdName
    String headCode
    String headName
    String gpsLng
    String gpsAlt
    String gpsLat
    String gpsAcc

    String collectedBy //fieldWorkerId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    static constraints = {
        id maxSize: 32

        regionCode blank: false
        regionName blank: false
        householdCode blank: true, unique: true
        householdName blank: true
        headCode blank: true
        headName blank: true
        gpsLng blank: true, nullable: true
        gpsAlt blank: true, nullable: true
        gpsLat blank: true, nullable: true
        gpsAcc blank: true, nullable: true

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_household'

        id column: "id", generator: 'assigned'

        version false

        regionCode column: "region_code"
        regionName column: "region_name"
        householdCode column: "household_code"
        householdName column: "household_name"
        headCode column: "head_code"
        headName column: "head_name"
        gpsLng column: "gps_longitude"
        gpsAlt column: "gps_altitude"
        gpsLat column: "gps_latitude"
        gpsAcc column: "gps_accuracy"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        processedStatus column: "processed", enumType: "identity"
    }
}
