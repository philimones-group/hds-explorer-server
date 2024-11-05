package org.philimone.hds.explorer.server.model.collect.raw.editors

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDateTime

/**
 * Household model taken from Mobile Data Collect, essentially for Enumeration Cases
 */
class RawEditRegion {

    String id

    String regionCode
    String regionName
    String parentCode

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

        regionCode blank: false
        regionName blank: false
        parentCode blank: true, nullable: true /* will be hierarchy1 */

        collectedBy blank: true
        collectedDeviceId nullable: true
        collectedStart nullable:true
        collectedEnd nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_edit_region'

        id column: "id", generator: 'assigned'

        version false

        regionCode column: "region_code", index: "idx_region_code"
        regionName column: "region_name"
        parentCode column: "parent_code", index: "idx_parent_code"

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

