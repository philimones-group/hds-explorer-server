package org.philimone.hds.explorer.server.model.enums

enum RawEntity {

    REGION ("syncdss.sync.region.label"),
    HOUSEHOLD ("syncdss.sync.households.label"),
    MEMBER ("syncdss.sync.members.label"),
    MEMBER_ENUMERATION ("syncdss.sync.memberenu.label"),
    RESIDENCY ("syncdss.sync.residency.label"),
    HEAD_RELATIONSHIP ("syncdss.sync.headrelationship.label"),
    MARITAL_RELATIONSHIP ("syncdss.sync.maritalreg.label"),
    ROUND ("syncdss.sync.round.label"),
    VISIT ("syncdss.sync.visit.label"),
    IN_MIGRATION ("syncdss.sync.inmigration.label"),
    OUT_MIGRATION ("syncdss.sync.outmigration.label"),
    PREGNANCY_REGISTRATION ("syncdss.sync.pregnancyreg.label"),
    PREGNANCY_OUTCOME ("syncdss.sync.pregnancyoutcome.label"),
    PREGNANCY_CHILD ("syncdss.sync.pregnancychild.label"),
    EXTERNAL_INMIGRATION ("syncdss.sync.externalinmigration.label"),
    DEATH ("syncdss.sync.death.label"),
    CHANGE_HEAD_OF_HOUSEHOLD ("syncdss.sync.changehead.label"),
    INCOMPLETE_VISIT ("syncdss.sync.incompletevisit.label")

    final String name

    RawEntity(String name){

        this.name = name
    }

    String getId(){
        name()
    }

    /* Finding Enum by code */
    private static final Map<String, RawEntity> MAP = new HashMap<>()

    static {
        for (RawEntity e: values()) {
            MAP.put(e.name(), e)
        }
    }

    public static RawEntity getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }


}