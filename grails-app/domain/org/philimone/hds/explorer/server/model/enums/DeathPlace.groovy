package org.philimone.hds.explorer.server.model.enums

enum DeathPlace {
    /*
    4.1. Place of Death
    */
    HOME             ("HOME", "deathPlace.home"),
    HOSPITAL         ("HOSPITAL", "deathPlace.hospital"),
    HEALTH_CENTER    ("HEALTH_CENTER", "deathPlace.health_center"),
    TRAD_HEALER      ("TRAD_HEALER", "deathPlace.trad_healer"),
    PRIVATE_MAT_HOME ("PRIVATE_MAT_HOME", "deathPlace.private_mat_home"),
    OTHER            ("OTHER", "deathPlace.other"),

    public String code;
    public String name;

    DeathPlace(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, DeathPlace> MAP = new HashMap<>();

    static {
        for (DeathPlace e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static DeathPlace getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}