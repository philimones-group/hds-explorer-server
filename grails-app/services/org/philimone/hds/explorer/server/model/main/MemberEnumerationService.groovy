package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawMemberEnu
import org.philimone.hds.explorer.server.model.collect.raw.RawHeadRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawMember
import org.philimone.hds.explorer.server.model.collect.raw.RawResidency
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDate

@Transactional
class MemberEnumerationService {
    def householdService
    def memberService
    def userService
    def residencyService
    def headRelationshipService
    def visitService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="Member Utilities Methods">

    List<RawMessage> deleteMember(Member member) {

        def errors = new ArrayList<RawMessage>()

        try {
            member.delete(flush: true)
        } catch(Exception ex) {
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER, "validation.general.database.member.error", [ex.getMessage() ], [])
            ex.printStackTrace()
        }

        return errors
    }

    List<RawMessage> deleteResidency(Residency residency) {
//println "member code=${residency.memberCode}, r.id=${residency.id}"
        def errors = new ArrayList<RawMessage>()

        try {
            residency.member = null;
            residency.household = null;
            residency.save()
            Residency.executeUpdate("delete from Residency r where r.id=?", [residency.id])
        } catch(Exception ex) {
            errors << errorMessageService.getRawMessage(RawEntity.RESIDENCY, "validation.general.database.residency.error", [ ex.getMessage() ], [])
            ex.printStackTrace()
        }

        return errors
    }

    List<RawMessage> deleteHeadRelationship(HeadRelationship headRelationship) {

        def errors = new ArrayList<RawMessage>()

        try {
            headRelationship.delete(flush: true)
        } catch(Exception ex) {
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.general.database.headrelationship.error", [ ex.getMessage() ], [])
            ex.printStackTrace()
        }

        return errors
    }

    //</editor-fold>

    //<editor-fold desc="Member Factory/Manager Methods">
    RawExecutionResult<Enumeration> createMemberEnumeration(RawMemberEnu rawMemberEnu) {

        /* Run Checks and Validations */

        def errors = validate(rawMemberEnu)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Member> obj = RawExecutionResult.newErrorResult(RawEntity.MEMBER_ENUMERATION, errors)
            return obj
        }

        def newRawMember = createNewRawMemberFrom(rawMemberEnu)
        def newRawResidency =  createNewResidencyFrom(rawMemberEnu)
        def newRawHeadRelationship = createNewRawHeadRelationshipFrom(rawMemberEnu)


        //create member and execute inmigration
        def resultMember = memberService.createMember(newRawMember)
        def resultResidency = residencyService.createResidency(newRawResidency)
        def resultHeadRelationship = headRelationshipService.createHeadRelationship(newRawHeadRelationship)
        def visit = visitService.getVisit(rawMemberEnu.visitCode)

        //Couldnt create Member
        if (resultMember.status == RawExecutionResult.Status.ERROR) {

            errors += errorMessageService.addPrefixToMessages(resultMember.errorMessages, "validation.field.member.enumeration.prefix.msg.error", [rawMemberEnu.id])

            RawExecutionResult<Member> obj = RawExecutionResult.newErrorResult(RawEntity.MEMBER_ENUMERATION, errors)
            return obj
        }

        if (resultMember?.domainInstance != null && resultResidency.status == RawExecutionResult.Status.ERROR) {

            //delete member
            errors += resultResidency.errorMessages

            if (resultHeadRelationship?.domainInstance != null) {
                errors += deleteHeadRelationship(resultHeadRelationship.domainInstance)
            }

            errors += deleteMember(resultMember.domainInstance)

            errors = errorMessageService.addPrefixToMessages(errors, "validation.field.member.enumeration.prefix.msg.error", [rawMemberEnu.id])

            RawExecutionResult<Member> obj = RawExecutionResult.newErrorResult(RawEntity.MEMBER_ENUMERATION, errors)
            return obj
        }

        if (resultMember?.domainInstance != null && resultHeadRelationship.status == RawExecutionResult.Status.ERROR) {

            //delete member and inmigration
            errors += resultHeadRelationship.errorMessages

            if (resultResidency?.domainInstance != null) {
                errors += deleteResidency(resultResidency.domainInstance)
            }

            errors += deleteMember(resultMember.domainInstance)

            errors = errorMessageService.addPrefixToMessages(errors, "validation.field.member.enumeration.prefix.msg.error", [rawMemberEnu.id])

            RawExecutionResult<Member> obj = RawExecutionResult.newErrorResult(RawEntity.MEMBER_ENUMERATION, errors)
            return obj
        }

        //if no errors - create enumeration
        def enumeration = new Enumeration()
        enumeration.visit = visit
        enumeration.visitCode = visit.code
        enumeration.household = householdService.getHousehold(visit.householdCode)
        enumeration.householdCode = visit.householdCode
        enumeration.member = resultMember.domainInstance
        enumeration.memberCode = enumeration.member.code
        enumeration.eventDate = rawMemberEnu.residencyStartDate
        enumeration.save(flush:true)


        if (enumeration.hasErrors()){
            errors = errorMessageService.getRawMessages(RawEntity.MEMBER_ENUMERATION, enumeration)

            RawExecutionResult<Enumeration> obj = RawExecutionResult.newErrorResult(RawEntity.MEMBER_ENUMERATION, errors)
            return obj
        }

        //SUCCESS
        RawExecutionResult<Enumeration> obj = RawExecutionResult.newSuccessResult(RawEntity.MEMBER_ENUMERATION, enumeration)
        return obj
    }

    ArrayList<RawMessage> validate(RawMemberEnu memberEnu){
        def errors = new ArrayList<RawMessage>()

        //code, name, gender, dob, motherCode, fatherCode, headRelationshipType
        def isBlankVisitCode = StringUtil.isBlank(memberEnu.visitCode)
        def isBlankCode = StringUtil.isBlank(memberEnu.code)
        def isBlankName = StringUtil.isBlank(memberEnu.name)
        def isBlankGender = StringUtil.isBlank(memberEnu.gender)
        def isBlankDob = StringUtil.isBlankDate(memberEnu.dob)
        def isBlankMotherCode = StringUtil.isBlank(memberEnu.motherCode)
        //def isBlankMotherName = StringUtil.isBlank(memberEnu.motherName) //not needed
        def isBlankFatherCode = StringUtil.isBlank(memberEnu.fatherCode)
        //def isBlankFatherName = StringUtil.isBlank(memberEnu.fatherName)

        def isBlankHouseholdCode = StringUtil.isBlank(memberEnu.householdCode)
        //def isBlankHouseholdName = StringUtil.isBlank(memberEnu.householdName)
        def isBlankHeadRelationshipType = StringUtil.isBlank(memberEnu.headRelationshipType)
        def isBlankResidencyStartDate = StringUtil.isBlankDate(memberEnu.residencyStartDate)

        def isBlankCollectedBy = StringUtil.isBlank(memberEnu.collectedBy)

        def mother = memberService.getMember(memberEnu.motherCode)
        def father = memberService.getMember(memberEnu.fatherCode)
        def motherExists = mother != null
        def fatherExists = father != null
        def motherUnknown = motherExists && mother?.code == Codes.MEMBER_UNKNOWN_CODE
        def fatherUnknown = fatherExists && father?.code == Codes.MEMBER_UNKNOWN_CODE
        def headRelationshipType = HeadRelationshipType.getFrom(memberEnu.headRelationshipType)
        def visit = visitService.getVisit(memberEnu.visitCode)
        def household = householdService.getHousehold(memberEnu.householdCode)

        def householdExists = household != null
        def visitExists = visit != null

        
        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (code)
        if (isBlankCode){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["code"], ["code"])
        }
        //C1. Check Blank Fields (name)
        if (isBlankName){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["name"], ["name"])
        }
        //C1. Check Blank Fields (gender)
        if (isBlankGender){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["gender"], ["gender"])
        }
        //C1. Check Blank Fields (dob)
        if (isBlankDob){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["dob"], ["dob"])
        }
        //C1. Check Blank Fields (motherCode)
        if (isBlankMotherCode){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["motherCode"], ["motherCode"])
        }
        //C1. Check Blank Fields (fatherCode)
        if (isBlankFatherCode){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["fatherCode"], ["fatherCode"])
        }
        //C1. Check Blank Fields (headRelationshipType)
        if (isBlankHeadRelationshipType){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["headRelationshipType"], ["headRelationshipType"])
        }

        //C2. Check Code Regex Pattern
        if (!isBlankCode && !codeGeneratorService.isMemberCodeValid(memberEnu.code)) {
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.pattern.no.matches", ["code", codeGeneratorService.memberSampleCode], ["code"])
        }
        //C3. Check Code Prefix Reference existence (Household Existence in code)
        if (!isBlankCode && !householdService.prefixExists(memberEnu.code)){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.pattern.prefix.household.reference.error", [memberEnu.code], ["code"])
        }

        //C12. Validate Gender Enum Options
        if (!isBlankGender && Gender.getFrom(memberEnu.gender)==null){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.enum.choices.error", [memberEnu.gender, "gender"], ["gender"])
        }
        //C12. Validate Enum Options (headRelationshipType)
        if (!isBlankHeadRelationshipType && HeadRelationshipType.getFrom(memberEnu.headRelationshipType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.enum.choices.error", [memberEnu.headRelationshipType, "headRelationshipType"], ["headRelationshipType"])
        }

        //C5. Check dob max date
        if (!isBlankDob && memberEnu.dob > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.date.not.greater.today", ["dob", StringUtil.format(memberEnu.dob)], ["dob"])
        }

        //C4. Check Mother reference existence
        if (!isBlankMotherCode && !motherExists){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.reference.error", ["Member", "motherCode", memberEnu.motherCode], ["motherCode"])
        }
        //C4. Check Father reference existence
        if (!isBlankFatherCode && !fatherExists){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.reference.error", ["Member", "fatherCode", memberEnu.fatherCode], ["fatherCode"])
        }

        //C6. Check mother Dob must be greater or equal to 12
        if (!motherUnknown && motherExists && GeneralUtil.getAge(mother.dob)< Codes.MIN_MOTHER_AGE_VALUE ){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.dob.mother.minage.error", [StringUtil.format(mother.dob), Codes.MIN_MOTHER_AGE_VALUE+""], ["mother.dob"])
        }
        //C7. Check father Dob must be greater or equal to 12
        if (!fatherUnknown && fatherExists && GeneralUtil.getAge(father.dob)< Codes.MIN_FATHER_AGE_VALUE ){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.dob.father.minage.error", [StringUtil.format(father.dob), Codes.MIN_FATHER_AGE_VALUE+""], ["father.dob"])
        }

        //C9. Check mother Gender
        if (Codes.GENDER_CHECKING && !motherUnknown &&  motherExists && mother.gender==Gender.MALE){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.gender.mother.error", [], ["mother.gender"])
        }
        //C10. Check father Gender
        if (Codes.GENDER_CHECKING && !fatherUnknown && fatherExists && father.gender==Gender.FEMALE){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.gender.father.error", [], ["father.gender"])
        }

        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (householdName)
        //if (isBlankHouseholdName){
        //    errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["householdName"], ["householdName"])
        //}
        //C1. Check Nullable Fields (residencyStartDate)
        if (isBlankResidencyStartDate){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.blank", ["residencyStartDate"], ["residencyStartDate"])
        }

        //C6. Validate headRelationshipType Enum Options
        if (!isBlankHeadRelationshipType && headRelationshipType==null){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.enum.choices.error", [memberEnu.headRelationshipType, "headRelationshipType"], ["headRelationshipType"])
        }

        //CX. Validate the visitCode with the householdCode(household being visited)
        if (!isBlankVisitCode && !isBlankHouseholdCode && !memberEnu.visitCode.startsWith(memberEnu.householdCode)){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.member.enumeration.visit.code.prefix.not.current.error", [memberEnu.visitCode, memberEnu.householdCode], ["visitCode","householdCode"])
        }

        //C2. Check Visit reference existence
        if (!visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.reference.error", ["Visit", "visitCode", memberEnu.visitCode], ["visitCode"])
        }
        //C2. Check Household reference existence
        if (!householdExists){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.reference.error", ["Member", "householdCode", memberEnu.householdCode], ["householdCode"])
        }

        //C3. Check residencyStartDate against maxDate
        if (!isBlankResidencyStartDate && memberEnu.residencyStartDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.date.not.greater.today", ["residencyStartDate"], ["residencyStartDate"])
        }
        //C4. Check residencyStartDate against dateOfBirth
        if (!isBlankResidencyStartDate && !isBlankDob && memberEnu.residencyStartDate < memberEnu.dob){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.member.enumeration.dob.not.greater.date", [StringUtil.format(memberEnu.residencyStartDate)], ["memberDob"])
        }

        //C5. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(memberEnu.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.user.dont.exists.error", [memberEnu.collectedBy], ["collectedBy"])
        }

        //C6. Check Duplicate of memberCode
        if (!isBlankCode && memberService.exists(memberEnu.code)){
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.reference.duplicate.error", ["Member", "code", memberEnu.code], ["code"])
        }

        if (errors.isEmpty()){

            //the codes must be validated (memberCode must contains householdCode)
            if (!memberEnu.code.startsWith(memberEnu.householdCode)){
                errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.member.enumeration.member.code.invalid.error", [memberEnu.code, memberEnu.householdCode], ["memberCode", "householdCode"])
                return errors
            }

            //check if there is a HeadOfHousehold already
            if (headRelationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD) {

                def currentHead = headRelationshipService.getCurrentHouseholdHead(household)

                if (currentHead != null && currentHead.endType == HeadRelationshipEndType.NOT_APPLICABLE) {
                    //cant create inmigration-head-relationship, the household
                    errors << errorMessageService.getRawMessage(RawEntity.MEMBER_ENUMERATION, "validation.field.member.enumeration.head.not.closed.error", [memberEnu.code, currentHead.householdCode], ["memberCode", "householdCode"])
                }
            }


            //We cant try to create Residency/HeadRelationship, member doesnt exists yet
            /*
            def newRawResidency = createNewResidencyFrom(memberEnu)
            def newRawHeadRelationship = createNewRawHeadRelationshipFrom(memberEnu)

            def innerErrors1 = inMigrationService.validate(newRawResidency)
            errors += errorMessageService.addPrefixToMessages(innerErrors1, "validation.field.member.enumeration.prefix.msg.error", [memberEnu.id])

            def innerErrors2 = headRelationshipService.validateCreateHeadRelationship(newRawHeadRelationship)
            errors += errorMessageService.addPrefixToMessages(innerErrors2, "validation.field.member.enumeration.prefix.msg.error", [memberEnu.id])
            */

        }

        return errors
    }

    private RawMember createNewRawMemberFrom(RawMemberEnu memberEnu){

        return new RawMember(
                code: memberEnu.code,
                name: memberEnu.name,
                gender: memberEnu.gender,
                dob: memberEnu.dob,
                motherCode: memberEnu.motherCode,
                fatherCode: memberEnu.fatherCode,
                householdCode: memberEnu.householdCode,
                modules: memberEnu.modules,
                collectedId: memberEnu.id,
                collectedBy: memberEnu.collectedBy,
                collectedDate: memberEnu.collectedDate,
                uploadedDate: memberEnu.uploadedDate
        )
    }

    private RawResidency createNewResidencyFrom(RawMemberEnu rawMemberEnu){
        return new RawResidency(
                memberCode: rawMemberEnu.code,
                householdCode: rawMemberEnu.householdCode,                
                startType: ResidencyStartType.ENUMERATION.code,
                startDate: rawMemberEnu.residencyStartDate)
    }
    
    private RawHeadRelationship createNewRawHeadRelationshipFrom(RawMemberEnu rawMemberEnu){
        return new RawHeadRelationship(
                memberCode: rawMemberEnu.code,
                householdCode: rawMemberEnu.householdCode,
                relationshipType: rawMemberEnu.headRelationshipType,
                startType: HeadRelationshipStartType.ENUMERATION.code,
                startDate: rawMemberEnu.residencyStartDate)
    }

    //</editor-fold>
}
