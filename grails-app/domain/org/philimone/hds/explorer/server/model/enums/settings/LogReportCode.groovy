package org.philimone.hds.explorer.server.model.enums.settings

/**
 * Created by paul on 4/5/17.
 */
enum LogReportCode {

    /*LogReportCode for events of Data Import from OpenHDS */
    REPORT_IMPORT_FROM_OPENHDS_FIELDWORKERS (10, "logreport"),
    REPORT_IMPORT_FROM_OPENHDS_HOUSEHOLDS (11, "logreport"),
    REPORT_IMPORT_FROM_OPENHDS_INDIVIDUALS (12, "logreport"),
    REPORT_IMPORT_FROM_OPENHDS_RESIDENCIES (13, "logreport"),

    /*LogReportCode for events of Data Import from HDS-XLS Files - must provide template */
    REPORT_IMPORT_HDSXLS_HOUSEHOLDS (20, "logreport"),
    REPORT_IMPORT_HDSXLS_INDIVIDUALS (21, "logreport"),

    /*LogReportCode for events of Uploading Tracking Lists */
    REPORT_UPLOAD_TRACKING_LISTS_BASIC (30, "logreport"),
    REPORT_UPLOAD_TRACKING_LISTS_W_EXTRA_DATA (31, "logreport"),

    /*LogReportCode for events that sync data from clients to HDS Explorer */
    REPORT_DSS_EVENTS_SYNC(40, "logreport"),
    REPORT_DSS_EVENTS_EXECUTE_SYNC(41, "logreport"),
    REPORT_DSS_EVENTS_RESET_ERRORS(42, "logreport"),

    /*LogReportCode for Data Validation and Sync Manager*/
    REPORT_SYNC_MANAGER_EXECUTE_ALL_EVENTS (60, "logreport-sync-all"),
    REPORT_SYNC_MANAGER_EXECUTE_COMPILE_EVENTS (61, "logreport-sync-compile"),
    REPORT_SYNC_MANAGER_EXECUTE_EVENTS (62, "logreport-sync-events"),
    REPORT_SYNC_MANAGER_EXECUTE_RESET_ERRORS (63, "logreport-sync-errors"),
    REPORT_SYNC_MANAGER_EXECUTE_RESTORE_TEMP_DISABLED (64, "logreport-restore-invalidated"),


    /*LogReportCode for events of Generating ZIP XML Files */
    REPORT_GENERATE_SETTINGS_ZIP_XML_FILES (51, "logreport"),
    REPORT_GENERATE_EXTERNAL_DATASETS_ZIP_XML_FILES (52, "logreport"),
    REPORT_GENERATE_TRACKING_LISTS_ZIP_XML_FILES (53, "logreport"),
    REPORT_GENERATE_HOUSEHOLDS_DATASETS_ZIP_XML_FILES (54, "logreport"),
    REPORT_GENERATE_DSS_EVENTS_ZIP_XML_FILES (55, "logreport"),

    /*LogReportCode for events of Generating ZIP XML Files */
    REPORT_DATA_RECONCILIATION_HOUSEHOLDS_STATUSES (70, "logreport"),
    REPORT_DATA_RECONCILIATION_MEMBERS_STATUSES (71, "logreport")

    Integer code
    String name

    LogReportCode(Integer code, String name){
        this.code = code
        this.name = name
    }

    Integer getId(){
        return code
    }

    @Override
    String toString() {
        return "${code}"
    }

    /* Finding Enum by code */
    private static final Map<Integer, LogReportCode> MAP = new HashMap<>()

    static {
        for (LogReportCode e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static LogReportCode getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}
