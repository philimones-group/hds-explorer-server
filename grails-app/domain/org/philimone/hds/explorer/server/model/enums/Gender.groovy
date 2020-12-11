package org.philimone.hds.explorer.server.model.enums

enum Gender {

    MALE   ("M", "default.gender.M"),
    FEMALE ("F", "default.gender.F")

    String code
    String name

    Gender(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, Gender> MAP = new HashMap<>();

    static {
        for (Gender e: values()) {
            MAP.put(e.code, e);
        }
    }

    public static Gender getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}