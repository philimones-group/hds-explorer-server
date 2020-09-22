package org.philimone.hds.explorer.server.model.enums

enum HeadRelationshipType {

    HEAD_OF_HOUSEHOLD ("HOH", "headRelationshipType.head_of_household"),
    SPOUSE            ("SPO", "headRelationshipType.spouse"),
    SON_DAUGHTER      ("SON", "headRelationshipType.son_daughter"),
    BROTHER_SISTER    ("BRO", "headRelationshipType.brother_sister"),
    PARENT            ("PAR", "headRelationshipType.parent"),
    GRANDCHILD        ("GCH", "headRelationshipType.grandchild"),
    NOT_RELATED       ("NOR", "headRelationshipType.not_related"),
    OTHER_RELATIVE    ("OTH", "headRelationshipType.other"),
    DONT_KNOW         ("DNK", "headRelationshipType.dont_know")

    String code
    String name

    HeadRelationshipType(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

}