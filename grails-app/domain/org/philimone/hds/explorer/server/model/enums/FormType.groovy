package org.philimone.hds.explorer.server.model.enums

enum FormType {

    REGULAR    ("REGULAR", "formType.REGULAR"), //Currently Living Here
    FORM_GROUP ("FORM_GROUP", "formType.FORM_GROUP")


    String code
    String name

    FormType(String code, String name){
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
    private static final Map<String, FormType> MAP = new HashMap<>()

    static {
        for (FormType e: values()) {
            MAP.put(e.code, e)
        }
    }

    public static FormType getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}