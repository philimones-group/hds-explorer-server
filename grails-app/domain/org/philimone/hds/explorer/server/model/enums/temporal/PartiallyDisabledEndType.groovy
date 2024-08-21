package org.philimone.hds.explorer.server.model.enums.temporal

import org.philimone.hds.explorer.server.model.enums.MaritalEndStatus

/**
 * This enum type concatenates the endType of Residency, HeadRelationship with the endStatus of MaritalRelationship
 * Used on PartiallyDisabled domain class to store end events
 */
enum PartiallyDisabledEndType {

    NOT_APPLICABLE        ("NA", "eventType.not_applicable"),          //Residency or HeadRelationship or MaritalRelationship
    INTERNAL_OUTMIGRATION ("CHG", "eventType.internal_outmigration"),  //Residency or HeadRelationship
    EXTERNAL_OUTMIGRATION ("EXT", "eventType.external_outmigration"),  //Residency or HeadRelationship
    DEATH                 ("DTH", "eventType.death"),                  //Residency or HeadRelationship
    DEATH_OF_HEAD_OF_HOUSEHOLD ("DHH", "eventType.death_of_hoh"),      //HeadRelationship
    CHANGE_OF_HEAD_OF_HOUSEHOLD("CHH", "eventType.change_of_hoh"),     //HeadRelationship
    DIVORCED       ("DIV", "maritalStatus.divorced"),                  //MaritalRelationship
    SEPARATED      ("SEP","maritalStatus.separated"),                  //MaritalRelationship
    WIDOWED        ("WID","maritalStatus.widowed")                     //MaritalRelationship

    final String code
    final String name

    PartiallyDisabledEndType(String code, String name){
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

    ResidencyEndType getResidencyEndType(){
        return ResidencyEndType.getFrom(code)
    }

    HeadRelationshipEndType getHeadRelationshipEndType(){
        return HeadRelationshipEndType.getFrom(code)
    }

    MaritalEndStatus getMaritalEndStatus(){
        return MaritalEndStatus.getFrom(code)
    }

    /* Finding Enum by code */
    private static final Map<String, PartiallyDisabledEndType> MAP = new HashMap<>()

    static {
        for (PartiallyDisabledEndType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static PartiallyDisabledEndType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

    static PartiallyDisabledEndType getFromHeadRelationship(HeadRelationshipEndType endType) {
        if (endType == null) return NOT_APPLICABLE

        for (def type : values()) {
            if (type.code.equals(endType.code)) return type
        }

        return NOT_APPLICABLE
    }

    static PartiallyDisabledEndType getFromResidency(ResidencyEndType endType) {
        if (endType == null) return NOT_APPLICABLE

        for (def type : values()) {
            if (type.code.equals(endType.code)) return type
        }

        return NOT_APPLICABLE
    }

    static PartiallyDisabledEndType getFromMaritalRelationship(MaritalEndStatus endType) {
        if (endType == null) return NOT_APPLICABLE

        for (def type : values()) {
            if (type.code.equals(endType.code)) return type
        }

        return NOT_APPLICABLE
    }

}