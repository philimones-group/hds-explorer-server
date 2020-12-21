package org.philimone.hds.explorer.server.model.enums

enum VisitLocationItem {
    /*
    12. Onde é que a visita esta sendo realizada?
    */

    HOME ("HOME", "visitLocation.home"),
    WORK_PLACE ("WORKPLACE","visitLocation.workplace"),
    HEALTH_UNIT ("HEALTHUNIT","visitLocation.healthunit"),
    OTHER_PLACE ("OTHERPLACE","visitLocation.other")

    String code
    String name

    VisitLocationItem(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, VisitLocationItem> MAP = new HashMap<>();

    static {
        for (VisitLocationItem e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static VisitLocationItem getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}