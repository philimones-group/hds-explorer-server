package org.philimone.hds.explorer.controllers

import grails.converters.JSON
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.model.json.JLanguage
import org.philimone.hds.explorer.server.model.main.CoreFormColumnOptions
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorFactory
import org.philimone.hds.explorer.taglib.DatatablesUITagLib
import org.philimone.hds.explorer.taglib.TabulatorTagLib

class SettingsController {

    def generalUtilitiesService
    def applicationParamService
    def codeGeneratorService
    def coreFormColumnOptionsService
    def settingsService
    def dt = new DatatablesUITagLib()
    def tb = new TabulatorTagLib()

    def parameters() {
        List<JLanguage> languages = generalUtilitiesService.getSystemLanguages()
        JLanguage currentLanguage = generalUtilitiesService.getCurrentSystemLanguage()
        def supportedCalendars = generalUtilitiesService.getSystemSupportedCalendars()
        def selectedCalendar = settingsService.getCurrentSupportedCalendar()
        def codeGenIncrementalRules = settingsService.codeGeneratorsIncrementalRules
        def regionHeadSupport = settingsService.getRegionHeadSupport()
        def gpsRequired = settingsService.getVisitGpsRequired()

        [languages: languages, selectedLanguage: currentLanguage.language, calendars: supportedCalendars, selectedCalendar: selectedCalendar, codeGenerators: Codes.SYSTEM_ALL_CODE_GENERATORS,
         codeGeneratorsRules: codeGenIncrementalRules, selectedCodeGenerator: Codes.SYSTEM_CODE_GENERATOR, selectedGpsRequired: gpsRequired,
         selectedCodeGeneratorIncRule: Codes.SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE, selectedRegionHeadSupport: regionHeadSupport, errorMessages: new ArrayList<String>()]
    }

    def updateParameters = {

        def selectedLanguage = params.systemLanguage
        def selectedCalendar = params.systemInputCalendar
        def selectedCodeGenerator = params.codeGenerator
        def selectedCodeGeneratorIncRule = params.codeGeneratorIncRule
        def selectedRegionHeadSupport = (params.regionHeadSupport==null ? false : params.regionHeadSupport?.equals("on")) as Boolean
        def selectedGpsRequired = (params.gpsRequired==null ? false : params.gpsRequired?.equals("on")) as Boolean
        def codeGenIncrementalRules = settingsService.codeGeneratorsIncrementalRules
        def supportedCalendars = generalUtilitiesService.getSystemSupportedCalendars()
        def errorMessages = new ArrayList<String>()

        println "selected lng: ${selectedLanguage}"
        println "selected cal: ${selectedCalendar}"
        println "selected cgn: ${selectedCodeGenerator}"
        println "selected cgr: ${selectedCodeGeneratorIncRule}"
        println "selected rhs: ${params.regionHeadSupport}, ${selectedRegionHeadSupport}, Codes.SYSTEM_REGION_HEAD_SUPPORT=${Codes.SYSTEM_REGION_HEAD_SUPPORT}"
        println "selected gps: ${selectedGpsRequired}"

        try {

            //Update System Language
            if (selectedLanguage != null) {
                Codes.SYSTEM_LANGUAGE = selectedLanguage
                applicationParamService.updateApplicationParam(Codes.PARAMS_SYSTEM_LANGUAGE, selectedLanguage)

                settingsService.afterUpdateSystemLanguage()
            } else {
                errorMessages << message(code: 'settings.parameters.update.language.error.null.label') + ""
            }

            //Update System Calendar
            if (selectedCalendar != null) {
                def supported = selectedCalendar?.equals(Codes.SYSTEM_SUPPORTED_CALENDAR_ETHIOPIAN)
                Codes.SYSTEM_USE_ETHIOPIAN_CALENDAR = supported
                applicationParamService.updateApplicationParam(Codes.PARAMS_SYSTEM_USE_ETHIOPIAN_CALENDAR, ""+Codes.SYSTEM_USE_ETHIOPIAN_CALENDAR)

                settingsService.afterUpdateSystemCalendar()
            } else {
                errorMessages << message(code: 'settings.parameters.update.language.error.null.label') + ""
            }

            //Update Code Generator
            if (selectedCodeGenerator != null && (!Codes.SYSTEM_CODE_GENERATOR.equals(selectedCodeGenerator))) {
                //its a non null className, that is different from the one stored in the database

                if (Region.count() > 0 || User.count()>1 || Module.count() > 1) {
                    //cannot change the code generator while the is Regions or Users or Modules inserted in the system
                    errorMessages << message(code: 'settings.parameters.update.codegenerator.not.possible.label')+""
                } else {
                    Codes.SYSTEM_CODE_GENERATOR = selectedCodeGenerator
                    applicationParamService.updateApplicationParam(Codes.PARAMS_SYSTEM_CODE_GENERATOR, selectedCodeGenerator)
                    codeGeneratorService.codeGenerator = CodeGeneratorFactory.newInstance()
                }
            }

            //Update Code Generator Incremental Rule
            if (selectedCodeGeneratorIncRule != null && (!Codes.SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE.equals(selectedCodeGeneratorIncRule))) {

                Codes.SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE = selectedCodeGeneratorIncRule
                applicationParamService.updateApplicationParam(Codes.PARAMS_SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE, selectedCodeGeneratorIncRule)
                codeGeneratorService.codeGenerator = CodeGeneratorFactory.newInstance()
            }

            //Update Region Head Support
            if (selectedRegionHeadSupport != null && !(Codes.SYSTEM_REGION_HEAD_SUPPORT == selectedRegionHeadSupport)) {
                println "its changed rhs"

                Codes.SYSTEM_REGION_HEAD_SUPPORT = selectedRegionHeadSupport
                applicationParamService.updateApplicationParam(Codes.PARAMS_SYSTEM_REGION_HEAD_SUPPORT, ""+selectedRegionHeadSupport)
            }

            //Update Visit GPS Required Enforcement
            if (selectedGpsRequired != null && !(Codes.SYSTEM_VISIT_GPS_REQUIRED == selectedGpsRequired)) {
                println "its changed gps"

                Codes.SYSTEM_VISIT_GPS_REQUIRED = selectedGpsRequired
                applicationParamService.updateApplicationParam(Codes.PARAMS_SYSTEM_VISIT_GPS_REQUIRED, ""+selectedGpsRequired)
            }

            flash.message = message(code: 'settings.parameters.update.success.label')

        } catch(Exception ex) {
            ex.printStackTrace()
            errorMessages << message(code: 'settings.parameters.update.error.label') + ": ${ex.getMessage()}"
        }

        List<JLanguage> languages = generalUtilitiesService.getSystemLanguages()
        JLanguage currentLanguage = generalUtilitiesService.getCurrentSystemLanguage()

        println "${errorMessages}"

        render view: "parameters", model: [languages: languages, selectedLanguage: currentLanguage.language, calendars: supportedCalendars, selectedCalendar: selectedCalendar,
                                           errorMessages: errorMessages, codeGenerators: Codes.SYSTEM_ALL_CODE_GENERATORS, codeGeneratorsRules: codeGenIncrementalRules,
                                           selectedCodeGenerator: Codes.SYSTEM_CODE_GENERATOR, selectedCodeGeneratorIncRule: Codes.SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE,
                                           selectedRegionHeadSupport: selectedRegionHeadSupport, selectedGpsRequired: selectedGpsRequired]
    }

