package org.philimone.hds.explorer.server.model.enums;

import groovy.transform.CompileStatic;

@CompileStatic
enum SyncEntity {

    SETTINGS           (0, "syncEntity.settings.label", "settings"),
    PARAMETERS         (1, "syncEntity.parameters.label", "params"),
    MODULES            (2, "syncEntity.modules.label", "modules"),
    FORMS              (3, "syncEntity.forms.label", "forms"),
    CORE_FORMS_EXT     (4, "syncEntity.coreforms.label", "coreforms"),
    USERS              (5, "syncEntity.users.label", "users"),
    DATASETS           (6, "syncEntity.datasets.label", "datasets"),
    DATASETS_CSV_FILES (7, "syncEntity.datasetsCsvFiles.label", "datasetsCsvFiles"),
    TRACKING_LISTS     (8, "syncEntity.trackingLists.label", "trackinglists"),
    HOUSEHOLDS_DATASETS (9, "syncEntity.householddatasets.label", "householddatasets"),
    ROUNDS             (10, "syncEntity.rounds.label", "rounds"),
    REGIONS            (11, "syncEntity.regions.label", "regions"),
    HOUSEHOLDS         (12, "syncEntity.households.label", "households"),
    MEMBERS            (13, "syncEntity.members.label", "members"),
    RESIDENCIES        (14, "syncEntity.residencies.label", "residencies"),
    DEMOGRAPHICS_EVENTS(15, "syncEntity.demographicsevents.label", "demographicsevents"),
    VISITS             (16, "syncEntity.visits.label", "visits"),
    HEAD_RELATIONSHIPS (17, "syncEntity.headRelationships.label", "headrelationships"),
    MARITAL_RELATIONSHIPS (18, "syncEntity.maritalRelationships.label", "maritalrelationships"),
    INMIGRATIONS       (19, "syncEntity.inmigrations.label", "inmigrations"),
    OUTMIGRATIONS      (20, "syncEntity.outmigrations.label", "outmigrations"),
    PREGNANCY_REGISTRATIONS (21, "syncEntity.pregnancyRegistrations.label", "pregnancyregistrations"),
    PREGNANCY_OUTCOMES (22, "syncEntity.pregnancyOutcomes.label", "pregnancyoutcomes"),
    DEATHS             (23, "syncEntity.deaths.label", "deaths"),
    INCOMPLETE_VISITS  (24, "syncEntity.incomplete_visits.label", "incompletevisits")

    final int code;
    final String name;
    final String filename

    SyncEntity(int code, String name, String filename){
        this.code = code;
        this.name = name;
        this.filename = filename;
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

    /* Finding Enum by code */
    private static final Map<Integer, SyncEntity> MAP = new HashMap<>();

    static {
        for (SyncEntity e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static SyncEntity getFrom(Integer code) {
        return code==null ? null : MAP.get(code);
    }
}
