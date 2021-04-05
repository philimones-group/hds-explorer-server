package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

class RawMember {

    static mapWith = "none"

    String id

    String code
    String name
    String gender
    LocalDate dob
    String motherCode
    String motherName
    String fatherCode
    String fatherName

    String householdCode

    String collectedBy
    LocalDateTime collectedDate
    LocalDateTime uploadedDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED /* General Status, 1-All Processed, 0-Not All Processed*/

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        code blank: false, unique: true
        name blank: false
        gender blank: false
        dob blank: false
        motherCode blank: false
        motherName blank: false
        fatherCode blank: false
        fatherName blank: false

        householdCode blank: false

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_member'

        id column: "id", generator: 'uuid'

        version false

        code column: 'code'
        name column: 'name'
        gender column: 'gender'
        dob column: 'dob'

        motherCode column: 'mother_code'
        motherName column: 'mother_name'

        fatherCode column: 'father_code'
        fatherName column: 'father_name'

        householdCode column: 'household_code'

        collectedBy column: 'collected_by'
        collectedDate column: 'collected_date'
        uploadedDate column: 'uploaded_date'

        processedStatus column: "processed", enumType: "identity"
    }
}
