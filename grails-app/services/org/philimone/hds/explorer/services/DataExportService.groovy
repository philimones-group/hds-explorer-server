package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
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
import org.philimone.hds.explorer.server.model.enums.DateAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.GpsAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.NameAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.PhoneAnonymizationMode
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.settings.DataExportReport
import org.philimone.hds.forms.model.enums.SensitiveType

import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Transactional(readOnly = true)
class DataExportService {

    DataDictionaryService dataDictionaryService
    GeneralUtilitiesService generalUtilitiesService
    SessionFactory sessionFactory

    // --- Configuration Constants ---
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
    private static final int PREVIEW_MAX_ROWS = 20
    private static final int EXCEL_STREAMING_ROW_FLUSH_SIZE = 100
    private static final int DATE_SHIFT_OFFSET_DAYS = 7
    private static final long DATE_SHIFT_OFFSET_MS = DATE_SHIFT_OFFSET_DAYS * 86400000L

    // --- PII Masking Constants ---
    private static final String PHONE_MASK_TEMPLATE = "***-***-****"
    private static final String NAME_MASK_SUFFIX = "***"

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

        String sql = buildExportSql(request)
        def session = sessionFactory.currentSession
        NativeQuery query = session.createNativeQuery(sql)
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)
        query.setMaxResults(PREVIEW_MAX_ROWS)

        List<Map<String, Object>> resultList = query.list()
        for (Map<String, Object> rawRow : resultList) {
            rows.add(processAndAnonymizeRow(rawRow, request))
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
                log.info("Successfully completed async export job for report ${reportId} in ${duration}ms")
            } catch (Exception e) {
                log.error("Async export processing failed for report ${reportId}: ${e.message}", e)
                transactionStatus.setRollbackOnly()

                DataExportReport.withNewTransaction {
                    DataExportReport errReport = DataExportReport.get(reportId)
                    if (errReport) {
                        errReport.status = DataExportStatus.FAILED
                        errReport.progressPercent = 0
                        errReport.currentStep = "Export failed"
                        errReport.errorMessage = e.message
                        errReport.save(flush: true)
                    }
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

            File attachmentsDir = getExportAttachmentsDir()
            File finalFile = new File(attachmentsDir, fileName)

            Files.copy(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            log.info("Successfully generated export file in attachments docs directory: ${finalFile.absolutePath} (${finalFile.length()} bytes)")
            return finalFile
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }

    File getExportAttachmentsDir() {
        File dir = new File(SystemPath.externalDocsPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private void exportToCsvStream(ExportRequest request, OutputStream outputStream) {
        def sql = buildExportSql(request)

        println "csv sql:\n${sql.trim()}"
        println("csv cols: ${request?.selectedColumns?.join(',')}")

        exportTableToCsvStream(request.datasetName, null, sql, request, outputStream)
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
            Map<String, Object> row = processAndAnonymizeRow(tableName, rawRow, request)
            List<String> values = headers.collect { h -> escapeCsv(row.get(h)?.toString() ?: "") }
            writer.write(values.join(",") + "\n")
        }

        writer.flush()
    }

    private void exportToExcelStream(ExportRequest request, OutputStream outputStream) {
        // SXSSFWorkbook streams rows to disk in chunks to prevent OutOfMemoryError
        SXSSFWorkbook workbook = new SXSSFWorkbook(EXCEL_STREAMING_ROW_FLUSH_SIZE)
        try {
            // Main Parent Sheet
            def mainTableSql = buildExportSql(request)
            writeTableToExcelSheet(workbook, request.datasetName, null, mainTableSql, request)

            // Child Repeat Tables Sheets (if present, breadth-first top-down order)
            if (request.hasRepeatTables) {
                List<DataDictionaryService.TableMetadata> repeatTables = dataDictionaryService.getAllDescendantRepeatTables(request.datasetName)
                repeatTables.each { childMeta ->
                    String childSql = buildChildTableSql(childMeta.tableName, childMeta.parentTableName, request)
                    writeTableToExcelSheet(workbook, childMeta.tableName, childMeta, childSql, request)
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
                Map<String, Object> row = processAndAnonymizeRow(sheetName, rawRow, request)
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
                String childSql = buildChildTableSql(childMeta.tableName, childMeta.parentTableName, request)
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
        if (isSyntaxScriptFormat(request.format)) { // SCRIPTS ARE ONLY GENERATED FOR STATA/SPSS/R/SAS
            String scriptExt = getScriptExtension(request.format)
            String scriptContent = generateSyntaxScript(request)
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

    static boolean isSyntaxScriptFormat(DataExportFormat format) {
        return format==DataExportFormat.R || format==DataExportFormat.SAS  || format==DataExportFormat.SPSS || format==DataExportFormat.STATA
    }

    // --- SQL Query Builders ---

    String buildExportSql(ExportRequest request) {
        def ds = DataExportItem.getFrom(request.datasetName)

        if (ds == DataExportItem.RESIDENCY_PERSON_TIME) {
            return buildPersonTimeSql(request)
        } else if (ds == DataExportItem.MATERNAL_PREGNANCY) {
            return buildMaternalPregnancySql(request)
        } else if (ds == DataExportItem.HOUSEHOLD_COMPOSITION) {
            return buildHouseholdCompositionSql(request)
        } else if (ds == DataExportItem.INDEPTH_RESIDENCY_PERSON_TIME) {
            return buildIndepthCoreResidencySql(request)
        } else if (ds == DataExportItem.INDEPTH_MATERNAL_PREGNANCY) {
            return buildIndepthMaternalPregnancySql(request)
        } else if (ds == DataExportItem.INDEPTH_HOUSEHOLD_COMPOSITION) {
            return buildIndepthHouseholdCompositionSql(request)
        } else {
            return buildRegularTableSql(request)
        }
    }

    private String buildRegionFilterClause(String regionCode, String householdAlias = "h") {
        if (!regionCode || regionCode.trim().isEmpty()) return ""

        Region reg = Region.findByCode(regionCode.trim())
        if (!reg) return ""

        String colName = reg.hierarchyLevel ? reg.hierarchyLevel.code : "hierarchy1"
        return " AND ${householdAlias}.${colName} = '${reg.code}'"
    }

    private String buildRegionCteSql(String tableName, String regionCode, String regionColumnName = "region_code") {
        if (!regionCode || regionCode.trim().isEmpty()) {
            return "SELECT t.* FROM `${tableName}` t WHERE 1=1"
        }

        String code = regionCode.trim()
        return """
            WITH RECURSIVE region_tree AS (
                SELECT code FROM region WHERE code = '${code}'
                UNION ALL
                SELECT r.code FROM region r INNER JOIN region_tree rt ON r.parent_region_code = rt.code
            )
            SELECT t.* 
            FROM `${tableName}` t
            WHERE 1=1 AND t.${regionColumnName} IN (SELECT code FROM region_tree)
        """
    }

    private String buildRegularTableSql(ExportRequest request) {
        String table = request.datasetName ? request.datasetName : "member"
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder()

        // 1. MEMBER & INDIVIDUAL COHORT Group (Group 1: Full Individual Cohort)
        if (table == "member" || table == "member_ext") {
            String fromClause = (table == "member") ? "FROM member m" : "FROM `${table}` t JOIN member m ON m.code = t.member_code"
            String selectCols = (table == "member") ? "SELECT m.*" : "SELECT t.*"

            sql = new StringBuilder("""
                ${selectCols}, 
                       r.household_id AS current_household_id, 
                       r.household_code AS current_household_code,
                       r.start_type AS current_residency_start_type,
                       r.start_date AS current_residency_start_date,
                       r.end_type AS current_residency_end_type,
                       r.end_date AS current_residency_end_date,
                       hr.relationship_type AS current_head_relationship_type,
                       TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) AS age_at_ref_date
                ${fromClause}
                LEFT JOIN residency r ON r.member_id = m.id 
                     AND r.start_date = (SELECT MAX(r2.start_date) FROM residency r2 WHERE r2.member_id = m.id AND (r2.status IS NULL OR r2.status <> 2))
                     AND (r.status IS NULL OR r.status <> 2)
                LEFT JOIN head_relationship hr ON hr.member_id = m.id 
                     AND hr.start_date = (SELECT MAX(hr2.start_date) FROM head_relationship hr2 WHERE hr2.member_id = m.id AND (hr2.status IS NULL OR hr2.status <> 2))
                     AND (hr.status IS NULL OR hr.status <> 2)
                LEFT JOIN household h ON h.id = r.household_id
                WHERE 1=1
            """)

            if (request.referenceDate) {
                if (request.activeResidentsOnly) {
                    sql.append(" AND r.start_date <= ${refDateStr} AND (r.end_type = 'NA' OR r.end_date IS NULL OR r.end_date >= ${refDateStr})")
                } else {
                    sql.append(" AND (r.start_date IS NULL OR r.start_date <= ${refDateStr})")
                }
            } else if (request.activeResidentsOnly) {
                sql.append(" AND (r.end_type = 'NA' OR r.end_date IS NULL)")
            }
            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 1B. RESIDENCY Table
        else if (table == "residency") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM residency t
                JOIN member m ON m.id = t.member_id
                LEFT JOIN household h ON h.id = t.household_id
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                if (request.activeResidentsOnly) {
                    sql.append(" AND t.start_date <= ${refDateStr} AND (t.end_type = 'NA' OR t.end_date IS NULL OR t.end_date >= ${refDateStr})")
                } else {
                    sql.append(" AND t.start_date <= ${refDateStr}")
                }
            } else if (request.activeResidentsOnly) {
                sql.append(" AND (t.end_type = 'NA' OR t.end_date IS NULL)")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 1C. HEAD_RELATIONSHIP Table
        else if (table == "head_relationship") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM head_relationship t
                JOIN member m ON m.id = t.member_id
                LEFT JOIN household h ON h.id = t.household_id
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                if (request.activeResidentsOnly) {
                    sql.append(" AND t.start_date <= ${refDateStr} AND (t.end_type = 'NA' OR t.end_date IS NULL OR t.end_date >= ${refDateStr})")
                } else {
                    sql.append(" AND t.start_date <= ${refDateStr}")
                }
            } else if (request.activeResidentsOnly) {
                sql.append(" AND (t.end_type = 'NA' OR t.end_date IS NULL)")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 1D. MARITAL_RELATIONSHIP Table
        else if (table == "marital_relationship") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM marital_relationship t
                LEFT JOIN member mA ON mA.id = t.member_a_id
                LEFT JOIN member mB ON mB.id = t.member_b_id
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND (mA.gender = '${request.gender}' OR mB.gender = '${request.gender}')")
            }
            if (request.ageMin != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, ${refDateStr}) >= ${request.ageMin} OR TIMESTAMPDIFF(YEAR, mB.dob, ${refDateStr}) >= ${request.ageMin})")
            }
            if (request.ageMax != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, ${refDateStr}) <= ${request.ageMax} OR TIMESTAMPDIFF(YEAR, mB.dob, ${refDateStr}) <= ${request.ageMax})")
            }
            if (request.referenceDate) {
                if (request.activeResidentsOnly) {
                    sql.append(" AND t.start_date <= ${refDateStr} AND (t.end_status IS NULL OR t.end_status = 'NA' OR t.end_date IS NULL OR t.end_date >= ${refDateStr})")
                } else {
                    sql.append(" AND t.start_date <= ${refDateStr}")
                }
            } else if (request.activeResidentsOnly) {
                sql.append(" AND (t.end_status IS NULL OR t.end_status = 'NA' OR t.end_date IS NULL)")
            }
            if (request.regionCode) {
                Region reg = Region.findByCode(request.regionCode.trim())
                if (reg) {
                    String colName = reg.hierarchyLevel ? reg.hierarchyLevel.code : "hierarchy1"
                    sql.append(""" AND EXISTS (
                        SELECT 1 FROM residency r 
                        JOIN household h ON h.id = r.household_id 
                        WHERE (r.member_id = mA.id OR r.member_id = mB.id) 
                          AND r.start_date = (SELECT MAX(r2.start_date) FROM residency r2 WHERE r2.member_id = r.member_id AND (r2.status IS NULL OR r2.status <> 2))
                          AND (r.status IS NULL OR r.status <> 2)
                          AND h.${colName} = '${reg.code}'
                    )""")
                }
            }
        }
        // 1E. MARITAL_RELATIONSHIP_EXT Form
        else if (table == "marital_relationship_ext") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM marital_relationship_ext t
                LEFT JOIN member mA ON mA.code = t.member_a
                LEFT JOIN member mB ON mB.code = t.member_b
                LEFT JOIN visit v ON v.code = t.visit_code
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND (mA.gender = '${request.gender}' OR mB.gender = '${request.gender}')")
            }
            if (request.ageMin != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, ${refDateStr}) >= ${request.ageMin} OR TIMESTAMPDIFF(YEAR, mB.dob, ${refDateStr}) >= ${request.ageMin})")
            }
            if (request.ageMax != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, ${refDateStr}) <= ${request.ageMax} OR TIMESTAMPDIFF(YEAR, mB.dob, ${refDateStr}) <= ${request.ageMax})")
            }
            if (request.referenceDate) {
                sql.append(" AND v.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 2. HOUSEHOLD STRUCTURAL Group (Group 2: Household Unit & Extensions)
        else if (table == "household" || table.startsWith("household_") || table == "change_head_ext") {
            String hhJoinCol = (table.startsWith("household_relocation")) ? "origin_code" : "household_code"
            String fromClause = (table == "household") ? "FROM household h" : "FROM `${table}` t JOIN household h ON h.code = t.${hhJoinCol}"
            String selectCols = (table == "household") ? "SELECT h.*" : "SELECT t.*"

            sql = new StringBuilder("""
                ${selectCols}
                ${fromClause}
                WHERE 1=1
            """)

            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 3. VISIT & FIELD ENCOUNTER Group (Group 3: Encounters & Extensions)
        else if (table == "visit" || table.startsWith("visit_") || table.startsWith("incomplete_visit")) {
            String fromClause = (table == "visit") ? "FROM visit v" : "FROM `${table}` t JOIN visit v ON v.code = t.visit_code"
            String selectCols = (table == "visit") ? "SELECT v.*" : "SELECT t.*"

            sql = new StringBuilder("""
                ${selectCols}
                ${fromClause}
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.referenceDate) {
                sql.append(" AND v.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 4. HISTORICAL EVENT LOGS Group (Group 4: Events - Death, Migrations, Enumeration)
        else if (table.startsWith("death") || table.contains("migration") || table == "enumeration") {
            boolean isExt = table.endsWith("_ext")
            String dateCol = isExt ? "v.visit_date" : "t." + ((table.startsWith("death")) ? "death_date" : ((table.contains("migration")) ? "migration_date" : "event_date"))
            String memberJoin = isExt ? "JOIN member m ON m.code = t.member_code" : "JOIN member m ON m.id = t.member_id"
            String visitJoin = isExt ? "LEFT JOIN visit v ON v.code = t.visit_code" : "LEFT JOIN visit v ON v.id = t.visit_id"
            String hhJoin = (table == "enumeration") ? "LEFT JOIN household h ON h.id = t.household_id" : "LEFT JOIN household h ON h.code = v.household_code"

            sql = new StringBuilder("""
                SELECT t.*
                FROM `${table}` t
                ${memberJoin}
                ${visitJoin}
                ${hhJoin}
                WHERE 1=1
            """)

            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND ${dateCol} <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 5. REPRODUCTIVE & MATERNAL SURVEILLANCE Group (Group 5: Pregnancy & Extensions)
        else if (table == "pregnancy_child") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM `pregnancy_child` t
                JOIN pregnancy_outcome po ON po.id = t.pregnancy_outcome_id
                JOIN member mother ON mother.id = po.mother_id
                LEFT JOIN visit v ON v.id = po.visit_id
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND po.outcome_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        else if (table == "pregnancy_child_ext") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM `pregnancy_child_ext` t
                JOIN pregnancy_outcome_ext poe ON poe.id = t.pregnancy_outcome_ext_id
                JOIN member mother ON mother.code = poe.mother_code
                LEFT JOIN visit v ON v.code = poe.visit_code
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND v.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        else if (table == "pregnancy_visit_child") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM `pregnancy_visit_child` t
                JOIN pregnancy_visit pv ON pv.id = t.pregnancy_visit_id
                JOIN member mother ON mother.id = pv.mother_id
                LEFT JOIN visit v ON v.id = pv.visit_id
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND pv.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        else if (table == "pregnancy_visit_child_ext") {
            sql = new StringBuilder("""
                SELECT t.*
                FROM `pregnancy_visit_child_ext` t
                JOIN pregnancy_visit_ext pve ON pve.id = t.pregnancy_visit_ext_id
                JOIN member mother ON mother.code = pve.mother_code
                LEFT JOIN visit v ON v.code = pve.visit_code
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND v.visit_date <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        else if (table.startsWith("pregnancy_")) {
            boolean isExt = table.endsWith("_ext")
            String motherJoin = isExt ? "JOIN member mother ON mother.code = t.mother_code" : "JOIN member mother ON mother.id = t.mother_id"
            String visitJoin = isExt ? "LEFT JOIN visit v ON v.code = t.visit_code" : "LEFT JOIN visit v ON v.id = t.visit_id"
            String dateCol = isExt ? "v.visit_date" : ((table.startsWith("pregnancy_outcome")) ? "t.outcome_date" : ((table.startsWith("pregnancy_visit")) ? "t.visit_date" : "t.recorded_date"))

            sql = new StringBuilder("""
                SELECT t.*
                FROM `${table}` t
                ${motherJoin}
                ${visitJoin}
                LEFT JOIN household h ON h.code = v.household_code
                WHERE 1=1
            """)

            if (request.ageMin != null && dateCol) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) >= ${request.ageMin}")
            }
            if (request.ageMax != null && dateCol) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${refDateStr}) <= ${request.ageMax}")
            }
            if (request.referenceDate && dateCol) {
                sql.append(" AND ${dateCol} <= ${refDateStr}")
            }
            if (request.regionCode) {
                sql.append(buildRegionFilterClause(request.regionCode, "h"))
            }
        }
        // 6. REGION & ADMINISTRATIVE MASTER Group
        else if (table == "region" || table == "region_ext") {
            String colName = (table == "region") ? "code" : "region_code"
            if (request.regionCode) {
                sql = new StringBuilder(buildRegionCteSql(table, request.regionCode, colName))
            } else {
                sql = new StringBuilder("SELECT t.* FROM `${table}` t WHERE 1=1")
            }
        }
        else if (table == "region_head_relationship" || table == "change_region_head_ext") {
            if (request.regionCode) {
                sql = new StringBuilder(buildRegionCteSql(table, request.regionCode, "region_code"))
            } else {
                sql = new StringBuilder("SELECT t.* FROM `${table}` t WHERE 1=1")
            }
        }
        else if (table == "round") {
            sql = new StringBuilder("SELECT t.* FROM `round` t WHERE 1=1")
            if (request.referenceDate) {
                sql.append(" AND t.start_date <= ${refDateStr}")
            }
        }
        // 7. GENERIC FALLBACK FOR ALL OTHER SYSTEM TABLES (_user, etc.)
        else {
            sql = new StringBuilder("SELECT t.* FROM `${table}` t WHERE 1=1")
        }

        // Global Native SQL Random Sub-Sampling
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        return sql.toString()
    }

    private String buildIndepthCoreResidencySql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder filterWhere = new StringBuilder(" WHERE (r.status IS NULL OR r.status <> 2)")
        String filterRand = ""

        if (request.referenceDate) {
            filterWhere.append(" AND r.start_date <= ${refDateStr} AND (r.end_date IS NULL OR r.end_date >= ${refDateStr})")
        }
        if (request.gender && request.gender != "ALL") {
            filterWhere.append(" AND m.gender = '${request.gender}'")
        }
        if (request.ageMin != null) {
            filterWhere.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            filterWhere.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
        }
        if (request.regionCode) {
            filterWhere.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            filterRand = " WHERE RAND() <= (${request.randomSamplePercent} / 100.0)"
        }

        String sql = """
            SELECT * FROM (
                SELECT m.code AS IndividualId,
                       r.household_code AS LocationId,
                       m.dob AS DoB,
                       m.gender AS Sex,
                       CASE r.start_type
                           WHEN 'ENU' THEN 'ENU'
                           WHEN 'BIR' THEN 'BTH'
                           WHEN 'XEN' THEN 'IMG'
                           WHEN 'ENT' THEN 'ENT'
                           ELSE r.start_type
                       END AS EventCode,
                       r.start_date AS EventDate,
                       1 AS residence,
                       TIMESTAMPDIFF(YEAR, m.dob, r.start_date) AS age_at_event,
                       reg.name AS region_name,
                       reg.code AS region_code
                FROM residency r
                JOIN member m ON m.id = r.member_id
                JOIN household h ON h.id = r.household_id
                LEFT JOIN region reg ON reg.code = h.region
                ${filterWhere.toString()}

                UNION ALL

                SELECT m.code AS IndividualId,
                       r.household_code AS LocationId,
                       m.dob AS DoB,
                       m.gender AS Sex,
                       CASE 
                           WHEN r.end_type = 'CHG' THEN 'EXT'
                           WHEN r.end_type = 'EXT' THEN 'OMG'
                           WHEN r.end_type = 'DTH' THEN 'DTH'
                           WHEN r.end_type = 'NA' OR r.end_date IS NULL THEN 'OBE'
                           ELSE COALESCE(r.end_type, 'OBE')
                       END AS EventCode,
                       COALESCE(r.end_date, ${refDateStr}) AS EventDate,
                       IF(r.end_type = 'NA' OR r.end_date IS NULL, 1, 0) AS residence,
                       TIMESTAMPDIFF(YEAR, m.dob, COALESCE(r.end_date, ${refDateStr})) AS age_at_event,
                       reg.name AS region_name,
                       reg.code AS region_code
                FROM residency r
                JOIN member m ON m.id = r.member_id
                JOIN household h ON h.id = r.household_id
                LEFT JOIN region reg ON reg.code = h.region
                ${filterWhere.toString()}
            ) eha
            ${filterRand}
            ORDER BY eha.IndividualId, eha.EventDate ASC
        """

        return sql
    }

    private String buildPersonTimeSql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT m.code AS member_code, 
                   m.name AS member_name, 
                   m.gender AS member_gender, 
                   m.dob AS member_dob,
                   TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) AS member_age_at_ref_date,
                   r.household_code, 
                   h.name AS household_name,
                   r.start_type, 
                   r.start_date, 
                   COALESCE(r.end_type, 'OBE') AS end_type, 
                   COALESCE(r.end_date, ${refDateStr}) AS end_date,
                   DATEDIFF(COALESCE(r.end_date, ${refDateStr}), r.start_date) AS exposure_days,
                   ROUND(DATEDIFF(COALESCE(r.end_date, ${refDateStr}), r.start_date) / 365.25, 3) AS person_years,
                   reg.code AS region_code,
                   reg.name AS region_name
            FROM residency r
            JOIN member m ON m.id = r.member_id
            JOIN household h ON h.id = r.household_id
            LEFT JOIN region reg ON reg.code = h.region
            WHERE (r.status IS NULL OR r.status <> 2)
        """)

        if (request.referenceDate) {
            sql.append(" AND r.start_date <= ${refDateStr} AND (r.end_date IS NULL OR r.end_date >= ${refDateStr})")
        }
        if (request.gender && request.gender != "ALL") {
            sql.append(" AND m.gender = '${request.gender}'")
        }
        if (request.ageMin != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
        }
        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        return sql.toString()
    }

    private String buildIndepthMaternalPregnancySql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT m.code AS IndividualId,
                   f.code AS FatherId,
                   child.code AS ChildId,
                   'DLV' AS EventCode,
                   po.outcome_date AS EventDate,
                   v.visit_date AS ObservationDate,
                   child.gender AS ChildSex,
                   po.number_of_outcomes AS MultiBirth,
                   po.number_of_livebirths AS LiveBirths,
                   (po.number_of_outcomes - po.number_of_livebirths) AS StillBirths,
                   h.code AS LocationId,
                   m.name AS mother_name,
                   f.name AS father_name,
                   child.name AS child_name,
                   pc.outcome_type AS child_outcome_type,
                   TIMESTAMPDIFF(YEAR, m.dob, po.outcome_date) AS mother_age_at_outcome,
                   reg.name AS region_name
            FROM pregnancy_outcome po
            JOIN member m ON m.id = po.mother_id
            LEFT JOIN member f ON f.id = po.father_id
            JOIN pregnancy_child pc ON pc.pregnancy_outcome_id = po.id
            LEFT JOIN member child ON child.id = pc.child_id
            LEFT JOIN visit v ON v.id = po.visit_id
            LEFT JOIN household h ON h.code = v.household_code
            LEFT JOIN region reg ON reg.code = h.region
            WHERE 1=1
        """)

        if (request.ageMin != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
        }
        if (request.referenceDate) {
            sql.append(" AND po.outcome_date <= ${refDateStr}")
        }
        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        return sql.toString()
    }

    private String buildMaternalPregnancySql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT po.code AS pregnancy_code, 
                   po.outcome_date,                   
                   m.code AS mother_code, 
                   m.name AS mother_name, 
                   TIMESTAMPDIFF(YEAR, m.dob, po.outcome_date) AS mother_age_at_outcome,
                   f.code AS father_code, 
                   f.name AS father_name,
                   po.number_of_outcomes, 
                   po.number_of_livebirths,
                   pc.outcome_type AS child_outcome_type, 
                   child.code AS child_code, 
                   child.name AS child_name, 
                   child.gender AS child_gender,
                   h.code AS household_code,
                   h.region AS region_code
            FROM pregnancy_outcome po
            JOIN member m ON m.id = po.mother_id
            LEFT JOIN member f ON f.id = po.father_id
            JOIN pregnancy_child pc ON pc.pregnancy_outcome_id = po.id
            LEFT JOIN member child ON child.id = pc.child_id
            LEFT JOIN visit v ON v.id = po.visit_id
            LEFT JOIN household h ON h.code = v.household_code
            WHERE 1=1
        """)

        if (request.ageMin != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) <= ${request.ageMax}")
        }
        if (request.referenceDate) {
            sql.append(" AND po.outcome_date <= ${refDateStr}")
        }
        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        return sql.toString()
    }

    private String buildIndepthHouseholdCompositionSql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT h.code AS LocationId,
                   m.code AS IndividualId,
                   m.dob AS DoB,
                   m.gender AS Sex,
                   head.code AS HeadId,
                   head.gender AS HeadSex,
                   hr.relationship_type AS RelationshipToHead,
                   h.name AS household_name,
                   h.type AS household_type,
                   h.institution_type AS household_institution_type,
                   m.name AS member_name,
                   head.name AS head_name,
                   TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) AS age_at_ref_date,
                   r.start_date AS resident_since,
                   ph.proxy_head_name AS ProxyHeadName,
                   ph.proxy_head_role AS ProxyHeadRole,
                   reg.code AS region_code,
                   reg.name AS region_name
            FROM household h
            LEFT JOIN member head ON head.id = h.head_id
            LEFT JOIN household_proxy_head ph ON ph.id = h.proxy_head_id
            JOIN residency r ON r.household_id = h.id 
                 AND r.start_date <= ${refDateStr} 
                 AND (r.end_date IS NULL OR r.end_date >= ${refDateStr}) 
                 AND (r.status IS NULL OR r.status <> 2)
            JOIN member m ON m.id = r.member_id
            LEFT JOIN head_relationship hr ON hr.member_id = m.id AND hr.household_id = h.id 
                 AND hr.start_date = (SELECT MAX(hr2.start_date) FROM head_relationship hr2 WHERE hr2.member_id = m.id AND hr2.household_id = h.id AND (hr2.status IS NULL OR hr2.status <> 2))
            LEFT JOIN region reg ON reg.code = h.region
            WHERE 1=1
        """)

        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        sql.append(" ORDER BY h.code, m.code")

        return sql.toString()
    }

    private String buildHouseholdCompositionSql(ExportRequest request) {
        String refDateStr = request.referenceDate ? "'${request.referenceDate.toString()}'" : "CURRENT_DATE"

        StringBuilder sql = new StringBuilder("""
            SELECT h.code AS household_code,
                   h.name AS household_name,
                   h.type AS household_type,
                   h.institution_type AS household_institution_type,
                   head.code AS head_code,
                   head.name AS head_name,
                   head.gender AS head_gender,
                   m.code AS member_code,
                   m.name AS member_name,
                   m.gender AS member_gender,
                   m.dob AS member_dob,
                   TIMESTAMPDIFF(YEAR, m.dob, ${refDateStr}) AS member_age_at_ref_date,
                   hr.relationship_type,
                   r.start_date AS resident_since,
                   ph.proxy_head_name,
                   ph.proxy_head_role,
                   reg.code AS region_code,
                   reg.name AS region_name
            FROM household h
            LEFT JOIN member head ON head.id = h.head_id
            LEFT JOIN household_proxy_head ph ON ph.id = h.proxy_head_id
            JOIN residency r ON r.household_id = h.id 
                 AND r.start_date <= ${refDateStr} 
                 AND (r.end_date IS NULL OR r.end_date >= ${refDateStr}) 
                 AND (r.status IS NULL OR r.status <> 2)
            JOIN member m ON m.id = r.member_id
            LEFT JOIN head_relationship hr ON hr.member_id = m.id AND hr.household_id = h.id 
                 AND hr.start_date = (SELECT MAX(hr2.start_date) FROM head_relationship hr2 WHERE hr2.member_id = m.id AND hr2.household_id = h.id AND (hr2.status IS NULL OR hr2.status <> 2))
            LEFT JOIN region reg ON reg.code = h.region
            WHERE 1=1
        """)

        if (request.regionCode) {
            sql.append(buildRegionFilterClause(request.regionCode, "h"))
        }
        if (request.randomSamplePercent != null && request.randomSamplePercent < 100.0) {
            sql.append(" AND RAND() <= (${request.randomSamplePercent} / 100.0)")
        }

        sql.append(" ORDER BY h.code, m.code")

        return sql.toString()
    }

    // --- Anonymization & Row Processing ---

    private Map<String, Object> processAndAnonymizeRow(Map<String, Object> rawRow, ExportRequest request) {
        return processAndAnonymizeRow(request.datasetName, rawRow, request)
    }

    private Map<String, Object> processAndAnonymizeRow(String tableName, Map<String, Object> rawRow, ExportRequest request) {
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
                SensitiveType sensType = colMeta?.sensitiveType ?: SensitiveType.NOT_APPLICABLE

                // 1. Person Names
                if (sensType == SensitiveType.PERSON_NAME) {
                    if (request.nameAnonymizationMode == NameAnonymizationMode.REMOVE) {
                        return // Skip column
                    } else if (request.nameAnonymizationMode == NameAnonymizationMode.INITIALS) {
                        value = maskName(value.toString())
                    }
                }
                // 2. Phone Numbers
                else if (sensType == SensitiveType.PHONE_NUMBER) {
                    if (request.phoneAnonymizationMode == PhoneAnonymizationMode.REMOVE) {
                        return // Skip column
                    } else if (request.phoneAnonymizationMode == PhoneAnonymizationMode.MASK) {
                        value = PHONE_MASK_TEMPLATE
                    }
                }
                // 3. GPS Coordinates
                else if (sensType == SensitiveType.GPS) {
                    if (request.gpsAnonymizationMode == GpsAnonymizationMode.REMOVE) {
                        return // Skip column
                    } else if (request.gpsAnonymizationMode == GpsAnonymizationMode.ROUNDED) {
                        if (value instanceof Double) {
                            value = Math.round(((Double) value).doubleValue() * 100.0) / 100.0
                        }
                    }
                }
                // 4. Dates
                else if (sensType == SensitiveType.DATE) {

                    if (request.dateAnonymizationMode == DateAnonymizationMode.REMOVE) {
                        return // Skip column
                    } else if (request.dateAnonymizationMode == DateAnonymizationMode.SHIFT_DATES && value != null) {
                        if (value instanceof LocalDate) {
                            value = ((LocalDate) value).minusDays(DATE_SHIFT_OFFSET_DAYS)
                        } else {
                            try {
                                String s = value.toString()
                                if (s.length() >= 10) {
                                    LocalDate parsed = LocalDate.parse(s.substring(0, 10))
                                    value = parsed.minusDays(DATE_SHIFT_OFFSET_DAYS).toString()
                                }
                            } catch (Exception e) {
                                // Keep original if unparseable
                            }
                        }
                    }
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

    private String maskName(String name) {
        if (!name) return ""
        String[] parts = name.split("\\s+")
        return parts.collect { p -> p.length() > 1 ? p.substring(0, 1) + NAME_MASK_SUFFIX : p }.join(" ")
    }

    // --- Statistical Script Generators ---

    private String generateSyntaxScript(ExportRequest request) {
        if (request.hasRepeatTables) {
            return generateMasterSyntaxScript(request)
        }

        def fmt = request.format

        switch (fmt) {
            case DataExportFormat.SPSS:
                return generateSpssScript(request)
            case DataExportFormat.R:
                return generateRScript(request)
            case DataExportFormat.SAS:
                return generateSasScript(request)
            default:
                return generateStataDoScript(request)
        }
    }

    private String getDisplayLabel(DataDictionaryService.ColumnMetadata colMeta) {
        if (!colMeta) return ""
        if (colMeta.label && colMeta.label != colMeta.columnName) {
            return colMeta.label
        }
        String name = colMeta.columnName ?: ""
        return name.split("_").collect { it.capitalize() }.join(" ")
    }

    private String generateStataDoScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        StringBuilder doScript = new StringBuilder()
        doScript.append("/* Stata Do-File for HDSS Data Export: ${tableName} */\n")
        doScript.append("/* Optional: Set working directory to folder containing CSV data files */\n")
        doScript.append("/* cd \"/path/to/folder\" */\n\n")
        doScript.append("clear all\n")
        doScript.append("set more off\n\n")
        doScript.append("/* Import CSV Dataset */\n")
        doScript.append("import delimited using \"${tableName}_data.csv\", case(lower) clear\n\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta) {
            doScript.append("/* Variable Descriptions */\n")
            meta.columns.each { colName, colMeta ->
                String stCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (label) {
                    doScript.append("capture label variable ${stCol} \"${escapeQuote(label)}\"\n")
                }
            }

            doScript.append("\n/* Date & Timestamp Conversions */\n")
            meta.columns.each { colName, colMeta ->
                String stCol = sanitizeColumnName(colName)
                String dataType = colMeta.dataType ? colMeta.dataType.toUpperCase() : ""
                String label = getDisplayLabel(colMeta)

                boolean isDate = dataType.contains("DATE") && !dataType.contains("TIME")
                boolean isDateTime = dataType.contains("DATETIME") || dataType.contains("TIMESTAMP") || dataType.contains("TIME")

                if (isDate) {
                    doScript.append("capture confirm variable ${stCol}\n")
                    doScript.append("if !_rc {\n")
                    doScript.append("    capture gen ${stCol}_d = date(${stCol}, \"YMD\")\n")
                    doScript.append("    capture format ${stCol}_d %td\n")
                    doScript.append("    capture drop ${stCol}\n")
                    doScript.append("    capture rename ${stCol}_d ${stCol}\n")
                    if (label) {
                        doScript.append("    capture label variable ${stCol} \"${escapeQuote(label)}\"\n")
                    }
                    doScript.append("}\n")
                } else if (isDateTime) {
                    doScript.append("capture confirm variable ${stCol}\n")
                    doScript.append("if !_rc {\n")
                    doScript.append("    capture gen ${stCol}_dt = clock(${stCol}, \"YMDhms\")\n")
                    doScript.append("    capture format ${stCol}_dt %tc\n")
                    doScript.append("    capture drop ${stCol}\n")
                    doScript.append("    capture rename ${stCol}_dt ${stCol}\n")
                    if (label) {
                        doScript.append("    capture label variable ${stCol} \"${escapeQuote(label)}\"\n")
                    }
                    doScript.append("}\n")
                }
            }

            doScript.append("\n/* Categorical Encoding & Value Labels */\n")
            meta.columns.each { colName, colMeta ->
                String stCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        List<DataDictionaryService.EnumOptionMetadata> validOptions = enumType.options.findAll {
                            it.code != null && !it.code.trim().isEmpty()
                        }

                        if (!validOptions.isEmpty()) {
                            String lblName = "${stCol}_lbl"

                            boolean isNumericCodes = validOptions.every { opt ->
                                opt.code != null && opt.code.matches(/^-?\d+$/)
                            }

                            if (isNumericCodes) {
                                doScript.append("capture confirm variable ${stCol}\n")
                                doScript.append("if !_rc {\n")
                                doScript.append("    capture label drop ${lblName}\n")
                                doScript.append("    label define ${lblName}")
                                validOptions.each { opt ->
                                    doScript.append(" ${opt.code} \"${escapeQuote(opt.label)}\"")
                                }
                                doScript.append(", replace\n")
                                doScript.append("    destring ${stCol}, replace\n")
                                doScript.append("    label values ${stCol} ${lblName}\n")
                                doScript.append("}\n")
                            } else {
                                def sortedOpts = validOptions.sort { a, b -> (a.code ?: "").compareTo(b.code ?: "") }
                                doScript.append("capture confirm variable ${stCol}\n")
                                doScript.append("if !_rc {\n")
                                doScript.append("    capture label drop ${lblName}\n")
                                doScript.append("    label define ${lblName}")
                                sortedOpts.eachWithIndex { opt, idx ->
                                    doScript.append(" ${idx + 1} \"${escapeQuote(opt.label)}\"")
                                }
                                doScript.append(", replace\n")
                                doScript.append("    encode ${stCol}, gen(${stCol}_encoded)\n")
                                doScript.append("    label values ${stCol}_encoded ${lblName}\n")
                                doScript.append("    drop ${stCol}\n")
                                doScript.append("    rename ${stCol}_encoded ${stCol}\n")
                                if (label) {
                                    doScript.append("    capture label variable ${stCol} \"${escapeQuote(label)}\"\n")
                                }
                                doScript.append("}\n")
                            }
                        }
                    }
                }
            }
        }

        doScript.append("\n/* Save Stata Data File */\n")
        doScript.append("save \"${tableName}.dta\", replace\n")
        return doScript.toString()
    }

    private String generateSpssScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        StringBuilder spss = new StringBuilder()
        spss.append("/* SPSS Syntax Script for HDSS Data Export: ${tableName} */\n")
        spss.append("/* Optional: Set working directory to folder containing CSV data files */\n")
        spss.append("/* CD '/path/to/folder'. */\n\n")
        spss.append("GET DATA\n  /TYPE=TXT\n  /FILE=\"${tableName}_data.csv\"\n")
        spss.append("  /ARRANGEMENT=DELIMITED\n  /DELCASE=LINE\n  /FIRSTCASE=2\n")
        spss.append("  /DELIMITERS=\",\"\n  /QUALIFIER='\"'\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta && meta.columns && !meta.columns.isEmpty()) {
            spss.append("  /VARIABLES=\n")
            int colCount = meta.columns.size()
            int i = 0
            meta.columns.each { colName, colMeta ->
                i++
                String spCol = sanitizeColumnName(colName)
                String term = (i == colCount) ? "." : ""
                spss.append("    ${spCol} A255${term}\n")
            }
        } else {
            spss.append(".\n")
        }
        spss.append("EXECUTE.\n")
        spss.append("DATASET NAME ${sanitizeColumnName(tableName)} WINDOW=FRONT.\n\n")

        if (meta) {
            spss.append("/* Variable Descriptions */\n")
            spss.append("VARIABLE LABELS\n")
            meta.columns.each { colName, colMeta ->
                String spCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (label) {
                    spss.append("  ${spCol} \"${escapeQuote(label)}\"\n")
                }
            }
            spss.append(".\nEXECUTE.\n\n")

            // Data Type Conversions via ALTER TYPE (Handles blank CSV strings cleanly)
            spss.append("/* Data Type Conversions */\n")
            meta.columns.each { colName, colMeta ->
                String spCol = sanitizeColumnName(colName)
                String dataType = colMeta.dataType ? colMeta.dataType.toUpperCase() : ""

                boolean isInt = dataType.contains("INT") || dataType.contains("LONG") || dataType.contains("SHORT")
                boolean isDouble = dataType.contains("DOUBLE") || dataType.contains("FLOAT") || dataType.contains("DECIMAL") || dataType.contains("NUMERIC")
                boolean isDate = dataType.contains("DATE") && !dataType.contains("TIME")
                boolean isDateTime = dataType.contains("DATETIME") || dataType.contains("TIMESTAMP") || dataType.contains("TIME")

                if (isInt) {
                    spss.append("ALTER TYPE ${spCol} (F12.0).\n")
                } else if (isDouble) {
                    spss.append("ALTER TYPE ${spCol} (F12.4).\n")
                } else if (isDate) {
                    spss.append("ALTER TYPE ${spCol} (SDATE10).\n")
                } else if (isDateTime) {
                    spss.append("ALTER TYPE ${spCol} (DATETIME20).\n")
                }
            }
            spss.append("EXECUTE.\n\n")

            // Value labels for categorical enum fields (filtering out blank option codes)
            spss.append("/* Value Labels for Categorical Variables */\n")
            meta.columns.each { colName, colMeta ->
                String spCol = sanitizeColumnName(colName)
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        List<DataDictionaryService.EnumOptionMetadata> validOptions = enumType.options.findAll {
                            it.code != null && !it.code.trim().isEmpty()
                        }

                        if (!validOptions.isEmpty()) {
                            boolean isNumericCodes = validOptions.every { opt ->
                                opt.code != null && opt.code.matches(/^-?\d+$/)
                            }

                            spss.append("VALUE LABELS ${spCol}\n")
                            validOptions.each { opt ->
                                String valSpec = isNumericCodes ? "${opt.code}" : "'${escapeQuote(opt.code)}'"
                                spss.append("  ${valSpec} \"${escapeQuote(opt.label)}\"\n")
                            }
                            spss.append(".\nEXECUTE.\n\n")
                        }
                    }
                }
            }

            spss.append("/* Save Native SPSS Data File (.sav) */\n")
            spss.append("SAVE OUTFILE=\"${tableName}.sav\".\n")
            spss.append("EXECUTE.\n")
        }

        return spss.toString()
    }

    private String generateRScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        StringBuilder r = new StringBuilder()
        r.append("# R Import Script for HDSS Data Export: ${tableName}\n")
        r.append("# Optional: Set working directory to folder containing CSV data files\n")
        r.append("# setwd(\"/path/to/folder\")\n\n")
        r.append("df <- read.csv(\"${tableName}_data.csv\", stringsAsFactors = FALSE, encoding = \"UTF-8\")\n\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta) {
            r.append("# Variable Labels / Descriptions\n")
            meta.columns.each { colName, colMeta ->
                String rCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (label) {
                    r.append("if (\"${rCol}\" %in% colnames(df)) attr(df\$${rCol}, \"label\") <- \"${escapeQuote(label)}\"\n")
                }
            }

            r.append("\n# Date & Timestamp Conversions\n")
            meta.columns.each { colName, colMeta ->
                String rCol = sanitizeColumnName(colName)
                String dataType = colMeta.dataType ? colMeta.dataType.toUpperCase() : ""

                boolean isDate = dataType.contains("DATE") && !dataType.contains("TIME")
                boolean isDateTime = dataType.contains("DATETIME") || dataType.contains("TIMESTAMP") || dataType.contains("TIME")

                if (isDate) {
                    r.append("if (\"${rCol}\" %in% colnames(df)) df\$${rCol} <- as.Date(df\$${rCol}, format = \"%Y-%m-%d\")\n")
                } else if (isDateTime) {
                    r.append("if (\"${rCol}\" %in% colnames(df)) df\$${rCol} <- as.POSIXct(df\$${rCol}, format = \"%Y-%m-%d %H:%M:%S\")\n")
                }
            }

            r.append("\n# Categorical Factors & Value Labels\n")
            meta.columns.each { colName, colMeta ->
                String rCol = sanitizeColumnName(colName)
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        List<DataDictionaryService.EnumOptionMetadata> validOptions = enumType.options.findAll {
                            it.code != null && !it.code.trim().isEmpty()
                        }
                        if (!validOptions.isEmpty()) {
                            String levels = validOptions.collect { "\"${escapeQuote(it.code)}\"" }.join(", ")
                            String labels = validOptions.collect { "\"${escapeQuote(it.label)}\"" }.join(", ")
                            r.append("if (\"${rCol}\" %in% colnames(df)) df\$${rCol} <- factor(df\$${rCol}, levels = c(${levels}), labels = c(${labels}))\n")
                        }
                    }
                }
            }
        }

        r.append("\nsummary(df)\n")
        return r.toString()
    }

    private String generateSasScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        String sasTable = sanitizeColumnName(tableName)
        if (sasTable.length() > 32) sasTable = sasTable.substring(0, 32)

        StringBuilder sas = new StringBuilder()
        sas.append("/* SAS Import Script for HDSS Data Export: ${tableName} */\n")
        sas.append("/* Optional: Set working directory in SAS Studio / SAS Viya / SASApp Server */\n")
        sas.append("/* Option 1 (SAS Studio / Files Home): */\n")
        sas.append("/* %let home_dir = %sysget(HOME); */\n")
        sas.append("/* %let rc = %sysfunc(dlgcdir(\"&home_dir\")); */\n")
        sas.append("/* Option 2 (SAS Viya Studio Auto-detect Program Path): */\n")
        sas.append("/* %let path = %substr(%sysget(SAS_EXECFILEPATH), 1, %eval(%length(%sysget(SAS_EXECFILEPATH)) - %length(%sysget(SAS_EXECFILENAME)))); */\n")
        sas.append("/* Option 3 (SAS Custom Folder Path): */\n")
        sas.append("/* %let path = /your/sas/folder/path; */\n\n")
        sas.append("PROC IMPORT DATAFILE=\"${tableName}_data.csv\"\n")
        sas.append("    OUT=WORK.${sasTable}\n")
        sas.append("    DBMS=CSV REPLACE;\n")
        sas.append("    GETNAMES=YES;\n")
        sas.append("RUN;\n\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta) {
            // 1. Categorical Value Formats via PROC FORMAT
            StringBuilder procFormat = new StringBuilder()
            Map<String, String> columnFormatMap = [:]

            meta.columns.each { colName, colMeta ->
                String sasCol = sanitizeColumnName(colName)
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        List<DataDictionaryService.EnumOptionMetadata> validOptions = enumType.options.findAll {
                            it.code != null && !it.code.trim().isEmpty()
                        }
                        if (!validOptions.isEmpty()) {
                            boolean isNumericCodes = validOptions.every { opt ->
                                opt.code != null && opt.code.matches(/^-?\d+$/)
                            }

                            String fmtName = sasCol.length() > 28 ? sasCol.substring(0, 28) + "_f" : "${sasCol}_f"
                            String fullFmt = isNumericCodes ? "${fmtName}" : "\$${fmtName}"

                            procFormat.append("    VALUE ${fullFmt}\n")
                            validOptions.each { opt ->
                                String valSpec = isNumericCodes ? "${opt.code}" : "'${escapeQuote(opt.code)}'"
                                procFormat.append("        ${valSpec} = \"${escapeQuote(opt.label)}\"\n")
                            }
                            procFormat.append("    ;\n")
                            columnFormatMap.put(sasCol, "${fullFmt}.")
                        }
                    }
                }
            }

            if (procFormat.length() > 0) {
                sas.append("/* Define Value Formats */\n")
                sas.append("PROC FORMAT;\n")
                sas.append(procFormat.toString())
                sas.append("RUN;\n\n")
            }

            // 2. Apply Variable Labels & Value Formats via PROC DATASETS
            sas.append("/* Apply Variable Labels and Value Formats */\n")
            sas.append("PROC DATASETS LIB=WORK NOLIST;\n")
            sas.append("    MODIFY ${sasTable};\n")

            sas.append("    LABEL\n")
            meta.columns.each { colName, colMeta ->
                String sasCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (label) {
                    sas.append("        ${sasCol} = \"${escapeQuote(label)}\"\n")
                }
            }
            sas.append("    ;\n")

            if (!columnFormatMap.isEmpty()) {
                sas.append("    FORMAT\n")
                columnFormatMap.each { col, fmt ->
                    sas.append("        ${col} ${fmt}\n")
                }
                sas.append("    ;\n")
            }

            sas.append("QUIT;\n")
        }

        return sas.toString()
    }

    // --- Master Script Generators (Single Master Script for Root + All Descendant Repeat Tables) ---

    private String generateMasterSyntaxScript(ExportRequest request) {
        def fmt = request.format
        switch (fmt) {
            case DataExportFormat.SPSS:
                return generateMasterSpssScript(request)
            case DataExportFormat.R:
                return generateMasterRScript(request)
            case DataExportFormat.SAS:
                return generateMasterSasScript(request)
            default:
                return generateMasterStataDoScript(request)
        }
    }

    private List<String> getAllExportTableNames(ExportRequest request) {
        List<String> tableNames = [request.datasetName]
        if (request.hasRepeatTables) {
            List<DataDictionaryService.TableMetadata> descendants = dataDictionaryService.getAllDescendantRepeatTables(request.datasetName)
            if (descendants) {
                descendants.each { tableNames.add(it.tableName) }
            }
        }
        return tableNames
    }

    private String generateMasterStataDoScript(ExportRequest request) {
        List<String> allTables = getAllExportTableNames(request)
        StringBuilder doScript = new StringBuilder()
        doScript.append("/* Master Stata Do-File for HDSS Data Export: ${request.datasetName} */\n")
        doScript.append("/* Optional: Set working directory to folder containing CSV data files */\n")
        doScript.append("/* cd \"/path/to/folder\" */\n\n")
        doScript.append("clear all\n")
        doScript.append("set more off\n\n")

        allTables.eachWithIndex { tableName, idx ->
            doScript.append("/* ==================================================================== */\n")
            doScript.append("/* Table ${idx + 1}: ${tableName} */\n")
            doScript.append("/* ==================================================================== */\n")

            ExportRequest tableReq = new ExportRequest(datasetName: tableName, format: request.format)
            String singleTableScript = generateStataDoScript(tableReq)

            // Strip header "clear all\nset more off\n\n" and cd comments from individual script block
            String cleanedBlock = singleTableScript.replace("clear all\nset more off\n\n", "")
            cleanedBlock = cleanedBlock.replace("/* Optional: Set working directory to folder containing CSV data files */\n/* cd \"/path/to/folder\" */\n\n", "")
            doScript.append(cleanedBlock).append("\n\n")
        }

        return doScript.toString()
    }

    private String generateMasterSpssScript(ExportRequest request) {
        List<String> allTables = getAllExportTableNames(request)
        StringBuilder spss = new StringBuilder()
        spss.append("/* Master SPSS Syntax Script for HDSS Data Export: ${request.datasetName} */\n")
        spss.append("/* Optional: Set working directory to folder containing CSV data files */\n")
        spss.append("/* CD '/path/to/folder'. */\n\n")

        allTables.eachWithIndex { tableName, idx ->
            spss.append("/* ==================================================================== */\n")
            spss.append("/* Table ${idx + 1}: ${tableName} */\n")
            spss.append("/* ==================================================================== */\n")

            ExportRequest tableReq = new ExportRequest(datasetName: tableName, format: request.format)
            String singleTableScript = generateSpssScript(tableReq)
            spss.append(singleTableScript).append("\n\n")
        }

        return spss.toString()
    }

    private String generateMasterRScript(ExportRequest request) {
        List<String> allTables = getAllExportTableNames(request)
        StringBuilder r = new StringBuilder()
        r.append("# Master R Import Script for HDSS Data Export: ${request.datasetName}\n")
        r.append("# Optional: Set working directory to folder containing CSV data files\n")
        r.append("# setwd(\"/path/to/folder\")\n\n")

        List<String> rDataFrames = []

        allTables.eachWithIndex { tableName, idx ->
            String varName = "df_${sanitizeColumnName(tableName)}"
            rDataFrames.add(varName)

            r.append("# ====================================================================\n")
            r.append("# Table ${idx + 1}: ${tableName}\n")
            r.append("# ====================================================================\n")

            ExportRequest tableReq = new ExportRequest(datasetName: tableName, format: request.format)
            String singleTableScript = generateRScript(tableReq)

            // Strip header comments from individual block
            String cleanedBlock = singleTableScript.replace("# Optional: Set working directory to folder containing CSV data files\n# setwd(\"/path/to/folder\")\n\n", "")

            // Customize data frame variable name from df to df_<tableName>
            String customizedScript = cleanedBlock
                    .replace("df <- read.csv(\"${tableName}_data.csv\"", "${varName} <- read.csv(\"${tableName}_data.csv\"")
                    .replace("df\$", "${varName}\$")
                    .replace("summary(df)", "summary(${varName})")

            r.append(customizedScript).append("\n\n")
        }

        if (!rDataFrames.isEmpty()) {
            String dfListStr = rDataFrames.join(", ")
            r.append("# Save all data frames into a single R Workspace file\n")
            r.append("save(${dfListStr}, file = \"${request.datasetName}_workspace.RData\")\n")
        }

        return r.toString()
    }

    private String generateMasterSasScript(ExportRequest request) {
        List<String> allTables = getAllExportTableNames(request)
        StringBuilder sas = new StringBuilder()
        sas.append("/* Master SAS Import Script for HDSS Data Export: ${request.datasetName} */\n")
        sas.append("/* Optional: Set working directory in SAS Studio / SAS Viya / SASApp Server */\n")
        sas.append("/* Option 1 (SAS Studio / Files Home): */\n")
        sas.append("/* %let home_dir = %sysget(HOME); */\n")
        sas.append("/* %let rc = %sysfunc(dlgcdir(\"&home_dir\")); */\n")
        sas.append("/* Option 2 (SAS Viya Studio Auto-detect Program Path): */\n")
        sas.append("/* %let path = %substr(%sysget(SAS_EXECFILEPATH), 1, %eval(%length(%sysget(SAS_EXECFILEPATH)) - %length(%sysget(SAS_EXECFILENAME)))); */\n")
        sas.append("/* Option 3 (SAS Custom Folder Path): */\n")
        sas.append("/* %let path = /your/sas/folder/path; */\n\n")

        allTables.eachWithIndex { tableName, idx ->
            sas.append("/* ==================================================================== */\n")
            sas.append("/* Table ${idx + 1}: ${tableName} */\n")
            sas.append("/* ==================================================================== */\n")

            ExportRequest tableReq = new ExportRequest(datasetName: tableName, format: request.format)
            String singleTableScript = generateSasScript(tableReq)
            sas.append(singleTableScript).append("\n\n")
        }

        return sas.toString()
    }

    private String generateSyntaxScriptForTable(String tableName, ExportRequest request) {
        ExportRequest childReq = new ExportRequest(
                datasetName: tableName,
                format: request.format,
                selectedColumns: []
        )
        return generateSyntaxScript(childReq)
    }

    String buildChildTableSql(String childTableName, String immediateParentTableName, ExportRequest request) {
        List<String> parentIds = request.exportedTableIdsMap.get(immediateParentTableName)
        if (!parentIds || parentIds.isEmpty()) {
            return "SELECT c.* FROM `${childTableName}` c WHERE 1=0"
        }

        String fkCol = "${immediateParentTableName}_id"

        // Chunk parent BIGINT IDs into groups of 1,000 for SQL safety
        List<List<String>> chunks = parentIds.collate(1000)
        String whereCondition = chunks.collect { chunk ->
            String idList = chunk.collect { it.replaceAll("[^0-9]", "") }.findAll { !it.empty }.join(",")
            return "c.${fkCol} IN (${idList})"
        }.join(" OR ")

        return "SELECT c.* FROM `${childTableName}` c WHERE ${whereCondition}"
    }

    // --- Helper Utilities ---

    private void trackAllColumnsForAutoResize(Sheet dictSheet) {
        if (dictSheet instanceof SXSSFSheet) {
            def sh = ((SXSSFSheet) dictSheet)
            sh.trackAllColumnsForAutoSizing()
        }
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

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(request.datasetName)
        List<DataDictionaryService.TableMetadata> targetMetas = [meta]
        if (request.hasRepeatTables) {
            List<DataDictionaryService.TableMetadata> descendants = dataDictionaryService.getAllDescendantRepeatTables(request.datasetName)
            if (descendants) targetMetas.addAll(descendants)
        }

        int rowIndex = 1
        targetMetas.each { tMeta ->
            if (tMeta) {
                tMeta.columns.values().each { colMeta ->
                    if (tMeta.tableName == request.datasetName && request.selectedColumns && !request.selectedColumns.contains(colMeta.columnName)) {
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

    private String sanitizeColumnName(String colName) {
        if (!colName) return "col"
        String sanitized = colName.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase()
        if (sanitized.length() > 32) {
            sanitized = sanitized.substring(0, 32)
        }
        return sanitized
    }

    private String escapeCsv(String val) {
        if (val == null) return ""
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\""
        }
        return val
    }

    private String escapeQuote(String val) {
        return val ? val.replace("\"", "\\\"") : ""
    }

    private List<String> extractHeadersForTable(String tableName, DataDictionaryService.TableMetadata tableMetadata, Map<String, Object> firstRow, ExportRequest request) {
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

    private String getScriptExtension(DataExportFormat format) {
        if (format == DataExportFormat.SPSS) return ".sps"
        if (format == DataExportFormat.R) return ".R"
        if (format == DataExportFormat.SAS) return ".sas"
        if (format == DataExportFormat.STATA) return ".do"
        return ".do"
    }

    private String getExportFileExtension(ExportRequest request) {
        DataExportFormat format = request.format
        if (request.isExportingZip()) return ".zip"
        if (format == DataExportFormat.EXCEL) return ".xlsx"
        if (format == DataExportFormat.CSV) return ".csv"
        return ".zip"
    }

    // --- Export Request DTO ---

    static class ExportRequest {
        String datasetType = DataExportItem.REGULAR_TABLE.code
        String datasetName = "member"
        String datasetLabel = ""
        DataExportFormat format = DataExportFormat.CSV
        LocalDate referenceDate = LocalDate.now()
        String gender = "ALL"
        Integer ageMin
        Integer ageMax
        String regionCode
        Double randomSamplePercent = 100.0
        List<String> selectedColumns = []
        Boolean includeMediaFiles = true
        Boolean includeDictionary = false
        Boolean activeResidentsOnly = false

        Map<String, List<String>> exportedTableIdsMap = new java.util.concurrent.ConcurrentHashMap<>()

        Boolean hasMediaFields = false
        Boolean hasRepeatTables = false

        // Granular Privacy Controls via Enums
        NameAnonymizationMode nameAnonymizationMode = NameAnonymizationMode.INITIALS
        PhoneAnonymizationMode phoneAnonymizationMode = PhoneAnonymizationMode.MASK
        GpsAnonymizationMode gpsAnonymizationMode = GpsAnonymizationMode.ROUNDED
        DateAnonymizationMode dateAnonymizationMode = DateAnonymizationMode.EXACT

        String exportedDatasetFilename = ""
        Long exportedDatasetFilesize = 0L
        String exportedCookbookFilename = ""
        Long exportedCookbookFilesize = 0L
        String exportedScriptFilename = ""
        Long exportedScriptFilesize = 0L
        List<File> exportedMediaFiles = new ArrayList<>()

        Integer maxRows

        boolean isExportingZip() {
            return (includeMediaFiles && hasMediaFields) || (hasRepeatTables && format != DataExportFormat.EXCEL) || isSyntaxScriptFormat(format) || (format == DataExportFormat.CSV && includeDictionary)
        }
    }
}
