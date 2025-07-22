package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawChangeHead
import org.philimone.hds.explorer.server.model.collect.raw.RawChangeHeadRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawHeadRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawHouseholdRelocation
import org.philimone.hds.explorer.server.model.collect.raw.RawInMigration
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.HouseholdRelocationReason
import org.philimone.hds.explorer.server.model.enums.InmigrationReason
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
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
class HouseholdRelocationService {

    /**
     * Validate data columns
     * 1. Close old Residencies and HeadRelationships
     * 2. Create new Residencies and HeadRelationships
     * 3. Try to create Inmigrations that avoid some checks
     */

    def householdService
    def memberService
    def headRelationshipService
    def residencyService
    def inMigrationService
    def visitService
    def coreExtensionService
    def userService
    def errorMessageService

    //<editor-fold desc="Utilities Methods">

    RawInMigration createFakeRawInmigration(Member member, RawHouseholdRelocation rawHouseholdRelocation, HeadRelationshipType headType) {
        def rawObj = new RawInMigration()

        rawObj.id = GeneralUtil.generateUUID()
        rawObj.visitCode = rawHouseholdRelocation.visitCode
        rawObj.memberCode = member.code

        rawObj.education = member.education
        rawObj.religion = member.religion
        rawObj.phonePrimary = member.phonePrimary
        rawObj.phoneAlternative = member.phoneAlternative

        rawObj.headRelationshipType = headType.code
        rawObj.migrationType = InMigrationType.INTERNAL.code
        rawObj.extMigrationType = null
        rawObj.originCode = rawHouseholdRelocation.originCode
        rawObj.originOther = null
        rawObj.destinationCode = rawHouseholdRelocation.destinationCode
        rawObj.migrationDate = rawHouseholdRelocation.eventDate
        rawObj.migrationReason = InmigrationReason.OTHER.code

        rawObj.collectedBy = rawHouseholdRelocation.collectedBy
        rawObj.collectedDeviceId = rawHouseholdRelocation.collectedDeviceId
        rawObj.collectedHouseholdId = rawHouseholdRelocation.collectedHouseholdId
        rawObj.collectedMemberId = member.collectedId
        rawObj.collectedStart = rawHouseholdRelocation.collectedDate
        rawObj.collectedEnd = rawHouseholdRelocation.collectedEnd
        rawObj.collectedDate = rawHouseholdRelocation.collectedDate
        rawObj.uploadedDate = rawHouseholdRelocation.uploadedDate

        rawObj.processedStatus = ProcessedStatus.NOT_PROCESSED
        rawObj.postExecution = false
        rawObj.isHouseholdRelocation = true

        return rawObj
    }

    HouseholdRelocation newHouseholdRelocationInstance(RawHouseholdRelocation rawHouseholdRelocation) {

        def visit = visitService.getVisit(rawHouseholdRelocation.visitCode)
        def origin = householdService.getHousehold(rawHouseholdRelocation.originCode)
        def destination = householdService.getHousehold(rawHouseholdRelocation.destinationCode)

        HouseholdRelocation relocation = new HouseholdRelocation()

        relocation.visit = visit
        relocation.visitCode = visit.code
        relocation.origin = origin
        relocation.originCode = origin?.code
        relocation.destination = destination
        relocation.destinationCode = destination?.code
        relocation.eventDate = rawHouseholdRelocation.eventDate
        relocation.reason = HouseholdRelocationReason.getFrom(rawHouseholdRelocation.reason)
        relocation.reasonOther = rawHouseholdRelocation.reasonOther

        //set collected by info
        relocation.collectedId = rawHouseholdRelocation.id
        relocation.collectedBy = userService.getUser(rawHouseholdRelocation.collectedBy)
        relocation.collectedDeviceId = rawHouseholdRelocation.collectedDeviceId
        relocation.collectedHouseholdId = rawHouseholdRelocation.collectedHouseholdId
        relocation.collectedMemberId = rawHouseholdRelocation.collectedMemberId
        relocation.collectedStart = rawHouseholdRelocation.collectedStart
        relocation.collectedEnd = rawHouseholdRelocation.collectedEnd
        relocation.collectedDate = rawHouseholdRelocation.collectedDate

        return relocation
    }
    //</editor-fold>

    //<editor-fold desc="HouseholdRelocation Factory/Manager Methods">

