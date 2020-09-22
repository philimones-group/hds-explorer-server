package org.philimone.hds.explorer.server.model.enums.temporal

enum ResidencyEndType {

    NOT_APPLICABLE        ("NA", "eventType.not_applicable"), //Currently Living Here
    INTERNAL_OUTMIGRATION ("CHG", "eventType.internal_outmigration"),
    EXTERNAL_OUTMIGRATION ("EXT", "eventType.external_outmigration"),
    DEATH                 ("DTH", "eventType.death")

    final String code;
    final String name;

    ResidencyEndType(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }
}