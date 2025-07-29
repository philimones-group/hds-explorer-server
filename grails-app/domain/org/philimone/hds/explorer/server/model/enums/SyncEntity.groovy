package org.philimone.hds.explorer.server.model.enums

import groovy.transform.CompileStatic

@CompileStatic
enum SyncEntity {

    SETTINGS           (0, "syncEntity.settings.label", "settings"),
    PARAMETERS         (1, "syncEntity.parameters.label", "params"),
    MODULES            (2, "syncEntity.modules.label", "modules"),
    FORMS              (3, "syncEntity.forms.label", "forms"),
    CORE_FORMS_EXT     (4, "syncEntity.coreforms.label", "coreforms"),
    CORE_FORMS_OPTIONS (5, "syncEntity.coreformsoptions.label", "coreformsoptions"),
    USERS              (6, "syncEntity.users.label", "users"),
    DATASETS           (7, "syncEntity.datasets.label", "datasets"),
    DATASETS_CSV_FILES (8, "syncEntity.datasetsCsvFiles.label", "datasetsCsvFiles"),
    TRACKING_LISTS     (9, "syncEntity.trackingLists.label", "trackinglists"),
    HOUSEHOLDS_DATASETS (10, "syncEntity.householddatasets.label", "householddatasets"),
    ROUNDS             (11, "syncEntity.rounds.label", "rounds"),
    REGIONS            (12, "syncEntity.regions.label", "regions"),
    HOUSEHOLDS         (13, "syncEntity.households.label", "households"),
    MEMBERS            (14, "syncEntity.members.label", "members"),
    RESIDENCIES        (15, "syncEntity.residencies.label", "residencies"),
    DEMOGRAPHICS_EVENTS(16, "syncEntity.demographicsevents.label", "demographicsevents"),
    VISITS             (17, "syncEntity.visits.label", "visits"),
    HEAD_RELATIONSHIPS (18, "syncEntity.headRelationships.label", "headrelationships"),
    MARITAL_RELATIONSHIPS (19, "syncEntity.maritalRelationships.label", "maritalrelationships"),
    INMIGRATIONS       (20, "syncEntity.inmigrations.label", "inmigrations"),
    OUTMIGRATIONS      (21, "syncEntity.outmigrations.label", "outmigrations"),
    PREGNANCY_REGISTRATIONS (22, "syncEntity.pregnancyRegistrations.label", "pregnancyregistrations"),
    PREGNANCY_OUTCOMES (23, "syncEntity.pregnancyOutcomes.label", "pregnancyoutcomes"),
    PREGNANCY_VISITS   (24, "syncEntity.pregnancyVisits.label", "pregnancyvisits"),
    DEATHS             (25, "syncEntity.deaths.label", "deaths"),
    INCOMPLETE_VISITS  (26, "syncEntity.incomplete_visits.label", "incompletevisits"),
    REGION_HEADS       (27, "syncEntity.regionheads.label", "regionheads")

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
