package org.philimone.hds.explorer.server.model.enums

enum MaritalStartStatus {

    MARRIED ("MAR", "maritalStatus.married"),
    LIVING_TOGHETER ("LIV","maritalStatus.living_togheter")

    String code
    String name

    MaritalStartStatus(String code, String name){
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

    /* Finding Enum by code */
    private static final Map<String, MaritalStartStatus> MAP = new HashMap<>()

    static {
        for (MaritalStartStatus e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static MaritalStartStatus getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}
