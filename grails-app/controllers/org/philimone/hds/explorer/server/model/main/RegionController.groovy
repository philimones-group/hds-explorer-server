package org.philimone.hds.explorer.server.model.main

import grails.converters.JSON
import grails.validation.ValidationException
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

import static org.springframework.http.HttpStatus.*

class RegionController {

    RegionService regionService

    //static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        respond regionService.list(params), model:[regionCount: regionService.count(), hierarchyLevelsMap : regionService.getRegionLevelNames()]
    }

    def show = {
        def region = regionService.get(params.id)

        def levelName = regionService.getRegionLevelName(region.hierarchyLevel.code)

        respond region, model: [hierarchyLevel: levelName]
    }

    def create() {
        respond new Region(params), model: [regionLevels: regionService.regionLevelsByExistentRegions, hierarchyLevelsJson : regionService.getRegionLevelNames() as JSON]
    }

    def save(Region region) {
        if (region == null) {
            notFound()
            return
        }

        println "${region.code}, ${region.name}, ${params.hierarchyLevel}, ${params.parent}"

        def parentCode = null as String

        if (params.parent){
            def parentRegion = Region.get(params.parent)
            parentCode = parentRegion.code
        }

        if (RegionLevel.getFrom(params.hierarchyLevel) != RegionLevel.HIERARCHY_1 && params.parent == null){
            def errorMessages = [new RawMessage(message(code: "region.parent.level.nullable.label", default: "Parent region missing"), ["parent"])]

            respond region, view:'create', model: [errorMessages: errorMessages, hierarchyLevel: params.hierarchyLevel]
            return
        }

        RawRegion rawRegion = new RawRegion(regionCode: region.code, regionName: region.name, parentCode: parentCode)


        def result = regionService.createRegion(rawRegion)

        if (result.status == RawExecutionResult.Status.SUCCESS) {
            region = result.domainInstance
        } else {

            respond region, view:'create', model: [errorMessages: result.errorMessages, hierarchyLevel: params.hierarchyLevel]
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'region.label', default: 'Region'), region.name])
                redirect region
            }
            '*' { respond region, [status: CREATED] }
        }
    }

    def edit = {
        def region = regionService.get(params.id)
        def levelName = regionService.getRegionLevelName(region.hierarchyLevel.code)

        respond region, model: [hierarchyLevel: levelName]
    }

    def update(Region region) {
        if (region == null) {
            notFound()
            return
        }

        try {
            regionService.save(region)
        } catch (ValidationException e) {
            respond region.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'region.label', default: 'Region'), region.name])
                redirect region
            }
            '*'{ respond region, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        regionService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'region.label', default: 'Region'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'region.label', default: 'Region'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def generateCodeGsp = {
        def regionName = params.name
        def code = regionService.generateCode(regionName)

        println "new generated code: ${code}"

        render "${code}"
    }

    def loadRegionsToGsp = {
        def args = params.name.split(":") //hierarchyLevel+":"+selectId

        def hierarchyLevel = RegionLevel.getFrom(args[0])
        def selectId = args[1]
        def regions = Region.findAllByHierarchyLevel(hierarchyLevel)

        render g.select(id: "${selectId}", name: "${selectId}", from: regions, optionKey:"id", optionValue:"name", noSelection: ['':''])
    }

    def loadRegionsByIdToGsp = {
        def args = params.name.split(":") //parentRegionId+":"+selectId

        def parentRegionId = args[0]
        def selectId = args[1]
        def parentRegion = Region.get(parentRegionId)
        def regions = Region.findAllByParent(parentRegion)

        render g.select(id: "${selectId}", name: "${selectId}", from: regions, optionKey:"id", optionValue:"name", noSelection: ['':''])
    }
}
