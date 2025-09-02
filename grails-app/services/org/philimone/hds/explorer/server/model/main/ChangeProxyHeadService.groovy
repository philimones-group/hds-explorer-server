package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.DateUtil
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawHouseholdProxyHead
import org.philimone.hds.explorer.server.model.enums.ProxyHeadChangeReason
import org.philimone.hds.explorer.server.model.enums.ProxyHeadRole
import org.philimone.hds.explorer.server.model.enums.ProxyHeadType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.ValidatableStatus
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDate

@Transactional
class ChangeProxyHeadService {

    /**
     * Validate data columns
     * 1.
     */

    def householdService
    def memberService
    def headRelationshipService
    def visitService
    def residencyService
    def coreExtensionService
    def userService
    def errorMessageService

    //<editor-fold desc="ProxyHead Utilities Methods">

    HouseholdProxyHead getLastHouseholdProxyHead(String householdCode) {
        if (StringUtil.isBlank(householdCode)) return null;

        return HouseholdProxyHead.findByHouseholdCode(householdCode, [sort: 'startDate', order: 'desc'])
    }

    //</editor-fold>

    //<editor-fold desc="HouseholdProxyHead Factory/Manager Methods">

    RawExecutionResult<HouseholdProxyHead> createChangeProxyHead(RawHouseholdProxyHead rawHouseholdProxyHead) {
        /* Run Checks and Validations */

        //get current data
        def household = householdService.getHousehold(rawHouseholdProxyHead.householdCode)
        
        def errors = validate(rawHouseholdProxyHead)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<HouseholdProxyHead> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_PROXY_HEAD, errors)
            return obj
        }
        
        def currentProxyHead = getLastHouseholdProxyHead(household.code)
        def closeEventDate = GeneralUtil.addDaysToDate(rawHouseholdProxyHead.eventDate, -1)  //the day of moving will be set 1 day before changing head - the last day the member was related to the current head of household

        //close previous proxy head if needed
        if (currentProxyHead != null) {
            currentProxyHead.endDate = closeEventDate
            currentProxyHead.save(flush:true)
            
            if (currentProxyHead.hasErrors()){
                errors = errorMessageService.getRawMessages(currentProxyHead)
                
                RawExecutionResult<HouseholdProxyHead> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_PROXY_HEAD, errors)
                return obj
            }
        }

        //create new proxy head
        def newHouseholdProxyHead = newHouseholdProxyHeadInstance(rawHouseholdProxyHead)
        def resultNewProxyHead = newHouseholdProxyHead.save(flush: true)

        if (resultNewProxyHead == null || newHouseholdProxyHead.hasErrors()) {
            errors += errorMessageService.getRawMessages(currentProxyHead)

            //restore previous
            if (currentProxyHead != null) {
                currentProxyHead.endDate = null
                currentProxyHead.save(flush: true)
            }

            RawExecutionResult<HouseholdProxyHead> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_PROXY_HEAD, errors)
            return obj
        }

        //update household
        if (household != null) {
            household.proxyHead = newHouseholdProxyHead
            household.save(flush:true)

            if (household.hasErrors()) {
                errors << errorMessageService.getRawMessages(household)
            }
        }

        
        //Roolback everything if an error ocurred - delete results
        if (!errors.empty) {
            deleteAllCreatedRecords(currentProxyHead, household, resultNewProxyHead)

            RawExecutionResult<HouseholdProxyHead> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_PROXY_HEAD, errors)
            return obj
        }

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertHouseholdProxyHeadExtension(rawHouseholdProxyHead, newHouseholdProxyHead)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail

            deleteAllCreatedRecords(currentProxyHead, household, resultNewProxyHead)

            println "Failed to insert extension: ${resultExtension.errorMessage}"

            errors << new RawMessage(resultExtension.errorMessage, null)
            RawExecutionResult<HouseholdProxyHead> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_PROXY_HEAD, errors)
            return obj
        }

        RawExecutionResult<HouseholdProxyHead> obj = RawExecutionResult.newSuccessResult(RawEntity.CHANGE_PROXY_HEAD, resultNewProxyHead, errors)
        return obj
    }

    private void deleteAllCreatedRecords(HouseholdProxyHead currentProxyHead, Household household, HouseholdProxyHead resultNewProxyHead) {
        //restore previous
        if (currentProxyHead != null) {
            currentProxyHead.endDate = null
            currentProxyHead.save(flush: true)
        }

        household.proxyHead = currentProxyHead
        household.save(flush: true)

        //delete proxy head
        if (resultNewProxyHead != null) {
            resultNewProxyHead.delete(flush: true)
        }
    }

    ArrayList<RawMessage> validate(RawHouseholdProxyHead rawHouseholdProxyHead) {
        def dateUtil = DateUtil.getInstance()

        //visitCode - must exists
        //householdCode - must exists        
        //proxyHeadType                 // RESIDENT | NON_RESIDENT | NON_DSS_MEMBER
        //proxyHeadCode                 // DSS member code (when applicable)
        //proxyHeadName                 // external name (when NON_DSS_MEMBER)
        //proxyHeadRole                 // e.g., ADM/WRD/...        
        //eventDate
        //reason                     // e.g., REP/RES/RPH/HTC/DEC/EXP/OTH
        //reasonOther

        def errors = new ArrayList<RawMessage>()

        def isBlankVisitCode = StringUtil.isBlank(rawHouseholdProxyHead.visitCode)
        def isBlankHouseholdCode = StringUtil.isBlank(rawHouseholdProxyHead.householdCode)
        def isBlankProxyHeadType = StringUtil.isBlank(rawHouseholdProxyHead.proxyHeadType)
        def isBlankProxyHeadCode = StringUtil.isBlank(rawHouseholdProxyHead.proxyHeadCode)
        def isBlankProxyHeadName = StringUtil.isBlank(rawHouseholdProxyHead.proxyHeadName)
        def isBlankProxyHeadRole = StringUtil.isBlank(rawHouseholdProxyHead.proxyHeadRole)
        def isBlankEventDate = StringUtil.isBlankDate(rawHouseholdProxyHead.eventDate)
        def isBlankReason = StringUtil.isBlank(rawHouseholdProxyHead.reason)

        def household = !isBlankHouseholdCode ? householdService.getHousehold(rawHouseholdProxyHead.householdCode) : null
        def headType = !isBlankProxyHeadType ? ProxyHeadType.getFrom(rawHouseholdProxyHead.proxyHeadType) : null
        def proxyHead = !isBlankProxyHeadCode ? memberService.getMember(rawHouseholdProxyHead.proxyHeadCode) : null
        def visit = visitService.getVisit(rawHouseholdProxyHead.visitCode)

        def householdExists = household != null
        def proxyHeadExists = proxyHead != null
        def visitExists = visit != null

        //C1. Check Blank Fields (visitCode)
        if (isBlankVisitCode){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.blank", ["visitCode"], ["visitCode"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (proxyHeadType)
        if (isBlankProxyHeadType){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.blank", ["proxyHeadType"], ["proxyHeadType"])
        }
        //C1. Check Blank Fields (proxyHeadCode)
        if (!isBlankProxyHeadType && headType != ProxyHeadType.NON_DSS_MEMBER && isBlankProxyHeadCode){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.blank", ["proxyHeadCode"], ["proxyHeadCode"])
        }
        //C1. Check Blank Fields (proxyHeadRole)
        if (isBlankProxyHeadRole){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.blank", ["proxyHeadRole"], ["proxyHeadRole"])
        }
        //C1. Check Nullable Fields (eventDate)
        if (isBlankEventDate){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.blank", ["eventDate"], ["eventDate"])
        }
        //C1. Check Nullable Fields (reason)
        if (isBlankReason){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.blank", ["reason"], ["reason"])
        }

        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdExists){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.reference.error", ["Household", "householdCode", rawHouseholdProxyHead.householdCode], ["householdCode"])
        }
        //C4. Check headType enum choice existence
        if (!isBlankProxyHeadType && headType==null){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.enum.choices.error", [rawHouseholdProxyHead.proxyHeadType, "proxyHeadType"], ["proxyHeadType"])
        }
        //C4. Check proxyHead Member reference existence
        if (!isBlankProxyHeadType && headType != ProxyHeadType.NON_DSS_MEMBER && !isBlankProxyHeadCode && !proxyHeadExists){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.reference.error", ["Member", "memberCode", rawHouseholdProxyHead.proxyHeadCode], ["proxyHeadCode"])
        }

        //C5. Check eventDate max date
        if (!isBlankEventDate && rawHouseholdProxyHead.eventDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.date.not.greater.today", ["rawHouseholdProxyHead.eventDate"], ["eventDate"])
        }
        //C5.2. Check eventDate Dates against DOB (for the new head of household)
        if (!isBlankEventDate && proxyHeadExists && rawHouseholdProxyHead.eventDate < proxyHead?.dob){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.dob.not.greater.date", ["rawHouseholdProxyHead.eventDate", dateUtil.formatYMD(proxyHead?.dob)], ["eventDate","member.dob"])
        }
        //C6. Check Age of the new head of Household
        //if (proxyHeadExists && proxyHead?.age < Codes.MIN_HEAD_AGE_VALUE && !onlyMinorsLeftToBeHead){
        //    errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.dob.head.minage.error", [dateUtil.formatYMD(proxyHead.dob), Codes.MIN_HEAD_AGE_VALUE+""], ["member.dob"])
        //}

        //Validation part 2
        if (errors.empty){

            def oldProxyHead = getLastHouseholdProxyHead(rawHouseholdProxyHead.householdCode) //until here the oldHead is the current head

            if (oldProxyHead != null) {

                if (rawHouseholdProxyHead.eventDate != null && rawHouseholdProxyHead.eventDate < oldProxyHead.startDate) {
                    //The event date cannot be before the [start date] of the [previous Proxy Head record].

                    errors << errorMessageService.getRawMessage(RawEntity.CHANGE_PROXY_HEAD, "validation.field.householdProxyHead.event_date.before.start_date.error", [], ["eventDate","rawHouseholdProxyHead.startDate"])
                }


            }


        }

        return errors

    }

    HouseholdProxyHead newHouseholdProxyHeadInstance(RawHouseholdProxyHead rawHouseholdProxyHead) {
        if (rawHouseholdProxyHead == null) return null
        
        def householdProxyHead = new HouseholdProxyHead()

		householdProxyHead.visit = visitService.getVisit(rawHouseholdProxyHead.visitCode)
		householdProxyHead.visitCode = rawHouseholdProxyHead.visitCode
		householdProxyHead.household = householdService.getHousehold(rawHouseholdProxyHead.householdCode)
		householdProxyHead.householdCode = rawHouseholdProxyHead.householdCode
		householdProxyHead.proxyHeadType = ProxyHeadType.getFrom(rawHouseholdProxyHead.proxyHeadType)
        householdProxyHead.proxyHeadCode = rawHouseholdProxyHead.proxyHeadCode
        householdProxyHead.proxyHeadName = rawHouseholdProxyHead.proxyHeadName
        householdProxyHead.proxyHead = StringUtil.isBlank(rawHouseholdProxyHead.proxyHeadCode) ? null : memberService.getMember(rawHouseholdProxyHead.proxyHeadCode)
		householdProxyHead.proxyHeadRole = ProxyHeadRole.getFrom(rawHouseholdProxyHead.proxyHeadRole)
		householdProxyHead.startDate = rawHouseholdProxyHead.eventDate
		householdProxyHead.endDate = null;
		householdProxyHead.reason = ProxyHeadChangeReason.getFrom(rawHouseholdProxyHead.reason)
		householdProxyHead.reasonOther = rawHouseholdProxyHead.reasonOther

		householdProxyHead.status = ValidatableStatus.ACTIVE

        return householdProxyHead
    }

    //</editor-fold>
}
