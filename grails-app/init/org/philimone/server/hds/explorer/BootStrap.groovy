package org.philimone.server.hds.explorer

import org.philimone.hds.explorer.authentication.Role
import org.philimone.hds.explorer.authentication.SecurityMap
import org.philimone.hds.explorer.authentication.User
import org.philimone.hds.explorer.authentication.UserRole
import org.philimone.hds.explorer.io.SystemPath

class BootStrap {

    def generalUtilitiesService
    def userService

    def init = { servletContext ->

        createDirectories(servletContext)
        configSecurityMap()
        defaultAppUser()
        insertDefaults()

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

    def configSecurityMap(){
        def svc = generalUtilitiesService

        if (Role.count() == 0){
            new Role(name: Role.LABEL_ADMINISTRATOR, authority: Role.ROLE_ADMINISTRATOR).save(flush: true)
            new Role(name: Role.LABEL_DATA_MANAGER, authority: Role.ROLE_DATA_MANAGER).save(flush: true)
            new Role(name: Role.LABEL_FIELD_WORKER, authority: Role.ROLE_FIELD_WORKER).save(flush: true)
        }

        if (true){

            SecurityMap.list().each{
                it.delete(flush: true)
            }

            for (String url in [
                    '/', '/**/favicon.ico',
                    '/**/js/**', '/**/css/**', '/**/images/**',
                    '/login', '/login.*', '/login/*', '/index', '/index.gsp',
                    '/logout', '/logout.*', '/logout/*', '/error**', '/shutdown',

                    '/**/assets/**',
                    '/**/images/**',
                    '/**/javascripts/**',
                    '/**/stylesheets/**'
            ]) {
                new SecurityMap(url: url, configAttribute: 'permitAll').save()
            }

            //new SecurityMap(url: "/index", configAttribute: "IS_AUTHENTICATED_FULLY").save(flush: true)

            new SecurityMap(url: "/user/*/**", configAttribute: "${Role.ROLE_ADMINISTRATOR}").save(flush: true)


            //new SecurityMap(url: "/exportFiles/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/generalUtilities/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/importData/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/export/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/updates/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            //new SecurityMap(url: "/household/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/member/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            //new SecurityMap(url: "/settings/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/reports/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/logReport/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            //rest api access
            //new SecurityMap(url: "/api/login", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/api/export/households/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/api/export/members/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/api/export/settings/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/api/export/users/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/api/export/trackinglists/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/api/export/stats/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)


        }
    }

    def defaultAppUser (){
        //Default ApplicationUser
        if (User.count()==0){
            User user = new User()
            user.firstName = "System"
            user.lastName = "Administrator"
            user.username = "admin";
            user.password = "admin";
            user.email = "paulphilimone@gmail.com"

            println ("admin user - hasErrors: "+user.hasErrors())
            println ("admin user - saving: " + userService.addUser(user))

            Role admin = Role.findByAuthority(Role.ROLE_ADMINISTRATOR)

            println "admin role: ${admin}"

            println "admin user role assign: ${UserRole.create(user, admin)}"
        }
    }

    def insertDefaults(){

    }
}
