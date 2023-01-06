package org.philimone.hds.explorer.server.model.enums

enum MaritalStatus {

    SINGLE ("SIN", "maritalStatus.single"),
    MARRIED ("MAR", "maritalStatus.married"),
    DIVORCED ("DIV", "maritalStatus.divorced"),
    SEPARATED ("SEP","maritalStatus.separated"),
    WIDOWED ("WID","maritalStatus.widowed"),
    LIVING_TOGHETER ("LIV","maritalStatus.living_togheter")

    String code
    String name

    MaritalStatus(String code, String name){
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
    private static final Map<String, MaritalStatus> MAP = new HashMap<>()

    static {
        for (MaritalStatus e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static MaritalStatus getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}
