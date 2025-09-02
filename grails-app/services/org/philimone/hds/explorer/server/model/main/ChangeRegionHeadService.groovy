package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.DateUtil
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawChangeRegionHead
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.ValidatableStatus
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
import org.philimone.hds.explorer.server.model.enums.temporal.RegionHeadEndType
import org.philimone.hds.explorer.server.model.enums.temporal.RegionHeadStartType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDate

@Transactional
class ChangeRegionHeadService {

    /**
     * Validate data columns
     * 1. Close old Head RegionHeadRelationship
     * 2. Close all old Head Relationship
     * 3. Create new Head RegionHeadRelationship
     * 4. Create new RegionHeadRelationship for all members of Household (living members - not deaths and outmigrations)
     */

    def regionService
    def memberService
    def headRelationshipService
    def visitService
    def coreExtensionService
    def userService
    def errorMessageService

    //<editor-fold desc="RegionHeadRelationship Utilities Methods">

    RegionHeadRelationship getLastRegionHeadRelationship(Region region){
        if (region != null && region?.id != null){
            def relationships = RegionHeadRelationship.executeQuery("select r from RegionHeadRelationship r where r.region=?0 and (r.status <> ?1 or r.status is null) order by r.startDate desc", [region, ValidatableStatus.TEMPORARILY_INACTIVE], [offset:0, max:1]) // limit 1

            if (relationships != null && relationships.size()>0) {
                return relationships.first()
            }
        }

        return null
    }

    Member getRegionHead(Region region){

        if (region != null && region?.id != null){
            def members = RegionHeadRelationship.executeQuery("select r.head from RegionHeadRelationship r where r.region=?0 and r.endType=?1 and (r.status <> ?2 or r.status is null) order by r.startDate desc", [region, RegionHeadEndType.NOT_APPLICABLE, ValidatableStatus.TEMPORARILY_INACTIVE], [offset:0, max:1]) // limit 1

            if (members != null && members.size()>0) {
                return members.first()
            }
        }

        return null
    }

    boolean isCurrentHeadOfRegion(Region region, Member member){

        if (region == null || member == null) return false

        def head = getRegionHead(region)

        if (head == null) return false

        return head.code?.equalsIgnoreCase(member.code)
    }


    //</editor-fold>

    //<editor-fold desc="RegionHeadRelationship Factory/Manager Methods">

