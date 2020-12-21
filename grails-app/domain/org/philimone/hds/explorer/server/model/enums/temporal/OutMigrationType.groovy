package org.philimone.hds.explorer.server.model.enums.temporal

enum OutMigrationType {

    INTERNAL ("CHG", "eventType.internal_outmigration"),
    EXTERNAL ("EXT", "eventType.external_outmigration")

    final String code;
    final String name;

    OutMigrationType(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, OutMigrationType> MAP = new HashMap<>();

    static {
        for (OutMigrationType e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static OutMigrationType getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}