    RawExecutionResult<HouseholdRelocation> createHouseholdRelocation(RawHouseholdRelocation rawHouseholdRelocation) {
        /* Run Checks and Validations */

        def errors = validate(rawHouseholdRelocation)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<HouseholdRelocation> obj = RawExecutionResult.newErrorResult(RawEntity.HOUSEHOLD_RELOCATION, errors)
            return obj
        }

        def originHousehold = householdService.getHousehold(rawHouseholdRelocation.originCode)
        //def destination = householdService.getHousehold(rawHouseholdRelocation.destinationCode)
        //def eventDate = GeneralUtil.addDaysToDate(rawHouseholdRelocation.eventDate, -1)  //the day of moving will be set 1 day before changing head - the last day the member was related to the current head of household

        def householdRelocation = newHouseholdRelocationInstance(rawHouseholdRelocation)
        def createdHouseholdRelocation = householdRelocation.save(flush:true)
        def createdInmigrations = new ArrayList<InMigration>()
        def createdOutmigrations = new ArrayList<OutMigration>()
        def createdResidencies = new ArrayList<Residency>()
        def createdHeadRelationships = new ArrayList<HeadRelationship>()
        def previousResidencies = new ArrayList<Residency>()
        def previousHeadRelationships = new ArrayList<HeadRelationship>()

        //Validate using Gorm Validations
        if (householdRelocation.hasErrors()){
            errors = errorMessageService.getRawMessages(RawEntity.HOUSEHOLD_RELOCATION, householdRelocation)

            RawExecutionResult<HouseholdRelocation> obj = RawExecutionResult.newErrorResult(RawEntity.HOUSEHOLD_RELOCATION, errors)
            return obj
        }

        //create inmigrations to relocate members
        if (errors.empty) {
            def head = memberService.getMember(rawHouseholdRelocation.headCode)
            def membersToRelocate = residencyService.getCurrentResidentMembers(originHousehold)
            membersToRelocate.removeAll { it.id == head.id }
            membersToRelocate.add(0, head) //the list will start with the head of household

            //get residencies and current head relationship - those will be closed and saved for restore
            //in inmigrationService lets return created residencies and head relationships

            //Execute InMigrations
            membersToRelocate.each { member ->
                def currentResidency = residencyService.getCurrentResidency(member, originHousehold)
                def currentHeadRelationship = headRelationshipService.getLastHeadRelationship(member, originHousehold)
                def rawInmigration = createFakeRawInmigration(member, rawHouseholdRelocation, currentHeadRelationship.relationshipType)
                //We must reorder this by HEAD first

                def result = inMigrationService.createInMigration(rawInmigration)

                if (result.status==RawExecutionResult.Status.ERROR) {
                    def innerErrors = result.errorMessages
                    errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.householdrelocation.prefix.msg.error", [rawHouseholdRelocation.id])
                } else {
                    createdInmigrations.add(result.domainInstance)
                    createdResidencies.addAll(result.createdResidencies)
                    createdHeadRelationships.addAll(result.createdHeadRelationships)

                    def outmigIn = result.createdDomains.get(RawEntity.OUT_MIGRATION) as OutMigration
                    if (outmigIn != null) createdOutmigrations.add(outmigIn)

                    previousResidencies.add(currentResidency)
                    previousHeadRelationships.add(currentHeadRelationship)
                }
            }
        }
        
        //Roolback everything if an error ocurred - delete results
        if (!errors.empty) {

            //delete householdRelocation
            HouseholdRelocation.deleteAll(createdHouseholdRelocation)

            //undo Inmigrations
            createdInmigrations.each {it.delete(flush: true) }
            createdOutmigrations.each { it.delete(flush: true)}
            createdResidencies.each {it.delete(flush: true) }
            createdHeadRelationships.each {it.delete(flush: true) }
            createdInmigrations.each {it.delete(flush: true) }

            previousResidencies.each {
                //set old residencies and head relationships to:
                it.endType = ResidencyEndType.NOT_APPLICABLE
                it.endDate = null
                it.save(flush:true)

                def member = it.member
                member.endType = ResidencyEndType.NOT_APPLICABLE
                member.endDate = null;
                member.save(flush:true)
            }

            previousHeadRelationships.each {
                //set old residencies and head relationships to:
                it.endType = HeadRelationshipEndType.NOT_APPLICABLE
                it.endDate = null
                it.save(flush:true)
            }

            createdInmigrations.clear()
            createdOutmigrations.clear()
            createdResidencies.clear()
            createdHeadRelationships.clear()
            previousResidencies.clear()
            previousHeadRelationships.clear()
        }

