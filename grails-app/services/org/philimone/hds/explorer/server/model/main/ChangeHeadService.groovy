package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawChangeHead
import org.philimone.hds.explorer.server.model.collect.raw.RawChangeHeadRelationship
import org.philimone.hds.explorer.server.model.collect.raw.RawHeadRelationship
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipStartType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDate

@Transactional
class ChangeHeadService {

    /**
     * Validate data columns
     * 1. Close old Head HeadRelationship
     * 2. Close all old Head Relationship
     * 3. Create new Head HeadRelationship
     * 4. Create new HeadRelationship for all members of Household (living members - not deaths and outmigrations)
     */

    def householdService
    def memberService
    def headRelationshipService
    def visitService
    def userService
    def errorMessageService

    //<editor-fold desc="HeadRelationship Utilities Methods">

    RawHeadRelationship createRawHeadOfHousehold(RawChangeHead rawChangeHead) {
        def rawHeadRelationship = new RawHeadRelationship()

        rawHeadRelationship.householdCode = rawChangeHead.householdCode
        rawHeadRelationship.memberCode = rawChangeHead.newHeadCode
        rawHeadRelationship.relationshipType = HeadRelationshipType.HEAD_OF_HOUSEHOLD.code
        rawHeadRelationship.startType = HeadRelationshipStartType.NEW_HEAD_OF_HOUSEHOLD.code
        rawHeadRelationship.startDate = rawChangeHead.eventDate

        return rawHeadRelationship
    }

    RawHeadRelationship createRawHeadRelationship(RawChangeHeadRelationship rawChr) {
        def rawHeadRelationship = new RawHeadRelationship()

        def rawChangeHead = rawChr.changeHead

        rawHeadRelationship.householdCode = rawChangeHead.householdCode
        rawHeadRelationship.memberCode = rawChr.newMemberCode
        rawHeadRelationship.relationshipType = rawChr.newRelationshipType
        rawHeadRelationship.startType = HeadRelationshipStartType.NEW_HEAD_OF_HOUSEHOLD.code
        rawHeadRelationship.startDate = rawChangeHead.eventDate

        return rawHeadRelationship
    }

    List<RawHeadRelationship> createRawHeadRelationships(List<RawChangeHeadRelationship> changeHeadRelationships) {
        def relationships = new ArrayList<RawHeadRelationship>()

        changeHeadRelationships.each {
            relationships << createRawHeadRelationship(it)
        }

        return relationships
    }

    HeadRelationship createFakeClosedHeadRelationship(HeadRelationship headRelationship, HeadRelationshipEndType endType, LocalDate endDate) {
        def closedHeadRelationship = new HeadRelationship()
        closedHeadRelationship.member = headRelationship.member
        closedHeadRelationship.memberCode = headRelationship.member?.code
        closedHeadRelationship.head = headRelationship.head
        closedHeadRelationship.headCode = headRelationship.head?.code
        closedHeadRelationship.household = headRelationship.household
        closedHeadRelationship.householdCode = headRelationship.household?.code
        closedHeadRelationship.relationshipType = headRelationship.relationshipType
        closedHeadRelationship.startType = headRelationship.startType
        closedHeadRelationship.startDate = headRelationship.startDate
        closedHeadRelationship.endType = endType
        closedHeadRelationship.endDate = endDate

        return closedHeadRelationship
    }

    RawHeadRelationship createClosedRawHeadRelationship(HeadRelationship headRelationship, HeadRelationshipEndType endType, LocalDate endDate) {

        if (headRelationship.endType != HeadRelationshipEndType.NOT_APPLICABLE) return null //its already closed

        def rawHeadRelationship = new RawHeadRelationship()

        rawHeadRelationship.memberCode = headRelationship.member?.code
        rawHeadRelationship.householdCode = headRelationship.household?.code
        rawHeadRelationship.relationshipType = headRelationship.relationshipType.code
        rawHeadRelationship.startType = headRelationship.startType.code
        rawHeadRelationship.startDate = headRelationship.startDate
        rawHeadRelationship.endType = endType.code
        rawHeadRelationship.endDate = endDate

        return rawHeadRelationship
    }

