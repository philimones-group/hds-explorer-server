package org.philimone.hds.explorer.server.model.enums

enum HouseholdType {

    REGULAR       ("REGULAR", "householdType.regular"),
    INSTITUTIONAL ("INSTITUTIONAL", "householdType.institutional")

    String code
    String name

    HouseholdType(String code, String name){
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
    private static final Map<String, HouseholdType> MAP = new HashMap<>()

    static {
        for (HouseholdType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static HouseholdType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}