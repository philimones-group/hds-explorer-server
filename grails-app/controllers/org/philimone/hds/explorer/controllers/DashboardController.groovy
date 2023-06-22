package org.philimone.hds.explorer.controllers

import grails.converters.JSON

class DashboardController {

    def dashboardService
    def localeResolver

    def index() { }

    def totals = {
        def totals = dashboardService.retrieveTotals()
        [totals: totals]
    }

    def populationPyramid = {
        def pyramidBars = dashboardService.retrievePopulationPyramid()

        //println "py : ${groovy.json.JsonOutput.toJson(pyramidBars)}"

        [pyramidBars: pyramidBars]
    }

    def fieldworkerStatus = {
        def statusList = dashboardService.retrieveFieldworkerStatus()

        [statusList: statusList]
    }

    def coreFormStatus = {
        def coreFormStatusMap = dashboardService.retrieveCoreFormStatus()

        [coreFormStatusMap : coreFormStatusMap]
    }

    def language = {
        Locale currentLocale = localeResolver.resolveLocale(request)
        String language = currentLocale.language

        render "{\"lang\" : \"${language}\"}"
    }

    def i18nmessages = {
        //Locale currentLocale = localeResolver.resolveLocale(request)
        //def messages = messageSource.getAllMessages(currentLocale)
        //render messages as JSON

        def messages = [:]
        def codes = ["dashboard.greeting",
                     "dashboard.totalHouseholds",
                     "dashboard.totalIndividuals",
                     "dashboard.totalResidents",
                     "dashboard.totalOutmigrated",
                     "dashboard.totalDeaths",
                     "dashboard.dataCollectionStatus",
                     "dashboard.householdRegistration",
                     "dashboard.householdVisit",
                     "dashboard.memberEnumeration",
                     "dashboard.maritalRelationship",
                     "dashboard.internalInMigration",
                     "dashboard.externalInMigration",
                     "dashboard.outmigration",
                     "dashboard.deathRegistration",
                     "dashboard.pregnancyRegistration",
                     "dashboard.birthRegistration",
                     "dashboard.changeHeadHousehold",
                     "dashboard.incompleteVisit",
                     "dashboard.fieldworkersPerformance",
                     "dashboard.collectionStatus",
                     "dashboard.populationByAgeAndGender",
                     "dashboard.collapse",
                     "dashboard.expand",
                     "dashboard.males",
                     "dashboard.females",
                     "dashboard.synced",
                     "dashboard.pending",
                     "dashboard.error",
                     "dashboard.table.id",
                     "dashboard.table.fieldworker",
                     "dashboard.table.collected",
                     "dashboard.table.synced",
                     "dashboard.table.pending",
                     "dashboard.table.error",
                     "dashboard.table.see",
                     "dashboard.table.rows"]

        codes.each { code ->
            messages.put(code, message(code: "${code}"))
        }

        render messages as JSON
    }
}
