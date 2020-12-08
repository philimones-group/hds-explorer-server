package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

class RawResidency {

    String id
    String memberCode
    String householdCode
    String startType
    Date startDate
    String endType
    Date endDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED /* General Status, 1-All Processed, 0-Not All Processed*/

    static constraints = {
        id maxSize: 32
        householdCode blank: false
        memberCode blank: false
        startType nullable: true, blank:true
        startDate nullable: true
        endType nullable: true, blank:true
        endDate nullable: true

        processedStatus nullable: true
    }

    static mapping = {
        table '_raw_residency'

        id column: "id", generator: 'uuid'

        householdCode column: "household_code"
        memberCode column: "member_code"
        startType column: "start_type"
        startDate column: "start_date"
        endType column: "end_type"
        endDate column: "end_date"

        processedStatus column: "processed", enumType: "identity"
    }
}
