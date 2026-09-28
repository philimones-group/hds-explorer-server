package org.philimone.hds.explorer.server.model.enums

enum DateAnonymizationMode {

    EXACT       ("EXACT", "dateAnonymizationMode.exact"),
    SHIFT_DATES ("SHIFT_DATES", "dateAnonymizationMode.shiftDates"),
    REMOVE      ("REMOVE", "dateAnonymizationMode.remove")

    String code
    String name

    DateAnonymizationMode(String code, String name) {
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
    private static final Map<String, DateAnonymizationMode> MAP = new HashMap<>()

    static {
        for (DateAnonymizationMode e : values()) {
            MAP.put(e.code, e)
        }
    }

    public static DateAnonymizationMode getFrom(String code) {
        return code == null ? null : MAP.get(code)
    }
}
