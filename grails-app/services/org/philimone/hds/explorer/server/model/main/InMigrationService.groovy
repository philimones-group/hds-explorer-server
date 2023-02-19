package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawHeadRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawInMigration
import org.philimone.hds.explorer.server.model.collect.raw.RawOutMigration
import org.philimone.hds.explorer.server.model.collect.raw.RawResidency
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType
import org.philimone.hds.explorer.server.model.enums.temporal.InMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.OutMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

import java.time.LocalDate

@Transactional
class InMigrationService {

    def householdService
    def memberService
    def visitService
    def userService
    def residencyService
    def headRelationshipService
    def deathService
    def outMigrationService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="InMigration Utilities Methods">

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

    List<RawMessage> deleteMemberResidencies(Member member) {

        def errors = new ArrayList<RawMessage>()

        try {
            Residency.executeUpdate("delete from Residency r where r.member.id=?", [member.id])
        } catch(Exception ex) {
            errors << errorMessageService.getRawMessage(RawEntity.MEMBER, "validation.general.database.residency.error", [ ex.getMessage() ], [])
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

    //<editor-fold desc="InMigration Factory/Manager Methods">
    RawExecutionResult<InMigration> createInMigration(RawInMigration rawInMigration) {

        /* Run Checks and Validations */

        def errors = validate(rawInMigration)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<InMigration> obj = RawExecutionResult.newErrorResult(RawEntity.IN_MIGRATION, errors)
            return obj
        }

        //create from raw data
        def inmigration = newInMigrationInstance(rawInMigration)
        def newRawResidency = createNewResidencyFromInMig(inmigration)
        def rawOutMigration = inmigration.type==InMigrationType.INTERNAL ? createOutMigrationFromInMig(inmigration) : null
        def newRawHeadRelationship = createNewRawHeadRelationshipFrom(rawInMigration)

        //1. create inmigration
        def resultInmigration = inmigration.save(flush:true)

        //Validate using Gorm Validations
        if (inmigration.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.IN_MIGRATION, inmigration)

            RawExecutionResult<InMigration> obj = RawExecutionResult.newErrorResult(RawEntity.IN_MIGRATION, errors)
            return obj
        }

        //inmigration executed successfully
        inmigration = resultInmigration


        //errors = afterCreateInMigration(inmigration)
        //1. Close Previous Residency with OutMigration if is an INTERNAL
        //2. Create new Residency record based on InMigration
        //3. Update InMigration destinationResidency


        //1. Create OutMigration from InMig if is a INTERNAL MIGRATION (close residency, etc)
        if (rawOutMigration != null){
            def resultOutMigIn = outMigrationService.createOutMigration(rawOutMigration)


            if (resultOutMigIn.status == RawExecutionResult.Status.ERROR) {
                errors += resultOutMigIn.errorMessages

                //delete the create inmigration - I can only delete it here (because we cant the rollbacks)
                inmigration.refresh()
                errors += deleteInMigration(inmigration)

                RawExecutionResult<InMigration> obj = RawExecutionResult.newErrorResult(RawEntity.IN_MIGRATION, errors)
                return obj
            }
        }

        //2. Create new Residency
        def resultNewResidency = residencyService.createResidency(newRawResidency)
        if (resultNewResidency != null && resultNewResidency.status == RawExecutionResult.Status.ERROR){
            errors += resultNewResidency.errorMessages

            //delete inmigration and outmigration - we need to implement a proper rollback solution

            RawExecutionResult<InMigration> obj = RawExecutionResult.newErrorResult(RawEntity.IN_MIGRATION, errors)
            return obj
        }

        //3. Create Head Relationship
        def resultHeadRelationship = headRelationshipService.createHeadRelationship(newRawHeadRelationship)
        if (resultHeadRelationship != null && resultHeadRelationship.status == RawExecutionResult.Status.ERROR) {

            //delete member and inmigration
            errors += resultHeadRelationship.errorMessages

            //errors += deleteInMigration(resultMember.domainInstance)

            /*if (isReturningToStudyArea == false) { //its a new member
                errors += deleteMemberResidencies(resultMember.domainInstance) //delete possible created residency
                errors += deleteMember(resultMember.domainInstance)
            }*/
            //errors = errorMessageService.addPrefixToMessages(errors, "validation.field.inmigration.external.prefix.msg.error", [rawInMigration.id])

            RawExecutionResult<InMigration> obj = RawExecutionResult.newErrorResult(RawEntity.IN_MIGRATION, errors)
            return obj
        }


        //X. update destinationResidency
        if (resultNewResidency?.domainInstance != null) {
            def residency = resultNewResidency.domainInstance.refresh()
            inmigration.destinationResidency = residency
            inmigration.save(flush:true)
        }

        RawExecutionResult<InMigration> obj = RawExecutionResult.newSuccessResult(RawEntity.IN_MIGRATION, inmigration, errors)
        return obj
    }

