package org.philimone.hds.explorer.server.model.enums

enum VisitRespondentRelationship {
    /*
    2.2.1. Who is responding to this visit?
    */
    FIELDWORKER ("FIELDWORKER", "visitRespondentRelationship.fieldworker"),
    NEIGHBOR    ("NEIGHBOR", "visitRespondentRelationship.neighbor"),
    FAMILY      ("FAMILY", "visitRespondentRelationship.family"),
    OTHER       ("OTHER", "visitRespondentRelationship.other"),

    public String code;
    public String name;

    VisitRespondentRelationship(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, VisitRespondentRelationship> MAP = new HashMap<>();

    static {
        for (VisitRespondentRelationship e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static VisitRespondentRelationship getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}