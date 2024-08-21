package org.philimone.hds.explorer.server.model.enums

enum ValidatableEntity {


    RESIDENCY ("residency", "syncdss.sync.residency.label"),
    HEAD_RELATIONSHIP ("head_relationship", "syncdss.sync.headrelationship.label"),
    MARITAL_RELATIONSHIP ("marital_relationship", "syncdss.sync.maritalreg.label"),

    final String name
    final String label

    ValidatableEntity(String name, String tag){
        this.name = name
        this.label = tag
    }

    String getId(){
        name()
    }

    @Override
    String toString() {
        return label
    }

    /* Finding Enum by code */
    private static final Map<String, ValidatableEntity> MAP = new HashMap<>()

    static {
        for (ValidatableEntity e: values()) {
            MAP.put(e.name(), e)
        }
    }

    public static ValidatableEntity getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }


}