package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.streaming.SXSSFSheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.hibernate.SessionFactory
import org.hibernate.query.NativeQuery
import org.hibernate.transform.AliasToEntityMapResultTransformer

import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.DataExportFormat
import org.philimone.hds.explorer.server.model.enums.DataExportItem
import org.philimone.hds.explorer.server.model.enums.DataExportStatus
import org.philimone.hds.explorer.server.model.settings.DataExportReport

import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Transactional(readOnly = true)
class DataExportService {

    DataDictionaryService dataDictionaryService
    DataExportSqlQueryBuilderService dataExportSqlQueryBuilderService
    DataExportScriptsBuilderService dataExportScriptsBuilderService
    DataExportAnonymizationService dataExportAnonymizationService
    DataExportNaturalLanguageSqlService dataExportNaturalLanguageSqlService
    GeneralUtilitiesService generalUtilitiesService
    SessionFactory sessionFactory

    // --- Configuration Constants ---
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
    private static final int PREVIEW_MAX_ROWS = 20
    private static final int EXCEL_STREAMING_ROW_FLUSH_SIZE = 100
    private static final int DATE_SHIFT_OFFSET_DAYS = 7
    private static final long DATE_SHIFT_OFFSET_MS = DATE_SHIFT_OFFSET_DAYS * 86400000L

    // --- File & Storage Constants ---
    private static final String TEMP_FILE_PREFIX = "hdss_export_temp_"
    private static final String TEMP_FILE_SUFFIX = ".tmp"
    private static final String DICTIONARY_EXCEL_FILENAME = "data_dictionary.xlsx"
    private static final String UTF8_BOM = "\uFEFF"

    // --- Progress Stage Percentages ---
    private static final int PROGRESS_INIT = 20
    private static final int PROGRESS_PROCESSING = 50
    private static final int PROGRESS_SAVING = 85
    private static final int PROGRESS_COMPLETED = 100

    /**
     * Executes live preview returning top sample rows as List of Maps
     */
    List<Map<String, Object>> executePreview(ExportRequest request) {
        request.maxRows = PREVIEW_MAX_ROWS
        List<Map<String, Object>> rows = []

        String sql = dataExportSqlQueryBuilderService.buildExportSql(request)
        def session = sessionFactory.currentSession
        NativeQuery query = session.createNativeQuery(sql)
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)
        query.setMaxResults(PREVIEW_MAX_ROWS)

        List<Map<String, Object>> resultList = query.list()
        for (Map<String, Object> rawRow : resultList) {
            Map<String, Object> processed = processRow(rawRow, request)
            if (request.selectedColumns && !request.selectedColumns.isEmpty()) {
                Map<String, Object> ordered = new LinkedHashMap<>()
                request.selectedColumns.each { colName ->
                    if (processed.containsKey(colName)) {
                        ordered.put(colName, processed.get(colName))
                    }
                }
                if (!ordered.isEmpty()) {
                    processed = ordered
                }
            }
            rows.add(processed)
        }

