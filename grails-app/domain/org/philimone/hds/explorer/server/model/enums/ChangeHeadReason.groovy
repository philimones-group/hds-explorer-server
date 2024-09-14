package org.philimone.hds.explorer.server.model.enums

enum ChangeHeadReason {
    /*
    4.2. Reason for changing the Head of Household
    */
    RESIDENCY_CHANGE ("RESIDENCY_CHANGE", "changeHeadReason.residency_change"),
    OLD_AGE          ("OLD_AGE", "changeHeadReason.old_age"),
    DIVORCE          ("DIVORCE", "changeHeadReason.divorce"),
    WORK             ("WORK", "changeHeadReason.work"),
    HEALTH_REASONS   ("HEALTH_REASONS", "changeHeadReason.health_reasons"),
    DEATH            ("DEATH", "changeHeadReason.death"),
    OTHER            ("OTHER", "changeHeadReason.other"),

    public String code;
    public String name;

    ChangeHeadReason(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, ChangeHeadReason> MAP = new HashMap<>();

    static {
        for (ChangeHeadReason e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static ChangeHeadReason getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}