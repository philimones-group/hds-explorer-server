package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawDeath
import org.philimone.hds.explorer.server.model.collect.raw.RawHeadRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawMember
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyChild
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyOutcome
import org.philimone.hds.explorer.server.model.collect.raw.RawResidency
import org.philimone.hds.explorer.server.model.enums.BirthPlace
import org.philimone.hds.explorer.server.model.enums.EstimatedDateOfDeliveryType
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.HouseholdStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.PregnancyOutcomeType
import org.philimone.hds.explorer.server.model.enums.PregnancyStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDate

@Transactional
class PregnancyOutcomeService {

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
    boolean pregnancyOutcomeExists(String code) {
        PregnancyOutcome.countByCode(code) > 0
    }

    PregnancyOutcome getLastPregnancyOutcome(String motherCode) {
        if (memberService.exists(motherCode)){
            def pregnancies = PregnancyOutcome.executeQuery("select p from PregnancyOutcome p where p.mother.code=?0 order by p.outcomeDate desc", [motherCode], [offset:0, max:1]) // limit 1

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
    RawExecutionResult<PregnancyOutcome> createPregnancyOutcome(RawPregnancyOutcome rawPregnancyOutcome, List<RawPregnancyChild> rawPregnancyChilds) {

        /* Run Checks and Validations */

        def errors = validate(rawPregnancyOutcome, rawPregnancyChilds)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<PregnancyOutcome> obj = RawExecutionResult.newErrorResult(RawEntity.PREGNANCY_OUTCOME, errors)
            return obj
        }

        def pregnancyOutcome = newPregnancyOutcomeInstance(rawPregnancyOutcome)
        def visit = pregnancyOutcome.visit
        def motherResidency = householdService.getHousehold(visit.householdCode) //residencyService.getCurrentResidency(pregnancyOutcome.mother)
        def outcomeDate = pregnancyOutcome.outcomeDate
        def childPacks = new ArrayList<ChildPack>()
        def numberOfLivebirths = 0

        def result = pregnancyOutcome.save(flush:true)

        //Validate using Gorm Validations
        if (pregnancyOutcome.hasErrors()){
            errors = errorMessageService.getRawMessages(RawEntity.PREGNANCY_OUTCOME, pregnancyOutcome)

            RawExecutionResult<PregnancyOutcome> obj = RawExecutionResult.newErrorResult(RawEntity.PREGNANCY_OUTCOME, errors)
            return obj
        } else {
            pregnancyOutcome = result
        }


        //PregnancyOutcome is saved, now create Childs
        for (RawPregnancyChild rawChild : rawPregnancyChilds) {

            def outcomeType = PregnancyOutcomeType.getFrom(rawChild.outcomeType)

            ChildPack childPack = new ChildPack()
            childPacks.add(childPack)

            //only create main domains if is a livebirth
            if (outcomeType == PregnancyOutcomeType.LIVEBIRTH) {
                numberOfLivebirths++
            }

            //create raw domains from RawPregnancyChild
            def rawMember = createNewRawMemberFrom(rawChild, motherResidency, outcomeDate)
            def rawResidency = createNewRawResidencyFrom(rawChild, motherResidency, outcomeDate)
            def rawHeadRelationship = createNewRawHeadRelationshipFrom(rawChild, motherResidency, outcomeDate)
            def rawDeath = createRawDeath(rawPregnancyOutcome, rawChild)

            //create main domain from raw domains using specific services
            def resultMember =  memberService.createMember(rawMember)
            def resultResidency =  residencyService.createResidency(rawResidency)
            def resultHeadRelationship = headRelationshipService.createHeadRelationship(rawHeadRelationship)
            def resultDeath = null as RawExecutionResult<Death>

            if (outcomeType != PregnancyOutcomeType.LIVEBIRTH){
                resultDeath = deathService.createDeath(rawDeath)
            }


            //get the result domains (can be null values - if it didnt save)
            childPack.member = resultMember.domainInstance
            childPack.residency = resultResidency.domainInstance
            childPack.headRelationship = resultHeadRelationship.domainInstance
            childPack.death = resultDeath?.domainInstance

            //concatenate all errors
            def allErrors = new ArrayList<RawMessage>()
            if (resultMember?.status == RawExecutionResult.Status.ERROR) {
                allErrors += errorMessageService.addPrefixToMessages(resultMember.errorMessages, "validation.field.pregnancy.child.prefix.msg.error", [rawChild.id])
            }
            if (resultResidency?.status == RawExecutionResult.Status.ERROR) {
                allErrors += errorMessageService.addPrefixToMessages(resultResidency.errorMessages, "validation.field.pregnancy.child.prefix.msg.error", [rawChild.id])
            }
            if (resultHeadRelationship?.status == RawExecutionResult.Status.ERROR) {
                allErrors += errorMessageService.addPrefixToMessages(resultHeadRelationship.errorMessages, "validation.field.pregnancy.child.prefix.msg.error", [rawChild.id])
            }
            if (resultDeath?.status == RawExecutionResult.Status.ERROR)  {
                allErrors += errorMessageService.addPrefixToMessages(resultDeath.errorMessages, "validation.field.pregnancy.child.prefix.msg.error", [rawChild.id])
            } else if (resultDeath?.status == RawExecutionResult.Status.SUCCESS) {

                def death = resultDeath.domainInstance
                death.isPregOutcomeDeath = true
                death.save(flush:true)

            }


            //if there is an error get out of the loop
            if (!allErrors.empty) {
                errors += allErrors
                break
            }


            //the child/member was created/not now create PregnancyChild
            def pregnancyChild = newPregnancyChildInstance(rawChild, pregnancyOutcome, childPack.headRelationship)
            def resultChild = pregnancyChild.save(flush:true)

            //if there is errors while trying to create PC getout of the loop
            if (pregnancyChild.hasErrors()){
                errors += errorMessageService.getRawMessages(RawEntity.PREGNANCY_OUTCOME, pregnancyChild)
                break
            }

            childPack.pregnancyChild = resultChild //everything went fine - store the pregnancyChild in the pack!!!
        }


        //If there is an error while trying to create childs - delete all created records
        if (!errors.empty) {
            //Delete all created

            try {
                childPacks.each { childPack ->

                    childPack.pregnancyChild?.delete(flush: true)
                    childPack.headRelationship?.delete(flush: true)
                    childPack.residency?.delete(flush: true)
                    childPack.member?.delete(flush: true)
                    childPack.death?.delete(flush: true)

                }
                pregnancyOutcome?.delete(flush: true)

            } catch (Exception ex) {

                println "we got you mr.error"

                ex.printStackTrace()
            }

            RawExecutionResult<PregnancyOutcome> obj = RawExecutionResult.newErrorResult(RawEntity.PREGNANCY_OUTCOME, errors)
            return obj
        }

        pregnancyOutcome.numberOfLivebirths = numberOfLivebirths
        pregnancyOutcome.save()

        closePregnancyRegistration(pregnancyOutcome)

        if (numberOfLivebirths > 0) {
            def household = visit.household
            if (household != null) {
                householdService.updateHouseholdStatus(household, HouseholdStatus.HOUSE_OCCUPIED)
            }
        }

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertPregnancyOutcomeExtension(rawPregnancyOutcome, result)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail
            println "Failed to insert extension: ${resultExtension.errorMessage}"
        }

        RawExecutionResult<PregnancyOutcome> obj = RawExecutionResult.newSuccessResult(RawEntity.PREGNANCY_OUTCOME, pregnancyOutcome)
        return obj
    }

