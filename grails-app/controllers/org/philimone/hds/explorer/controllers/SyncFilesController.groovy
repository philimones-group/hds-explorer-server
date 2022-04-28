package org.philimone.hds.explorer.controllers

import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.settings.LogGroupCode
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.enums.SyncEntity
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.main.Dataset

import java.time.LocalDateTime

/**
 * This controller exposes the generated XML/Zip files to be downloaded and controls the export views tasks
 */
class SyncFilesController {

    static responseFormats = ['xml']

    def syncFilesService
    def syncFilesReportService

    def households = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "households.xml")
        render file: file
    }

    def members = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "members.xml")
        render file: file
    }

    def settings = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "settings.xml")
        render file: file
    }

    def modules = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "modules.xml")
        render file: file
    }

    def forms = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "forms.xml")
        render file: file
    }

    def coreforms = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + SyncEntity.CORE_FORMS_EXT.xmlFilename)
        render file: file
    }

    def users = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "users.xml")
        render file: file
    }

    def trackinglists = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "trackinglists.xml")
        render file: file
    }

    def stats = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "stats.xml")
        render file: file
    }

    def appParams = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "params.xml")
        render file: file
    }

    def regions = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "regions.xml")
        render file: file
    }

    def datasets = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "datasets.xml")
        render file: file
    }

    def dataset(String id){
        def dataset = Dataset.get(id)

        def file = new File(dataset.filename)
        render file: file, fileName: file.name
    }

    def residencies = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.RESIDENCIES.xmlFilename}")
        render file: file
    }

    def headRelationships = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.HEAD_RELATIONSHIPS.xmlFilename}")
        render file: file
    }

    def maritalRelationships = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.MARITAL_RELATIONSHIPS.xmlFilename}")
        render file: file
    }

    def rounds = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.ROUNDS.xmlFilename}")
        render file: file
    }

    def visits = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.VISITS.xmlFilename}")
        render file: file
    }

    def pregnancyRegistrations = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.PREGNANCY_REGISTRATIONS.xmlFilename}")
        render file: file
    }

    /* ZIP Files*/

    def householdsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "households.zip")
        //render file: file, fileName: "households.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def membersZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "members.zip")
        //render file: file, fileName: "members.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def settingsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "settings.zip")
        //render file: file, fileName: "settings.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def modulesZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "modules.zip")
        //render file: file, fileName: "modules.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def formsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "forms.zip")
        //render file: file, fileName: "forms.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def coreformsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + SyncEntity.CORE_FORMS_EXT.zipFilename)
        //render file: file, fileName: "forms.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def usersZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "users.zip")
        //render file: file, fileName: "users.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def trackinglistsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "trackinglists.zip")
        //render file: file, fileName: "trackinglists.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def statsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "stats.zip")
        //render file: file, fileName: "stats.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def appParamsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "params.zip")
        //render file: file, fileName: "params.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def regionsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "regions.zip")
        //render file: file, fileName: "regions.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def datasetsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "datasets.zip")
        //render file: file, fileName: "datasets.zip"

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def datasetZip(String id){
        def dataset = Dataset.get(id)

        def file = new File(dataset.compressedFilename)
        //render file: file, fileName: file.name

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def residenciesZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.RESIDENCIES.zipFilename}")

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def headRelationshipsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.HEAD_RELATIONSHIPS.zipFilename}")

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def maritalRelationshipsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.MARITAL_RELATIONSHIPS.zipFilename}")

        response.setContentLengthLong(file.size())
        response.setContentType("application/zip")
        response.outputStream << file.bytes
    }

    def roundsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.ROUNDS.zipFilename}")
        render file: file
    }

    def visitsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.VISITS.zipFilename}")
        render file: file
    }

    def pregnancyRegistrationsZip = {
        def file = new File(SystemPath.getGeneratedFilesPath() + File.separator + "${SyncEntity.PREGNANCY_REGISTRATIONS.zipFilename}")
        render file: file
    }

    def syncFilesReport = {
        //println(params.id)
        def report = syncFilesReportService.get(params?.id)
        long records = -1

        if (report != null){
            records = report.records
        }

        render records
    }

    /**
     * Free APK Download - we can think on creating an controller only for this
     */
    def downloadAndroidApk = {
        def file = new File(SystemPath.getApksPath() + File.separator + "hds-explorer-tablet.apk")
        render file: file, fileName: "hds-explorer-tablet.apk", contentType:"application/octet-stream"
    }

    /**/
    def index = {

        def logReports = LogReport.executeQuery("select lr from LogReport lr where lr.group.groupId=? order by lr.reportId", [LogGroupCode.GROUP_GENERATE_FILES])

        render view: "index", model: [logReports : logReports]
    }

    def export = {
        LogReport logReport = null

        LogReport.withTransaction {
            logReport = LogReport.get(params.id)
            logReport.keyTimestamp = System.currentTimeMillis() //CREATE timestamp code
            logReport.status = LogStatus.STARTED
            logReport.start = LocalDateTime.now();
            logReport.save(flush: true)
        }


        println "log ${logReport.errors}"

        def id = logReport.reportId

        if (logReport.reportId==LogReportCode.REPORT_GENERATE_SETTINGS_ZIP_XML_FILES){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing export files to settings xmls/zips"
                    syncFilesService.generateSettingsXML(id)
                }
            }).start()
        }

        if (logReport.reportId== LogReportCode.REPORT_GENERATE_EXTERNAL_DATASETS_ZIP_XML_FILES){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing export files to external datasets xml/zip"
                    syncFilesService.generateDatasetsXML(id)
                }
            }).start()
        }

        if (logReport.reportId==LogReportCode.REPORT_GENERATE_TRACKING_LISTS_ZIP_XML_FILES){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing export files to tracking lists xml/zip"
                    syncFilesService.generateTrackingListsXML(id)
                }
            }).start()
        }

        if (logReport.reportId==LogReportCode.REPORT_GENERATE_HOUSEHOLDS_DATASETS_ZIP_XML_FILES){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing export files to households datasets xmls/zips"
                    syncFilesService.generateHouseholdDatasets(id)
                }
            }).start()
        }

        if (logReport.reportId==LogReportCode.REPORT_GENERATE_DSS_EVENTS_ZIP_XML_FILES){
            new Thread(new Runnable() {
                @Override
                void run() {
                    println "executing export files to dss events xmls/zips"
                    syncFilesService.generateDemographicEvents(id)
                }
            }).start()
        }

        redirect action:'index'
    }

    def exportAll = {

        createLogStartup(LogReportCode.REPORT_GENERATE_SETTINGS_ZIP_XML_FILES)
        new Thread(new Runnable() {
            @Override
            void run() {
                println "executing export files to settings xmls/zips"
                syncFilesService.generateSettingsXML(LogReportCode.REPORT_GENERATE_SETTINGS_ZIP_XML_FILES)
            }
        }).start()

        createLogStartup(LogReportCode.REPORT_GENERATE_EXTERNAL_DATASETS_ZIP_XML_FILES)
        new Thread(new Runnable() {
            @Override
            void run() {
                println "executing export files to external datasets xml/zip"
                syncFilesService.generateDatasetsXML(LogReportCode.REPORT_GENERATE_EXTERNAL_DATASETS_ZIP_XML_FILES)
            }
        }).start()

        createLogStartup(LogReportCode.REPORT_GENERATE_TRACKING_LISTS_ZIP_XML_FILES)
        new Thread(new Runnable() {
            @Override
            void run() {
                println "executing export files to tracking lists xml/zip"
                syncFilesService.generateTrackingListsXML(LogReportCode.REPORT_GENERATE_TRACKING_LISTS_ZIP_XML_FILES)
            }
        }).start()

        createLogStartup(LogReportCode.REPORT_GENERATE_HOUSEHOLDS_DATASETS_ZIP_XML_FILES)
        new Thread(new Runnable() {
            @Override
            void run() {
                println "executing export files to households datasets xmls/zips"
                syncFilesService.generateHouseholdDatasets(LogReportCode.REPORT_GENERATE_HOUSEHOLDS_DATASETS_ZIP_XML_FILES)
            }
        }).start()

        createLogStartup(LogReportCode.REPORT_GENERATE_DSS_EVENTS_ZIP_XML_FILES)
        new Thread(new Runnable() {
            @Override
            void run() {
                println "executing export files to dss events xmls/zips"
                syncFilesService.generateDemographicEvents(LogReportCode.REPORT_GENERATE_DSS_EVENTS_ZIP_XML_FILES)
            }
        }).start()


        redirect action: "index"
    }

    void createLogStartup(LogReportCode logReportCode) {
        LogReport logReport = null

        LogReport.withTransaction {
            logReport = LogReport.findByReportId(logReportCode)
            logReport.keyTimestamp = System.currentTimeMillis() //CREATE timestamp code
            logReport.status = LogStatus.STARTED
            logReport.start = LocalDateTime.now();
            logReport.save(flush: true)
        }
    }
}
