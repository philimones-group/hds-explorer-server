package org.philimone.hds.explorer.server.model.collect.raw.aggregate

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEventType

import java.time.LocalDateTime

/**
 * Table used for storing events that will be executed by the batch execution process
 */
class RawEvent {

    /* (date of event/capture) will be yyyy-MM-dd or yyyy-MM-dd HH:mm:ss */
    LocalDateTime keyDate

    /* The HDS Event to be executed) as INTEGER */
    RawEventType eventType

    /* RawDomainModel ID */
    String eventId

    /* Code of a RawModel domain (household, member, visit, pregnancy code, etc) */
    String entityCode

    String childCodes

    /* Is it processed (Yes or No) */
    ProcessedStatus processed = ProcessedStatus.NOT_PROCESSED

    boolean isProcessed(){
        processed != ProcessedStatus.NOT_PROCESSED
    }

    void setChildCodesFrom(List<String> codes){
        this.childCodes = codes.toString()
    }

    static transients = ['isProcessed']

    static constraints = {

        keyDate nullable: false
        eventType nullable: false
        eventId nullable: false, maxSize: 32
        entityCode nullable: false, maxSize: 32
        childCodes nullable: true
        processed nullable: false
    }

    static mapping = {
        //datasource 'internal'
        table '__raw_event'

        version false

        keyDate    column: "key_date",   indexColumn: [name: "idx_kydate", type: Integer]
        eventType  column: "event_type", indexColumn: [name: "idx_type", type: Integer], enumType: "identity"
        eventId    column: "event_uuid", indexColumn: [name: "idx_id", type: Integer]
        entityCode column: "event_code", indexColumn: [name: "idx_code", type: Integer]
        childCodes column: "child_codes", indexColumn: [name: "idx_childs", type: Integer]

        processed column: "processed"
    }

}
