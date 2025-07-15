package org.philimone.hds.explorer.server.model.enums

enum PregnancyVisitType {

    ANTEPARTUM ("ANTEPARTUM", "pregnancyVisitType.antepartum"),
    POSTPARTUM ("POSTPARTUM", "pregnancyVisitType.postpartum")

    String code
    String name

    PregnancyVisitType(String code, String name){
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
    private static final Map<String, PregnancyVisitType> MAP = new HashMap<>()

    static {
        for (PregnancyVisitType e: values()) {
            MAP.put(e.code, e)
        }
    }

    static PregnancyVisitType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}