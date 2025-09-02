package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.DateUtil
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawDeath
import org.philimone.hds.explorer.server.model.collect.raw.RawDeathRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawHeadRelationship
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

import java.time.LocalDate

@Transactional
class DeathService {

    def householdService
    def memberService
    def visitService
    def userService
    def residencyService
    def headRelationshipService
    def maritalRelationshipService
    def coreExtensionService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="Death Utilities Methods">
    boolean isMemberDead(String memberCode) {
        def member = memberService.getMember(memberCode)
        return Death.countByMember(member) > 0
    }

    boolean hasAnyDeathRecord(Member member) {
        return Death.countByMember(member) > 0 || Residency.countByMemberAndEndType(member, ResidencyEndType.DEATH) > 0;
    }

    Death getDeath(String memberCode){
        def member = memberService.getMember(memberCode)

        if (!StringUtil.isBlank(memberCode)){
            return Death.findByMember(member)
        }
        return null
    }

    Death getDeath(Member member){
        return Death.findByMember(member)
    }

    List<RawHeadRelationship> createRawHeadRelationships(List<RawDeathRelationship> deathRelationships) {
        def relationships = new ArrayList<RawHeadRelationship>()

        deathRelationships.each {
            relationships << createRawHeadRelationship(it)
        }

        return relationships
    }

    RawHeadRelationship createRawHeadRelationship(RawDeathRelationship rawDthRel) {

        def rawDeath = rawDthRel.death

        def rawHeadRelationship = new RawHeadRelationship()

        rawHeadRelationship.householdCode = visitService.getHouseholdCode(rawDeath.visitCode)
        rawHeadRelationship.memberCode = rawDthRel.newMemberCode
        rawHeadRelationship.relationshipType = rawDthRel.newRelationshipType
        rawHeadRelationship.startType = HeadRelationshipStartType.NEW_HEAD_OF_HOUSEHOLD.code
        rawHeadRelationship.startDate = rawDeath.deathDate.plusDays(1)
        rawHeadRelationship.endType = HeadRelationshipEndType.NOT_APPLICABLE.code
        rawHeadRelationship.endDate = null

        return rawHeadRelationship
    }

    //</editor-fold>

    //<editor-fold desc="Death Factory/Manager Methods">
    RawExecutionResult<Death> createDeath(RawDeath rawDeath) {

        /* Run Checks and Validations */
        def visit = visitService.getVisit(rawDeath.visitCode)
        def household = visit?.household
        def member = memberService.getMember(rawDeath.memberCode)
        def hasOnlyMinorsLeftInHousehold = residencyService.hasOnlyMinorsLeftInHousehold(household, member)

        def errors = validate(rawDeath, hasOnlyMinorsLeftInHousehold)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Death> obj = RawExecutionResult.newErrorResult(RawEntity.DEATH, errors)
            return obj
        }

        def death = newDeathInstance(rawDeath)

        def resultDeath = death.save(flush:true)
        //Validate using Gorm Validations
        if (death.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.DEATH, death)

