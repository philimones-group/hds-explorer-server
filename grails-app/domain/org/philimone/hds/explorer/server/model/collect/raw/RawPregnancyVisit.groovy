package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.*
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.PregnancyVisitChild
import org.philimone.hds.explorer.server.model.main.Visit

import java.time.LocalDate
import java.time.LocalDateTime

class RawPregnancyVisit {

    String id
    String visitCode
    String code
    String motherCode
    String status  // PREGNANT, DELIVERED, LOST_TRACK
    Integer visitNumber
    String visitType  // ANTEPARTUM, POSTPARTUM
    LocalDate visitDate

    /* Antepartum questions */
    Integer weeksGestation  //How many weeks pregnant is the mother? (Gestational age in weeks)
    Boolean prenatalCareReceived //Has the mother attended any prenatal care visits?
    String prenatalCareProvider  //Where did the mother receive prenatal care?  (HOSPITAL, CLINIC, MIDWIFE, TRADITIONAL_HEALER, OTHER)
    Boolean complicationsReported //Has the mother experienced any health issues or complications during pregnancy?
    String complicationDetails //Please specify the complications reported?
    Boolean hasBirthPlan //Does the mother have a birth plan?
    String expectedBirthPlace //Where does the mother plan to give birth?
    String birthPlaceOther //Where does the mother plan to give birth?
    Boolean transportationPlan //Has the mother arranged transportation for delivery?
    Boolean financialPreparedness //Does the mother have financial support for medical expenses related to delivery?

    /* Postpartum questions */
    Boolean postpartumComplications //Were there any complications during or after delivery?
    String postpartumComplicationDetails //If complications occurred, please specify.  - Free text – e.g., excessive bleeding, high fever, infections, retained placenta, etc.
    String breastfeedingStatus  // EXCLUSIVE, PARTIAL, NOT_BREASTFEEDING
    Boolean resumedDailyActivities //Has the mother resumed normal daily activities? (Yes/No)
    Boolean attendedPostpartumCheckup //Has the mother attended a postpartum health check-up? (Yes/No)

    String modules

    byte[] extensionForm

    String collectedBy //fieldWorkerId
    String collectedDeviceId
    String collectedHouseholdId
    String collectedMemberId
    LocalDateTime collectedStart
    LocalDateTime collectedEnd
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false

    static hasMany = [childs: RawPregnancyVisitChild]

    static constraints = {
        id maxSize: 32

        visitCode nullable: false, blank: false
        code nullable: false
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

        extensionForm nullable: true

        collectedBy blank: true
        collectedDeviceId nullable:true
        collectedHouseholdId nullable:true
        collectedMemberId nullable:true
        collectedStart nullable:true
        collectedEnd nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true

        modules nullable: true
    }

    static mapping = {
        table '_raw_pregnancy_visit'

        id column: "id", generator: 'uuid'

        visitCode column: "visit_code", index: "idx_visit_code"
        code column: "code", index: "idx_code"
        motherCode column: "mother_code", index: "idx_mother_code"
        status column: "status", index: "idx_status"
        visitNumber column: "visit_number"
        visitType column: "visit_type", index: "idx_visit_type"
        visitDate column: "visit_date"

        weeksGestation column: "weeks_gestation"
        prenatalCareReceived column: "prenatal_care_received"
        prenatalCareProvider column: "prenatal_care_provider"
        complicationsReported column: "complications_reported"
        complicationDetails column: "complication_details"
        hasBirthPlan column: "has_birth_plan"
        expectedBirthPlace column: "expected_birth_place"
        birthPlaceOther column: "birth_place_other"
        transportationPlan column: "transportation_plan"
        financialPreparedness column: "financial_preparedness"

        postpartumComplications column: "postpartum_complications"
        postpartumComplicationDetails column: "postpartum_complication_details"
        breastfeedingStatus column: "breastfeeding_status"
        resumedDailyActivities column: "resumed_daily_activities"
        attendedPostpartumCheckup column: "attended_postpartum_checkup"

        modules column: "modules"

        extensionForm column: "extension_form", sqlType: "mediumblob"

        collectedBy column: "collected_by", index: "idx_collected_by"
        collectedDeviceId column: "collected_device_id", index: "idx_cdeviceid"
        collectedHouseholdId column: "collected_household_id", index: "idx_chouseid"
        collectedMemberId column: "collected_member_id", index: "idx_cmemberid"
        collectedStart column: "collected_start"
        collectedEnd column: "collected_end"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}
