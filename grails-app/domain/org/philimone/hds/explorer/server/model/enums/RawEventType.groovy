package org.philimone.hds.explorer.server.model.enums

enum RawEventType {

    EVENT_REGION                       (0),
    EVENT_HOUSEHOLD                    (1),
    EVENT_VISIT                        (2),
    EVENT_MEMBER_ENU                   (3),
    EVENT_EXTERNAL_INMIGRATION_ENTRY   (4),
    EVENT_INTERNAL_INMIGRATION         (5),
    EVENT_EXTERNAL_INMIGRATION_REENTRY (6),
    EVENT_HOUSEHOLD_RELOCATION         (7),
    EVENT_PREGNANCY_REGISTRATION       (8),
    EVENT_PREGNANCY_OUTCOME            (9),
    EVENT_PREGNANCY_VISIT              (91),
    EVENT_MARITAL_RELATIONSHIP         (92),
    EVENT_CHANGE_HEAD_OF_HOUSEHOLD     (93),
    EVENT_INCOMPLETE_VISIT             (94),
    EVENT_OUTMIGRATION                 (95),
    EVENT_DEATH                        (96),
    EVENT_CHANGE_HEAD_OF_REGION        (97),
    EVENT_CHANGE_PROXY_HEAD            (98)


    Integer code

    RawEventType(int code){
        this.code = code
    }

    int getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<Integer, RawEventType> MAP = new HashMap<>()

    static {
        for (RawEventType e: values()) {
            MAP.put(e.code, e)
        }
    }

    static RawEventType getFrom(Integer code) {
        return code==null ? null : MAP.get(code)
    }
}