package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

@Transactional
class RoundService {

    def householdService
    def memberService
    def userService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="Round Utilities Methods">
    boolean exists(int roundNumber) {
        Round.countByRoundNumber(roundNumber) > 0
    }

    Round getRound(int roundNumber){
        return Round.findByRoundNumber(roundNumber)
    }
    
    int nextRoundNumber(){
        if (Round.count() > 0) {
            def last = Round.list().max{ it.roundNumber }?.roundNumber
            return last==null ? 0 : last+1
        }

        return 0
    }

    //</editor-fold>

    //<editor-fold desc="Round Factory/Manager Methods">
    RawExecutionResult<Round> createRound(Date roundStartDate, Date roundEndDate, String description) {

        /* Run Checks and Validations */

        def round = newRoundInstance(roundStartDate, roundEndDate, description)

        def errors = validate(round)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Round> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        //save round
        round = round.save(flush:true)

        //Validate using Gorm Validations
        if (round.hasErrors()){

            errors = errorMessageService.getRawMessages(round)

            RawExecutionResult<Round> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        RawExecutionResult<Round> obj = RawExecutionResult.newSuccessResult(round)
        return obj
    }

    ArrayList<RawMessage> validate(Round round){
        def errors = new ArrayList<RawMessage>()

        //code, householdCode, visitDate, visitLocation, visitLocationOther, roundNumber, respondentCode, hasInterpreter, interpreterName, gpsAccuracy, gpsAltitude, gpsLatitude, gpsLongitude
        def isBlankRoundNumber = round.roundNumber == null
        def isBlankStartDate = round.startDate == null
        def isBlankEndDate = round.endDate == null
        def isBlankDescription = StringUtil.isBlank(round.description)
        def checkDescription = false

        def roundExists = exists(round.roundNumber)

        //C1. Check Blank Fields (roundNumber)
        if (isBlankRoundNumber){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["roundNumber"], ["roundNumber"])
        }
        //C1. Check Blank Fields (startDate)
        if (isBlankStartDate){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["startDate"], ["startDate"])
        }
        //C1. Check Nullable Fields (endDate)
        if (isBlankEndDate){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["endDate"], ["endDate"])
        }
        //C1. Check Blank Fields (description)
        if (checkDescription && isBlankDescription){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["description"], ["description"])
        }

        //C6. Check Duplicate of Round using roundNumber
        if (!isBlankRoundNumber && roundExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.duplicate.error", ["Round", "roundNumber", round.roundNumber], ["roundNumber"])
        }

        //C6. Check Round (start/end)Date and today
        /*if (!isBlankStartDate && round.startDate > new Date()){
            errors << errorMessageService.getRawMessage("validation.field.date.not.greater.today", [round.startDate], ["startDate"])
        }
        if (!isBlankEndDate && round.endDate > new Date()){
            errors << errorMessageService.getRawMessage("validation.field.date.not.greater.today", [round.endDate], ["endDate"])
        }*/
        if (!isBlankStartDate && !isBlankEndDate && (round.startDate > round.endDate)){
            errors << errorMessageService.getRawMessage("validation.field.startdate.not.greater.enddate", [round.startDate, round.endDate], ["startDate","endDate"])
        }

        if (errors.size()==0){//no errors
            //def lastRound = Round.list().max{ it.roundNumber }

            def roundsStarts = Round.executeQuery("select r from Round r where (:sdate >= r.startDate and :sdate <= r.endDate)", [sdate: round.startDate])
            def roundsEnds = Round.executeQuery("select r from Round r where (:edate >= r.startDate and :edate <= r.endDate)", [edate: round.endDate])

            if (roundsStarts.size() > 0) { //we have overlapping dates with startDate
                def rounds = roundsStarts.collect { it.roundNumber }
                errors << errorMessageService.getRawMessage("validation.field.round.startdate.overlaps.error", [round.startDate, rounds], ["startDate"])
            }

            if (roundsEnds.size() > 0) { //we have overlapping dates with endDate
                def rounds = roundsEnds.collect { it.roundNumber }
                errors << errorMessageService.getRawMessage("validation.field.round.enddate.overlaps.error", [round.endDate, rounds], ["endDate"])
            }
        }

        return errors
    }

    private Round newRoundInstance(Date roundStartDate, Date roundEndDate, String description){

        Round round = new Round()

        round.roundNumber = nextRoundNumber()
        round.startDate = roundStartDate
        round.endDate = roundEndDate
        round.description = description

        return round

    }
    //</editor-fold>
}
