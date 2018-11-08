package org.philimone.hds.explorer.io

/**
 * Created by paul on 5/6/15.
 */
class SystemPath {
    static String HOME_PATH = ""
    static String APP_PATH = "openva-ext-files"
    static String BIN_PATH = "openva-ext-files${File.separator}bin"
    static String LOG_PATH = "openva-ext-files${File.separator}logs"
    static String RESOURCES_PATH = "openva-ext-files${File.separator}exported-files"

    static String getAbsoluteAppPath(){
        return "${HOME_PATH}${APP_PATH}"
    }

    static String getAbsoluteBinPath(){
        return "${HOME_PATH}${BIN_PATH}"
    }

    static String getAbsoluteLogPath(){
        return "${HOME_PATH}${LOG_PATH}"
    }

    static String getAbsoluteResourcesPath(){
        return "${HOME_PATH}${RESOURCES_PATH}"
    }
}

