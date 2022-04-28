package org.philimone.hds.explorer.server.model.enums;

public enum IncompleteVisitReason {
    /*
    12. Onde é que a visita esta sendo realizada?
    */

    UNAVAILABLE_TODAY ("UNAVAILABLE_TODAY", "incompleteVisitReason.unavail_today"),
    UNAVAILABLE_ROUND ("UNAVAILABLE_ROUND","incompleteVisitReason.unavail_round"),
    UNWILLING         ("UNWILLING","incompleteVisitReason.unwilling"),
    HOSPITALIZED      ("HOSPITALIZED","incompleteVisitReason.hospitalized"),
    RELOCATED         ("RELOCATED","incompleteVisitReason.relocated"),
    WITHDREW          ("WITHDREW","incompleteVisitReason.withdrew"),
    DEAD              ("DEAD","incompleteVisitReason.dead"),
    OTHER             ("OTHER", "incompleteVisitReason.other"),
    INVALID_ENUM      ( "-1", "invalid_enum_value")

    public String code;
    public String name;

    IncompleteVisitReason(String code, String name){
        this.code = code
        this.name = name
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, IncompleteVisitReason> MAP = new HashMap<>();

    static {
        for (IncompleteVisitReason e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static IncompleteVisitReason getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}