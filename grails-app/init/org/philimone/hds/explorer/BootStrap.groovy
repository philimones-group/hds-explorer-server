package org.philimone.hds.explorer


import org.philimone.hds.explorer.server.model.authentication.Role
import org.philimone.hds.explorer.server.model.authentication.SecurityMap
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.RegionLevel
import org.philimone.hds.explorer.server.model.enums.settings.LogGroupCode
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.logs.LogGroup
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.main.MappingFormatType
import org.philimone.hds.explorer.server.model.main.StudyModule
import org.philimone.hds.explorer.server.model.enums.SyncEntity
import org.philimone.hds.explorer.server.model.settings.Codes
import org.philimone.hds.explorer.server.model.settings.SyncFilesReport

class BootStrap {

    def generalUtilitiesService
    def userService
    def applicationParamService

    def init = { servletContext ->

        createDirectories(servletContext)
        configSecurityMap()
        defaultAppUser()
        insertDefaults()
        retrieveAndPopulateStaticConstants()
        //testApp()
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


            new SecurityMap(url: "/syncFiles/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/syncFilesReport/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/generalUtilities/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/importOpenHDS/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/dssSynchronization/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/studyModule/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/form/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/formMapping/*/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/dataSet/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/trackingList/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            //new SecurityMap(url: "/household/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/member/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            //new SecurityMap(url: "/settings/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            //new SecurityMap(url: "/reports/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/logReport/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            //rest api access
            new SecurityMap(url: "/api/login", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/households/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/members/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/settings/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/modules/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/forms/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/users/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/trackinglists/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/residencies/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/hrelationships/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/mrelationships/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/stats/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            new SecurityMap(url: "/api/export/params/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/regions/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            new SecurityMap(url: "/api/export/datasets/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)
            new SecurityMap(url: "/api/export/dataset/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            new SecurityMap(url: "/api/export/sync-report/**", configAttribute: "${Role.ROLE_ADMINISTRATOR},${Role.ROLE_DATA_MANAGER}").save(flush: true)

            //File downloads
            new SecurityMap(url: "/download/free/android/apk", configAttribute: "permitAll").save(flush: true)
            new SecurityMap(url: "/syncFiles/downloadAndroidApk", configAttribute: "permitAll").save(flush: true)


        }
    }

    def defaultAppUser (){
        //Default ApplicationUser
        if (User.count()==0){

            Role admin = Role.findByAuthority(Role.ROLE_ADMINISTRATOR)

            User user = new User()

            user.firstName = "System"
            user.lastName = "Administrator"
            user.username = "admin";
            user.password = "admin";
            user.email = "youremail@domain.net"

            println ("admin user - hasErrors: "+user.hasErrors())
            println ("admin user - saving: " + userService.addUser(user, [admin]))



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

        /* Group Sync DSS Data from Client (odk, etc) */
        new LogReport(
                reportId: LogReportCode.REPORT_DSS_ODK_CENSUS_SYNC,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_SYNC_DSS_DATA_FROM_CLIENT),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.sync.syncdss.odk.census.label'
        ).save(flush: true)

        /* Group Generate Files */
        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_USERS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.users.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_HOUSEHOLDS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.households.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_MEMBERS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.individuals.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_SETTINGS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.settings.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_TRACKING_LISTS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.trakinglists.zip_xml_files.label'
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_RESIDENCIES_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.residencies.zip_xml_files.label',
                enabled: false
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_HEAD_RELATIONSHIPS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.head_relationships.zip_xml_files.label',
                enabled: false
        ).save(flush: true)

        new LogReport(
                reportId: LogReportCode.REPORT_GENERATE_MARTIAL_RELATIONSHIPS_ZIP_XML_FILES,
                group: LogGroup.findByGroupId(LogGroupCode.GROUP_GENERATE_FILES),
                status: LogStatus.NOT_STARTED,
                description: 'logreport.export.marital_relationships.zip_xml_files.label',
                enabled: false
        ).save(flush: true)

        //Get defaults system paramaters from properties file
        def maxCols = svc.getConfigValue("${Codes.PARAMS_TRACKLIST_MAX_DATA_COLUMNS}")
        def fmaxAge = svc.getConfigValue("${Codes.PARAMS_MIN_AGE_OF_FATHER}")
        def mmaxAge = svc.getConfigValue("${Codes.PARAMS_MIN_AGE_OF_MOTHER}")
        def hmaxAge = svc.getConfigValue("${Codes.PARAMS_MIN_AGE_OF_HEAD}")
        def gndChck = svc.getConfigValue("${Codes.PARAMS_GENDER_CHECKING}")
        println "code: ${maxCols}"

        //Save Application/System Parameters to database, Will persist to the database only when its empty - changing parameters will be done through database
        aps.addParam(Codes.PARAMS_TRACKLIST_MAX_DATA_COLUMNS, Integer.parseInt(maxCols))
        aps.addParam(Codes.PARAMS_MIN_AGE_OF_MOTHER, Integer.parseInt(mmaxAge))
        aps.addParam(Codes.PARAMS_MIN_AGE_OF_FATHER, Integer.parseInt(fmaxAge))
        aps.addParam(Codes.PARAMS_MIN_AGE_OF_HEAD, Integer.parseInt(hmaxAge))
        aps.addParam(Codes.PARAMS_GENDER_CHECKING, Boolean.parseBoolean(gndChck))

        aps.addParam(RegionLevel.HIERARCHY_1, "")
        aps.addParam(RegionLevel.HIERARCHY_2, "")
        aps.addParam(RegionLevel.HIERARCHY_3, "")
        aps.addParam(RegionLevel.HIERARCHY_4, "")
        aps.addParam(RegionLevel.HIERARCHY_5, "")
        aps.addParam(RegionLevel.HIERARCHY_6, "")
        aps.addParam(RegionLevel.HIERARCHY_7, "")
        aps.addParam(RegionLevel.HIERARCHY_8, "")
        aps.addParam(RegionLevel.HIERARCHY_9, "")
        aps.addParam(RegionLevel.HIERARCHY_10, "")

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
    }

    def retrieveAndPopulateStaticConstants(){

        def valueDtc = applicationParamService.getIntegerValue(Codes.PARAMS_TRACKLIST_MAX_DATA_COLUMNS)
        def valueAgm = applicationParamService.getIntegerValue(Codes.PARAMS_MIN_AGE_OF_MOTHER)
        def valueAgf = applicationParamService.getIntegerValue(Codes.PARAMS_MIN_AGE_OF_FATHER)
        def valueAgh = applicationParamService.getIntegerValue(Codes.PARAMS_MIN_AGE_OF_HEAD)
        def valueGch = applicationParamService.getBooleanValue(Codes.PARAMS_GENDER_CHECKING)

        Codes.MAX_TRACKLIST_DATA_COLUMNS_VALUE = valueDtc != null ? valueDtc : Codes.MAX_TRACKLIST_DATA_COLUMNS_VALUE
        Codes.MIN_MOTHER_AGE_VALUE = valueAgm != null ? valueAgm : Codes.MIN_MOTHER_AGE_VALUE
        Codes.MIN_FATHER_AGE_VALUE = valueAgf != null ? valueAgf : Codes.MIN_FATHER_AGE_VALUE
        Codes.MIN_HEAD_AGE_VALUE = valueAgh != null ? valueAgh : Codes.MIN_HEAD_AGE_VALUE
        Codes.GENDER_CHECKING = valueGch != null ? valueGch : Codes.GENDER_CHECKING
    }

    def insertTestData(){
        //insert modules, form mappings, tracking lists
        def module = StudyModule.findByCode(StudyModule.DSS_SURVEY_MODULE)
/*
        def form1 = new Form()
        form1.formId = "Eligv1"
        form1.formName = "Eligibity Form"
        form1.formDescription = ""
        form1.gender  = "ALL"          //  M, F, ALL, if HouseholdForm is true, gender should be ALL
        form1.minAge  = 0
        form1.maxAge  = 120
        form1.isHouseholdForm = false
        form1.isMemberForm = true
        form1.isHouseholdHeadForm = false
        form1.isFollowUpForm = false
        form1.enabled = true
        form1.addToModules(module)


        def form2 = new Form()
        form2.formId = "Form1"
        form2.formName = "ODK Form 1 Test"
        form2.formDescription = ""
        form2.gender  = "ALL"          //  M, F, ALL, if HouseholdForm is true, gender should be ALL
        form2.minAge  = 0
        form2.maxAge  = 120
        form2.isHouseholdForm = false
        form2.isMemberForm = true
        form2.isHouseholdHeadForm = true
        form2.isFollowUpForm = false
        form2.enabled = true
        form2.addToModules(module)


        def form3 = new Form()
        form3.formId = "Form2"
        form3.formName = "ODK Form 1 Test"
        form3.formDescription = ""
        form3.gender  = "ALL"          //  M, F, ALL, if HouseholdForm is true, gender should be ALL
        form3.minAge  = 0
        form3.maxAge  = 120
        form3.isHouseholdForm = false
        form3.isMemberForm = true
        form3.isHouseholdHeadForm = false
        form3.isFollowUpForm = false
        form3.enabled = true
        form3.addToModules(module)
        form3.addToDependencies(form2)

        form1.save(flush:true)
        form2.save(flush:true)
        form3.save(flush:true)

        //add mapping and dependencies
        new FormMapping(form: form1, formVariableName: 'individualId', tableName: 'Member', columnName: 'code').save(flush:true)
*/

        //Insert Tracking Lists Sample
/*
        trackingListService.copySampleFilesToExternalDocsPath()

        def trackList1 = TrackingList.findOrCreateByCode('E01')
        trackList1.code = 'E01'
        trackList1.name = 'Eligibility Follow-up'
        trackList1.filename = '/var/lib/hds-explorer/tracking_list_sample_1.xlsx'
        trackList1.module = module
        trackList1.hasExtraData = false
        trackList1.enabled = true
        trackList1.createdBy = null
        trackList1.createdDate = null
        trackList1.updatedBy = null
        trackList1.updatedDate = null

        trackList1.save()


        def trackList2 = TrackingList.findOrCreateByCode('E02')
        trackList2.code = 'E02'
        trackList2.name = 'Eligibility Follow-up EX'
        trackList2.filename = '/var/lib/hds-explorer/tracking_list_sample_2.xlsx'
        trackList2.module = module
        trackList2.hasExtraData = true
        trackList2.enabled = true
        trackList2.createdBy = null
        trackList2.createdDate = null
        trackList2.updatedBy = null
        trackList2.updatedDate = null

        trackList2.save()

        //trackList2.addToMappings(new TrackingListMapping( list: trackList2, name: 'data.religion', label:'Religion' ))
        //trackList2.addToMappings(new TrackingListMapping( list: trackList2, name: 'data.education', label:'Education' ))
        //trackList2.addToMappings(new TrackingListMapping( list: trackList2, name: 'data.has_hiv', label:'Has HIV' ))

        def m1 = new TrackingListMapping( list: trackList2, name: 'data.religion', label:'Religion')
        def m2 = new TrackingListMapping( list: trackList2, name: 'data.education', label:'Education')
        def m3 = new TrackingListMapping( list: trackList2, name: 'data.has_hiv', label:'Has HIV')

        m1.save(fluh: true)
        m2.save(fluh: true)
        m3.save(fluh: true)

        println "error: ${m1.errors}"

        //trackList2.save()
        */



    }

    def testApp(){

        //insertTestData();

        //testDataService.repairKeyTimestamp()

        //String xml = trackingListService.createXMLfromFile("/home/paul/snap/tracking_list_sample_1.xlsx")
        //println(xml)

        //importDataFromOpenHDSService.importFieldWorkers(LogReportCode.REPORT_IMPORT_FROM_OPENHDS_FIELDWORKERS)
        //importDataFromOpenHDSService.importRegions(LogReportCode.REPORT_IMPORT_FROM_OPENHDS_HOUSEHOLDS)
        //importDataFromOpenHDSService.importHouseholds(LogReportCode.REPORT_IMPORT_FROM_OPENHDS_HOUSEHOLDS)
        //importDataFromOpenHDSService.importIndividuals(LogReportCode.REPORT_IMPORT_FROM_OPENHDS_INDIVIDUALS)

        //exportFilesService.generateUsersXML(LogReportCode.REPORT_GENERATE_USERS_ZIP_XML_FILES)
        //exportFilesService.generateSettingsXML(LogReportCode.REPORT_GENERATE_SETTINGS_ZIP_XML_FILES)
        //exportFilesService.generateHouseHoldsXML(LogReportCode.REPORT_GENERATE_HOUSEHOLDS_ZIP_XML_FILES)
        //exportFilesService.generateMembersXML(LogReportCode.REPORT_GENERATE_MEMBERS_ZIP_XML_FILES)


/*
        println "reading relationships"
        def relationships1 = Relationship.executeQuery("select r.individualA.extId, r.individualB.extId, r.aisToB, r.startDate from Relationship r, Individual i where r.individualA.uuid=i.uuid and r.startDate=(select max(r2.startDate) from Relationship r2 where r2.individualA.uuid=r.individualA.uuid)")
        def relationships2 = Relationship.executeQuery("select r.individualB.extId, r.individualA.extId, r.aisToB, r.startDate from Relationship r, Individual i where r.individualB.uuid=i.uuid and r.startDate=(select max(r2.startDate) from Relationship r2 where r2.individualB.uuid=r.individualB.uuid)")
        def map1 = [:]

        println "count A->B: ${relationships1.size()}"
        relationships1.each { r ->
            println "${r[0]}, ${r[1]}, ${r[3]}, ${r[2]}"

            map1.put(r[0], [r[1], r[3], r[2]])
        }

        println "count B->A: ${relationships2.size()}"
        relationships2.each { r ->
            println "${r[0]}, ${r[1]}, ${r[3]}, ${r[2]}"
            map1.put(r[0], [r[1], r[3], r[2]])
        }

        println "testing: \n\n"
        relationships2.each { r ->
            if (map1.containsKey(r[0])){
                println("duplicated ${r[0]} of ${map1.get(r[0])}")
            }
        }
*/
/*
        def webAccess = new WebApiAccess("http://localhost:8081/openhds2/api/rest/", "admin", "Simple123!")

        def individual = new org.philimone.hds.explorer.server.openhds.xml.model.Individual();

        individual.setCollectedBy("UNK");
        individual.setExtId("MHD000001999"); //MHD000001001
        individual.setDob(new Date());
        individual.setFirstName("X-47X-47-X47");
        individual.setLastName("4747-4646-47");
        //individual.setMiddleName(womenRelation);
        individual.setGender("M");
        individual.setFather("UNK");
        individual.setMother("UNK");

        WebApiAccess.Response result = webAccess.send("individual", individual);

        if (result.getStatus()=="ErrorIO"){
            System.out.println("\nErrorIO: ");
            System.out.println(result.getBodyText());

            System.out.println("ErrorIO: \n");
            System.out.println(result.getBodyText());

            return;
        }

        if (!result.getStatus().toString().contains("201 Created")){
            System.out.println(""+result.getStatus());
            System.out.println(result.getBodyText()+"\n");
        }

        System.out.println(" ["+result.getStatus()+"]");
*/

        assert 1==0

    }
}
