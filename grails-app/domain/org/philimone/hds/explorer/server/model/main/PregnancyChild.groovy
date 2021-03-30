package org.philimone.hds.explorer.server.model.main

import org.philimone.hds.explorer.server.model.enums.PregnancyOutcomeType

class PregnancyChild {

    String id
    PregnancyOutcome outcome
    String outcomeCode
    PregnancyOutcomeType outcomeType
    Member child
    String childCode
    Integer childOrdinalPosition
    HeadRelationship childHeadRelationship

    static belongsTo = [outcome:PregnancyOutcome]

    static constraints = {
        id maxSize: 32

        outcome nullable: false
        outcomeCode nullable: false, blank: false
        outcomeType nullable: false
        child nullable: true
        childCode nullable: true, blank: true
        childOrdinalPosition nullable: true
        childHeadRelationship nullable: true
    }

    static mapping = {
        table 'pregnancy_child'

        id column: "id", generator: 'uuid'

        outcome column: "pregnancy_outcome_id"
        outcomeCode column: "pregnancy_outcome_code"
        outcomeType column: "outcomde_type"
        child column: "child_id"
        childCode column: "child_code"
        childOrdinalPosition column: "child_ordinal_pos"
        childHeadRelationship column: "child_head_relationship_id"
    }

}
