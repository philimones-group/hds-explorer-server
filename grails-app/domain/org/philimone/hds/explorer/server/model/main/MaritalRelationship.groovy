package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStartStatus

class MaritalRelationship extends CollectableEntity {

    Member memberA
    Member memberB
    String memberA_code
    String memberB_code
    MaritalStartStatus startStatus
    Date startDate
    MaritalEndStatus endStatus
    Date endDate

    static constraints = {
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
        datasource 'main'
        table 'marital_relationship'

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
