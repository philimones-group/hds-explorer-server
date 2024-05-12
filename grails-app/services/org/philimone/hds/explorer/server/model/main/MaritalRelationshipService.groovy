package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawMaritalRelationship
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStartStatus
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

import java.time.LocalDate

@Transactional
class MaritalRelationshipService {

    def householdService
    def memberService
    def userService
    def deathService
    def coreExtensionService
    def errorMessageService
    def messageSource

    //<editor-fold desc="MaritalRelationship Utilities Methods">
    MaritalRelationship getCurrentMaritalRelationship(Member member) {
        if (member != null && member.id != null) {

            def maritalRelationshipsA = MaritalRelationship.executeQuery("select r from MaritalRelationship r where r.memberA.id=?0 order by r.startDate desc", [member.id], [offset:0, max:1]) // limit 1
            def maritalRelationshipsB = MaritalRelationship.executeQuery("select r from MaritalRelationship r where r.memberB.id=?0 order by r.startDate desc", [member.id], [offset:0, max:1]) // limit 1
            MaritalRelationship relationA = (maritalRelationshipsA != null && maritalRelationshipsA.size() > 0) ? maritalRelationshipsA.first() as MaritalRelationship : null
            MaritalRelationship relationB = (maritalRelationshipsB != null && maritalRelationshipsB.size() > 0) ? maritalRelationshipsB.first() as MaritalRelationship : null

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

        if (memberA != null && memberA.id != null && memberB != null && memberB.id != null) {

            def maritalRelationshipsA = MaritalRelationship.executeQuery("select r from MaritalRelationship r where r.memberA=?0 and r.memberB=?1 order by r.startDate desc", [memberA, memberB], [offset:0, max:1]) // limit 1
            def maritalRelationshipsB = MaritalRelationship.executeQuery("select r from MaritalRelationship r where r.memberA=?0 and r.memberB=?1 order by r.startDate desc", [memberB, memberA], [offset:0, max:1]) // limit 1
            MaritalRelationship relationA = (maritalRelationshipsA != null && maritalRelationshipsA.size() > 0) ? maritalRelationshipsA.first() as MaritalRelationship : null
            MaritalRelationship relationB = (maritalRelationshipsB != null && maritalRelationshipsB.size() > 0) ? maritalRelationshipsB.first() as MaritalRelationship : null

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

    RawMaritalRelationship getCurrentMaritalRelationshipAsRaw(Member member) {
        def maritalRelationship = getCurrentMaritalRelationship(member)
        return convertToRaw(maritalRelationship)
    }

    /*
    RawMaritalRelationship getCurrentMaritalRelationshipAsRaw(Member memberA, Member memberB) {
        def maritalRelationship = getCurrentMaritalRelationship(memberA, memberB)
        return convertToRaw(maritalRelationship)
    }*/

    MaritalStatus getMaritalStatusFromStartStatus(MaritalStartStatus status){
        switch (status){
            case MaritalStartStatus.MARRIED : return MaritalStatus.MARRIED
            case MaritalStartStatus.LIVING_TOGHETER : return MaritalStatus.LIVING_TOGHETER
        }

        return null
    }

    MaritalStatus getMaritalStatusFromEndStatus(MaritalEndStatus status){
        switch (status){
            //case MaritalEndStatus.NOT_APPLICABLE : return MaritalStatus.MARRIED
            case MaritalEndStatus.DIVORCED : return MaritalStatus.DIVORCED
            case MaritalEndStatus.SEPARATED : return MaritalStatus.SEPARATED
            case MaritalEndStatus.WIDOWED : return MaritalStatus.WIDOWED
        }

        return null
    }

    MaritalStatus getMaritalStatusFrom(MaritalRelationship maritalRelationship) {
        if (maritalRelationship != null) {
            if (maritalRelationship.endStatus == MaritalEndStatus.NOT_APPLICABLE) {
                return getMaritalStatusFromStartStatus(maritalRelationship.startStatus)
            } else {
                return getMaritalStatusFromEndStatus(maritalRelationship.endStatus)
            }
        }

        return MaritalStatus.SINGLE
    }

    /*
    boolean isMainOfPolygamicRawRelationship(RawMaritalRelationship maritalRelationship) {
        return RawMaritalRelationship.countByPolygamicId(maritalRelationship.collectedId)>0
    }
    */

    RawMaritalRelationship convertToRaw(MaritalRelationship maritalRelationship){

        if (maritalRelationship == null) return null

        def rm = new RawMaritalRelationship()

        rm.memberA = maritalRelationship.memberA.code
        rm.memberB = maritalRelationship.memberB.code
        rm.startStatus = maritalRelationship.startStatus.code
        rm.startDate = maritalRelationship.startDate
        rm.endStatus = maritalRelationship.endStatus.code
        rm.endDate = maritalRelationship.endDate

        return rm
    }

    List<RawMessage> updatesAfterCreatingRelationship(MaritalRelationship maritalRelationship) {

        def errors = [] as ArrayList<RawMessage>
        def memberA = maritalRelationship.memberA.refresh()
        def memberB = maritalRelationship.memberB.refresh()

        //update spouse status
        memberA.maritalStatus = getMaritalStatusFromStartStatus(maritalRelationship.startStatus)
        memberA.spouse = memberB
        memberA.spouseCode = memberB.code
        memberA.spouseName = memberB.name

        memberB.maritalStatus = getMaritalStatusFromStartStatus(maritalRelationship.startStatus)
        memberB.spouse = memberA
        memberB.spouseCode = memberA.code
        memberB.spouseName = memberA.name


        memberA.save(flush: true)
        memberB.save(flush: true)


        //get errors if they occur and send with the success report
        if (memberA.hasErrors()) {
            errors += errorMessageService.getRawMessages(RawEntity.MEMBER, memberA)
        }
        if (memberB.hasErrors()) {
            errors += errorMessageService.getRawMessages(RawEntity.MEMBER, memberB)
        }

        return errors
    }

    List<RawMessage> updatesAfterClosingRelationship(MaritalRelationship maritalRelationship){

        def errors = [] as ArrayList<RawMessage>
        def memberA = maritalRelationship.memberA.refresh()
        def memberB = maritalRelationship.memberB.refresh()
        def endStatus = getMaritalStatusFromEndStatus(maritalRelationship.endStatus)
        def marStatusA = getMaritalStatusFromStartStatus(maritalRelationship.startStatus) //the default is the start status
        def marStatusB = marStatusA

        if (endStatus == MaritalStatus.WIDOWED){ //If things go OK, for two members who died - maritalrelationship close runs once - so do this check on DeathService.afterCreateDeath
            //Find who died and set the other Member as WIDOWED
            def deathA = deathService.getDeath(memberA)
            def deathB = deathService.getDeath(memberB)
            def isDeadA = deathA != null
            def isDeadB = deathB != null

            if (isDeadA && !isDeadB && (deathA.deathDate == maritalRelationship.endDate)){ //the relationship ended this day
                marStatusB = MaritalStatus.WIDOWED
                marStatusA = getMaritalStatusFromStartStatus(maritalRelationship.startStatus) //The dead Member will remain with the last Status
            }
            if (isDeadB && !isDeadA && (deathB.deathDate == maritalRelationship.endDate)){ //the relationship ended this day
                marStatusA = MaritalStatus.WIDOWED
                marStatusB = getMaritalStatusFromStartStatus(maritalRelationship.startStatus) //The dead Member will remain with the last Status
            }
            if (isDeadA && isDeadB) { //this can happen if for a odd reason the relationship wasnt closed when one of them died
                if (deathA.deathDate == deathB.deathDate){
                    //they will mantain the last status / startStatus - assuring that both died married/any
                } else if (deathA.deathDate < deathB.deathDate){ //memberA died first, then memberB is widow and vice-versa
                    marStatusB = MaritalStatus.WIDOWED
                } else {
                    marStatusA = MaritalStatus.WIDOWED
                }
            }
        }

        //update spouse status
        memberA.maritalStatus = marStatusA
        //memberA.spouse = memberB
        //memberA.spouseCode = memberB.code
        //memberA.spouseName = memberB.name

        memberB.maritalStatus = marStatusB
        //memberB.spouse = memberA
        //memberB.spouseCode = memberA.code
        //memberB.spouseName = memberA.name

        memberA.save(flush: true)
        memberB.save(flush: true)

        //get errors if they occur and send with the success report
        if (memberA.hasErrors()) {
            errors += errorMessageService.getRawMessages(RawEntity.MEMBER, memberA)
        }
        if (memberB.hasErrors()) {
            errors += errorMessageService.getRawMessages(RawEntity.MEMBER, memberB)
        }

        return errors
    }
    //</editor-fold>

    //<editor-fold desc="MaritalRelationship Factory/Manager Methods">
    RawExecutionResult<MaritalRelationship> executeMaritalRelationship(RawMaritalRelationship rawMaritalRelationship) {
        //identify if is a creation or "closation" kkkk of a marital relationship
        if (rawMaritalRelationship.startStatus != null && rawMaritalRelationship.startDate != null){

            return createMaritalRelationship(rawMaritalRelationship)

        } else if (rawMaritalRelationship.endStatus != null && rawMaritalRelationship.endDate != null){

            return closeMaritalRelationship(rawMaritalRelationship)

        }
    }

    RawExecutionResult<MaritalRelationship> createMaritalRelationship(RawMaritalRelationship rawMaritalRelationship) {

        /* Run Checks and Validations */

        def errors = validateCreateMaritalRelationship(rawMaritalRelationship)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.MARITAL_RELATIONSHIP, errors)
            return obj
        }

        def maritalRelationship = newCreateMaritalRelationshipInstance(rawMaritalRelationship)

        def result = maritalRelationship.save(flush:true)

        //Validate using Gorm Validations
        if (maritalRelationship.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.MARITAL_RELATIONSHIP, maritalRelationship)

            RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.MARITAL_RELATIONSHIP, errors)
            return obj
        } else {
            maritalRelationship = result
        }

