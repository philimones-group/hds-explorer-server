package org.philimone.hds.explorer

import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.Role
import org.philimone.hds.explorer.server.model.authentication.SecurityMap
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.CoreForm
import org.philimone.hds.explorer.server.model.enums.EducationType
import org.philimone.hds.explorer.server.model.enums.Gender
import org.philimone.hds.explorer.server.model.enums.MaritalStatus
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.enums.ReligionType
import org.philimone.hds.explorer.server.model.enums.settings.LogGroupCode
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.model.logs.LogGroup
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.main.CoreFormColumnMap
import org.philimone.hds.explorer.server.model.main.CoreFormColumnOptions
import org.philimone.hds.explorer.server.model.main.CoreFormExtension
import org.philimone.hds.explorer.server.model.main.MappingFormatType
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.Module
import org.philimone.hds.explorer.server.model.enums.SyncEntity
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.model.settings.SyncFilesReport
import org.philimone.hds.explorer.server.settings.generator.CodeGenerator
import org.philimone.hds.explorer.server.settings.generator.CodeGeneratorFactory
import org.reflections.Reflections

class BootStrap {

    def generalUtilitiesService
    def userService
    def applicationParamService
    def codeGeneratorService
    def coreFormExtensionService

    def init = { servletContext ->

        configSecurityMap()
        insertDefaults()
        retrieveAndPopulateStaticConstants()
        createDirectories(servletContext)
        setupCodeGenerators()
        defaultAppUser()

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
        def apkDir = "${SystemPath.apksPath}"


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

        //create apks dir
        println "Creating dir "+apkDir
        println "created: " + new File(apkDir).mkdirs()
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
                    /*'/',*/ '/**/favicon.ico',
                    '/**/js/**', '/**/css/**', '/**/images/**',
                    '/login', '/login.*', '/login/*', '/index', '/index.gsp', '/index.html',
                    '/logout', '/logout.*', '/logout/*', '/error**', '/shutdown',

                    '/**/assets/**',
                    '/**/images/**',
                    '/**/javascripts/**',
                    '/**/stylesheets/**'
            ]) {
                new SecurityMap(url: url, configAttribute: 'permitAll').save()
            }

            new SecurityMap(url: "/", configAttribute: "IS_AUTHENTICATED_FULLY").save(flush: true)

            new SecurityMap(url: "/user/*/**", configAttribute: "${Role.ROLE_ADMINISTRATOR}").save(flush: true)


            new SecurityMap(url: "/syncFiles/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/syncFilesReport/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/generalUtilities/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/coreFormExtension/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/importOpenHDS/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/eventSync/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/module/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/form/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/formMapping/*/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/dataset/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/trackingList/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            new SecurityMap(url: "/applicationParam/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            new SecurityMap(url: "/rawDomain/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            new SecurityMap(url: "/round/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/region/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/household/index", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/household/show/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/household/householdList", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/member/index", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/member/show/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/member/showHousehold/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/member/memberList", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            new SecurityMap(url: "/settings/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/dashboard/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/reports/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/logReport/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            //rest api access
            new SecurityMap(url: "/api/login", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/households/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/members/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/settings/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/modules/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/forms/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/coreforms/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/coreformsoptions/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/users/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/trackinglists/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/residencies/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/hrelationships/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/mrelationships/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/rounds/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/visits/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/pregnancies/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/deaths/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/stats/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)

            new SecurityMap(url: "/api/export/params/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/regions/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)

            new SecurityMap(url: "/api/export/datasets/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/export/dataset/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)

            new SecurityMap(url: "/api/export/sync-report/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)

            //File downloads
            new SecurityMap(url: "/download/apk", configAttribute: "permitAll").save(flush: true)
            new SecurityMap(url: "/syncFiles/downloadAndroidApk", configAttribute: "permitAll").save(flush: true)


            new SecurityMap(url: "/api/import/regions", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/prehouseholds", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/households", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/members", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/visits", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/memberenus", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/externalinmigrations", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/inmigrations", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/outmigrations", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/headrelationships", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/maritalrelationships", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/pregnancyregistrations", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/pregnancyoutcomes", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/deaths", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/changeheads", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/incompletevisits", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/editregions", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/edithouseholds", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/import/editmembers", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
            new SecurityMap(url: "/api/trackinglist/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER},${Role.ROLE_FIELD_WORKER}").save(flush: true)
        }
    }

    def defaultAppUser (){

        //Insert Default Module
        if (Module.count()==0) {
            new Module(code: codeGeneratorService.generateModuleCode(null), name: Module.DSS_SURVEY_MODULE, description: "The default module of HDS-Explorer that allows you to navigate the system").save(flush: true)
        }


        //Default ApplicationUser
        if (User.count()==0){



            Role admin = Role.findByAuthority(Role.ROLE_ADMINISTRATOR)
            def modules = Module.list()

            User user = new User()

            user.firstName = "System"
            user.lastName = "Administrator"
            user.username = "admin";
            user.password = "admin";
            user.email = "youremail@domain.net"

            println ("admin user - hasErrors: "+user.hasErrors())
            println ("admin user - saving: " + userService.addUser(user, [admin], modules))
            println "admin role: ${admin}"
            //println "admin user role assign: ${UserRole.create(user, [admin])}"
        }
    }

    def insertDefaults(){
        def svc = generalUtilitiesService
        def aps = applicationParamService

        //Inserting Log Groups
        new LogGroup(groupId: LogGroupCode.GROUP_IMPORT_DATA_OPENHDS,       name: "OPENHDS",    description: "").save(flush: true)
        new LogGroup(groupId: LogGroupCode.GROUP_IMPORT_DATA_XLSHDS,        name: "XLSHDS",     description: "").save(flush: true)
        new LogGroup(groupId: LogGroupCode.GROUP_UPLOAD_TRACKING_LISTS,     name: "TRACKLISTS", description: "").save(flush: true)
        new LogGroup(groupId: LogGroupCode.GROUP_GENERATE_FILES,            name: "GENFILES",   description: "").save(flush: true)
        new LogGroup(groupId: LogGroupCode.GROUP_SYNC_DSS_DATA_FROM_CLIENT, name: "SYNCDSS",    description: "").save(flush: true)


        //Inserting Log Reports

        /* Group Import from OpenHDS */
        new LogReport(
                reportId: LogReportCode.REPORT_IMPORT_FROM_OPENHDS_FIELDWORKERS,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_IMPORT_DATA_OPENHDS),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.import.openhds.fieldworkers.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_IMPORT_FROM_OPENHDS_HOUSEHOLDS,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_IMPORT_DATA_OPENHDS),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.import.openhds.households.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_IMPORT_FROM_OPENHDS_INDIVIDUALS,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_IMPORT_DATA_OPENHDS),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.import.openhds.individuals.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_IMPORT_FROM_OPENHDS_RESIDENCIES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_IMPORT_DATA_OPENHDS),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.import.openhds.residencies.label'
        ).save(flush: true)


        /* Group Import from HDS-XLS Files */
        new LogReport(
                reportId: LogReportCode.REPORT_IMPORT_HDSXLS_HOUSEHOLDS,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_IMPORT_DATA_XLSHDS),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.import.hdsxls.households.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_IMPORT_HDSXLS_INDIVIDUALS,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_IMPORT_DATA_XLSHDS),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.import.hdsxls.individuals.label'
        ).save(flush: true)


        /* Group Uploading Tracking Lists */
        new LogReport(
                reportId: LogReportCode.REPORT_UPLOAD_TRACKING_LISTS_BASIC,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_UPLOAD_TRACKING_LISTS),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.upload.trackinglists.basic.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_UPLOAD_TRACKING_LISTS_W_EXTRA_DATA,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_UPLOAD_TRACKING_LISTS),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.upload.trackinglists.with_extradata.label'
        ).save(flush: true)

        /* Group Sync DSS Data */
        new LogReport(
                reportId: LogReportCode.REPORT_DSS_EVENTS_SYNC,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_SYNC_DSS_DATA_FROM_CLIENT),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.sync.syncdss.events.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_DSS_EVENTS_RESET_ERRORS,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_SYNC_DSS_DATA_FROM_CLIENT),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.sync.syncdss.reset.errors.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_DSS_EVENTS_EXECUTE_SYNC,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_SYNC_DSS_DATA_FROM_CLIENT),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.sync.syncdss.executecompiled.label'
        ).save(flush: true)

        /* Group Generate Files */
        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_SETTINGS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.settings.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_EXTERNAL_DATASETS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.extdatasets.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_TRACKING_LISTS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.trakinglists.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_HOUSEHOLDS_DATASETS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.households_data.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_DSS_EVENTS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.dss_events.zip_xml_files.label'
        ).save(flush: true)


        //Get defaults system paramaters from properties file
        def fmaxAge = svc.getConfigValue("${Codes.PARAMS_MIN_AGE_OF_FATHER}")
        def mmaxAge = svc.getConfigValue("${Codes.PARAMS_MIN_AGE_OF_MOTHER}")
        def hmaxAge = svc.getConfigValue("${Codes.PARAMS_MIN_AGE_OF_HEAD}")
        def smaxAge = svc.getConfigValue("${Codes.PARAMS_MIN_AGE_OF_SPOUSE}")
        def gndChck = svc.getConfigValue("${Codes.PARAMS_GENDER_CHECKING}")
        def sysLang = svc.getConfigValue("${Codes.PARAMS_SYSTEM_LANGUAGE}")
        def sysCdgn = svc.getConfigValue("${Codes.PARAMS_SYSTEM_CODE_GENERATOR}")
        def sysPath = svc.getConfigValue("${Codes.PARAMS_SYSTEM_HOMEPATH}")

        println "config: ${fmaxAge}"

        //Save Application/System Parameters to database, Will persist to the database only when its empty - changing parameters will be done through database
        aps.addParam(Codes.PARAMS_MIN_AGE_OF_MOTHER, StringUtil.getInteger(mmaxAge))
        aps.addParam(Codes.PARAMS_MIN_AGE_OF_FATHER, StringUtil.getInteger(fmaxAge))
        aps.addParam(Codes.PARAMS_MIN_AGE_OF_HEAD, StringUtil.getInteger(hmaxAge))
        aps.addParam(Codes.PARAMS_MIN_AGE_OF_SPOUSE, StringUtil.getInteger(smaxAge))
        aps.addParam(Codes.PARAMS_GENDER_CHECKING, StringUtil.getBoolean(gndChck))
        aps.addParam(Codes.PARAMS_SYSTEM_LANGUAGE, sysLang) //set default language to english
        aps.addParam(Codes.PARAMS_SYSTEM_CODE_GENERATOR, sysCdgn)
        aps.addParam(Codes.PARAMS_SYSTEM_HOMEPATH, sysPath)

        aps.addParamNullable(RegionLevel.HIERARCHY_1.code, null)
        aps.addParamNullable(RegionLevel.HIERARCHY_2.code, null)
        aps.addParamNullable(RegionLevel.HIERARCHY_3.code, null)
        aps.addParamNullable(RegionLevel.HIERARCHY_4.code, null)
        aps.addParamNullable(RegionLevel.HIERARCHY_5.code, null)
        aps.addParamNullable(RegionLevel.HIERARCHY_6.code, null)
        aps.addParamNullable(RegionLevel.HIERARCHY_7.code, null)
        aps.addParamNullable(RegionLevel.HIERARCHY_8.code, null)
        aps.addParamNullable(RegionLevel.HIERARCHY_9.code, null)
        aps.addParamNullable(RegionLevel.HIERARCHY_10.code, null)



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


        //Insert Sync Reports
        new SyncFilesReport(name: SyncEntity.PARAMETERS).save(flush:true)
        new SyncFilesReport(name: SyncEntity.MODULES).save(flush:true)
        new SyncFilesReport(name: SyncEntity.FORMS).save(flush:true)
        new SyncFilesReport(name: SyncEntity.DATASETS).save(flush:true)
        new SyncFilesReport(name: SyncEntity.DATASETS_CSV_FILES).save(flush:true)
        new SyncFilesReport(name: SyncEntity.TRACKING_LISTS).save(flush:true)
        new SyncFilesReport(name: SyncEntity.USERS).save(flush:true)
        new SyncFilesReport(name: SyncEntity.REGIONS).save(flush:true)
        new SyncFilesReport(name: SyncEntity.HOUSEHOLDS).save(flush:true)
        new SyncFilesReport(name: SyncEntity.MEMBERS).save(flush:true)

        //Insert Unknown Member for reference Unknown Individual
        //code: UNK, Unknown Member,
        new Member(id: "Unknown Member", code: Codes.MEMBER_UNKNOWN_CODE, name: "member.unknown.label", gender: Gender.MALE, dob: GeneralUtil.getDate(1900,1,1), maritalStatus: MaritalStatus.SINGLE).save(flush: true)


        insertCoreFormsExtensions()
        insertCoreFormsCustomColumns()
    }

    def insertCoreFormsExtensions(){
        def svc = coreFormExtensionService

        def map = new LinkedHashMap<CoreForm, Boolean[]>()
        CoreFormExtension.list().each {
            map.put(it.coreForm, new Boolean[]{ it.required, it.enabled})
            it.delete(flush: true)
        }

        def core0 = new CoreFormExtension(formName: CoreForm.REGION_FORM.name, coreForm: CoreForm.REGION_FORM, formId: CoreForm.REGION_FORM.code, extFormId: "region_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.REGION_FORM))
        def core1 = new CoreFormExtension(formName: CoreForm.HOUSEHOLD_FORM.name, coreForm: CoreForm.HOUSEHOLD_FORM, formId: CoreForm.HOUSEHOLD_FORM.code, extFormId: "household_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.HOUSEHOLD_FORM))
        def core2 = new CoreFormExtension(formName: CoreForm.VISIT_FORM.name, coreForm: CoreForm.VISIT_FORM, formId: CoreForm.VISIT_FORM.code, extFormId: "visit_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.VISIT_FORM))
        def core3 = new CoreFormExtension(formName: CoreForm.MEMBER_ENU_FORM.name, coreForm: CoreForm.MEMBER_ENU_FORM, formId: CoreForm.MEMBER_ENU_FORM.code, extFormId: "member_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.MEMBER_ENU_FORM))
        def core4 = new CoreFormExtension(formName: CoreForm.MARITAL_RELATIONSHIP_FORM.name, coreForm: CoreForm.MARITAL_RELATIONSHIP_FORM, formId: CoreForm.MARITAL_RELATIONSHIP_FORM.code, extFormId: "marital_relationship_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.MARITAL_RELATIONSHIP_FORM))
        def core5 = new CoreFormExtension(formName: CoreForm.INMIGRATION_FORM.name, coreForm: CoreForm.INMIGRATION_FORM, formId: CoreForm.INMIGRATION_FORM.code, extFormId: "inmigration_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.INMIGRATION_FORM))
        def core6 = new CoreFormExtension(formName: CoreForm.OUTMIGRATION_FORM.name, coreForm: CoreForm.OUTMIGRATION_FORM, formId: CoreForm.OUTMIGRATION_FORM.code, extFormId: "outmigration_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.OUTMIGRATION_FORM))
        def core7 = new CoreFormExtension(formName: CoreForm.PREGNANCY_REGISTRATION_FORM.name, coreForm: CoreForm.PREGNANCY_REGISTRATION_FORM, formId: CoreForm.PREGNANCY_REGISTRATION_FORM.code, extFormId: "pregnancy_registration_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.PREGNANCY_REGISTRATION_FORM))
        def core8 = new CoreFormExtension(formName: CoreForm.PREGNANCY_OUTCOME_FORM.name, coreForm: CoreForm.PREGNANCY_OUTCOME_FORM, formId: CoreForm.PREGNANCY_OUTCOME_FORM.code, extFormId: "pregnancy_outcome_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.PREGNANCY_OUTCOME_FORM))
        def core9 = new CoreFormExtension(formName: CoreForm.DEATH_FORM.name, coreForm: CoreForm.DEATH_FORM, formId: CoreForm.DEATH_FORM.code, extFormId: "death_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.DEATH_FORM))
        def core10 = new CoreFormExtension(formName: CoreForm.CHANGE_HEAD_FORM.name, coreForm: CoreForm.CHANGE_HEAD_FORM, formId: CoreForm.CHANGE_HEAD_FORM.code, extFormId: "change_head_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.CHANGE_HEAD_FORM))
        def core11 = new CoreFormExtension(formName: CoreForm.INCOMPLETE_VISIT_FORM.name, coreForm: CoreForm.INCOMPLETE_VISIT_FORM, formId: CoreForm.INCOMPLETE_VISIT_FORM.code, extFormId: "incomplete_visit_ext", required: true, enabled: false, columnsMapping: svc.getColumnMapping(CoreForm.INCOMPLETE_VISIT_FORM))

        def cores = [core0, core1, core2, core3, core4, core5, core6, core6, core7, core8, core9, core10, core11] as List<CoreFormExtension>

        for (CoreFormExtension core : cores){
            if (CoreFormExtension.countByFormId(core.formId)==0){
                if (map.containsKey(core.coreForm)){
                    def mapItem = map.get(core.coreForm)
                    core.required = mapItem[0]
                    core.enabled = mapItem[1]
                }

                core.save(flush:true)
            }
        }

    }

    def insertCoreFormsCustomColumns() {
        def svc = generalUtilitiesService

        if (CoreFormColumnMap.count()==0) {
            new CoreFormColumnMap(formName: "rawMemberEnu", columnName: "education", enabled: true).save(flush: true)
            new CoreFormColumnMap(formName: "rawMemberEnu", columnName: "religion", enabled: true).save(flush: true)

            new CoreFormColumnMap(formName: "rawExternalInMigration", columnName: "education", enabled: true).save(flush: true)
            new CoreFormColumnMap(formName: "rawExternalInMigration", columnName: "religion", enabled: true).save(flush: true)

            new CoreFormColumnMap(formName: "rawInMigration", columnName: "education", enabled: true).save(flush: true)
            new CoreFormColumnMap(formName: "rawInMigration", columnName: "religion", enabled: true).save(flush: true)
        }

        if (CoreFormColumnOptions.count()==0) {
            //Education
            new CoreFormColumnOptions(columnName: "education", ordinal: 1, optionValue: EducationType.NO_EDUCATION.code,        optionLabel: svc.getMessage(EducationType.NO_EDUCATION.name), optionLabelCode: EducationType.NO_EDUCATION.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "education", ordinal: 2, optionValue: EducationType.PRIMARY_EDUCATION.code,   optionLabel: svc.getMessage(EducationType.PRIMARY_EDUCATION.name), optionLabelCode: EducationType.PRIMARY_EDUCATION.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "education", ordinal: 3, optionValue: EducationType.SECONDARY_EDUCATION.code, optionLabel: svc.getMessage(EducationType.SECONDARY_EDUCATION.name), optionLabelCode: EducationType.SECONDARY_EDUCATION.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "education", ordinal: 4, optionValue: EducationType.BACHELORS_DEGRE.code,     optionLabel: svc.getMessage(EducationType.BACHELORS_DEGRE.name), optionLabelCode: EducationType.BACHELORS_DEGRE.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "education", ordinal: 5, optionValue: EducationType.MASTERS_DEGRE.code,       optionLabel: svc.getMessage(EducationType.MASTERS_DEGRE.name), optionLabelCode: EducationType.MASTERS_DEGRE.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "education", ordinal: 6, optionValue: EducationType.DOCTORATE_DEGRE.code,     optionLabel: svc.getMessage(EducationType.DOCTORATE_DEGRE.name), optionLabelCode: EducationType.DOCTORATE_DEGRE.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "education", ordinal: 7, optionValue: EducationType.OTHER.code,               optionLabel: svc.getMessage(EducationType.OTHER.name), optionLabelCode: EducationType.OTHER.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "education", ordinal: 8, optionValue: EducationType.UNKNOWN.code,             optionLabel: svc.getMessage(EducationType.UNKNOWN.name), optionLabelCode: EducationType.UNKNOWN.name).save(flush: true)

            //Religion
            new CoreFormColumnOptions(columnName: "religion", ordinal: 1, optionValue: ReligionType.CHRISTIAN_CATHOLIC.code,   optionLabel: svc.getMessage(ReligionType.CHRISTIAN_CATHOLIC.name), optionLabelCode: ReligionType.CHRISTIAN_CATHOLIC.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 2, optionValue: ReligionType.CHRISTIAN_ORTHODOX.code,   optionLabel: svc.getMessage(ReligionType.CHRISTIAN_ORTHODOX.name), optionLabelCode: ReligionType.CHRISTIAN_ORTHODOX.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 3, optionValue: ReligionType.CHRISTIAN_PROTESTANT.code, optionLabel: svc.getMessage(ReligionType.CHRISTIAN_PROTESTANT.name), optionLabelCode: ReligionType.CHRISTIAN_PROTESTANT.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 4, optionValue: ReligionType.MUSLIM.code,               optionLabel: svc.getMessage(ReligionType.MUSLIM.name), optionLabelCode: ReligionType.MUSLIM.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 5, optionValue: ReligionType.JEWISH.code,               optionLabel: svc.getMessage(ReligionType.JEWISH.name), optionLabelCode: ReligionType.JEWISH.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 6, optionValue: ReligionType.HINDU.code,                optionLabel: svc.getMessage(ReligionType.HINDU.name), optionLabelCode: ReligionType.HINDU.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 7, optionValue: ReligionType.BUDHIST.code,              optionLabel: svc.getMessage(ReligionType.BUDHIST.name), optionLabelCode: ReligionType.BUDHIST.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 8, optionValue: ReligionType.TRADITIONAL_FAITH.code,    optionLabel: svc.getMessage(ReligionType.TRADITIONAL_FAITH.name), optionLabelCode: ReligionType.TRADITIONAL_FAITH.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 9, optionValue: ReligionType.ATHEIST.code,              optionLabel: svc.getMessage(ReligionType.ATHEIST.name), optionLabelCode: ReligionType.ATHEIST.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 10, optionValue: ReligionType.NO_RELIGION.code,          optionLabel: svc.getMessage(ReligionType.NO_RELIGION.name), optionLabelCode: ReligionType.NO_RELIGION.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 11, optionValue: ReligionType.OTHER.code,                optionLabel: svc.getMessage(ReligionType.OTHER.name), optionLabelCode: ReligionType.OTHER.name).save(flush: true)
            new CoreFormColumnOptions(columnName: "religion", ordinal: 12, optionValue: ReligionType.UNKNOWN.code,              optionLabel: svc.getMessage(ReligionType.UNKNOWN.name), optionLabelCode: ReligionType.UNKNOWN.name).save(flush: true)
        }
    }

    def retrieveAndPopulateStaticConstants(){

        def valueAgm = applicationParamService.getIntegerValue(Codes.PARAMS_MIN_AGE_OF_MOTHER)
        def valueAgf = applicationParamService.getIntegerValue(Codes.PARAMS_MIN_AGE_OF_FATHER)
        def valueAgh = applicationParamService.getIntegerValue(Codes.PARAMS_MIN_AGE_OF_HEAD)
        def valueAgs = applicationParamService.getIntegerValue(Codes.PARAMS_MIN_AGE_OF_SPOUSE)
        def valueGch = applicationParamService.getBooleanValue(Codes.PARAMS_GENDER_CHECKING)
        def valueSlg = applicationParamService.getStringValue(Codes.PARAMS_SYSTEM_LANGUAGE)
        def valueScg = applicationParamService.getStringValue(Codes.PARAMS_SYSTEM_CODE_GENERATOR)
        def valuePth = applicationParamService.getStringValue(Codes.PARAMS_SYSTEM_HOMEPATH)

        Codes.MIN_MOTHER_AGE_VALUE = valueAgm != null ? valueAgm : Codes.MIN_MOTHER_AGE_VALUE
        Codes.MIN_FATHER_AGE_VALUE = valueAgf != null ? valueAgf : Codes.MIN_FATHER_AGE_VALUE
        Codes.MIN_HEAD_AGE_VALUE =   valueAgh != null ? valueAgh : Codes.MIN_HEAD_AGE_VALUE
        Codes.MIN_SPOUSE_AGE_VALUE = valueAgs != null ? valueAgs : Codes.MIN_SPOUSE_AGE_VALUE
        Codes.GENDER_CHECKING =      valueGch != null ? valueGch : Codes.GENDER_CHECKING
        Codes.SYSTEM_LANGUAGE =      !StringUtil.isBlank(valueSlg) ? valueSlg : Codes.SYSTEM_LANGUAGE
        Codes.SYSTEM_CODE_GENERATOR = !StringUtil.isBlank(valueScg) ? valueScg : Codes.SYSTEM_CODE_GENERATOR
        Codes.SYSTEM_HOMEPATH = !StringUtil.isBlank(valuePth) ? valuePth : Codes.SYSTEM_HOMEPATH
        SystemPath.HOME_PATH = Codes.SYSTEM_HOMEPATH

        println("gender ${Codes.GENDER_CHECKING}")
    }

    def setupCodeGenerators() {

        Reflections reflections = new Reflections("org.philimone.hds.explorer.server.settings.generator")
        def subTypes = reflections.getSubTypesOf(CodeGenerator.class);

        println "Number of Code Generator Implementations: ${subTypes.size()}"


        for (def implClass : subTypes) {
            def codeGen = (CodeGenerator) implClass.newInstance()
            def jcgen = new JConstant(name: codeGen.getName(), value: implClass.name)

            Codes.SYSTEM_ALL_CODE_GENERATORS.add(jcgen)

            println "Implemented class: "+implClass.name
            println "Implemented name:  "+codeGen.getName()
        }

        //Initialize Code Generator after retrieving from the database
        codeGeneratorService.codeGenerator = CodeGeneratorFactory.newInstance()
    }

    def testApp() {

    }

}
