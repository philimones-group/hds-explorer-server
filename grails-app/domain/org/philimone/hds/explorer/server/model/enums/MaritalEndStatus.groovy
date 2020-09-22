package org.philimone.hds.explorer.server.model.enums

enum MaritalEndStatus {

    NOT_APPLICABLE ("NA", "maritalStatus.not_applicable"), //Currently Living Here
    DIVORCED       ("DIV", "maritalStatus.divorced"),
    SEPARATED      ("SEP","maritalStatus.separated"),
    WIDOWED        ("WID","maritalStatus.widowed")

    String code
    String name

    MaritalEndStatus(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

}
