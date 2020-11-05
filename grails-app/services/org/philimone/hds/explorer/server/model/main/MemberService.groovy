package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawMember
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.Codes

@Transactional
class MemberService {

    def householdService
    def userService
    def errorMessageService

    //<editor-fold desc="Member Utilities Methods">
    boolean isCodeValid(String memberCode){
        memberCode.matches(Codes.MEMBER_CODE_PATTERN)
    }

    boolean exists(String memberCode) {
        Member.countByCode(memberCode) > 0
    }

    Member getMember(String memberCode){
        if (!StringUtil.isBlank(memberCode)){
            return Member.findByCode(memberCode)
        }
        return null
    }

    boolean isDead(Member member) {
        return false /* TODO Will be Implemented when we build Death Registration */
    }

    /*
     * Generate Household Code using the Region.code and User.code
     */
    String generateCode(Household household){
        def cbase = "${household.code}"

        def codes = Member.findAllByCodeLike("${cbase}%", [sort:'code', order: 'asc']).collect{ t -> t.code}

        //Generate codes and try to match the database until u cant
        if (codes.size()==0){
            return "${cbase}001"
        } else {
            for (int i=1; i <= 999; i++){
                def code = "${cbase}${String.format('%03d', i)}" as String
                if (!codes.contains(code)){
                    return code
                }
            }
        }

        return "${cbase}ERROR"
    }
    //</editor-fold>