        //Update Member with start status
        errors += updatesAfterCreatingRelationship(maritalRelationship)

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertMaritalRelationshipExtension(rawMaritalRelationship, result)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail
            println "Failed to insert extension: ${resultExtension.errorMessage}"
        }

        RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newSuccessResult(RawEntity.MARITAL_RELATIONSHIP, maritalRelationship, errors)
        return obj
    }

    RawExecutionResult<MaritalRelationship> closeMaritalRelationship(RawMaritalRelationship rawMaritalRelationship) {

        /* Run Checks and Validations */

        def errors = validateCloseMaritalRelationship(rawMaritalRelationship)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.MARITAL_RELATIONSHIP, errors)
            return obj
        }

        def maritalRelationship = newCloseMaritalRelationshipInstance(rawMaritalRelationship)

        def result = maritalRelationship.save(flush:true)

        //Validate using Gorm Validations
        if (maritalRelationship.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.MARITAL_RELATIONSHIP, maritalRelationship)

            RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newErrorResult(RawEntity.MARITAL_RELATIONSHIP, errors)
            return obj
        } else {
            maritalRelationship = result
        }

        //Update Household - remove head if member was one
        errors = updatesAfterClosingRelationship(maritalRelationship)

        RawExecutionResult<MaritalRelationship> obj = RawExecutionResult.newSuccessResult(RawEntity.MARITAL_RELATIONSHIP, maritalRelationship, errors)
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
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.blank", ["memberA"], ["memberA"])
        }
        //C1. Check Blank Fields (headCode)
        if (isBlankMemberBCode){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.blank", ["memberB"], ["memberB"])
        }
        //C1. Check Blank Fields (startStatus)
        if (isBlankStartStatus){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.blank", ["startStatus"], ["startStatus"])
        }
        //C1. Check Nullable Fields (startDate)
        if (isNullStartDate){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.blank", ["startDate"], ["startDate"])
        }
        //C2. Validate startStatus Enum Options
        if (!isBlankStartStatus && MaritalStartStatus.getFrom(maritalRelationship.startStatus)==null){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.startstatus.valid.error", [maritalRelationship.startStatus], ["startStatus"])
        }
        //C4. Check Member A reference existence
        if (!isBlankMemberACode && !memberAExists){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.reference.error", ["Member", "memberA", maritalRelationship.memberA], ["memberA"])
        }
        //C4. Check Member B reference existence
        if (!isBlankMemberBCode && !memberBExists){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.reference.error", ["Member", "memberB", maritalRelationship.memberB], ["memberB"])
        }
        //C5. Check startdate max date
        if (!isNullStartDate && maritalRelationship.startDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.date.not.greater.today", [StringUtil.format(maritalRelationship.startDate)], ["startDate"])
        }
        //C6. Check Dates against DOB
        if (!isNullStartDate && memberAExists && memberBExists){
            if (maritalRelationship.startDate < memberA.dob){
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.dob.not.greater.date", ["maritalRelationship.startDate", StringUtil.format(memberA.dob)], ["startDate","memberA.dob"])
            }
            if (maritalRelationship.startDate < memberB.dob){
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.dob.not.greater.date", ["maritalRelationship.startDate", StringUtil.format(memberB.dob)], ["startDate","memberB.dob"])
            }
        }
        //C7. Check Age of Member A
        if (memberAExists && GeneralUtil.getAge(memberA.dob)< Codes.MIN_SPOUSE_AGE_VALUE ){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.dob.spouse.minage.error", [StringUtil.format(memberA.dob), Codes.MIN_SPOUSE_AGE_VALUE+""], ["member.dob"])
        }
        //C7. Check Age of Member B
        if (memberBExists && GeneralUtil.getAge(memberB.dob)< Codes.MIN_SPOUSE_AGE_VALUE ){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.dob.spouse.minage.error", [StringUtil.format(memberB.dob), Codes.MIN_SPOUSE_AGE_VALUE+""], ["member.dob"])
        }
        //C8. Check Gender as Optional
        if (Codes.GENDER_CHECKING && memberAExists && memberBExists){
            //who's male whos female
            if (memberA.gender == memberB.gender){
                def genderA = messageSource.getMessage(memberA.gender.name, null, new Locale(Codes.SYSTEM_LANGUAGE))
                def genderB = messageSource.getMessage(memberB.gender.name, null, new Locale(Codes.SYSTEM_LANGUAGE))
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.gender.spouse.error", [genderA, genderB], ["member.gender"])
            }
        }
        //C9. Check Death Status of member A
        if (memberAExists && memberService.isDead(memberA)){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.death.a.or.b.error", ["memberA", memberA.code], ["memberA_code"])
        }
        //C9. Check Death Status of member B
        if (memberBExists && memberService.isDead(memberB)){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.death.a.or.b.error", ["memberB", memberB.code], ["memberB_code"])
        }


        //Validation part 2: Previous MaritalRelationship against new MaritalRelationship
        if (memberAExists && memberBExists){
            def currentA = getCurrentMaritalRelationship(memberA)
            def currentB = getCurrentMaritalRelationship(memberB)
            def newStartDate = maritalRelationship.startDate
            def isPolygamic = maritalRelationship.isPolygamic
            boolean isMalePolygamyA = memberA.gender==Gender.MALE && (isPolygamic);
            boolean isMalePolygamyB = memberB.gender==Gender.MALE && (isPolygamic);

            //P1. Check if last relationship of A still opened
            if (currentA != null && (currentA.endStatus == null || currentA.endStatus == MaritalEndStatus.NOT_APPLICABLE) && !isMalePolygamyA){
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.prev.relationships.ab.closed.error", [memberA.code], ["previous.memberA.endStatus"])
            }
            //P1. Check if last relationship of B still opened
            if (currentB != null && (currentB.endStatus == null || currentB.endStatus == MaritalEndStatus.NOT_APPLICABLE) && !isMalePolygamyB){
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.prev.relationships.ab.closed.error", [memberB.code], ["previous.memberB.endStatus"])
            }
            //P2. Check If endDate is greater than new startDate, for memberA
            if (currentA != null && (currentA.endDate != null && currentA.endDate >= newStartDate) && !isMalePolygamyA){
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.prev.enddate.before.n.startdate.error", [memberA.code], ["previous.memberA.endDate"])
            }
            //P2. Check If endDate is greater than new startDate, for memberB
            if (currentB != null && (currentB.endDate != null && currentB.endDate >= newStartDate) && !isMalePolygamyB){
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.prev.enddate.before.n.startdate.error", [memberB.code], ["previous.memberB.endDate"])
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
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.blank", ["memberA"], ["memberA"])
        }
        //C1. Check Blank Fields (headCode)
        if (isBlankMemberBCode){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.blank", ["memberB"], ["memberB"])
        }
        //C1. Check Blank Fields (endStatus)
        if (isBlankEndStatus){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.blank", ["endStatus"], ["endStatus"])
        }
        //C1. Check Nullable Fields (endDate)
        if (isNullEndDate){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.blank", ["endDate"], ["endDate"])
        }
        //C3. Validate endStatus Enum Options
        if (!isBlankEndStatus && MaritalEndStatus.getFrom(maritalRelationship.endStatus)==null){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.endstatus.valid.error", [maritalRelationship.endStatus], ["endStatus"])
        }
        //C4. Check Member A reference existence
        if (!isBlankMemberACode && !memberAExists){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.reference.error", ["Member", "memberA", maritalRelationship.memberA], ["memberA"])
        }
        //C4. Check Member B reference existence
        if (!isBlankMemberBCode && !memberBExists){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.reference.error", ["Member", "memberB", maritalRelationship.memberB], ["memberB"])
        }
        //C5. Check dob max date
        if (!isNullEndDate && maritalRelationship.endDate > LocalDate.now()){
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.date.not.greater.today", ["maritalRelationship.endDate"], ["endDate"])
        }
        //C6. Check Dates against DOB
        if (!isNullEndDate && memberAExists && memberBExists){
            if (maritalRelationship.endDate < memberA.dob){
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.dob.not.greater.date", ["maritalRelationship.endDate", StringUtil.format(memberA.dob)], ["endDate","memberA.dob"])
            }
            if (maritalRelationship.endDate < memberB.dob){
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.dob.not.greater.date", ["maritalRelationship.endDate", StringUtil.format(memberB.dob)], ["endDate","memberB.dob"])
            }
        }

        //C7. Check member A and B equality
        if (!isBlankMemberACode && !isBlankMemberBCode && maritalRelationship.memberA.equalsIgnoreCase(maritalRelationship.memberB)) {
            errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.same.member.error", [memberA.code, memberB.code], ["memberA.code", "memberB.code"])
        }

        //Validation part 2: Previous MaritalRelationship against new MaritalRelationship
        if (memberAExists && memberBExists){
            def currentMaritalRelationship = getCurrentMaritalRelationship(memberA, memberB)
            def endDate = maritalRelationship.endDate

            //P1. Check If Current Relationship Exists
            if (currentMaritalRelationship == null) {
                //THERE IS NO CURRENT RELATIONSHIP TO CLOSE
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.close.not.exists.error", [memberA.code, memberB.code], ["memberA.code", "memberB.code"])
                return errors
            }

            //must not be closed
            //P2. Check If endStatus is empty or NA
            if ( !(currentMaritalRelationship.endStatus == null || currentMaritalRelationship.endStatus == MaritalEndStatus.NOT_APPLICABLE) ){
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.closed.already.error", [currentMaritalRelationship.id, currentMaritalRelationship.endStatus.code], ["previous.endStatus"])
            }

            //P3. Check If the proposed endDate is before or equal to the startDate of this relationship
            if (currentMaritalRelationship.startDate >= endDate){ //endDate <= startDate
                errors << errorMessageService.getRawMessage(RawEntity.MARITAL_RELATIONSHIP, "validation.field.maritalRelationship.enddate.before.startdate.error", [memberA.code, memberB.code], ["currentMaritalRelationship.startDate", "new.endDate"])
            }

        }


        return errors
    }

    private MaritalRelationship newCreateMaritalRelationshipInstance(RawMaritalRelationship mr){

        MaritalRelationship maritalRelationship = new MaritalRelationship()

        maritalRelationship.memberA = memberService.getMember(mr.memberA)        
        maritalRelationship.memberB = memberService.getMember(mr.memberB)
        maritalRelationship.memberA_code = maritalRelationship.memberA?.code        
        maritalRelationship.memberB_code = maritalRelationship.memberB?.code
        maritalRelationship.isPolygamic = mr.isPolygamic
        maritalRelationship.polygamicId = mr.polygamicId
        maritalRelationship.startStatus = MaritalStartStatus.getFrom(mr.startStatus)
        maritalRelationship.startDate = mr.startDate
        maritalRelationship.endStatus = MaritalEndStatus.NOT_APPLICABLE
        maritalRelationship.endDate = null

        maritalRelationship.collectedId = mr.id
        maritalRelationship.collectedBy = userService.getUser(mr.collectedBy)
        maritalRelationship.collectedDeviceId = mr.collectedDeviceId
        maritalRelationship.collectedHouseholdId = mr.collectedHouseholdId
        maritalRelationship.collectedMemberId = mr.collectedMemberId
        maritalRelationship.collectedStart = mr.collectedStart
        maritalRelationship.collectedEnd = mr.collectedEnd
        maritalRelationship.collectedDate = mr.collectedDate
        maritalRelationship.updatedDate = mr.uploadedDate

        return maritalRelationship

    }

    private MaritalRelationship newCloseMaritalRelationshipInstance(RawMaritalRelationship mr){

        def memberA = memberService.getMember(mr.memberA)
        def memberB = memberService.getMember(mr.memberB)
        def maritalRelationship = getCurrentMaritalRelationship(memberA, memberB)
        def endStatus = MaritalEndStatus.getFrom(mr.endStatus)

        maritalRelationship.memberA = memberA
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
