package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.enums.HouseholdInstitutionType
import org.philimone.hds.explorer.server.model.enums.HouseholdStatus
import org.philimone.hds.explorer.server.model.enums.HouseholdType
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.temporal.ResidencyEndType
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

@Transactional
class HouseholdService {

    def regionService
    def residencyService
    def userService
    def moduleService
    def coreExtensionService
    def codeGeneratorService
    def errorMessageService

    //<editor-fold desc="Controller service methods">
    Household get(Serializable id){
        Household.get(id)
    }

    List<Household> list(Map args){
        Household.list(args)
    }

    Long count(){
        Household.count()
    }

    Household save(Household household){
        household.save(flush:true)
    }

    void delete(Serializable id){
        get(id).delete()
    }
    //</editor-fold>

    //<editor-fold desc="Household Utilities Methods">
    boolean exists(String householdCode) {
        Household.countByCode(householdCode) > 0
    }

    boolean existsByCollectedId(String collectedId) {
        Household.countByCollectedId(collectedId) > 0
    }

    boolean existsByCodeAndCollectedId(String householdCode, String collectedId) {
        Household.countByCodeAndCollectedId(householdCode, collectedId) > 0
    }

    /* This type of checking prefixes is bad specially because we are in a system with multiple code systems*/
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

    List<Member> getResidentMembers(Household household) {
        def members = residencyService.getCurrentResidentMembers(household)
        return members
    }

    def setHouseholdStatusVacant(Household household) {
        if (!residencyService.hasResidents(household)){
            household.status = HouseholdStatus.HOUSE_VACANT;
            household.save(flush:true)

            //println("status errors: ${household.errors}")
            //Household.executeUpdate("update Household h set h.status=?0 where h.id=?1", [HouseholdStatus.HOUSE_VACANT, household.id])
        }
    }

    def updateHouseholdStatus(Household household, HouseholdStatus status) {
        Household.executeUpdate("update Household h set h.status=?0 where h.id=?1", [status, household.id])
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
            RawExecutionResult<Household> obj = RawExecutionResult.newErrorResult(RawEntity.HOUSEHOLD, errors)
            return obj
        }

        def household = newHouseholdInstance(rawHousehold)

        def result = household.save(flush:true)
        //Validate using Gorm Validations
        if (household.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.HOUSEHOLD, household)

