package org.philimone.hds.explorer.server.model.enums

enum ProxyHeadType {

    RESIDENT     ("RESIDENT", "proxyHeadType.resident"),
    NON_RESIDENT ("NON_RESIDENT", "proxyHeadType.non_resident"),
    NON_DSS_MEMBER ("NON_DSS_MEMBER", "proxyHeadType.non_dss_member")

    String code
    String name

    ProxyHeadType(String code, String name){
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
    private static final Map<String, ProxyHeadType> MAP = new HashMap<>()

    static {
        for (ProxyHeadType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static ProxyHeadType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }

}