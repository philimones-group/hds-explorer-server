package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawMaritalRelationship
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStartStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes
import org.springframework.context.i18n.LocaleContextHolder

@Transactional
class MaritalRelationshipService {

    def householdService
    def memberService
    def userService
    def errorMessageService
    def messageSource

    //<editor-fold desc="MaritalRelationship Utilities Methods">
    MaritalRelationship getCurrentMaritalRelationship(Member member) {
        if (member != null && member.id != null) {

            def maritalRelationshipsA = MaritalRelationship.executeQuery("select r from MaritalRelationship r where r.memberA.id=? order by r.startDate desc", [member.id], [offset:0, max:1]) // limit 1
            def maritalRelationshipsB = MaritalRelationship.executeQuery("select r from MaritalRelationship r where r.memberB.id=? order by r.startDate desc", [member.id], [offset:0, max:1]) // limit 1
            MaritalRelationship relationA = (maritalRelationshipsA != null && maritalRelationshipsA.size() > 0) ? maritalRelationshipsA.first() : null
            MaritalRelationship relationB = (maritalRelationshipsB != null && maritalRelationshipsB.size() > 0) ? maritalRelationshipsB.first() : null

            if (relationA == null) return relationB
            if (relationB == null) return relationA
            if (relationA.startDate > relationB.startDate){ //get the most recent relationship
                return relationA
            }else {
                return relationB
            }

        }
        return null
    }

    MaritalRelationship getCurrentMaritalRelationship(Member memberA, Member memberB) {
        if (member != null && member.id != null) {

            def maritalRelationshipsA = MaritalRelationship.executeQuery("select r from MaritalRelationship r where r.memberA.id=? and r.memberB.id=? order by r.startDate desc", [memberA.id, memberB.id], [offset:0, max:1]) // limit 1
            def maritalRelationshipsB = MaritalRelationship.executeQuery("select r from MaritalRelationship r where r.memberB.id=? and r.memberB.id=? order by r.startDate desc", [memberB.id, memberA.id], [offset:0, max:1]) // limit 1
            MaritalRelationship relationA = (maritalRelationshipsA != null && maritalRelationshipsA.size() > 0) ? maritalRelationshipsA.first() : null
            MaritalRelationship relationB = (maritalRelationshipsB != null && maritalRelationshipsB.size() > 0) ? maritalRelationshipsB.first() : null

            if (relationA == null) return relationB
            if (relationB == null) return relationA
            if (relationA.startDate > relationB.startDate){ //get the most recent relationship
                return relationA
            }else {
                return relationB
            }

        }
        return null
    }

    MaritalStatus convertFrom(MaritalStartStatus status){
        switch (status){
            case MaritalStartStatus.MARRIED : return MaritalStatus.MARRIED
            case MaritalStartStatus.LIVING_TOGHETER : return MaritalStatus.LIVING_TOGHETER
        }

        return null
    }

    MaritalStatus convertFrom(MaritalEndStatus status){
        switch (status){
            //case MaritalEndStatus.NOT_APPLICABLE : return MaritalStatus.MARRIED
            case MaritalEndStatus.DIVORCED : return MaritalStatus.DIVORCED
            case MaritalEndStatus.SEPARATED : return MaritalStatus.SEPARATED
            case MaritalEndStatus.WIDOWED : return MaritalStatus.WIDOWED
        }

        return null
    }

    List<RawMessage> updatesAfterCreatingRelationship(MaritalRelationship maritalRelationship) {

        def errors = [] as ArrayList<RawMessage>
        def memberA = maritalRelationship.memberA.refresh()
        def memberB = maritalRelationship.memberB.refresh()

        //update spouse status
        memberA.maritalStatus = convertFrom(maritalRelationship.startStatus)
        memberA.spouse = memberB
        memberA.spouseCode = memberB.code
        memberA.spouseName = memberB.name

        memberB.maritalStatus = convertFrom(maritalRelationship.startStatus)
        memberB.spouse = memberA
        memberB.spouseCode = memberA.code
        memberB.spouseName = memberA.name


        memberA.save(flush: true)
        memberB.save(flush: true)


        //get errors if they occur and send with the success report
        if (memberA.hasErrors()) {
            errors << errorMessageService.getRawMessages(memberA)
        }
        if (memberB.hasErrors()) {
            errors << errorMessageService.getRawMessages(memberB)
        }

        return errors
    }

