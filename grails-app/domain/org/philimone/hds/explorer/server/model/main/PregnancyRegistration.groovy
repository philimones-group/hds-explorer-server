package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.EstimatedDateOfDeliveryType
import org.philimone.hds.explorer.server.model.enums.PregnancyStatus

import java.time.LocalDate

class PregnancyRegistration extends CollectableEntity {

    String id
    String code
    Member mother
    String motherCode
    LocalDate recordedDate
    Integer pregMonths
    Boolean eddKnown
    Boolean hasPrenatalRecord
    LocalDate eddDate
    EstimatedDateOfDeliveryType eddType
    Boolean lmpKnown
    LocalDate lmpDate
    LocalDate expectedDeliveryDate
    PregnancyStatus status
    Visit visit
    String visitCode


    static constraints = {
        id maxSize: 32

        code unique: true
        mother nullable: false
        motherCode nullable: false, blank: false
        recordedDate nullable: false
        pregMonths nullable: true
        eddKnown nullable: true
        hasPrenatalRecord nullable: true
        eddDate nullable: true
        eddType nullable: true
        lmpKnown nullable: true
        lmpDate nullable: true
        expectedDeliveryDate nullable: true
        status nullable: false
        visit nullable: false
        visitCode blank: false
    }

    static mapping = {
        table 'pregnancy_registration'

        id column: "id", generator: 'uuid'

        code column: "code"
        mother column: "mother_id"
        motherCode column: "mother_code", index: "idx_mother_code"
        recordedDate column: "recorded_date"

        pregMonths column: "preg_months"
        eddKnown column: "edd_known"
        hasPrenatalRecord column: "has_precord"
        eddDate column: "edd_date"
        eddType column: "edd_type"
        lmpKnown column: "lmp_known"
        lmpDate column: "lmp_date"
        expectedDeliveryDate column: "expected_delivery_date"
        status column: "status"

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"
    }
}