            RawExecutionResult<Death> obj = RawExecutionResult.newErrorResult(RawEntity.DEATH, errors)
            return obj
        } else {
            death = resultDeath
        }

        //Update creating Death -
        //1. closeResidency, closeHeadRelationship, closeMaritalRelationship
        //2. Update Member residencyStatus, maritalStatus

        def residency = residencyService.getCurrentResidencyAsRaw(member)
        def headRelationship = headRelationshipService.getLastHeadRelationshipAsRaw(member) //his relationship with the head, even if he is the head
        //def maritalRelationship = maritalRelationshipService.getCurrentMaritalRelationshipAsRaw(member)
        def maritalRelationships = maritalRelationshipService.getCurrentlyMarriedRelationshipsAsRaw(member)
        def isHeadOfHousehold = headRelationship!=null ? (headRelationship.relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD.code) : false
        def isLastMemberOfHousehold = residencyService.hasOneResident(household)
        def headRelationships = headRelationshipService.getCurrentHeadRelationships(member, household)

        /*
        * CHECK VERY CAREFULLY THE RELATIONSHIPS, SOME DONT NEED VALIDATIONS
        * Residency,
        * 1. If we are going to kill someone endType must be NA
        *    Cannot register death, The Member is not currently living in the Household [{0}], the current residency uuid=[{0}] have the endType=[{1}]
        * HeadRelationship, MaritalRelationship
        * 2. Find Opened HeadRelationships and try to close then
        * */

        Residency closedResidency = null
        HeadRelationship closedHeadRelationship = null
        List<MaritalRelationship> closedMaritalRelationships = []
        List<HeadRelationship> closedHeadRelationships = []
        List<HeadRelationship> createdHeadRelationships = []

        // Closing the Residency with Death
        if (residency != null && residency.endType == ResidencyEndType.NOT_APPLICABLE.code){ //must be opened
            residency.endType = ResidencyEndType.DEATH.code
            residency.endDate = death.deathDate
            def result = residencyService.closeResidency(residency)
            closedResidency = result.domainInstance
            errors += result.errorMessages
        }

        // Closing the HeadRelationship with Death
        if (headRelationship != null && headRelationship.endType == HeadRelationshipEndType.NOT_APPLICABLE.code){ //must be opened
            headRelationship.endType = HeadRelationshipEndType.DEATH.code
            headRelationship.endDate = death.deathDate
            def result = headRelationshipService.closeHeadRelationship(headRelationship)
            closedHeadRelationship = result.domainInstance
            errors += result.errorMessages
        }

        // Closing MaritalRelationship with WIDOWED
        maritalRelationships.each { maritalRelationship ->
            if (maritalRelationship != null){
                //println "member[${death.memberCode}] is dead, we are dealing with his maritalrelationship endtype=${maritalRelationship.endStatus}"
                if (maritalRelationship.endStatus == MaritalEndStatus.NOT_APPLICABLE.code){
                    maritalRelationship.endStatus = MaritalEndStatus.WIDOWED.code
                    maritalRelationship.endDate = death.deathDate

                    def result = maritalRelationshipService.closeMaritalRelationship(maritalRelationship)
                    if (result.domainInstance != null) closedMaritalRelationships.add(result.domainInstance)
                    errors += result.errorMessages

                }
            }
        }

        // Closing Head Relationships with Members of the Household (dead Member as Head-Of-Household)
        if (isHeadOfHousehold && (headRelationships != null && headRelationships.size()>0)){
            headRelationships.each { hr ->

                //ignore the head of household - its already closed
                if (hr.id?.equals(headRelationship?.id)) {
                    return
                }

                if (hr.endType == HeadRelationshipEndType.NOT_APPLICABLE){ //opened relationship
                    def rawHr = headRelationshipService.convertToRaw(hr)
                    rawHr.endType = HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD.code
                    rawHr.endDate = death.deathDate
                    def result = headRelationshipService.closeHeadRelationship(rawHr)
                    if (result.domainInstance != null) closedHeadRelationships.add(result.domainInstance)
                    errors += result.errorMessages
                }
            }
        }

        //Create new Head Relationships
        def rawDeathRelationships = RawDeathRelationship.findAllByDeath(rawDeath);
        if (isHeadOfHousehold && rawDeathRelationships.size()>0) {
            def newRelationships = createRawHeadRelationships(rawDeathRelationships)
            newRelationships.each {
                def result = headRelationshipService.createHeadRelationship(it, hasOnlyMinorsLeftInHousehold)
                if (result.domainInstance != null) createdHeadRelationships.add(result.domainInstance)

                if (result.status==RawExecutionResult.Status.ERROR) {
                    def innerErrors = result.errorMessages
                    errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.death.prefix.msg.error", [rawDeath.id])
                }
            }
        }

        if (!errors.isEmpty()) {
            //Roolback data
            deleteAllCreatedRecords(closedResidency, closedHeadRelationship, closedMaritalRelationships, closedHeadRelationships, createdHeadRelationships)

            //1. Delete Death
            if (death != null) {
                death.delete(flush: true)
            }

            //create result and close
            RawExecutionResult<Death> obj = RawExecutionResult.newErrorResult(RawEntity.DEATH, errors)
            return obj
        }

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertDeathExtension(rawDeath, resultDeath)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail

            deleteAllCreatedRecords(closedResidency, closedHeadRelationship, closedMaritalRelationships, closedHeadRelationships, createdHeadRelationships)
            death.delete(flush: true)

            println "Failed to insert extension: ${resultExtension.errorMessage}"

            errors << new RawMessage(resultExtension.errorMessage, null)
            RawExecutionResult<Death> obj = RawExecutionResult.newErrorResult(RawEntity.DEATH, errors)
            return obj
        }

        //Set Vacant if no members in the household
        householdService.setHouseholdStatusVacant(household)

        RawExecutionResult<Death> obj = RawExecutionResult.newSuccessResult(RawEntity.DEATH, death, errors)
        return obj
    }

    private void deleteAllCreatedRecords(Residency closedResidency, HeadRelationship closedHeadRelationship, ArrayList<MaritalRelationship> closedMaritalRelationships, ArrayList<HeadRelationship> closedHeadRelationships, ArrayList<HeadRelationship> createdHeadRelationships) {
        //Roolback
        //Residency closedResidency = null
        if (closedResidency != null) {
            closedResidency.endType = ResidencyEndType.NOT_APPLICABLE
            closedResidency.endDate = null
            closedResidency.save(flush: true)
        }
        //HeadRelationship closedHeadRelationship = null
        if (closedHeadRelationship != null) {
            closedHeadRelationship.endType = HeadRelationshipEndType.NOT_APPLICABLE
            closedHeadRelationship.endDate = null
            closedHeadRelationship.save(flush: true)
        }
        //List<MaritalRelationship> closedMaritalRelationships = []
        closedMaritalRelationships.each { closed ->
            closed.endStatus = MaritalEndStatus.NOT_APPLICABLE
            closed.endDate = null
            closed.save(flush: true)
        }
        //List<HeadRelationship> closedHeadRelationships = []
        closedHeadRelationships.each { closed ->
            closed.endType = HeadRelationshipEndType.NOT_APPLICABLE
            closed.endDate = null
            closed.save(flush: true)
        }
        //List<HeadRelationship> createdHeadRelationships = []
        createdHeadRelationships.each {
            it.delete(flush: true)
        }
    }

    ArrayList<RawMessage> validate(RawDeath rawDeath, def hasOnlyMinorsLeftInHousehold = false){
        def errors = new ArrayList<RawMessage>()

        //visitCode, memberCode, deathDate, deathCause, deathPlace
        def isBlankVisitCode = StringUtil.isBlank(rawDeath.visitCode)
        def isBlankMemberCode = StringUtil.isBlank(rawDeath.memberCode)
        def isBlankDeathDate = StringUtil.isBlankDate(rawDeath.deathDate)
        def isBlankDeathCause = StringUtil.isBlank(rawDeath.deathCause)
        def isBlankDeathPlace = StringUtil.isBlank(rawDeath.deathPlace)
        def isBlankCollectedBy = StringUtil.isBlank(rawDeath.collectedBy)

        def member = memberService.getMember(rawDeath.memberCode)
        def visit = visitService.getVisit(rawDeath.visitCode)
        def memberExists = member != null
        def visitExists = visit != null

        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Nullable Fields (deathDate)
        if (isBlankDeathDate){
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.blank", ["deathDate"], ["deathDate"])
        }
        //C1. Check Blank Fields (deathCause)
        if (isBlankDeathCause){
            //errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.blank", ["deathCause"], ["deathCause"])
        }
        //C1. Check Blank Fields (deathPlace)
        if (isBlankDeathPlace){
            //errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.blank", ["deathPlace"], ["deathPlace"])
        }

        //C2. Check Visit reference existence
        if (!visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.reference.error", ["Visit", "code", rawDeath.visitCode], ["visitCode"])
        }
        //C2. Check Member reference existence
        if (!memberExists){
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.reference.error", ["Member", "code", rawDeath.memberCode], ["memberCode"])
        }

        //C3. Check DeathDate against maxDate
        if (!isBlankDeathDate && rawDeath.deathDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.date.not.greater.today", ["deathDate"], ["deathDate"])
        }
        //C4. Check DeathDate against dateOfBirth
        if (!isBlankDeathDate && memberExists && rawDeath.deathDate < member.dob){
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.death.dob.not.greater.date", [DateUtil.getInstance().formatYMD(rawDeath.deathDate)], ["dob"])
        }

        //CV. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(rawDeath.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.user.dont.exists.error", [rawDeath.collectedBy], ["collectedBy"])
        }

        //C5. Check Member Death Status
        if (memberExists && isMemberDead(rawDeath.memberCode)){
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.death.exists.error", [rawDeath.memberCode], ["memberCode"])
        }


        //Validate associated services (Residency,HeadRelationship,MaritalRelationship) - get current first
        if (errors.isEmpty()){

            def residency = residencyService.getCurrentResidencyAsRaw(member)
            def headRelationship = headRelationshipService.getLastHeadRelationshipAsRaw(member) //his relationship with the head, even if he is the head
            //def maritalRelationship = maritalRelationshipService.getCurrentMaritalRelationshipAsRaw(member)
            def maritalRelationships = maritalRelationshipService.getCurrentlyMarriedRelationshipsAsRaw(member)
            def isHeadOfHousehold = headRelationship!=null ? (headRelationship.relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD.code) : false
            def headRelationships = headRelationshipService.getCurrentHeadRelationships(rawDeath.memberCode, member?.household?.code)

            /*
            * CHECK VERY CAREFULLY THE RELATIONSHIPS, SOME DONT NEED VALIDATIONS
            * Residency,
            * 1. If we are going to kill someone endType must be NA
            *    Cannot register death, The Member is not currently living in the Household [{0}], the current residency uuid=[{0}] have the endType=[{1}]
            * HeadRelationship, MaritalRelationship
            * 2. Find Opened HeadRelationships and try to close then
            * */

            /* Member must be Living in current Household */
            if (residency != null){

                if (residency.endType != ResidencyEndType.NOT_APPLICABLE.code) {
                    errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.death.opened.residency.error", [residency.householdCode, residency.id, residency.endType], ["householdCode","memberCode"])
                    return errors
                }

                //Test Closing the Residency with Death
                //simulate DTH
                residency.endType = ResidencyEndType.DEATH.code
                residency.endDate = rawDeath.deathDate

                errors += residencyService.validateCloseResidency(residency)
            }

            // HeadRelationship as Member who relates with Head of Household
            if (headRelationship != null && headRelationship.endType == HeadRelationshipEndType.NOT_APPLICABLE.code){ //must be opened

                //simulate DTH
                headRelationship.endType = HeadRelationshipEndType.DEATH.code
                headRelationship.endDate = rawDeath.deathDate

                errors += headRelationshipService.validateCloseHeadRelationship(headRelationship)
            }

            //MaritalRelationship - support multiple relationships
            //if (maritalRelationship != null && maritalRelationship.endStatus == MaritalEndStatus.NOT_APPLICABLE.code){
            maritalRelationships.each { maritalRelationship ->
                //simulate DTH
                maritalRelationship.endStatus = MaritalEndStatus.WIDOWED.code
                maritalRelationship.endDate = rawDeath.deathDate

                errors += maritalRelationshipService.validateCloseMaritalRelationship(maritalRelationship)
            }

            //Other opened relationships if he is head of household
            if (isHeadOfHousehold && (headRelationships != null && headRelationships.size()>0)){

                headRelationships.each { hr ->
                    if (hr.endType == HeadRelationshipEndType.NOT_APPLICABLE){ //opened relationship
                        def rawHr = headRelationshipService.convertToRaw(hr)
                        rawHr.endType = HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD.code
                        rawHr.endDate = rawDeath.deathDate

                        errors += headRelationshipService.validateCloseHeadRelationship(rawHr)
                    }
                }
            }

            //try to create new relationships with the new head
            def newRawDeathRelationships = RawDeathRelationship.findAllByDeath(rawDeath)

            if (newRawDeathRelationships.size() > 0) {

                def fakeHeadOfHouseholdRelationship = headRelationshipService.createFakeHeadRelationshipFromRaw(headRelationship) //if has rawDeathRelationships is a Head of Household, so will use the closed Head

                for (def rawDeathRelationship : newRawDeathRelationships) {

                    /*// We must test the head relationship too
                    if (rawDeathRelationship.newRelationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD.code) {
                        return
                    }*/

                    def rawHeadRelationship = createRawHeadRelationship(rawDeathRelationship)
                    def rawCurrentRelationship = headRelationshipService.getLastHeadRelationshipAsRaw(rawHeadRelationship.memberCode) //get fake current head relationship for this member (close it)

                    if (rawCurrentRelationship != null) {
                        def fakeCurrentRelationship = headRelationshipService.createFakeHeadRelationshipFromRaw(rawCurrentRelationship)
                        fakeCurrentRelationship.endType = HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD
                        fakeCurrentRelationship.endDate = rawDeath.deathDate

                        //ignore head of households (its unusual to have relationshipType=HEAD here)
                        def innerErrors = headRelationshipService.validateCreateHeadRelationship(rawHeadRelationship, fakeCurrentRelationship, fakeHeadOfHouseholdRelationship, hasOnlyMinorsLeftInHousehold)
                        errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.death.prefix.msg.error", [rawDeath.id])

                        //If no ERRORS and we were dealing with the NEW HEAD OF HOUSEHOLD, we will update the fakeHeadOfHouseholdRelationship with the recently validated rawHeadRelationship
                        if (innerErrors.size() == 0 && rawHeadRelationship.relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD.code){
                            //set new fake head of household
                            fakeHeadOfHouseholdRelationship = headRelationshipService.createFakeHeadRelationshipFromRaw(rawHeadRelationship)
                        }

                        if (innerErrors.size() > 0) {
                            break //one error its enough to invalidate the record and avoid a huge message error
                        }
                    }
                }
            }

        }

        return errors
    }

    private Death newDeathInstance(RawDeath rd){

        def member = memberService.getMember(rd.memberCode)
        def visit = visitService.getVisit(rd.visitCode)

        Death death = new Death()

        death.member = member
        death.memberCode = member.code

        death.visit = visit
        death.visitCode = visit.code

        death.deathDate = rd.deathDate
        death.ageAtDeath = GeneralUtil.getAge(member.dob, death.deathDate)
        death.ageDaysAtDeath = GeneralUtil.getAgeInDays(member.dob, death.deathDate)

        death.deathCause = rd.deathCause
        death.deathPlace = rd.deathPlace

        //set collected by info
        death.collectedId = rd.id
        death.collectedDeviceId = rd.collectedDeviceId
        death.collectedHouseholdId = rd.collectedHouseholdId
        death.collectedMemberId = rd.collectedMemberId
        death.collectedBy = userService.getUser(rd.collectedBy)
        death.collectedStart = rd.collectedStart
        death.collectedEnd = rd.collectedEnd
        death.collectedDate = rd.collectedDate
        death.updatedDate = rd.uploadedDate

        return death

    }
    //</editor-fold>
}
