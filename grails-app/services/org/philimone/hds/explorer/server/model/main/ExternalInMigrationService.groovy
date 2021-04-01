package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawExternalInMigration
import org.philimone.hds.explorer.server.model.collect.raw.RawHeadRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawInMigration
import org.philimone.hds.explorer.server.model.collect.raw.RawMember
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyChild
import org.philimone.hds.explorer.server.model.collect.raw.RawPregnancyOutcome
import org.philimone.hds.explorer.server.model.enums.BirthPlace
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.PregnancyOutcomeType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType
import org.philimone.hds.explorer.server.model.enums.temporal.InMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDate

@Transactional
class ExternalInMigrationService {

    def householdService
    def memberService
    def userService
    def residencyService
    def headRelationshipService
    def deathService
    def inMigrationService
    def visitService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="InMigration Utilities Methods">

    boolean isMemberReturningToStudyArea(String memberCode) {

        def member = memberService.getMember(memberCode)
        def residency = residencyService.getCurrentResidency(member)

        if (member == null || residency == null) return false

         //current residency
        return (residency.endType == ResidencyEndType.EXTERNAL_OUTMIGRATION) //if its out of area, now is returning
    }

    List<RawMessage> deleteMember(Member member) {

        def errors = new ArrayList<RawMessage>()

        try {
            member.delete(flush: true)
        } catch(Exception ex) {
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER, "validation.general.database.member.error", [ ex.getMessage() ], [])
            ex.printStackTrace()
        }

