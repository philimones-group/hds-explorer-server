package org.philimone.hds.explorer.server.model.enums.temporal

enum HeadRelationshipStartType {

    ENUMERATION          ("ENU", "eventType.enumeration"),
    BIRTH                ("BIR", "eventType.birth"),
    INTERNAL_INMIGRATION ("ENT", "eventType.internal_inmigration"),
    EXTERNAL_INMIGRATION ("XEN", "eventType.external_inmigration"),
    NEW_HEAD_OF_HOUSEHOLD ("NHH", "eventType.new_hoh") //Event Related to the Head of Household

    final String code;
    final String name;

    HeadRelationshipStartType(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }
}