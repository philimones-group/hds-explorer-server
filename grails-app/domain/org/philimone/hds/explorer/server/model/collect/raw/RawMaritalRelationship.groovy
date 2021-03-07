package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate

class RawMaritalRelationship {

    String id
    String memberA
    String memberB
    String startStatus
    LocalDate startDate
    String endStatus
    LocalDate endDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED /* General Status, 1-All Processed, 0-Not All Processed*/

    static constraints = {
        id maxSize: 32
        startStatus nullable: false, blank:false, enumType: "identity"
        startDate nullable: false
        endStatus nullable: true, blank:true, enumType: "identity"
        endDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_marital_relationship'

        id column: "id", generator: 'uuid'

        memberA column: "member_a_code"
        memberB column: "member_b_code"
        startStatus column: "start_status"
        startDate column: "start_date"
        endStatus column: "end_status"
        endDate column: "end_date"

        processedStatus column: "processed", enumType: "identity"
    }
}
