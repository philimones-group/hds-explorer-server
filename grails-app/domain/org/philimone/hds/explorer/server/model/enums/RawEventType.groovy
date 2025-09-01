package org.philimone.hds.explorer.server.model.enums

enum RawEventType {

    EVENT_REGION                       (0,  "syncdss.sync.region.label"),
    EVENT_HOUSEHOLD                    (1,  "syncdss.sync.household.label"),
    EVENT_VISIT                        (2,  "syncdss.sync.visit.label"),
    EVENT_MEMBER_ENU                   (3,  "syncdss.sync.memberenu.label"),
    EVENT_EXTERNAL_INMIGRATION_ENTRY   (4,  "syncdss.sync.externalinmigration.entry.label"),
    EVENT_INTERNAL_INMIGRATION         (5,  "syncdss.sync.inmigration.label"),
    EVENT_EXTERNAL_INMIGRATION_REENTRY (6,  "syncdss.sync.externalinmigration.reentry.label"),
    EVENT_HOUSEHOLD_RELOCATION         (7,  "syncdss.sync.householdrelocation.label"),
    EVENT_PREGNANCY_REGISTRATION       (8,  "syncdss.sync.pregnancyreg.label"),
    EVENT_PREGNANCY_OUTCOME            (9,  "syncdss.sync.pregnancyoutcome.label"),
    EVENT_PREGNANCY_VISIT              (91, "syncdss.sync.pregnancyvisit.label"),
    EVENT_MARITAL_RELATIONSHIP         (92, "syncdss.sync.maritalreg.label"),
    EVENT_CHANGE_HEAD_OF_HOUSEHOLD     (93, "syncdss.sync.changehead.label"),
    EVENT_INCOMPLETE_VISIT             (94, "syncdss.sync.incompletevisit.label"),
    EVENT_OUTMIGRATION                 (95, "syncdss.sync.outmigration.label"),
    EVENT_DEATH                        (96, "syncdss.sync.death.label"),
    EVENT_CHANGE_HEAD_OF_REGION        (97, "syncdss.sync.changeregionhead.label"),
    EVENT_CHANGE_PROXY_HEAD            (98, "syncdss.sync.changeproxyheads.label");


    Integer code
    String name

    RawEventType(int code, String name){
        this.code = code
        this.name = name
    }

    int getId(){
        return code
    }

    @Override
    String toString() {
        return name
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