        if (!errors.empty){
            RawExecutionResult<HouseholdRelocation> obj = RawExecutionResult.newErrorResult(RawEntity.HOUSEHOLD_RELOCATION, errors)
            return obj
        }

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertHouseholdRelocationExtension(rawHouseholdRelocation, createdHouseholdRelocation)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail
            println "Failed to insert extension: ${resultExtension.errorMessage}"
        }

        RawExecutionResult<HouseholdRelocation> obj = RawExecutionResult.newSuccessResult(RawEntity.HOUSEHOLD_RELOCATION, createdHouseholdRelocation, errors)
        return obj
    }

    ArrayList<RawMessage> validate(RawHouseholdRelocation rawHouseholdRelocation) {

        //visitCode - must exists
        //originCode - must exists
        //destinationCode - must exists
        //eventDate
        //reason
        //reasonOther

        def errors = new ArrayList<RawMessage>()

        def isBlankVisitCode = StringUtil.isBlank(rawHouseholdRelocation.visitCode)
        def isBlankOriginCode = StringUtil.isBlank(rawHouseholdRelocation.originCode)
        def isBlankDestinationCode = StringUtil.isBlank(rawHouseholdRelocation.destinationCode)
        def isBlankEventDate = StringUtil.isBlankDate(rawHouseholdRelocation.eventDate)
        def isBlankReason = StringUtil.isBlank(rawHouseholdRelocation.reason)

        def visit = visitService.getVisit(rawHouseholdRelocation.visitCode)
        def originHousehold = !isBlankOriginCode ? householdService.getHousehold(rawHouseholdRelocation.originCode) : null
        def destinationHousehold = !isBlankDestinationCode ? householdService.getHousehold(rawHouseholdRelocation.destinationCode) : null
        def reason = !isBlankReason ? HouseholdRelocationReason.getFrom(rawHouseholdRelocation.reason) : null

        def visitExists = visit != null
        def originExists = originHousehold != null
        def destinationExists = destinationHousehold != null


        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD_RELOCATION, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (originCode)
        if (isBlankOriginCode){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD_RELOCATION, "validation.field.blank", ["originCode"], ["originCode"])
        }
        //C1. Check Blank Fields (destinationCode)
        if (isBlankDestinationCode){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD_RELOCATION, "validation.field.blank", ["destinationCode"], ["destinationCode"])
        }
        //C1. Check Nullable Fields (eventDate)
        if (isBlankEventDate){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD_RELOCATION, "validation.field.blank", ["eventDate"], ["eventDate"])
        }

        //C4. Check origin Household reference existence
        if (!isBlankOriginCode && !originExists){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD_RELOCATION, "validation.field.reference.error", ["Household", "householdCode", rawHouseholdRelocation.originCode], ["originCode"])
        }
        //C4. Check destination Household reference existence
        if (!isBlankDestinationCode && !destinationExists){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD_RELOCATION, "validation.field.reference.error", ["Household", "householdCode", rawHouseholdRelocation.destinationCode], ["destinationCode"])
        }
        //C4. Check Visit reference existence
        if (!isBlankVisitCode && !visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD_RELOCATION, "validation.field.reference.error", ["Visit", "code", rawHouseholdRelocation.visitCode], ["visitCode"])
        }

        //C5. Check eventDate max date
        if (!isBlankEventDate && rawHouseholdRelocation.eventDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD_RELOCATION, "validation.field.date.not.greater.today", ["householdRelocation.eventDate"], ["eventDate"])
        }

        //Validation part 2
        if (errors.empty){
            //try to create inmigrations of all members
            def head = memberService.getMember(rawHouseholdRelocation.headCode)
            def members = residencyService.getCurrentResidentMembers(originHousehold)
            members.removeAll { it.id == head.id }
            members.add(0, head) //the list will start with the head of household

            members.each { member ->
                def headRelationship = headRelationshipService.getLastHeadRelationship(member, originHousehold)
                def fakeInmigration = createFakeRawInmigration(member, rawHouseholdRelocation, headRelationship.relationshipType)
                //We must reorder this by HEAD first

                def innerErrors = inMigrationService.validate(fakeInmigration)
                errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.householdrelocation.prefix.msg.error", [rawHouseholdRelocation.id])
            }

        }

        return errors
    }
    //</editor-fold>

}