    def closePregnancyRegistration(PregnancyOutcome pregnancyOutcome) {

        def pregnancyRegistration = PregnancyRegistration.findByCode(pregnancyOutcome.code)

        if (pregnancyRegistration != null) {
            pregnancyRegistration.status = PregnancyStatus.DELIVERED
            pregnancyRegistration.save(flush: true)
        } else {
            println "no pregnancy registration to close"
        }
    }

    ArrayList<RawMessage> validate(RawPregnancyOutcome pregnancyOutcome, List<RawPregnancyChild> pregnancyChildren){
        def errors = new ArrayList<RawMessage>()

        //code, motherCode, fatherCode, numberOfOutcomes, outcomeDate, birthPlace, birthPlaceOther, visitCode
        def isBlankCode = StringUtil.isBlank(pregnancyOutcome.code)
        def isBlankMotherCode = StringUtil.isBlank(pregnancyOutcome.motherCode)
        def isBlankFatherCode = StringUtil.isBlank(pregnancyOutcome.fatherCode)
        def isBlankOutcomeDate = StringUtil.isBlankDate(pregnancyOutcome.outcomeDate)
        def isBlankNumberOfOutcomes = StringUtil.isBlankInteger(pregnancyOutcome.numberOfOutcomes)

        def isBlankBirthPlace = StringUtil.isBlank(pregnancyOutcome.birthPlace)
        def isBlankBirthPlaceOther = StringUtil.isBlank(pregnancyOutcome.birthPlaceOther)

        def isBlankVisitCode = StringUtil.isBlank(pregnancyOutcome.visitCode)

        def isBlankCollectedBy = StringUtil.isBlank(pregnancyOutcome.collectedBy)


        def mother = memberService.getMember(pregnancyOutcome.motherCode)
        def father = memberService.getMember(pregnancyOutcome.fatherCode)
        def visit = visitService.getVisit(pregnancyOutcome.visitCode)
        def household = visit != null ? householdService.getHousehold(visit?.householdCode) : null

        def motherExists = mother != null
        def fatherExists = father != null
        def visitExists = visit != null

        //fields we want be checking for now
        isBlankBirthPlace = false
        isBlankBirthPlaceOther = false

        //C1. Check Blank Fields (code)
        if (isBlankCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.blank", ["code"], ["code"])
        }
        //C1. Check Blank Fields (motherCode)
        if (isBlankMotherCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.blank", ["motherCode"], ["motherCode"])
        }
        //C1. Check Blank Fields (fatherCode)
        if (isBlankFatherCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.blank", ["fatherCode"], ["fatherCode"])
        }
        //C1. Check Blank Fields (outcomeDate)
        if (isBlankOutcomeDate){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.blank", ["outcomeDate"], ["outcomeDate"])
        }
        //C1. Check Blank Fields (numberOfOutcomes)
        if (isBlankNumberOfOutcomes){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.blank", ["numberOfOutcomes"], ["numberOfOutcomes"])
        }
        //C1. Check Blank Fields (birthPlace)
        if (isBlankBirthPlace){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.blank", ["birthPlace"], ["birthPlace"])
        }
        //C1. Check Blank Fields (birthPlaceOther)
        if (isBlankBirthPlaceOther){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.blank", ["birthPlaceOther"], ["birthPlaceOther"])
        }
        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.blank", ["visitCode"], ["visitCode"])
        }