        return errors
    }

    List<RawMessage> deleteMemberResidencies(Member member) {

        def errors = new ArrayList<RawMessage>()

        try {
            Residency.executeUpdate("delete r from Residency r where r.member.id=?", [member.id])
        } catch(Exception ex) {
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER, "validation.general.database.residency.error", [ ex.getMessage() ], [])
            ex.printStackTrace()
        }

        return errors
    }

    List<RawMessage> deleteInMigration(InMigration inMigration) {

        def errors = new ArrayList<RawMessage>()

        try {
            inMigration.delete(flush: true)
        } catch(Exception ex) {
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.general.database.inmigration.error", [ ex.getMessage() ], [])
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
    RawExecutionResult<InMigration> createExternalInMigration(RawExternalInMigration rawExternalInMigration) {

        /* Run Checks and Validations */

        def errors = validate(rawExternalInMigration)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<InMigration> obj = RawExecutionResult.newErrorResult(RawEntity.EXTERNAL_INMIGRATION, errors)
            return obj
        }


        def member = memberService.getMember(rawExternalInMigration.memberCode)
        def isReturningToStudyArea = member != null

        def newRawMember = createNewRawMemberFrom(rawExternalInMigration)
        def newRawInMigration =  createRawInMigration(rawExternalInMigration)
        def newRawHeadRelationship = createNewRawHeadRelationshipFrom(rawExternalInMigration)


        //create member and execute inmigration
        def resultMember = (isReturningToStudyArea ? null : memberService.createMember(newRawMember)) as RawExecutionResult<Member>
        def resultInMigration = inMigrationService.createInMigration(newRawInMigration)
        def resultHeadRelationship = (resultInMigration.status == RawExecutionResult.Status.ERROR) ? null : headRelationshipService.createHeadRelationship(newRawHeadRelationship)

        //Couldnt create Member
        if (resultMember != null && resultMember.status == RawExecutionResult.Status.ERROR) {

            errors += errorMessageService.addPrefixToMessages(resultMember.errorMessages, "validation.field.inmigration.external.prefix.msg.error", [rawExternalInMigration.id])

            RawExecutionResult<InMigration> obj = RawExecutionResult.newErrorResult(RawEntity.EXTERNAL_INMIGRATION, errors)
            return obj
        }

        if (resultInMigration.status == RawExecutionResult.Status.ERROR) {

            //delete member
            errors += resultInMigration.errorMessages

            if (isReturningToStudyArea == false) { //its a new member
                errors += deleteMember(resultMember.domainInstance)
            }

            errors = errorMessageService.addPrefixToMessages(errors, "validation.field.inmigration.external.prefix.msg.error", [rawExternalInMigration.id])

            RawExecutionResult<InMigration> obj = RawExecutionResult.newErrorResult(RawEntity.EXTERNAL_INMIGRATION, errors)
            return obj
        }

        if (resultHeadRelationship != null && resultHeadRelationship.status == RawExecutionResult.Status.ERROR) {

            //delete member and inmigration
            errors += resultHeadRelationship.errorMessages
            errors += deleteInMigration(resultMember.domainInstance)

            if (isReturningToStudyArea == false) { //its a new member
                errors += deleteMemberResidencies(resultMember.domainInstance) //delete possible created residency
                errors += deleteMember(resultMember.domainInstance)
            }

            errors = errorMessageService.addPrefixToMessages(errors, "validation.field.inmigration.external.prefix.msg.error", [rawExternalInMigration.id])

            RawExecutionResult<InMigration> obj = RawExecutionResult.newErrorResult(RawEntity.EXTERNAL_INMIGRATION, errors)
            return obj
        }


        //SUCCESS
        RawExecutionResult<PregnancyOutcome> obj = RawExecutionResult.newSuccessResult(RawEntity.EXTERNAL_INMIGRATION, resultInMigration.domainInstance)
        return obj
    }

    ArrayList<RawMessage> validate(RawExternalInMigration externalInMigration){
        def errors = new ArrayList<RawMessage>()

        //memberCode, memberName, memberGender, memberDob, memberMotherCode, memberFatherCode, headRelationshipType
        def isBlankMemberCode = StringUtil.isBlank(externalInMigration.memberCode)
        def isBlankMemberName = StringUtil.isBlank(externalInMigration.memberName)
        def isBlankMemberGender = StringUtil.isBlank(externalInMigration.memberGender)
        def isBlankMemberDob = StringUtil.isBlankDate(externalInMigration.memberDob)
        def isBlankMotherCode = StringUtil.isBlank(externalInMigration.memberMotherCode)
        def isBlankFatherCode = StringUtil.isBlank(externalInMigration.memberFatherCode)
        def isBlankHeadRelationshipType = StringUtil.isBlank(externalInMigration.headRelationshipType)

        def isBlankVisitCode = StringUtil.isBlank(externalInMigration.visitCode)
        def isBlankMigrationType = StringUtil.isBlank(externalInMigration.migrationType)
        def isBlankOriginCode = StringUtil.isBlank(externalInMigration.originCode)
        def isBlankDestinationCode = StringUtil.isBlank(externalInMigration.destinationCode)
        def isBlankMigrationDate = StringUtil.isBlankDate(externalInMigration.migrationDate)
        def isBlankMigrationReason = StringUtil.isBlank(externalInMigration.migrationReason)

        def isBlankCollectedBy = StringUtil.isBlank(externalInMigration.collectedBy)

        def mother = memberService.getMember(externalInMigration.memberMotherCode)
        def father = memberService.getMember(externalInMigration.memberFatherCode)
        def motherExists = mother != null
        def fatherExists = father != null
        def motherUnknown = motherExists && mother?.code == Codes.MEMBER_UNKNOWN_CODE
        def fatherUnknown = fatherExists && father?.code == Codes.MEMBER_UNKNOWN_CODE
        def migrationType = InMigrationType.getFrom(externalInMigration.migrationType)
        def visit = visitService.getVisit(externalInMigration.visitCode)
        def origin = householdService.getHousehold(externalInMigration.originCode)
        def destination = householdService.getHousehold(externalInMigration.destinationCode)

        def originExists = origin != null
        def destinationExists = destination != null
        def visitExists = visit != null

        def isReturningToStudyArea = isMemberReturningToStudyArea(externalInMigration.memberCode)

        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (memberName)
        if (isBlankMemberName && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["memberName"], ["memberName"])
        }
        //C1. Check Blank Fields (memberGender)
        if (isBlankMemberGender && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["memberGender"], ["memberGender"])
        }
        //C1. Check Blank Fields (memberMotherCode)
        if (isBlankMotherCode && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["memberMotherCode"], ["memberMotherCode"])
        }
        //C1. Check Blank Fields (memberFatherCode)
        if (isBlankFatherCode && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["memberFatherCode"], ["memberFatherCode"])
        }
        //C1. Check Blank Fields (memberDob)
        if (isBlankMemberDob && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["memberDob"], ["memberDob"])
        }
        //C1. Check Blank Fields (headRelationshipType)
        if (isBlankHeadRelationshipType){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["headRelationshipType"], ["headRelationshipType"])
        }

        //C2. Check Code Regex Pattern
        if (!isBlankMemberCode && !codeGeneratorService.isMemberCodeValid(externalInMigration.memberCode)) {
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.pattern.no.matches", ["memberCode", "TXUPF1001001"], ["memberCode"])
        }
        //C3. Check Code Prefix Reference existence (Household Existence in memberCode)
        if (!isBlankMemberCode && !householdService.prefixExists(externalInMigration.memberCode)){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.pattern.prefix.household.reference.error", [externalInMigration.memberCode], ["memberCode"])
        }

        //C12. Validate Gender Enum Options
        if (!isBlankMemberGender && Gender.getFrom(externalInMigration.memberGender)==null && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.enum.choices.error", [externalInMigration.memberGender, "memberGender"], ["memberGender"])
        }
        //C12. Validate Enum Options (headRelationshipType)
        if (!isBlankHeadRelationshipType && HeadRelationshipType.getFrom(externalInMigration.headRelationshipType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.enum.choices.error", [externalInMigration.headRelationshipType, "headRelationshipType"], ["headRelationshipType"])
        }

        //C5. Check dob max date
        if (!isBlankMemberDob && externalInMigration.memberDob > LocalDate.now() && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.date.not.greater.today", ["dob", StringUtil.format(externalInMigration.memberDob)], ["memberDob"])
        }

        //C4. Check Mother reference existence
        if (!isBlankMotherCode && !motherExists && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.reference.error", ["Member", "motherCode", externalInMigration.memberMotherCode], ["memberMotherCode"])
        }
        //C4. Check Father reference existence
        if (!isBlankFatherCode && !fatherExists && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.reference.error", ["Member", "fatherCode", externalInMigration.memberFatherCode], ["memberFatherCode"])
        }

        //C6. Check mother Dob must be greater or equal to 12
        if (!motherUnknown && motherExists && GeneralUtil.getAge(mother.dob)< Codes.MIN_MOTHER_AGE_VALUE ){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.dob.mother.minage.error", [StringUtil.format(mother.dob), Codes.MIN_MOTHER_AGE_VALUE+""], ["mother.dob"])
        }
        //C7. Check father Dob must be greater or equal to 12
        if (!fatherUnknown && fatherExists && GeneralUtil.getAge(father.dob)< Codes.MIN_FATHER_AGE_VALUE ){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.dob.father.minage.error", [StringUtil.format(father.dob), Codes.MIN_FATHER_AGE_VALUE+""], ["father.dob"])
        }

        //C9. Check mother Gender
        if (Codes.GENDER_CHECKING && !motherUnknown &&  motherExists && mother.gender==Gender.MALE && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.gender.mother.error", [], ["mother.gender"])
        }
        //C10. Check father Gender
        if (Codes.GENDER_CHECKING && !fatherUnknown && fatherExists && father.gender==Gender.FEMALE && !isReturningToStudyArea){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.gender.father.error", [], ["father.gender"])
        }

        //Add InMigration Checks
        //C1. Check Blank Fields (migrationType)
        if (isBlankMigrationType){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["migrationType"], ["migrationType"])
        }
        //C1. Check Blank Fields (destinationCode)
        if (isBlankDestinationCode){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["destinationCode"], ["destinationCode"])
        }
        //C1. Check Blank Fields (originCode)
        if (isBlankOriginCode && migrationType==InMigrationType.INTERNAL){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["originCode"], ["originCode"])
        }
        //C1. Check Nullable Fields (migrationDate)
        if (isBlankMigrationDate){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["migrationDate"], ["migrationDate"])
        }
        //C1. Check Blank Fields (migrationReason)
        if (isBlankMigrationReason){
            //errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["migrationReason"], ["migrationReason"])
        }
        //C6. Validate migrationType Enum Options
        if (!isBlankMigrationType && migrationType==null){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.enum.choices.error", [externalInMigration.migrationType, "migrationType"], ["migrationType"])
        }

        //CX. Validate the visitCode with the destinationCode(household being visited)
        if (!isBlankVisitCode && !isBlankDestinationCode && !externalInMigration.visitCode.startsWith(externalInMigration.destinationCode)){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.inmigration.visit.code.prefix.not.current.error", [externalInMigration.visitCode, externalInMigration.destinationCode], ["visitCode","destinationCode"])
        }

        //C2. Check Visit reference existence
        if (!visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.reference.error", ["Visit", "visitCode", externalInMigration.visitCode], ["visitCode"])
        }
        //C2. CHECK ORIGIN(if migType is INTERNAL) AND DESTINATION
        if (!destinationExists){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.reference.error", ["Member", "destinationCode", externalInMigration.destinationCode], ["destinationCode"])
        }

        //C3. Check MigrationDate against maxDate
        if (!isBlankMigrationDate && externalInMigration.migrationDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.date.not.greater.today", ["migrationDate"], ["migrationDate"])
        }
        //C4. Check MigrationDate against dateOfBirth
        if (!isBlankMigrationDate && !isBlankMemberDob && externalInMigration.migrationDate < externalInMigration.memberDob){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.inmigration.dob.not.greater.date", [StringUtil.format(externalInMigration.migrationDate)], ["memberDob"])
        }

        //C5. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(externalInMigration.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.user.dont.exists.error", [externalInMigration.collectedBy], ["collectedBy"])
        }

        //C6. Check Duplicate of memberCode - only if is the first entry
        if (!isReturningToStudyArea && !isBlankMemberCode && memberService.exists(externalInMigration.memberCode)){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.reference.duplicate.error", ["Member", "code", externalInMigration.memberCode], ["code"])
        }

        //C5. Check Member Death Status
        if (isReturningToStudyArea && deathService.isMemberDead(externalInMigration.memberCode)){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.inmigration.death.exists.error", [externalInMigration.memberCode], ["memberCode"])
        }

        if (errors.isEmpty()){

            if (isReturningToStudyArea == false) { //Coming from outside the area - its his first entry

                //the codes must be validated (memberCode must contains destinationCode)
                if (!externalInMigration.memberCode.startsWith(externalInMigration.destinationCode)){
                    errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.inmigration.member.code.invalid.error", [externalInMigration.memberCode, externalInMigration.destinationCode], ["memberCode", "destinationCode"])
                    return errors
                }

                //check if there is a HeadOfHousehold already
                def headType = HeadRelationshipType.getFrom(externalInMigration.headRelationshipType)

                if (headType == HeadRelationshipType.HEAD_OF_HOUSEHOLD) {

                    def currentHead = headRelationshipService.getCurrentHouseholdHead(destination)

                    if (currentHead != null && currentHead.endType == HeadRelationshipEndType.NOT_APPLICABLE) {
                        //cant create inmigration-head-relationship, the household
                        errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.inmigration.external.head.not.closed.error", [externalInMigration.memberCode, externalInMigration.destinationCode], ["memberCode", "destinationCode"])
                    }
                }
            }


            //We cant try to create Residency/HeadRelationship, member doesnt exists yet
            if (isReturningToStudyArea == true) { //member already exists - try new residency and headrelationship
                def newRawInMigration = createRawInMigration(externalInMigration)
                def newRawHeadRelationship = createNewRawHeadRelationshipFrom(externalInMigration)

                def innerErrors1 = inMigrationService.validate(newRawInMigration)
                errors += errorMessageService.addPrefixToMessages(innerErrors1, "validation.field.inmigration.external.prefix.msg.error", [externalInMigration.id])

                def innerErrors2 = headRelationshipService.validateCreateHeadRelationship(newRawHeadRelationship)
                errors += errorMessageService.addPrefixToMessages(innerErrors2, "validation.field.inmigration.external.prefix.msg.error", [externalInMigration.id])
            }

        }

        return errors
    }

    private RawMember createNewRawMemberFrom(RawExternalInMigration externalInMigration){

        return new RawMember(
                code: externalInMigration.memberCode,
                name: externalInMigration.memberName,
                gender: externalInMigration.memberGender,
                dob: externalInMigration.memberDob,
                motherCode: externalInMigration.memberMotherCode,
                fatherCode: externalInMigration.memberFatherCode,
                householdCode: externalInMigration.destinationCode)
    }

    private RawInMigration createRawInMigration(RawExternalInMigration rawExternalInMigration) {
        def rawInMig = new RawInMigration()

        rawInMig.visitCode = rawExternalInMigration.visitCode
        rawInMig.memberCode = rawExternalInMigration.memberCode
        rawInMig.migrationType = InMigrationType.EXTERNAL.code
        rawInMig.originCode = rawExternalInMigration.originCode
        rawInMig.originOther = rawExternalInMigration.originOther
        rawInMig.destinationCode = rawExternalInMigration.destinationCode
        rawInMig.migrationDate = rawExternalInMigration.migrationDate
        rawInMig.migrationReason = rawExternalInMigration.migrationReason

        rawInMig.collectedBy = rawExternalInMigration.collectedBy
        rawInMig.collectedDate = rawExternalInMigration.collectedDate
        rawInMig.uploadedDate = rawExternalInMigration.uploadedDate

        return rawInMig
    }

    private RawHeadRelationship createNewRawHeadRelationshipFrom(RawExternalInMigration rawExternalInMigration){
        return new RawHeadRelationship(
                memberCode: rawExternalInMigration.memberCode,
                householdCode: rawExternalInMigration.destinationCode,
                relationshipType: rawExternalInMigration.headRelationshipType,
                startType: HeadRelationshipStartType.EXTERNAL_INMIGRATION.code,
                startDate: rawExternalInMigration.migrationDate)
    }

    //</editor-fold>

}
