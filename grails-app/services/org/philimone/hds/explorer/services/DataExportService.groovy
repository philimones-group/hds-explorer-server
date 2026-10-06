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
                        activeResidentsOnly: report.activeResidentsOnly,
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
                    if (req.format == DataExportFormat.EXCEL || (req.format == DataExportFormat.CSV && !req.includeDictionary)) {
                        report.datasetFileName = exportedFile.name
                        report.datasetFileSize = exportedFile.length()
                    } else {
                        report.zipFileName = exportedFile.name
                        report.zipFileSize = exportedFile.length()
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
     * Generates export into a temporary file, moves to attachments docs directory, and streams to output stream
     */
    File exportDataset(ExportRequest request, OutputStream outputStream) {
        File finalExportFile = generateExportFile(request)
        if (outputStream && finalExportFile && finalExportFile.exists()) {
            finalExportFile.withInputStream { is ->
                outputStream << is
            }
            outputStream.flush()
        }
        return finalExportFile
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
                if (request.format == DataExportFormat.EXCEL) {
                    exportToExcelStream(request, tempOut) //exports also data dictionary if it includes data dictionary
                } else if (request.format == DataExportFormat.CSV && !request.includeDictionary) {
                    exportToCsvStream(request, tempOut) //exports just the csv if not data dictionary included
                } else {
                    exportToZipBundle(request, tempOut)
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
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"))
        // Write UTF-8 BOM to preserve Portuguese/French accents in Excel & Stata
        writer.write(UTF8_BOM)

        String sql = buildExportSql(request)
        def session = sessionFactory.currentSession
        NativeQuery query = session.createNativeQuery(sql)
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)

        List<Map<String, Object>> resultList = query.list()
        if (resultList.isEmpty()) {
            writer.write("No records found\n")
            writer.flush()
            return
        }

        List<String> headers = extractHeaders(resultList.first(), request)
        writer.write(headers.collect { escapeCsv(it) }.join(",") + "\n")

        for (Map<String, Object> rawRow : resultList) {
            Map<String, Object> row = processAndAnonymizeRow(rawRow, request)
            List<String> values = headers.collect { h -> escapeCsv(row.get(h)?.toString() ?: "") }
            writer.write(values.join(",") + "\n")
        }

        writer.flush()
    }

    private void exportToExcelStream(ExportRequest request, OutputStream outputStream) {
        // SXSSFWorkbook streams rows to disk in chunks to prevent OutOfMemoryError
        SXSSFWorkbook workbook = new SXSSFWorkbook(EXCEL_STREAMING_ROW_FLUSH_SIZE)
        try {
            Sheet sheet = workbook.createSheet(request.datasetName)

            String sql = buildExportSql(request)
            def session = sessionFactory.currentSession
            NativeQuery query = session.createNativeQuery(sql)
            query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)

            List<Map<String, Object>> resultList = query.list()

            if (!resultList.isEmpty()) {
                List<String> headers = extractHeaders(resultList.first(), request)

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
                    Map<String, Object> row = processAndAnonymizeRow(rawRow, request)
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

            // Optional Data Dictionary Tab
            if (request.includeDictionary) {
                appendDataDictionarySheet(workbook, request)
            }

            workbook.write(outputStream)
        } finally {
            workbook.dispose() // dispose of temporary files on disk
        }
    }

    private void exportToZipBundle(ExportRequest request, OutputStream outputStream) {
        ZipOutputStream zipOut = new ZipOutputStream(outputStream)

        // 1. Generate CSV Data
        ByteArrayOutputStream csvOut = new ByteArrayOutputStream()
        exportToCsvStream(request, csvOut)
        byte[] csvBytes = csvOut.toByteArray()
        def baseName = "${request.datasetName?.toLowerCase()}"

        zipOut.putNextEntry(new ZipEntry("${baseName}_data.csv"))
        zipOut.write(csvBytes)
        zipOut.closeEntry()

        // 2. Generate Syntax Import Script
        if (request.format != DataExportFormat.CSV && request.format != DataExportFormat.EXCEL) { //SCRIPTS ARE ONLY GENERATED FOR STATA/SPSS/R/SAS
            String scriptExt = getScriptExtension(request.format)
            String scriptContent = generateSyntaxScript(request)

            zipOut.putNextEntry(new ZipEntry("import_${baseName}${scriptExt}"))
            zipOut.write(scriptContent.getBytes("UTF-8"))
            zipOut.closeEntry()
        }

        // 3. Generate Data Dictionary Excel
        if (request.includeDictionary && request.format != DataExportFormat.EXCEL) {
            ByteArrayOutputStream dictOut = new ByteArrayOutputStream()
            SXSSFWorkbook dictWorkbook = new SXSSFWorkbook(EXCEL_STREAMING_ROW_FLUSH_SIZE)
            appendDataDictionarySheet(dictWorkbook, request)
            dictWorkbook.write(dictOut)
            dictWorkbook.dispose()

            zipOut.putNextEntry(new ZipEntry(DICTIONARY_EXCEL_FILENAME))
            zipOut.write(dictOut.toByteArray())
            zipOut.closeEntry()
        }

        zipOut.finish()
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

            if (request.activeResidentsOnly) {
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

            if (request.activeResidentsOnly) {
                sql.append(" AND (t.end_type = 'NA' OR t.end_date IS NULL)")
            }
            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, t.start_date) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, t.start_date) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND t.start_date <= ${refDateStr} AND (t.end_date IS NULL OR t.end_date >= ${refDateStr})")
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

            if (request.activeResidentsOnly) {
                sql.append(" AND (t.end_type = 'NA' OR t.end_date IS NULL)")
            }
            if (request.gender && request.gender != "ALL") {
                sql.append(" AND m.gender = '${request.gender}'")
            }
            if (request.ageMin != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, t.start_date) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, t.start_date) <= ${request.ageMax}")
            }
            if (request.referenceDate) {
                sql.append(" AND t.start_date <= ${refDateStr} AND (t.end_date IS NULL OR t.end_date >= ${refDateStr})")
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

            if (request.activeResidentsOnly) { //currently married
                sql.append(" AND (t.end_status IS NULL OR t.end_status = 'NA' OR t.end_date IS NULL)")
            }
            if (request.gender && request.gender != "ALL") {
                sql.append(" AND (mA.gender = '${request.gender}' OR mB.gender = '${request.gender}')")
            }
            if (request.ageMin != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, t.start_date) >= ${request.ageMin} OR TIMESTAMPDIFF(YEAR, mB.dob, t.start_date) >= ${request.ageMin})")
            }
            if (request.ageMax != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, t.start_date) <= ${request.ageMax} OR TIMESTAMPDIFF(YEAR, mB.dob, t.start_date) <= ${request.ageMax})")
            }
            if (request.referenceDate) {
                sql.append(" AND t.start_date <= ${refDateStr} AND (t.end_date IS NULL OR t.end_date >= ${refDateStr})")
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
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, v.visit_date) >= ${request.ageMin} OR TIMESTAMPDIFF(YEAR, mB.dob, v.visit_date) >= ${request.ageMin})")
            }
            if (request.ageMax != null) {
                sql.append(" AND (TIMESTAMPDIFF(YEAR, mA.dob, v.visit_date) <= ${request.ageMax} OR TIMESTAMPDIFF(YEAR, mB.dob, v.visit_date) <= ${request.ageMax})")
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
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${dateCol}) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, ${dateCol}) <= ${request.ageMax}")
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
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, po.outcome_date) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, po.outcome_date) <= ${request.ageMax}")
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
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, v.visit_date) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, v.visit_date) <= ${request.ageMax}")
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
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, pv.visit_date) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, pv.visit_date) <= ${request.ageMax}")
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
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, v.visit_date) >= ${request.ageMin}")
            }
            if (request.ageMax != null) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, v.visit_date) <= ${request.ageMax}")
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
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${dateCol}) >= ${request.ageMin}")
            }
            if (request.ageMax != null && dateCol) {
                sql.append(" AND TIMESTAMPDIFF(YEAR, mother.dob, ${dateCol}) <= ${request.ageMax}")
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
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, po.outcome_date) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, po.outcome_date) <= ${request.ageMax}")
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
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, po.outcome_date) >= ${request.ageMin}")
        }
        if (request.ageMax != null) {
            sql.append(" AND TIMESTAMPDIFF(YEAR, m.dob, po.outcome_date) <= ${request.ageMax}")
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
        Map<String, Object> processed = new LinkedHashMap<>()

        rawRow.each { k, val ->
            String origColName = k.toString()
            String colName = origColName.toLowerCase()
            Object value = val

            DataDictionaryService.ColumnMetadata colMeta = dataDictionaryService.getColumnMetadata(request.datasetName, origColName)
            SensitiveType sensType = colMeta?.sensitiveType ?: SensitiveType.NOT_APPLICABLE

            // 1. Person Names
            if (sensType == SensitiveType.PERSON_NAME) {
                if (request.nameAnonymizationMode == NameAnonymizationMode.REMOVE) {
                    return // Skip column
                } else if (request.nameAnonymizationMode == NameAnonymizationMode.INITIALS) {
                    value = value ? maskName(value.toString()) : value
                }
            }
            // 2. Phone Numbers
            else if (sensType == SensitiveType.PHONE_NUMBER) {
                if (request.phoneAnonymizationMode == PhoneAnonymizationMode.REMOVE) {
                    return // Skip column
                } else if (request.phoneAnonymizationMode == PhoneAnonymizationMode.MASK) {
                    value = value ? PHONE_MASK_TEMPLATE : value
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

    private String generateStataDoScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        StringBuilder doScript = new StringBuilder()
        doScript.append("/* Stata Do-File for HDSS Data Export: ${tableName} */\n")
        doScript.append("clear\n")
        doScript.append("import delimited using \"${tableName}_data.csv\", case(lower) clear\n\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta) {
            meta.columns.each { colName, colMeta ->
                String stCol = sanitizeColumnName(colName)
                if (colMeta.label) {
                    doScript.append("label variable ${stCol} \"${escapeQuote(colMeta.label)}\"\n")
                }
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        String lblName = "${stCol}_lbl"
                        doScript.append("label define ${lblName}")
                        enumType.options.each { opt ->
                            doScript.append(" ${opt.code} \"${escapeQuote(opt.label)}\"")
                        }
                        doScript.append("\nlabel values ${stCol} ${lblName}\n")
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
        spss.append("GET DATA\n  /TYPE=TXT\n  /FILE=\"${tableName}_data.csv\"\n")
        spss.append("  /DELCASE=LINE\n  /DELIMITERS=\",\"\n  /QUALIFIER='\"'\n  /ARRANGEMENT=DELIMITED\n")
        spss.append("  /FIRSTCASE=2\n  /IMPORTCASE=ALL.\nEXECUTE.\n\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta) {
            spss.append("VARIABLE LABELS\n")
            meta.columns.each { colName, colMeta ->
                String spCol = sanitizeColumnName(colName)
                if (colMeta.label) {
                    spss.append("  ${spCol} \"${escapeQuote(colMeta.label)}\"\n")
                }
            }
            spss.append(".\nEXECUTE.\n\n")
        }

        spss.append("MISSING VALUES ALL ('').\nEXECUTE.\n")
        return spss.toString()
    }

    private String generateRScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        StringBuilder r = new StringBuilder()
        r.append("# R Import Script for HDSS Data Export: ${tableName}\n")
        r.append("df <- read.csv(\"${tableName}_data.csv\", stringsAsFactors = FALSE, encoding = \"UTF-8\")\n\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta) {
            meta.columns.each { colName, colMeta ->
                String rCol = sanitizeColumnName(colName)
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        String levels = enumType.options.collect { "\"${it.code}\"" }.join(", ")
                        String labels = enumType.options.collect { "\"${escapeQuote(it.label)}\"" }.join(", ")
                        r.append("df\$${rCol} <- factor(df\$${rCol}, levels = c(${levels}), labels = c(${labels}))\n")
                    }
                }
            }
        }

        r.append("\nsummary(df)\n")
        return r.toString()
    }

    private String generateSasScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        StringBuilder sas = new StringBuilder()
        sas.append("/* SAS Import Script for HDSS Data Export: ${tableName} */\n")
        sas.append("PROC IMPORT DATAFILE=\"${tableName}_data.csv\"\n")
        sas.append("    OUT=WORK.${sanitizeColumnName(tableName)}\n")
        sas.append("    DBMS=CSV REPLACE;\n")
        sas.append("    GETNAMES=YES;\n")
        sas.append("RUN;\n\n")
        return sas.toString()
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
        if (meta) {
            int rowIndex = 1
            meta.columns.values().eachWithIndex { colMeta, idx ->

                //create only selected columns
                if (!request.selectedColumns.contains(colMeta.columnName)) return

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
                        String choices = enumType.options.collect { "${it.code}=${it.label}" }.join("; ")
                        r.createCell(6).setCellValue(choices)
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

    private List<String> extractHeaders(Map<String, Object> firstRow, ExportRequest request) {
        if (!firstRow) return []

        if (request.selectedColumns && !request.selectedColumns.isEmpty()) {
            Map<String, String> rowKeysLowerMap = firstRow.keySet().collectEntries { [it.toLowerCase(), it] }
            List<String> matchedHeaders = []
            for (String userCol : request.selectedColumns) {
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
        return ".do"
    }

    private String getExportFileExtension(ExportRequest request) {
        DataExportFormat format = request.format
        if (format == DataExportFormat.EXCEL) return ".xlsx"
        if (format == DataExportFormat.CSV && !request.includeDictionary) return ".csv"
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
        Boolean includeDictionary = true
        Boolean activeResidentsOnly = true

        // Granular Privacy Controls via Enums
        NameAnonymizationMode nameAnonymizationMode = NameAnonymizationMode.INITIALS
        PhoneAnonymizationMode phoneAnonymizationMode = PhoneAnonymizationMode.MASK
        GpsAnonymizationMode gpsAnonymizationMode = GpsAnonymizationMode.ROUNDED
        DateAnonymizationMode dateAnonymizationMode = DateAnonymizationMode.EXACT

        Integer maxRows
    }
}
