package org.philimone.hds.explorer

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "dashboard", action: "index")
        "/login/$action?"(controller: "login")
        "/logout/$action?"(controller: "logout")
        "/logout/index"(controller: "logout")

        "500"(view:'/error')
        "404"(view:'/notFound')

        //REST Api access
        "/api/login"(controller: "generalUtilities", action: "loginTest")

        "/api/export/households"(controller:    "syncFiles", action: "households")
        "/api/export/members"(controller:   "syncFiles", action: "members")
        "/api/export/settings"(controller:      "syncFiles", action: "settings")
        "/api/export/forms"(controller:      "syncFiles", action: "forms")
        "/api/export/coreforms"(controller:      "syncFiles", action: "coreforms")
        "/api/export/modules"(controller:      "syncFiles", action: "modules")
        "/api/export/users"(controller:         "syncFiles", action: "users")
        "/api/export/trackinglists"(controller: "syncFiles", action: "trackinglists")
        "/api/export/stats"(controller:         "syncFiles", action: "stats")
        "/api/export/params"(controller:         "syncFiles", action: "appParams")
        "/api/export/regions"(controller:         "syncFiles", action: "regions")

        "/api/export/datasets"(controller:         "syncFiles", action: "datasets")
        "/api/export/dataset/${id}"(controller:         "syncFiles", action: "dataset", id: "${id}")

        "/api/export/residencies"(controller:         "syncFiles", action: "residencies")
        "/api/export/hrelationships"(controller:         "syncFiles", action: "headRelationships")
        "/api/export/mrelationships"(controller:         "syncFiles", action: "maritalRelationships")
        "/api/export/rounds"(controller:         "syncFiles", action: "rounds")
        "/api/export/visits"(controller:         "syncFiles", action: "visits")
        "/api/export/pregnancies"(controller:         "syncFiles", action: "pregnancyRegistrations")
        "/api/export/deaths"(controller:         "syncFiles", action: "deaths")


        "/api/export/households/zip"(controller:    "syncFiles", action: "householdsZip")
        "/api/export/members/zip"(controller:   "syncFiles", action: "membersZip")
        "/api/export/settings/zip"(controller:      "syncFiles", action: "settingsZip")
        "/api/export/forms/zip"(controller:      "syncFiles", action: "formsZip")
        "/api/export/coreforms/zip"(controller:      "syncFiles", action: "coreformsZip")
        "/api/export/modules/zip"(controller:      "syncFiles", action: "modulesZip")
        "/api/export/users/zip"(controller:         "syncFiles", action: "usersZip")
        "/api/export/trackinglists/zip"(controller: "syncFiles", action: "trackinglistsZip")
        "/api/export/stats/zip"(controller:         "syncFiles", action: "statsZip")
        "/api/export/params/zip"(controller:         "syncFiles", action: "appParamsZip")
        "/api/export/regions/zip"(controller:         "syncFiles", action: "regionsZip")

        "/api/export/datasets/zip"(controller:         "syncFiles", action: "datasetsZip")
        "/api/export/dataset/zip/${id}"(controller:         "syncFiles", action: "datasetZip", id: "${id}")

        "/api/export/residencies/zip"(controller:         "syncFiles", action: "residenciesZip")
        "/api/export/hrelationships/zip"(controller:         "syncFiles", action: "headRelationshipsZip")
        "/api/export/mrelationships/zip"(controller:         "syncFiles", action: "maritalRelationshipsZip")
        "/api/export/rounds/zip"(controller:         "syncFiles", action: "roundsZip")
        "/api/export/visits/zip"(controller:         "syncFiles", action: "visitsZip")
        "/api/export/pregnancies/zip"(controller:         "syncFiles", action: "pregnancyRegistrationsZip")
        "/api/export/deaths/zip"(controller:         "syncFiles", action: "deathsZip")

        "/api/export/sync-report"(controller: "syncFiles", action: "syncFilesReport")

        //API
        "/api/import/prehouseholds"(controller: "RawImportApi", action: "prehouseholds")
        "/api/import/households"(controller: "RawImportApi", action: "households")
        "/api/import/regions"(controller: "RawImportApi", action: "regions")
        "/api/import/members"(controller: "RawImportApi", action: "members")
        "/api/import/visits"(controller: "RawImportApi", action: "visits")
        "/api/import/memberenus"(controller: "RawImportApi", action: "memberenus")
        "/api/import/externalinmigrations"(controller: "RawImportApi", action: "externalinmigrations")
        "/api/import/inmigrations"(controller: "RawImportApi", action: "inmigrations")
        "/api/import/outmigrations"(controller: "RawImportApi", action: "outmigrations")
        "/api/import/headrelationships"(controller: "RawImportApi", action: "headrelationships")
        "/api/import/maritalrelationships"(controller: "RawImportApi", action: "maritalrelationships")
        "/api/import/pregnancyregistrations"(controller: "RawImportApi", action: "pregnancyregistrations")
        "/api/import/pregnancyoutcomes"(controller: "RawImportApi", action: "pregnancyoutcomes")
        "/api/import/deaths"(controller: "RawImportApi", action: "deaths")
        "/api/import/changeheads"(controller: "RawImportApi", action: "changeheads")
        "/api/import/inconmpletevisits"(controller: "RawImportApi", action: "inconmpletevisits")
        "/api/import/editregions"(controller: "RawImportApi", action: "editregions")
        "/api/import/edithouseholds"(controller: "RawImportApi", action: "edithouseholds")
        "/api/import/editmembers"(controller: "RawImportApi", action: "editmembers")
        "/api/trackinglist/add"(controller: "trackingList", action: "addxls")  //workds for create and update
        "/api/trackinglist/get/${id}"(controller: "trackingList", action: "get", id: "${id}") //working
        "/api/trackinglist/getsample"(controller: "trackingList", action: "downloadSampleXLS") //working
        "/api/trackinglist/gettemplate"(controller: "trackingList", action: "downloadTemplateXLS") //working


        "/download/apk"(controller: "syncFiles", action:"downloadAndroidApk")

    }
}
