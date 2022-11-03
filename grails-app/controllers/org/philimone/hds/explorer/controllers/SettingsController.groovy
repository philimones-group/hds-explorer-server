package org.philimone.hds.explorer.controllers

import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.json.JLanguage
import org.philimone.hds.explorer.server.model.settings.Codes

class SettingsController {

    def generalUtilitiesService
    def applicationParamService

    def parameters() {
        List<JLanguage> languages = generalUtilitiesService.getSystemLanguages()
        JLanguage currentLanguage = generalUtilitiesService.getCurrentSystemLanguage()

        [languages: languages, selectedLanguage: currentLanguage.language]
    }

    def updateParameters = {

        def selectedLanguage = params.systemLanguage
        def errorMessage = null as String

        println "selected ${selectedLanguage}"

        try {

            if (selectedLanguage != null) {
                Codes.SYSTEM_LANGUAGE = selectedLanguage
                applicationParamService.updateApplicationParam(Codes.PARAMS_SYSTEM_LANGUAGE, selectedLanguage)
                flash.message = message(code: 'settings.parameters.update.success.label')
            } else {
                errorMessage = message(code: 'settings.parameters.update.error.label') + ": " + message(code: 'settings.parameters.update.error.null.label')
            }

        } catch(Exception ex) {
            ex.printStackTrace()
            errorMessage = message(code: 'settings.parameters.update.error.label') + ": ${ex.getMessage()}"
        }

        List<JLanguage> languages = generalUtilitiesService.getSystemLanguages()
        JLanguage currentLanguage = generalUtilitiesService.getCurrentSystemLanguage()

        println "${errorMessage}"

        redirect action:"parameters", model: [languages: languages, selectedLanguage: currentLanguage.language, errorMessage: errorMessage]
    }
}
