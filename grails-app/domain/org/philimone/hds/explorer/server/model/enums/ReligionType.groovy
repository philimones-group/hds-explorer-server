package org.philimone.hds.explorer.server.model.enums

enum ReligionType {

    CHRISTIAN_CATHOLIC   ("REL_01", "member.religion.CHRISTIAN_CATHOLIC"),
    CHRISTIAN_ORTHODOX   ("REL_02", "member.religion.CHRISTIAN_ORTHODOX"),
    CHRISTIAN_PROTESTANT ("REL_03", "member.religion.CHRISTIAN_PROTESTANT"),
    MUSLIM               ("REL_04", "member.religion.MUSLIM"),
    JEWISH               ("REL_05", "member.religion.JEWISH"),
    HINDU                ("REL_06", "member.religion.HINDU"),
    BUDHIST              ("REL_07", "member.religion.BUDHIST"),
    TRADITIONAL_FAITH    ("REL_08", "member.religion.TRADITIONAL_FAITH"),
    ATHEIST              ("REL_09", "member.religion.ATHEIST"),
    NO_RELIGION          ("NO_REL", "member.religion.NO_RELIGION"),
    OTHER                ("OTHER",  "member.religion.OTHER"),
    UNKNOWN              ("UNK",    "member.religion.UNKNOWN");

    String code
    String name

    ReligionType(String code, String name){
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
    private static final Map<String, ReligionType> MAP = new HashMap<>()

    static {
        for (ReligionType e: values()) {
            MAP.put(e.code, e)
        }
    }

    static ReligionType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}