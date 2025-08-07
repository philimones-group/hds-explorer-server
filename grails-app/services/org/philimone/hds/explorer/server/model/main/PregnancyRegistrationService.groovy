package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.DateUtil
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyOutcome
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyRegistration
import org.philimone.hds.explorer.server.model.enums.EstimatedDateOfDeliveryType
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.PregnancyStatus
import org.philimone.hds.explorer.server.model.enums.PregnancyVisitType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDate

@Transactional
class PregnancyRegistrationService {

    def householdService
    def memberService
    def userService
    def residencyService
    def headRelationshipService
    def deathService
    def visitService
    def coreExtensionService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="Pregnancy Utilities Methods">
    boolean pregnancyRegistrationExists(String code) {
        PregnancyRegistration.countByCode(code) > 0
    }

    PregnancyRegistration getPregnancyRegistration(String code) {
        return PregnancyRegistration.findByCode(code)
    }

    PregnancyRegistration getLastPregnancyRegistration(String motherCode) {
        if (memberService.exists(motherCode)){
            def pregnancies = PregnancyRegistration.executeQuery("select p from PregnancyRegistration p where p.mother.code=?0 order by p.recordedDate desc", [motherCode], [offset:0, max:1]) // limit 1

            if (pregnancies != null && pregnancies.size()>0){
                return pregnancies.first()
            }
        }

        return null
    }

    String generateCode(Member mother){
        return codeGeneratorService.generatePregnancyCode(mother)
    }

    //</editor-fold>

    //<editor-fold desc="Pregnancy Factory/Manager Methods">
    RawExecutionResult<PregnancyRegistration> createPregnancyRegistration(RawPregnancyRegistration rawPregnancyRegistration) {

        /* Run Checks and Validations */

        def errors = validate(rawPregnancyRegistration)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<PregnancyRegistration> obj = RawExecutionResult.newErrorResult(RawEntity.PREGNANCY_REGISTRATION, errors)
            return obj
        }

        def pregnancyRegistration = newPregnancyRegistrationInstance(rawPregnancyRegistration)

        pregnancyRegistration = pregnancyRegistration.save(flush:true)

        //Validate using Gorm Validations
        if (pregnancyRegistration.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.PREGNANCY_REGISTRATION, pregnancyRegistration)

