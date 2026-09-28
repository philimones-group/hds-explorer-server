package org.philimone.hds.explorer.server.model.enums

enum DataExportStatus {

    EXECUTING ("EXECUTING", "dataExportStatus.executing"),
    COMPLETED ("COMPLETED", "dataExportStatus.completed"),
    FAILED    ("FAILED", "dataExportStatus.failed")

    String code
    String name

    DataExportStatus(String code, String name) {
        this.code = code
        this.name = name
    }

    String getId() {
        return code
    }

    @Override
    String toString() {
        return name
    }

    /* Finding Enum by code */
    private static final Map<String, DataExportStatus> MAP = new HashMap<>()

    static {
        for (DataExportStatus e : values()) {
            MAP.put(e.code, e)
        }
    }

    public static DataExportStatus getFrom(String code) {
        return code == null ? null : MAP.get(code)
    }
}
