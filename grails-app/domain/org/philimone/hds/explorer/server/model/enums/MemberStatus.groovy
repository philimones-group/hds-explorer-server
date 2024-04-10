package org.philimone.hds.explorer.server.model.enums

enum MemberStatus {

    ACTIVE ("ACTIVE", "memberStatus.active"),
    INACTIVE ("INACTIVE", "memberStatus.inactive"),
    MEMBER_NOT_FOUND ("MEMBER_NOT_FOUND", "memberStatus.not_found");

    public String code;
    public String name;

    MemberStatus(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getId(){
        return code;
    }

    /* Finding Enum by code */
    private static final Map<String, MemberStatus> MAP = new HashMap<>();

    static {
        for (MemberStatus e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static MemberStatus getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}