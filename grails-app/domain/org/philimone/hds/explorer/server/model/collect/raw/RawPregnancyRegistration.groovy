package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.EstimatedDateOfDeliveryType
import org.philimone.hds.explorer.server.model.enums.PregnancyStatus
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.Visit

import java.time.LocalDate
import java.time.LocalDateTime

class RawPregnancyRegistration {

    String id
    String code
    String motherCode
    LocalDate recordedDate
    Integer pregMonths
    Boolean eddKnown
    Boolean hasPrenatalRecord
    LocalDate eddDate
    String eddType
    Boolean lmpKnown
    LocalDate lmpDate
    LocalDate expectedDeliveryDate
    String status
    String visitCode

    String collectedBy
    LocalDateTime collectedDate
    LocalDateTime uploadedDate

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED

    boolean postExecution = false


    static constraints = {
        id maxSize: 32

        code unique: true
        motherCode nullable: false, blank: false
        recordedDate nullable: false
        pregMonths nullable: true
        eddKnown nullable: true
        hasPrenatalRecord nullable: true
        eddDate nullable: true
        eddType nullable: true
        lmpKnown nullable: true
        lmpDate nullable: true
        expectedDeliveryDate nullable: true
        status nullable: false
        visitCode blank: false
    }

    static mapping = {
        table '_raw_pregnancy_registration'

        id column: "id", generator: 'assigned'

        code column: "code"
        motherCode column: "mother_code"
        recordedDate column: "recorded_date"

        pregMonths column: "preg_months"
        eddKnown column: "edd_known"
        hasPrenatalRecord column: "has_precord"
        eddDate column: "edd_date"
        eddType column: "edd_type"
        lmpKnown column: "lmp_known"
        lmpDate column: "lmp_date"
        expectedDeliveryDate column: "expected_delivery_date"
        status column: "status"

        visitCode column: "visit_code"
    }
}
