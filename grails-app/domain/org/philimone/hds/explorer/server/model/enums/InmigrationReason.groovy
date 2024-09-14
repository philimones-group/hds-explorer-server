package org.philimone.hds.explorer.server.model.enums

enum InmigrationReason {
    /*
    2.6. Reason for Migration
    */
    CAME_WITH_RELATIVES ("CAME_WITH_RELATIVES", "inmigrationReason.came_with_relatives"),
    FARMING             ("FARMING", "inmigrationReason.farming"),
    MARITAL_CHANGE      ("MARITAL_CHANGE", "inmigrationReason.marital_change"),
    FISHING             ("FISHING", "inmigrationReason.fishing"),
    EDUCATION           ("EDUCATION", "inmigrationReason.education"),
    HEALTH_REASON       ("HEALTH_REASON", "inmigrationReason.health_reason"),
    WORK                ("WORK", "inmigrationReason.work"),
    NEW_HOUSE           ("NEW_HOUSE", "inmigrationReason.new_house"),
    OTHER               ("OTHER", "inmigrationReason.other"),
    DONT_KNOW           ("DONT_KNOW", "inmigrationReason.dont_know"),

    public String code;
    public String name;

    InmigrationReason(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, InmigrationReason> MAP = new HashMap<>();

    static {
        for (InmigrationReason e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static InmigrationReason getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}