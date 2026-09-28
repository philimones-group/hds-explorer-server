package org.philimone.hds.explorer.server.model.enums

enum NameAnonymizationMode {

    FULL     ("FULL", "nameAnonymizationMode.full"),
    INITIALS ("INITIALS", "nameAnonymizationMode.initials"),
    REMOVE   ("REMOVE", "nameAnonymizationMode.remove")

    String code
    String name

    NameAnonymizationMode(String code, String name) {
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
    private static final Map<String, NameAnonymizationMode> MAP = new HashMap<>()

    static {
        for (NameAnonymizationMode e : values()) {
            MAP.put(e.code, e)
        }
    }

    public static NameAnonymizationMode getFrom(String code) {
        return code == null ? null : MAP.get(code)
    }
}
