package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawExternalInMigration
import org.philimone.hds.explorer.server.model.collect.raw.RawHeadRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawInMigration
import org.philimone.hds.explorer.server.model.collect.raw.RawMember
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
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
    def coreExtensionService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="InMigration Utilities Methods">

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


        def extInMigType = ExternalInMigrationType.getFrom(rawExternalInMigration.extMigrationType)
        def isReturningToStudyArea = extInMigType==ExternalInMigrationType.REENTRY

        def newRawMember = createNewRawMemberFrom(rawExternalInMigration)
        def newRawInMigration =  createRawInMigration(rawExternalInMigration)

        //create member and execute inmigration
        def resultMember = (isReturningToStudyArea ? null : memberService.createMember(newRawMember)) as RawExecutionResult<Member>
        def resultInMigration = inMigrationService.createInMigration(newRawInMigration)

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

        afterNewHouseholdMember(rawExternalInMigration)

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertExternalInMigrationExtension(rawExternalInMigration, resultInMigration.domainInstance)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail
            println "Failed to insert extension: ${resultExtension.errorMessage}"
        }

        //SUCCESS
        RawExecutionResult<InMigration> obj = RawExecutionResult.newSuccessResult(RawEntity.EXTERNAL_INMIGRATION, resultInMigration.domainInstance)
        return obj
    }

    def afterNewHouseholdMember(RawExternalInMigration rawObj) {
        def visit = visitService.getVisit(rawObj.visitCode)

        if (visit != null && visit.respondent == null && rawObj.memberCode?.equalsIgnoreCase(visit.respondentCode)) {
            visit.respondent = memberService.getMember(rawObj.memberCode)
            visit.save(flush:true)
        }
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
        def isBlankExtMigrationType = StringUtil.isBlank(externalInMigration.extMigrationType)
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
        def extMigrationType = ExternalInMigrationType.getFrom(externalInMigration.extMigrationType)
        def visit = visitService.getVisit(externalInMigration.visitCode)
        def origin = householdService.getHousehold(externalInMigration.originCode)
        def destination = householdService.getHousehold(externalInMigration.destinationCode)
        def member = memberService.getMember(externalInMigration.memberCode)

        def originExists = origin != null
        def destinationExists = destination != null
        def visitExists = visit != null
        def memberExists = member != null

        def isReturningToStudyArea = extMigrationType==ExternalInMigrationType.REENTRY //isMemberReturningToStudyArea(externalInMigration.memberCode)

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
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.pattern.no.matches", ["memberCode", codeGeneratorService.memberSampleCode], ["memberCode"])
        }
        //C3. Check Code Prefix Reference existence (Household Existence in memberCode)
        /*if (!isBlankMemberCode && !householdService.prefixExists(externalInMigration.memberCode)){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.pattern.prefix.household.reference.error", [externalInMigration.memberCode], ["memberCode"])
        }*/

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

        //C8. Check loop on memberCode equals to fatherCode
        if (!isBlankMemberCode && !isBlankFatherCode && externalInMigration.memberCode.equalsIgnoreCase(externalInMigration.memberFatherCode)) {
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.inmigration.member.code.loop.father.error", [externalInMigration.memberCode], ["fatherCode"])
        }

        //C8. Check loop on memberCode equals to motherCode
        if (!isBlankMemberCode && !isBlankMotherCode && externalInMigration.memberCode.equalsIgnoreCase(externalInMigration.memberMotherCode)) {
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.inmigration.member.code.loop.mother.error", [externalInMigration.memberCode], ["motherCode"])
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
        //C1. Check Blank Fields (extMigrationType)
        if (isBlankExtMigrationType){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.blank", ["extMigrationType"], ["extMigrationType"])
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
        //C6. Validate extMigrationType Enum Options
        if (!isBlankExtMigrationType && ExternalInMigrationType.getFrom(externalInMigration.extMigrationType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.enum.choices.error", [externalInMigration.migrationType, "extMigrationType"], ["extMigrationType"])
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
        if (!isReturningToStudyArea && memberExists){
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

                //this is not duplicated by memberCode - check if this extInMig is entering to the correct household by validating collectedHouseholdId
                if (destination.collectedId != null && !destination.collectedId.equalsIgnoreCase(externalInMigration.collectedHouseholdId)) {
                    //duplicate error
                    errors << errorMessageService.getRawMessage(RawEntity.EXTERNAL_INMIGRATION, "validation.field.inmigration.household.code.duplicated.error", [externalInMigration.destinationCode, destination.collectedId, externalInMigration.collectedHouseholdId], ["memberCode", "householdCode", "collectedHouseholdId"])
                    return errors
                }

            }

            //We cant try to create Residency/HeadRelationship, member doesnt exists yet
            if (isReturningToStudyArea == true) { //member already exists - try new residency and headrelationship
                def newRawInMigration = createRawInMigration(externalInMigration)

                def innerErrors1 = inMigrationService.validate(newRawInMigration)
                errors += errorMessageService.addPrefixToMessages(innerErrors1, "validation.field.inmigration.external.prefix.msg.error", [externalInMigration.id])
            }

        }

        return errors
    }

    private RawMember createNewRawMemberFrom(RawExternalInMigration externalInMigration){

        return new RawMember(
                id: externalInMigration.collectedMemberId,
                code: externalInMigration.memberCode,
                name: externalInMigration.memberName,
                gender: externalInMigration.memberGender,
                dob: externalInMigration.memberDob,
                motherCode: externalInMigration.memberMotherCode,
                fatherCode: externalInMigration.memberFatherCode,
                householdCode: externalInMigration.destinationCode,
                education: externalInMigration.education,
                religion: externalInMigration.religion,
                phonePrimary: externalInMigration.phonePrimary,
                phoneAlternative: externalInMigration.phoneAlternative,
                collectedId: externalInMigration.collectedMemberId,
                collectedBy: externalInMigration.collectedBy,
                collectedDate: externalInMigration.collectedDate,
                collectedDeviceId: externalInMigration.collectedDeviceId,
                collectedHouseholdId: externalInMigration.collectedHouseholdId,
                collectedMemberId: externalInMigration.collectedMemberId,
                modules: externalInMigration.modules)
    }

    private RawInMigration createRawInMigration(RawExternalInMigration rawExternalInMigration) {
        def rawInMig = new RawInMigration()

        rawInMig.visitCode = rawExternalInMigration.visitCode
        rawInMig.memberCode = rawExternalInMigration.memberCode
        rawInMig.migrationType = InMigrationType.EXTERNAL.code
        rawInMig.extMigrationType = rawExternalInMigration.extMigrationType
        rawInMig.originCode = rawExternalInMigration.originCode
        rawInMig.originOther = rawExternalInMigration.originOther
        rawInMig.destinationCode = rawExternalInMigration.destinationCode
        rawInMig.migrationDate = rawExternalInMigration.migrationDate
        rawInMig.migrationReason = rawExternalInMigration.migrationReason

        rawInMig.headRelationshipType = rawExternalInMigration.headRelationshipType

        rawInMig.education = rawExternalInMigration.education
        rawInMig.religion = rawExternalInMigration.religion
        rawInMig.phonePrimary = rawExternalInMigration.phonePrimary;
        rawInMig.phoneAlternative = rawExternalInMigration.phoneAlternative;

        rawInMig.collectedBy = rawExternalInMigration.collectedBy
        rawInMig.collectedDeviceId = rawExternalInMigration.collectedDeviceId
        rawInMig.collectedHouseholdId = rawExternalInMigration.collectedHouseholdId
        rawInMig.collectedMemberId = rawExternalInMigration.collectedMemberId
        rawInMig.collectedStart = rawExternalInMigration.collectedStart
        rawInMig.collectedEnd = rawExternalInMigration.collectedEnd
        rawInMig.collectedDate = rawExternalInMigration.collectedDate
        rawInMig.uploadedDate = rawExternalInMigration.uploadedDate

        return rawInMig
    }

    //</editor-fold>

}
