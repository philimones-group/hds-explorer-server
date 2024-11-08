package org.philimone.hds.explorer.server.model.collect.raw.editors

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDateTime

/**
 * Household model taken from Mobile Data Collect, essentially for Enumeration Cases
 */
class RawEditHousehold {

    String id

    String regionCode
    String regionName
    String householdCode
    String householdName
    String headCode
    String headName
    String gpsLat
    String gpsLon
    String gpsAlt
    String gpsAcc

    String collectedBy //fieldWorkerId
    String collectedDeviceId
    LocalDateTime collectedStart
    LocalDateTime collectedEnd
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        regionCode blank: true, nullable: true
        regionName blank: true, nullable: true
        householdCode blank: true
        householdName blank: true
        headCode blank: true, nullable: true
        headName blank: true, nullable: true
        gpsLon blank: true, nullable: true
        gpsAlt blank: true, nullable: true
        gpsLat blank: true, nullable: true
        gpsAcc blank: true, nullable: true

        collectedBy blank: true
        collectedDeviceId nullable:true
        collectedStart nullable:true
        collectedEnd nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_edit_household'

        id column: "id", generator: 'assigned'

        version false

        regionCode column: "region_code", index: "idx_region_code"
        regionName column: "region_name"
        householdCode column: "household_code", index: "idx_household_code"
        householdName column: "household_name"
        headCode column: "head_code", index: "idx_head_code"
        headName column: "head_name"
        gpsLon column: "gps_longitude"
        gpsAlt column: "gps_altitude"
        gpsLat column: "gps_latitude"
        gpsAcc column: "gps_accuracy"

        collectedBy column: "collected_by", index: "idx_collected_by"
        collectedDeviceId column: "collected_device_id", index: "idx_cdeviceid"
        collectedStart column: "collected_start"
        collectedEnd column: "collected_end"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}


