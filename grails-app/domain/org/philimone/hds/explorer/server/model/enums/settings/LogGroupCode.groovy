package org.philimone.hds.explorer.server.model.enums.settings

enum LogGroupCode {

    GROUP_IMPORT_DATA_OPENHDS       (1, "logreport.import.openhds"),
    GROUP_IMPORT_DATA_XLSHDS        (2, "logreport.import.hdsxls"),
    GROUP_UPLOAD_TRACKING_LISTS     (3, "logreport.upload.trackinglists.basic.label"),
    GROUP_GENERATE_FILES            (4, "logreport.export.files.label"),
    GROUP_SYNC_DSS_DATA_FROM_CLIENT (5, "logreport.sync.syncdss"),
    GROUP_SYNC_MANAGER              (6, "logreport.sync.syncdss")

    Integer code
    String name

    LogGroupCode(Integer code, String name){
        this.code = code
        this.name = name
    }

    Integer getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<Integer, LogGroupCode> MAP = new HashMap<>()

    static {
        for (LogGroupCode e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static LogGroupCode getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}
