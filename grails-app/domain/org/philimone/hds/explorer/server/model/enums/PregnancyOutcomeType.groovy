package org.philimone.hds.explorer.server.model.enums

enum PregnancyOutcomeType {

    LIVEBIRTH ("LBR", "pregOutcomeType.livebirth"),
    STILLBIRTH ("SBR",	"pregOutcomeType.stillbirth"),
    MISCARRIAGE("MIS",	"pregOutcomeType.miscarriage"),
    ABORTION ("ABT", "pregOutcomeType.abortion")

    String code
    String name

    PregnancyOutcomeType(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, PregnancyOutcomeType> MAP = new HashMap<>();

    static {
        for (PregnancyOutcomeType e: values()) {
            MAP.put(e.code, e);
        }
    }

    static PregnancyOutcomeType getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }

}