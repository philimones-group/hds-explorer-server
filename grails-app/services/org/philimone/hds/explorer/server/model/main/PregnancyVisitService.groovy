package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.enums.*
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDate

@Transactional
class PregnancyVisitService {

    def householdService
    def memberService
    def userService
    def residencyService
    def headRelationshipService
    def deathService
    def visitService
    def pregnancyRegistrationService
    def coreExtensionService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="Pregnancy Utilities Methods">
    boolean pregnancyOutcomeExists(String code) {
        PregnancyVisit.countByCode(code) > 0
    }

    PregnancyVisit getLastPregnancyVisit(String motherCode) {
        if (memberService.exists(motherCode)){
            def pregnancies = PregnancyVisit.executeQuery("select p from PregnancyVisit p where p.mother.code=?0 order by p.outcomeDate desc", [motherCode], [offset:0, max:1]) // limit 1

            if (pregnancies != null && pregnancies.size()>0){
                return pregnancies.first()
            }
        }

        return null
    }

    boolean isVisitNumberDuplicated(String pregnancyCode, int visitNumber) {
        return PregnancyVisit.countByCodeAndVisitNumber(pregnancyCode, visitNumber) > 0
    }

    Integer getLastVisitNumber(String pregnancyCode) {
        def results = PregnancyVisit.executeQuery("select max(visitNumber) from PregnancyVisit where code=?0", [pregnancyCode])
        def vnumber = results!=null && !results.isEmpty() ? results.first() : null
        return vnumber as Integer
    }

    //</editor-fold>

    //<editor-fold desc="Pregnancy Factory/Manager Methods">
    RawExecutionResult<PregnancyVisit> createPregnancyVisit(RawPregnancyVisit rawPregnancyVisit, List<RawPregnancyVisitChild> rawPregnancyVisitChildren) {

        /* Run Checks and Validations */

        def errors = validate(rawPregnancyVisit, rawPregnancyVisitChildren)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<PregnancyVisit> obj = RawExecutionResult.newErrorResult(RawEntity.PREGNANCY_VISIT, errors)
            return obj
        }

        def pregnancyVisit = newPregnancyVisit(rawPregnancyVisit)
        def createdPregnancyVisit = pregnancyVisit.save(flush:true)

        //Validate using Gorm Validations
        if (pregnancyVisit.hasErrors()){
            errors = errorMessageService.getRawMessages(RawEntity.PREGNANCY_VISIT, pregnancyVisit)

            RawExecutionResult<PregnancyVisit> obj = RawExecutionResult.newErrorResult(RawEntity.PREGNANCY_VISIT, errors)
            return obj
        }

        //PregnancyVisit is saved, now create Childs
        def createdPregnancyVisitChilds = new ArrayList<PregnancyVisitChild>()
        for (RawPregnancyVisitChild rawChild : rawPregnancyVisitChildren) {
            //create PregnancyChild
            def pregnancyChild = newPregnancyChildInstance(rawChild, createdPregnancyVisit)
            def resultChild = pregnancyChild.save(flush:true)

            //if there is errors while trying to create PC getout of the loop
            if (pregnancyChild.hasErrors()){
                errors += errorMessageService.getRawMessages(RawEntity.PREGNANCY_VISIT, pregnancyChild)
                break
            }

            createdPregnancyVisitChilds.add(resultChild)
        }

        //If there is an error while trying to create childs - delete all created records
        if (!errors.empty) {
            //Delete all created
            try {
                createdPregnancyVisitChilds.each {
                    it.delete(flush: true)
                }
                createdPregnancyVisit.delete(flush: true)

            } catch (Exception ex) {
                println "we got you mr.error"
                ex.printStackTrace()
            }

            RawExecutionResult<PregnancyVisit> obj = RawExecutionResult.newErrorResult(RawEntity.PREGNANCY_VISIT, errors)
            return obj
        }

