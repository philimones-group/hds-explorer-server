package org.philimone.hds.explorer.server.model.main

import grails.converters.JSON
import grails.validation.ValidationException
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.RawRegion
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

import static org.springframework.http.HttpStatus.*

class RegionController {

    RegionService regionService
    ModuleService moduleService
    def dataModelsService
    def applicationParamService

    //static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        //params.max = Math.min(max ?: 10, 100)

        //def regionList = Region.executeQuery("select r from Region r order by r.createdDate asc, r.hierarchyLevel asc")

        render view: "index", model:[hierarchyLevelsMap : regionService.getRegionLevelNames()]
    }

    def show = {
        def region = regionService.get(params.id)

        def levelName = regionService.getRegionLevelName(region.hierarchyLevel.code)

        def modules = moduleService.findAllByCodes(region.modules)
        respond region, model: [hierarchyLevel: levelName, modules: modules]
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

        def modulesText = moduleService.getListModulesAsText(params.list("modules"))
        def modules = moduleService.findAllByCodes(params.list("modules"))

        if (RegionLevel.getFrom(params.hierarchyLevel) != RegionLevel.HIERARCHY_1 && params.parent == null){
            def errorMessages = [new RawMessage(""+message(code: "region.parent.level.nullable.label", default: "Parent region missing"), ["parent"])]

            respond region, view:'create', model: [errorMessages: errorMessages, hierarchyLevel: params.hierarchyLevel, modules: modules]
            return
        }

        RawRegion rawRegion = new RawRegion(regionCode: region.code, regionName: region.name, parentCode: parentCode, modules: modulesText)


        def result = regionService.createRegion(rawRegion)

        if (result.status == RawExecutionResult.Status.SUCCESS) {
            region = result.domainInstance
        } else {

            respond region, view:'create', model: [errorMessages: result.errorMessages, hierarchyLevel: params.hierarchyLevel, modules: modules]
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

        def modules = Module.findAllByCodeInList(region.modules)

        respond region, model: [hierarchyLevel: levelName, modules: modules]
    }

    def update(Region region) {
        if (region == null) {
            notFound()
            return
        }

        //def modulesText = moduleService.getListModulesAsText(params.list("modules"))
        //def modules = Module.getAll(params.list("modules"))

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
        def args = params.name.split(":") //name+":"+parentRegionId
        def regionName = args[0] as String
        def parentRegionId = args[1] as String
        def parentRegion = regionService.get(parentRegionId)
        def code = regionService.generateCode(parentRegion, regionName)

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
        def regions = Region.findAllByParent(parentRegion, [sort: "code", order: "asc"])

        render g.select(id: "${selectId}", name: "${selectId}", from: regions, optionKey:"id", optionValue:"name", noSelection: ['':''])
    }

    def showRegion = {
        def region = regionService.getRegion(params.id)

        redirect controller: "region", action: "show", id: region.id
    }

    def regionList = {
        //convert datatables params to gorm params
        def jqdtParams = [:]
        params.each { key, value ->
            def keyFields = key.replace(']','').split(/\[/)
            def table = jqdtParams
            for (int f = 0; f < keyFields.size() - 1; f++) {
                def keyField = keyFields[f]
                if (!table.containsKey(keyField))
                    table[keyField] = [:]
                table = table[keyField]
            }
            table[keyFields[-1]] = value
        }

        def params_search = jqdtParams.search?.value
        def columnsList = jqdtParams.columns.collect { k, v -> v.data }
        def orderList = jqdtParams.order.collect { k, v -> [columnsList[v.column as Integer], v.dir] }
        def entitiesList = dataModelsService.findRegionLevelLike("${params_search}")
        def hierarchyParams = applicationParamService.getHierarchyLevelsParameters()

        //code, name, hierarchyLevel, hierarchyName, parent, createdBy, createdDate

        //FILTERS - if not null will filter
        //we must search also hierarchyLevel.code, hierarchyLevel.name.i18n, parent.name
        def search_filter = (params_search != null && !"${params_search}".empty) ? "%${params_search}%" : null
        def filterer = {
            or {
                if (search_filter) ilike 'code', search_filter
                if (search_filter) ilike 'name', search_filter
                if (search_filter) 'in'('hierarchyLevel', entitiesList)
                //if (search_filter) ilike 'hierarchyLevel', search_filter
            }
        }

        //ORDERS
        def orderer = Region.withCriteria {
            filterer.delegate = delegate
            filterer()
            orderList.each { oi ->
                switch (oi[0]) {
                    case 'code':           order 'code',          oi[1]; break
                    case 'name':           order 'name',          oi[1]; break
                    case 'hierarchyLevel': order 'hierarchyLevel',        oi[1]; break
                    case 'parent':         order 'parent', oi[1]; break
                    case 'createdBy':      order 'createdBy', oi[1]; break
                    case 'createdDate':    order 'createdDate',   oi[1]; break
                }
            }
            maxResults (jqdtParams.length as Integer)
            firstResult (jqdtParams.start as Integer)
        }


        //Display records
        def objects = orderer.collect { obj ->
            ['code':           "<a href='${createLink(controller: 'region', action: 'show', id: obj.id)}'>${obj.code}</a>",
             'name':           obj.name,
             'hierarchyLevel': obj.hierarchyLevel.code,
             'hierarchyName':  message(code: obj.hierarchyLevel.name) + " - ${hierarchyParams.find { it.name.equals(obj.hierarchyLevel.code)}?.value}",
             'head':           obj.head ? "${obj.head?.code} - ${obj.head?.name}" : "",
             'parent':         "<a href='${createLink(controller: 'region', action: 'show', id: obj.parent?.id)}'>${obj}</a>",
             'createdBy':      obj.createdBy?.getFullname(),
             'createdDate':    StringUtil.formatLocalDateTime(obj.createdDate)
            ]
        }


        //FINAL PARAMETERS
        def recordsTotal = Region.count()
        def recordsFiltered = Region.createCriteria().count {
            filterer.delegate = delegate
            filterer()
        }


        //println "region recordsTotal $recordsTotal"
        //println "region recordsFiltered $recordsFiltered"
        //println "region ${members.size()}"


        def result = [draw: jqdtParams.draw, recordsTotal: recordsTotal, recordsFiltered: recordsFiltered, data: objects]

        render result as JSON
    }
}
