package org.philimone.hds.explorer

import org.philimone.hds.explorer.authentication.Role
import org.philimone.hds.explorer.authentication.SecurityMap
import org.philimone.hds.explorer.authentication.User
import org.philimone.hds.explorer.authentication.UserRole
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.Codes
import org.philimone.hds.explorer.server.model.logs.LogGroup
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogStatus
import org.philimone.hds.explorer.server.model.main.MappingFormatType
import org.philimone.hds.explorer.server.model.main.StudyModule
import org.philimone.hds.explorer.server.model.settings.ApplicationParam

class BootStrap {

    def generalUtilitiesService
    def userService
    def applicationParamService
    def importDataFromOpenHDSService

    def init = { servletContext ->

        createDirectories(servletContext)
        configSecurityMap()
        defaultAppUser()
        insertDefaults()

        testApp()
    }

    def destroy = {

    }

    def createDirectories(def servletContext) {

        //def homeKey = isUnix() ? "HOME" : "HOMEPATH"

        def genDir = "${SystemPath.generatedFilesPath}"
        def binDir = "${SystemPath.binariesPath}"
        def logDir = "${SystemPath.logsPath}"
        def extDir = "${SystemPath.externalDocsPath}"


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
            new SecurityMap(url: "/api/login", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/households/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/members/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/settings/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/users/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/trackinglists/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/stats/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)


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
            user.email = "youremail@domain.net"

            println ("admin user - hasErrors: "+user.hasErrors())
            println ("admin user - saving: " + userService.addUser(user))

            Role admin = Role.findByAuthority(Role.ROLE_ADMINISTRATOR)

            println "admin role: ${admin}"

            println "admin user role assign: ${UserRole.create(user, admin)}"
        }
    }

    def insertDefaults(){
        def svc = generalUtilitiesService
        def aps = applicationParamService

        if (LogStatus.count() == 0){
            new LogStatus(name: LogStatus.STARTED).save(flush: true)
            new LogStatus(name: LogStatus.FINISHED).save(flush: true)
            new LogStatus(name: LogStatus.ERROR).save(flush: true)
            new LogStatus(name: LogStatus.NOT_STARTED).save(flush: true)
        }

        //Inserting Log Groups
        new LogGroup(groupId: Codes.GROUP_IMPORT_DATA_OPENHDS,   name: "OPENHDS",    description: "").save(flush: true)
        new LogGroup(groupId: Codes.GROUP_IMPORT_DATA_XLSHDS,    name: "XLSHDS",     description: "").save(flush: true)
        new LogGroup(groupId: Codes.GROUP_UPLOAD_TRACKING_LISTS, name: "TRACKLISTS", description: "").save(flush: true)
        new LogGroup(groupId: Codes.GROUP_GENERATE_FILES,        name: "GENFILES",   description: "").save(flush: true)


        //Inserting Log Reports

        /* Group Import from OpenHDS */
        new LogReport(
                reportId: Codes.REPORT_IMPORT_FROM_OPENHDS_FIELDWORKERS,
                group: LogGroup.findByGroupId(Codes.GROUP_IMPORT_DATA_OPENHDS),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.import.openhds.fieldworkers.label'
        ).save(flush: true)

        new LogReport(
                reportId: Codes.REPORT_IMPORT_FROM_OPENHDS_HOUSEHOLDS,
                group: LogGroup.findByGroupId(Codes.GROUP_IMPORT_DATA_OPENHDS),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.import.openhds.households.label'
        ).save(flush: true)

        new LogReport(
                reportId: Codes.REPORT_IMPORT_FROM_OPENHDS_INDIVIDUALS,
                group: LogGroup.findByGroupId(Codes.GROUP_IMPORT_DATA_OPENHDS),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.import.openhds.individuals.label'
        ).save(flush: true)

        new LogReport(
                reportId: Codes.REPORT_IMPORT_FROM_OPENHDS_RESIDENCIES,
                group: LogGroup.findByGroupId(Codes.GROUP_IMPORT_DATA_OPENHDS),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.import.openhds.residencies.label'
        ).save(flush: true)


        /* Group Import from HDS-XLS Files */
        new LogReport(
                reportId: Codes.REPORT_IMPORT_HDSXLS_HOUSEHOLDS,
                group: LogGroup.findByGroupId(Codes.GROUP_IMPORT_DATA_XLSHDS),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.import.hdsxls.households.label'
        ).save(flush: true)

        new LogReport(
                reportId: Codes.REPORT_IMPORT_HDSXLS_INDIVIDUALS,
                group: LogGroup.findByGroupId(Codes.GROUP_IMPORT_DATA_XLSHDS),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.import.hdsxls.individuals.label'
        ).save(flush: true)


        /* Group Uploading Tracking Lists */
        new LogReport(
                reportId: Codes.REPORT_UPLOAD_TRACKING_LISTS_BASIC,
                group: LogGroup.findByGroupId(Codes.GROUP_UPLOAD_TRACKING_LISTS),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.upload.trackinglists.basic.label'
        ).save(flush: true)

        new LogReport(
                reportId: Codes.REPORT_UPLOAD_TRACKING_LISTS_W_EXTRA_DATA,
                group: LogGroup.findByGroupId(Codes.GROUP_UPLOAD_TRACKING_LISTS),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.upload.trackinglists.with_extradata.label'
        ).save(flush: true)

        /* Group Generate Files */
        new LogReport(
                reportId: Codes.REPORT_GENERATE_USERS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(Codes.GROUP_GENERATE_FILES),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.export.users.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: Codes.REPORT_GENERATE_HOUSEHOLDS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(Codes.GROUP_GENERATE_FILES),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.export.households.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: Codes.REPORT_GENERATE_INDIVIDUALS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(Codes.GROUP_GENERATE_FILES),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.export.individuals.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: Codes.REPORT_GENERATE_SETTINGS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(Codes.GROUP_GENERATE_FILES),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.export.settings.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: Codes.REPORT_GENERATE_TRACKING_LISTS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(Codes.GROUP_GENERATE_FILES),
                status: LogStatus.findByName(LogStatus.NOT_STARTED),
                description: 'logreport.export.trakinglists.zip_xml_files.label'
        ).save(flush: true)


        //Inserting defaults system params

        def maxCols = svc.getConfigValue("hds.explorer.trackinglists.max_data_columns")
        println "code: ${maxCols}"

        aps.addApplicationParam(new ApplicationParam(name: Codes.PARAMS_TRACKING_LISTS_MAX_DATA_COLUMNS, type: "string", value: maxCols))
        aps.addApplicationParam(new ApplicationParam(name: Codes.PARAMS_HIERARCHY_LEVEL_1, type: "string", value: ""))
        aps.addApplicationParam(new ApplicationParam(name: Codes.PARAMS_HIERARCHY_LEVEL_2, type: "string", value: ""))
        aps.addApplicationParam(new ApplicationParam(name: Codes.PARAMS_HIERARCHY_LEVEL_3, type: "string", value: ""))
        aps.addApplicationParam(new ApplicationParam(name: Codes.PARAMS_HIERARCHY_LEVEL_4, type: "string", value: ""))
        aps.addApplicationParam(new ApplicationParam(name: Codes.PARAMS_HIERARCHY_LEVEL_5, type: "string", value: ""))
        aps.addApplicationParam(new ApplicationParam(name: Codes.PARAMS_HIERARCHY_LEVEL_6, type: "string", value: ""))
        aps.addApplicationParam(new ApplicationParam(name: Codes.PARAMS_HIERARCHY_LEVEL_7, type: "string", value: ""))
        aps.addApplicationParam(new ApplicationParam(name: Codes.PARAMS_HIERARCHY_LEVEL_8, type: "string", value: ""))

        //Inserting Default Mapping Formats
        new MappingFormatType(description: "Boolean [yes, no]", type: "Boolean", format:"yes,no").save(flush: true)
        new MappingFormatType(description: "Choices eg. [true=yes, false=no]", type: "Choices", format:"true=yes,false=no").save(flush: true)
        new MappingFormatType(description: "Date [Y-M-D]", type: "Date", format:"yyyy-MM-dd").save(flush: true)
        new MappingFormatType(description: "Date [D-M-Y]", type: "Date", format: "dd-MM-yyyy").save(flush: true)
        new MappingFormatType(description: "Date [M-D-Y]", type: "Date", format: "MM-dd-yyyy").save(flush: true)
        new MappingFormatType(description: "Date [Y-M-D H:M]", type: "Date", format: "yyyy-MM-dd HH:mm").save(flush: true)
        new MappingFormatType(description: "Date [D-M-Y H:M]", type: "Date", format: "dd-MM-yyyy HH:mm").save(flush: true)
        new MappingFormatType(description: "Date [M-D-Y H:M]", type: "Date", format: "MM-dd-yyyy HH:mm").save(flush: true)
        new MappingFormatType(description: "Date [Y-M-D H:M:S]", type: "Date", format: "yyyy-MM-dd HH:mm:ss").save(flush: true)
        new MappingFormatType(description: "Date [D-M-Y H:M:S]", type: "Date", format: "dd-MM-yyyy HH:mm:ss").save(flush: true)
        new MappingFormatType(description: "Date [M-D-Y H:M:S]", type: "Date", format: "MM-dd-yyyy HH:mm:ss").save(flush: true)


        //Insert Default StudyModule
        new StudyModule(code: StudyModule.DSS_SURVEY_MODULE, name: "DSS Surveillance", description: "The default module of HDS-Explorer that allows you to navigate the system").save(flush: true)


    }

    def testApp(){

        importDataFromOpenHDSService.importFieldWorkersFromOpenHDS(Codes.REPORT_IMPORT_FROM_OPENHDS_FIELDWORKERS)

        assert 1==0
    }
}
