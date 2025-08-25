package org.philimone.hds.explorer.server.model.enums

enum ProxyHeadRole {

    ADMINISTRATOR            ("ADM", "proxyHeadRole.administrator"),
    WARDEN                   ("WRD", "proxyHeadRole.warden"),
    SOCIAL_WORKER            ("SOC", "proxyHeadRole.social_worker"),
    CARETAKER                ("CTK", "proxyHeadRole.caretaker"),
    LEGAL_GUARDIAN           ("LGU", "proxyHeadRole.legal_guardian"),
    FAMILY_REPRESENTATIVE    ("FAM", "proxyHeadRole.family_representative"),
    COMMUNITY_REPRESENTATIVE ("COM", "proxyHeadRole.community_representative"),
    RELIGIOUS_LEADER         ("REL", "proxyHeadRole.religious_leader"),
    MILITARY_COMMANDER       ("MIL", "proxyHeadRole.military_commander"),
    PRINCIPAL                ("PRI", "proxyHeadRole.principal"),
    DIRECTOR                 ("DIR", "proxyHeadRole.director"),
    OTHER                    ("OTH", "proxyHeadRole.other")

    String code
    String name

    ProxyHeadRole(String code, String name){
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

    private static final Map<String, ProxyHeadRole> MAP = new HashMap<>()

    static {
        for (ProxyHeadRole e: values()) {
            MAP.put(e.code, e)
        }
    }

    static ProxyHeadRole getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}