package org.philimone.hds.explorer.server.model.enums

enum SubjectType {

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

    @Override
    String toString() {
        return name
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