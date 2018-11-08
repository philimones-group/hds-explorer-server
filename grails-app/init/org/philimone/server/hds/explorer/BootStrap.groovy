package org.philimone.server.hds.explorer

import org.philimone.hds.explorer.io.SystemPath

class BootStrap {

    def init = { servletContext ->

        createDirectories(servletContext)

    }

    def destroy = {

    }

    def createDirectories(def servletContext) {

        //def homeKey = isUnix() ? "HOME" : "HOMEPATH"

        def genDir = "${SystemPath.GEN_PATH}"
        def binDir = "${SystemPath.BIN_PATH}"
        def logDir = "${SystemPath.LOG_PATH}"
        def extDir = "${SystemPath.EXT_PATH}"


        println "Homepath: ${SystemPath.HOME_PATH}"

        //create app dir
        println "Creating dir "+genDir
        println "created: " + new File(genDir).mkdirs()

        //create bin dir
        println "Creating dir "+binDir
        println "created: " + new File(binDir).mkdirs()

        //create log dir
        println "Creating dir "+logDir
        println "created: " + new File(logDir).mkdirs()

        //create resources dir
        println "Creating dir "+extDir
        println "created: " + new File(extDir).mkdirs()
    }
}
