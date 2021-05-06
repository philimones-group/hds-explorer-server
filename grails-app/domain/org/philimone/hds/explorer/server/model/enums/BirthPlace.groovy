package org.philimone.hds.explorer.server.model.enums

enum BirthPlace {

    HOME ("HOME", "birthPlace.home"),
    HOSPITAL ("HOSPITAL", "birthPlace.hospital"),
    TRADITIONAL_MIDWIFE ("TRADITIONAL_MIDWIFE", "birthPlace.traditional_midwife"),
    HEALTH_CENTER_CLINIC ("HEALTH_CENTER_CLINIC", "birthPlace.health_center_clinic"),
    OTHER ("OTHER", "birthPlace.other")

    String code
    String name

    BirthPlace(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, BirthPlace> MAP = new HashMap<>();

    static {
        for (BirthPlace e: values()) {
            MAP.put(e.code, e);
        }
    }

    static BirthPlace getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }

}