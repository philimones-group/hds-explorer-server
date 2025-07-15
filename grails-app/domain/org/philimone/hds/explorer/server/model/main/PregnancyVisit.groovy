package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.BirthPlace
import org.philimone.hds.explorer.server.model.enums.BreastFeedingStatus
import org.philimone.hds.explorer.server.model.enums.HealthcareProviderType
import org.philimone.hds.explorer.server.model.enums.NewBornStatus
import org.philimone.hds.explorer.server.model.enums.PregnancyStatus
import org.philimone.hds.explorer.server.model.enums.PregnancyVisitType

import java.time.LocalDate

class PregnancyVisit extends CollectableEntity {

    String id
    Visit visit
    String visitCode
    String code
    Member mother
    String motherCode
    PregnancyStatus status  // PREGNANT, DELIVERED, LOST_TRACK
    Integer visitNumber
    PregnancyVisitType visitType  // ANTEPARTUM, POSTPARTUM
    LocalDate visitDate

    /* Antepartum questions */
    Integer weeksGestation  //How many weeks pregnant is the mother? (Gestational age in weeks)
    Boolean prenatalCareReceived //Has the mother attended any prenatal care visits?
    HealthcareProviderType prenatalCareProvider  //Where did the mother receive prenatal care?  (HOSPITAL, CLINIC, MIDWIFE, TRADITIONAL_HEALER, OTHER)
    Boolean complicationsReported //Has the mother experienced any health issues or complications during pregnancy?
    String complicationDetails //Please specify the complications reported?
    Boolean hasBirthPlan //Does the mother have a birth plan?
    BirthPlace expectedBirthPlace //Where does the mother plan to give birth?
    String birthPlaceOther //Where does the mother plan to give birth?
    Boolean transportationPlan //Has the mother arranged transportation for delivery?
    Boolean financialPreparedness //Does the mother have financial support for medical expenses related to delivery?

    /* Postpartum questions */
    Boolean postpartumComplications //Were there any complications during or after delivery?
    String postpartumComplicationDetails //If complications occurred, please specify.  - Free text – e.g., excessive bleeding, high fever, infections, retained placenta, etc.
    BreastFeedingStatus breastfeedingStatus  // EXCLUSIVE, PARTIAL, NOT_BREASTFEEDING
    Boolean resumedDailyActivities //Has the mother resumed normal daily activities? (Yes/No)
    Boolean attendedPostpartumCheckup //Has the mother attended a postpartum health check-up? (Yes/No)

    static hasMany = [childs:PregnancyVisitChild]

    static constraints = {
        id maxSize: 32

        code nullable: false
        mother nullable: false
        motherCode nullable: false, blank: false
        status nullable: false
        visitNumber nullable: false, min: 1
        visitType nullable: false
        visitDate nullable: false

        weeksGestation nullable: true, min: 1, max: 42
        prenatalCareReceived nullable: true
        prenatalCareProvider nullable: true
        complicationsReported nullable: true
        complicationDetails nullable: true, blank: true
        hasBirthPlan nullable: true
        expectedBirthPlace nullable: true, blank: true
        birthPlaceOther nullable: true
        transportationPlan nullable: true
        financialPreparedness nullable: true

        postpartumComplications nullable: true
        postpartumComplicationDetails nullable: true, blank: true
        breastfeedingStatus nullable: true
        resumedDailyActivities nullable: true
        attendedPostpartumCheckup nullable: true

        visit nullable: false
        visitCode nullable: false, blank: false
    }

    static mapping = {
        table 'pregnancy_visit'

        id column: "id", generator: 'uuid'

        code column: "code", index: "idx_code"
        mother column: "mother_id"
        motherCode column: "mother_code", index: "idx_mother_code"
        status column: "status", enumType: "identity"
        visitNumber column: "visit_number"
        visitType column: "visit_type", enumType: "identity"
        visitDate column: "visit_date"

        weeksGestation column: "weeks_gestation"
        prenatalCareReceived column: "prenatal_care_received"
        prenatalCareProvider column: "prenatal_care_provider", enumType: "identity"
        complicationsReported column: "complications_reported"
        complicationDetails column: "complication_details"
        hasBirthPlan column: "has_birth_plan"
        expectedBirthPlace column: "expected_birth_place", enumType: "identity"
        birthPlaceOther column: "birth_place_other"
        transportationPlan column: "transportation_plan"
        financialPreparedness column: "financial_preparedness"

        postpartumComplications column: "postpartum_complications"
        postpartumComplicationDetails column: "postpartum_complication_details"
        breastfeedingStatus column: "breastfeeding_status", enumType: "identity"
        resumedDailyActivities column: "resumed_daily_activities"
        attendedPostpartumCheckup column: "attended_postpartum_checkup"

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"
    }
}
