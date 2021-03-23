package org.philimone.hds.explorer.server.model.enums

enum RawEntity {

    REGION,
    HOUSEHOLD,
    MEMBER,
    MEMBER_ENUMERATION,
    RESIDENCY,
    HEAD_RELATIONSHIP,
    MARITAL_RELATIONSHIP,
    ROUND,
    VISIT,
    IN_MIGRATION,
    OUT_MIGRATION,
    PREGNANCY_REGISTRATION,
    PREGNANCY_OUTCOME,
    PREGNANCY_CHILD,
    EXTERNAL_INMIGRATION,
    DEATH,
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