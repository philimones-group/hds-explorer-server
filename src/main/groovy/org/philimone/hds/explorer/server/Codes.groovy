package org.philimone.hds.explorer.server

/**
 * Created by paul on 4/5/17.
 */
class Codes {

    static final int GROUP_IMPORT_DATA_OPENHDS = 1
    static final int GROUP_IMPORT_DATA_XLSHDS = 2 /* Import Data from HDS-XLS files - excel files with HDS DataSet */
    static final int GROUP_UPLOAD_TRACKING_LISTS = 3
    static final int GROUP_GENERATE_FILES = 4


    /*Codes for events of Data Import from OpenHDS */
    static final int REPORT_IMPORT_FROM_OPENHDS_FIELDWORKERS = 10
    static final int REPORT_IMPORT_FROM_OPENHDS_HOUSEHOLDS = 11
    static final int REPORT_IMPORT_FROM_OPENHDS_INDIVIDUALS = 12
    static final int REPORT_IMPORT_FROM_OPENHDS_RESIDENCIES = 13
    // DEATHS, MIGRATIONS, BIRTHS CAN BE CONSIDERED LATER


    /*Codes for events of Data Import from HDS-XLS Files - must provide template */
    static final int REPORT_IMPORT_HDSXLS_HOUSEHOLDS = 20
    static final int REPORT_IMPORT_HDSXLS_INDIVIDUALS = 21
    //RESIDENCIES AND OTHER DATA CAN BE CONSIDERED LATER

    /*Codes for events of Uploading Tracking Lists */
    static final int REPORT_UPLOAD_TRACKING_LISTS_BASIC = 30
    static final int REPORT_UPLOAD_TRACKING_LISTS_W_EXTRA_DATA = 31


    /*Codes for events of Generating ZIP XML Files */
    static final int REPORT_GENERATE_USERS_ZIP_XML_FILES = 50
    static final int REPORT_GENERATE_HOUSEHOLDS_ZIP_XML_FILES = 51
    static final int REPORT_GENERATE_INDIVIDUALS_ZIP_XML_FILES = 52
    static final int REPORT_GENERATE_SETTINGS_ZIP_XML_FILES = 53
    static final int REPORT_GENERATE_TRACKING_LISTS_ZIP_XML_FILES = 54


    /* Application default parameters */
    static final String PARAMS_TRACKING_LISTS_MAX_DATA_COLUMNS = "hds.explorer.trackinglists.max_data_columns"

}
