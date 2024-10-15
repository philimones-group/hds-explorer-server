package org.philimone.hds.explorer.server.model.collect.raw

import org.philimone.hds.explorer.server.model.enums.PregnancyOutcomeType
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.Member

import java.time.LocalDate

class RawPregnancyChild {

    String id
    RawPregnancyOutcome outcome
    String outcomeType
    String childCollectedId
    String childCode
    String childName
    String childGender
    Integer childOrdinalPosition
    String headRelationshipType

    static belongsTo = [outcome:RawPregnancyOutcome]

    @Override
    String toString() {
        "${childCode}"
    }

    static constraints = {
        id maxSize: 32

        outcome nullable: false
        outcomeType nullable: false
        childCode nullable: true, blank: true
        childOrdinalPosition nullable: true
        headRelationshipType nullable: true
    }

    static mapping = {
        table '_raw_pregnancy_child'

        id column: "id", generator: 'uuid'

        outcome column: "pregnancy_outcome_id"
        outcomeType column: "outcome_type"
        childCollectedId column: "child_collected_id"
        childCode column: "child_code", index: "idx_child_code"
        childName column: "child_name"
        childGender column: "child_gender"
        childOrdinalPosition column: "child_ordinal_pos"
        headRelationshipType column: "head_relationship_type"
    }

}
