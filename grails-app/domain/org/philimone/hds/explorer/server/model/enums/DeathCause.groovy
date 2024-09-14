package org.philimone.hds.explorer.server.model.enums

enum DeathCause {
    /*
    3.1. Cause of Death
    */
    MALARIA   ("MALARIA", "deathCause.malaria"),
    HIV_AIDS  ("HIV_AIDS", "deathCause.hiv_aids"),
    TB        ("TB", "deathCause.tb"),
    OTHER_VB_INFECTION ("OTHER_VB_INFECTION", "deathCause.other_vb_infection"),
    ACCIDENT  ("ACCIDENT", "deathCause.accident"),
    CRIME     ("CRIME", "deathCause.crime"),
    AGE       ("AGE", "deathCause.age"),
    UNKNOWN   ("UNKNOWN", "deathCause.unknown"),
    OTHER     ("OTHER", "deathCause.other"),
    DONT_KNOW ("DONT_KNOW", "deathCause.dont_know"),

    public String code;
    public String name;

    DeathCause(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, DeathCause> MAP = new HashMap<>();

    static {
        for (DeathCause e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static DeathCause getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}