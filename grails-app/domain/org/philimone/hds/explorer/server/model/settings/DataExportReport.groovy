package org.philimone.hds.explorer.server.model.settings

import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.enums.DataExportItem
import org.philimone.hds.explorer.server.model.enums.DataExportStatus
import org.philimone.hds.explorer.server.model.enums.DateAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.GpsAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.NameAnonymizationMode
import org.philimone.hds.explorer.server.model.enums.PhoneAnonymizationMode

import java.time.LocalDate
import java.time.LocalDateTime

class DataExportReport {

    String id
    DataExportItem exportItem
    String datasetName
    String datasetLabel
    String format
    DataExportStatus status

    // Progress Monitoring
    Integer progressPercent = 0
    String currentStep = "Initializing export..."

    // Filter Parameters Snapshot
    LocalDate referenceDate
    String gender
    Integer ageMin
    Integer ageMax
    String regionCode
    Double randomSamplePercent
    Boolean includeDictionary = true
    Boolean activeResidentsOnly = true

    // Granular Anonymization & Privacy Enums
    NameAnonymizationMode nameAnonymizationMode = NameAnonymizationMode.INITIALS
    PhoneAnonymizationMode phoneAnonymizationMode = PhoneAnonymizationMode.MASK
    GpsAnonymizationMode gpsAnonymizationMode = GpsAnonymizationMode.ROUNDED
    DateAnonymizationMode dateAnonymizationMode = DateAnonymizationMode.EXACT

    // File Names & Sizes in SystemPath.externalDocsPath
    String datasetFileName
    String codebookFileName
    String zipFileName

    Long datasetFileSize = 0L
    Long codebookFileSize = 0L
    Long zipFileSize = 0L

    Long totalRecords = 0L
    Long totalColumns = 0L
    Long executionTimeMs = 0L
    String errorMessage

    User createdBy
    LocalDateTime createdDate = LocalDateTime.now()

    static constraints = {
        id maxSize: 32
        exportItem nullable: false
        datasetName nullable: false
        datasetLabel nullable: false
        format nullable: false
        status nullable: false

        progressPercent nullable: true
        currentStep nullable: true, maxSize: 500

        referenceDate nullable: true
        gender nullable: true
        ageMin nullable: true
        ageMax nullable: true
        regionCode nullable: true
        randomSamplePercent nullable: true
        includeDictionary nullable: false
        activeResidentsOnly nullable: false

        nameAnonymizationMode nullable: false
        phoneAnonymizationMode nullable: false
        gpsAnonymizationMode nullable: false
        dateAnonymizationMode nullable: false

        datasetFileName nullable: true
        codebookFileName nullable: true
        zipFileName nullable: true

        datasetFileSize nullable: true
        codebookFileSize nullable: true
        zipFileSize nullable: true

        totalRecords nullable: true
        totalColumns nullable: true
        executionTimeMs nullable: true
        errorMessage nullable: true, maxSize: 2000

        createdBy nullable: true
        createdDate nullable: false
    }

    static mapping = {
        table 'data_export_report'

        id column: "id", generator: 'uuid'

        exportItem column: 'export_item', enumType: 'identity'
        datasetName column: 'dataset_name'
        datasetLabel column: 'dataset_label'
        format column: 'format'
        status column: 'status', enumType: 'identity', index: 'idx_export_status'

        progressPercent column: 'progress_percent'
        currentStep column: 'current_step'

        referenceDate column: 'reference_date'
        gender column: 'gender'
        ageMin column: 'age_min'
        ageMax column: 'age_max'
        regionCode column: 'region_code'
        randomSamplePercent column: 'random_sample_percent'
        includeDictionary column: 'include_dictionary'
        activeResidentsOnly column: 'active_residents_only'

        nameAnonymizationMode column: 'name_anonymization_mode', enumType: 'identity'
        phoneAnonymizationMode column: 'phone_anonymization_mode', enumType: 'identity'
        gpsAnonymizationMode column: 'gps_anonymization_mode', enumType: 'identity'
        dateAnonymizationMode column: 'date_anonymization_mode', enumType: 'identity'

        datasetFileName column: 'dataset_file_name'
        codebookFileName column: 'codebook_file_name'
        zipFileName column: 'zip_file_name'

        datasetFileSize column: 'dataset_file_size'
        codebookFileSize column: 'codebook_file_size'
        zipFileSize column: 'zip_file_size'

        totalRecords column: 'total_records'
        totalColumns column: 'total_columns'
        executionTimeMs column: 'execution_time_ms'
        errorMessage column: 'error_message'

        createdBy column: 'created_by'
        createdDate column: 'created_date'
    }
}