            RawExecutionResult<PregnancyRegistration> obj = RawExecutionResult.newErrorResult(RawEntity.PREGNANCY_REGISTRATION, errors)
            return obj
        }

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertPregnancyRegistrationExtension(rawPregnancyRegistration, pregnancyRegistration)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail
            println "Failed to insert extension: ${resultExtension.errorMessage}"
        }

        RawExecutionResult<PregnancyRegistration> obj = RawExecutionResult.newSuccessResult(RawEntity.PREGNANCY_REGISTRATION, pregnancyRegistration)
        return obj
    }

    ArrayList<RawMessage> validate(RawPregnancyRegistration pregnancyRegistration){
        def dateUtil = DateUtil.getInstance()

        def errors = new ArrayList<RawMessage>()

        //code, motherCode, pregMonths, expectedDeliveryDate, status, visitCode
        def isBlankCode = StringUtil.isBlank(pregnancyRegistration.code)
        def isBlankMotherCode = StringUtil.isBlank(pregnancyRegistration.motherCode)
        def isBlankRecordedDate = StringUtil.isBlankDate(pregnancyRegistration.recordedDate)
        def isBlankPregMonths = StringUtil.isBlankInteger(pregnancyRegistration.pregMonths)
        def isBlankEddKnown = StringUtil.isBlankBoolean(pregnancyRegistration.eddKnown)
        def isBlankHasPrenatalRecord = StringUtil.isBlankBoolean(pregnancyRegistration.hasPrenatalRecord)

        def isBlankEddDate = StringUtil.isBlankDate(pregnancyRegistration.eddDate)
        def isBlankEddType = StringUtil.isBlank(pregnancyRegistration.eddType)
        def isBlankLmpKnown = StringUtil.isBlankBoolean(pregnancyRegistration.lmpKnown)

        def isBlankLmpDate = StringUtil.isBlankDate(pregnancyRegistration.lmpDate)
        def isBlankExpectedDeliveryDate = StringUtil.isBlankDate(pregnancyRegistration.expectedDeliveryDate)
        def isBlankStatus = StringUtil.isBlank(pregnancyRegistration.status)
        def isBlankVisitCode = StringUtil.isBlank(pregnancyRegistration.visitCode)

        def isBlankCollectedBy = StringUtil.isBlank(pregnancyRegistration.collectedBy)

        def status = PregnancyStatus.getFrom(pregnancyRegistration.status)

        def isPregnant = status!=null && status==PregnancyStatus.PREGNANT;

        def mother = memberService.getMember(pregnancyRegistration.motherCode)
        def visit = visitService.getVisit(pregnancyRegistration.visitCode)

        def motherExists = mother != null
        def visitExists = visit != null

        //fields we want be checking for now
        def ignoreBlankEddKnown = true
        def ignoreBlankHasPrenatalRecord = true
        def ignoreBlankEddDate = true
        def ignoreBlankEddType = true
        def ignoreBlankLmpKnown = true
        def ignoreBlankLmpDate = true

        //C1. Check Nullable Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (code)
        if (isBlankCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["code"], ["code"])
        }
        //C1.1 Check Code Regex Pattern
        if (!isBlankCode && !codeGeneratorService.isPregnancyCodeValid(pregnancyRegistration.code)) {
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.pattern.no.matches", ["code", codeGeneratorService.pregnancySampleCode], ["code"])
        }
        //C1. Check Blank Fields (motherCode)
        if (isBlankMotherCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["motherCode"], ["motherCode"])
        }
        //C1. Check Nullable Fields (status)
        if (isBlankStatus){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["status"], ["status"])
        }
        //C1. Check Blank Fields (recordedDate)
        if (isBlankRecordedDate){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["recordedDate"], ["recordedDate"])
        }
        //C1. Check Blank Fields (eddKnown)
        if (isPregnant && isBlankEddKnown && !ignoreBlankEddKnown){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["eddKnown"], ["eddKnown"])
        }
        //C1. Check Blank Fields (hasPrenatalRecord)
        if (isPregnant && isBlankHasPrenatalRecord && !ignoreBlankHasPrenatalRecord){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["hasPrenatalRecord"], ["hasPrenatalRecord"])
        }
        //C1. Check Blank Fields (eddDate)
        if (isPregnant && isBlankEddDate && !ignoreBlankEddDate){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["eddDate"], ["eddDate"])
        }
        //C1. Check Blank Fields (eddType)
        if (isPregnant && isBlankEddType && !ignoreBlankEddType){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["eddType"], ["eddType"])
        }
        //C1. Check Blank Fields (pregMonths)
        if (isPregnant && isBlankPregMonths){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["pregMonths"], ["pregMonths"])
        }
        //C1. Check Nullable Fields (lmpKnown)
        if (isPregnant && isBlankLmpKnown && !ignoreBlankLmpKnown){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["lmpKnown"], ["lmpKnown"])
        }
        //C1. Check Nullable Fields (lmpDate)
        if (isPregnant && isBlankLmpDate && !ignoreBlankLmpDate){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["lmpDate"], ["lmpDate"])
        }
        //C1. Check Nullable Fields (expectedDeliveryDate)
        if (isPregnant && isBlankExpectedDeliveryDate){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.blank", ["expectedDeliveryDate"], ["expectedDeliveryDate"])
        }

        //C2. Check Mother reference existence
        if (!isBlankMotherCode && !motherExists){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.reference.error", ["Member", "motherCode", pregnancyRegistration.motherCode], ["motherCode"])
        }

        //C2. Check Visit reference existence
        if (!visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.reference.error", ["Visit", "visitCode", pregnancyRegistration.visitCode], ["visitCode"])
        }

        //C3. Check Date is greater than today (recordedDate)
        if (!isBlankRecordedDate && pregnancyRegistration.recordedDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.date.not.greater.today", ["recordedDate", dateUtil.formatYMD(pregnancyRegistration.recordedDate)], ["recordedDate"])
        }
        //C3. Check Date is greater than today (lmpDate)
        if (isPregnant && !isBlankLmpDate && !ignoreBlankLmpDate && pregnancyRegistration.lmpDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.date.not.greater.today", ["lmpDate", dateUtil.formatYMD(pregnancyRegistration.lmpDate)], ["lmpDate"])
        }

        //C4. Check Dates is older than Member Date of Birth (recordedDate)
        if (!isBlankRecordedDate && motherExists && pregnancyRegistration.recordedDate < mother.dob){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.dob.not.greater.date", ["recordedDate", dateUtil.formatYMD(mother.dob)], ["dob"])
        }
        //C4. Check Dates is older than Member Date of Birth (eddDate)
        if (isPregnant && !isBlankEddDate && !ignoreBlankEddDate && motherExists && pregnancyRegistration.eddDate < mother.dob){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.dob.not.greater.date", ["eddDate", dateUtil.formatYMD(mother.dob)], ["dob"])
        }
        //C4. Check Dates is older than Member Date of Birth (lmpDate)
        if (isPregnant && !isBlankLmpDate && !ignoreBlankLmpDate && motherExists && pregnancyRegistration.lmpDate < mother.dob){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.dob.not.greater.date", ["lmpDate", dateUtil.formatYMD(mother.dob)], ["dob"])
        }

        //C5. Validate Enum Options (edd_type)
        if (isPregnant && !isBlankEddType && !ignoreBlankEddType && EstimatedDateOfDeliveryType.getFrom(pregnancyRegistration.eddType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.enum.choices.error", [pregnancyRegistration.eddType, "eddType"], ["eddType"])
        }
        //C5. Validate Enum Options (pregnancyStatus)
        if (!isBlankStatus && PregnancyStatus.getFrom(pregnancyRegistration.status)==null){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.enum.choices.error", [pregnancyRegistration.status, "status"], ["status"])
        }

        //C6. Check Mother Death Status
        if (motherExists && deathService.isMemberDead(pregnancyRegistration.motherCode)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.pregnancy.registration.death.exists.error", [pregnancyRegistration.motherCode], ["motherCode"])
        }

        //C7. Check mother Gender
        if (Codes.GENDER_CHECKING && motherExists && mother.gender==Gender.MALE){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.gender.mother.error", [], ["mother.gender"])
        }

        //C8. Check mother Dob must be greater or equal to 12
        if (motherExists && GeneralUtil.getAge(mother.dob) < Codes.MIN_MOTHER_AGE_VALUE ){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.pregnancy.registration.age.error", [dateUtil.formatYMD(mother.dob), Codes.MIN_MOTHER_AGE_VALUE+""], ["mother.dob"])
        }

        //C5. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(pregnancyRegistration.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.user.dont.exists.error", [pregnancyRegistration.collectedBy], ["collectedBy"])
        }

        //C9. Check Duplicate of Pregnancy Registration
        if (!isBlankCode && pregnancyRegistrationExists(pregnancyRegistration.code)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.reference.duplicate.error", ["PregnancyRegistration", "code", pregnancyRegistration.code], ["code"])
        }

        //C9. Check If last previous is Opened
        if (errors.empty){
            def lastPregnancy = getLastPregnancyRegistration(pregnancyRegistration.motherCode)

            if (lastPregnancy != null && lastPregnancy.status==PregnancyStatus.PREGNANT) {
                errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_REGISTRATION, "validation.field.pregnancy.registration.status.opened.error", ["status"], ["status"])
            }

        }


        return errors
    }

    private PregnancyRegistration newPregnancyRegistrationInstance(RawPregnancyRegistration pr){

        def mother = memberService.getMember(pr.motherCode)
        def visit = visitService.getVisit(pr.visitCode)
        def status = PregnancyStatus.getFrom(pr.status)
        def eddType = EstimatedDateOfDeliveryType.getFrom(pr.eddType)

        PregnancyRegistration pregnancyRegistration = new PregnancyRegistration()

        pregnancyRegistration.code = pr.code
        pregnancyRegistration.mother = mother
        pregnancyRegistration.motherCode = pr.motherCode
        pregnancyRegistration.recordedDate = pr.recordedDate
        pregnancyRegistration.pregMonths = pr.pregMonths
        pregnancyRegistration.eddKnown = pr.eddKnown
        pregnancyRegistration.hasPrenatalRecord = pr.hasPrenatalRecord
        pregnancyRegistration.eddDate = pr.eddDate
        pregnancyRegistration.eddType = eddType

        pregnancyRegistration.lmpKnown = pr.lmpKnown
        pregnancyRegistration.lmpDate = pr.lmpDate
        pregnancyRegistration.expectedDeliveryDate = pr.expectedDeliveryDate

        pregnancyRegistration.status = status

        pregnancyRegistration.visit = visit
        pregnancyRegistration.visitCode = pr.visitCode

        pregnancyRegistration.summary_followup_completed = false
        pregnancyRegistration.summary_antepartum_count = 0
        pregnancyRegistration.summary_postpartum_count = 0
        pregnancyRegistration.summary_last_visit_status = null
        pregnancyRegistration.summary_last_visit_type = null
        pregnancyRegistration.summary_last_visit_date = null
        //pregnancyRegistration.summary_first_visit_date = null
        pregnancyRegistration.summary_has_pregnancy_outcome = false
        pregnancyRegistration.summary_nr_outcomes = 0

        //set collected by info
        pregnancyRegistration.collectedId = pr.id
        pregnancyRegistration.collectedBy = userService.getUser(pr.collectedBy)
        pregnancyRegistration.collectedDeviceId = pr.collectedDeviceId
        pregnancyRegistration.collectedHouseholdId = pr.collectedHouseholdId
        pregnancyRegistration.collectedMemberId = pr.collectedMemberId
        pregnancyRegistration.collectedStart = pr.collectedStart
        pregnancyRegistration.collectedEnd = pr.collectedEnd
        pregnancyRegistration.collectedDate = pr.collectedDate
        pregnancyRegistration.updatedDate = pr.uploadedDate

        return pregnancyRegistration

    }
    //</editor-fold>
}