    ArrayList<RawMessage> validate(RawInMigration rawInMigration){
        def errors = new ArrayList<RawMessage>()

        //visitCode, memberCode, migrationType, originCode, destinationCode, migrationDate, migrationReason, InMigrationPlace
        def isBlankVisitCode = StringUtil.isBlank(rawInMigration.visitCode)
        def isBlankMemberCode = StringUtil.isBlank(rawInMigration.memberCode)
        def isBlankHeadRelationshipType = StringUtil.isBlank(rawInMigration.headRelationshipType)
        def isBlankMigrationType = StringUtil.isBlank(rawInMigration.migrationType)
        def isBlankOriginCode = StringUtil.isBlank(rawInMigration.originCode)
        def isBlankDestinationCode = StringUtil.isBlank(rawInMigration.destinationCode)
        def isBlankMigrationDate = StringUtil.isBlankDate(rawInMigration.migrationDate)
        def isBlankMigrationReason = StringUtil.isBlank(rawInMigration.migrationReason)
        def isBlankCollectedBy = StringUtil.isBlank(rawInMigration.collectedBy)

        def migrationType = InMigrationType.getFrom(rawInMigration.migrationType)
        def member = memberService.getMember(rawInMigration.memberCode)
        def visit = visitService.getVisit(rawInMigration.visitCode)
        def origin = householdService.getHousehold(rawInMigration.originCode)
        def destination = householdService.getHousehold(rawInMigration.destinationCode)

        def originExists = origin != null
        def destinationExists = destination != null
        def memberExists = member != null
        def visitExists = visit != null

        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (migrationType)
        if (isBlankMigrationType){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.blank", ["migrationType"], ["migrationType"])
        }
        //C1. Check Blank Fields (headRelationshipType)
        if (isBlankHeadRelationshipType){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.blank", ["headRelationshipType"], ["headRelationshipType"])
        }
        //C1. Check Blank Fields (destinationCode)
        if (isBlankDestinationCode){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.blank", ["destinationCode"], ["destinationCode"])
        }
        //C1. Check Blank Fields (originCode)
        if (isBlankOriginCode && migrationType==InMigrationType.INTERNAL){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.blank", ["originCode"], ["originCode"])
        }
        //C1. Check Nullable Fields (migrationDate)
        if (isBlankMigrationDate){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.blank", ["migrationDate"], ["migrationDate"])
        }
        //C1. Check Blank Fields (migrationReason)
        if (isBlankMigrationReason){
            //errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.blank", ["migrationReason"], ["migrationReason"])
        }
        //C6. Validate migrationType Enum Options
        if (!isBlankMigrationType && migrationType==null){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.enum.choices.error", [rawInMigration.migrationType, "migrationType"], ["migrationType"])
        }

        //C12. Validate Enum Options (headRelationshipType)
        if (!isBlankHeadRelationshipType && HeadRelationshipType.getFrom(rawInMigration.headRelationshipType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.enum.choices.error", [rawInMigration.headRelationshipType, "headRelationshipType"], ["headRelationshipType"])
        }

        //CX. Validate the visitCode with the destinationCode(household being visited)
        if (!isBlankVisitCode && !isBlankDestinationCode && !rawInMigration.visitCode.startsWith(rawInMigration.destinationCode)){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.inmigration.visit.code.prefix.not.current.error", [rawInMigration.visitCode, rawInMigration.destinationCode], ["visitCode","destinationCode"])
        }

        //C2. Check Visit reference existence
        if (!visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.reference.error", ["Visit", "visitCode", rawInMigration.visitCode], ["visitCode"])
        }
        //C2. Check Member reference existence
        if (!memberExists){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.reference.error", ["Member", "memberCode", rawInMigration.memberCode], ["memberCode"])
        }
        //C2. CHECK ORIGIN(if migType is INTERNAL) AND DESTINATION
        if (!originExists && migrationType == InMigrationType.INTERNAL){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.reference.error", ["Member", "originCode", rawInMigration.originCode], ["originCode"])
        }
        if (!destinationExists){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.reference.error", ["Member", "destinationCode", rawInMigration.destinationCode], ["destinationCode"])
        }

        //C3. Check MigrationDate against maxDate
        if (!isBlankMigrationDate && rawInMigration.migrationDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.date.not.greater.today", ["migrationDate"], ["migrationDate"])
        }
        //C4. Check MigrationDate against dateOfBirth
        if (!isBlankMigrationDate && memberExists && rawInMigration.migrationDate < member.dob){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.inmigration.dob.not.greater.date", [StringUtil.format(rawInMigration.migrationDate)], ["dob"])
        }

        //CV. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(rawInMigration.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.user.dont.exists.error", [rawInMigration.collectedBy], ["collectedBy"])
        }

        //C5. Check Member Death Status
        if (memberExists && deathService.isMemberDead(rawInMigration.memberCode)){
            errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.inmigration.death.exists.error", [rawInMigration.memberCode], ["memberCode"])
        }


        if (errors.isEmpty()){

            //println "No Errors Found - Jump to test phase 2"


            //check if there is a HeadOfHousehold already
            def headType = HeadRelationshipType.getFrom(rawInMigration.headRelationshipType)

            if (headType == HeadRelationshipType.HEAD_OF_HOUSEHOLD) {

                def currentHead = headRelationshipService.getCurrentHouseholdHead(destination)

                if (currentHead != null && currentHead.endType == HeadRelationshipEndType.NOT_APPLICABLE) {
                    //cant create inmigration-head-relationship, the household
                    errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.inmigration.external.head.not.closed.error", [rawInMigration.memberCode, rawInMigration.destinationCode], ["memberCode", "destinationCode"])
                }
            }

            /*
             * C7.3. Try to Create an OutMigration (validation)  -  set endType=CHG/EXT, endDate=migrationDate
             * C8.0. Try to create a new Residency (Use a simulated closed Residency)
             */

            def newRawHeadRelationship = createNewRawHeadRelationshipFrom(rawInMigration)
            def currentResidency = residencyService.getCurrentResidency(member) //current residency
            def currentHeadRelationship = headRelationshipService.getCurrentHeadRelationship(member)
            def newRawResidency = createNewResidencyFromInMig(rawInMigration)   //possible new residency

            /* Member must be Living in current Household */
            if (migrationType == InMigrationType.INTERNAL && currentResidency != null){

                //origin code vs residency.householdCode
                if (currentResidency.householdCode != rawInMigration.originCode){
                    errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.residency.not.current.error", ["originCode", currentResidency.memberCode], ["originCode","memberCode"])
                    return errors
                }

            }

            /* Internals InMigs must have residency */
            if (migrationType == InMigrationType.INTERNAL && currentResidency==null) {

                //The individual doesnt have a residency registry in the system
                errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.inmigration.residency.not.found.error", [member.code], ["memberCode"])

                return errors
            }

            /* When coming from outside the area - the memberCode must contains the destinationCode */
            if (migrationType == InMigrationType.EXTERNAL && currentResidency == null) {

                //if coming from outside and its his first time - the codes must be validated (memberCode must contains destinationCode)

                if (!rawInMigration.memberCode.startsWith(rawInMigration.destinationCode)){
                    errors << errorMessageService.getRawMessage(RawEntity.IN_MIGRATION, "validation.field.inmigration.member.code.invalid.error", [member.code, rawInMigration.destinationCode], ["memberCode", "destinationCode"])
                }

            }



            //Try create/close

            if (migrationType==InMigrationType.INTERNAL){

                //1. Try to Close Residency with an Outmigration
                def rawOutMigration = createOutMigrationFromInMig(rawInMigration)  ////simulate outmig
                errors += outMigrationService.validate(rawOutMigration)

                //2. Try to create new Residency if OutMigration worked
                if (errors.size() == 0){
                    def fakeResidency = createFakeClosedResidency(currentResidency, ResidencyEndType.INTERNAL_OUTMIGRATION, rawOutMigration.migrationDate) //simulates a residency closed by an outmigration

                    errors += residencyService.validateCreateResidency(fakeResidency, newRawResidency) //try to see if is possible to register a member in a new household
                }

                //3. Try to validate the creation of new Head Relationship - ???? - must have first a outmigration
                if (errors.size() == 0){

                    //create fake old relationship
                    def currentHead = headRelationshipService.getCurrentHouseholdHead(destination)
                    def fakeClosedHeadRelationship = createFakeClosedHeadRelationship(currentHeadRelationship, HeadRelationshipEndType.INTERNAL_OUTMIGRATION, rawOutMigration.migrationDate)
                    def fakeClosedHouseholdHead = createFakeClosedHeadRelationship(currentHead, HeadRelationshipEndType.INTERNAL_OUTMIGRATION, rawOutMigration.migrationDate)
                    def innerErrors1 = headRelationshipService.validateCreateHeadRelationship(newRawHeadRelationship, fakeClosedHeadRelationship, fakeClosedHouseholdHead)

                    if (innerErrors1.size()>0){
                        errors += errorMessageService.addPrefixToMessages(innerErrors1, "validation.field.inmigration.prefix.msg.error", [rawInMigration.id])
                    }
                }


            } else {
                //In this case you either have none residency for (External InMigration firstime) or you have a closed Residency record

                errors += residencyService.validateCreateResidency(newRawResidency)

                //if it is a external inmigration reentry - already has an outmigration - and if entry dont have any - just validate the creation of head relationship
                def innerErrors1 = headRelationshipService.validateCreateHeadRelationship(newRawHeadRelationship)
                if (innerErrors1.size()>0){
                    errors += errorMessageService.addPrefixToMessages(innerErrors1, "validation.field.inmigration.prefix.msg.error", [rawInMigration.id])
                }
            }

        }

        return errors
    }

