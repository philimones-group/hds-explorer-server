package org.philimone.hds.explorer

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')

        //REST Api access
        "/api/login"(controller: "generalUtilities", action: "loginTest")

        "/api/export/households"(controller:    "exportFiles", action: "households")
        "/api/export/individuals"(controller:   "exportFiles", action: "individuals")
        "/api/export/settings"(controller:      "exportFiles", action: "settings")
        "/api/export/users"(controller:         "exportFiles", action: "users")
        "/api/export/trackinglists"(controller: "exportFiles", action: "trackinglists")
        "/api/export/stats"(controller:         "exportFiles", action: "stats")

        "/api/export/households/zip"(controller:    "exportFiles", action: "householdsZip")
        "/api/export/individuals/zip"(controller:   "exportFiles", action: "individualsZip")
        "/api/export/settings/zip"(controller:      "exportFiles", action: "settingsZip")
        "/api/export/users/zip"(controller:         "exportFiles", action: "usersZip")
        "/api/export/trackinglists/zip"(controller: "exportFiles", action: "trackinglistsZip")
        "/api/export/stats/zip"(controller:         "exportFiles", action: "statsZip")


    }
}
