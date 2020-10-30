package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.Codes

@Transactional
class RegionService {

    def userService
    def errorMessageService

    //<editor-fold desc="Region Utilities Methods">
    boolean isCodeValid(String regionCode){
        regionCode.matches(Codes.REGION_CODE_PATTERN)
    }

    boolean exists(String regionCode) {
        Region.countByCode(regionCode) > 0
    }

    boolean prefixExists(String code){
        exists(code.length()>2 ? code.substring(0, 3) : code)
    }

    Region getRegion(String code) {
        if (StringUtil.isBlank(code)) return null;
        return Region.findByCode(code)
    }

    RegionLevel getNextLevel(Region region){
        if (region == null)
            return  RegionLevel.HIERARCHY_1
        else {
            int level = 0;
            try {
                def c = region.hierarchyLevel.code
                c = c.replaceAll("hierarchy","")
                level = Integer.parseInt(c)
            } catch (Exception ex){
            }

            level++

            return RegionLevel."HIERARCHY_${level}"
        }
    }

    HierarchyRegion getHierarchies(String lastRegionCode){
        def hierarchy = new HierarchyRegion()
        def region = Region.findByCode(lastRegionCode)

        //println "search region ${lastRegionCode}: ${region}"
        if (region != null){
            hierarchy.region = region
            hierarchy.regionCode = region.code

            def parent = region

            while (parent != null){

                //println "${parent.code}, parent=${parent.parent}, ${parent.hierarchyLevel}"
                hierarchy."${parent.hierarchyLevel.code}" = parent.code
                parent = parent.parent // get the previous hierarchy level
            }
        }

        return hierarchy
    }
    //</editor-fold>

    //<editor-fold desc="Region Factory/Manager Methods">
    /**
     * Validate Household and Create
     */
    RawExecutionResult<Region> createRegion(RawRegion rawRegion) {

        /* Run Checks and Validations */

        def errors = validate(rawRegion)

        if (!errors.isEmpty()){
            //create result and close
            RawExecutionResult<Region> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        def region = newRegionInstance(rawRegion)

        //Validate using Gorm Validations
        region = region.save(flush:true)

        if (region.hasErrors()){

            errors = errorMessageService.getRawMessages(region)

            RawExecutionResult<Region> obj = RawExecutionResult.newErrorResult(errors)
            return obj
        }

        RawExecutionResult<Region> obj = RawExecutionResult.newSuccessResult(region)
        return obj
    }

    ArrayList<RawMessage> validate(RawRegion region){
        def errors = new ArrayList<RawMessage>()

        def isBlankRegionCode = StringUtil.isBlank(region.regionCode)
        def isBlankRegionName = StringUtil.isBlank(region.regionName)
        def isBlankParentCode = StringUtil.isBlank(region.parentCode)
        def isBlankCollectedBy = StringUtil.isBlank(region.collectedBy)

        //C1. Check Blank Fields (regionCode)
        if (isBlankRegionCode){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["regionCode"], ["regionCode"])
        }
        //C1. Check Blank Fields (regionName)
        if (isBlankRegionName){
            errors << errorMessageService.getRawMessage("validation.field.blank", ["regionName"], ["regionName"])
        }
        //C1. Check Blank Fields (parentCode)
        //if (isBlankParentCode){
        //    errors << errorMessageService.getRawMessage("validation.field.blank", ["parentCode"], ["parentCode"])
        //}
        //C2. Check Code Regex Pattern
        if (!isBlankRegionCode && !isCodeValid(region.regionCode)) {
            errors << errorMessageService.getRawMessage("validation.field.pattern.no.matches", ["regionCode", "TXU"], ["regionCode"])
        }
        //C3. Check Region reference existence
        if (!isBlankParentCode && !exists(region.parentCode)){
            errors << errorMessageService.getRawMessage("validation.field.reference.error", ["Region", "parentCode", region.parentCode], ["parentCode"])
        }

        //C4. Check User existence
        if (!isBlankCollectedBy && !userService.exists(region.collectedBy)){
            errors << errorMessageService.getRawMessage("validation.field.user.dont.exists.error", [region.collectedBy], ["collectedBy"])
        }

        //C5. Check Duplicate of regionCode
        if (!isBlankRegionCode && exists(region.regionCode)){
            errors << errorMessageService.getRawMessage("validation.field.reference.duplicate.error", ["Region", "regionCode", region.regionCode], ["regionCode"])
        }

        return errors
    }

    private Region newRegionInstance(RawRegion rr){

        Region region = new Region()

        def parent = getRegion(rr.parentCode)
        def nextLevel = getNextLevel(parent)

        region.code = rr.regionCode
        region.name = rr.regionName
        region.hierarchyLevel = nextLevel
        region.parent = parent

        //set collected by info
        region.collectedBy = userService.getUser(rr.collectedBy)
        region.collectedDate = rr.collectedDate

        //println "parent: ${parent}\nlevel: ${nextLevel}"

        return region

    }
    //</editor-fold>

    class HierarchyRegion {
        Region region
        String regionCode
        String hierarchy1
        String hierarchy2
        String hierarchy3
        String hierarchy4
        String hierarchy5
        String hierarchy6
        String hierarchy7
        String hierarchy8
    }
}
