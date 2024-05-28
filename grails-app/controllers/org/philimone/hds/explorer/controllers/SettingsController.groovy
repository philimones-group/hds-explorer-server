package org.philimone.hds.explorer.controllers

import grails.converters.JSON
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.json.JLanguage
import org.philimone.hds.explorer.server.model.main.CoreFormColumnOptions
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorFactory
import org.philimone.hds.explorer.taglib.DatatablesUITagLib

class SettingsController {

    def generalUtilitiesService
    def applicationParamService
    def codeGeneratorService
    def coreFormColumnOptionsService
    def settingsService
    def dt = new DatatablesUITagLib()

    def parameters() {
        List<JLanguage> languages = generalUtilitiesService.getSystemLanguages()
        JLanguage currentLanguage = generalUtilitiesService.getCurrentSystemLanguage()
        def codeGenIncrementalRules = settingsService.codeGeneratorsIncrementalRules

        [languages: languages, selectedLanguage: currentLanguage.language, codeGenerators: Codes.SYSTEM_ALL_CODE_GENERATORS, codeGeneratorsRules: codeGenIncrementalRules, selectedCodeGenerator: Codes.SYSTEM_CODE_GENERATOR, selectedCodeGeneratorIncRule: Codes.SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE, errorMessages: new ArrayList<String>()]
    }

    def updateParameters = {

        def selectedLanguage = params.systemLanguage
        def selectedCodeGenerator = params.codeGenerator
        def selectedCodeGeneratorIncRule = params.codeGeneratorIncRule
        def codeGenIncrementalRules = settingsService.codeGeneratorsIncrementalRules
        def errorMessages = new ArrayList<String>()

        println "selected ${selectedLanguage}"
        println "selected ${selectedCodeGenerator}"
        println "selected ${selectedCodeGeneratorIncRule}"

        try {

            //Update System Language
            if (selectedLanguage != null) {
                Codes.SYSTEM_LANGUAGE = selectedLanguage
                applicationParamService.updateApplicationParam(Codes.PARAMS_SYSTEM_LANGUAGE, selectedLanguage)
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

            flash.message = message(code: 'settings.parameters.update.success.label')

        } catch(Exception ex) {
            ex.printStackTrace()
            errorMessages << message(code: 'settings.parameters.update.error.label') + ": ${ex.getMessage()}"
        }

        List<JLanguage> languages = generalUtilitiesService.getSystemLanguages()
        JLanguage currentLanguage = generalUtilitiesService.getCurrentSystemLanguage()

        println "${errorMessages}"

        render view: "parameters", model: [languages: languages, selectedLanguage: currentLanguage.language, errorMessages: errorMessages,
                                              codeGenerators: Codes.SYSTEM_ALL_CODE_GENERATORS, codeGeneratorsRules: codeGenIncrementalRules, selectedCodeGenerator: Codes.SYSTEM_CODE_GENERATOR, selectedCodeGeneratorIncRule: Codes.SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE]
    }

    def customOptions = {

        flash.message = message(code: 'settings.coreformoptions.dataintegrity.info.label')
        [forms: coreFormColumnOptionsService.getCustomOptionsForms()]
    }

    def getCustomOptionsColumns = {
        def columns = coreFormColumnOptionsService.getCustomOptionsColumns(params.name)

        render g.select(id: "column", name: "column", from: columns)
    }

    def renderCustomOptionsTable = {
        //params.name

        def dataUrl = createLink(controller: 'settings', action: 'fetchOptionsList', id: params.name)
        def updateUrl = createLink(controller: 'settings', action: 'updateCustomOptions')
        def createUrl = createLink(controller: 'settings', action: 'createCustomOptions')
        def deleteUrl = createLink(controller: 'settings', action: 'deleteCustomOptions')
        def addlabel = message(code: 'settings.coreformoptions.addlabel.label')
        def remlabel = message(code: 'settings.coreformoptions.remlabel.label')
        def errlabel = message(code: 'settings.coreformoptions.errlabel.label')
        def inflabel = message(code: 'settings.coreformoptions.inflabel.label')

        def tableRendered = dt.tabulator(id: "optionsTable", name: "optionsTable", addlabel: addlabel, remlabel: remlabel,
                                                         errlabel: errlabel, inflabel: inflabel, toastid: "options_toast",
                                                         data: dataUrl, update: updateUrl, createrow: createUrl, deleterow: deleteUrl, boxed: "true") {
            dt.column(name: "columnName", label: "${message(code: 'settings.coreformoptions.columnname.label')}") +
            dt.column(name: "ordinal", label: "${message(code: 'settings.coreformoptions.ordinal.label')}") +
            dt.column(name: "optionValue", label: "${message(code: 'settings.coreformoptions.optionValue.label')}", hzalign: "left", editor: "input") +
            dt.column(name: "optionLabel", label: "${message(code: 'settings.coreformoptions.optionLabel.label')}", hzalign: "left", editor: "input") +
            dt.column(name: "optionLabelCode", label: "${message(code: 'settings.coreformoptions.messageCode.label')}", hzalign: "left", editor: "input")
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
        def list = coreFormColumnOptionsService.getOptions(params.id)

        //println "testing jsonx - ${params.id}"

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
}
