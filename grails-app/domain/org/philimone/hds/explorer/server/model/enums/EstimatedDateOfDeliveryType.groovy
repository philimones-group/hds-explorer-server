package org.philimone.hds.explorer.server.model.enums

enum EstimatedDateOfDeliveryType {

    ULTRASOUND ("1", "eddtype.ultrasound"),
    LAST_MENSTRUAL_PERIOD ("2", "eddtype.last_menstrual_period"),
    SYMPHISIS_FUNDAL_EIGHT ("3", "eddtype.symphisis_fundal_eight"),
    DONT_KNOW ("99", "eddtype.dont_know")

    String code
    String name

    EstimatedDateOfDeliveryType(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    /* Finding Enum by code */
    private static final Map<String, EstimatedDateOfDeliveryType> MAP = new HashMap<>();

    static {
        for (EstimatedDateOfDeliveryType e: values()) {
            MAP.put(e.code, e);
        }
    }

    static EstimatedDateOfDeliveryType getFrom(String code) {
        return code==null ? null : MAP.get(code);
    }
}