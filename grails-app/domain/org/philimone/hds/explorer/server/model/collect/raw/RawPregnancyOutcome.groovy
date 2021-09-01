package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.BirthPlace
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.Visit

import java.time.LocalDate
import java.time.LocalDateTime

class RawPregnancyOutcome {

    String id
    String code
    String motherCode
    String fatherCode
    Integer numberOfOutcomes
    LocalDate outcomeDate
    String birthPlace
    String birthPlaceOther
    String visitCode

    String modules

    String collectedBy //fieldWorkerId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    static hasMany = [childs:RawPregnancyChild]

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        code unique: true
        motherCode nullable: false, blank: false
        fatherCode nullable: false, blank: false
        numberOfOutcomes min: 1
        //numberOfLivebirths min: 0
        outcomeDate nullable: false
        birthPlace nullable: true
        birthPlaceOther nullable: true, blank: true

        visitCode nullable: false, blank: false

        modules nullable: true
    }

    static mapping = {
        table '_raw_pregnancy_outcome'

        id column: "id", generator: 'uuid'

        code column: "code"
        motherCode column: "mother_code"
        fatherCode column: "father_code"
        numberOfOutcomes column: "number_of_outcomes"
        //numberOfLivebirths column: "number_of_livebirths"
        outcomeDate column: "outcome_date"
        birthPlace column: "birthplace"
        birthPlaceOther column: "birthplace_other"

        visitCode column: "visit_code"

        modules column: "modules"
    }
}
