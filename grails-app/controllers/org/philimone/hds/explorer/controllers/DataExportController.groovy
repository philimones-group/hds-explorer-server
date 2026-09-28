package org.philimone.hds.explorer.controllers

import grails.converters.JSON
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.DataExportFormat
import org.philimone.hds.explorer.server.model.enums.DataExportItem
import org.philimone.hds.explorer.server.model.enums.DataExportStatus
import org.philimone.hds.explorer.server.model.enums.DateAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.GpsAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.NameAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.PhoneAnonymizationMode
import org.philimone.hds.explorer.server.model.main.CoreFormExtension
import org.philimone.hds.explorer.services.DataDictionaryService
import org.philimone.hds.explorer.services.DataExportService
import org.philimone.hds.explorer.services.DataExportService.ExportRequest
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.settings.DataExportReport

import java.time.LocalDate
import java.time.LocalDateTime

class DataExportController {

    DataDictionaryService dataDictionaryService
    DataExportService dataExportService
    def generalUtilitiesService

    def index() {
        List<DataDictionaryService.TableMetadata> allTables = dataDictionaryService.getAllTables()
        List<CoreFormExtension> customForms = CoreFormExtension.findAllByEnabled(true)
        List<DataExportItem> prejoinedViews = DataExportItem.getPrejoinedViews()
        List<Region> regions = Region.list([sort: 'code', order: 'asc'])

        [
                tables: allTables,
                customForms: customForms,
                prejoinedViews: prejoinedViews,
                regions: regions,
                exportFormats: DataExportFormat.values(),
                nameModes: NameAnonymizationMode.values(),
                phoneModes: PhoneAnonymizationMode.values(),
                gpsModes: GpsAnonymizationMode.values(),
                dateModes: DateAnonymizationMode.values()
        ]
    }

    def history() {
        List<DataExportReport> reports = DataExportReport.list([sort: 'createdDate', order: 'desc', max: 100])

        long totalExports = DataExportReport.count()
        long runningExports = DataExportReport.countByStatus(DataExportStatus.EXECUTING)
        long completedExports = DataExportReport.countByStatus(DataExportStatus.COMPLETED)
        long failedExports = DataExportReport.countByStatus(DataExportStatus.FAILED)

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay()
        List<DataExportReport> todayReports = DataExportReport.findAllByCreatedDateGreaterThanEquals(startOfDay)
        long recordsToday = todayReports.sum { it.totalRecords ?: 0L } ?: 0L
        long filesGeneratedToday = todayReports.count { it.status == DataExportStatus.COMPLETED } ?: 0L

        long totalStorageBytes = reports.sum { (it.datasetFileSize ?: 0L) + (it.zipFileSize ?: 0L) } ?: 0L

        [
                reports: reports,
                totalExports: totalExports,
                runningExports: runningExports,
                completedExports: completedExports,
                failedExports: failedExports,
                recordsToday: recordsToday,
                filesGeneratedToday: filesGeneratedToday,
                totalStorageBytes: totalStorageBytes,
                activeReport: reports.find { it.status == DataExportStatus.EXECUTING }
        ]
    }

    def getJobStatus() {
        List<DataExportReport> activeReports = DataExportReport.findAllByStatus(DataExportStatus.EXECUTING)
        List<Map<String, Object>> result = activeReports.collect { r ->
            [
                    id: r.id,
                    datasetName: r.datasetName,
                    status: r.status.code,
                    progressPercent: r.progressPercent ?: 0,
                    currentStep: r.currentStep ?: "Processing...",
                    downloadUrl: createLink(action: 'downloadReportFile', id: r.id)
            ]
        }
        render([success: true, activeJobs: result] as JSON)
    }

    def getJobDetails() {
        String reportId = params.id
        DataExportReport r = DataExportReport.get(reportId)

        if (r) {
            DataExportFormat fmtObj = DataExportFormat.getFrom(r.format)
            render([
                    success: true,
                    job: [
                            id: r.id,
                            datasetName: r.datasetLabel ?: r.datasetName,
                            format: fmtObj ? fmtObj.title : r.format,
                            createdBy: r.createdBy?.username,
                            createdDate: r.createdDate?.toString()?.replace('T', ' ')?.substring(0, 16),
                            executionTimeMs: r.executionTimeMs ? (r.executionTimeMs / 1000.0).round(1) : '0',
                            totalColumns: r.totalColumns ?: 0,
                            status: r.status?.code,
                            referenceDate: r.referenceDate?.toString() ?: 'N/A',
                            gender: r.gender ?: 'Both Genders',
                            regionCode: r.regionCode ?: 'All Study Regions',
                            nameAnonymizationMode: r.nameAnonymizationMode?.name ?: 'Initials',
                            phoneAnonymizationMode: r.phoneAnonymizationMode?.name ?: 'Masked',
                            gpsAnonymizationMode: r.gpsAnonymizationMode?.name ?: 'Rounded',
                            dateAnonymizationMode: r.dateAnonymizationMode?.name ?: 'Exact',
                            downloadUrl: createLink(action: 'downloadReportFile', id: r.id)
                    ]
            ] as JSON)
        } else {
            render([success: false, message: "Report ${reportId} not found"] as JSON)
        }
    }