    def customOptions = {

        flash.message = message(code: 'settings.coreformoptions.dataintegrity.info.label')
        [forms: coreFormColumnOptionsService.getCustomOptionsForms()]
    }

    def getCustomOptionsColumns = {
        def columns = coreFormColumnOptionsService.getCustomOptionsColumns(params.name)

        render g.select(id: "column", name: "column", from: columns, optionKey: "value", optionValue: "name")
    }

    def renderCustomOptionsTable = {
        //println params

        def dataUrl = createLink(controller: 'settings', action: 'fetchOptionsList', params: [columnCode: params.name])
        def updateUrl = createLink(controller: 'settings', action: 'updateCustomOptions')
        def createUrl = createLink(controller: 'settings', action: 'createCustomOptions')
        def deleteUrl = createLink(controller: 'settings', action: 'deleteCustomOptions')
        def addlabel = message(code: 'settings.coreformoptions.addlabel.label')
        def remlabel = message(code: 'settings.coreformoptions.remlabel.label')
        def errlabel = message(code: 'settings.coreformoptions.errlabel.label')
        def inflabel = message(code: 'settings.coreformoptions.inflabel.label')

        def tableRendered = tb.tabulator(id: "optionsTable", name: "optionsTable", contextMenu: "true", boxed: "true",
                                                         errlabel: errlabel, inflabel: inflabel, toastid: "options_toast",
                                                         data: dataUrl, update: updateUrl) {

            tb.menuBar() {
                tb.menu(label: addlabel, action: createUrl, type: "add") +
                tb.menu(label: remlabel, action: deleteUrl, type: "remove")
            } +
            tb.column(name: "columnName", label: "${message(code: 'settings.coreformoptions.columnname.label')}") +
            tb.column(name: "ordinal", label: "${message(code: 'settings.coreformoptions.ordinal.label')}") +
            tb.column(name: "optionValue", label: "${message(code: 'settings.coreformoptions.optionValue.label')}", hzalign: "left", editor: "input") +
            tb.column(name: "optionLabel", label: "${message(code: 'settings.coreformoptions.optionLabel.label')}", hzalign: "left", editor: "input") +
            tb.column(name: "optionLabelCode", label: "${message(code: 'settings.coreformoptions.messageCode.label')}", hzalign: "left", editor: "input")
        }

        render tableRendered
    }

    def updateCustomOptions = {
        //receives a json
        def data = request.JSON

        //println "params: ${data}"
        //println "id = ${data.id}"
        //println "column = ${data.column}"
        //println "value = ${data.value}"

        def result = coreFormColumnOptionsService.updateOptions(data.column, data.id, data.value)

        render result as JSON
    }

    def fetchOptionsList = {
        def list = coreFormColumnOptionsService.getOptions(params.columnCode)

        //println "testing jsonx - ${params.columnCode}"

        render list as JSON
    }

    def createCustomOptions = {
        def data = request.JSON
        def id = data.id

        def newOption = coreFormColumnOptionsService.createCustomOptions(id)

        render newOption as JSON
    }

    def deleteCustomOptions = {
        def data = request.JSON
        def id = data.id

        def result = coreFormColumnOptionsService.deleteCustomOptions(id)

        render result as JSON
    }

    def createDefaultOptions = {
        def columnForm = params.columnForm
        def columnCode = params.columnCode


        //println "${columnCode}"

        if (columnCode != null) {
            coreFormColumnOptionsService.createDefaultOptionsFor(columnCode)

        }

        flash.message = message(code: 'settings.coreformoptions.dataintegrity.info.label')

        render view: "customOptions", model: [forms: coreFormColumnOptionsService.getCustomOptionsForms(), selectedForm : columnForm, selectedColumn: columnCode]
    }
}
