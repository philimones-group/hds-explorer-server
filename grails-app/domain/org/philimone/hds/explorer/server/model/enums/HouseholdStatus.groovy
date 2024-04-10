package org.philimone.hds.explorer.server.model.enums

enum HouseholdStatus {

    HOUSE_OCCUPIED ("HOUSE_OCCUPIED", "householdStatus.occupied"),
    HOUSE_NOT_FOUND ("HOUSE_NOT_FOUND", "householdStatus.notfound"),
    HOUSE_DESTROYED ("HOUSE_DESTROYED", "householdStatus.destroyed"),
    HOUSE_ABANDONED ("HOUSE_ABANDONED", "householdStatus.abandoned"),
    HOUSE_VACANT ("HOUSE_VACANT", "householdStatus.vacant"),
    OTHER         ("OTHER", "householdStatus.other"),

    public String code;
    public String name;

    HouseholdStatus(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, HouseholdStatus> MAP = new HashMap<>();

    static {
        for (HouseholdStatus e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static HouseholdStatus getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}