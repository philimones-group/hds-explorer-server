package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.audit.CollectableEntity
import org.philimone.hds.explorer.server.model.enums.BirthPlace

import java.time.LocalDate

class PregnancyOutcome extends CollectableEntity {

    String id
    String code
    Member mother
    String motherCode
    Member father
    String fatherCode
    Integer numberOfOutcomes
    Integer numberOfLivebirths
    LocalDate outcomeDate
    BirthPlace birthPlace
    String birthPlaceOther
    Visit visit
    String visitCode

    static hasMany = [childs:PregnancyChild]

    static constraints = {
        id maxSize: 32

        code unique: true
        mother nullable: false
        motherCode nullable: false, blank: false
        father nullable: false
        fatherCode nullable: false, blank: false
        numberOfOutcomes min: 1
        numberOfLivebirths min: 0
        outcomeDate nullable: false
        birthPlace nullable: true
        birthPlaceOther nullable: true, blank: true

        visit nullable: false
        visitCode nullable: false, blank: false
    }

    static mapping = {
        table 'pregnancy_outcome'

        id column: "id", generator: 'uuid'

        code column: "code"
        mother column: "mother_id"
        motherCode column: "mother_code", index: "idx_mother_code"
        father column: "father_id"
        fatherCode column: "father_code", index: "idx_father_code"
        numberOfOutcomes column: "number_of_outcomes"
        numberOfLivebirths column: "number_of_livebirths"
        outcomeDate column: "outcome_date"
        birthPlace column: "birthplace", enumType: "identity"
        birthPlaceOther column: "birthplace_other"

        visit column: "visit_id"
        visitCode column: "visit_code", index: "idx_visit_code"
    }
}
