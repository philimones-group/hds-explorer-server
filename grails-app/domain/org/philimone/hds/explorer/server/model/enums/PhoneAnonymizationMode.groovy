package org.philimone.hds.explorer.server.model.enums

enum PhoneAnonymizationMode {

    FULL   ("FULL", "phoneAnonymizationMode.full"),
    MASK   ("MASK", "phoneAnonymizationMode.mask"),
    REMOVE ("REMOVE", "phoneAnonymizationMode.remove")

    String code
    String name

    PhoneAnonymizationMode(String code, String name) {
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
    private static final Map<String, PhoneAnonymizationMode> MAP = new HashMap<>()

    static {
        for (PhoneAnonymizationMode e : values()) {
            MAP.put(e.code, e)
        }
    }

    public static PhoneAnonymizationMode getFrom(String code) {
        return code == null ? null : MAP.get(code)
    }
}
