package org.philimone.hds.explorer.server.model.enums

enum VisitReason {
    /*
    12. Onde é que a visita esta sendo realizada?
    */

    UPDATE_ROUNDS ("UPDATE", "visitReason.update_round"),
    BASELINE      ("BASELINE","visitReason.baseline"),
    NEW_HOUSEHOLD ("NEW_HOUSE","visitReason.newhouse"),
    DATA_CLEANING ("CLEAN","visitReason.dataclean"),
    MIGRATION     ("MIGRATION", "visitReason.migration"),
    OTHER         ("OTHER", "visitReason.other")

    String code
    String name

    VisitReason(String code, String name){
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
    private static final Map<String, VisitReason> MAP = new HashMap<>()

    static {
        for (VisitReason e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static VisitReason getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}