package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDateTime

/**
 * Household model taken from Mobile Data Collect, essentially for Enumeration Cases
 */
class RawRegion {

    String id

    String regionCode
    String regionName
    String parentCode

    String modules

    String collectedBy //fieldWorkerId
    String collectedDeviceId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        regionCode blank: false
        regionName blank: false
        parentCode blank: true, nullable: true /* will be hierarchy1 */

        modules nullable: true

        collectedBy blank: true
        collectedDeviceId nullable: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_region'

        id column: "id", generator: 'assigned'

        version false

        regionCode column: "region_code", index: "idx_region_code"
        regionName column: "region_name"
        parentCode column: "parent_code", index: "idx_parent_code"

        modules column: "modules"

        collectedBy column: "collected_by", index: "idx_collected_by"
        collectedDeviceId column: "collected_device_id", index: "idx_cdeviceid"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}
