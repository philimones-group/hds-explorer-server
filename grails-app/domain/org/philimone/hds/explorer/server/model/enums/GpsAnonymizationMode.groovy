package org.philimone.hds.explorer.server.model.enums

enum GpsAnonymizationMode {

    EXACT   ("EXACT", "gpsAnonymizationMode.exact"),
    ROUNDED ("ROUNDED", "gpsAnonymizationMode.rounded"),
    REMOVE  ("REMOVE", "gpsAnonymizationMode.remove")

    String code
    String name

    GpsAnonymizationMode(String code, String name) {
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
    private static final Map<String, GpsAnonymizationMode> MAP = new HashMap<>()

    static {
        for (GpsAnonymizationMode e : values()) {
            MAP.put(e.code, e)
        }
    }

    public static GpsAnonymizationMode getFrom(String code) {
        return code == null ? null : MAP.get(code)
    }
}
