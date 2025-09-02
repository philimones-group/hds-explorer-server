package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.DateUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawOutMigration
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.OutMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

import java.time.LocalDate

@Transactional
class OutMigrationService {

    def householdService
    def memberService
    def visitService
    def userService
    def residencyService
    def headRelationshipService
    def deathService
    def coreExtensionService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="InMigration Utilities Methods">

    //</editor-fold>

    //<editor-fold desc="OutMigration Factory/Manager Methods">
    RawExecutionResult<OutMigration> createOutMigration(RawOutMigration rawOutMigration) {

        /* Run Checks and Validations */

        def errors = validate(rawOutMigration)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<OutMigration> obj = RawExecutionResult.newErrorResult(RawEntity.OUT_MIGRATION, errors)
            return obj
        }

        //1. closeResidency, closeHeadRelationship, closeMaritalRelationship
        //2. Update Member residencyStatus, maritalStatus

        def member = memberService.getMember(rawOutMigration.memberCode)
        def previousRawResidency = residencyService.getCurrentResidencyAsRaw(member)
        def previousRawHeadRelationship = headRelationshipService.getLastHeadRelationshipAsRaw(member) //his relationship with the head, even if he is the head
        def createdOutmigration = newOutMigrationInstance(rawOutMigration) as OutMigration
        def closedResidency = null as Residency
        def closedHeadRelationship = null as HeadRelationship

        // Closing the Residency with OutMigration
        if (previousRawResidency != null && previousRawResidency.endType == ResidencyEndType.NOT_APPLICABLE.code){ //must be opened
            previousRawResidency.endType = createdOutmigration.migrationType==OutMigrationType.INTERNAL ? ResidencyEndType.INTERNAL_OUTMIGRATION.code : ResidencyEndType.EXTERNAL_OUTMIGRATION.code
            previousRawResidency.endDate = createdOutmigration.migrationDate
            def result = residencyService.closeResidency(previousRawResidency)

            if (result.status == RawExecutionResult.Status.ERROR) {
                errors += result.errorMessages

                RawExecutionResult<OutMigration> obj = RawExecutionResult.newErrorResult(RawEntity.OUT_MIGRATION, errors)
                return obj
            }
        }

        // Closing the HeadRelationship with OutMigration
        if (previousRawHeadRelationship != null && previousRawHeadRelationship.endType == HeadRelationshipEndType.NOT_APPLICABLE.code){ //must be opened
            previousRawHeadRelationship.endType = createdOutmigration.migrationType==OutMigrationType.INTERNAL ? HeadRelationshipEndType.INTERNAL_OUTMIGRATION.code : HeadRelationshipEndType.EXTERNAL_OUTMIGRATION.code
            previousRawHeadRelationship.endDate = createdOutmigration.migrationDate
            def result = headRelationshipService.closeHeadRelationship(previousRawHeadRelationship)

            if (result.status == RawExecutionResult.Status.ERROR) {
                //rollback previous data
                if (closedResidency != null) {
                    closedResidency.endType = ResidencyEndType.NOT_APPLICABLE
                    closedResidency.endDate = null
                    closedResidency.save(flush:true)

                    member.endType = ResidencyEndType.NOT_APPLICABLE
                    member.endDate = null
                    member.save(flush:true)
                }

                errors += result.errorMessages

                RawExecutionResult<OutMigration> obj = RawExecutionResult.newErrorResult(RawEntity.OUT_MIGRATION, errors)
                return obj
            }
        }

        //Create OutMigration
        def result = createdOutmigration.save(flush:true)

        //Validate using Gorm Validations
        if (createdOutmigration.hasErrors()){

            //rollback data
            deleteAllCreatedRecords(closedResidency, member, closedHeadRelationship)

            errors = errorMessageService.getRawMessages(RawEntity.OUT_MIGRATION, createdOutmigration)

            RawExecutionResult<OutMigration> obj = RawExecutionResult.newErrorResult(RawEntity.OUT_MIGRATION, errors)
            return obj
        } else {
            createdOutmigration = result
        }

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertOutMigrationExtension(rawOutMigration, result)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail

            deleteAllCreatedRecords(closedResidency, member, closedHeadRelationship)
            createdOutmigration.delete(flush: true)

            println "Failed to insert extension: ${resultExtension.errorMessage}"