        afterCreatingPregnancyVisit(createdPregnancyVisit)

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertPregnancyVisitExtension(rawPregnancyVisit, createdPregnancyVisit)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail
            println "Failed to insert extension: ${resultExtension.errorMessage}"
        }

        RawExecutionResult<PregnancyVisit> obj = RawExecutionResult.newSuccessResult(RawEntity.PREGNANCY_VISIT, pregnancyVisit)
        return obj
    }

    def afterCreatingPregnancyVisit(PregnancyVisit pregnancyVisit) {

        def pregnancyRegistration = PregnancyRegistration.findByCode(pregnancyVisit.code)
        if (pregnancyRegistration != null) {
            if (pregnancyRegistration.status == PregnancyStatus.PREGNANT) {
                pregnancyRegistration.status = pregnancyVisit.status //will remain pregnant ou set as lost_track, delivered status should remain as is.
            }

            def isAntepartumVisitsComplete = isVisitScheduleCompleted(pregnancyRegistration.code, PregnancyVisitType.ANTEPARTUM, Codes.MAX_ANTEPARTUM_VISITS)
            def isPostpartumVisitsComplete = isVisitScheduleCompleted(pregnancyRegistration.code, PregnancyVisitType.POSTPARTUM, Codes.MAX_POSTPARTUM_VISITS)

            pregnancyRegistration.summary_followup_completed = (isAntepartumVisitsComplete && isPostpartumVisitsComplete) || pregnancyVisit.status == PregnancyStatus.LOST_TRACK
            pregnancyRegistration.summary_antepartum_count = PregnancyVisit.countByCodeAndVisitType(pregnancyVisit.code, PregnancyVisitType.ANTEPARTUM)
            pregnancyRegistration.summary_postpartum_count = PregnancyVisit.countByCodeAndVisitType(pregnancyVisit.code, PregnancyVisitType.POSTPARTUM)
            pregnancyRegistration.summary_last_visit_status = pregnancyVisit.status
            pregnancyRegistration.summary_last_visit_type = pregnancyVisit.visitType
            pregnancyRegistration.summary_last_visit_date = pregnancyVisit.visitDate
            if (PregnancyVisit.countByCode(pregnancyVisit.code)==1)
                pregnancyRegistration.summary_first_visit_date =  pregnancyVisit.visitDate
            pregnancyRegistration.summary_has_pregnancy_outcome = PregnancyOutcome.countByCode(pregnancyVisit.code) > 0;
            pregnancyRegistration.summary_nr_outcomes = PregnancyChild.countByOutcomeCode(pregnancyVisit.code)

            pregnancyRegistration.save(flush: true)
        }
    }

    boolean isVisitScheduleCompleted(String pregnancyCode, PregnancyVisitType visitType, int expectedCount) {
        return PregnancyVisit.countByCodeAndVisitType(pregnancyCode, visitType) >= expectedCount
    }

    ArrayList<RawMessage> validate(RawPregnancyVisit pregnancyVisit, List<RawPregnancyVisitChild> pregnancyChildren){
        def errors = new ArrayList<RawMessage>()

        //visitCode, code, motherCode, visitType, visitNumber, visitDate, status
        def isBlankVisitCode = StringUtil.isBlank(pregnancyVisit.visitCode)
        def isBlankCode = StringUtil.isBlank(pregnancyVisit.code)
        def isBlankMotherCode = StringUtil.isBlank(pregnancyVisit.motherCode)        
        def isBlankVisitDate = StringUtil.isBlankDate(pregnancyVisit.visitDate)
        def isBlankVisitTyoe = StringUtil.isBlank(pregnancyVisit.visitType)
        def isBlankVisitNumber = StringUtil.isBlankInteger(pregnancyVisit.visitNumber)
        def isBlankStatus = StringUtil.isBlank(pregnancyVisit.status)

        def isBlankCollectedBy = StringUtil.isBlank(pregnancyVisit.collectedBy)

        def visit = visitService.getVisit(pregnancyVisit.visitCode)
        def pregnancy = pregnancyRegistrationService.getPregnancyRegistration(pregnancyVisit.code)
        def mother = memberService.getMember(pregnancyVisit.motherCode)        
        def household = visit != null ? householdService.getHousehold(visit?.householdCode) : null
        def status = !isBlankStatus ? PregnancyStatus.getFrom(pregnancyVisit.status) : null

        def visitExists = visit != null
        def motherExists = mother != null
        def pregnancyExists = pregnancy != null
        

        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (code)
        if (isBlankCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.blank", ["code"], ["code"])
        }
        //C1. Check Blank Fields (motherCode)
        if (isBlankMotherCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.blank", ["motherCode"], ["motherCode"])
        }
        //C1. Check Blank Fields (outcomeDate)
        if (isBlankVisitDate){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.blank", ["visitDate"], ["visitDate"])
        }
        //C1. Check Blank Fields (visitNumber)
        if (isBlankVisitNumber){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.blank", ["visitNumber"], ["visitNumber"])
        }
        //C1. Check Blank Fields (status)
        if (isBlankStatus){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.blank", ["status"], ["status"])
        }

        //C1.1 Check Code Regex Pattern
        if (!isBlankCode && !codeGeneratorService.isPregnancyCodeValid(pregnancyVisit.code)) {
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.pattern.no.matches", ["code", codeGeneratorService.pregnancySampleCode], ["code"])
        }

        //C2. Check Pregnancy reference existence
        if (!pregnancyExists){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.reference.error", ["PregnancyRegistration", "code", pregnancyVisit.code], ["code"])
        }
        //C2. Check Visit reference existence
        if (!visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.reference.error", ["Visit", "visitCode", pregnancyVisit.visitCode], ["visitCode"])
        }
        //C2. Check Mother reference existence
        if (!isBlankMotherCode && !motherExists){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.reference.error", ["Member", "motherCode", pregnancyVisit.motherCode], ["motherCode"])
        }

        //C3. Check Date is greater than today (outcomeDate)
        if (!isBlankVisitDate && pregnancyVisit.visitDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.date.not.greater.today", ["visitDate", StringUtil.format(pregnancyVisit.visitDate)], ["visitDate"])
        }

        //C4. Check Dates is older than Member Date of Birth (outcomeDate)
        if (!isBlankVisitDate && motherExists && pregnancyVisit.visitDate <= mother.dob){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.dob.not.greater.date", ["visitDate", StringUtil.format(mother.dob)], ["dob"])
        }

        //C5. Validate Enum Options (birthPlace)
        /*if (!isBlankBirthPlace && BirthPlace.getFrom(pregnancyVisit.birthPlace)==null){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.enum.choices.error", [pregnancyVisit.birthPlace, "birthPlace"], ["birthPlace"])
        }*/

        //C8. Check Number of Visits greater than zero
        if (!isBlankVisitNumber && pregnancyVisit.visitNumber < 1){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.pregnancy.visit.number.positive.error", [], ["numberOfOutcomes"])
        }

        //C8. Check Number of Visits duplication
        if (!isBlankVisitNumber && isVisitNumberDuplicated(pregnancyVisit.code, pregnancyVisit.visitNumber)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.pregnancy.visit.duplicated.error", [], ["numberOfOutcomes"])
        }

        //C2. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(pregnancyVisit.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.user.dont.exists.error", [pregnancyVisit.collectedBy], ["collectedBy"])
        }

        //C10. Check The Children
        if (errors.empty){

            def visitNumber = getLastVisitNumber(pregnancyVisit.code)
            def nextVisitNumber = visitNumber==null ? 1 : visitNumber+1

            if (pregnancyVisit.visitNumber != nextVisitNumber) {
                errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.pregnancy.visit.number.invalid.error", [pregnancyVisit.visitNumber+"", nextVisitNumber+""], ["visitNumber", "visitNumber"])
                return errors
            }

            //Check existence of outcomes
            if (status == PregnancyStatus.DELIVERED && (pregnancyChildren == null || pregnancyChildren.empty)){
                errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT, "validation.field.pregnancy.visit.postpartum.no_outcomes.raw.error", [pregnancyVisit.code], ["code", "status"])
                return errors
            }

            pregnancyChildren.each { rawChild ->
                errors += validateChild(rawChild)
            }
        }


        return errors
    }

    ArrayList<RawMessage> validateChild(RawPregnancyVisitChild pregnancyChild) {

        def errors = new ArrayList<RawMessage>()

        def isBlankOutcomeType = StringUtil.isBlank(pregnancyChild.outcomeType)
        def isBlankChildCode = StringUtil.isBlank(pregnancyChild.childCode)
        def isBlankChildStatus = StringUtil.isBlank(pregnancyChild.childStatus)

        def outcomeType = PregnancyOutcomeType.getFrom(pregnancyChild.outcomeType)
        def childStatus = NewBornStatus.getFrom(pregnancyChild.childStatus)

        //1. Validate OutcomeType enum string
        if (!isBlankOutcomeType && outcomeType==null){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT_CHILD, "validation.field.enum.choices.error", [pregnancyChild.outcomeType, "outcomeType"], ["outcomeType"])
            return errors
        }

        //3. Check blank (code,name,gender,relation to head)
        if (isBlankChildCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT_CHILD, "validation.field.blank", ["childCode"], ["childCode"])
        }
        if (isBlankChildStatus){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT_CHILD, "validation.field.blank", ["childStatus"], ["childStatus"])
        }

        //2. Check child code existence
        if (!isBlankChildCode && !memberService.exists(pregnancyChild.childCode)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT_CHILD, "validation.field.reference.error", ["PregnancyChild", "childCode", pregnancyChild.childCode], ["childCode"])
        }
        //4. Check childStatus
        if (!isBlankChildStatus && childStatus==null){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_VISIT_CHILD, "validation.field.enum.choices.error", [pregnancyChild.childStatus, "childStatus"], ["childStatus"])
        }

        return errors
    }

    private PregnancyVisit newPregnancyVisit(RawPregnancyVisit obj){

        def mother = memberService.getMember(obj.motherCode)
        def visit = visitService.getVisit(obj.visitCode)


        PregnancyVisit pregnancyVisit = new PregnancyVisit()

        pregnancyVisit.visit = visit
        pregnancyVisit.visitCode = obj.visitCode
        pregnancyVisit.code = obj.code
        pregnancyVisit.mother = mother
        pregnancyVisit.motherCode = obj.motherCode
        pregnancyVisit.status = PregnancyStatus.getFrom(obj.status)
        pregnancyVisit.visitNumber = obj.visitNumber
        pregnancyVisit.visitType = PregnancyVisitType.getFrom(obj.visitType)
        pregnancyVisit.visitDate = obj.visitDate

        pregnancyVisit.weeksGestation = obj.weeksGestation
        pregnancyVisit.prenatalCareReceived = obj.prenatalCareReceived
        pregnancyVisit.prenatalCareProvider = obj.prenatalCareProvider != null ? HealthcareProviderType.getFrom(obj.prenatalCareProvider) : null
        pregnancyVisit.complicationsReported = obj.complicationsReported
        pregnancyVisit.complicationDetails = obj.complicationDetails
        pregnancyVisit.hasBirthPlan = obj.hasBirthPlan
        pregnancyVisit.expectedBirthPlace = obj.expectedBirthPlace != null ? BirthPlace.getFrom(obj.expectedBirthPlace) : null
        pregnancyVisit.birthPlaceOther = obj.birthPlaceOther
        pregnancyVisit.transportationPlan = obj.transportationPlan
        pregnancyVisit.financialPreparedness = obj.financialPreparedness

        pregnancyVisit.postpartumComplications = obj.postpartumComplications
        pregnancyVisit.postpartumComplicationDetails = obj.postpartumComplicationDetails
        pregnancyVisit.breastfeedingStatus = obj.breastfeedingStatus != null ? BreastFeedingStatus.getFrom(obj.breastfeedingStatus) : null
        pregnancyVisit.resumedDailyActivities = obj.resumedDailyActivities
        pregnancyVisit.attendedPostpartumCheckup = obj.attendedPostpartumCheckup

        //set collected by info
        pregnancyVisit.collectedId = obj.id
        pregnancyVisit.collectedBy = userService.getUser(obj.collectedBy)
        pregnancyVisit.collectedDeviceId = obj.collectedDeviceId
        pregnancyVisit.collectedHouseholdId = obj.collectedHouseholdId
        pregnancyVisit.collectedMemberId = obj.collectedMemberId
        pregnancyVisit.collectedStart = obj.collectedStart
        pregnancyVisit.collectedEnd = obj.collectedEnd
        pregnancyVisit.collectedDate = obj.collectedDate
        pregnancyVisit.updatedDate = obj.uploadedDate

        return pregnancyVisit

    }

    private PregnancyVisitChild newPregnancyChildInstance(RawPregnancyVisitChild pc, PregnancyVisit pregnancyVisit){
        def child = memberService.getMember(pc.childCode)

        PregnancyVisitChild pregnancyChild = new PregnancyVisitChild()

        pregnancyChild.pregnancyVisit = pregnancyVisit
        pregnancyChild.pregnancyCode = pregnancyVisit.code
        pregnancyChild.outcomeType = PregnancyOutcomeType.getFrom(pc.outcomeType)
        pregnancyChild.child = child
        pregnancyChild.childCode = pc.childCode
        pregnancyChild.childStatus = pc.childStatus != null ? NewBornStatus.getFrom(pc.childStatus) : null
        pregnancyChild.childWeight = pc.childWeight
        pregnancyChild.hadIllnessSymptoms = pc.hadIllnessSymptoms
        if (pc.childIllnessSymptoms != null) {
            pc.childIllnessSymptoms.split(",").each { smp ->
                def symptom = IllnessSymptoms.getFrom(smp)
                if (symptom != null)
                    pregnancyChild.addToChildIllnessSymptoms(symptom)
            }
        }


        pregnancyChild.childBreastfeedingStatus = pc.childBreastfeedingStatus != null ? BreastFeedingStatus.getFrom(pc.childBreastfeedingStatus) : null
        pregnancyChild.childImmunizationStatus = pc.childImmunizationStatus != null ? ImmunizationStatus.getFrom(pc.childImmunizationStatus) : null
        pregnancyChild.notes = pc.notes

        return pregnancyChild
    }

    //</editor-fold>

    class ChildPack {
        Member member
        Residency residency
        HeadRelationship headRelationship
        Death death
        PregnancyChild pregnancyChild
    }
}