    RawExecutionResult<RegionHeadRelationship> createChangeRegionHead(RawChangeRegionHead changeRegionHead) {
        /* Run Checks and Validations */

        def errors = validate(changeRegionHead)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<RegionHeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_HEAD_OF_REGION, errors)
            return obj
        }

        //get current data
        def region = regionService.getRegion(changeRegionHead.regionCode)
        def previousRegionHeadRelationship = getLastRegionHeadRelationship(region)
        def newHead = memberService.getMember(changeRegionHead.newHeadCode)
        def previousHead = previousRegionHeadRelationship?.head
        def previousEndType = previousRegionHeadRelationship?.endType
        def previousEndDate = previousRegionHeadRelationship?.endDate
        def newStartDate = changeRegionHead.eventDate
        def newEndDate = GeneralUtil.addDaysToDate(newStartDate, -1)  //the day of moving will be set 1 day before changing head - the last day the member was related to the current head of region

        //close previous region head relationship if needed
        if (previousRegionHeadRelationship != null && (previousRegionHeadRelationship.endType==RegionHeadEndType.NOT_APPLICABLE)) {
            previousRegionHeadRelationship.endType = RegionHeadEndType.CHANGE_OF_HEAD_OF_REGION
            previousRegionHeadRelationship.endDate = newEndDate
        }

        //create new region head relationship
        def newRegionHeadRelationship = new RegionHeadRelationship()
        newRegionHeadRelationship.region = region
        newRegionHeadRelationship.regionCode = region.code
        newRegionHeadRelationship.head = newHead
        newRegionHeadRelationship.headCode = newHead.code
        newRegionHeadRelationship.startType = RegionHeadStartType.NEW_HEAD_OF_REGION
        newRegionHeadRelationship.startDate = newStartDate
        newRegionHeadRelationship.endType = RegionHeadEndType.NOT_APPLICABLE
        newRegionHeadRelationship.endDate = null
        newRegionHeadRelationship.reason = changeRegionHead.reason
        newRegionHeadRelationship.status = ValidatableStatus.ACTIVE

        //save new head
        region.head = newHead
        region.headCode = newHead.code

        //execute everything if any error try to restore previous states

        //save - close previous relationship
        if (errors.empty && previousRegionHeadRelationship != null) {
            previousRegionHeadRelationship.save(flush: true)

            if (previousRegionHeadRelationship.hasErrors()){
                errors = errorMessageService.getRawMessages(RawEntity.CHANGE_HEAD_OF_REGION, previousRegionHeadRelationship)

                RawExecutionResult<RegionHeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_HEAD_OF_REGION, errors)
                return obj
            }
        }

        //save - create new region head relationship
        if (errors.empty) {
            newRegionHeadRelationship.save(flush: true)

            if (newRegionHeadRelationship.hasErrors()){

                //we must restore back the previousRegionHeadRelationship info
                if (previousRegionHeadRelationship != null) {
                    previousRegionHeadRelationship.endType = previousEndType
                    previousRegionHeadRelationship.endDate = previousEndDate
                    previousRegionHeadRelationship.save(flush:true)
                }

                errors = errorMessageService.getRawMessages(RawEntity.CHANGE_HEAD_OF_REGION, newRegionHeadRelationship)

                RawExecutionResult<RegionHeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_HEAD_OF_REGION, errors)
                return obj
            }
        }

        //save - region new head
        if (errors.empty) {
            region.save(flush: true)

            if (region.hasErrors()){

                //roolback data
                if (previousRegionHeadRelationship != null) {
                    previousRegionHeadRelationship.endType = previousEndType
                    previousRegionHeadRelationship.endDate = previousEndDate
                    previousRegionHeadRelationship.save(flush: true)
                }

                newRegionHeadRelationship.delete(flush: true)

                errors = errorMessageService.getRawMessages(RawEntity.CHANGE_HEAD_OF_REGION, region)

                RawExecutionResult<RegionHeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_HEAD_OF_REGION, errors)
                return obj
            }
        }
        
        //Roolback everything if an error ocurred - delete results
        if (!errors.empty) {
            deleteAllCreatedRecords(newRegionHeadRelationship, previousEndType, previousRegionHeadRelationship, previousEndDate, previousHead, region)

            RawExecutionResult<RegionHeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_HEAD_OF_REGION, errors)
            return obj
        }

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertChangeRegionHeadExtension(changeRegionHead, null)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail

            deleteAllCreatedRecords(newRegionHeadRelationship, previousEndType, previousRegionHeadRelationship, previousEndDate, previousHead, region)

            println "Failed to insert extension: ${resultExtension.errorMessage}"

            errors << new RawMessage(resultExtension.errorMessage, null)
            RawExecutionResult<RegionHeadRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.CHANGE_HEAD_OF_REGION, errors)
            return obj
        }

        RawExecutionResult<RegionHeadRelationship> obj = RawExecutionResult.newSuccessResult(RawEntity.CHANGE_HEAD_OF_REGION, newRegionHeadRelationship, errors)
        return obj
    }

    private void deleteAllCreatedRecords(RegionHeadRelationship newRegionHeadRelationship, RegionHeadEndType previousEndType, RegionHeadRelationship previousRegionHeadRelationship, LocalDate previousEndDate, Member previousHead, Region region) {
        //delete created records
        newRegionHeadRelationship.delete(flush: true)

        //unclosed closed relationship
        if (previousRegionHeadRelationship != null) {
            previousRegionHeadRelationship.endType = previousEndType
            previousRegionHeadRelationship.endDate = previousEndDate
            previousRegionHeadRelationship.save(flush: true)
        }

        //restore previous head
        region.head = previousHead
        region.headCode = previousHead?.code
        region.save(flush: true)
    }

    ArrayList<RawMessage> validate(RawChangeRegionHead changeHead) {
        def dateUtil = DateUtil.getInstance()

        //visitCode - must exists
        //regionCode - must exists
        //oldHeadCode - must be the current household head
        //newHeadCode - must not be head of any household
        //eventDate
        //reason

        def errors = new ArrayList<RawMessage>()

        def isBlankVisitCode = StringUtil.isBlank(changeHead.visitCode)
        def isBlankRegionCode = StringUtil.isBlank(changeHead.regionCode)
        def isBlankOldHeadCode = StringUtil.isBlank(changeHead.oldHeadCode)
        def isBlankNewHeadCode = StringUtil.isBlank(changeHead.newHeadCode)
        def isBlankEventDate = StringUtil.isBlankDate(changeHead.eventDate)

        def region = !isBlankRegionCode ? regionService.getRegion(changeHead.regionCode) : null
        def oldHead = !isBlankOldHeadCode ? memberService.getMember(changeHead.oldHeadCode) : null
        def newHead = !isBlankNewHeadCode ? memberService.getMember(changeHead.newHeadCode) : null
        def visit = visitService.getVisit(changeHead.visitCode)

        def regionExists = region != null
        def oldHeadExists = oldHead != null
        def newHeadExists = newHead != null
        def visitExists = visit != null

        //C1. Check Blank Fields (regionCode) - NOT REQUIRED
        if (isBlankRegionCode){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.blank", ["regionCode"], ["regionCode"])
        }
        //C1. Check Blank Fields (oldHeadCode) - NOT REQUIRED
        //if (isBlankOldHeadCode){
        //    errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.blank", ["oldHeadCode"], ["oldHeadCode"])
        //}
        //C1. Check Blank Fields (newHeadCode)
        if (isBlankNewHeadCode){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.blank", ["newHeadCode"], ["newHeadCode"])
        }
        //C1. Check Nullable Fields (eventDate)
        if (isBlankEventDate){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.blank", ["eventDate"], ["eventDate"])
        }
        //C1. Check Blank Fields (visitCode) - NOT REQUIRED
        //if (isBlankVisitCode){
        //    errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.blank", ["visitCode"], ["visitCode"])
        //}

        //C4. Check Region reference existence
        if (!isBlankRegionCode && !regionExists){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.reference.error", ["Region", "regionCode", changeHead.regionCode], ["regionCode"])
        }
        //C4. Check OldHead Member reference existence
        if (!isBlankOldHeadCode && !oldHeadExists){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.reference.error", ["Member", "memberCode", changeHead.oldHeadCode], ["oldHeadCode"])
        }
        //C4. Check NewHead Member reference existence
        if (!isBlankNewHeadCode && !newHeadExists){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.reference.error", ["Member", "memberCode", changeHead.newHeadCode], ["newHeadCode"])
        }

        //C5. Check eventDate max date
        if (!isBlankEventDate && changeHead.eventDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.date.not.greater.today", ["changeHead.eventDate"], ["eventDate"])
        }
        //C5.2. Check eventDate Dates against DOB (for the new head of household)
        if (!isBlankEventDate && newHeadExists && changeHead.eventDate < newHead.dob){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.dob.not.greater.date", ["changeHead.eventDate", dateUtil.formatYMD(newHead.dob)], ["eventDate","member.dob"])
        }
        //C6. Check Age of the new head of Household
        if (newHeadExists && GeneralUtil.getAge(newHead.dob) < Codes.MIN_HEAD_AGE_VALUE){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.changeregionhead.dob.head.minage.error", [dateUtil.formatYMD(newHead.dob), Codes.MIN_HEAD_AGE_VALUE+""], ["member.dob"])
        }

        //C7. Check If the Current Head is the Old Head
        if (oldHeadExists && !isCurrentHeadOfRegion(region, oldHead) ){
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.changeregionhead.not.current.head.error", [changeHead.oldHeadCode, changeHead.regionCode], ["oldHeadCode"])
        }

        //C9. Check oldHeadCode and newHeadCode equality
        if (!isBlankOldHeadCode && !isBlankNewHeadCode && changeHead.oldHeadCode.equalsIgnoreCase(changeHead.newHeadCode)) {
            errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.changeregionhead.head.same.member.error", [changeHead.oldHeadCode], ["newHeadCode", "oldHeadCode"])
        }

        //Validation part 2
        if (errors.empty){

            def previousRegionHeadRelationship = getLastRegionHeadRelationship(region) //until here the oldHead is the current head
            def newStartDate = changeHead.eventDate
            def closeEventDate = GeneralUtil.addDaysToDate(newStartDate, -1)  //the day of moving will be set 1 day before changing head - the last day the member was related to the current head of household

            if (previousRegionHeadRelationship == null) {
                return errors
            }

            //previous head exists

            //validate close - (must set endDate and endType on previous) - if its closed/opened we must validate the previous.endDate/closeEventDate with the newStartDate
            if (previousRegionHeadRelationship.endDate != null && previousRegionHeadRelationship.endType != RegionHeadEndType.NOT_APPLICABLE) {
                //already closed
                closeEventDate = previousRegionHeadRelationship.endDate
            }

            //validate creation
            //check closeEventDate/endDate is greater than new startDate
            if (closeEventDate >= newStartDate){
                errors << errorMessageService.getRawMessage(RawEntity.CHANGE_HEAD_OF_REGION, "validation.field.headRelationship.enddate.greater.new.startdate.error", null, ["previous.endDate"])
            }

        }


        return errors

    }

    //</editor-fold>


}