    List<RawHeadRelationship> createClosedRawHeadRelationships(List<HeadRelationship> headRelationships, HeadRelationshipEndType endType, LocalDate endDate) {
        def relationships = new ArrayList<RawHeadRelationship>()

        headRelationships.each {
            if (it.endType == HeadRelationshipEndType.NOT_APPLICABLE) { //create if its not closed
                relationships << createClosedRawHeadRelationship(it, endType, endDate)
            }
        }

        return relationships
    }

    //</editor-fold>

    //<editor-fold desc="HeadRelationship Factory/Manager Methods">

    RawExecutionResult<HeadRelationship> createChangeHead(RawChangeHead changeHead, List<RawChangeHeadRelationship> changeHeadRelationships) {
        /* Run Checks and Validations */

        def errors = validate(changeHead, changeHeadRelationships)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<HeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, errors)
            return obj
        }

        //get current data
        def household = householdService.getHousehold(changeHead.householdCode)
        def currentHeadOfHousehold = memberService.getMember(changeHead.oldHeadCode)
        //def currentHeadRelationship = headRelationshipService.getCurrentHouseholdHead(household) //the list below contains this relationship
        def currentHeadRelationships = headRelationshipService.getCurrentHeadRelationships(currentHeadOfHousehold, household)
        def eventDate = GeneralUtil.addDaysToDate(changeHead.eventDate, -1)  //the day of moving will be set 1 day before changing head - the last day the member was related to the current head of household

        //remove the head of household relationship from the list -

        //close previous relationship if needed
        //def closedCurrentRawHeadRelationship = createClosedRawHeadRelationship(currentHeadRelationship, HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD, eventDate)
        def closedCurrentRawHeadRelationships = createClosedRawHeadRelationships(currentHeadRelationships, HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD, eventDate)
        //create new relationships
        def newRawHeadRelationship = createRawHeadOfHousehold(changeHead) //execute this
        def newRawHeadRelationships = createRawHeadRelationships(changeHeadRelationships) //execute this

        HeadRelationship resultHeadRelationship = null

        //execute everything inside a transaction to be able to roolback in case of any error
        HeadRelationship.withTransaction { status ->

            //try to close the current head of household
            /*
            if (closedCurrentRawHeadRelationship != null) {
                def result = headRelationshipService.closeHeadRelationship(closedCurrentRawHeadRelationship)

                if (result.status==RawExecutionResult.Status.ERROR) {
                    def innerErrors = result.errorMessages
                    errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.changehead.prefix.msg.error", [changeHead.id])
                }

            }*/

            //try to close all relationships
            closedCurrentRawHeadRelationships.each { closedHeadRelationship ->
                def result = headRelationshipService.closeHeadRelationship(closedHeadRelationship)

                if (result.status==RawExecutionResult.Status.ERROR) {
                    def innerErrors = result.errorMessages
                    errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.changehead.prefix.msg.error", [changeHead.id])
                }
            }

            //create new Head Relationship - set new Head of Household
            if (errors.empty) {
                def result = headRelationshipService.createHeadRelationship(newRawHeadRelationship)

                if (result.status==RawExecutionResult.Status.ERROR) {
                    def innerErrors = result.errorMessages
                    errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.changehead.prefix.msg.error", [changeHead.id])
                } else {
                    resultHeadRelationship = result.domainInstance //result of the operation - the new head of household
                }
            }

            //create relationships with the new head of household
            if (errors.empty) {
                newRawHeadRelationships.each {
                    def result = headRelationshipService.createHeadRelationship(it)

                    if (result.status==RawExecutionResult.Status.ERROR) {
                        def innerErrors = result.errorMessages
                        errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.changehead.prefix.msg.error", [changeHead.id])
                    }
                }
            }

            //Roolback everything if an error ocurred
            if (!errors.empty) {
                resultHeadRelationship = null
                status.setRollbackOnly()
            }
        }


        if (resultHeadRelationship == null || !errors.empty){
            RawExecutionResult<HeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, errors)
            return obj
        }

        RawExecutionResult<HeadRelationship> obj = RawExecutionResult.newSuccessResult(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, resultHeadRelationship, errors)
        return obj
    }

    ArrayList<RawMessage> validate(RawChangeHead changeHead, List<RawChangeHeadRelationship> headRelationships) {

        //visitCode - must exists
        //householdCode - must exists
        //oldHeadCode - must be the current household head
        //newHeadCode - must not be head of any household
        //eventDate
        //reason

        def errors = new ArrayList<RawMessage>()

        def isBlankVisitCode = StringUtil.isBlank(changeHead.visitCode)
        def isBlankHouseholdCode = StringUtil.isBlank(changeHead.householdCode)
        def isBlankOldHeadCode = StringUtil.isBlank(changeHead.oldHeadCode)
        def isBlankNewHeadCode = StringUtil.isBlank(changeHead.newHeadCode)
        def isBlankEventDate = StringUtil.isBlankDate(changeHead.eventDate)


        def household = !isBlankHouseholdCode ? householdService.getHousehold(changeHead.householdCode) : null
        def oldHead = !isBlankOldHeadCode ? memberService.getMember(changeHead.oldHeadCode) : null
        def newHead = !isBlankNewHeadCode ? memberService.getMember(changeHead.newHeadCode) : null
        def visit = visitService.getVisit(changeHead.visitCode)

        def householdExists = household != null
        def oldHeadExists = oldHead != null
        def newHeadExists = newHead != null
        def visitExists = visit != null

        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (oldHeadCode)
        if (isBlankOldHeadCode){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.blank", ["oldHeadCode"], ["oldHeadCode"])
        }
        //C1. Check Blank Fields (newHeadCode)
        if (isBlankNewHeadCode){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.blank", ["newHeadCode"], ["newHeadCode"])
        }
        //C1. Check Nullable Fields (eventDate)
        if (isBlankEventDate){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.blank", ["eventDate"], ["eventDate"])
        }
        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.blank", ["visitCode"], ["visitCode"])
        }

        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.reference.error", ["Household", "householdCode", changeHead.householdCode], ["householdCode"])
        }
        //C4. Check OldHead Member reference existence
        if (!isBlankOldHeadCode && !oldHeadExists){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.reference.error", ["Member", "memberCode", changeHead.oldHeadCode], ["oldHeadCode"])
        }
        //C4. Check NewHead Member reference existence
        if (!isBlankNewHeadCode && !newHeadExists){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.reference.error", ["Member", "memberCode", changeHead.newHeadCode], ["newHeadCode"])
        }

        //C5. Check eventDate max date
        if (!isBlankEventDate && changeHead.eventDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.date.not.greater.today", ["changeHead.eventDate"], ["eventDate"])
        }
        //C5.2. Check eventDate Dates against DOB (for the new head of household)
        if (!isBlankEventDate && newHeadExists && changeHead.eventDate < newHead.dob){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.dob.not.greater.date", ["changeHead.eventDate", StringUtil.format(newHead.dob)], ["eventDate","member.dob"])
        }
        //C6. Check Age of the new head of Household
        if (newHeadExists && GeneralUtil.getAge(newHead.dob) < Codes.MIN_HEAD_AGE_VALUE){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.dob.head.minage.error", [StringUtil.format(newHead.dob), Codes.MIN_HEAD_AGE_VALUE+""], ["member.dob"])
        }

        //C7. Check If the Current Head is the Old Head
        if (oldHeadExists && !headRelationshipService.isCurrentHeadOfHousehold(household, oldHead) ){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.changehead.not.current.head.error", [changeHead.oldHeadCode, changeHead.householdCode], ["oldHeadCode"])
        }

        //C8. Check If the New Head of Household is a Head somewhere else
        if (newHeadExists && headRelationshipService.isHeadOfHousehold(newHead)) {
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.changehead.new.head.invalid.error", [changeHead.newHeadCode], ["newHeadCode"])
        }

        //C9. Check oldHeadCode and newHeadCode equality
        if (!isBlankOldHeadCode && !isBlankNewHeadCode && changeHead.oldHeadCode.equalsIgnoreCase(changeHead.newHeadCode)) {
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, "validation.field.changehead.head.same.member.error", [changeHead.oldHeadCode], ["newHeadCode", "oldHeadCode"])
        }

        //Validation part 2
        if (errors.empty){


            //try to create new head relationship
            //try to close previous head if needed
            //try to close previous head relationships
            //

            def newHeadCurrentRelationship = headRelationshipService.getCurrentHeadRelationship(newHead)
            def currentHead = headRelationshipService.getCurrentHouseholdHead(household)
            def currentHeadRelationships = headRelationshipService.getCurrentHeadRelationships(oldHead, household)
            def eventDate = GeneralUtil.addDaysToDate(changeHead.eventDate, -1)  //the day of moving will be set 1 day before changing head - the last day the member was related to the current head of household

            if (newHeadCurrentRelationship != null) {

                return errors
            }

            if (currentHead != null) {

                return errors
            }

            boolean isNewHeadOpened = newHeadCurrentRelationship.endType==HeadRelationshipEndType.NOT_APPLICABLE && newHeadCurrentRelationship.household?.id==currentHead.household?.id

            def newRawHouseholdHeadRelationship = createRawHeadOfHousehold(changeHead)
            def fakePreviousOldHeadRelationship = createFakeClosedHeadRelationship(currentHead, HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD, eventDate)
            def fakePreviousNewHeadRelationship = isNewHeadOpened ? createFakeClosedHeadRelationship(newHeadCurrentRelationship, HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD, eventDate) : newHeadCurrentRelationship

            //try to close the current head of household
            if (currentHead.endType == HeadRelationshipEndType.NOT_APPLICABLE) {
                //try to close

                def rawCloseOldHeadRelationship = headRelationshipService.convertToRaw(fakePreviousOldHeadRelationship)

                def innerErrors = headRelationshipService.validateCloseHeadRelationship(rawCloseOldHeadRelationship)
                errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.changehead.prefix.msg.error", [changeHead.id])
            }

            //try to close all relationships
            currentHeadRelationships.each { headRelat ->
                if (headRelat.endType == HeadRelationshipEndType.NOT_APPLICABLE) {
                    def closeHeadRelat = createFakeClosedHeadRelationship(headRelat, HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD, eventDate)
                    def rawCloseHeadRelat = headRelationshipService.convertToRaw(closeHeadRelat)

                    def innerErrors = headRelationshipService.validateCloseHeadRelationship(rawCloseHeadRelat)
                    errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.changehead.prefix.msg.error", [changeHead.id])
                }
            }

            //try to create new head relationship
            if (errors.empty) {
                def innerErrors = headRelationshipService.validateCreateHeadRelationship(newRawHouseholdHeadRelationship, fakePreviousNewHeadRelationship, fakePreviousOldHeadRelationship)
                errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.changehead.prefix.msg.error", [changeHead.id])
            }

            //try to create new relationships with the new head
            headRelationships.each { rawChangeHeadRelationship ->

                if (rawChangeHeadRelationship.newRelationshipType==HeadRelationshipType.HEAD_OF_HOUSEHOLD.code) return

                def rawHeadRelationship = createRawHeadRelationship(rawChangeHeadRelationship)
                def currentRelationship = headRelationshipService.getCurrentHeadRelationship(rawHeadRelationship.memberCode) //get fake current head relationship for this member (close it)
                currentRelationship.endType = HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD.code
                currentRelationship.endDate = eventDate

                //ignore head of households (its unusual to have relationshipType=HEAD here)
                def innerErrors = headRelationshipService.validateCreateHeadRelationship(rawHeadRelationship, currentRelationship, null)
                errors += errorMessageService.addPrefixToMessages(innerErrors, "validation.field.changehead.prefix.msg.error", [changeHead.id])
            }


        }


        return errors

    }

    //</editor-fold>


}
