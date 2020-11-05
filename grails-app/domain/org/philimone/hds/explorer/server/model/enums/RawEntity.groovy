package org.philimone.hds.explorer.server.model.enums

enum RawEntity {

    CENSUS_REGION,
    CENSUS_HOUSEHOLD,
    CENSUS_MEMBER,
    PREGNANCY_REG,
    PREGNANCY_BIRTH,
    EXTERNAL_INMIGRATION,
    IN_MIGRATION,
    OUT_MIGRATION,
    DEATH_REG,
    CHANGE_HOH

    /* Finding Enum by code */
    private static final Map<String, RawEntity> MAP = new HashMap<>();

    static {
        for (RawEntity e: values()) {
            MAP.put(e.name(), e);
        }
    }

    public static RawEntity getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}