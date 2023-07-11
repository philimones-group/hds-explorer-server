package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawChangeHeadRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawDeath
import org.philimone.hds.explorer.server.model.collect.raw.RawDeathRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawHeadRelationship
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStartStatus
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
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="Death Utilities Methods">
    boolean isMemberDead(String memberCode) {
        def member = memberService.getMember(memberCode)
        return Death.countByMember(member) > 0
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

    List<RawMessage> afterDeathRegistered(Death death, RawDeath rawDeath){
        //1. closeResidency, closeHeadRelationship, closeMaritalRelationship
        //2. Update Member residencyStatus, maritalStatus

        def errors = [] as ArrayList<RawMessage>

        def member = death.member
        def household = residencyService.getCurrentHousehold(member)

        def residency = residencyService.getCurrentResidencyAsRaw(member)
        def headRelationship = headRelationshipService.getCurrentHeadRelationshipAsRaw(member) //his relationship with the head, even if he is the head
        def maritalRelationship = maritalRelationshipService.getCurrentMaritalRelationshipAsRaw(member)
        def isHeadOfHousehold = headRelationship!=null ? (headRelationship.relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD.code) : false
        def headRelationships = headRelationshipService.getCurrentHeadRelationships(member, household)

        /*
        * CHECK VERY CAREFULLY THE RELATIONSHIPS, SOME DONT NEED VALIDATIONS
        * Residency,
        * 1. If we are going to kill someone endType must be NA
        *    Cannot register death, The Member is not currently living in the Household [{0}], the current residency uuid=[{0}] have the endType=[{1}]
        * HeadRelationship, MaritalRelationship
        * 2. Find Opened HeadRelationships and try to close then
        * */

        // Closing the Residency with Death
        if (residency != null && residency.endType == ResidencyEndType.NOT_APPLICABLE.code){ //must be opened
            residency.endType = ResidencyEndType.DEATH.code
            residency.endDate = death.deathDate
            def result = residencyService.closeResidency(residency)
            errors += result.errorMessages
        }

        // Closing the HeadRelationship with Death
        if (headRelationship != null && headRelationship.endType == HeadRelationshipEndType.NOT_APPLICABLE.code){ //must be opened
            headRelationship.endType = HeadRelationshipEndType.DEATH.code
            headRelationship.endDate = death.deathDate
            def result = headRelationshipService.closeHeadRelationship(headRelationship)
            errors += result.errorMessages
        }

        // Closing MaritalRelationship with WIDOWED
        if (maritalRelationship != null){
            println "member[${death.memberCode}] is dead, we are dealing with his maritalrelationship endtype=${maritalRelationship.endStatus}"
            if (maritalRelationship.endStatus == MaritalEndStatus.NOT_APPLICABLE.code){
                maritalRelationship.endStatus = MaritalEndStatus.WIDOWED.code
                maritalRelationship.endDate = death.deathDate

                def result = maritalRelationshipService.closeMaritalRelationship(maritalRelationship)
                errors += result.errorMessages

            } else if (maritalRelationship.endStatus == MaritalEndStatus.WIDOWED.code){ //one of them already died
                //the idea is to set the correct members maritalStatus

                def memberA = maritalRelationship.memberA==member.code ? member : memberService.getMember(maritalRelationship.memberA)
                def memberB = maritalRelationship.memberB==member.code ? member : memberService.getMember(maritalRelationship.memberB)
                def deathA = getDeath(memberA)
                def deathB = getDeath(memberB)
                //println "member[${death.memberCode}] relationship already closed! mantain old status[${maritalRelationship.startStatus}] = ${(deathA != null && deathB != null)}, ${(GeneralUtil.dateEquals(deathA.deathDate, deathB.deathDate))}, ${GeneralUtil.dateEquals(maritalRelationship.endDate, deathA.deathDate)}"
                //println "ma=${memberA.code},mb=${memberB.code}, dates, d.a.date=${StringUtil.format(deathA.deathDate,true)}, d.b.date=${StringUtil.format(deathB.deathDate,true)}, mr.e.date=${StringUtil.format(maritalRelationship.endDate,true)}"
                //println "ma=${memberA.code},mb=${memberB.code}, dates, d.a.date=${deathA.deathDate.getTime()}, d.b.date=${deathB.deathDate.getTime()}, mr.e.date=${StringUtil.format(maritalRelationship.endDate)}"

                if ((deathA != null && deathB != null) && (deathA.deathDate == deathB.deathDate) && (maritalRelationship.endDate == deathA.deathDate)) { //both died in the same die while in sort of a relationship
                    def startStatus = MaritalStartStatus.getFrom(maritalRelationship.startStatus)
                    def marStatus = maritalRelationshipService.convertFrom(startStatus)

                    //set the maritalStatus as the same when they started the relationship, so that we preserve the last maritalStatus before they death (it cant be WID - because they died in the same date)
                    memberA.maritalStatus = marStatus
                    memberB.maritalStatus = marStatus
                    memberA.save(flush:true)
                    memberB.save(flush:true)
                }
            }
        }

        // Closing Head Relationships with Members of the Household (dead Member as Head-Of-Household)
        if (isHeadOfHousehold && (headRelationships != null && headRelationships.size()>0)){
            headRelationships.each { hr ->
                if (hr.endType == HeadRelationshipEndType.NOT_APPLICABLE){ //opened relationship
                    def rawHr = headRelationshipService.convertToRaw(hr)
                    rawHr.endType = HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD.code
                    rawHr.endDate = death.deathDate
                    def result = headRelationshipService.closeHeadRelationship(rawHr)
                    errors += result.errorMessages
                }
            }
        }

        //Create new Head Relationships
        def rawDeathRelationships = RawDeathRelationship.findAllByDeath(rawDeath)
        if (isHeadOfHousehold && rawDeathRelationships.size()>0) {
            def newRelationships = createRawHeadRelationships(rawDeathRelationships)

            newRelationships.each {
                def result = headRelationshipService.createHeadRelationship(it)

                if (result.status==RawExecutionResult.Status.ERROR) {
                    def innerErrors = result.errorMessages
                    errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.death.prefix.msg.error", [rawDeath.id])
                }
            }
        }

        return errors

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

        rawHeadRelationship.householdCode = rawDeath.visitCode.substring(0,9)
        rawHeadRelationship.memberCode = rawDthRel.newMemberCode
        rawHeadRelationship.relationshipType = rawDthRel.newRelationshipType
        rawHeadRelationship.startType = HeadRelationshipStartType.NEW_HEAD_OF_HOUSEHOLD.code
        rawHeadRelationship.startDate = rawDeath.deathDate

        return rawHeadRelationship
    }

    //</editor-fold>

    //<editor-fold desc="Death Factory/Manager Methods">
    RawExecutionResult<Death> createDeath(RawDeath rawDeath) {

        /* Run Checks and Validations */

        def errors = validate(rawDeath)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Death> obj = RawExecutionResult.newErrorResult(RawEntity.DEATH, errors)
            return obj
        }

        def death = newDeathInstance(rawDeath)

        def result = death.save(flush:true)
        //Validate using Gorm Validations
        if (death.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.DEATH, death)

            RawExecutionResult<Death> obj = RawExecutionResult.newErrorResult(RawEntity.DEATH, errors)
            return obj
        } else {
            death = result
        }

        //Update After Death -
        //1. closeResidency, closeHeadRelationship, closeMaritalRelationship
        //2. Update Member residencyStatus, maritalStatus

        errors = afterDeathRegistered(death, rawDeath)

        RawExecutionResult<Death> obj = RawExecutionResult.newSuccessResult(RawEntity.DEATH, death, errors)
        return obj
    }

    ArrayList<RawMessage> validate(RawDeath rawDeath){
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
            errors << errorMessageService.getRawMessage(RawEntity.DEATH, "validation.field.death.dob.not.greater.date", [StringUtil.format(rawDeath.deathDate)], ["dob"])
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
            def headRelationship = headRelationshipService.getCurrentHeadRelationshipAsRaw(member) //his relationship with the head, even if he is the head
            def maritalRelationship = maritalRelationshipService.getCurrentMaritalRelationshipAsRaw(member)
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

            //MaritalRelationship
            if (maritalRelationship != null && maritalRelationship.endStatus == MaritalEndStatus.NOT_APPLICABLE.code){
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
            if (newRawDeathRelationships.size() > 0)
            newRawDeathRelationships.each { rawDeathRelationship ->

                if (rawDeathRelationship.newRelationshipType==HeadRelationshipType.HEAD_OF_HOUSEHOLD.code) return

                def rawHeadRelationship = createRawHeadRelationship(rawDeathRelationship)
                def currentRelationship = headRelationshipService.getCurrentHeadRelationship(rawHeadRelationship.memberCode) //get fake current head relationship for this member (close it)
                currentRelationship.endType = HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD
                currentRelationship.endDate = rawDeath.deathDate

                //ignore head of households (its unusual to have relationshipType=HEAD here)
                def innerErrors = headRelationshipService.validateCreateHeadRelationship(rawHeadRelationship, currentRelationship, null)
                errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.death.prefix.msg.error", [rawDeath.id])
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
