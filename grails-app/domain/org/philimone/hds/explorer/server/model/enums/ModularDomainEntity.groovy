package org.philimone.hds.explorer.server.model.enums;

enum ModularDomainEntity {
    REGION (1, "Regions"),
    HOUSEHOLD (2, "Households"),
    MEMBER (3, "Members")

    int code
    String name

    ModularDomainEntity(int code, String name) {
        this.code = code
        this.name = name
    }

    Integer getId(){
        return code
    }

    @Override
    String toString() {
        return name
    }
/* Finding Enum by code */
    private static final Map<Integer, ModularDomainEntity> MAP = new HashMap<>();

    static {
        for (ModularDomainEntity e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static ModularDomainEntity getFrom(Integer code) {
        return code==null ? null : MAP.get(code);
    }
}