            RawExecutionResult<Household> obj = RawExecutionResult.newErrorResult(RawEntity.HOUSEHOLD, errors)
            return obj
        } else {
            household = result

            //--> take the extensionXml and save to Extension Table
            def resultExtension = coreExtensionService.insertHouseholdExtension(rawHousehold, result)
            if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
                //it supposed to not fail
                println "Failed to insert extension: ${resultExtension.errorMessage}"
            }
        }

        RawExecutionResult<Household> obj = RawExecutionResult.newSuccessResult(RawEntity.HOUSEHOLD, household)
        return obj
    }

    ArrayList<RawMessage> validate(RawHousehold household){
        def errors = new ArrayList<RawMessage>()

        def isBlankHouseholdCode = StringUtil.isBlank(household.householdCode)
        def isBlankHouseholdName = StringUtil.isBlank(household.householdName)
        def isBlankRegionCode = StringUtil.isBlank(household.regionCode)
        def isBlankCollectedBy = StringUtil.isBlank(household.collectedBy)
        def isBlankModules = StringUtil.isBlank(household.modules)
        def notFoundModules = moduleService.getNonExistenceModuleCodes(household.modules)

        //C1. Check Blank Fields (code)
        if (isBlankHouseholdCode){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.blank", ["householdCode"], ["householdCode"])
        }
        //C1. Check Blank Fields (name)
        if (isBlankHouseholdName){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.blank", ["householdName"], ["householdName"])
        }
        //C1. Check Blank Fields (region)
        if (isBlankRegionCode){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.blank", ["regionCode"], ["regionCode"])
        }
        //C2. Check Code Regex Pattern
        if (!isBlankHouseholdCode && !codeGeneratorService.isHouseholdCodeValid(household.householdCode)) {
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.pattern.no.matches", ["householdCode", codeGeneratorService.householdSampleCode], ["householdCode"])
        }
        //C3. Check Region reference existence
        if (!isBlankRegionCode && !regionService.exists(household.regionCode)){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.reference.error", ["Region", "regionCode", household.regionCode], ["regionCode"])
        }
        //C4. Check Code Prefix Reference existence (Region Existence)
        if (!isBlankHouseholdCode && !household.householdCode?.startsWith(household.regionCode)) { // !regionService.prefixExists(household.householdCode)){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.pattern.prefix.region.reference.error", [household.householdCode], ["householdCode"])
        }
        //C5. Check User existence
        if (!isBlankCollectedBy && !userService.exists(household.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.user.dont.exists.error", [household.collectedBy], ["collectedBy"])
        }

        //C6. Check Duplicate of householdCode
        if (!isBlankHouseholdCode){

            def existentHousehold = getHousehold(household.householdCode)
            boolean exists = existentHousehold != null


            //check is if is a pre-registration trying to be registered while the code exists
            if (exists) {

                if (household.preRegistered) {
                    errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.household.code.cannot.preregist.error", [household.householdCode], ["householdCode"])
                } else {
                    if (!existentHousehold.preRegistered) {
                        //its duplicate - otherwise it can upgrade the household status from pre-registered to fully registered
                        errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.reference.duplicate.error", ["Household", "householdCode", household.householdCode], ["householdCode"])
                    }
                }

            }

        }

        //Check If invalid modules where selected
        if (!isBlankModules && notFoundModules.size()>0) {
            errors << errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.field.module.codes.notfound.error", [notFoundModules], ["modules"])
        }

        return errors
    }

    private Household newHouseholdInstance(RawHousehold rh){

        def hierarchies = regionService.getHierarchies(rh.regionCode)
        def modules = moduleService.getListModulesFrom(rh.modules)
        moduleService.addDefaultModuleTo(modules) //ensure it has a default module = generall access

        Household household = getHousehold(rh.householdCode) //get Household if exists

        if (!(household != null && household.preRegistered)) { //upgrades a Household to fully registered or create a new one
            household = new Household()
        }


        household.code = rh.householdCode
        household.name = rh.householdName
        household.region = rh.regionCode
        //household.headCode = rh.headCode
        //household.headName = rh.headName
        household.type = HouseholdType.getFrom(rh.householdType)
        household.institutionType = HouseholdInstitutionType.getFrom(rh.institutionType)
        household.institutionTypeOther = rh.institutionTypeOther

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
        household.gpsLongitude = StringUtil.getDouble(rh.gpsLon)
        household.gpsNull = household.gpsLatitude==null || household.gpsLongitude==null

        household.cosLatitude =  household.gpsLatitude==null ?  null : Math.cos(household.gpsLatitude*Math.PI / 180.0)
        household.sinLatitude =  household.gpsLatitude==null ?  null : Math.sin(household.gpsLatitude*Math.PI / 180.0)
        household.cosLongitude = household.gpsLongitude==null ? null : Math.cos(household.gpsLongitude*Math.PI / 180.0)
        household.sinLongitude = household.gpsLongitude==null ? null : Math.sin(household.gpsLongitude*Math.PI / 180.0)

        household.preRegistered = rh.preRegistered

        modules.each {
            household.addToModules(it)
        }

        //set collected by info
        household.collectedId = rh.id
        household.collectedDeviceId = rh.collectedDeviceId
        household.collectedBy = userService.getUser(rh.collectedBy)
        household.collectedStart = rh.collectedStart
        household.collectedEnd = rh.collectedEnd
        household.collectedDate = rh.collectedDate
        household.updatedDate = rh.uploadedDate

        return household

    }
    //</editor-fold>

}
