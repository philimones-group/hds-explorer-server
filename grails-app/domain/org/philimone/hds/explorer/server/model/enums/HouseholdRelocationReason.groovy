package org.philimone.hds.explorer.server.model.enums

enum HouseholdRelocationReason {

    RESIDENCY_CHANGE   ("RESIDENCY_CHANGE", "householdRelocationReason.residency_change"),
    EMPLOYMENT_WORK    ("EMPLOYMENT_WORK", "householdRelocationReason.employment_work"),
    MARITAL_CHANGE     ("MARITAL_CHANGE", "householdRelocationReason.marital_change"),
    HEALTH_REASONS     ("HEALTH_REASONS", "householdRelocationReason.health_reasons"),
    EDUCATION          ("EDUCATION", "householdRelocationReason.education"),
    DISASTER_ENVIRONMENT ("DISASTER_ENVIRONMENT", "householdRelocationReason.disaster_environment"),
    EVICTION_HOUSING   ("EVICTION_HOUSING", "householdRelocationReason.eviction_housing"),
    SECURITY_CONFLICT  ("SECURITY_CONFLICT", "householdRelocationReason.security_conflict"),
    OTHER              ("OTHER", "householdRelocationReason.other")

    public String code
    public String name

    HouseholdRelocationReason(String code, String name) {
        this.code = code
        this.name = name
    }

    public String getId() {
        return code
    }

    private static final Map<String, HouseholdRelocationReason> MAP = new HashMap<>()

    static {
        for (HouseholdRelocationReason e : values()) {
            MAP.put(e.code, e)
        }
    }

    static HouseholdRelocationReason getFrom(String code) {
        return code == null ? null : MAP.get(code)
    }
}