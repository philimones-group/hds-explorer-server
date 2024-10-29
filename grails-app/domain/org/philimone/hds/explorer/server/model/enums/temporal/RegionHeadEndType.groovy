package org.philimone.hds.explorer.server.model.enums.temporal

enum RegionHeadEndType {

    NOT_APPLICABLE           ("NA", "eventType.not_applicable"), //Currently Living Here
    EXTERNAL_OUTMIGRATION    ("EXT", "eventType.external_outmigration"),
    DEATH_OF_HEAD            ("DHR", "eventType.death_of_hoh"), //Event Related to the Head of Household
    CHANGE_OF_HEAD_OF_REGION ("CHR", "eventType.change_of_hoh") //Event Related to the Head of Household

    final String code
    final String name

    RegionHeadEndType(String code, String name){
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

    /* Finding Enum by code */
    private static final Map<String, RegionHeadEndType> MAP = new HashMap<>()

    static {
        for (RegionHeadEndType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static RegionHeadEndType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}