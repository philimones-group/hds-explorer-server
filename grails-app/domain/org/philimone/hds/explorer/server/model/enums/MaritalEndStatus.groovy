package org.philimone.hds.explorer.server.model.enums

enum MaritalEndStatus {

    NOT_APPLICABLE ("NA", "maritalStatus.not_applicable"), //Currently Living Here
    DIVORCED       ("DIV", "maritalStatus.divorced"),
    SEPARATED      ("SEP","maritalStatus.separated"),
    WIDOWED        ("WID","maritalStatus.widowed")

    String code
    String name

    MaritalEndStatus(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, MaritalEndStatus> MAP = new HashMap<>()

    static {
        for (MaritalEndStatus e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static MaritalEndStatus getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}
