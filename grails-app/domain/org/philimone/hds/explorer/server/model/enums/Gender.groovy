package org.philimone.hds.explorer.server.model.enums

enum Gender {

    MALE   ("M", "default.gender.M"),
    FEMALE ("F", "default.gender.F")

    String code
    String name

    Gender(String code, String name){
        this.code = code
    }

    String getId(){
        return code
    }
}