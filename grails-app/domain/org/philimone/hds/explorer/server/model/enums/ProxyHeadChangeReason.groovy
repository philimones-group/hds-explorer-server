package org.philimone.hds.explorer.server.model.enums

enum ProxyHeadChangeReason {

    ADMIN_APPOINTMENT    ("AAP", "proxyHeadChangeReason.admin_appointment"),
    INSTITUTION_REQUIRED ("INS", "proxyHeadChangeReason.institution_required"),
    TEMP_ABSENCE_OF_HEAD ("TAH", "proxyHeadChangeReason.temp_absence_of_head"),
    LEGAL_GUARDIANSHIP   ("LGU", "proxyHeadChangeReason.legal_guardianship"),
    REPLACEMENT          ("REP", "proxyHeadChangeReason.replacement"),
    RESIGNATION          ("RES", "proxyHeadChangeReason.resignation"),
    DECEASED             ("DEC", "proxyHeadChangeReason.deceased"),
    OTHER                ("OTH", "proxyHeadChangeReason.other")

    String code
    String name

    ProxyHeadChangeReason(String code, String name){
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
    private static final Map<String, ProxyHeadChangeReason> MAP = new HashMap<>()

    static {
        for (ProxyHeadChangeReason e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static ProxyHeadChangeReason getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}