    List<RawMessage> updatesAfterClosingRelationship(MaritalRelationship maritalRelationship){

        def errors = [] as ArrayList<RawMessage>
        def memberA = maritalRelationship.memberA.refresh()
        def memberB = maritalRelationship.memberB.refresh()

        //update spouse status
        memberA.maritalStatus = convertFrom(maritalRelationship.endStatus)
        //memberA.spouse = memberB
        //memberA.spouseCode = memberB.code
        //memberA.spouseName = memberB.name

        memberB.maritalStatus = convertFrom(maritalRelationship.endStatus)
        //memberB.spouse = memberA
        //memberB.spouseCode = memberA.code
        //memberB.spouseName = memberA.name

        //get errors if they occur and send with the success report
        if (memberA.hasErrors()) {
            errors << errorMessageService.getRawMessages(memberA)
        }
        if (memberB.hasErrors()) {
            errors << errorMessageService.getRawMessages(memberB)
        }

        return errors
    }
    //</editor-fold>

    //<editor-fold desc="MaritalRelationship Factory/Manager Methods">
    RawExecutionResult<MaritalRelationship> createMaritalRelationship(RawMaritalRelationship rawMaritalRelationship) {

        /* Run Checks and Validations */

        def errors = validateCreateMaritalRelationship(rawMaritalRelationship)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        def maritalRelationship = newMaritalRelationshipInstance(rawMaritalRelationship)

        maritalRelationship = maritalRelationship.save(flush:true)

        //Validate using Gorm Validations
        if (maritalRelationship.hasErrors()){

            errors = errorMessageService.getRawMessages(maritalRelationship)

            RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        //Update Member with start status
        errors << updatesAfterCreatingRelationship(maritalRelationship)

        RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newSuccessResult(maritalRelationship, errors)
        return obj
    }

    RawExecutionResult<MaritalRelationship> closeMaritalRelationship(RawMaritalRelationship rawMaritalRelationship) {

        /* Run Checks and Validations */

        def errors = validateCloseMaritalRelationship(rawMaritalRelationship)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        def maritalRelationship = closeMaritalRelationshipInstance(rawMaritalRelationship)

        maritalRelationship = maritalRelationship.save(flush:true)
        //Validate using Gorm Validations
        if (maritalRelationship.hasErrors()){

            errors = errorMessageService.getRawMessages(maritalRelationship)

            RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        //Update Household - remove head if member was one
        errors = updatesAfterClosingRelationship(maritalRelationship)

        RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newSuccessResult(maritalRelationship, errors)
        return obj
    }

    ArrayList<RawMessage> validateCreateMaritalRelationship(RawMaritalRelationship maritalRelationship){
        def errors = new ArrayList<RawMessage>()

        def isBlankMemberACode = StringUtil.isBlank(maritalRelationship.memberA)
        def isBlankMemberBCode = StringUtil.isBlank(maritalRelationship.memberB)
        def isBlankStartStatus = StringUtil.isBlank(maritalRelationship.startStatus)
        def isNullStartDate = maritalRelationship.startDate == null

        def memberA = !isBlankMemberACode ? memberService.getMember(maritalRelationship.memberA) : null
        def memberB = !isBlankMemberBCode ? memberService.getMember(maritalRelationship.memberB) : null
        def memberAExists = memberA != null
        def memberBExists = memberB != null

        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberACode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["memberA"], ["memberA"])
        }
        //C1. Check Blank Fields (headCode)
        if (isBlankMemberBCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["memberB"], ["memberB"])
        }
        //C1. Check Blank Fields (startStatus)
        if (isBlankStartStatus){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["startStatus"], ["startStatus"])
        }
        //C1. Check Nullable Fields (startDate)
        if (isNullStartDate){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["startDate"], ["startDate"])
        }
        //C3. Validate startStatus Enum Options
        if (!isBlankStartStatus && MaritalStartStatus.getFrom(maritalRelationship.startStatus)==null){
            errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.startstatus.valid.error", [maritalRelationship.startStatus], ["startStatus"])
        }
        //C4. Check Member A reference existence
        if (!isBlankMemberACode && !memberAExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "memberA", maritalRelationship.memberA], ["memberA"])
        }
        //C4. Check Member B reference existence
        if (!isBlankMemberBCode && !memberBExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "memberB", maritalRelationship.memberB], ["memberB"])
        }
        //C5. Check startdate max date
        if (!isNullStartDate && maritalRelationship.startDate > new Date()){
            errors << errorMessageService.getRawMessage("validation.field.date.not.greater.today", [maritalRelationship.startDate], ["startDate"])
        }
        //C6. Check Age of Member A
        if (memberAExists && GeneralUtil.getAge(memberA.dob)< Codes.MIN_SPOUSE_AGE_VALUE ){
            errors << errorMessageService.getRawMessage("validation.field.dob.spouse.minage.error", [memberA.dob, Codes.MIN_SPOUSE_AGE_VALUE], ["member.dob"])
        }
        //C6. Check Age of Member B
        if (memberBExists && GeneralUtil.getAge(memberB.dob)< Codes.MIN_SPOUSE_AGE_VALUE ){
            errors << errorMessageService.getRawMessage("validation.field.dob.spouse.minage.error", [memberB.dob, Codes.MIN_SPOUSE_AGE_VALUE], ["member.dob"])
        }
        //C7. Check Gender as Optional
        if (Codes.GENDER_CHECKING && memberAExists && memberBExists){
            //who's male whos female
            if (memberA.gender == memberB.gender){
                def genderA = messageSource.getMessage(memberA.gender.name, null, LocaleContextHolder.getLocale())
                def genderB = messageSource.getMessage(memberB.gender.name, null, LocaleContextHolder.getLocale())
                errors << errorMessageService.getRawMessage("validation.field.gender.spouse.error", [genderA, genderB], ["member.gender"])
            }
        }
        //C8. Check Death Status of member A
        if (memberAExists && memberService.isDead(memberA)){
            errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.death.a.or.b.error", ["memberA", memberA.code], ["memberA_code"])
        }
        //C9. Check Death Status of member B
        if (memberBExists && memberService.isDead(memberB)){
            errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.death.a.or.b.error", ["memberB", memberB.code], ["memberB_code"])
        }


        //Validation part 2: Previous MaritalRelationship against new MaritalRelationship
        if (memberAExists && memberBExists){
            def currentA = getCurrentMaritalRelationship(memberA)
            def currentB = getCurrentMaritalRelationship(memberB)
            def newStartDate = maritalRelationship.startDate

            //P1. Check if last relationship of A still opened
            if (currentA != null && (currentA.endStatus == null || currentA.endStatus == MaritalEndStatus.NOT_APPLICABLE)){
                errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.prev.relationships.ab.closed.error", [memberA.code], ["previous.memberA.endStatus"])
            }
            //P1. Check if last relationship of B still opened
            if (currentB != null && (currentB.endStatus == null || currentB.endStatus == MaritalEndStatus.NOT_APPLICABLE)){
                errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.prev.relationships.ab.closed.error", [memberB.code], ["previous.memberB.endStatus"])
            }
            //P2. Check If endDate is greater than new startDate, for memberA
            if (currentA != null && (currentA.endDate != null && currentA.endDate >= newStartDate)){
                errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.prev.enddate.before.n.startdate.error", [memberA.code], ["previous.memberA.endDate"])
            }
            //P2. Check If endDate is greater than new startDate, for memberB
            if (currentA != null && (currentB.endDate != null && currentB.endDate >= newStartDate)){
                errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.prev.enddate.before.n.startdate.error", [memberB.code], ["previous.memberB.endDate"])
            }
        }


        return errors
    }

    ArrayList<RawMessage> validateCloseMaritalRelationship(RawMaritalRelationship maritalRelationship){
        def errors = new ArrayList<RawMessage>()


        def isBlankMemberACode = StringUtil.isBlank(maritalRelationship.memberA)
        def isBlankMemberBCode = StringUtil.isBlank(maritalRelationship.memberB)
        def isBlankEndStatus = StringUtil.isBlank(maritalRelationship.endStatus)
        def isNullEndDate = maritalRelationship.endDate == null

        def memberA = !isBlankMemberACode ? memberService.getMember(maritalRelationship.memberA) : null
        def memberB = !isBlankMemberBCode ? memberService.getMember(maritalRelationship.memberB) : null
        def memberAExists = memberA != null
        def memberBExists = memberB != null

        //C1. Check Blank Fields (memberCode)
        if (isBlankMemberACode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["memberA"], ["memberA"])
        }
        //C1. Check Blank Fields (headCode)
        if (isBlankMemberBCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["memberB"], ["memberB"])
        }
        //C1. Check Blank Fields (endStatus)
        if (isBlankEndStatus){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["endStatus"], ["endStatus"])
        }
        //C1. Check Nullable Fields (endDate)
        if (isNullEndDate){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["endDate"], ["endDate"])
        }
        //C3. Validate endStatus Enum Options
        if (!isBlankEndStatus && MaritalEndStatus.getFrom(maritalRelationship.endStatus)==null){
            errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.endstatus.valid.error", [maritalRelationship.endStatus], ["endStatus"])
        }
        //C4. Check Member A reference existence
        if (!isBlankMemberACode && !memberAExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "memberA", maritalRelationship.memberA], ["memberA"])
        }
        //C4. Check Member B reference existence
        if (!isBlankMemberBCode && !memberBExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "memberB", maritalRelationship.memberB], ["memberB"])
        }

        //C5. Check dob max date
        if (!isNullEndDate && maritalRelationship.endDate > new Date()){
            errors << errorMessageService.getRawMessage("validation.field.date.not.greater.today", [maritalRelationship.endDate], ["endDate"])
        }

        //Validation part 2: Previous MaritalRelationship against new MaritalRelationship
        if (memberAExists && memberBExists){
            def currentMaritalRelationship = getCurrentMaritalRelationship(memberA, memberB)
            def endDate = maritalRelationship.endDate

            //must exist
            if (currentMaritalRelationship == null) {
                //THERE IS NO CURRENT RELATIONSHIP TO CLOSE
                errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.close.not.exists.error", [memberA.code, memberB.code], ["memberA.code", "memberB.code"])
                return errors
            }

            //must not be closed
            //P1. Check If endStatus is empty or NA
            if ( !(currentMaritalRelationship.endStatus == null || currentMaritalRelationship.endStatus == MaritalEndStatus.NOT_APPLICABLE) ){
                errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.closed.already.error", [currentMaritalRelationship.id, currentMaritalRelationship.endStatus], ["previous.endStatus"])
            }

            //C6. Check If endDate is before or equal to startDate
            if (currentMaritalRelationship.startDate >= endDate){
                errors << errorMessageService.getRawMessage("validation.field.maritalRelationship.enddate.before.startdate.error", null, ["currentMaritalRelationship.startDate", "new.endDate"])
            }

        }


        return errors
    }

    private MaritalRelationship newMaritalRelationshipInstance(RawMaritalRelationship mr){

        MaritalRelationship maritalRelationship = new MaritalRelationship()

        maritalRelationship.memberA = memberService.getMember(mr.memberA)        
        maritalRelationship.memberB = memberService.getMember(mr.memberB)
        maritalRelationship.memberA_code = maritalRelationship.memberA?.code        
        maritalRelationship.memberB_code = maritalRelationship.memberB?.code        
        maritalRelationship.startStatus = MaritalStartStatus.getFrom(mr.startStatus)
        maritalRelationship.startDate = mr.startDate
        maritalRelationship.endStatus = MaritalEndStatus.NOT_APPLICABLE
        maritalRelationship.endDate = null

        return maritalRelationship

    }

    private MaritalRelationship closeMaritalRelationshipInstance(RawMaritalRelationship mr){

        def memberA = memberService.getMember(mr.memberA)
        def memberB = memberService.getMember(mr.memberB)
        def maritalRelationship = getCurrentMaritalRelationship(memberA, memberB)
        def endStatus = MaritalEndStatus.getFrom(mr.endStatus)

        maritalRelationship.memberA = memberB
        maritalRelationship.memberB = memberB
        maritalRelationship.memberA_code = memberA.code
        maritalRelationship.memberB_code = memberB.code
        //maritalRelationship.startStatus = Already defined
        //maritalRelationship.startDate = Already defined
        maritalRelationship.endStatus = endStatus
        maritalRelationship.endDate = mr.endDate

        return maritalRelationship

    }
    //</editor-fold>
}