    def getColumns() {
        String datasetName = params.datasetName ? params.datasetName.trim() : ""
        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(datasetName)

        if (meta) {
            List<Map<String, Object>> cols = meta.columns.values().collect { c ->
                [
                        columnName: c.columnName,
                        label: c.label ?: c.columnName,
                        dataType: c.dataType,
                        description: c.description ?: "",
                        sensitiveType: c.sensitiveType?.code ?: "not_applicable"
                ]
            }
            render([success: true, tableName: meta.tableName, columns: cols] as JSON)
        } else {
            render([success: false, message: "Dataset ${datasetName} not found in dictionary"] as JSON)
        }
    }

    def preview() {
        try {
            ExportRequest req = bindExportRequest()
            List<Map<String, Object>> rows = dataExportService.executePreview(req)
            render([success: true, rows: rows, totalRows: rows.size()] as JSON)
        } catch (Exception e) {
            log.error("Preview failed: ${e.message}", e)
            render([success: false, message: e.message] as JSON)
        }
    }

    /**
     * Initiates asynchronous export job and redirects to Data Export History page
     */
    def download() {
        ExportRequest req = bindExportRequest()

        DataExportItem exportItem = DataExportItem.getFrom(req.datasetType)

        DataExportReport report = new DataExportReport(
                exportItem: exportItem,
                datasetName: req.datasetName,
                datasetLabel: req.datasetLabel,
                format: req.format,
                status: DataExportStatus.EXECUTING,
                progressPercent: 5,
                currentStep: "Initiating export job...",
                referenceDate: req.referenceDate,
                gender: req.gender,
                ageMin: req.ageMin,
                ageMax: req.ageMax,
                regionCode: req.regionCode,
                randomSamplePercent: req.randomSamplePercent,
                includeDictionary: req.includeDictionary,
                activeResidentsOnly: req.activeResidentsOnly,
                nameAnonymizationMode: req.nameAnonymizationMode,
                phoneAnonymizationMode: req.phoneAnonymizationMode,
                gpsAnonymizationMode: req.gpsAnonymizationMode,
                dateAnonymizationMode: req.dateAnonymizationMode,
                totalColumns: req.selectedColumns ? req.selectedColumns.size() : 0,
                createdBy: generalUtilitiesService.currentUser()
        )
        report.save(flush: true)

        final String reportId = report.id
        Thread.start {
            DataExportReport.withTransaction {
                dataExportService.processAsyncReport(reportId)
            }
        }

        flash.message = message(code: 'dataExport.jobCreated', default: "Export job '${req.datasetName}' created successfully. Monitoring execution progress...")
        redirect(action: 'history')
    }

    def cancelExport() {
        String reportId = params.id
        DataExportReport report = DataExportReport.get(reportId)
        if (report && report.status == DataExportStatus.EXECUTING) {
            report.status = DataExportStatus.FAILED
            report.progressPercent = 0
            report.currentStep = "Cancelled by user"
            report.errorMessage = "Export job was cancelled by user."
            report.save(flush: true)
            flash.message = "Export job cancelled."
        }
        redirect(action: 'history')
    }

    def retryExport() {
        String reportId = params.id
        DataExportReport report = DataExportReport.get(reportId)
        if (report) {
            report.status = DataExportStatus.EXECUTING
            report.progressPercent = 5
            report.currentStep = "Retrying export job..."
            report.errorMessage = null
            report.save(flush: true)

            final String id = report.id
            Thread.start {
                DataExportReport.withTransaction {
                    dataExportService.processAsyncReport(id)
                }
            }
            flash.message = "Export job re-queued."
        }
        redirect(action: 'history')
    }

