package org.philimone.hds.explorer.server.model.enums.temporal

enum InMigrationType {

    INTERNAL  ("ENT", "eventType.internal_inmigration"),
    EXTERNAL  ("XEN", "eventType.external_inmigration"),
    //RETURNING ( "XEN", "eventType.returning_inmigration") /* I dont think I will need this, entry_date and start_type suits enough to know that its a return to dss */

    final String code;
    final String name;

    InMigrationType(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, InMigrationType> MAP = new HashMap<>();

    static {
        for (InMigrationType e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static InMigrationType getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}