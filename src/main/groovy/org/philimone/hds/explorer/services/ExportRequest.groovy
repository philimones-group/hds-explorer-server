package org.philimone.hds.explorer.services

import org.philimone.hds.explorer.server.model.enums.DataExportFormat
import org.philimone.hds.explorer.server.model.enums.DataExportItem
import org.philimone.hds.explorer.server.model.enums.DateAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.GpsAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.NameAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.PhoneAnonymizationMode

import java.time.LocalDate

class ExportRequest {
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
        return (includeMediaFiles && hasMediaFields) || (hasRepeatTables && format != DataExportFormat.EXCEL) || format.isSyntaxScriptFormat() || (format == DataExportFormat.CSV && includeDictionary)
    }
}
