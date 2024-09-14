package org.philimone.hds.explorer.server.model.enums

enum OutmigrationReason {
    /*
    5.2. Reason for Out-migration
    */
    WENT_WITH_RELATIVES ("WENT_WITH_RELATIVES", "outmigrationReason.went_with_relatives"),
    FARMING             ("FARMING", "outmigrationReason.farming"),
    MARITAL_CHANGE      ("MARITAL_CHANGE", "outmigrationReason.marital_change"),
    FISHING             ("FISHING", "outmigrationReason.fishing"),
    EDUCATION           ("EDUCATION", "outmigrationReason.education"),
    HEALTH_REASON       ("HEALTH_REASON", "outmigrationReason.health_reason"),
    WORK                ("WORK", "outmigrationReason.work"),
    NEW_HOUSE           ("NEW_HOUSE", "outmigrationReason.new_house"),
    OTHER               ("OTHER", "outmigrationReason.other"),
    DONT_KNOW           ("DONT_KNOW", "outmigrationReason.dont_know"),

    public String code;
    public String name;

    OutmigrationReason(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, OutmigrationReason> MAP = new HashMap<>();

    static {
        for (OutmigrationReason e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static OutmigrationReason getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}