    //<editor-fold desc="Member Factory/Manager Methods">
    RawExecutionResult<Member> createMember(RawMember rawMember) {

        /* Run Checks and Validations */

        def errors = validate(rawMember)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Member> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        def member = newMemberInstance(rawMember)

        member = member.save(flush:true)
        //Validate using Gorm Validations
        if (member.hasErrors()){

            errors = errorMessageService.getRawMessages(member)

            RawExecutionResult<Member> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        RawExecutionResult<Member> obj = RawExecutionResult.newSuccessResult(member)
        return obj
    }

    ArrayList<RawMessage> validate(RawMember member){
        def errors = new ArrayList<RawMessage>()

        //code, name, gender, dob, motherCode, fatherCode, maritalStatus, householdCode
        def isBlankMemberCode = StringUtil.isBlank(member.code)
        def isBlankMemberName = StringUtil.isBlank(member.name)
        def isBlankMemberGender = StringUtil.isBlank(member.gender)
        def isBlankHouseholdCode = StringUtil.isBlank(member.householdCode)
        def isBlankMotherCode = StringUtil.isBlank(member.motherCode)
        def isBlankFatherCode = StringUtil.isBlank(member.fatherCode)
        def isBlankMaritalStatus = StringUtil.isBlank(member.maritalStatus)
        def isBlankCollectedBy = StringUtil.isBlank(member.collectedBy)
        def isNullDob = member.dob == null
        def mother = getMember(member.motherCode)
        def father = getMember(member.fatherCode)
        def motherExists = mother != null
        def fatherExists = father != null

        //C1. Check Blank Fields (code)
        if (isBlankMemberCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["code"], ["code"])
        }
        //C1. Check Blank Fields (name)
        if (isBlankMemberName){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["name"], ["name"])
        }
        //C1. Check Blank Fields (gender)
        if (isBlankMemberGender){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["gender"], ["gender"])
        }
        //C1. Check Blank Fields (householdCode)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (motherCode)
        if (isBlankMotherCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["motherCode"], ["motherCode"])
        }
        //C1. Check Blank Fields (fatherCode)
        if (isBlankFatherCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["fatherCode"], ["fatherCode"])
        }
        //C1. Check Blank Fields (maritalStatus)
        if (isBlankMaritalStatus){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["maritalStatus"], ["maritalStatus"])
        }
        //C1. Check Nullable Fields (dob)
        if (isNullDob){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["dob"], ["dob"])
        }

        //C2. Check Code Regex Pattern
        if (!isBlankMemberCode && !isCodeValid(member.code)) {
            errors << errorMessageService.getRawMessage("validation.field.pattern.no.matches", ["code", "TXUPF1001001"], ["code"])
        }
        //C3. Check Code Prefix Reference existence (Household Existence in memberCode)
        if (!isBlankMemberCode && !householdService.prefixExists(member.code)){
            errors << errorMessageService.getRawMessage("validation.field.pattern.prefix.household.reference.error", [member.code], ["code"])
        }

        //C12. Validate Gender Enum Options
        if (!isBlankMemberGender && Gender.getFrom(member.gender)==null){
            errors << errorMessageService.getRawMessage("validation.field.enum.choices.error", [member.gender, "gender"], ["gender"])
        }
        //C12. Validate MaritalStatus Enum Options
        if (!isBlankMaritalStatus && MaritalStatus.getFrom(member.maritalStatus)==null){
            errors << errorMessageService.getRawMessage("validation.field.enum.choices.error", [member.maritalStatus, "maritalStatus"], ["maritalStatus"])
        }

        //C5. Check dob max date
        if (!isNullDob && member.dob > new Date()){
            errors << errorMessageService.getRawMessage("validation.field.date.not.greater.today", [member.dob], ["dob"])
        }

        //C4. Check Household reference existence
        if (!isBlankHouseholdCode && !householdService.exists(member.householdCode)){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Household", "householdCode", member.householdCode], ["householdCode"])
        }
        //C4. Check Mother reference existence
        if (!isBlankMotherCode && !motherExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "motherCode", member.motherCode], ["motherCode"])
        }
        //C4. Check Father reference existence
        if (!isBlankFatherCode && !fatherExists){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Member", "fatherCode", member.fatherCode], ["fatherCode"])
        }

        //C6. Check mother Dob must be greater or equal to 12
        if (motherExists && GeneralUtil.getAge(mother.dob)< Codes.MIN_MOTHER_AGE_VALUE ){
            errors << errorMessageService.getRawMessage("validation.field.dob.mother.minage.error", [mother.dob, Codes.MIN_MOTHER_AGE_VALUE], ["mother.dob"])
        }
        //C7. Check father Dob must be greater or equal to 12
        if (fatherExists && GeneralUtil.getAge(father.dob)< Codes.MIN_FATHER_AGE_VALUE ){
            errors << errorMessageService.getRawMessage("validation.field.dob.father.minage.error", [mother.dob, Codes.MIN_FATHER_AGE_VALUE], ["father.dob"])
        }

        //C9. Check mother Gender
        if (Codes.GENDER_CHECKING && motherExists && mother.gender==Gender.MALE){
            errors << errorMessageService.getRawMessage("validation.field.gender.mother.error", [], ["mother.gender"])
        }
        //C10. Check father Gender
        if (Codes.GENDER_CHECKING && fatherExists && father.gender==Gender.FEMALE){
            errors << errorMessageService.getRawMessage("validation.field.gender.father.error", [], ["father.gender"])
        }

        //C5. Check CollectedBy User existence
        if (!isBlankCollectedBy && !userService.exists(member.collectedBy)){
            errors << errorMessageService.getRawMessage("validation.field.user.dont.exists.error", [member.collectedBy], ["collectedBy"])
        }

        //C6. Check Duplicate of memberCode
        if (!isBlankMemberCode && exists(member.code)){
            errors << errorMessageService.getRawMessage("validation.field.reference.duplicate.error", ["Member", "code", member.code], ["code"])
        }

        return errors
    }

    private Member newMemberInstance(RawMember rm){

        def father = getMember(rm.fatherCode)
        def mother = getMember(rm.motherCode)
        def household = householdService.getHousehold(rm.householdCode)

        Member member = new Member()

        member.code = rm.code
        member.name = rm.name
        member.gender = Gender.getFrom(rm.gender)
        member.dob = rm.dob
        //member.age //its a transient method that returns the age calculated with dob
        //member.ageAtDeath //is null until the Member dies - its updated when a death is registered
        member.mother = mother
        member.motherCode = mother.code
        member.motherName = mother.name
        member.father = father
        member.fatherCode = father.code
        member.fatherName = father.name

        member.maritalStatus = MaritalStatus.getFrom(rm.maritalStatus)

        member.household = household
        member.householdCode = household.code
        member.householdName = household.name

        member.gpsAccuracy = household.gpsAccuracy
        member.gpsAltitude = household.gpsAltitude
        member.gpsLatitude = household.gpsLatitude
        member.gpsLongitude = household.gpsLongitude
        member.gpsNull = member.gpsLatitude==null || member.gpsLongitude

        member.cosLatitude =  member.gpsLatitude==null ?  null : Math.cos(member.gpsLatitude*Math.PI / 180.0)
        member.sinLatitude =  member.gpsLatitude==null ?  null : Math.sin(member.gpsLatitude*Math.PI / 180.0)
        member.cosLongitude = member.gpsLongitude==null ? null : Math.cos(member.gpsLongitude*Math.PI / 180.0)
        member.sinLongitude = member.gpsLongitude==null ? null : Math.sin(member.gpsLongitude*Math.PI / 180.0)

        //set collected by info
        member.collectedBy = userService.getUser(rm.collectedBy)
        member.collectedDate = rm.collectedDate
        member.updatedDate = rm.uploadedDate

        return member

    }
    //</editor-fold>

}
