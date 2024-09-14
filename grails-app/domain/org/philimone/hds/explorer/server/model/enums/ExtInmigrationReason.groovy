package org.philimone.hds.explorer.server.model.enums

enum ExtInmigrationReason {
    /*
    4.5. Reason for Immigration
    */
    CAME_WITH_RELATIVES ("CAME_WITH_RELATIVES", "extInmigrationReason.came_with_relatives"),
    FARMING             ("FARMING", "extInmigrationReason.farming"),
    MARITAL_CHANGE      ("MARITAL_CHANGE", "extInmigrationReason.marital_change"),
    FISHING             ("FISHING", "extInmigrationReason.fishing"),
    EDUCATION           ("EDUCATION", "extInmigrationReason.education"),
    HEALTH_REASON       ("HEALTH_REASON", "extInmigrationReason.health_reason"),
    WORK                ("WORK", "extInmigrationReason.work"),
    NEW_HOUSE           ("NEW_HOUSE", "extInmigrationReason.new_house"),
    OTHER               ("OTHER", "extInmigrationReason.other"),
    DONT_KNOW           ("DONT_KNOW", "extInmigrationReason.dont_know"),

    public String code;
    public String name;

    ExtInmigrationReason(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, ExtInmigrationReason> MAP = new HashMap<>();

    static {
        for (ExtInmigrationReason e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static ExtInmigrationReason getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}