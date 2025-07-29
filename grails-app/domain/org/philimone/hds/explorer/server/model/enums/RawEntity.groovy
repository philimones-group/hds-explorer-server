package org.philimone.hds.explorer.server.model.enums

enum RawEntity {

    REGION ("syncdss.sync.region.label", "rawRegion"),
    HOUSEHOLD ("syncdss.sync.households.label", "rawHousehold"),
    MEMBER ("syncdss.sync.members.label", "rawMember"),
    MEMBER_ENUMERATION ("syncdss.sync.memberenu.label", "rawMemberEnu"),
    RESIDENCY ("syncdss.sync.residency.label", "rawResidency"),
    HEAD_RELATIONSHIP ("syncdss.sync.headrelationship.label", "rawHeadRelationship"),
    MARITAL_RELATIONSHIP ("syncdss.sync.maritalreg.label", "rawMaritalRelationship"),
    ROUND ("syncdss.sync.round.label", "rawRound"),
    VISIT ("syncdss.sync.visit.label", "rawVisit"),
    IN_MIGRATION ("syncdss.sync.inmigration.label", "rawInMigration"),
    OUT_MIGRATION ("syncdss.sync.outmigration.label", "rawOutMigration"),
    PREGNANCY_REGISTRATION ("syncdss.sync.pregnancyreg.label", "rawPregnancyRegistration"),
    PREGNANCY_OUTCOME ("syncdss.sync.pregnancyoutcome.label", "rawPregnancyOutcome"),
    PREGNANCY_CHILD ("syncdss.sync.pregnancychild.label", "rawPregnancyChild"),
    PREGNANCY_VISIT ("syncdss.sync.pregnancyvisit.label", "rawPregnancyVisit"),
    PREGNANCY_VISIT_CHILD ("syncdss.sync.pregnancyvisitchild.label", "rawPregnancyVisitChild"),
    EXTERNAL_INMIGRATION ("syncdss.sync.externalinmigration.label", "rawExternalInMigration"),
    DEATH ("syncdss.sync.death.label", "rawDeath"),
    CHANGE_HEAD_OF_HOUSEHOLD ("syncdss.sync.changehead.label", "rawChangeHead"),
    INCOMPLETE_VISIT ("syncdss.sync.incompletevisit.label", "rawIncompleteVisit"),
    CHANGE_HEAD_OF_REGION ("syncdss.sync.changeregionhead.label", "rawChangeRegionHead"),
    HOUSEHOLD_RELOCATION ("syncdss.sync.householdrelocation.label", "rawHouseholdRelocation")

    final String name
    final String tag

    RawEntity(String name, String tag){
        this.name = name
        this.tag = tag
    }

    String getId(){
        name()
    }

    @Override
    String toString() {
        return name
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