package org.philimone.hds.explorer.server.model.enums

enum FormCollectType {

    NORMAL_COLLECT("NORMAL_COLLECT", "formCollectType.NORMAL_COLLECT"), //Currently Living Here
    PREVIOUS_FORM_COLLECTED    ("PREVIOUS_FORM_COLLECTED", "formCollectType.PREVIOUS_FORM_COLLECTED"),
    CALCULATE_EXPRESSION       ("CALCULATE_EXPRESSION","formCollectType.CALCULATE_EXPRESSION")

    String code
    String name

    FormCollectType(String code, String name){
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
    private static final Map<String, FormCollectType> MAP = new HashMap<>()

    static {
        for (FormCollectType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static FormCollectType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}