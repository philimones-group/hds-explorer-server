package org.philimone.hds.explorer.server.model.enums

import groovy.transform.CompileStatic

@CompileStatic
enum SyncEntity {

    SETTINGS           (0, "syncEntity.settings.label", "settings"),
    PARAMETERS         (1, "syncEntity.parameters.label", "params"),
    MODULES            (2, "syncEntity.modules.label", "modules"),
    FORMS              (3, "syncEntity.forms.label", "forms"),
    CORE_FORMS         (4, "syncEntity.coreforms.label", "coreforms"),
    CORE_FORMS_EXT     (5, "syncEntity.coreforms.label", "coreformsext"),
    CORE_FORMS_EXT_FILES (6, "syncEntity.coreforms.label", "coreformsext"),
    CORE_FORMS_OPTIONS (7, "syncEntity.coreformsoptions.label", "coreformsoptions"),
    USERS              (8, "syncEntity.users.label", "users"),
    DATASETS           (9, "syncEntity.datasets.label", "datasets"),
    DATASETS_CSV_FILES (10, "syncEntity.datasetsCsvFiles.label", "datasetsCsvFiles"),
    TRACKING_LISTS     (11, "syncEntity.trackingLists.label", "trackinglists"),
    HOUSEHOLDS_DATASETS (12, "syncEntity.householddatasets.label", "householddatasets"),
    ROUNDS             (13, "syncEntity.rounds.label", "rounds"),
    REGIONS            (14, "syncEntity.regions.label", "regions"),
    HOUSEHOLDS         (15, "syncEntity.households.label", "households"),
    MEMBERS            (16, "syncEntity.members.label", "members"),
    RESIDENCIES        (17, "syncEntity.residencies.label", "residencies"),
    DEMOGRAPHICS_EVENTS(18, "syncEntity.demographicsevents.label", "demographicsevents"),
    VISITS             (19, "syncEntity.visits.label", "visits"),
    HEAD_RELATIONSHIPS (20, "syncEntity.headRelationships.label", "headrelationships"),
    MARITAL_RELATIONSHIPS (21, "syncEntity.maritalRelationships.label", "maritalrelationships"),
    INMIGRATIONS       (22, "syncEntity.inmigrations.label", "inmigrations"),
    OUTMIGRATIONS      (23, "syncEntity.outmigrations.label", "outmigrations"),
    PREGNANCY_REGISTRATIONS (24, "syncEntity.pregnancyRegistrations.label", "pregnancyregistrations"),
    PREGNANCY_OUTCOMES (25, "syncEntity.pregnancyOutcomes.label", "pregnancyoutcomes"),
    PREGNANCY_VISITS   (26, "syncEntity.pregnancyVisits.label", "pregnancyvisits"),
    DEATHS             (27, "syncEntity.deaths.label", "deaths"),
    INCOMPLETE_VISITS  (28, "syncEntity.incomplete_visits.label", "incompletevisits"),
    REGION_HEADS       (29, "syncEntity.regionheads.label", "regionheads"),
    HOUSEHOLD_PROXY_HEADS (30, "syncEntity.proxyheads.label", "proxyheads")

    final int code
    final String name
    final String filename

    SyncEntity(int code, String name, String filename){
        this.code = code
        this.name = name
        this.filename = filename
    }

    int getId(){
        code
    }

    String getXmlFilename(){
        "${filename}.xml"
    }

    String getZipFilename(){
        "${filename}.zip"
    }

    @Override
    String toString() {
        return name
    }

    /* Finding Enum by code */
    private static final Map<Integer, SyncEntity> MAP = new HashMap<>()

    static {
        for (SyncEntity e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static SyncEntity getFrom(Integer code) {
        return code==null ? null : MAP.get(code)
    }
}
