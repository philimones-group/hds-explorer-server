package org.philimone.hds.explorer.server.model.enums;

import groovy.transform.CompileStatic;

@CompileStatic
enum SyncEntity {

    PARAMETERS         (1, "syncEntity.parameters.label", "params"),
    MODULES            (2, "syncEntity.modules.label", "modules"),
    FORMS              (3, "syncEntity.forms.label", "forms"),
    DATASETS           (4, "syncEntity.datasets.label", "datasets"),
    DATASETS_CSV_FILES (5, "syncEntity.datasetsCsvFiles.label", "datasetsCsvFiles"),
    TRACKING_LISTS     (6, "syncEntity.trackingLists.label", "trackinglists"),
    USERS              (7, "syncEntity.users.label", "users"),
    REGIONS            (8, "syncEntity.regions.label", "regions"),
    HOUSEHOLDS         (9, "syncEntity.households.label", "households"),
    MEMBERS            (10, "syncEntity.members.label", "members"),
    RESIDENCIES        (11, "syncEntity.residencies.label", "residencies"),
    HEAD_RELATIONSHIPS (12, "syncEntity.headRelationships.label", "head_relationships"),
    MARITAL_RELATIONSHIPS (13, "syncEntity.maritalRelationships.label", "marital_relationships"),

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

    public static SyncEntity valueOf(Integer code) {
        return code==null ? null : MAP.get(code);
    }
}