    def downloadReportFile() {
        String reportId = params.id
        DataExportReport report = DataExportReport.get(reportId)

        if (!report) {
            flash.error = message(code: 'dataExport.report.notFound', default: 'Export report record not found.')
            redirect(action: 'history')
            return
        }

        String fileName = report.zipFileName ?: report.datasetFileName
        if (!fileName) {
            flash.error = message(code: 'dataExport.report.noFile', default: 'No export file associated with this report.')
            redirect(action: 'history')
            return
        }

        File file = new File(SystemPath.externalDocsPath, fileName)
        if (!file.exists()) {
            flash.error = message(code: 'dataExport.report.fileExpired', args: [fileName], default: "Export file ${fileName} has expired or was deleted.")
            redirect(action: 'history')
            return
        }

        String fmt = report.format ? report.format.toUpperCase() : "CSV"
        if (fmt == "EXCEL") {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        } else if (fmt == "CSV") {
            response.setContentType("text/csv; charset=UTF-8")
        } else {
            response.setContentType("application/zip")
        }

        response.setHeader("Content-Disposition", "attachment; filename=\"${file.name}\"")
        file.withInputStream { is ->
            response.outputStream << is
        }
        response.outputStream.flush()
    }

    private ExportRequest bindExportRequest() {
        println("params: ${params}")
        ExportRequest req = new ExportRequest()
        req.datasetName = params.datasetName ?: "member"
        req.datasetType = params.datasetType == "PREJOINED_DSS" ? req.datasetName : params.datasetType
        req.format = params.modalFormat ?: params.format ?: "CSV"

        //get datasetLabel
        if (req.datasetLabel?.empty) {
            def allTables = dataDictionaryService.getTablesMap()
            List<CoreFormExtension> customForms = CoreFormExtension.findAllByEnabled(true)
            List<DataExportItem> prejoinedViews = DataExportItem.getPrejoinedViews()

            def dataExportItem = DataExportItem.getFrom(req.datasetType)
            if (dataExportItem == DataExportItem.REGULAR_TABLE) {
                def t = allTables.get(req.datasetName)
                if (t)
                    req.datasetLabel = "${t.domainName} (${t.tableName})"
            } else if (dataExportItem == DataExportItem.DYNAMIC_FORM) {
                def f = customForms.find { it.extFormId == req.datasetName}
                if (f)
                    req.datasetLabel = "${g.message(code: f.formName)} (${f.extFormId})"
            } else {
                def v = prejoinedViews.find{ it.code == req.datasetName}
                if (v)
                    req.datasetLabel = "${g.message(code: v.name)}"
            }
        }

        if (params.referenceDate) {
            try {
                req.referenceDate = LocalDate.parse(params.referenceDate.toString())
            } catch (Exception e) {
                req.referenceDate = LocalDate.now()
            }
        }

        req.gender = params.gender ?: "ALL"
        req.ageMin = params.ageMin ? params.ageMin.toInteger() : null
        req.ageMax = params.ageMax ? params.ageMax.toInteger() : null
        req.regionCode = params.regionCode ?: null

        if (params.randomSamplePercent) {
            try {
                req.randomSamplePercent = params.randomSamplePercent.toDouble()
            } catch (Exception e) {
                req.randomSamplePercent = 100.0
            }
        }

        if (params.selectedColumns) {
            if (params.selectedColumns instanceof String[]) {
                req.selectedColumns = Arrays.asList(params.selectedColumns)
            } else if (params.selectedColumns instanceof List) {
                req.selectedColumns = (List<String>) params.selectedColumns
            } else {
                req.selectedColumns = params.selectedColumns.toString().split(",").collect { it.trim() }
            }
        }

        req.includeDictionary = params.includeDictionary ? params.includeDictionary.toBoolean() : true
        req.activeResidentsOnly = params.activeResidentsOnly ? params.activeResidentsOnly.toBoolean() : true

        req.nameAnonymizationMode = NameAnonymizationMode.getFrom(params.nameAnonymizationMode) ?: NameAnonymizationMode.INITIALS
        req.phoneAnonymizationMode = PhoneAnonymizationMode.getFrom(params.phoneAnonymizationMode) ?: PhoneAnonymizationMode.MASK
        req.gpsAnonymizationMode = GpsAnonymizationMode.getFrom(params.gpsAnonymizationMode) ?: GpsAnonymizationMode.ROUNDED
        req.dateAnonymizationMode = DateAnonymizationMode.getFrom(params.dateAnonymizationMode) ?: DateAnonymizationMode.EXACT

        return req
    }
}
