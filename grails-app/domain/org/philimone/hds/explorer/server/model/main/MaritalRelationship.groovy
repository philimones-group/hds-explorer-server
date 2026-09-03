package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStartStatus
import org.philimone.hds.explorer.server.model.enums.ValidatableStatus

import java.time.LocalDate

class MaritalRelationship extends CollectableEntity {

    String id
    Member memberA
    Member memberB
    String memberA_code
    String memberB_code
    Boolean isPolygamic
    String polygamicId
    MaritalStartStatus startStatus
    LocalDate startDate
    MaritalEndStatus endStatus
    LocalDate endDate

    ValidatableStatus status

    static constraints = {
        id maxSize: 32
        memberA nullable: false
        memberB nullable: false
        memberA_code nullable: false
        memberB_code nullable: false
        isPolygamic nullable: true
        polygamicId nullable: true
        startStatus nullable: false, blank:false
        startDate nullable: false
        endStatus nullable: true, blank:true
        endDate nullable: true

        status nullable: true
    }

    static mapping = {
        table 'marital_relationship'

        id column: "id", generator: 'uuid'

        // Composite Indexes: idx_marrel_member_a (member_a_id, start_date DESC, status), idx_marrel_member_b (member_b_id, start_date DESC, status)
        memberA column: "member_a_id", index: "idx_marrel_member_a"
        memberB column: "member_b_id", index: "idx_marrel_member_b"
        memberA_code column: "member_a_code", index: "idx_member_a_code"
        memberB_code column: "member_b_code", index: "idx_member_b_code"
        isPolygamic column: "is_polygamic"
        polygamicId column: "polygamic_id", index: "idx_polygamic_id"
        startStatus column: "start_status", enumType: "identity"
        startDate column: "start_date", index: "idx_marrel_member_a,idx_marrel_member_b"
        endStatus column: "end_status", enumType: "identity"
        endDate column: "end_date"

        status column: "status", enumType: "identity", index: "idx_marrel_member_a,idx_marrel_member_b"
    }
}