        //C1.1 Check Code Regex Pattern
        if (!isBlankCode && !codeGeneratorService.isPregnancyCodeValid(pregnancyOutcome.code)) {
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.pattern.no.matches", ["code", codeGeneratorService.pregnancySampleCode], ["code"])
        }

        //C2. Check Mother reference existence
        if (!isBlankMotherCode && !motherExists){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.reference.error", ["Member", "motherCode", pregnancyOutcome.motherCode], ["motherCode"])
        }
        //C2. Check Father reference existence
        if (!isBlankFatherCode && !fatherExists){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.reference.error", ["Member", "fatherCode", pregnancyOutcome.fatherCode], ["fatherCode"])
        }
        //C2. Check Visit reference existence
        if (!visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.reference.error", ["Visit", "visitCode", pregnancyOutcome.visitCode], ["visitCode"])
        }

        //C3. Check Date is greater than today (outcomeDate)
        if (!isBlankOutcomeDate && pregnancyOutcome.outcomeDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.date.not.greater.today", ["outcomeDate", StringUtil.format(pregnancyOutcome.outcomeDate)], ["outcomeDate"])
        }

        //C4. Check Dates is older than Member Date of Birth (outcomeDate)
        if (!isBlankOutcomeDate && motherExists && pregnancyOutcome.outcomeDate <= mother.dob){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.dob.not.greater.date", ["outcomeDate", StringUtil.format(mother.dob)], ["dob"])
        }

        //C5. Validate Enum Options (birthPlace)
        if (!isBlankBirthPlace && BirthPlace.getFrom(pregnancyOutcome.birthPlace)==null){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.enum.choices.error", [pregnancyOutcome.birthPlace, "birthPlace"], ["birthPlace"])
        }

        //C6. Check Mother Death Status
        if (motherExists && deathService.isMemberDead(pregnancyOutcome.motherCode)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.pregnancy.outcome.death.exists.error", [pregnancyOutcome.motherCode], ["motherCode"])
        }

        //CX. Check mother Gender
        if (Codes.GENDER_CHECKING && motherExists && mother.gender== Gender.MALE){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.gender.mother.error", [], ["mother.gender"])
        }

