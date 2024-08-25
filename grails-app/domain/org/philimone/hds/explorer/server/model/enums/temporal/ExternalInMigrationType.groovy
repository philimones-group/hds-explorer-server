package org.philimone.hds.explorer.server.model.enums.temporal

enum ExternalInMigrationType {

    ENTRY   ("ENTRY", "externalInmigrationType.entry"),
    REENTRY ("REENTRY", "externalInmigrationType.reentry")

    /* Finding Enum by code */
    private static final Map<String, ExternalInMigrationType> MAP = new HashMap<>()

    String code
    String name

    ExternalInMigrationType(String code, String name) {
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    @Override
    String toString() {
        return name
    }

    static {
        for (ExternalInMigrationType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static ExternalInMigrationType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}