package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.EstimatedDateOfDeliveryType
import org.philimone.hds.explorer.server.model.enums.PregnancyStatus
import org.philimone.hds.explorer.server.model.enums.PregnancyVisitType

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

    Integer   summary_antepartum_count
    Integer   summary_postpartum_count
    PregnancyStatus summary_last_visit_status
    PregnancyVisitType summary_last_visit_type
    LocalDate summary_last_visit_date
    LocalDate summary_first_visit_date
    Boolean   summary_has_pregnancy_outcome
    Integer   summary_nr_outcomes
    Boolean   summary_followup_completed

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

        summary_antepartum_count nullable: true, min: 0
        summary_postpartum_count nullable: true, min: 0
        summary_last_visit_status nullable: true
        summary_last_visit_type nullable: true
        summary_last_visit_date nullable: true
        summary_first_visit_date nullable: true
        summary_has_pregnancy_outcome nullable: true
        summary_nr_outcomes nullable: true, min: 0
        summary_followup_completed nullable: true
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
        eddType column: "edd_type", enumType: "identity"
        lmpKnown column: "lmp_known"
        lmpDate column: "lmp_date"
        expectedDeliveryDate column: "expected_delivery_date"
        status column: "status", enumType: "identity"

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"

        summary_antepartum_count column: "summary_antepartum_count"
        summary_postpartum_count column: "summary_postpartum_count"
        summary_last_visit_status column: "summary_last_visit_status", enumType: "identity"
        summary_last_visit_type column: "summary_last_visit_type", enumType: "identity"
        summary_last_visit_date column: "summary_last_visit_date"
        summary_first_visit_date column: "summary_first_visit_date"
        summary_has_pregnancy_outcome column: "summary_has_pregnancy_outcome"
        summary_nr_outcomes column: "summary_nr_outcomes"
        summary_followup_completed column: "summary_followup_completed"
    }
}
