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

    byte[] extensionForm

    String collectedBy //fieldWorkerId
    String collectedDeviceId
    String collectedHouseholdId
    String collectedMemberId
    LocalDateTime collectedStart
    LocalDateTime collectedEnd
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate  //submissionDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    static hasMany = [childs:RawPregnancyChild]

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        code nullable: false
        motherCode nullable: false, blank: false
        fatherCode nullable: false, blank: false
        numberOfOutcomes min: 1
        //numberOfLivebirths min: 0
        outcomeDate nullable: false
        birthPlace nullable: true
        birthPlaceOther nullable: true, blank: true

        visitCode nullable: false, blank: false

        extensionForm nullable: true

        collectedBy blank: true
        collectedDeviceId nullable:true
        collectedHouseholdId nullable:true
        collectedMemberId nullable:true
        collectedStart nullable:true
        collectedEnd nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true

        modules nullable: true

    }

    static mapping = {
        table '_raw_pregnancy_outcome'

        id column: "id", generator: 'assigned'

        code column: "code", index: "idx_code"
        motherCode column: "mother_code", index: "idx_mother_code"
        fatherCode column: "father_code", index: "idx_father_code"
        numberOfOutcomes column: "number_of_outcomes"
        //numberOfLivebirths column: "number_of_livebirths"
        outcomeDate column: "outcome_date"
        birthPlace column: "birthplace"
        birthPlaceOther column: "birthplace_other"
        visitCode column: "visit_code", index: "idx_visit_code"
        modules column: "modules"

        extensionForm column: "extension_form", sqlType: "mediumblob"

        collectedBy column: "collected_by", index: "idx_collected_by"
        collectedDeviceId column: "collected_device_id", index: "idx_cdeviceid"
        collectedHouseholdId column: "collected_household_id", index: "idx_chouseid"
        collectedMemberId column: "collected_member_id", index: "idx_cmemberid"
        collectedStart column: "collected_start"
        collectedEnd column: "collected_end"
        collectedDate column: "collected_date"
        uploadedDate column: "uploaded_date"

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}


