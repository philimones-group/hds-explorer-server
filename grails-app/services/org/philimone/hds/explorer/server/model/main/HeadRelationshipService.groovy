package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
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
class HeadRelationshipService {

    def householdService
    def memberService
    def userService
    def errorMessageService

    //<editor-fold desc="HeadRelationship Utilities Methods">
    HeadRelationship getCurrentHeadRelationship(Member member) {
        if (member != null && member.id != null) {

            def headRelationships = HeadRelationship.executeQuery("select r from HeadRelationship r where r.member.id=?0 order by r.startDate desc", [member.id], [offset:0, max:1]) // limit 1

            if (headRelationships != null && headRelationships.size()>0) {
                return headRelationships.first()
            }

        }
        return null
    }

    HeadRelationship getCurrentHeadRelationship(String memberCode) {
        if (!StringUtil.isBlank(memberCode)) {

            def headRelationships = HeadRelationship.executeQuery("select r from HeadRelationship r where r.member.code=?0 order by r.startDate desc", [memberCode], [offset:0, max:1]) // limit 1

            if (headRelationships != null && headRelationships.size()>0) {
                return headRelationships.first()
            }

        }
        return null
    }

    HeadRelationship getCurrentHeadRelationship(Member member, Household household) {
        if (member != null && member.id != null && household != null && household.id != null) {

            def headRelationships = HeadRelationship.executeQuery("select r from HeadRelationship r where r.member.id=?0 and r.household.id=?1 order by r.startDate desc", [member.id, household.id], [offset:0, max:1]) // limit 1

            if (headRelationships != null && headRelationships.size()>0) {
                return headRelationships.first()
            }

        }
        return null
    }

    /*
     * Gets the last Head of Household that this household had
     */
    HeadRelationship getCurrentHouseholdHead(Household household){

        if (household != null && household.id != null){
            def headRelationships = HeadRelationship.executeQuery("select r from HeadRelationship r where r.household.id=?0 and r.relationshipType=?1 order by r.startDate desc", [household.id, HeadRelationshipType.HEAD_OF_HOUSEHOLD], [offset:0, max:1]) // limit 1

            if (headRelationships != null && headRelationships.size()>0) {
                return headRelationships.first()
            }
        }

        return null
    }

    HeadRelationship getCurrentHouseholdHead(String householdCode){

        if (!StringUtil.isBlank(householdCode)){
            def headRelationships = HeadRelationship.executeQuery("select r from HeadRelationship r where r.household.code=?0 and r.relationshipType=?1 order by r.startDate desc", [householdCode, HeadRelationshipType.HEAD_OF_HOUSEHOLD], [offset:0, max:1]) // limit 1

            if (headRelationships != null && headRelationships.size()>0) {
                return headRelationships.first()
            }
        }

        return null
    }

    HeadRelationship getCurrentActiveHouseholdHead(String householdCode){

        if (!StringUtil.isBlank(householdCode)){
            def headRelationships = HeadRelationship.executeQuery("select r from HeadRelationship r where r.household.code=?0 and r.relationshipType=?1 and r.endType=?2 order by r.startDate desc", [householdCode, HeadRelationshipType.HEAD_OF_HOUSEHOLD, HeadRelationshipEndType.NOT_APPLICABLE], [offset:0, max:1]) // limit 1

            if (headRelationships != null && headRelationships.size()>0) {
                return headRelationships.first()
            }
        }

        return null
    }

    boolean hasHeadOfHousehold(String householdCode) {
        return getCurrentHouseholdHead(householdCode) != null
    }

    HeadRelationship getCurrentHouseholdHead(Member member){

        if (member != null && member?.id != null){
            def headRelationships = HeadRelationship.executeQuery("select r from HeadRelationship r where r.member.id=?0 and r.relationshipType=?1 order by r.startDate desc", [member.id, HeadRelationshipType.HEAD_OF_HOUSEHOLD], [offset:0, max:1]) // limit 1

            if (headRelationships != null && headRelationships.size()>0) {
                return headRelationships.first()
            }
        }

        return null
    }

    boolean isHeadOfHousehold(Member member) {
        def headRelationship = getCurrentHouseholdHead(member)

        if (headRelationship != null) {
            return (headRelationship.endType == HeadRelationshipEndType.NOT_APPLICABLE) //if true is a head of an household currently, otherwise he was an head of a household
        }

        return false
    }

