package org.philimone.hds.explorer.server.model.main.settings

import org.philimone.hds.explorer.server.model.settings.ApplicationParam

class ApplicationParamController {

    def applicationParamService

    def hierarchyLevels = {
        def appParams = applicationParamService.getHierarchyLevelsParameters()

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

    def updateHierarchiesTable = {
        def value = params.name as String

        //enabled.hierarchy1:true.District
        //head.hierachy1:true - hierachy1.head = true/false

        println "params: ${value}"

        if (value != null) {

            def spt = value.split(":")

            if (spt[0] != null){
                def var = spt[0]
                def val = spt[1]

                if (var.startsWith("enabled.")) {

                    def spt2 = val.split("\\.")

                    def hierarchyId = var.replace("enabled.", "")
                    def hierarchyEnabled = "true".equals(spt2[0])
                    def hierarchyLabel = spt2[1]
                    def param = ApplicationParam.findByName(hierarchyId)

                    param.value = hierarchyEnabled ? hierarchyLabel : null
                    param.save(flush: true)

                    println "param.errors: ${param.errors}"


                } else if (var.startsWith("head.")) {
                    //handles head
                    def spt2 = val.split("\\.")
                    def enabled = "true".equals(spt2[0])
                    def paramName = var.replace("head.", "") + ".head"
                    def param = ApplicationParam.findByName(paramName)

                    param.value = enabled ? "true" : null
                    param.save(flush: true)

                    println "regionhead.errors: ${param.errors}"

                }

            }




        }

        render "OK"
    }
}
