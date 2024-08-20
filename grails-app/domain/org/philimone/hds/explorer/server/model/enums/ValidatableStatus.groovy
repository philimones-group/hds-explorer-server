package org.philimone.hds.explorer.server.model.enums

/**
 * A Processed Status represents four different states of collected data
 */
enum ValidatableStatus {

    ACTIVE               (0, "validatableStatus.active.label"),
    PARTIALLY_INACTIVE   (2, "validatableStatus.partially_inactive.label"),
    TEMPORARILY_INACTIVE (1, "validatableStatus.temporarily_inactive.label")

    Integer code
    String name

    ValidatableStatus(Integer code, String name){
        this.code = code
        this.name = name
    }

    Integer getId(){
        code
    }

    String toString(){
        name
    }

    /* Finding Enum by code */
    private static final Map<Integer, ValidatableStatus> MAP = new HashMap<>()

    static {
        for (ValidatableStatus e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static ValidatableStatus getFrom(Integer code) {
        return code==null ? null : MAP.get(code)
    }
}
