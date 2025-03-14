package org.philimone.hds.explorer.server.model.collect.raw.editors

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawEditMember {

    String id

    String code
    String name
    String gender
    LocalDate dob
    String motherCode
    String motherName
    String fatherCode
    String fatherName

    String education
    String religion

    String phonePrimary
    String phoneAlternative

    String householdCode

    String collectedBy
    String collectedDeviceId
    String collectedHouseholdId
    String collectedMemberId
    LocalDateTime collectedStart
    LocalDateTime collectedEnd
    LocalDateTime collectedDate
    LocalDateTime uploadedDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED /* General Status, 1-All Processed, 0-Not All Processed*/

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        code blank: false
        name blank: false
        gender blank: false
        dob blank: false
        motherCode blank: false
        motherName blank: false
        fatherCode blank: false
        fatherName blank: false

        education blank: true, nullable: true
        religion blank: true, nullable: true

        phonePrimary blank: true, nullable: true
        phoneAlternative blank: true, nullable: true

        householdCode blank: true, nullable: true

        collectedBy blank: true
        collectedDeviceId nullable:true
        collectedHouseholdId nullable:true
        collectedMemberId nullable:true
        collectedStart nullable:true
        collectedEnd nullable:true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_edit_member'

        id column: "id", generator: 'assigned'

        version false

        code column: 'code', index: "idx_code"
        name column: 'name'
        gender column: 'gender'
        dob column: 'dob'

        motherCode column: 'mother_code', index: "idx_mother_code"
        motherName column: 'mother_name'

        fatherCode column: 'father_code', index: "idx_father_code"
        fatherName column: 'father_name'

        education column: "education"
        religion column: "religion"

        phonePrimary column: "phone_primary"
        phoneAlternative column: "phone_alternative"

        householdCode column: 'household_code', index: "idx_household_code"

        collectedBy column: 'collected_by', index: "idx_collected_by"
        collectedDate column: 'collected_date'
        uploadedDate column: 'uploaded_date'

        postExecution column: "post_execution"

        processedStatus column: "processed", enumType: "identity"
    }
}


