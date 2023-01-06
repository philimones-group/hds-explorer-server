package org.philimone.hds.explorer.server.model.enums

enum FormSubjectType {

    REGION         ("REGION", "formSubjectType.REGION"),
    HOUSEHOLD      ("HOUSEHOLD","formSubjectType.HOUSEHOLD"),
    MEMBER         ("MEMBER", "formSubjectType.MEMBER"),
    HOUSEHOLD_HEAD ("HOUSEHOLD_HEAD", "formSubjectType.HOUSEHOLD_HEAD")

    String code
    String name

    FormSubjectType(String code, String name){
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
    private static final Map<String, FormSubjectType> MAP = new HashMap<>()

    static {
        for (FormSubjectType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static FormSubjectType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}