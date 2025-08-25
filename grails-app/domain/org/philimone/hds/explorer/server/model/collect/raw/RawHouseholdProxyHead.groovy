package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Raw staging table for Household Proxy Head assignments captured on mobile.
 * Enum-like fields are stored as STRING CODES (not enums) to keep parsing simple.
 * This domain will represent either a start of end event
 */
class RawHouseholdProxyHead {

    String id

    String visitCode
    String householdCode                 // target household
    String proxyHeadType                 // RESIDENT | NON_RESIDENT | NON_DSS_MEMBER
    String proxyHeadCode                 // DSS member code (when applicable)
    String proxyHeadName                 // external name (when NON_DSS_MEMBER)
    String proxyHeadRole                 // e.g., ADM/WRD/...

    LocalDate eventDate
    String reason                     // e.g., REP/RES/RPH/HTC/DEC/EXP/OTH
    String reasonOther

    String modules

    byte[] extensionForm

    // Standard raw collection metadata
    String collectedBy
    String collectedDeviceId
    LocalDateTime collectedStart
    LocalDateTime collectedEnd
    LocalDateTime collectedDate
    LocalDateTime uploadedDate

    // Processing status
    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED
    boolean postExecution = false

    static constraints = {
        id maxSize: 32

        visitCode nullable: false
        householdCode blank: false
        proxyHeadType blank: false

        proxyHeadCode nullable: true, blank: true
        proxyHeadName nullable: true, blank: true

        proxyHeadRole nullable: true, blank: true

        eventDate nullable: true
        reason nullable: true, blank: true
        reasonOther nullable: true

        modules nullable: true

        extensionForm nullable: true

        collectedBy nullable: true, blank: true
        collectedDeviceId nullable: true, blank: true
        collectedStart nullable: true
        collectedEnd nullable: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
        postExecution nullable: false
    }

    static mapping = {
        table '_raw_household_proxy_head'

        id column: 'id', generator: 'uuid'

        visitCode column: "visit_code"
        householdCode column: 'household_code', index: 'idx_raw_hhph_household_code'
        proxyHeadType column: 'proxy_head_type', index: 'idx_raw_hhph_type'
        proxyHeadCode column: 'proxy_head_code', index: 'idx_raw_hhph_proxy_code'
        proxyHeadName column: 'proxy_head_name'
        proxyHeadRole column: 'proxy_head_role', index: 'idx_raw_hhph_proxy_role'

        eventDate column: 'event_date'
        reason column: 'reason', enumType: 'identity', index: 'idx_hh_start_reason'
        reasonOther column: 'reason_other'

        modules column: "modules"

        extensionForm column: "extension_form", sqlType: "mediumblob"

        collectedBy column: 'collected_by'
        collectedDeviceId column: 'collected_device_id'
        collectedStart column: 'collected_start'
        collectedEnd column: 'collected_end'
        collectedDate column: 'collected_date'
        uploadedDate column: 'uploaded_date'

        postExecution column: 'post_execution'

        processedStatus column: 'processed', enumType: 'identity'
    }
}

