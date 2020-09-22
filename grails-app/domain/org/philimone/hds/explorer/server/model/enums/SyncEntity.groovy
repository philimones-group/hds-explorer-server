package org.philimone.hds.explorer.server.model.enums;

import groovy.transform.CompileStatic;

@CompileStatic
enum SyncEntity {

    PARAMETERS         (1, "syncEntity.parameters.label"),
    MODULES            (2, "syncEntity.modules.label"),
    FORMS              (3, "syncEntity.forms.label"),
    DATASETS           (4, "syncEntity.datasets.label"),
    DATASETS_CSV_FILES (5, "syncEntity.datasetsCsvFiles.label"),
    TRACKING_LISTS     (6, "syncEntity.trackingLists.label"),
    USERS              (7, "syncEntity.users.label"),
    REGIONS            (8, "syncEntity.regions.label"),
    HOUSEHOLDS         (9, "syncEntity.households.label"),
    MEMBERS            (10, "syncEntity.members.label");

    final int code;
    final String name;

    SyncEntity(int code, String name){
        this.code = code;
        this.name = name;
    }

    int getId(){
        return code
    }
}
