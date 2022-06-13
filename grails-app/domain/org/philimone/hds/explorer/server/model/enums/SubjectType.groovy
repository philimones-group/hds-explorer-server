package org.philimone.hds.explorer.server.model.enums

enum SubjectType {
    /*
    12. Onde é que a visita esta sendo realizada?
    */

    REGION    ("Region", "subjectType.region"),
    HOUSEHOLD ("Household","subjectType.household"),
    MEMBER    ("Member", "subjectType.member"),
    USER      ("User", "subjectType.user")

    String code
    String name

    SubjectType(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, SubjectType> MAP = new HashMap<>()

    static {
        for (SubjectType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static SubjectType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}