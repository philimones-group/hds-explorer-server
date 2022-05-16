package org.philimone.hds.explorer.server.model.enums

enum RegionLevel {

    HIERARCHY_1 ("hierarchy1", "region.level1.label"),
    HIERARCHY_2 ("hierarchy2", "region.level2.label"),
    HIERARCHY_3 ("hierarchy3", "region.level3.label"),
    HIERARCHY_4 ("hierarchy4", "region.level4.label"),
    HIERARCHY_5 ("hierarchy5", "region.level5.label"),
    HIERARCHY_6 ("hierarchy6", "region.level6.label"),
    HIERARCHY_7 ("hierarchy7", "region.level7.label"),
    HIERARCHY_8 ("hierarchy8", "region.level8.label"),
    HIERARCHY_9 ("hierarchy9", "region.level9.label"),
    HIERARCHY_10 ("hierarchy10", "region.level10.label")

    String code
    String name

    RegionLevel(String code, String name) {
        this.code = code
        this.name = name
    }

    String getId(){
        code
    }

    /* Finding Enum by code */
    private static final Map<String, RegionLevel> MAP = new HashMap<>()

    static {
        for (RegionLevel e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static RegionLevel getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}