package org.philimone.hds.explorer.server.model.enums.settings;

enum LogGroupCode {

    GROUP_IMPORT_DATA_OPENHDS       (1, "logreport.import.openhds"),
    GROUP_IMPORT_DATA_XLSHDS        (2, "logreport.import.hdsxls"),
    GROUP_UPLOAD_TRACKING_LISTS     (3, "logreport.upload.trackinglists.basic.label"),
    GROUP_GENERATE_FILES            (4, "logreport.export.files.label"),
    GROUP_SYNC_DSS_DATA_FROM_CLIENT (5, "logreport.sync.syncdss")

    Integer code
    String name

    LogGroupCode(Integer code, String name){
        this.code = code
    }

    Integer getId(){
        return code
    }

}
