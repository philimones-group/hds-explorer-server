package org.philimone.hds.explorer.server.model.main

import grails.validation.ValidationException
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.ModularDomainEntity
import org.philimone.hds.explorer.server.model.json.JConstant

import static org.springframework.http.HttpStatus.*

class ModuleController {

    def moduleService
    def codeGeneratorService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond moduleService.list(params), model:[moduleCount: moduleService.count()]
    }

    def show(String id) {
        respond moduleService.get(id)
    }

    def create() {
        params.code = codeGeneratorService.generateModuleCode(null)
        respond new Module(params)
    }

    def save(Module module) {
        if (module == null) {
            notFound()
            return
        }

        try {
            moduleService.save(module)
        } catch (ValidationException e) {
            respond module.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'module.label', default: 'Module'), module.code])
                redirect module
            }
            '*' { respond module, [status: CREATED] }
        }
    }

    def edit(String id) {
        respond moduleService.get(id)
    }

    def update(Module module) {
        if (module == null) {
            notFound()
            return
        }

        try {
            moduleService.save(module)
        } catch (ValidationException e) {
            respond module.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'module.label', default: 'Module'), module.code])
                redirect module
            }
            '*'{ respond module, [status: OK] }
        }
    }

    def delete(String id) {
        if (id == null) {
            notFound()
            return
        }

        moduleService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'module.label', default: 'Module'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'module.label', default: 'Module'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    def add = {
        def max = 9

        def entities = ModularDomainEntity.values()

        [groupModulesMappings:  moduleService.getGroupModulesMappings(), entities: entities, grantModes: getGrantModes()]
    }

    def uploadCodesFile = {

        def file = request.getFile('fileUpload')
        def fileName = file.originalFilename
        def newFile = SystemPath.externalDocsPath + File.separator + fileName

        println "test2 ${file}"
        println "test3 ${file.originalFilename}"

        file.transferTo(new File(newFile))

        //read csv file and get the list of columns
        def ccounts = moduleService.getEntitiesCodesCounting(newFile)
        def selectedEntity = params.selectedEntity
        def entities = ModularDomainEntity.values()
        def selectedModules = params.selectedModules
        def grantMode = params.grantMode

        render view: "add", model: [groupModulesMappings:  moduleService.getGroupModulesMappings(), modulesShortFilename: fileName, modulesFilename: newFile , selectedEntity: selectedEntity, entities: entities,
                                    totalRegions: ccounts[0], totalHouseholds: ccounts[1], totalMembers: ccounts[2], selectedModules: selectedModules, grantModes: getGrantModes(), grantModeValue: grantMode]
    }

    def saveModuleMappings = {
        def grantMode = params.grantModeValue
        def selEntity = params.entity
        def filename = params["filename"]
        def modules = Module.getAll(params.list("modules"))

        println "file ${filename}, modules: ${modules}, ${params}"


        if (grantMode == "0") {

            def list = moduleService.getEntitiesCodesList(filename)


            def resultList = moduleService.grantEntitiesAccess(list, modules)

            int countr = resultList[0];
            int counth = resultList[1];
            int countm = resultList[2];

            flash.message = g.message(code: "module.updates.success.csv.label", args: [countr, counth, countm])
        }
        else if (grantMode == "1") {

            if (ModularDomainEntity.REGION.name.equalsIgnoreCase(selEntity)) {
                //grant access to all Regions
                moduleService.grantRegionsAccess(modules)
            }

            if (ModularDomainEntity.HOUSEHOLD.name.equalsIgnoreCase(selEntity)) {
                //grant access to all Households
                moduleService.grantHouseholdsAccess(modules)
            }

            if (ModularDomainEntity.MEMBER.name.equalsIgnoreCase(selEntity)) {
                //grant access to all Members
                moduleService.grantMembersAccess(modules)
            }

            flash.message = g.message(code: "module.updates.success.all.label", args: [selEntity])
        }

        redirect action: "add"
    }

    List<JConstant> getGrantModes(){
        def list = new ArrayList<JConstant>()

        list << new JConstant(name: g.message(code: "module.updates.grantMode.csv.label"), value: "0")
        list << new JConstant(name: g.message(code: "module.updates.grantMode.all.label"), value: "1")

        return list
    }
}