    boolean isCurrentHeadOfHousehold(Household household, Member member){
        def currentHeadRelationship = getCurrentHouseholdHead(household)
        def currentHead = currentHeadRelationship?.member

        return currentHead?.code==member?.code
    }

    List<HeadRelationship> getCurrentHeadRelationships(Member headOfHousehold, Household household){
        if (headOfHousehold != null && headOfHousehold.id != null && household != null && household.id != null) {

            def headRelationships = HeadRelationship.executeQuery("select r from HeadRelationship r where r.head.id=?0 and r.household.id=?1 order by r.startDate", [headOfHousehold.id, household.id])

            return headRelationships

        }
        return null
    }

    List<HeadRelationship> getCurrentHeadRelationships(String headCode, String householdCode){
        def member = memberService.getMember(headCode)
        def household = householdService.getHousehold(householdCode)
        return getCurrentHeadRelationships(member, household)
    }

    RawHeadRelationship getCurrentHeadRelationshipAsRaw(Member member) {
        def headRelationship = getCurrentHeadRelationship(member)
        return convertToRaw(headRelationship)
    }

    RawHeadRelationship getCurrentHeadRelationshipAsRaw(String memberCode) {
        def headRelationship = getCurrentHeadRelationship(memberCode)
        return convertToRaw(headRelationship)
    }

    /*RawHeadRelationship getCurrentHeadRelationshipAsRaw(Member member, Household household) {
        def headRelationship = getCurrentHeadRelationship(member, household)
        return convertToRaw(headRelationship)
    }*/

    /*RawHeadRelationship getCurrentHouseholdHeadAsRaw(Household household){
        def headRelationship = getCurrentHouseholdHead(household)
        return convertToRaw(headRelationship)
    }*/

    RawHeadRelationship convertToRaw(HeadRelationship headRelationship){

        if (headRelationship == null) return null

        def rh = new RawHeadRelationship()

        rh.id = headRelationship.id
        rh.memberCode = headRelationship.member.code
        rh.householdCode = headRelationship.household.code
        rh.relationshipType = headRelationship.relationshipType.code
        rh.startType = headRelationship.startType.code
        rh.startDate = headRelationship.startDate
        rh.endType = headRelationship.endType.code
        rh.endDate = headRelationship.endDate

        return rh
    }

    List<RawMessage> updatesAfterCreatingRelationship(HeadRelationship headRelationship) {

        def errors = [] as ArrayList<RawMessage>
        def member = headRelationship.member.refresh()
        def household = headRelationship.household.refresh()

        member.headRelationshipType = headRelationship.relationshipType
        member.save(flush: true)

        //is a new head of household
        if (member.headRelationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD) {
            //Set New Head on Household
            household.headMember = member
            household.headCode = member.code
            household.headName = member.name

            household.save(flush: true)
        }

        //get errors if they occur and send with the success report
        if (member.hasErrors()) {
            errors += errorMessageService.getRawMessages(RawEntity.MEMBER, member)
        }
        if (household.hasErrors()) {
            errors += errorMessageService.getRawMessages(RawEntity.HOUSEHOLD, household)
        }

        return errors
    }

    List<RawMessage> updatesAfterClosingRelationship(HeadRelationship headRelationship){

        def errors = [] as ArrayList<RawMessage>
        def member = headRelationship.member.refresh()
        def household = headRelationship.household.refresh()

        //if member was the head of household, remove reference on household
        if (member.headRelationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD){
            household.headCode = null
            household.headName = null
            household.headMember = null
            household.save()
        }

        //get errors if they occur and send with the success report
        if (member.hasErrors()) {
            errors += errorMessageService.getRawMessages(RawEntity.MEMBER, member)
        }
        if (household.hasErrors()) {
            errors += errorMessageService.getRawMessages(RawEntity.HOUSEHOLD, household)
        }

        return errors
    }
    //</editor-fold>

