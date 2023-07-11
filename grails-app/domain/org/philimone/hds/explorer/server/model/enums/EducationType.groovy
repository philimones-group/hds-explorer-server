package org.philimone.hds.explorer.server.model.enums

enum EducationType {

    NO_EDUCATION         ("NO_EDU", "member.education.NO_EDUCATION"),
    PRIMARY_EDUCATION    ("EDU_01", "member.education.PRIMARY_EDUCATION"),
    SECONDARY_EDUCATION  ("EDU_02", "member.education.SECONDARY_EDUCATION"),
    BACHELORS_DEGRE      ("EDU_03", "member.education.BACHELORS_DEGRE"),
    MASTERS_DEGRE        ("EDU_04", "member.education.MASTERS_DEGRE"),
    DOCTORATE_DEGRE      ("EDU_05", "member.education.DOCTORATE_DEGRE"),
    OTHER                ("OTHER",  "member.education.OTHER"),
    UNKNOWN              ("UNK",    "member.education.UNKNOWN");

    String code
    String name

    EducationType(String code, String name){
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
    private static final Map<String, EducationType> MAP = new HashMap<>()

    static {
        for (EducationType e: values()) {
            MAP.put(e.code, e)
        }
    }

    static EducationType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}