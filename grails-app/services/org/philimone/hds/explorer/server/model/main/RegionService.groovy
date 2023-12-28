package org.philimone.hds.explorer.server.model.main

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.json.JRegionLevel
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.philimone.hds.explorer.server.model.settings.ApplicationParam

@Transactional
class RegionService {

    def userService
    def codeGeneratorService
    def coreExtensionService
    def errorMessageService
    def moduleService

    //<editor-fold desc="Region Utilities Methods">
    boolean exists(String regionCode) {
        Region.countByCode(regionCode) > 0
    }

    boolean prefixExists(String code){
        exists(code.length()>2 ? code.substring(0, 3) : code)
    }

    Region getRegion(String code) {
        if (StringUtil.isBlank(code)) return null
        return Region.findByCode(code)
    }

    RegionLevel getNextLevel(Region region){
        if (region == null)
            return  RegionLevel.HIERARCHY_1
        else {

            def level = region.hierarchyLevel

            if (level == getLowestRegionLevel()) return null

            return level?.nextLevel()

            /*
            int level = 0
            try {
                def c = region.hierarchyLevel.code
                c = c.replaceAll("hierarchy","")
                level = Integer.parseInt(c)
            } catch (Exception ex){
                
            }

            level++

            return RegionLevel."HIERARCHY_${level}"
            */
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

    String generateCode(Region parentRegion, String regionName){

        def lowestRegionLevel = getLowestRegionLevel()

        if ((parentRegion == null && lowestRegionLevel==RegionLevel.HIERARCHY_1) || (parentRegion != null && lowestRegionLevel == getNextLevel(parentRegion))) {
            return codeGeneratorService.generateLowestRegionCode(parentRegion, regionName)
        }


        return codeGeneratorService.generateRegionCode(parentRegion, regionName)
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
            RawExecutionResult<Region> obj = RawExecutionResult.newErrorResult(RawEntity.REGION, errors)
            return obj
        }

        def region = newRegionInstance(rawRegion)

        //Validate using Gorm Validations
        def result = region.save(flush:true)

        if (region.hasErrors()){

            errors = errorMessageService.getRawMessages(RawEntity.REGION, region)

            RawExecutionResult<Region> obj = RawExecutionResult.newErrorResult(RawEntity.REGION, errors)
            return obj
        } else {
            region = result
        }

        //--> take the extensionXml and save to Extension Table
        def resultExtension = coreExtensionService.insertRegionExtension(rawRegion, result)
        if (resultExtension != null && !resultExtension.success) { //if null - there is no extension to process
            //it supposed to not fail
            println "Failed to insert extension: ${resultExtension.errorMessage}"
        }

        RawExecutionResult<Region> obj = RawExecutionResult.newSuccessResult(RawEntity.REGION, region)
        return obj
    }

    ArrayList<RawMessage> validate(RawRegion region){
        def errors = new ArrayList<RawMessage>()

        def isBlankRegionCode = StringUtil.isBlank(region.regionCode)
        def isBlankRegionName = StringUtil.isBlank(region.regionName)
        def isBlankParentCode = StringUtil.isBlank(region.parentCode)
        def isBlankCollectedBy = StringUtil.isBlank(region.collectedBy)
        def isBlankModules = StringUtil.isBlank(region.modules)
        def notFoundModules = moduleService.getNonExistenceModuleCodes(region.modules)

        //C1. Check Blank Fields (regionCode)
        if (isBlankRegionCode){
            errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.blank", ["regionCode"], ["regionCode"])
        }
        //C1. Check Blank Fields (regionName)
        if (isBlankRegionName){
            errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.blank", ["regionName"], ["regionName"])
        }
        //C1. Check Blank Fields (parentCode)
        //if (isBlankParentCode){
        //    errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.blank", ["parentCode"], ["parentCode"])
        //}

        //C3. Check parent Region reference existence
        if (!isBlankParentCode && !exists(region.parentCode)){
            errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.reference.error", ["Region", "parentCode", region.parentCode], ["parentCode"])
        }

        //C4. Check loop on regionCode equals to parentCode
        if (!isBlankRegionCode && !isBlankParentCode && region.regionCode.equalsIgnoreCase(region.parentCode)) {
            errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.region.code.loop.parent.error", [region.regionCode, region.parentCode], ["regionCode"])
        }

        //C2. Check Code Regex Pattern
        if (!isBlankRegionCode) {
            def lowestLevel = getLowestRegionLevel()
            def validateLowest = isBlankParentCode && lowestLevel==RegionLevel.HIERARCHY_1
            println "lwl = ${lowestLevel}"

            if (validateLowest==false && !isBlankParentCode && exists(region.parentCode)){
                def parentRegion = getRegion(region.parentCode)
                def nextLevel = getNextLevel(parentRegion)

                if (nextLevel == null) {
                    //error: Can't create a new region with code=[{0}], because the region parent with parentCode=[{1}] is invalid, it is the lowest region level available
                    errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.region.invalid.parent.error", [region.regionCode, region.parentCode], ["parentCode"])


                } else if (nextLevel == lowestLevel) {
                    validateLowest = true
                }
            }

            println "validate ${validateLowest}"

            //Validate using lowestRegionCodeValidation and regionCodeValidation
            if ((validateLowest && !codeGeneratorService.isLowestRegionCodeValid(region.regionCode)) || (!validateLowest && !codeGeneratorService.isRegionCodeValid(region.regionCode))) {
                errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.pattern.no.matches", ["regionCode", codeGeneratorService.regionSampleCode], ["regionCode"])
            }

        }

        //C4. Check User existence
        if (!isBlankCollectedBy && !userService.exists(region.collectedBy)){
            errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.user.dont.exists.error", [region.collectedBy], ["collectedBy"])
        }

        //C5. Check Duplicate of regionCode
        if (!isBlankRegionCode && exists(region.regionCode)){
            errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.reference.duplicate.error", ["Region", "regionCode", region.regionCode], ["regionCode"])
        }

        //Check If invalid modules where selected
        if (!isBlankModules && notFoundModules.size()>0) {
            errors << errorMessageService.getRawMessage(RawEntity.REGION, "validation.field.module.codes.notfound.error", [notFoundModules], ["modules"])
        }

        return errors
    }

    private Region newRegionInstance(RawRegion rr){

        def parent = getRegion(rr.parentCode)
        def nextLevel = getNextLevel(parent)
        def modules = moduleService.getListModulesFrom(rr.modules)
        moduleService.addDefaultModuleTo(modules) //ensure it has a default module = generall access

        Region region = new Region()
        region.code = rr.regionCode
        region.name = rr.regionName
        region.hierarchyLevel = nextLevel
        region.parent = parent
        region.parentRegionCode = parent?.code

        modules.each {
            region.addToModules(it)
        }

        //set collected by info
        region.collectedId = rr.id
        region.collectedBy = userService.getUser(rr.collectedBy)
        region.collectedDeviceId = rr.collectedDeviceId
        region.collectedStart = rr.collectedStart
        region.collectedEnd = rr.collectedEnd
        region.collectedDate = rr.collectedDate
        region.updatedDate = rr.uploadedDate

        //println "parent: ${parent}\nlevel: ${nextLevel}"

        return region

    }
    //</editor-fold>

    //*Controller utilities *//
    Region get(Serializable id){
        Region.get(id)
    }

    List<Region> list(Map args){
        Region.list(args)
    }

    Long count(){
        Region.count()
    }

    void delete(Serializable id){
        get(id).delete()
    }

    Region save(Region module){
        module.save(flush:true)
    }
    
    List<JRegionLevel> getRegionLevels(){
        def list = []
        ApplicationParam.executeQuery("select p from ApplicationParam p where p.name like '%hierarchy%' and p.value is not null order by p.name asc" ).each {
            list << new JRegionLevel(level: it.name, name: it.value, regionLevel: RegionLevel.getFrom(it.name))
        }
        return list
    }

    List<JRegionLevel> getRegionLevelsByExistentRegions(){
        def list = new ArrayList<JRegionLevel>()
        def listRemove = new ArrayList<JRegionLevel>()

        ApplicationParam.executeQuery("select p from ApplicationParam p where p.name like 'hierarchy%' and p.value is not null order by p.name asc" ).each {
            list << new JRegionLevel(level: it.name, name: it.value, regionLevel: RegionLevel.getFrom(it.name))
        }

        def levels = Region.executeQuery("select distinct(r.hierarchyLevel) from Region r")
        def max = 1;

        if (levels.size()>0){
            levels.each {
                int v = Integer.parseInt(it.code.replace("hierarchy", ""))
                max = Math.max(max, v+1)
            }
        }

        //println "max level = ${max}"

        list.each { jr ->
            int v = Integer.parseInt(jr.level.replace("hierarchy", ""))
            if (v > max) listRemove.add(jr)
        }

        list.removeAll(listRemove)

        return list
    }

    String getRegionLevelName(String level){
        ApplicationParam.findByName(level)?.value
    }

    Map<String, String> getRegionLevelNames(){
        def map = new LinkedHashMap()
        ApplicationParam.executeQuery("select p from ApplicationParam p where p.name like '%hierarchy%' and p.value is not null order by p.name asc" ).each {
            map.put(it.name, it.value)
        }
        return map
    }

    RegionLevel getLowestRegionLevel() {
        def lastHierarchy = ""
        ApplicationParam.executeQuery("select p from ApplicationParam p where p.name like '%hierarchy%' and p.value is not null order by p.name asc" ).each { ap ->
            lastHierarchy = ap.name
        }

        return RegionLevel.getFrom(lastHierarchy)

    }

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
