package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

@Transactional
class HouseholdService {

    def regionService
    def userService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="Household Utilities Methods">
    boolean exists(String householdCode) {
        Household.countByCode(householdCode) > 0
    }

    boolean prefixExists(String memberCode){
        exists(memberCode.length()>8 ? memberCode.substring(0, 9) : memberCode)
    }

    Household getHousehold(String householdCode) {
        if (!StringUtil.isBlank(householdCode)){
            return Household.findByCode(householdCode)
        }
        return null
    }

    String generateCode(Region region, User user){
        return codeGeneratorService.generateHouseholdCode(region, user)
    }

    //</editor-fold>

    //<editor-fold desc="Household Factory/Manager Methods">
    /**
     * Validate Household and Create
     */
    RawExecutionResult<Household> createHousehold(RawHousehold rawHousehold) {

        /* Run Checks and Validations */

        def errors = validate(rawHousehold)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Household> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        def household = newHouseholdInstance(rawHousehold)

        household = household.save(flush:true)
        //Validate using Gorm Validations
        if (household.hasErrors()){

            errors = errorMessageService.getRawMessages(household)

            RawExecutionResult<Household> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        RawExecutionResult<Household> obj = RawExecutionResult.newSuccessResult(household)
        return obj
    }

    ArrayList<RawMessage> validate(RawHousehold household){
        def errors = new ArrayList<RawMessage>()

        def isBlankHouseholdCode = StringUtil.isBlank(household.householdCode)
        def isBlankHouseholdName = StringUtil.isBlank(household.householdName)
        def isBlankRegionCode = StringUtil.isBlank(household.regionCode)
        def isBlankCollectedBy = StringUtil.isBlank(household.collectedBy)

        //C1. Check Blank Fields (code)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (name)
        if (isBlankHouseholdName){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["householdName"], ["householdName"])
        }
        //C1. Check Blank Fields (region)
        if (isBlankRegionCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["regionCode"], ["regionCode"])
        }
        //C2. Check Code Regex Pattern
        if (!isBlankHouseholdCode && !codeGeneratorService.isHouseholdCodeValid(household.householdCode)) {
            errors << errorMessageService.getRawMessage("validation.field.pattern.no.matches", ["householdCode", "TXUPF1001"], ["householdCode"])
        }
        //C3. Check Region reference existence
        if (!isBlankRegionCode && !regionService.exists(household.regionCode)){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Region", "regionCode", household.regionCode], ["regionCode"])
        }
        //C4. Check Code Prefix Reference existence (Region Existence)
        if (!isBlankHouseholdCode && !regionService.prefixExists(household.householdCode)){
            errors << errorMessageService.getRawMessage("validation.field.pattern.prefix.region.reference.error", [household.householdCode], ["householdCode"])
        }
        //C5. Check User existence
        if (!isBlankCollectedBy && !userService.exists(household.collectedBy)){
            errors << errorMessageService.getRawMessage("validation.field.user.dont.exists.error", [household.collectedBy], ["collectedBy"])
        }

        //C6. Check Duplicate of householdCode
        if (!isBlankHouseholdCode && exists(household.householdCode)){
            errors << errorMessageService.getRawMessage("validation.field.reference.duplicate.error", ["Household", "householdCode", household.householdCode], ["householdCode"])
        }

        return errors
    }

    private Household newHouseholdInstance(RawHousehold rh){

        def hierarchies = regionService.getHierarchies(rh.regionCode)

        Household household = new Household()

        household.code = rh.householdCode
        household.name = rh.householdName
        household.region = rh.regionCode
        //household.headCode = rh.headCode
        //household.headName = rh.headName

        household.parentRegion = hierarchies.region
        //household.headMember = memberService.getVisit(rh.headCode)

        household.hierarchy1 = hierarchies.hierarchy1
        household.hierarchy2 = hierarchies.hierarchy2
        household.hierarchy3 = hierarchies.hierarchy3
        household.hierarchy4 = hierarchies.hierarchy4
        household.hierarchy5 = hierarchies.hierarchy5
        household.hierarchy6 = hierarchies.hierarchy6
        household.hierarchy7 = hierarchies.hierarchy7
        household.hierarchy8 = hierarchies.hierarchy8

        household.gpsAccuracy = StringUtil.getDouble(rh.gpsAcc)
        household.gpsAltitude = StringUtil.getDouble(rh.gpsAlt)
        household.gpsLatitude = StringUtil.getDouble(rh.gpsLat)
        household.gpsLongitude = StringUtil.getDouble(rh.gpsLng)
        household.gpsNull = household.gpsLatitude==null || household.gpsLongitude

        household.cosLatitude =  household.gpsLatitude==null ?  null : Math.cos(household.gpsLatitude*Math.PI / 180.0)
        household.sinLatitude =  household.gpsLatitude==null ?  null : Math.sin(household.gpsLatitude*Math.PI / 180.0)
        household.cosLongitude = household.gpsLongitude==null ? null : Math.cos(household.gpsLongitude*Math.PI / 180.0)
        household.sinLongitude = household.gpsLongitude==null ? null : Math.sin(household.gpsLongitude*Math.PI / 180.0)

        //set collected by info
        household.collectedBy = userService.getUser(rh.collectedBy)
        household.collectedDate = rh.collectedDate
        household.updatedDate = rh.uploadedDate

        return household

    }
    //</editor-fold>
}