    private InMigration newInMigrationInstance(RawInMigration rin){

        def member = memberService.getMember(rin.memberCode)
        def visit = visitService.getVisit(rin.visitCode)
        def origin = householdService.getHousehold(rin.originCode)

        InMigration inmigration = new InMigration()

        inmigration.member = member
        inmigration.memberCode = member.code

        inmigration.visit = visit
        inmigration.visitCode = visit.code

        inmigration.type = InMigrationType.getFrom(rin.migrationType)

        //origin
        if (inmigration.type == InMigrationType.EXTERNAL) {
            inmigration.extMigType = ExternalInMigrationType.getFrom(rin.extMigrationType)

            inmigration.originOther = rin.originOther
        }

        //destination - has reference if is INTERNAL if not just a description of where the member is moving to
        if (inmigration.type == InMigrationType.INTERNAL){
            inmigration.origin = origin
            inmigration.originCode = origin.code
        }

        inmigration.destination = householdService.getHousehold(rin.destinationCode)
        inmigration.destinationCode = rin.destinationCode
        //inmigration.destinationResidency - get after create migration

        inmigration.migrationDate = rin.migrationDate
        inmigration.migrationReason = rin.migrationReason

        //set collected by info
        inmigration.collectedId = rin.id
        inmigration.collectedBy = userService.getUser(rin.collectedBy)
        inmigration.collectedDeviceId = rin.collectedDeviceId
        inmigration.collectedHouseholdId = rin.collectedHouseholdId
        inmigration.collectedMemberId = rin.collectedMemberId
        inmigration.collectedDate = rin.collectedDate

        return inmigration

    }

