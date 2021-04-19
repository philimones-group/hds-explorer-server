package org.philimone.hds.explorer.server.model.enums.temporal

enum ExternalInMigrationType {

    ENTRY,
    REENTRY

    /* Finding Enum by code */
    private static final Map<String, ExternalInMigrationType> MAP = new HashMap<>();

    static {
        for (ExternalInMigrationType e: values()) {
            MAP.put(e.name(), e);
        }
    }

    public static ExternalInMigrationType getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}