    //<editor-fold desc="HeadRelationship Factory/Manager Methods">
    RawExecutionResult<HeadRelationship> createHeadRelationship(RawHeadRelationship rawHeadRelationship) {

        /* Run Checks and Validations */

        def errors = validateCreateHeadRelationship(rawHeadRelationship)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<HeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.HEAD_RELATIONSHIP, errors)
            return obj
        }

        def headRelationship = newHeadRelationshipInstance(rawHeadRelationship)

        def result = headRelationship.save(flush:true)

        //Validate using Gorm Validations
        if (headRelationship.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.HEAD_RELATIONSHIP, headRelationship)

            RawExecutionResult<HeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.HEAD_RELATIONSHIP, errors)
            return obj
        } else {
            headRelationship = result
        }

        //Update Member with start status
        errors += updatesAfterCreatingRelationship(headRelationship)

        RawExecutionResult<HeadRelationship> obj = RawExecutionResult.newSuccessResult(RawEntity.HEAD_RELATIONSHIP, headRelationship, errors)
        return obj
    }

    RawExecutionResult<HeadRelationship> closeHeadRelationship(RawHeadRelationship rawHeadRelationship) {

        /* Run Checks and Validations */

        def errors = validateCloseHeadRelationship(rawHeadRelationship)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<HeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.HEAD_RELATIONSHIP, errors)
            return obj
        }

        def headRelationship = closeHeadRelationshipInstance(rawHeadRelationship)

        def result = headRelationship.save(flush:true)
        //Validate using Gorm Validations
        if (headRelationship.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.HEAD_RELATIONSHIP, headRelationship)

            RawExecutionResult<HeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.HEAD_RELATIONSHIP, errors)
            return obj
        } else {
            headRelationship = result
        }

        //Update Household - remove head if member was one
        errors = updatesAfterClosingRelationship(headRelationship)

        RawExecutionResult<HeadRelationship> obj = RawExecutionResult.newSuccessResult(RawEntity.HEAD_RELATIONSHIP, headRelationship, errors)
        return obj
    }

    ArrayList<RawMessage> validateCreateHeadRelationship(RawHeadRelationship headRelationship, HeadRelationship fakePreviousHeadRelationship, HeadRelationship fakePreviousHouseholdHead){
        def errors = new ArrayList<RawMessage>()

        def isBlankMemberCode = StringUtil.isBlank(headRelationship.memberCode)
        def isBlankHouseholdCode = StringUtil.isBlank(headRelationship.householdCode)
        def isBlankStartType = StringUtil.isBlank(headRelationship.startType)
        def isNullStartDate = headRelationship.startDate == null
        def isBlankRelationshipType = StringUtil.isBlank(headRelationship.relationshipType)
        def relationshipType = !isBlankRelationshipType ? HeadRelationshipType.getFrom(headRelationship.relationshipType) : null
        def member = !isBlankMemberCode ? memberService.getMember(headRelationship.memberCode) : null
        def household = !isBlankHouseholdCode ? householdService.getHousehold(headRelationship.householdCode) : null
        def head = !isBlankHouseholdCode ? getCurrentHouseholdHead(household) : null
        def memberExists = member != null
        def householdExists = household != null
        def headExists = head != null

        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (startType)
        if (isBlankStartType){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["startType"], ["startType"])
        }
        //C1. Check Nullable Fields (startDate)
        if (isNullStartDate){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["startDate"], ["startDate"])
        }
        //C3. Validate startType Enum Options
        if (!isBlankStartType && HeadRelationshipStartType.getFrom(headRelationship.startType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.enum.starttype.error", [headRelationship.startType], ["startType"])
        }
        //C3. Validate relationshipType Enum Options
        if (!isBlankRelationshipType && HeadRelationshipType.getFrom(headRelationship.relationshipType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.type.valid.error", [headRelationship.relationshipType], ["relationshipType"])
        }
        //C4. Check Member reference existence
        if (!isBlankMemberCode && !memberExists){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.reference.error", ["Member", "memberCode", headRelationship.memberCode], ["memberCode"])
        }
        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.reference.error", ["Household", "householdCode", headRelationship.householdCode], ["householdCode"])
        }
        //C5. Check startDate max date
        if (!isNullStartDate && headRelationship.startDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.date.not.greater.today", ["headRelationship.startDate"], ["startDate"])
        }
        //C5.2. Check Dates against DOB
        if (!isNullStartDate && memberExists && headRelationship.startDate < member.dob){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.dob.not.greater.date", ["headRelationship.startDate", StringUtil.format(member.dob)], ["startDate","member.dob"])
        }
        //C6. Check Age of Head of Household
        if (memberExists && (relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD && GeneralUtil.getAge(member.dob)< Codes.MIN_HEAD_AGE_VALUE )){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.dob.head.minage.error", [StringUtil.format(member.dob), Codes.MIN_HEAD_AGE_VALUE+""], ["member.dob"])
        }
        //C7. Check Current Head Existence and the new relation is not a head of household - We must have a existent Head of Household in order to create new Relationship with the Head
        if (!headExists && relationshipType != HeadRelationshipType.HEAD_OF_HOUSEHOLD){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.head.not.exists.error", [headRelationship.householdCode], ["householdCode"])
        }

        //Validation part 2: Previous HeadRelationship against new HeadRelationship
        if (memberExists && householdExists){
            def previous = fakePreviousHeadRelationship //getCurrentHeadRelationship(member) - get new head of household previous relationship
            def newStartType = HeadRelationshipStartType.getFrom(headRelationship.startType)
            def newStartDate = headRelationship.startDate

            if (previous == null) {
                return errors
            }

            //P1. Check If endType is empty or NA
            if (previous.endType == null || previous.endType == HeadRelationshipEndType.NOT_APPLICABLE){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.na.error", null, ["previous.endType"])
            }
            //P2. Check If endType is DTH or Member has DTH Reg
            if (previous.endType == HeadRelationshipEndType.DEATH || memberService.isDead(member)){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.dth.error", null, ["previous.endType"])
            }
            //P3. Check If endType is CHG and new startType isnt ENT
            if (previous.endType == HeadRelationshipEndType.INTERNAL_OUTMIGRATION && newStartType != HeadRelationshipStartType.INTERNAL_INMIGRATION){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.chg.error", null, ["previous.endType"])
            }
            //P4. Check If endType is EXT and new startType isnt XEN
            if (previous.endType == HeadRelationshipEndType.EXTERNAL_OUTMIGRATION && newStartType != HeadRelationshipStartType.EXTERNAL_INMIGRATION){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.ext.error", null, ["previous.endType"])
            }
            //P5. Check If endType is DHH/CHH and new startType isnt NHH
            if ((previous.endType == HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD || previous.endType == HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD) &&
                 newStartType != HeadRelationshipStartType.NEW_HEAD_OF_HOUSEHOLD){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.dhh.chh.error", null, ["previous.endType"])
            }
            //P6. Check If endDate is greater than new startDate
            if (previous.endDate != null && (previous.endDate >= newStartDate)){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.enddate.greater.new.startdate.error", null, ["previous.endType"])
            }

            //P7. C7. Check If relationshipType is HEAD and if previous head is closed
            if (relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD){
                //get current household head, we can get that using the household.headMember and/or HeadRelationship
                def previousHead = fakePreviousHouseholdHead //getCurrentHouseholdHead(household)  - get old/current head of household relationship

                if (previousHead != null && (previousHead.endType == null || previousHead.endType == HeadRelationshipEndType.NOT_APPLICABLE)){
                    errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.type.head.not.closed.error", null, ["lastHead.endType"])
                }
            }
        }


        return errors
    }

    ArrayList<RawMessage> validateCreateHeadRelationship(RawHeadRelationship headRelationship){
        def errors = new ArrayList<RawMessage>()

        def isBlankMemberCode = StringUtil.isBlank(headRelationship.memberCode)
        def isBlankHouseholdCode = StringUtil.isBlank(headRelationship.householdCode)
        def isBlankStartType = StringUtil.isBlank(headRelationship.startType)
        def isNullStartDate = headRelationship.startDate == null
        def isBlankRelationshipType = StringUtil.isBlank(headRelationship.relationshipType)
        def relationshipType = !isBlankRelationshipType ? HeadRelationshipType.getFrom(headRelationship.relationshipType) : null
        def member = !isBlankMemberCode ? memberService.getMember(headRelationship.memberCode) : null
        def household = !isBlankHouseholdCode ? householdService.getHousehold(headRelationship.householdCode) : null
        def head = !isBlankHouseholdCode ? getCurrentHouseholdHead(household) : null
        def memberExists = member != null
        def householdExists = household != null
        def headExists = head != null

        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (startType)
        if (isBlankStartType){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["startType"], ["startType"])
        }
        //C1. Check Nullable Fields (startDate)
        if (isNullStartDate){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["startDate"], ["startDate"])
        }
        //C3. Validate startType Enum Options
        if (!isBlankStartType && HeadRelationshipStartType.getFrom(headRelationship.startType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.enum.starttype.error", [headRelationship.startType], ["startType"])
        }
        //C3. Validate relationshipType Enum Options
        if (!isBlankRelationshipType && HeadRelationshipType.getFrom(headRelationship.relationshipType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.type.valid.error", [headRelationship.relationshipType], ["relationshipType"])
        }
        //C4. Check Member reference existence
        if (!isBlankMemberCode && !memberExists){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.reference.error", ["Member", "memberCode", headRelationship.memberCode], ["memberCode"])
        }
        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.reference.error", ["Household", "householdCode", headRelationship.householdCode], ["householdCode"])
        }
        //C5. Check startDate max date
        if (!isNullStartDate && headRelationship.startDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.date.not.greater.today", ["headRelationship.startDate"], ["startDate"])
        }
        //C5.2. Check Dates against DOB
        if (!isNullStartDate && memberExists && headRelationship.startDate < member.dob){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.dob.not.greater.date", ["headRelationship.startDate", StringUtil.format(member.dob)], ["startDate","member.dob"])
        }
        //C6. Check Age of Head of Household
        if (memberExists && (relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD && GeneralUtil.getAge(member.dob)< Codes.MIN_HEAD_AGE_VALUE )){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.dob.head.minage.error", [StringUtil.format(member.dob), Codes.MIN_HEAD_AGE_VALUE+""], ["member.dob"])
        }
        //C7. Check Current Head Existence and the new relation is not a head of household - We must have a existent Head of Household in order to create new Relationship with the Head
        if (!headExists && relationshipType != HeadRelationshipType.HEAD_OF_HOUSEHOLD){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.head.not.exists.error", [headRelationship.householdCode], ["householdCode"])
        }

        //Validation part 2: Previous HeadRelationship against new HeadRelationship
        if (memberExists && householdExists){
            def previous = getCurrentHeadRelationship(member)
            def newStartType = HeadRelationshipStartType.getFrom(headRelationship.startType)
            def newStartDate = headRelationship.startDate

            if (previous == null) {
                return errors
            }

            //P1. Check If endType is empty or NA
            if (previous.endType == null || previous.endType == HeadRelationshipEndType.NOT_APPLICABLE){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.na.error", null, ["previous.endType"])
            }
            //P2. Check If endType is DTH or Member has DTH Reg
            if (previous.endType == HeadRelationshipEndType.DEATH || memberService.isDead(member)){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.dth.error", null, ["previous.endType"])
            }
            //P3. Check If endType is CHG and new startType isnt ENT
            if (previous.endType == HeadRelationshipEndType.INTERNAL_OUTMIGRATION && newStartType != HeadRelationshipStartType.INTERNAL_INMIGRATION){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.chg.error", null, ["previous.endType"])
            }
            //P4. Check If endType is EXT and new startType isnt XEN
            if (previous.endType == HeadRelationshipEndType.EXTERNAL_OUTMIGRATION && newStartType != HeadRelationshipStartType.EXTERNAL_INMIGRATION){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.ext.error", null, ["previous.endType"])
            }
            //P5. Check If endType is DHH/CHH and new startType isnt NHH
            if ((previous.endType == HeadRelationshipEndType.DEATH_OF_HEAD_OF_HOUSEHOLD || previous.endType == HeadRelationshipEndType.CHANGE_OF_HEAD_OF_HOUSEHOLD) &&
                    newStartType != HeadRelationshipStartType.NEW_HEAD_OF_HOUSEHOLD){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.endtype.dhh.chh.error", null, ["previous.endType"])
            }
            //P6. Check If endDate is greater than new startDate
            if (previous.endDate != null && (previous.endDate >= newStartDate)){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.enddate.greater.new.startdate.error", null, ["previous.endType"])
            }

            //P7. C7. Check If relationshipType is HEAD and if previous head is closed
            if (relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD){
                //get current household head, we can get that using the household.headMember and/or HeadRelationship
                def previousHead = getCurrentHouseholdHead(household)

                if (previousHead != null && (previousHead.endType == null || previousHead.endType == HeadRelationshipEndType.NOT_APPLICABLE)){
                    errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.type.head.not.closed.error", null, ["lastHead.endType"])
                }
            }
        }


        return errors
    }

    ArrayList<RawMessage> validateCloseHeadRelationship(RawHeadRelationship headRelationship){
        def errors = new ArrayList<RawMessage>()

        def isBlankMemberCode = StringUtil.isBlank(headRelationship.memberCode)
        def isBlankHouseholdCode = StringUtil.isBlank(headRelationship.householdCode)
        def isBlankEndType = StringUtil.isBlank(headRelationship.endType)
        def isNullEndDate = headRelationship.endDate == null
        def member = !isBlankMemberCode ? memberService.getMember(headRelationship.memberCode) : null
        def household = !isBlankHouseholdCode ? householdService.getHousehold(headRelationship.householdCode) : null
        def memberExists = member != null
        def householdExists = household != null

        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (startType)
        if (isBlankEndType){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["endType"], ["endType"])
        }
        //C1. Check Nullable Fields (startDate)
        if (isNullEndDate){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.blank", ["endDate"], ["endDate"])
        }
        //C3. Validate startType Enum Options
        if (!isBlankEndType && HeadRelationshipEndType.getFrom(headRelationship.endType)==null){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.enum.endtype.error", [headRelationship.endType], ["endType"])
        }
        //C4. Check Member reference existence
        if (!isBlankMemberCode && !memberExists){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.reference.error", ["Member", "memberCode", headRelationship.memberCode], ["memberCode"])
        }
        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.reference.error", ["Household", "householdCode", headRelationship.householdCode], ["householdCode"])
        }
        //C5. Check endDate max date
        if (!isNullEndDate && headRelationship.endDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.date.not.greater.today", ["headRelationship.endDate"], ["endDate"])
        }
        //C6. Check Dates against DOB
        if (!isNullEndDate && memberExists && headRelationship.endDate < member.dob){
            errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.dob.not.greater.date", ["headRelationship.endDate", StringUtil.format(member.dob)], ["endDate","member.dob"])
        }

        //Validation part 2: Previous HeadRelationship against new HeadRelationship
        if (memberExists && householdExists){
            def currentHeadRelationship = getCurrentHeadRelationship(member, household)
            def endDate = headRelationship.endDate

            //must exist
            if (currentHeadRelationship == null) {
                //THERE IS NO CURRENT RELATIONSHIP TO CLOSE
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.close.not.exists.error", [household.code, member.code], ["household.code", "member.code"])
                return errors
            }

            //must not be closed
            //P1. Check If endType is empty or NA
            if ( !(currentHeadRelationship.endType == null || currentHeadRelationship.endType == HeadRelationshipEndType.NOT_APPLICABLE) ){
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.closed.already.error", [currentHeadRelationship.id+"", currentHeadRelationship.endType.code], ["previous.endType"])
            }

            //C6. Check If endDate is before or equal to startDate
            if (currentHeadRelationship.startDate > endDate){ //RECHECK THIS WITH >=
                errors << errorMessageService.getRawMessage(RawEntity.HEAD_RELATIONSHIP, "validation.field.headRelationship.enddate.before.startdate.error", [currentHeadRelationship.id, StringUtil.format(endDate), StringUtil.format(currentHeadRelationship.startDate)], ["currentHeadRelationship.startDate", "new.endDate"])
            }

        }


        return errors
    }

    private HeadRelationship newHeadRelationshipInstance(RawHeadRelationship rh){

        HeadRelationship headRelationship = new HeadRelationship()

        headRelationship.member = memberService.getMember(rh.memberCode)
        headRelationship.household = householdService.getHousehold(rh.householdCode)
        headRelationship.memberCode = headRelationship.member?.code
        headRelationship.householdCode = headRelationship.household?.code
        headRelationship.relationshipType = HeadRelationshipType.getFrom(rh.relationshipType)
        headRelationship.startType = HeadRelationshipStartType.getFrom(rh.startType)
        headRelationship.startDate = rh.startDate
        headRelationship.endType = HeadRelationshipEndType.NOT_APPLICABLE
        headRelationship.endDate = null

        // Set head of household in relationship
        if (headRelationship.relationshipType == HeadRelationshipType.HEAD_OF_HOUSEHOLD){
            headRelationship.head = headRelationship.member
            headRelationship.headCode = headRelationship.member.code
        } else {

            def headMemberRel = getCurrentHouseholdHead(headRelationship.household)

            headRelationship.head = headMemberRel.member
            headRelationship.headCode = headMemberRel.member.code
        }

        return headRelationship

    }

    private HeadRelationship closeHeadRelationshipInstance(RawHeadRelationship rr){

        def household = householdService.getHousehold(rr.householdCode)
        def member = memberService.getMember(rr.memberCode)
        def headRelationship = getCurrentHeadRelationship(member, household)
        def endType = HeadRelationshipEndType.getFrom(rr.endType)


        headRelationship.member = member
        headRelationship.household = household
        headRelationship.memberCode = member.code
        headRelationship.householdCode = household.code
        //headRelationship.startType = Already defined
        //headRelationship.startDate = Already defined
        headRelationship.endType = endType
        headRelationship.endDate = rr.endDate


        return headRelationship

    }
    //</editor-fold>
}
