package org.philimone.hds.explorer.server.model.enums

enum NoVisitReason {
    /*
    2.1.1. Why is not possible to perform the visit to this Household?
    */
    HOUSE_OCCUPIED ("HOUSE_OCCUPIED", "noVisitReason.hh_occupied"),
    HOUSE_NOT_FOUND ("HOUSE_NOT_FOUND", "noVisitReason.hh_notfound"),
    HOUSE_DESTROYED ("HOUSE_DESTROYED", "noVisitReason.hh_destroyed"),
    HOUSE_ABANDONED ("HOUSE_ABANDONED", "noVisitReason.hh_abandoned"),
    HOUSE_VACANT ("HOUSE_VACANT", "noVisitReason.hh_vacant"),
    NO_RESPONDENT ("NO_RESPONDENT", "noVisitReason.no_respondent"),
    REFUSE ("REFUSE", "noVisitReason.refuse"),
    OTHER ("OTHER", "noVisitReason.other"),;

    public String code;
    public String name;

    NoVisitReason(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, NoVisitReason> MAP = new HashMap<>();

    static {
        for (NoVisitReason e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static NoVisitReason getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}