    private RawHeadRelationship createNewRawHeadRelationshipFrom(RawInMigration rawInMigration){
        return new RawHeadRelationship(
                memberCode: rawInMigration.memberCode,
                householdCode: rawInMigration.destinationCode,
                relationshipType: rawInMigration.headRelationshipType,
                startType: HeadRelationshipStartType.getFrom(rawInMigration.migrationType).code,
                startDate: rawInMigration.migrationDate)
    }

    private static RawOutMigration createOutMigrationFromInMig(RawInMigration rawInMigration){
        def rawOutm = new RawOutMigration()

        rawOutm.visitCode = rawInMigration.visitCode
        rawOutm.memberCode = rawInMigration.memberCode
        rawOutm.migrationType = OutMigrationType.INTERNAL.code
        rawOutm.originCode = rawInMigration.originCode
        rawOutm.destinationCode = rawInMigration.destinationCode
        //rawOutm.destinationOther = ""
        rawOutm.migrationDate = GeneralUtil.addDaysToDate(rawInMigration.migrationDate, -1)  //the day of moving will be set 1 day before migration - the last day the member lived on that household
        rawOutm.migrationReason = rawInMigration.migrationReason

        rawOutm.collectedBy = rawInMigration.collectedBy
        rawOutm.collectedDeviceId = rawInMigration.collectedDeviceId
        rawOutm.collectedHouseholdId = rawInMigration.collectedHouseholdId
        rawOutm.collectedMemberId = rawInMigration.collectedMemberId
        rawOutm.collectedDate = rawInMigration.collectedDate
        rawOutm.uploadedDate = rawInMigration.uploadedDate

        return rawOutm
    }

