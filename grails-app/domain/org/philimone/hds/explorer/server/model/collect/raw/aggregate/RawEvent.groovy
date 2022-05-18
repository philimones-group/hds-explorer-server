package org.philimone.hds.explorer.server.model.collect.raw.aggregate

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEventType
import org.philimone.hds.explorer.server.model.enums.RawMemberOrder

import java.time.LocalDateTime

/**
 * Table used for storing events that will be executed by the batch execution process
 */
class RawEvent {

    /* (date of event/capture) will be yyyy-MM-dd or yyyy-MM-dd HH:mm:ss */
    LocalDateTime keyDate

    /* The HDS Event to be executed) as INTEGER */
    RawEventType eventType

    /* The order of insertion in case of Member insertion order - the priority goes to heads of household and his spouse */
    RawMemberOrder eventOrder = RawMemberOrder.NOT_APPLICABLE //set it to default when creating event (this will allow to not touch events insertion code where its not necessary)

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
        eventOrder nullable: true
        eventId nullable: false, maxSize: 32
        entityCode nullable: false, maxSize: 32
        childCodes nullable: true
        processed nullable: false
    }

    static mapping = {
        //datasource 'internal'
        table '__raw_event'

        id column: "id"

        version false

        keyDate    column: "key_date",   index: "idx_kydate"
        eventType  column: "event_type", index: "idx_type", enumType: "identity"
        eventOrder column: "event_order", index: "idx_event_order", enumType: "identity"
        eventId    column: "event_uuid", index: "idx_id"
        entityCode column: "event_code", index: "idx_code"
        childCodes column: "child_codes", index: "idx_childs"
        processed column: "processed"
    }

}
