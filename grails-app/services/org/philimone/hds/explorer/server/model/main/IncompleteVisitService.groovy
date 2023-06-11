package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawIncompleteVisit
import org.philimone.hds.explorer.server.model.enums.IncompleteVisitReason
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.VisitReason
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

@Transactional
class IncompleteVisitService {

    def householdService
    def memberService
    def userService
    def codeGeneratorService
    def visitService
    def errorMessageService

    //<editor-fold desc="Incomplete Visit Utilities Methods">
    boolean exists(String visitCode) {
        Visit.countByCode(visitCode) > 0
    }

    Visit getVisit(String visitCode){
        if (!StringUtil.isBlank(visitCode)){
            return Visit.findByCode(visitCode)
        }
        return null
    }

    //</editor-fold>

    //<editor-fold desc="Incomplete Visit Factory/Manager Methods">
    RawExecutionResult<IncompleteVisit> createIncompleteVisit(RawIncompleteVisit rawIncompleteVisit) {

        /* Run Checks and Validations */

        def errors = validate(rawIncompleteVisit)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<IncompleteVisit> obj = RawExecutionResult.newErrorResult(RawEntity.INCOMPLETE_VISIT, errors)
            return obj
        }

        def incompleteVisit = newIncompleteVisitInstance(rawIncompleteVisit)

        def result = incompleteVisit.save(flush:true)
        //Validate using Gorm Validations
        if (incompleteVisit.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.INCOMPLETE_VISIT, incompleteVisit)

            RawExecutionResult<IncompleteVisit> obj = RawExecutionResult.newErrorResult(RawEntity.INCOMPLETE_VISIT, errors)
            return obj
        } else {
            incompleteVisit = result
        }

        RawExecutionResult<IncompleteVisit> obj = RawExecutionResult.newSuccessResult(RawEntity.INCOMPLETE_VISIT, incompleteVisit)
        return obj
    }

    ArrayList<RawMessage> validate(RawIncompleteVisit rawIncompleteVisit){
        def errors = new ArrayList<RawMessage>()

        //code, householdCode, visitDate, visitLocation, visitLocationOther, roundNumber, respondentCode, hasInterpreter, interpreterName, gpsAccuracy, gpsAltitude, gpsLatitude, gpsLongitude
        def isBlankVisitCode = StringUtil.isBlank(rawIncompleteVisit.visitCode)
        def isBlankHouseholdCode = StringUtil.isBlank(rawIncompleteVisit.householdCode)
        def isBlankMemberCode = StringUtil.isBlank(rawIncompleteVisit.memberCode)        
        def isBlankReason = StringUtil.isBlank(rawIncompleteVisit.reason)
        def isBlankReasonOther = StringUtil.isBlank(rawIncompleteVisit.reasonOther)
        def isBlankCollectedBy = StringUtil.isBlank(rawIncompleteVisit.collectedBy)

        def visit = visitService.getVisit(rawIncompleteVisit.visitCode)
        def member = memberService.getMember(rawIncompleteVisit.memberCode)
        def household = householdService.getHousehold(rawIncompleteVisit.householdCode)
        def visitExists = visit != null
        def memberExists = member != null
        def householdExists = household != null
        def reason = IncompleteVisitReason.getFrom(rawIncompleteVisit.reason)
        def isReasonOther = reason != null && reason==IncompleteVisitReason.OTHER

        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.blank", ["memberCode"], ["memberCode"])
        }
        //C1. Check Blank Fields (reason)
        if (isBlankReason){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.blank", ["reason"], ["reason"])
        }
        //C1. Check Blank Fields (reasonOther) //Its Conditional
        if (isReasonOther && isBlankReasonOther){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.blank", ["reasonOther"], ["reasonOther"])
        }

        //C5. Validate reason Enum Options
        if (!isBlankReason && IncompleteVisitReason.getFrom(rawIncompleteVisit.reason)==null){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.enum.choices.error", [rawIncompleteVisit.reason, "reason"], ["reason"])
        }

        //C4. Check Visit reference existence
        if (!isBlankVisitCode && !visitExists){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.reference.error", ["Visit", "code", rawIncompleteVisit.visitCode], ["visitCode"])
        }
        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.reference.error", ["Household", "householdCode", rawIncompleteVisit.householdCode], ["householdCode"])
        }
        //C4. Check Member reference existence
        if (!memberExists){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.reference.error", ["Member", "code", rawIncompleteVisit.memberCode], ["memberCode"])
        }

        //C5. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(rawIncompleteVisit.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.INCOMPLETE_VISIT, "validation.field.user.dont.exists.error", [rawIncompleteVisit.collectedBy], ["collectedBy"])
        }



        return errors
    }

    private IncompleteVisit newIncompleteVisitInstance(RawIncompleteVisit riv){

        def visit = visitService.getVisit(riv.visitCode)
        def member = memberService.getMember(riv.memberCode)
        def household = householdService.getHousehold(riv.householdCode)

        IncompleteVisit incompleteVisit = new IncompleteVisit()

        incompleteVisit.visit = visit
        incompleteVisit.visitCode = riv.visitCode
        incompleteVisit.memberCode = riv.memberCode
        incompleteVisit.member = member
        //incompleteVisit.householdCode = household.code

        incompleteVisit.reason = IncompleteVisitReason.getFrom(riv.reason)
        incompleteVisit.reasonOther = riv.reasonOther

        //set collected by info
        incompleteVisit.collectedId = riv.id
        incompleteVisit.collectedBy = userService.getUser(riv.collectedBy)
        incompleteVisit.collectedDeviceId = riv.collectedDeviceId
        incompleteVisit.collectedHouseholdId = riv.collectedHouseholdId
        incompleteVisit.collectedMemberId = riv.collectedMemberId
        incompleteVisit.collectedStart = riv.collectedStart
        incompleteVisit.collectedEnd = riv.collectedEnd
        incompleteVisit.collectedDate = riv.collectedDate
        incompleteVisit.updatedDate = riv.uploadedDate

        return incompleteVisit

    }
    //</editor-fold>
}
