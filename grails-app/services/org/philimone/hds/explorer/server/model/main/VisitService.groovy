package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawVisit
import org.philimone.hds.explorer.server.model.enums.VisitLocationItem
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

@Transactional
class VisitService {

    def householdService
    def memberService
    def userService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="Visit Utilities Methods">
    boolean exists(String visitCode) {
        Visit.countByCode(visitCode) > 0
    }

    Visit getVisit(String visitCode){
        if (!StringUtil.isBlank(visitCode)){
            return Visit.findByCode(visitCode)
        }
        return null
    }

    boolean isValidRoundNumber(Integer roundNumber){
        Round.countByRoundNumber(roundNumber) > 0
    }

    String generateCode(Household household){
        return codeGeneratorService.generateVisitCode(household)
    }

    //</editor-fold>

    //<editor-fold desc="Visit Factory/Manager Methods">
    RawExecutionResult<Visit> createVisit(RawVisit rawVisit) {

        /* Run Checks and Validations */

        def errors = validate(rawVisit)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Visit> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        def visit = newVisitInstance(rawVisit)

        def result = visit.save(flush:true)
        //Validate using Gorm Validations
        if (visit.hasErrors()){

            errors = errorMessageService.getRawMessages(visit)

            RawExecutionResult<Visit> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        } else {
            visit = result
        }

        RawExecutionResult<Visit> obj = RawExecutionResult.newSuccessResult(visit)
        return obj
    }

