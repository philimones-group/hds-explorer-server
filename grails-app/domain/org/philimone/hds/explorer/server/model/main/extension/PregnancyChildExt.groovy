package org.philimone.hds.explorer.server.model.main.extension

class PregnancyChildExt {

    String collected_id
    String child_outcome_type
    String child_code
    String child_name
    String child_gender

    static belongsTo = [outcome: PregnancyOutcomeExt]

    static constraints = {
        collected_id nullable: false

        outcome nullable: false

        child_outcome_type nullable: false
        child_code nullable: false
        child_name nullable: false
        child_gender nullable: false
    }

    static mapping = {
        table 'pregnancy_child_ext'

        collected_id column: 'collected_id'

        outcome column: "pregnancy_outcome_ext_id"

        child_outcome_type column: 'child_outcome_type'
        child_code column: 'child_code', index: 'idx_child_code'
        child_name column: 'child_name'
        child_gender column: 'child_gender'

    }
}