        //C7. Check mother Dob must be greater or equal to 12
        if (motherExists && GeneralUtil.getAge(mother.dob) < Codes.MIN_MOTHER_AGE_VALUE ){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.pregnancy.outcome.age.error", [StringUtil.format(mother.dob), Codes.MIN_MOTHER_AGE_VALUE+""], ["mother.dob"])
        }

        //C8. Check Number of Outcomes
        if (!isBlankNumberOfOutcomes && pregnancyOutcome.numberOfOutcomes < 1){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.pregnancy.outcome.nr_outcomes.error", [], ["numberOfOutcomes"])
        }

        //C2. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(pregnancyOutcome.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.user.dont.exists.error", [pregnancyOutcome.collectedBy], ["collectedBy"])
        }

        //C9. Check Duplicate of Pregnancy Registration
        if (!isBlankCode && pregnancyOutcomeExists(pregnancyOutcome.code)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.reference.duplicate.error", ["PregnancyOutcome", "code", pregnancyOutcome.code], ["code"])
        }

        //C9. Check If Mother has a Residency record
        if (motherExists) {
            def residency = residencyService.getCurrentResidency(mother)

            if (residency == null){
                errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.pregnancy.outcome.residency.not.found.error", [pregnancyOutcome.motherCode], ["motherCode"])
            } else if (residency.endType != ResidencyEndType.NOT_APPLICABLE) {
                //Not living in the current house
                errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.pregnancy.outcome.residency.closed.error", [residency.household.code, residency.endType.code], ["motherCode"])
            }
        }

        //C10. Check The Children
        if (errors.empty){

            //Check existence of outcomes
            if (pregnancyChildren == null || pregnancyChildren.empty){
                errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.pregnancy.outcome.nr_outcomes.raw.error", [pregnancyOutcome.code], ["code", "numberOfOutcomes"])
                return errors
            }

            //this is not duplicated by memberCode - check if this pregOutcome childs are entering to the correct household by validating collectedHouseholdId
            if (household.collectedId != null && !household.collectedId.equalsIgnoreCase(pregnancyOutcome.collectedHouseholdId)) {
                //duplicate error
                errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_OUTCOME, "validation.field.pregnancy.outcome.household.code.duplicated.error", [household.code, household.collectedId, pregnancyOutcome.collectedHouseholdId], ["memberCode", "householdCode", "collectedHouseholdId"])
                return errors
            }

            pregnancyChildren.each { rawChild ->
                errors += validate(rawChild)
            }

        }


        return errors
    }

    ArrayList<RawMessage> validate(RawPregnancyChild pregnancyChild) {

        def errors = new ArrayList<RawMessage>()

        def isBlankOutcomeType = StringUtil.isBlank(pregnancyChild.outcomeType)
        def isBlankChildCode = StringUtil.isBlank(pregnancyChild.childCode)
        def isBlankChildName = StringUtil.isBlank(pregnancyChild.childName)
        def isBlankChildGender = StringUtil.isBlank(pregnancyChild.childGender)
        def isBlankChildOrdinal = StringUtil.isBlankInteger(pregnancyChild.childOrdinalPosition)
        def isBlankHeadRelatType = StringUtil.isBlank(pregnancyChild.headRelationshipType)

        def outcomeType = PregnancyOutcomeType.getFrom(pregnancyChild.outcomeType)
        def headRelationshipType = HeadRelationshipType.getFrom(pregnancyChild.headRelationshipType)
        def isLivebirth = outcomeType == PregnancyOutcomeType.LIVEBIRTH
        def outcomeDate = pregnancyChild?.outcome?.outcomeDate
        def motherCode = pregnancyChild?.outcome?.motherCode

        //1. Validate OutcomeType enum string
        if (!isBlankOutcomeType && outcomeType==null){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.enum.choices.error", [pregnancyChild.outcomeType, "outcomeType"], ["outcomeType"])
            return errors
        }

        //3. Check blank (code,name,gender,relation to head)
        if (isBlankChildCode){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.blank", ["childCode"], ["childCode"])
        }
        if (isBlankChildName){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.blank", ["childName"], ["childName"])
        }
        if (isBlankChildGender){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.blank", ["childGender"], ["childGender"])
        }
        if (isBlankChildOrdinal){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.blank", ["childOrdinalPosition"], ["childOrdinalPosition"])
        }
        if (isBlankHeadRelatType){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.blank", ["child.headRelationshipType"], ["headRelationshipType"])
        }

        //2. Check child code existence (If is Livebirth)
        if (!isBlankChildCode && memberService.exists(pregnancyChild.childCode)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.reference.duplicate.error", ["PregnancyChild", "childCode", pregnancyChild.childCode], ["childCode"])
        }
        //4. Check Gender enum string
        if (!isBlankChildGender && Gender.getFrom(pregnancyChild.childGender)==null){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.enum.choices.error", [pregnancyChild.childGender, "childGender"], ["childGender"])
        }
        //5. Check HeadRelationshipType enum string
        if (!isBlankHeadRelatType && headRelationshipType==null){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.enum.choices.error", [pregnancyChild.headRelationshipType, "child.headRelationshipType"], ["child.headRelationshipType"])
        }

        //get mother residency
        def visit = visitService.getVisit(pregnancyChild?.outcome.visitCode)
        def motherResidency = householdService.getHousehold(visit.householdCode) //residencyService.getCurrentResidency(memberService.getMember(motherCode))

        //Check If childCode belongs to the mother current household
        if (!isBlankChildCode && motherResidency != null && !pregnancyChild.childCode.startsWith(motherResidency?.code)){
            errors << errorMessageService.getRawMessage(RawEntity.PREGNANCY_CHILD, "validation.field.pregnancy.child.code.invalid.mother.error", [pregnancyChild.childCode, motherResidency?.code], ["childCode"])
        }

        if (!errors.empty) {
            return errors
        }

        //6. Validate Child through MemberService
        def rawMember = createNewRawMemberFrom(pregnancyChild, motherResidency, outcomeDate)
        def innerErrors1 = memberService.validate(rawMember)
        errors += errorMessageService.addPrefixToMessages(innerErrors1, "validation.field.pregnancy.child.prefix.msg.error", [pregnancyChild.id])

        //7. Try to create Residency of Childs - Cant Validate Residency - Member doesnt exists
        //def rawResidency = createNewRawResidencyFrom(pregnancyChild, motherResidency, outcomeDate)
        //def innerErrors2 = residencyService.validateCreateResidency(rawResidency)
        //errors += errorMessageService.addPrefixToMessages(innerErrors2, "validation.field.pregnancy.child.prefix.msg.error", [pregnancyChild.id])

        //8. Try to create HeadRelationships of Childs  - Cant Validate HeadRelationship - Member doesnt exists
        //def rawHeadRelationship = createNewRawHeadRelationshipFrom(pregnancyChild, motherResidency, outcomeDate)
        //def innerErrors3 = headRelationshipService.validateCreateHeadRelationship(rawHeadRelationship)
        //errors += errorMessageService.addPrefixToMessages(innerErrors3, "validation.field.pregnancy.child.prefix.msg.error", [pregnancyChild.id])

        return errors
    }

    private RawMember createNewRawMemberFrom(RawPregnancyChild pregnancyChild, Household motherResidency, LocalDate outcomeDate){

        def pregnancyOutcome = pregnancyChild.outcome
        def fatherCode = pregnancyOutcome.fatherCode
        def motherCode = pregnancyOutcome.motherCode

        return new RawMember(
                code: pregnancyChild.childCode,
                name: pregnancyChild.childName,
                gender: pregnancyChild.childGender,
                dob: outcomeDate,
                motherCode: motherCode,
                fatherCode: fatherCode,
                householdCode: motherResidency.code,
                modules: pregnancyOutcome.modules,
                collectedId: pregnancyChild.childCollectedId,
                collectedBy: pregnancyOutcome.collectedBy,
                collectedDeviceId: pregnancyOutcome.collectedDeviceId,
                collectedHouseholdId: pregnancyOutcome.collectedHouseholdId,
                collectedMemberId: pregnancyChild.childCollectedId,
                collectedDate: pregnancyOutcome.collectedDate,
                uploadedDate: pregnancyOutcome.uploadedDate)
    }

    private RawResidency createNewRawResidencyFrom(RawPregnancyChild pregnancyChild, Household motherResidency, LocalDate outcomeDate){
        return new RawResidency(
                memberCode: pregnancyChild.childCode,
                householdCode: motherResidency.code,
                startType: ResidencyStartType.BIRTH.code,
                startDate: outcomeDate)
    }

    private RawHeadRelationship createNewRawHeadRelationshipFrom(RawPregnancyChild pregnancyChild, Household motherResidency, LocalDate outcomeDate){
        return new RawHeadRelationship(
                memberCode: pregnancyChild.childCode,
                householdCode: motherResidency.code,
                relationshipType: pregnancyChild.headRelationshipType,
                startType: HeadRelationshipStartType.BIRTH.code,
                startDate: outcomeDate)
    }

    private RawDeath createRawDeath(RawPregnancyOutcome rawPregnancyOutcome, RawPregnancyChild rawPregnancyChild) {
        def rawDeath = new RawDeath()

        rawDeath.id = rawPregnancyOutcome.id
        rawDeath.visitCode = rawPregnancyOutcome.visitCode
        rawDeath.memberCode = rawPregnancyChild.childCode
        rawDeath.deathDate = rawPregnancyOutcome.outcomeDate
        rawDeath.deathCause = rawPregnancyChild.outcomeType
        rawDeath.deathPlace = rawPregnancyOutcome?.birthPlace?.equals("OTHER") ? rawPregnancyOutcome.birthPlaceOther : rawPregnancyOutcome.birthPlace

        rawDeath.collectedBy = rawPregnancyOutcome.collectedBy
        rawDeath.collectedDeviceId = rawPregnancyOutcome.collectedDeviceId
        rawDeath.collectedHouseholdId = rawPregnancyOutcome.collectedHouseholdId
        rawDeath.collectedMemberId = rawPregnancyChild.childCollectedId
        rawDeath.collectedStart = rawPregnancyOutcome.collectedStart
        rawDeath.collectedEnd = rawPregnancyOutcome.collectedEnd
        rawDeath.collectedDate = rawPregnancyOutcome.collectedDate
        rawDeath.uploadedDate = rawPregnancyOutcome.uploadedDate

        return rawDeath
    }

    private PregnancyOutcome newPregnancyOutcomeInstance(RawPregnancyOutcome po){

        def mother = memberService.getMember(po.motherCode)
        def father = memberService.getMember(po.fatherCode)
        def visit = visitService.getVisit(po.visitCode)
        def birthplace = BirthPlace.getFrom(po.birthPlace)


        PregnancyOutcome pregnancyOutcome = new PregnancyOutcome()

        pregnancyOutcome.code = po.code
        pregnancyOutcome.mother = mother
        pregnancyOutcome.motherCode = po.motherCode
        pregnancyOutcome.father = father
        pregnancyOutcome.fatherCode = po.fatherCode
        pregnancyOutcome.outcomeDate = po.outcomeDate
        pregnancyOutcome.numberOfOutcomes = po.numberOfOutcomes
        pregnancyOutcome.numberOfLivebirths = 0 //Update when create childs

        pregnancyOutcome.birthPlace = birthplace
        pregnancyOutcome.birthPlaceOther = po.birthPlaceOther

        pregnancyOutcome.visit = visit
        pregnancyOutcome.visitCode = po.visitCode

        //set collected by info
        pregnancyOutcome.collectedId = po.id
        pregnancyOutcome.collectedBy = userService.getUser(po.collectedBy)
        pregnancyOutcome.collectedDeviceId = po.collectedDeviceId
        pregnancyOutcome.collectedHouseholdId = po.collectedHouseholdId
        pregnancyOutcome.collectedMemberId = po.collectedMemberId
        pregnancyOutcome.collectedStart = po.collectedStart
        pregnancyOutcome.collectedEnd = po.collectedEnd
        pregnancyOutcome.collectedDate = po.collectedDate
        pregnancyOutcome.updatedDate = po.uploadedDate

        return pregnancyOutcome

    }

    private PregnancyChild newPregnancyChildInstance(RawPregnancyChild pc, PregnancyOutcome pregnancyOutcome, HeadRelationship headRelationship){

        // Create after saving Member, Residency and HeadRelationship of a child

        PregnancyChild pregnancyChild = new PregnancyChild()

        pregnancyChild.outcome = pregnancyOutcome
        pregnancyChild.outcomeCode = pregnancyOutcome.code
        pregnancyChild.outcomeType = PregnancyOutcomeType.getFrom(pc.outcomeType)

        if (pregnancyChild.outcomeType == PregnancyOutcomeType.LIVEBIRTH) {

            def child = memberService.getMember(pc.childCode)

            pregnancyChild.child = child
            pregnancyChild.childCollectedId = pc.childCollectedId
            pregnancyChild.childCode = child.code
            pregnancyChild.childOrdinalPosition = pc.childOrdinalPosition
            pregnancyChild.childHeadRelationship = headRelationship
        }

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
