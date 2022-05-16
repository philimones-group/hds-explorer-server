package org.philimone.hds.explorer.server.model.enums

enum PregnancyStatus {

    PREGNANT ("PREGNANT", "pregnacyStatus.pregnant"),
    DELIVERED ("DELIVERED", "pregnacyStatus.delivered"),
    LOST_TRACK ("LOST_TRACK", "pregnacyStatus.lost_track")

    String code
    String name

    PregnancyStatus(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, PregnancyStatus> MAP = new HashMap<>()

    static {
        for (PregnancyStatus e: values()) {
            MAP.put(e.code, e)
        }
    }

    static PregnancyStatus getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}