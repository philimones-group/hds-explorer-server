package org.philimone.hds.explorer.server.model.enums.temporal

enum HeadRelationshipEndType {

    NOT_APPLICABLE        ("NA", "eventType.not_applicable"), //Currently Living Here
    INTERNAL_OUTMIGRATION ("CHG", "eventType.internal_outmigration"),
    EXTERNAL_OUTMIGRATION ("EXT", "eventType.external_outmigration"),
    DEATH                 ("DTH", "eventType.death"),
    DEATH_OF_HEAD_OF_HOUSEHOLD ("DHH", "eventType.death_of_hoh"), //Event Related to the Head of Household
    CHANGE_OF_HEAD_OF_HOUSEHOLD("CHH", "eventType.change_of_hoh") //Event Related to the Head of Household

    final String code;
    final String name;

    HeadRelationshipEndType(String code, String name){
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