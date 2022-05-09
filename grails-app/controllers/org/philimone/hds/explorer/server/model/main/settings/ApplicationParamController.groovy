package org.philimone.hds.explorer.server.model.main.settings

import org.philimone.hds.explorer.server.model.settings.ApplicationParam

class ApplicationParamController {

    def hierarchyLevels = {
        def appParams = ApplicationParam.findAllByNameLike("hierarchy%")

        [applicationParamList: appParams, applicationParamCount: appParams.size()]
    }

    def updateLevels = {

        def hierarchies = ["hierarchy1":false, "hierarchy2":false, "hierarchy3":false, "hierarchy4":false, "hierarchy5":false, "hierarchy6":false, "hierarchy7":false, "hierarchy8":false, "hierarchy9":false, "hierarchy10":false]
        def hierarchiesValue = ["hierarchy1":"", "hierarchy2":"", "hierarchy3":"", "hierarchy4":"", "hierarchy5":"", "hierarchy6":"", "hierarchy7":"", "hierarchy8":"", "hierarchy9":"", "hierarchy10":""]
        
        def mape = params.list("enabled").get(0)

        mape.each { e ->
            //println "enabledx: ${e}"

            if (e.key.startsWith("hierarchy") && e.value.equals("on")){
                def level = e.key
                def value = params.get("value")?.get(level)
                
                hierarchies.put(level, true)
                hierarchiesValue.put(level, value)


                //println "hierarchy: (${e.key}) -> ${value}"
            }
        }

        hierarchiesValue.each { key, value ->
            def param = ApplicationParam.findByName(key)
            param.value = value==null || value.isEmpty() ? null : value
            param.save(flush:true)
        }

        redirect action:"hierarchyLevels"
    }
}
