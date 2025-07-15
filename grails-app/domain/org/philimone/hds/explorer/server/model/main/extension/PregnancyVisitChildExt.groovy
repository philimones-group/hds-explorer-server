package org.philimone.hds.explorer.server.model.main.extension

class PregnancyVisitChildExt {

    String child_outcome_type
    String child_code
    String child_name
    String child_gender
    String child_status

    static belongsTo = [visit: PregnancyVisitExt]

    static constraints = {
        visit nullable: false

        child_outcome_type nullable: false
        child_code nullable: false
        child_name nullable: false
        child_gender nullable: false
        child_status nullable: false
    }

    static mapping = {
        table 'pregnancy_visit_child_ext'

        version false

        visit column: "pregnancy_visit_ext_id"

        child_outcome_type column: 'child_outcome_type'
        child_code column: 'child_code', index: 'idx_child_code'
        child_name column: 'child_name'
        child_gender column: 'child_gender'
        child_status column: 'child_status'
    }
}
