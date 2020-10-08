package org.philimone.hds.explorer

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
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

        "/api/export/households/zip"(controller:    "syncFiles", action: "householdsZip")
        "/api/export/members/zip"(controller:   "syncFiles", action: "membersZip")
        "/api/export/settings/zip"(controller:      "syncFiles", action: "settingsZip")
        "/api/export/forms/zip"(controller:      "syncFiles", action: "formsZip")
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

        "/api/export/sync-report/${id}"(controller: "syncFiles", action: "syncFilesReport", id: "${id}")


    }
}
