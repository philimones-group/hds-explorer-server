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

    /*LogReportCode for events that export data to OpenHDS or HDS Explorer from ODK*/
    REPORT_DSS_ODK_CENSUS_SYNC (40, "logreport"),

    /*LogReportCode for events of Generating ZIP XML Files */
    REPORT_GENERATE_USERS_ZIP_XML_FILES (50, "logreport"),
    REPORT_GENERATE_HOUSEHOLDS_ZIP_XML_FILES (51, "logreport"),
    REPORT_GENERATE_MEMBERS_ZIP_XML_FILES (52, "logreport"),
    REPORT_GENERATE_SETTINGS_ZIP_XML_FILES (53, "logreport"),
    REPORT_GENERATE_TRACKING_LISTS_ZIP_XML_FILES (54, "logreport"),
    REPORT_GENERATE_RESIDENCIES_ZIP_XML_FILES (55, "logreport"),
    REPORT_GENERATE_HEAD_RELATIONSHIPS_ZIP_XML_FILES (56, "logreport"),
    REPORT_GENERATE_MARTIAL_RELATIONSHIPS_ZIP_XML_FILES (57, "logreport")

    Integer code
    String name

    LogReportCode(Integer code, String name){
        this.code = code
        this.name = name
    }

    Integer getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, LogReportCode> MAP = new HashMap<>();

    static {
        for (LogReportCode e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static LogReportCode getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }

}
