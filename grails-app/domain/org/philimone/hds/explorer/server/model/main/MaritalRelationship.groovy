package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStartStatus

import java.time.LocalDate

class MaritalRelationship extends CollectableEntity {

    String id
    Member memberA
    Member memberB
    String memberA_code
    String memberB_code
    MaritalStartStatus startStatus
    LocalDate startDate
    MaritalEndStatus endStatus
    LocalDate endDate

    static constraints = {
        id maxSize: 32
        memberA nullable: false
        memberB nullable: false
        memberA_code nullable: false
        memberB_code nullable: false
        startStatus nullable: false, blank:false, enumType: "identity"
        startDate nullable: false
        endStatus nullable: true, blank:true, enumType: "identity"
        endDate nullable: true
    }

    static mapping = {
        table 'marital_relationship'

        id column: "id", generator: 'uuid'

        memberA column: "member_a_id"
        memberB column: "member_b_id"
        memberA_code column: "member_a_code"
        memberB_code column: "member_b_code"
        startStatus column: "start_status"
        startDate column: "start_date"
        endStatus column: "end_status"
        endDate column: "end_date"
    }
}
