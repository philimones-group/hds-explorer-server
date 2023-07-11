package org.philimone.hds.explorer.controllers

import grails.converters.JSON
import net.betainteractive.utilities.StringUtil
import org.apache.xmlbeans.impl.xb.ltgfmt.Code
import org.hibernate.boot.model.source.spi.ColumnsAndFormulasSourceContainer
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.json.JLanguage
import org.philimone.hds.explorer.server.model.main.CoreFormColumnOptions
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorFactory

class SettingsController {

    def generalUtilitiesService
    def applicationParamService
    def codeGeneratorService

    def parameters() {
        List<JLanguage> languages = generalUtilitiesService.getSystemLanguages()
        JLanguage currentLanguage = generalUtilitiesService.getCurrentSystemLanguage()

        [languages: languages, selectedLanguage: currentLanguage.language, codeGenerators: Codes.SYSTEM_ALL_CODE_GENERATORS, selectedCodeGenerator: Codes.SYSTEM_CODE_GENERATOR, errorMessages: new ArrayList<String>()]
    }

    def updateParameters = {

        def selectedLanguage = params.systemLanguage
        def selectedCodeGenerator = params.codeGenerator
        def errorMessages = new ArrayList<String>()

        println "selected ${selectedLanguage}"
        println "selected ${selectedCodeGenerator}"

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

            flash.message = message(code: 'settings.parameters.update.success.label')

        } catch(Exception ex) {
            ex.printStackTrace()
            errorMessages << message(code: 'settings.parameters.update.error.label') + ": ${ex.getMessage()}"
        }

        List<JLanguage> languages = generalUtilitiesService.getSystemLanguages()
        JLanguage currentLanguage = generalUtilitiesService.getCurrentSystemLanguage()

        println "${errorMessages}"

        render view: "parameters", model: [languages: languages, selectedLanguage: currentLanguage.language, errorMessages: errorMessages,
                                              codeGenerators: Codes.SYSTEM_ALL_CODE_GENERATORS, selectedCodeGenerator: Codes.SYSTEM_CODE_GENERATOR]
    }

}
