package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

/**
 * Household model taken from Mobile Data Collect, essentially for Enumeration Cases
 */
class RawRegion {

    String id

    String regionCode
    String regionName
    String parentCode

    String collectedBy //fieldWorkerId
    Date collectedDate //visitDate / creationDate
    Date uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    static constraints = {
        id maxSize: 32

        regionCode blank: false
        regionName blank: false
        parentCode blank: true, nullable: true /* will be hierarchy1 */

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_region'

        id column: "id", generator: 'assigned'

        version false

        regionCode column: "region_code"
        regionName column: "region_name"
        parentCode column: "parent_code"

        collectedBy column: "collected_by"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        processedStatus column: "processed", enumType: "identity"
    }
}