        return rows
    }

    /**
     * Asynchronously executes export report job in background thread with guaranteed transaction commit
     */
    void processAsyncReport(String reportId) {
        long startTime = System.currentTimeMillis()

        DataExportReport.withTransaction { transactionStatus ->
            DataExportReport report = DataExportReport.get(reportId)
            if (!report) return

            try {
                report.status = DataExportStatus.EXECUTING
                report.progressPercent = PROGRESS_INIT
                report.currentStep = "Querying dataset records from database..."
                report.save(flush: true, failOnError: true)

                List<String> selectedColsList = report.selectedColumns ? report.selectedColumns.split(",").collect { it.trim() } : []
                DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(report.datasetName)

                List<Object> sqlParamsList = []
                if (report.sqlParamValues) {
                    try {
                        def parsedParams = new JsonSlurper().parseText(report.sqlParamValues)
                        if (parsedParams instanceof List) {
                            sqlParamsList = (List<Object>) parsedParams
                        }
                    } catch (Exception e) {
                        log.warn("Failed parsing report.sqlParamValues: ${e.message}")
                    }
                }

                ExportRequest req = new ExportRequest(
                        datasetType: report.exportItem?.code ?: DataExportItem.REGULAR_TABLE.code,
                        datasetName: report.datasetName,
                        format: DataExportFormat.getFrom(report.format),
                        referenceDate: report.referenceDate,
                        gender: report.gender,
                        ageMin: report.ageMin,
                        ageMax: report.ageMax,
                        regionCode: report.regionCode,
                        randomSamplePercent: report.randomSamplePercent,
                        selectedColumns: selectedColsList,
                        includeDictionary: report.includeDictionary,
                        includeMediaFiles: report.includeMediaFiles,
                        activeResidentsOnly: report.activeResidentsOnly,
                        hasMediaFields: meta?.hasMediaColumns ?: false,
                        hasRepeatTables: meta?.hasRepeatTables ?: false,
                        aiPromptMode: report.aiPromptMode ?: false,
                        aiPromptText: report.aiPromptText ?: "",
                        generatedSql: report.executedSql ?: "",
                        sqlParamValues: sqlParamsList,
                        nameAnonymizationMode: report.nameAnonymizationMode,
                        phoneAnonymizationMode: report.phoneAnonymizationMode,
                        gpsAnonymizationMode: report.gpsAnonymizationMode,
                        dateAnonymizationMode: report.dateAnonymizationMode
                )

                report.progressPercent = PROGRESS_PROCESSING
                report.currentStep = "Processing anonymization & formatting data..."
                report.save(flush: true, failOnError: true)

                File exportedFile = generateExportFile(req)

                report.progressPercent = PROGRESS_SAVING
                report.currentStep = "Writing files to secure storage..."
                report.save(flush: true, failOnError: true)

                long duration = System.currentTimeMillis() - startTime

                // Guaranteed completion commit
                report.status = DataExportStatus.COMPLETED
                report.progressPercent = PROGRESS_COMPLETED
                report.currentStep = "Export completed successfully"
                report.executionTimeMs = duration

                if (exportedFile && exportedFile.exists()) {
                    if (req.isExportingZip()) {
                        report.zipFileName = exportedFile.name
                        report.zipFileSize = exportedFile.length()
                    } else {
                        //single file
                        report.datasetFileName = exportedFile.name
                        report.datasetFileSize = exportedFile.length()
                    }

                    if (req.exportedDatasetFilename) {
                        report.datasetFileName = req.exportedDatasetFilename
                        report.datasetFileSize = req.exportedDatasetFilesize
                    }
                    if (req.exportedCookbookFilename) {
                        report.codebookFileName = req.exportedCookbookFilename
                        report.codebookFileSize = req.exportedCookbookFilesize
                    }
                    if (req.exportedScriptFilename) {
                        report.scriptFileName = req.exportedScriptFilename
                        report.scriptFileSize = req.exportedScriptFilesize
                    }
                }

                report.save(flush: true, failOnError: true)
                println("Successfully completed async export job for report ${reportId} in ${duration}ms")
            } catch (Exception e) {
                e.printStackTrace()
                println("Async export processing failed for report ${reportId}: ${e.message}")
                transactionStatus.setRollbackOnly()
            }
        }

        DataExportReport reportCheck = DataExportReport.read(reportId)
        if (reportCheck && reportCheck.status != DataExportStatus.COMPLETED) {
            DataExportReport.withNewTransaction {
                DataExportReport errReport = DataExportReport.get(reportId)
                if (errReport && errReport.status != DataExportStatus.COMPLETED) {
                    errReport.status = DataExportStatus.FAILED
                    errReport.progressPercent = 0
                    errReport.currentStep = "Export failed"
                    errReport.errorMessage = "Export processing failed. Please check server logs."
                    errReport.save(flush: true)
                }
            }
        }
    }

    /**
     * Generates export into temporary file first, then copies to attachments docs directory
     */
    File generateExportFile(ExportRequest request) {
        String fileExt = getExportFileExtension(request)
        String fileName = "hds_${request.datasetName?.toLowerCase()}_${request.format?.code?.toLowerCase()}_${System.currentTimeMillis()}${fileExt}"

        File tempFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX)
        try {
            tempFile.withOutputStream { tempOut ->
                if (request.isExportingZip()) {
                    //must be a zip bundle
                    println "exporting zip media - ${fileName}"
                    exportToZipBundle(request, tempOut)
                } else {
                    if (request.format == DataExportFormat.EXCEL) {
                        exportToExcelStream(request, tempOut)
                        //exports also data dictionary if it includes data dictionary
                    } else if (request.format == DataExportFormat.CSV) {
                        exportToCsvStream(request, tempOut) //exports just the csv if not data dictionary included
                    } else {
                        exportToZipBundle(request, tempOut)
                    }
                }
            }

            File finalFile = new File(new File(SystemPath.externalDocsPath), fileName)

            Files.copy(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            println("Successfully generated export file in attachments docs directory: ${finalFile.absolutePath} (${finalFile.length()} bytes)")
            return finalFile
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }

    private void exportToCsvStream(ExportRequest request, OutputStream outputStream) {
        if (request.aiPromptMode && request.generatedSql) {
            exportAiPromptTableToCsvStream(request.generatedSql, request.sqlParamValues, request, outputStream)
            return
        }

        def sql = dataExportSqlQueryBuilderService.buildExportSql(request)

        println "csv sql:\n${sql.trim()}"
        println("csv cols: ${request?.selectedColumns?.join(',')}")

        exportTableToCsvStream(request.datasetName, null, sql, request, outputStream)
    }

    private void exportAiPromptTableToCsvStream(String sqlQuery, List<Object> params, ExportRequest request, OutputStream outputStream) {
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))
        writer.write(UTF8_BOM)

        dataExportNaturalLanguageSqlService.withSql { Sql sql ->
            List<String> headers = request.selectedColumns ?: []
            boolean headersWritten = false

            Closure rowHandler = { rawRow ->
                Map<String, Object> mapRow = new LinkedHashMap<>()
                if (rawRow instanceof GroovyRowResult || rawRow instanceof Map) {
                    ((Map) rawRow).entrySet().each { entry -> mapRow.put(entry.key.toString(), entry.value) }
                } else if (rawRow != null) {
                    def metaData = rawRow.getMetaData()
                    for (int i = 1; i <= metaData.columnCount; i++) {
                        String colName = metaData.getColumnLabel(i) ?: metaData.getColumnName(i)
                        mapRow.put(colName, rawRow.getObject(i))
                    }
                }

                if (!headersWritten) {
                    List<String> sqlOutputCols = mapRow.keySet().toList()
                    if (request.selectedColumns && !request.selectedColumns.isEmpty()) {
                        headers = request.selectedColumns.findAll { sqlOutputCols.contains(it) }
                        if (headers.isEmpty()) headers = sqlOutputCols
                    } else {
                        headers = sqlOutputCols
                    }
                    writer.write(headers.collect { escapeCsv(it) }.join(",") + "\n")
                    headersWritten = true
                }

                Map<String, Object> row = processRow(request.datasetName, mapRow, request)
                List<String> values = headers.collect { h -> escapeCsv(row.get(h)?.toString() ?: "") }
                writer.write(values.join(",") + "\n")
            }

            if (params && !params.isEmpty()) {
                sql.eachRow(sqlQuery, params, rowHandler)
            } else {
                sql.eachRow(sqlQuery, rowHandler)
            }
        }

        writer.flush()
    }

    private void exportTableToCsvStream(String tableName, DataDictionaryService.TableMetadata tableMetadata, String sql, ExportRequest request, OutputStream outputStream) {
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))
        // Write UTF-8 BOM to preserve Portuguese/French accents in Excel & Stata
        writer.write(UTF8_BOM)

        def session = sessionFactory.currentSession
        NativeQuery query = session.createNativeQuery(sql)
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)

        List<Map<String, Object>> resultList = query.list()
        if (resultList.isEmpty()) {
            writer.write("No records found\n")
            writer.flush()
            return
        }

        List<String> headers = extractHeadersForTable(tableName, tableMetadata, resultList.first(), request)
        writer.write(headers.collect { escapeCsv(it) }.join(",") + "\n")

        for (Map<String, Object> rawRow : resultList) {
            Map<String, Object> row = processRow(tableName, rawRow, request)
            List<String> values = headers.collect { h -> escapeCsv(row.get(h)?.toString() ?: "") }
            writer.write(values.join(",") + "\n")
        }

        writer.flush()
    }

    private void exportToExcelStream(ExportRequest request, OutputStream outputStream) {
        // SXSSFWorkbook streams rows to disk in chunks to prevent OutOfMemoryError
        SXSSFWorkbook workbook = new SXSSFWorkbook(EXCEL_STREAMING_ROW_FLUSH_SIZE)
        try {
            if (request.aiPromptMode && request.generatedSql) {
                writeAiPromptTableToExcelSheet(workbook, request.datasetName ?: "AI Export", request.generatedSql, request.sqlParamValues, request)
            } else {
                // Main Parent Sheet
                def mainTableSql = dataExportSqlQueryBuilderService.buildExportSql(request)
                writeTableToExcelSheet(workbook, request.datasetName, null, mainTableSql, request)

                // Child Repeat Tables Sheets (if present, breadth-first top-down order)
                if (request.hasRepeatTables) {
                    List<DataDictionaryService.TableMetadata> repeatTables = dataDictionaryService.getAllDescendantRepeatTables(request.datasetName)
                    repeatTables.each { childMeta ->
                        String childSql = dataExportSqlQueryBuilderService.buildChildTableSql(childMeta.tableName, childMeta.parentTableName, request)
                        writeTableToExcelSheet(workbook, childMeta.tableName, childMeta, childSql, request)
                    }
                }
            }

            // Optional Data Dictionary Tab
            if (request.includeDictionary) {
                appendDataDictionarySheet(workbook, request)
            }

            workbook.write(outputStream)
        } finally {
            workbook.dispose() // dispose of temporary files on disk
        }
    }

    private void writeAiPromptTableToExcelSheet(SXSSFWorkbook workbook, String sheetName, String sqlQuery, List<Object> params, ExportRequest request) {
        Sheet sheet = workbook.createSheet(sheetName ?: "AI Export")

        dataExportNaturalLanguageSqlService.withSql { Sql sql ->
            List<String> headers = request.selectedColumns ?: []
            CellStyle headerStyle = workbook.createCellStyle()
            Font font = workbook.createFont()
            font.setBold(true)
            headerStyle.setFont(font)

            boolean headersWritten = false
            int rowIndex = 0

            Closure rowHandler = { rawRow ->
                Map<String, Object> mapRow = new LinkedHashMap<>()
                if (rawRow instanceof GroovyRowResult || rawRow instanceof Map) {
                    ((Map) rawRow).entrySet().each { entry -> mapRow.put(entry.key.toString(), entry.value) }
                } else if (rawRow != null) {
                    def metaData = rawRow.getMetaData()
                    for (int i = 1; i <= metaData.columnCount; i++) {
                        String colName = metaData.getColumnLabel(i) ?: metaData.getColumnName(i)
                        mapRow.put(colName, rawRow.getObject(i))
                    }
                }

                if (!headersWritten) {
                    List<String> sqlOutputCols = mapRow.keySet().toList()
                    if (request.selectedColumns && !request.selectedColumns.isEmpty()) {
                        headers = request.selectedColumns.findAll { sqlOutputCols.contains(it) }
                        if (headers.isEmpty()) headers = sqlOutputCols
                    } else {
                        headers = sqlOutputCols
                    }
                    Row headerRow = sheet.createRow(0)
                    headers.eachWithIndex { h, idx ->
                        Cell cell = headerRow.createCell(idx)
                        cell.setCellValue(h)
                        cell.setCellStyle(headerStyle)
                    }
                    headersWritten = true
                    rowIndex = 1
                }

                Map<String, Object> row = processRow(sheetName, mapRow, request)
                Row dataRow = sheet.createRow(rowIndex++)
                headers.eachWithIndex { h, cIdx ->
                    Object val = row.get(h)
                    Cell cell = dataRow.createCell(cIdx)
                    if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue())
                    } else {
                        cell.setCellValue(val?.toString() ?: "")
                    }
                }
            }

            if (params && !params.isEmpty()) {
                sql.eachRow(sqlQuery, params, rowHandler)
            } else {
                sql.eachRow(sqlQuery, rowHandler)
            }
        }
    }

    private void writeTableToExcelSheet(SXSSFWorkbook workbook, String sheetName, DataDictionaryService.TableMetadata tableMetadata, String sql, ExportRequest request) {
        Sheet sheet = workbook.createSheet(sheetName)

        def session = sessionFactory.currentSession
        NativeQuery query = session.createNativeQuery(sql)
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)

        List<Map<String, Object>> resultList = query.list()

        if (!resultList.isEmpty()) {
            List<String> headers = extractHeadersForTable(sheetName, tableMetadata, resultList.first(), request)

            // Header Row
            Row headerRow = sheet.createRow(0)
            CellStyle headerStyle = workbook.createCellStyle()
            Font font = workbook.createFont()
            font.setBold(true)
            headerStyle.setFont(font)

            headers.eachWithIndex { h, idx ->
                Cell cell = headerRow.createCell(idx)
                cell.setCellValue(h)
                cell.setCellStyle(headerStyle)
            }

            // Data Rows
            resultList.eachWithIndex { rawRow, rIdx ->
                Map<String, Object> row = processRow(sheetName, rawRow, request)
                Row dataRow = sheet.createRow(rIdx + 1)
                headers.eachWithIndex { h, cIdx ->
                    Object val = row.get(h)
                    Cell cell = dataRow.createCell(cIdx)
                    if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue())
                    } else {
                        cell.setCellValue(val?.toString() ?: "")
                    }
                }
            }
        }
    }

    private void exportToZipBundle(ExportRequest request, OutputStream outputStream) {
        ZipOutputStream zipOut = new ZipOutputStream(outputStream)

        // 1. Generate Main Data File (Excel or CSV)
        ByteArrayOutputStream dataOut = new ByteArrayOutputStream()
        if (request.format == DataExportFormat.EXCEL) {
            exportToExcelStream(request, dataOut)
        } else {
            exportToCsvStream(request, dataOut)
        }
        byte[] dataBytes = dataOut.toByteArray()
        def dataFilenameExtension = (request.format == DataExportFormat.EXCEL) ? "xlsx" : "csv"
        def baseName = "${request.datasetName?.toLowerCase()}"
        def dataFilename = "${baseName}_data.${dataFilenameExtension}"
        def mediaFolder = "media${File.separator}"

        zipOut.putNextEntry(new ZipEntry(dataFilename))
        zipOut.write(dataBytes)
        zipOut.closeEntry()

        request.exportedDatasetFilename = dataFilename
        request.exportedDatasetFilesize = dataBytes.length

        // 1b. Export Child Repeat Tables to ZIP (for CSV and non-Excel formats)
        if (request.hasRepeatTables && request.format != DataExportFormat.EXCEL) {
            List<DataDictionaryService.TableMetadata> repeatTables = dataDictionaryService.getAllDescendantRepeatTables(request.datasetName)
            repeatTables.each { childMeta ->
                ByteArrayOutputStream childOut = new ByteArrayOutputStream()
                String childSql = dataExportSqlQueryBuilderService.buildChildTableSql(childMeta.tableName, childMeta.parentTableName, request)
                exportTableToCsvStream(childMeta.tableName, childMeta, childSql, request, childOut)

                println "child sql:\n${childSql}"

                byte[] childBytes = childOut.toByteArray()
                String childFilename = "${childMeta.tableName.toLowerCase()}_data.csv"

                zipOut.putNextEntry(new ZipEntry(childFilename))
                zipOut.write(childBytes)
                zipOut.closeEntry()
            }
        }

        // 2. Generate Syntax Import Script (Single Master Script if repeat tables exist)
        if (dataExportScriptsBuilderService.isSyntaxScriptFormat(request.format)) { // SCRIPTS ARE ONLY GENERATED FOR STATA/SPSS/R/SAS
            String scriptExt = dataExportScriptsBuilderService.getScriptExtension(request.format)
            String scriptContent = dataExportScriptsBuilderService.generateSyntaxScript(request)
            String scriptFilename = "import_${baseName}${scriptExt}"

            byte[] scriptBytes = scriptContent.getBytes("UTF-8")
            zipOut.putNextEntry(new ZipEntry(scriptFilename))
            zipOut.write(scriptBytes)
            zipOut.closeEntry()

            request.exportedScriptFilename = scriptFilename
            request.exportedScriptFilesize = scriptBytes.length
        }

        // 3. Generate Data Dictionary Excel
        if (request.includeDictionary && request.format != DataExportFormat.EXCEL) {
            ByteArrayOutputStream dictOut = new ByteArrayOutputStream()
            SXSSFWorkbook dictWorkbook = new SXSSFWorkbook(EXCEL_STREAMING_ROW_FLUSH_SIZE)
            appendDataDictionarySheet(dictWorkbook, request)
            dictWorkbook.write(dictOut)
            dictWorkbook.dispose()

            byte[] dictBytes = dictOut.toByteArray()
            zipOut.putNextEntry(new ZipEntry(DICTIONARY_EXCEL_FILENAME))
            zipOut.write(dictBytes)
            zipOut.closeEntry()

            request.exportedCookbookFilename = DICTIONARY_EXCEL_FILENAME
            request.exportedCookbookFilesize = dictBytes.length
        }

        // 4. Add Media Files to zip bundle
        if (request.includeMediaFiles && !request.exportedMediaFiles.empty) {
            request.exportedMediaFiles.each { mediaFile ->
                zipOut.putNextEntry(new ZipEntry(mediaFolder + mediaFile.getName()))
                zipOut.write(mediaFile.bytes)
                zipOut.closeEntry()
            }

        }

        zipOut.finish()
    }

    private void appendDataDictionarySheet(Workbook workbook, ExportRequest request) {
        Sheet dictSheet = workbook.createSheet("Data Dictionary")
        trackAllColumnsForAutoResize(dictSheet)
        Row headerRow = dictSheet.createRow(0)
        CellStyle headerStyle = workbook.createCellStyle()
        Font font = workbook.createFont()
        font.setBold(true)
        headerStyle.setFont(font)

        String[] headers = ["Table", "Column Name", "Data Type", "Sensitive Type", "Label", "Description", "Category Choices"]
        headers.eachWithIndex { h, i ->
            Cell c = headerRow.createCell(i)
            c.setCellValue(h)
            c.setCellStyle(headerStyle)
        }

        List<DataDictionaryService.TableMetadata> targetMetas = []
        if (request.aiPromptMode && request.generatedSql) {
            List<Map<String, Object>> extractedCols = dataExportNaturalLanguageSqlService.extractSqlColumnMetadata(request.generatedSql, request.sqlParamValues)
            DataDictionaryService.TableMetadata aiMeta = dataDictionaryService.buildAiExportTableMetadata(extractedCols)
            targetMetas = [aiMeta]
        } else {
            DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(request.datasetName)
            if (meta) targetMetas.add(meta)
            if (request.hasRepeatTables) {
                List<DataDictionaryService.TableMetadata> descendants = dataDictionaryService.getAllDescendantRepeatTables(request.datasetName)
                if (descendants) targetMetas.addAll(descendants)
            }
        }

        int rowIndex = 1
        targetMetas.each { tMeta ->
            if (tMeta) {
                tMeta.columns.values().each { colMeta ->
                    if (request.selectedColumns && !request.selectedColumns.contains(colMeta.columnName)) {
                        return
                    }

                    Row r = dictSheet.createRow(rowIndex++)
                    r.createCell(0).setCellValue(colMeta.tableName)
                    r.createCell(1).setCellValue(colMeta.columnName)
                    r.createCell(2).setCellValue(colMeta.dataType)
                    r.createCell(3).setCellValue(colMeta.sensitiveType?.code ?: "not_applicable")
                    r.createCell(4).setCellValue(colMeta.label ?: "")
                    r.createCell(5).setCellValue(colMeta.description ?: "")

                    if (colMeta.enumTypeName) {
                        DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                        if (enumType) {
                            String choices = enumType.options.findAll { it.code }.collect { "${it.code}=${it.label}" }.join("; ")
                            r.createCell(6).setCellValue(choices)
                        }
                    }
                }
            }
        }

        // Auto-resize all columns after all rows are written
        headers.eachWithIndex { h, i ->
            dictSheet.autoSizeColumn(i)
        }
    }

    // --- Row Processing ---

    private Map<String, Object> processRow(Map<String, Object> rawRow, ExportRequest request) {
        return processRow(request.datasetName, rawRow, request)
    }

    private Map<String, Object> processRow(String tableName, Map<String, Object> rawRow, ExportRequest request) {
        // Collect primary key 'id' into exportedTableIdsMap for repeat table filtering
        Object rowId = rawRow.get("id") ?: rawRow.get("ID")
        if (rowId != null) {
            String idStr = rowId.toString().trim()
            if (!idStr.isEmpty()) {
                request.exportedTableIdsMap.computeIfAbsent(tableName) { [] }.add(idStr)
            }
        }

        Map<String, Object> processed = new LinkedHashMap<>()

        rawRow.each { k, val ->
            String dbColName = k.toString()
            //println "origColName ${tableName}.${dbColName}"
            String colName = dbColName.toLowerCase()
            Object value = val

            if (value) {

                DataDictionaryService.ColumnMetadata colMeta = dataDictionaryService.getColumnMetadata(tableName, dbColName)
                def anonyResult = dataExportAnonymizationService.anonymizeValue(request, colMeta, value)
                value = anonyResult?.value

                if (anonyResult == null || anonyResult.skipColumn) {
                    return
                }

                // Clean unlocalized i18n keys from data values
                if (value != null && (value.toString() == "member.unknown.label" || value.toString() == "member.unknown")) {
                    value = generalUtilitiesService.getMessage("member.unknown.label", "")
                }

                // Save media files
                if (request.includeMediaFiles && colMeta?.isMediaColumn && value != null) {
                    def mediaFile = new File(SystemPath.externalDocsPath, value.toString())
                    println "file ${value.toString()} - exists = ${mediaFile.exists()}"
                    if (mediaFile.exists()) {
                        request.exportedMediaFiles.add(mediaFile)
                    }
                }
            }

            processed.put(k, value)
        }

        return processed
    }


    // --- Helper Utilities ---

    private static void trackAllColumnsForAutoResize(Sheet dictSheet) {
        if (dictSheet instanceof SXSSFSheet) {
            def sh = ((SXSSFSheet) dictSheet)
            sh.trackAllColumnsForAutoSizing()
        }
    }

    private static String escapeCsv(String val) {
        if (val == null) return ""
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\""
        }
        return val
    }

    private static List<String> extractHeadersForTable(String tableName, DataDictionaryService.TableMetadata tableMetadata, Map<String, Object> firstRow, ExportRequest request) {
        if (!firstRow) return []

        def selectedColumns = tableMetadata?.isRepeatTable ? tableMetadata.columnNames : request.selectedColumns

        if (selectedColumns && !selectedColumns.isEmpty()) {
            Map<String, String> rowKeysLowerMap = firstRow.keySet().collectEntries { [it.toLowerCase(), it] }
            List<String> matchedHeaders = []
            for (String userCol : selectedColumns) {
                String matchedKey = rowKeysLowerMap.get(userCol.toLowerCase())
                if (matchedKey) {
                    matchedHeaders.add(matchedKey)
                }
            }
            if (!matchedHeaders.isEmpty()) {
                return matchedHeaders
            }
        }
        return new ArrayList<>(firstRow.keySet())
    }

    private static String getExportFileExtension(ExportRequest request) {
        DataExportFormat format = request.format
        if (request.isExportingZip()) return ".zip"
        if (format == DataExportFormat.EXCEL) return ".xlsx"
        if (format == DataExportFormat.CSV) return ".csv"
        return ".zip"
    }

}
