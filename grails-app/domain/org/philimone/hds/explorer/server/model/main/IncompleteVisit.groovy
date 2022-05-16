package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.IncompleteVisitReason

class IncompleteVisit extends CollectableEntity {

    String id

    Visit visit
    String visitCode
    Member member
    String memberCode
    IncompleteVisitReason reason
    String reasonOther

    static constraints = {
        id maxSize: 32

        visit nullable: false
        visitCode nullable: false, blank: false
        member nullable: false
        memberCode nullable: false, blank: false
        reason nullable: true
        reasonOther nullable: true, blank: true
    }

    static mapping = {
        table "incomplete_visit"

        id column: "id", generator: 'uuid'

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"
        member column: "member_id"
        memberCode column: "member_code", index: "idx_member_code"
        reason column: "visit_reason", enumType: "identity"
        reasonOther column: "other_visit_reason"
    }
}
