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

        "/api/export/households"(controller:    "exportFiles", action: "households")
        "/api/export/members"(controller:   "exportFiles", action: "members")
        "/api/export/settings"(controller:      "exportFiles", action: "settings")
        "/api/export/forms"(controller:      "exportFiles", action: "forms")
        "/api/export/modules"(controller:      "exportFiles", action: "modules")
        "/api/export/users"(controller:         "exportFiles", action: "users")
        "/api/export/trackinglists"(controller: "exportFiles", action: "trackinglists")
        "/api/export/stats"(controller:         "exportFiles", action: "stats")
        "/api/export/params"(controller:         "exportFiles", action: "appParams")
        "/api/export/regions"(controller:         "exportFiles", action: "regions")

        "/api/export/datasets"(controller:         "exportFiles", action: "datasets")
        "/api/export/dataset/${id}"(controller:         "exportFiles", action: "dataset", id: "${id}")

        "/api/export/households/zip"(controller:    "exportFiles", action: "householdsZip")
        "/api/export/members/zip"(controller:   "exportFiles", action: "membersZip")
        "/api/export/settings/zip"(controller:      "exportFiles", action: "settingsZip")
        "/api/export/forms/zip"(controller:      "exportFiles", action: "formsZip")
        "/api/export/modules/zip"(controller:      "exportFiles", action: "modulesZip")
        "/api/export/users/zip"(controller:         "exportFiles", action: "usersZip")
        "/api/export/trackinglists/zip"(controller: "exportFiles", action: "trackinglistsZip")
        "/api/export/stats/zip"(controller:         "exportFiles", action: "statsZip")
        "/api/export/params/zip"(controller:         "exportFiles", action: "appParamsZip")
        "/api/export/regions/zip"(controller:         "exportFiles", action: "regionsZip")

        "/api/export/datasets/zip"(controller:         "exportFiles", action: "datasetsZip")
        "/api/export/dataset/zip/${id}"(controller:         "exportFiles", action: "datasetZip", id: "${id}")


    }
}
