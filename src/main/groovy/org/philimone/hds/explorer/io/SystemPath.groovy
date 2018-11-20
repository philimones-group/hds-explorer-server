package org.philimone.hds.explorer.io

/**
 * Created by paul on 5/6/15.
 */
class SystemPath {
    static String HOME_PATH = "/var/lib/hds-explorer" /* we must ensure that this path is created and tomcat has access to it*/
    static String GEN_PATH = "generated-files" /* xml files that will be downloaded to the mobile app */
    static String BIN_PATH = "bin"
    static String LOG_PATH = "logs"
    static String EXT_PATH = "a-docs" /* uploaded files or attachments docs directory */

    static String getGeneratedFilesPath(){
        return "${HOME_PATH}${File.separator}${GEN_PATH}"
    }

    static String getBinariesPath(){
        return "${HOME_PATH}${File.separator}${BIN_PATH}"
    }

    static String getLogsPath(){
        return "${HOME_PATH}${File.separator}${LOG_PATH}"
    }

    static String getExternalDocsPath(){
        return "${HOME_PATH}${File.separator}${EXT_PATH}"
    }
}

