package org.philimone.hds.explorer.server.model.enums

enum CoreForm {

    REGION_FORM ("rawRegion", "coreFormExtension.region.label", "region_ext"),
    HOUSEHOLD_FORM ("rawHousehold", "coreFormExtension.household.label", "household_ext"),
    VISIT_FORM ("rawVisit", "coreFormExtension.visit.label", "visit_ext"),
    MEMBER_ENU_FORM ("rawMemberEnu", "coreFormExtension.memberenu.label", "member_ext"),
    MARITAL_RELATIONSHIP_FORM ("rawMaritalRelationship", "coreFormExtension.maritalreg.label", "marital_relationship_ext"),
    EXT_INMIGRATION_FORM ("rawExternalInMigration", "coreFormExtension.extinmigration.label", "in_migration_ext"),
    INMIGRATION_FORM ("rawInMigration", "coreFormExtension.inmigration.label", "in_migration_ext"),
    OUTMIGRATION_FORM ("rawOutMigration", "coreFormExtension.outmigration.label", "out_migration_ext"),
    PREGNANCY_REGISTRATION_FORM ("rawPregnancyRegistration", "coreFormExtension.pregnancyreg.label", "pregnancy_registration_ext"),
    PREGNANCY_OUTCOME_FORM ("rawPregnancyOutcome", "coreFormExtension.pregnancyoutcome.label", "pregnancy_outcome_ext"),
    PREGNANCY_VISIT_FORM ("rawPregnancyVisit", "coreFormExtension.pregnancyvisit.label", "pregnancy_visit_ext"),
    DEATH_FORM ("rawDeath", "coreFormExtension.death.label", "death_ext"),
    CHANGE_HEAD_FORM ("rawChangeHead", "coreFormExtension.changehead.label", "change_head_ext"),
    INCOMPLETE_VISIT_FORM ("rawIncompleteVisit", "coreFormExtension.incompletevisit.label", "incomplete_visit_ext"),
    CHANGE_REGION_HEAD_FORM ("rawChangeRegionHead", "coreFormExtension.changeregionhead.label", "change_region_head_ext"),
    HOUSEHOLD_RELOCATION_FORM ("rawHouseholdRelocation", "coreFormExtension.householdrelocation.label", "household_relocation_ext")

    String code
    String name
    String extension

    CoreForm(String code, String name, String extension){
        this.code = code
        this.name = name
        this.extension = extension
    }

    String getId(){
        return code
    }

    @Override
    String toString() {
        return name
    }

    /* Finding Enum by code */
    private static final Map<String, CoreForm> MAP = new HashMap<>()

    static {
        for (CoreForm e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static CoreForm getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}
