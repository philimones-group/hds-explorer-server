package org.philimone.hds.explorer.server.model.enums

enum MaritalStartStatus {

    MARRIED ("MAR", "maritalStatus.married"),
    LIVING_TOGHETER ("LIV","maritalStatus.living_togheter")

    String code
    String name

    MaritalStartStatus(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

}
