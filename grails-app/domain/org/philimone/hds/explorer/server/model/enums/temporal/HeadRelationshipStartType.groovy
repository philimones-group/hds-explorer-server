package org.philimone.hds.explorer.server.model.enums.temporal

enum HeadRelationshipStartType {

    ENUMERATION          ("ENU", "eventType.enumeration"),
    BIRTH                ("BIR", "eventType.birth"),
    INTERNAL_INMIGRATION ("ENT", "eventType.internal_inmigration"),
    EXTERNAL_INMIGRATION ("XEN", "eventType.external_inmigration"),
    NEW_HEAD_OF_HOUSEHOLD ("NHH", "eventType.new_hoh") //Event Related to the Head of Household

    final String code;
    final String name;

    HeadRelationshipStartType(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, HeadRelationshipStartType> MAP = new HashMap<>();

    static {
        for (HeadRelationshipStartType e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static HeadRelationshipStartType getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}