    ArrayList<RawMessage> validate(RawVisit rawVisit){
        def errors = new ArrayList<RawMessage>()

        //code, householdCode, visitDate, visitLocation, visitLocationOther, roundNumber, respondentCode, hasInterpreter, interpreterName, gpsAccuracy, gpsAltitude, gpsLatitude, gpsLongitude
        def isBlankCode = StringUtil.isBlank(rawVisit.code)
        def isBlankHouseholdCode = StringUtil.isBlank(rawVisit.householdCode)
        def isBlankVisitDate = rawVisit.visitDate == null
        def isBlankVisitLocation = StringUtil.isBlank(rawVisit.visitLocation)
        def isBlankVisitLocationOther = StringUtil.isBlank(rawVisit.visitLocationOther)
        def isBlankRoundNumber = StringUtil.isBlankInteger(rawVisit.roundNumber)
        def isBlankRespondentCode = StringUtil.isBlank(rawVisit.respondentCode)
        def isBlankHasInterpreter = StringUtil.isBlankBoolean(rawVisit.hasInterpreter)
        def isBlankInterpreterName = StringUtil.isBlank(rawVisit.interpreterName)
        def isBlankCollectedBy = StringUtil.isBlank(rawVisit.collectedBy)

        def respondent = memberService.getMember(rawVisit.respondentCode)
        def household = householdService.getHousehold(rawVisit.householdCode)
        def respondentExists = respondent != null
        def householdExists = household != null
        def visitLocation = !isBlankVisitLocation ? VisitLocationItem.getFrom(rawVisit.visitLocation) : null
        def isVisitLocationOther = visitLocation != null && visitLocation==VisitLocationItem.OTHER_PLACE

        //C1. Check Blank Fields (code)
        if (isBlankCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["code"], ["code"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Nullable Fields (visitDate)
        if (isBlankVisitDate){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["visitDate"], ["visitDate"])
        }
        //C1. Check Blank Fields (roundNumber)
        if (isBlankRoundNumber){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["roundNumber"], ["roundNumber"])
        }
        //C1. Check Blank Fields (visitLocation)
        if (isBlankVisitLocation){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["visitLocation"], ["visitLocation"])
        }
        //C1. Check Blank Fields (visitLocationOther) //Its Conditional
        if (isVisitLocationOther && isBlankVisitLocationOther){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["visitLocationOther"], ["visitLocationOther"])
        }
        //C1. Check Blank Fields (respondentCode)
        if (isBlankRespondentCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["respondentCode"], ["respondentCode"])
        }
        //C1. Check Blank Fields (hasInterpreter)
        if (isBlankHasInterpreter){
            //If is BLANK we will consider it false

            //errors << errorMessageService.getRawMessage("validation.field.blank", ["hasInterpreter"], ["hasInterpreter"])
        }
        //C1. Check Blank Fields (interpreterName) //Its Conditional
        if (rawVisit.hasInterpreter && isBlankInterpreterName){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["interpreterName"], ["interpreterName"])
        }

        //C2. Check VisitCode Regex Pattern
        if (!isBlankCode && !codeGeneratorService.isVisitCodeValid(rawVisit.code)) {
            errors << errorMessageService.getRawMessage("validation.field.pattern.no.matches", ["code", "TXUPF1001001"], ["code"])
        }

        if (!isBlankCode && !isBlankHouseholdCode && !rawVisit.code.startsWith(rawVisit.householdCode)){
            errors << errorMessageService.getRawMessage("validation.field.visit.code.prefix.not.current.error", [rawVisit.code, rawVisit.householdCode], ["visitCode","householdCode"])
        }

        //C4. Check If RoundNumber is Valid
        if (!isBlankRoundNumber && !isValidRoundNumber(rawVisit.roundNumber)){
            errors << errorMessageService.getRawMessage("validation.field.visit.roundnumber.valid.error", ["${rawVisit.roundNumber}"], ["roundNumber"])
        }

        //C5. Validate VisitLocation Enum Options
        if (!isBlankVisitLocation && VisitLocationItem.getFrom(rawVisit.visitLocation)==null){
            errors << errorMessageService.getRawMessage("validation.field.enum.choices.error", [rawVisit.visitLocation, "visitLocation"], ["visitLocation"])
        }

        //C6. Check Visit Date max date
        if (!isBlankVisitDate && rawVisit.visitDate > new Date()){
            errors << errorMessageService.getRawMessage("validation.field.date.not.greater.today", ["visitDate"], ["visitDate"])
        }

        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Household", "householdCode", rawVisit.householdCode], ["householdCode"])
        }
        //C4. Check Respondent reference existence
        if (!respondentExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "code", rawVisit.respondentCode], ["respondentCode"])
        }

        //C5. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(rawVisit.collectedBy)){
            errors << errorMessageService.getRawMessage("validation.field.user.dont.exists.error", [rawVisit.collectedBy], ["collectedBy"])
        }

        //C6. Check Duplicate of visitCode
        if (!isBlankCode && exists(rawVisit.code)){
            errors << errorMessageService.getRawMessage("validation.field.reference.duplicate.error", ["Visit", "code", rawVisit.code], ["code"])
        }

        return errors
    }

    private Visit newVisitInstance(RawVisit rv){

        def respondent = memberService.getMember(rv.respondentCode)
        def household = householdService.getHousehold(rv.householdCode)

        Visit visit = new Visit()

        visit.code = rv.code
        visit.household = household
        visit.householdCode = household.code

        visit.visitDate = rv.visitDate
        visit.visitLocation = VisitLocationItem.getFrom(rv.visitLocation)
        visit.visitLocationOther = rv.visitLocationOther

        visit.roundNumber = rv.roundNumber

        visit.respondent = respondent
        visit.respondentCode = respondent.code

        visit.hasInterpreter = rv.hasInterpreter
        visit.interpreterName = rv.interpreterName

        visit.gpsAccuracy = StringUtil.getDouble(rv.gpsAccuracy)
        visit.gpsAltitude = StringUtil.getDouble(rv.gpsAltitude)
        visit.gpsLatitude = StringUtil.getDouble(rv.gpsLatitude)
        visit.gpsLongitude = StringUtil.getDouble(rv.gpsLongitude)

        //set collected by info
        visit.collectedBy = userService.getUser(rv.collectedBy)
        visit.collectedDate = rv.collectedDate
        visit.updatedDate = rv.uploadedDate

        return visit

    }
    //</editor-fold>
}
