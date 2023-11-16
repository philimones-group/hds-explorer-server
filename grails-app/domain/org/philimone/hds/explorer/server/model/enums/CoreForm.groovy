package org.philimone.hds.explorer.server.model.enums

enum CoreForm {

    REGION_FORM ("rawRegion", "coreFormExtension.region.label"),
    HOUSEHOLD_FORM ("rawHousehold", "coreFormExtension.household.label"),
    VISIT_FORM ("rawVisit", "coreFormExtension.visit.label"),
    MEMBER_ENU_FORM ("rawMemberEnu", "coreFormExtension.memberenu.label"),
    MARITAL_RELATIONSHIP_FORM ("rawMaritalRelationship", "coreFormExtension.maritalreg.label"),
    INMIGRATION_FORM ("rawInMigration", "coreFormExtension.inmigration.label"),
    OUTMIGRATION_FORM ("rawOutMigration", "coreFormExtension.outmigration.label"),
    PREGNANCY_REGISTRATION_FORM ("rawPregnancyRegistration", "coreFormExtension.pregnancyreg.label"),
    PREGNANCY_OUTCOME_FORM ("rawPregnancyOutcome", "coreFormExtension.pregnancyoutcome.label"),
    DEATH_FORM ("rawDeath", "coreFormExtension.death.label"),
    CHANGE_HEAD_FORM ("rawChangeHead", "coreFormExtension.changehead.label"),
    INCOMPLETE_VISIT_FORM ("rawIncompleteVisit", "coreFormExtension.incompletevisit.label")


    String code
    String name

    CoreForm(String code, String name){
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