    private static RawOutMigration createOutMigrationFromInMig(InMigration inMigration){
        def rawOutm = new RawOutMigration()

        rawOutm.visitCode = inMigration.visit?.code
        rawOutm.memberCode = inMigration.member?.code
        rawOutm.migrationType = OutMigrationType.INTERNAL.code
        rawOutm.originCode = inMigration.origin?.code
        rawOutm.destinationCode = inMigration.destination?.code
        //rawOutm.destinationOther = ""
        rawOutm.migrationDate = GeneralUtil.addDaysToDate(inMigration.migrationDate, -1)  //the day of moving will be set 1 day before migration - the last day the member lived on that household
        rawOutm.migrationReason = inMigration.migrationReason

        rawOutm.collectedBy = inMigration.collectedBy.code
        rawOutm.collectedDeviceId = inMigration.collectedDeviceId
        rawOutm.collectedHouseholdId = inMigration.collectedHouseholdId
        rawOutm.collectedMemberId = inMigration.collectedMemberId
        rawOutm.collectedDate = inMigration.collectedDate
        //rawOutm.uploadedDate = inMigration.uploadedDate

        return rawOutm
    }

    private static RawResidency createNewResidencyFromInMig(RawInMigration rawInMigration){
        def residency = new RawResidency()

        residency.memberCode = rawInMigration.memberCode
        residency.householdCode = rawInMigration.destinationCode

        residency.startDate = rawInMigration.migrationDate
        residency.startType = (rawInMigration.migrationType == InMigrationType.INTERNAL.code) ? ResidencyStartType.INTERNAL_INMIGRATION.code : ResidencyStartType.EXTERNAL_INMIGRATION.code

        return residency
    }

    private static RawResidency createNewResidencyFromInMig(InMigration inMigration){
        def residency = new RawResidency()

        residency.memberCode = inMigration.member?.code
        residency.householdCode = inMigration.destination?.code

        residency.startDate = inMigration.migrationDate
        residency.startType = (inMigration.type == InMigrationType.INTERNAL) ? ResidencyStartType.INTERNAL_INMIGRATION.code : ResidencyStartType.EXTERNAL_INMIGRATION.code

        return residency
    }

    private static Residency createFakeClosedResidency(Residency residency, ResidencyEndType endType, LocalDate endDate){
        def fakeResidency = new Residency()
        fakeResidency.member = residency.member
        fakeResidency.memberCode = residency.member?.code
        fakeResidency.household = residency.household
        fakeResidency.householdCode = residency.household?.code
        fakeResidency.startType = residency.startType
        fakeResidency.startDate = residency.startDate
        fakeResidency.endType = endType
        fakeResidency.endDate = endDate

        return fakeResidency
    }

    private static HeadRelationship createFakeClosedHeadRelationship(HeadRelationship headRelationship, HeadRelationshipEndType endType, LocalDate endDate){
        def fakeHeadRelationship = new HeadRelationship()
        fakeHeadRelationship.member = headRelationship.member
        fakeHeadRelationship.memberCode = headRelationship.member?.code
        fakeHeadRelationship.household = headRelationship.household
        fakeHeadRelationship.householdCode = headRelationship.household?.code
        fakeHeadRelationship.startType = headRelationship.startType
        fakeHeadRelationship.startDate = headRelationship.startDate
        fakeHeadRelationship.endType = endType
        fakeHeadRelationship.endDate = endDate

        return fakeHeadRelationship
    }

    //</editor-fold>

}