            errors << new RawMessage(resultExtension.errorMessage, null)
            RawExecutionResult<OutMigration> obj = RawExecutionResult.newErrorResult(RawEntity.OUT_MIGRATION, errors)
            return obj
        }

        //Set Vacant if no members in the household
        householdService.setHouseholdStatusVacant(createdOutmigration.origin)

        RawExecutionResult<OutMigration> obj = RawExecutionResult.newSuccessResult(RawEntity.OUT_MIGRATION, createdOutmigration, errors)
        return obj
    }

    private void deleteAllCreatedRecords(Residency closedResidency, Member member, HeadRelationship closedHeadRelationship) {
        //restore previous residency data
        if (closedResidency != null) {
            closedResidency.endType = ResidencyEndType.NOT_APPLICABLE
            closedResidency.endDate = null
            closedResidency.save(flush: true)

            member.endType = ResidencyEndType.NOT_APPLICABLE
            member.endDate = null
            member.save(flush: true)
        }
        //restore previous head relationship data
        if (closedHeadRelationship != null) {
            closedHeadRelationship.endType = HeadRelationshipEndType.NOT_APPLICABLE
            closedHeadRelationship.endDate = null
            closedHeadRelationship.save(flush: true)
        }
    }

    ArrayList<RawMessage> validate(RawOutMigration rawOutMigration){
        def errors = new ArrayList<RawMessage>()

        //visitCode, memberCode, migrationType, originCode, destinationCode, migrationDate, migrationReason, outMigrationPlace
        def isBlankVisitCode = StringUtil.isBlank(rawOutMigration.visitCode)
        def isBlankMemberCode = StringUtil.isBlank(rawOutMigration.memberCode)
        def isBlankMigrationType = StringUtil.isBlank(rawOutMigration.migrationType)
        def isBlankOriginCode = StringUtil.isBlank(rawOutMigration.originCode)
        def isBlankDestinationCode = StringUtil.isBlank(rawOutMigration.destinationCode)
        def isBlankMigrationDate = StringUtil.isBlankDate(rawOutMigration.migrationDate)
        def isBlankMigrationReason = StringUtil.isBlank(rawOutMigration.migrationReason)
        def isBlankCollectedBy = StringUtil.isBlank(rawOutMigration.collectedBy)

        def migrationType = OutMigrationType.getFrom(rawOutMigration.migrationType)
        def member = memberService.getMember(rawOutMigration.memberCode)
        def visit = visitService.getVisit(rawOutMigration.visitCode)
        def origin = householdService.getHousehold(rawOutMigration.originCode)
        def destination = householdService.getHousehold(rawOutMigration.destinationCode)

        def originExists = origin != null
        def destinationExists = destination != null
        def memberExists = member != null
        def visitExists = visit != null

        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (migrationType)
        if (isBlankMigrationType){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.blank", ["migrationType"], ["migrationType"])
        }
        //C1. Check Blank Fields (originCode)
        if (isBlankOriginCode){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.blank", ["originCode"], ["originCode"])
        }
        //C1. Check Blank Fields (destinationCode)
        if (isBlankDestinationCode && migrationType==OutMigrationType.INTERNAL){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.blank", ["destinationCode"], ["destinationCode"])
        }
        //C1. Check Nullable Fields (migrationDate)
        if (isBlankMigrationDate){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.blank", ["migrationDate"], ["migrationDate"])
        }
        //C1. Check Blank Fields (migrationReason)
        if (isBlankMigrationReason){
            //errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.blank", ["migrationReason"], ["migrationReason"])
        }
        //C6. Validate migrationType Enum Options
        if (!isBlankMigrationType && migrationType==null){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.enum.choices.error", [rawOutMigration.migrationType, "migrationType"], ["migrationType"])
        }

        //CX. Validate the visitCode with the originCode(household being visited) - but its must be EXTERNAL OutMigration, INTERNAL takes VisitCode from his InMigration fellow
        if (!isBlankVisitCode && !isBlankOriginCode && migrationType==OutMigrationType.EXTERNAL && !rawOutMigration.visitCode.startsWith(rawOutMigration.originCode)){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.outmigration.visit.code.prefix.not.current.error", [rawOutMigration.visitCode, rawOutMigration.originCode], ["visitCode","originCode"])
        }

        //C2. Check Visit reference existence
        if (!visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.reference.error", ["Visit", "visitCode", rawOutMigration.visitCode], ["visitCode"])
        }
        //C2. Check Member reference existence
        if (!memberExists){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.reference.error", ["Member", "memberCode", rawOutMigration.memberCode], ["memberCode"])
        }
        //C2. CHECK ORIGIN AND DESTINATION(if migType is INTERNAL)
        if (!originExists){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.reference.error", ["Member", "originCode", rawOutMigration.originCode], ["originCode"])
        }
        if (!destinationExists && migrationType == OutMigrationType.INTERNAL){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.reference.error", ["Member", "destinationCode", rawOutMigration.destinationCode], ["destinationCode"])
        }

        //C3. Check MigrationDate against maxDate
        if (!isBlankMigrationDate && rawOutMigration.migrationDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.date.not.greater.today", ["migrationDate"], ["migrationDate"])
        }
        //C4. Check MigrationDate against dateOfBirth
        if (!isBlankMigrationDate && memberExists && rawOutMigration.migrationDate < member.dob){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.outmigration.dob.not.greater.date", [DateUtil.getInstance().formatYMD(rawOutMigration.migrationDate)], ["dob"])
        }

        //CV. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(rawOutMigration.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.user.dont.exists.error", [rawOutMigration.collectedBy], ["collectedBy"])
        }

        //C5. Check Member Death Status
        if (memberExists && deathService.isMemberDead(rawOutMigration.memberCode)){
            errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.outmigration.death.exists.error", [rawOutMigration.memberCode], ["memberCode"])
        }


        if (errors.isEmpty()){


            def residency = residencyService.getCurrentResidencyAsRaw(member)
            def headRelationship = headRelationshipService.getLastHeadRelationshipAsRaw(member) //his relationship with the head, even if he is the head
            def isHeadOfHousehold = headRelationship!=null ? (headRelationship.relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD.code) : false
            def headRelationships = headRelationshipService.getCurrentHeadRelationships(rawOutMigration.memberCode, member?.household?.code)

            /*
            * CHECK VERY CAREFULLY THE RELATIONSHIPS, SOME DONT NEED VALIDATIONS
            * Residency,
            * 1. If we are going to Outmigrate someone endType must be NA
            *    Cannot register OutMigration, The Member is not currently living in the Household [{0}], the current residency uuid=[{0}] have the endType=[{1}]
            * HeadRelationship
            * 2. Find Opened HeadRelationships and try to close then
            * */

            /* Member must be Living in current Household */
            if (residency != null){

                //origin code vs residency.householdCode
                if (residency.householdCode != rawOutMigration.originCode){
                    errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.residency.not.current.error", ["originCode", residency.memberCode], ["originCode","memberCode"])
                    return errors
                }

                //Test Closing the Residency with OutMigration
                //simulate DTH
                residency.endType = migrationType==OutMigrationType.INTERNAL ? ResidencyEndType.INTERNAL_OUTMIGRATION.code : ResidencyEndType.EXTERNAL_OUTMIGRATION.code
                residency.endDate = rawOutMigration.migrationDate

                errors += residencyService.validateCloseResidency(residency)
            } else {

                //The individual doesnt have a residency registry in the system

                errors << errorMessageService.getRawMessage(RawEntity.OUT_MIGRATION, "validation.field.outmigration.residency.not.found.error", [member.code], ["memberCode"])

                return errors
            }

            // HeadRelationship as Member who relates with Head of Household
            if (headRelationship != null && headRelationship.endType == HeadRelationshipEndType.NOT_APPLICABLE.code){ //must be opened

                //simulate DTH
                headRelationship.endType = migrationType==OutMigrationType.INTERNAL ? HeadRelationshipEndType.INTERNAL_OUTMIGRATION.code : HeadRelationshipEndType.EXTERNAL_OUTMIGRATION.code
                headRelationship.endDate = rawOutMigration.migrationDate

                errors += headRelationshipService.validateCloseHeadRelationship(headRelationship)
            }


            //Other opened relationships if he is head of household
            if (isHeadOfHousehold && (headRelationships != null && headRelationships.size()>0)){

                headRelationships.each { hr ->
                    if (hr.endType == HeadRelationshipEndType.NOT_APPLICABLE){ //opened relationship
                        def rawHr = headRelationshipService.convertToRaw(hr)
                        rawHr.endType = migrationType==OutMigrationType.INTERNAL ? HeadRelationshipEndType.INTERNAL_OUTMIGRATION.code : HeadRelationshipEndType.EXTERNAL_OUTMIGRATION.code
                        rawHr.endDate = rawOutMigration.migrationDate

                        errors += headRelationshipService.validateCloseHeadRelationship(rawHr)
                    }
                }
            }

        }

        return errors
    }

    private OutMigration newOutMigrationInstance(RawOutMigration rout){

        def member = memberService.getMember(rout.memberCode)
        def visit = visitService.getVisit(rout.visitCode)
        def origin = householdService.getHousehold(rout.originCode)
        def originRes = residencyService.getCurrentResidency(member)
        def type = OutMigrationType.getFrom(rout?.migrationType)

        OutMigration outMigration = new OutMigration()

        outMigration.member = member
        outMigration.memberCode = member.code

        outMigration.visit = visit
        outMigration.visitCode = visit.code

        outMigration.migrationType = type

        //origin
        outMigration.origin = origin
        outMigration.originCode = origin.code
        outMigration.originResidency = originRes

        //destination - has reference if is INTERNAL if not just a description of where the member is moving to
        if (outMigration.migrationType == OutMigrationType.INTERNAL){
            outMigration.destination = householdService.getHousehold(rout.destinationCode)
            outMigration.destinationCode = rout.destinationCode
        } else {
            outMigration.destinationOther = rout.destinationOther
        }

        outMigration.migrationDate = rout.migrationDate
        outMigration.migrationReason = rout.migrationReason


        //set collected by info
        outMigration.collectedId = rout.id
        outMigration.collectedBy = userService.getUser(rout.collectedBy)
        outMigration.collectedDeviceId = rout.collectedDeviceId
        outMigration.collectedHouseholdId = rout.collectedHouseholdId
        outMigration.collectedMemberId = rout.collectedMemberId
        outMigration.collectedStart = rout.collectedStart
        outMigration.collectedEnd = rout.collectedEnd
        outMigration.collectedDate = rout.collectedDate
        outMigration.updatedDate = rout.uploadedDate

        return outMigration

    }
    //</editor-fold>
}
