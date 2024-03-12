package org.philimone.hds.explorer.server.model.enums

enum BirthPlace {

    HOME ("HOME", "birthPlace.home"),
    HOSPITAL ("HOSPITAL", "birthPlace.hospital"),
    TRADITIONAL_MIDWIFE ("TRADITIONAL_MIDWIFE", "birthPlace.traditional_midwife"),
    HEALTH_CENTER_CLINIC ("HEALTH_CENTER_CLINIC", "birthPlace.health_center_clinic"),
    ON_THE_WAY ("ON_THE_WAY", "birthPlace.on_the_way"),
    PRIVATE_MAT_HOME ("PRIVATE_MAT_HOME", "birthPlace.private_mat_home"),
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

    @Override
    String toString() {
        return name
    }

    /* Finding Enum by code */
    private static final Map<String, BirthPlace> MAP = new HashMap<>()

    static {
        for (BirthPlace e: values()) {
            MAP.put(e.code, e)
        }
    }

    static BirthPlace getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}