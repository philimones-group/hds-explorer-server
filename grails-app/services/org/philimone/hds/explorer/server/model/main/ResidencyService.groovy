package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawResidency
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyStartType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

@Transactional
class ResidencyService {

    def householdService
    def memberService
    def userService
    def errorMessageService

    //<editor-fold desc="Residency Utilities Methods">
    Residency getCurrentResidency(Member member){
        if (member != null && member.id != null){

            def residencies = Residency.executeQuery("select r from Residency r where r.member.id=? order by r.startDate desc", [member.id], [offset:0, max:1]) // limit 1

            /*
            def residencies = Residency.createCriteria(offset:0, max: 1).list {
                eq("member", member)
                order("startDate", "desc")
            }
            */

            if (residencies != null && residencies.size()>0){
                return residencies.first()
            }

        }
        return null
    }

    Residency getCurrentResidency(Member member, Household household){
        if (member != null && member.id != null && household != null && household.id != null){

            def residencies = Residency.executeQuery("select r from Residency r where r.member.id=? and r.household.id=? order by r.startDate desc", [member.id, household.id], [offset:0, max:1]) // limit 1

            if (residencies != null && residencies.size()>0){
                return residencies.first()
            }

        }
        return null
    }

    Household getCurrentHousehold(Member member){
        def residency = getCurrentResidency(member)
        return residency?.household
    }

    RawResidency getCurrentResidencyAsRaw(Member member){
        def residency = getCurrentResidency(member)
        return convertToRaw(residency)
    }

    RawResidency getCurrentResidencyAsRaw(Member member, Household household){
        def residency = getCurrentResidency(member, household)
        return convertToRaw(residency)
    }

    RawResidency convertToRaw(Residency residency){

        if (residency == null) return null

        def rr = new RawResidency()

        rr.memberCode = residency.member.code
        rr.householdCode = residency.household.code
        rr.startType = residency.startType.code
        rr.startDate = residency.startDate
        rr.endType = residency.endType.code
        rr.endDate = residency.endDate

        return rr
    }


    List<RawMessage> updatesAfterCreatingResidency(Residency residency){

        def errors = [] as ArrayList<RawMessage>
        def member = residency.member.refresh()
        def household = residency.household.refresh()

        //check if is first entry
        if (Residency.countByMember(member) == 1){
            member.entryType = residency.startType
            member.entryDate = residency.startDate
            member.entryHousehold = residency.household.code
        }

        member.householdCode = household.code
        member.household = household

        member.startType = residency.startType
        member.startDate = residency.startDate
        member.save(flush:true)

        //get errors if they occur and send with the success report
        if (member.hasErrors()){
            errors = errorMessageService.getRawMessages(member)
        }

        return errors
    }

    List<RawMessage> updatesAfterClosingResidency(Residency residency){

        def errors = [] as ArrayList<RawMessage>
        def member = residency.member.refresh()

        member.endType = residency.endType
        member.endDate = residency.endDate
        member.save(flush:true)
        //get errors if they occur and send with the success report
        if (member.hasErrors()){
            errors = errorMessageService.getRawMessages(member)
        }

        return errors
    }
    //</editor-fold>

