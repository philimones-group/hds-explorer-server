package org.philimone.hds.explorer.server.model.enums.temporal

enum RegionHeadStartType {

    NEW_HEAD_OF_REGION ("NHR", "eventType.new_hor")

    final String code
    final String name

    RegionHeadStartType(String code, String name){
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
    private static final Map<String, RegionHeadStartType> MAP = new HashMap<>()

    static {
        for (RegionHeadStartType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static RegionHeadStartType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}