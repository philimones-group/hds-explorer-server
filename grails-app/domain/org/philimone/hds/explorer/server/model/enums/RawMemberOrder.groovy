package org.philimone.hds.explorer.server.model.enums

/**
 * Used to order event execution when we are inserting individuals, to start with HEAD of Household
 */
enum RawMemberOrder {

    HEAD_OF_HOUSEHOLD (1, "HOH"), //"headRelationshipType.head_of_household"),
    SPOUSE            (2, "SPO"), //"headRelationshipType.spouse"),
    NOT_APPLICABLE    (9, "NA") //, "headRelationshipType.dont_know")

    Integer id
    String code

    RawMemberOrder(int id, String code){
        this.id = id
        this.code = code
    }

    int getId(){
        return id
    }

    /* Finding Enum by code */
    private static final Map<Integer, RawMemberOrder> MAP = new HashMap<>()
    private static final Map<String, RawMemberOrder> MAPCODE = new HashMap<>()

    static {
        for (RawMemberOrder e: values()) {
            MAP.put(e.id, e)
            MAPCODE.put(e.code, e)
        }
    }

    static RawMemberOrder getFrom(Integer id) {
        return id==null ? null : MAP.get(id)
    }

    static RawMemberOrder getFromCode(String code) {
        def e = code==null ? NOT_APPLICABLE : MAPCODE.get(code)
        return e==null ? NOT_APPLICABLE : e
    }
}