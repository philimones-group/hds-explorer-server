package org.philimone.hds.explorer.controllers

class GeneralUtilitiesController {

    def generalUtilitiesService

    def loginTest = {
        render "${generalUtilitiesService.getAppVersion()}" //"OK"
    }
}
