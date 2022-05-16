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

    /* Finding Enum by code */
    private static final Map<String, HeadRelationshipType> MAP = new HashMap<>()

    static {
        for (HeadRelationshipType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static HeadRelationshipType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}