    //<editor-fold desc="Residency Factory/Manager Methods">
    RawExecutionResult<Residency> createResidency(RawResidency rawResidency) {

        /* Run Checks and Validations */

        def errors = validateCreateResidency(rawResidency)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Residency> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        def residency = newResidencyInstance(rawResidency)

        residency = residency.save(flush:true)

        //Validate using Gorm Validations
        if (residency.hasErrors()){

            errors = errorMessageService.getRawMessages(residency)

            RawExecutionResult<Residency> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        //Update Member with start status
        errors = updatesAfterCreatingResidency(residency)

        RawExecutionResult<Residency> obj = RawExecutionResult.newSuccessResult(residency, errors)
        return obj
    }

    RawExecutionResult<Residency> closeResidency(RawResidency rawResidency) {

        /* Run Checks and Validations */

        def errors = validateCloseResidency(rawResidency)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Residency> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        def residency = closeResidencyInstance(rawResidency)

        residency = residency.save(flush:true)
        //Validate using Gorm Validations
        if (residency.hasErrors()){

            errors = errorMessageService.getRawMessages(residency)

            RawExecutionResult<Residency> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        //Update Member with start status
        errors = updatesAfterClosingResidency(residency)


        RawExecutionResult<Residency> obj = RawExecutionResult.newSuccessResult(residency, errors)
        return obj
    }

    ArrayList<RawMessage> validateCreateResidency(RawResidency residency){
        def errors = new ArrayList<RawMessage>()

        def isBlankMemberCode = StringUtil.isBlank(residency.memberCode)
        def isBlankHouseholdCode = StringUtil.isBlank(residency.householdCode)
        def isBlankStartType = StringUtil.isBlank(residency.startType)
        def isNullStartDate = residency.startDate == null
        def member = !isBlankMemberCode ? memberService.getMember(residency.memberCode) : null
        def household = !isBlankHouseholdCode ? householdService.getHousehold(residency.householdCode) : null
        def memberExists = member != null
        def householdExists = household != null

        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (startType)
        if (isBlankStartType){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["startType"], ["startType"])
        }
        //C1. Check Nullable Fields (startDate)
        if (isNullStartDate){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["startDate"], ["startDate"])
        }
        //C3. Validate startType Enum Options
        if (!isBlankStartType && ResidencyStartType.getFrom(residency.startType)==null){
            errors << errorMessageService.getRawMessage("validation.field.enum.starttype.error", [residency.startType], ["startType"])
        }
        //C4. Check Member reference existence
        if (!isBlankMemberCode && !memberExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "memberCode", residency.memberCode], ["memberCode"])
        }
        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Household", "householdCode", residency.householdCode], ["householdCode"])
        }
        //C5. Check startDate max date
        if (!isNullStartDate && residency.startDate > new Date()){
            errors << errorMessageService.getRawMessage("validation.field.date.not.greater.today", ["residency.startDate"], ["startDate"])
        }
        //C6. Check Dates against DOB
        if (!isNullStartDate && memberExists && residency.startDate < member.dob){
            errors << errorMessageService.getRawMessage("validation.field.dob.not.greater.date", ["residency.startDate", StringUtil.format(member.dob)], ["startDate","member.dob"])
        }

        //Validation part 2: Previous Residency against new Residency
        if (memberExists && householdExists){
            def previous = getCurrentResidency(member)
            def newStartType = ResidencyStartType.getFrom(residency.startType)
            def newStartDate = residency.startDate

            if (previous == null) {
                return errors
            }

            //P1. Check If endType is empty or NA
            if (previous.endType == null || previous.endType == ResidencyEndType.NOT_APPLICABLE){
                errors << errorMessageService.getRawMessage("validation.field.residency.endtype.na.error", null, ["previous.endType"])
            }
            //P2. Check If endType is DTH or Member has DTH Reg
            if (previous.endType == ResidencyEndType.DEATH || memberService.isDead(member)){
                errors << errorMessageService.getRawMessage("validation.field.residency.endtype.dth.error", null, ["previous.endType"])
            }
            //P3. Check If endType is CHG and new startType isnt ENT
            if (previous.endType == ResidencyEndType.INTERNAL_OUTMIGRATION && newStartType != ResidencyStartType.INTERNAL_INMIGRATION){
                errors << errorMessageService.getRawMessage("validation.field.residency.endtype.chg.error", null, ["previous.endType"])
            }
            //P4. Check If endType is EXT and new startType isnt XEN
            if (previous.endType == ResidencyEndType.EXTERNAL_OUTMIGRATION && newStartType != ResidencyStartType.EXTERNAL_INMIGRATION){
                errors << errorMessageService.getRawMessage("validation.field.residency.endtype.ext.error", null, ["previous.endType"])
            }
            //C5. Check If endDate is greater than new startDate
            if (previous.endDate != null && (previous.endDate >= newStartDate)){
                errors << errorMessageService.getRawMessage("validation.field.residency.enddate.greater.new.startdate.error", null, ["previous.endType"])
            }
        }


        return errors
    }

    ArrayList<RawMessage> validateCreateResidency(Residency previousFakeResidency, RawResidency residency){
        def errors = new ArrayList<RawMessage>()

        def isBlankMemberCode = StringUtil.isBlank(residency.memberCode)
        def isBlankHouseholdCode = StringUtil.isBlank(residency.householdCode)
        def isBlankStartType = StringUtil.isBlank(residency.startType)
        def isNullStartDate = residency.startDate == null
        def member = !isBlankMemberCode ? memberService.getMember(residency.memberCode) : null
        def household = !isBlankHouseholdCode ? householdService.getHousehold(residency.householdCode) : null
        def memberExists = member != null
        def householdExists = household != null

        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (startType)
        if (isBlankStartType){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["startType"], ["startType"])
        }
        //C1. Check Nullable Fields (startDate)
        if (isNullStartDate){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["startDate"], ["startDate"])
        }
        //C3. Validate startType Enum Options
        if (!isBlankStartType && ResidencyStartType.getFrom(residency.startType)==null){
            errors << errorMessageService.getRawMessage("validation.field.enum.starttype.error", [residency.startType], ["startType"])
        }
        //C4. Check Member reference existence
        if (!isBlankMemberCode && !memberExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "memberCode", residency.memberCode], ["memberCode"])
        }
        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Household", "householdCode", residency.householdCode], ["householdCode"])
        }
        //C5. Check startDate max date
        if (!isNullStartDate && residency.startDate > new Date()){
            errors << errorMessageService.getRawMessage("validation.field.date.not.greater.today", ["residency.startDate"], ["startDate"])
        }
        //C6. Check Dates against DOB
        if (!isNullStartDate && memberExists && residency.startDate < member.dob){
            errors << errorMessageService.getRawMessage("validation.field.dob.not.greater.date", ["residency.startDate", StringUtil.format(member.dob)], ["startDate","member.dob"])
        }

        //Validation part 2: Previous Residency against new Residency
        if (memberExists && householdExists){
            def previous = previousFakeResidency //getCurrentResidency(member)
            def newStartType = ResidencyStartType.getFrom(residency.startType)
            def newStartDate = residency.startDate

            if (previous == null) {
                return errors
            }

            //P1. Check If endType is empty or NA
            if (previous.endType == null || previous.endType == ResidencyEndType.NOT_APPLICABLE){
                errors << errorMessageService.getRawMessage("validation.field.residency.endtype.na.error", null, ["previous.endType"])
            }
            //P2. Check If endType is DTH or Member has DTH Reg
            if (previous.endType == ResidencyEndType.DEATH || memberService.isDead(member)){
                errors << errorMessageService.getRawMessage("validation.field.residency.endtype.dth.error", null, ["previous.endType"])
            }
            //P3. Check If endType is CHG and new startType isnt ENT
            if (previous.endType == ResidencyEndType.INTERNAL_OUTMIGRATION && newStartType != ResidencyStartType.INTERNAL_INMIGRATION){
                errors << errorMessageService.getRawMessage("validation.field.residency.endtype.chg.error", null, ["previous.endType"])
            }
            //P4. Check If endType is EXT and new startType isnt XEN
            if (previous.endType == ResidencyEndType.EXTERNAL_OUTMIGRATION && newStartType != ResidencyStartType.EXTERNAL_INMIGRATION){
                errors << errorMessageService.getRawMessage("validation.field.residency.endtype.ext.error", null, ["previous.endType"])
            }
            //C5. Check If endDate is greater than new startDate
            if (previous.endDate != null && (previous.endDate >= newStartDate)){
                errors << errorMessageService.getRawMessage("validation.field.residency.enddate.greater.new.startdate.error", null, ["previous.endType"])
            }
        }


        return errors
    }

    ArrayList<RawMessage> validateCloseResidency(RawResidency residency){
        def errors = new ArrayList<RawMessage>()

        def isBlankMemberCode = StringUtil.isBlank(residency.memberCode)
        def isBlankHouseholdCode = StringUtil.isBlank(residency.householdCode)
        def isBlankEndType = StringUtil.isBlank(residency.endType)
        def isNullEndDate = residency.endDate == null
        def member = !isBlankMemberCode ? memberService.getMember(residency.memberCode) : null
        def household = !isBlankHouseholdCode ? householdService.getHousehold(residency.householdCode) : null
        def memberExists = member != null
        def householdExists = household != null

        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (startType)
        if (isBlankEndType){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["endType"], ["endType"])
        }
        //C1. Check Nullable Fields (startDate)
        if (isNullEndDate){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["endDate"], ["endDate"])
        }
        //C3. Validate endType Enum Options
        if (!isBlankEndType && ResidencyEndType.getFrom(residency.endType)==null){
            errors << errorMessageService.getRawMessage("validation.field.enum.endtype.error", [residency.endType], ["endType"])
        }
        //C4. Check Member reference existence
        if (!isBlankMemberCode && !memberExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "memberCode", residency.memberCode], ["memberCode"])
        }
        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Household", "householdCode", residency.householdCode], ["householdCode"])
        }
        //C5. Check endDate max date
        if (!isNullEndDate && residency.endDate > new Date()){
            errors << errorMessageService.getRawMessage("validation.field.date.not.greater.today", ["endDate"], ["endDate"])
        }
        //C6. Check Dates against DOB
        if (!isNullEndDate && memberExists && residency.endDate < member.dob){
            errors << errorMessageService.getRawMessage("validation.field.dob.not.greater.date", ["residency.endDate", StringUtil.format(member.dob)], ["endDate","member.dob"])
        }

        //Validation part 2: Previous Residency against new Residency
        if (memberExists && householdExists){
            def currentResidency = getCurrentResidency(member, household)
            def endDate = residency.endDate

            //must exist
            if (currentResidency == null) {
                //THERE IS NO CURRENT RESIDENCY TO CLOSE
                errors << errorMessageService.getRawMessage("validation.field.residency.close.not.exists.error", [household.code, member.code], ["household.code","member.code"])
                return errors
            }

            //must not be closed
            //P1. Check If endType is empty or NA
            if ( !(currentResidency.endType == null || currentResidency.endType == ResidencyEndType.NOT_APPLICABLE) ){
                errors << errorMessageService.getRawMessage("validation.field.residency.closed.already.error", [currentResidency.id, currentResidency.endType], ["previous.endType"])
            }

            //C6. Check If endDate is before or equal to startDate
            if (currentResidency.startDate >= endDate){ //RECHECK THIS WITH >=
                errors << errorMessageService.getRawMessage("validation.field.residency.enddate.before.startdate.error", null, ["currentResidency.startDate", "new.endDate"])
            }

        }


        return errors
    }

    private Residency newResidencyInstance(RawResidency rr){

        Residency residency = new Residency()

        residency.member = memberService.getMember(rr.memberCode)
        residency.household = householdService.getHousehold(rr.householdCode)
        residency.memberCode = residency.member?.code
        residency.householdCode = residency.household?.code
        residency.startType = ResidencyStartType.getFrom(rr.startType)
        residency.startDate = rr.startDate
        residency.endType = ResidencyEndType.NOT_APPLICABLE
        residency.endDate = null


        return residency

    }

    private Residency closeResidencyInstance(RawResidency rr){

        def household = householdService.getHousehold(rr.householdCode)
        def member = memberService.getMember(rr.memberCode)
        def residency = getCurrentResidency(member, household)
        def endType = ResidencyEndType.getFrom(rr.endType)


        residency.member = member
        residency.household = household
        residency.memberCode = member.code
        residency.householdCode = household.code
        //residency.startType = Already defined
        //residency.startDate = Already defined
        residency.endType = endType
        residency.endDate = rr.endDate


        return residency

    }
    //</editor-fold>
}
