package org.philimone.hds.explorer.server.model.enums

enum NewBornStatus {

    ALIVE      ("ALIVE", "newBornStatus.alive"),
    STILLBORN  ("SBR", "newBornStatus.stillborn"),
    MISCARRIAGE("MIS", "newBornStatus.miscarriage"),
    ABORTION   ("ABT", "newBornStatus.abortion"),
    DIED_AFTER_BIRTH ("DIED_AFTER_BIRTH", "newBornStatus.died_after_birth")

    String code
    String name

    NewBornStatus(String code, String name){
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
    private static final Map<String, NewBornStatus> MAP = new HashMap<>()

    static {
        for (NewBornStatus e: values()) {
            MAP.put(e.code, e)
        }
    }

    static NewBornStatus getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}