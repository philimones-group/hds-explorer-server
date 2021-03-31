package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.ProcessedStatus

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Member model taken from Mobile Data Collect, essentially for Enumeration Cases
 */
class RawMemberEnu {

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
    String householdName
    String headRelationshipType
    LocalDate residencyStartDate
    String isHouseholdHead

    String collectedBy //fieldWorkerId
    LocalDateTime collectedDate //visitDate / creationDate
    LocalDateTime uploadedDate

    ProcessedStatus processedMember
    ProcessedStatus processedResidency
    ProcessedStatus processedHeadRelationship
    ProcessedStatus processedMaritalRelationship

    ProcessedStatus processedStatus = ProcessedStatus.NOT_PROCESSED /* General Status, 1-All Processed, 0-Not All Processed*/

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

        maritalStatus blank: false
        spouseCode blank: true
        spouseName blank: true
        spouseDate blank: true

        householdCode blank: false
        householdName blank: false
        headRelationshipType blank: false
        residencyStartDate blank: false
        isHouseholdHead blank: false

        collectedBy blank: true
        collectedDate nullable: true
        uploadedDate nullable: true

        processedMember  nullable: false
        processedResidency nullable: false
        processedHeadRelationship nullable: false
        processedMaritalRelationship nullable: false

        processedStatus nullable: false
    }

    static mapping = {
        table '_raw_member_enu'

        id column: "id", generator: 'assigned'

        version false

        code column: 'code'
        name column: 'name'
        gender column: 'gender'
        dob column: 'dob'

        maritalStatus column: 'marital_status'
        spouseCode column: 'spouse_code'
        spouseName column: 'spouse_name'
        spouseDate column: 'spouse_date'

        motherCode column: 'mother_code'
        motherName column: 'mother_name'

        fatherCode column: 'father_code'
        fatherName column: 'father_name'

        householdCode column: 'household_code'
        householdName column: 'household_name'
        headRelationshipType column: 'head_relationship_type'
        residencyStartDate column: 'residency_start_date'
        isHouseholdHead column: 'is_household_head'

        collectedBy column: 'collected_by'
        collectedDate column: 'collected_date'
        uploadedDate column: 'uploaded_date'

        processedMember column: 'processed_member', enumType: "identity"
        processedResidency column: 'processed_residency', enumType: "identity"
        processedHeadRelationship column: 'processed_head_relationship', enumType: "identity"
        processedMaritalRelationship column: 'processed_marital_relationship', enumType: "identity"

        processedStatus column: "processed", enumType: "identity"
    }
}
