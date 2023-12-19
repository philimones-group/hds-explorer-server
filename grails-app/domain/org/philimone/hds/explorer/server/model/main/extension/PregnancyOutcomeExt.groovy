package org.philimone.hds.explorer.server.model.main.extension

class PregnancyOutcomeExt {

    String collected_id
    String visit_code
    String pregnancy_code
    String mother_code
    String mother_name
    String father_code
    String father_name

    static hasMany = [childs:PregnancyChildExt]

    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        pregnancy_code nullable: false
        mother_code nullable: false
        mother_name nullable: false
        father_code nullable: false
        father_name nullable: false
    }

    static mapping = {
        table 'pregnancy_outcome_ext'

        version false

        collected_id column: 'collected_id'
        visit_code column: 'visit_code', index: 'idx_visit_code'
        pregnancy_code column: 'pregnancy_code', index: 'idx_pregnancy_code'
        mother_code column: 'mother_code', index: 'idx_mother_code'
        mother_name column: 'mother_name'
        father_code column: 'father_code', index: 'idx_father_code'
        father_name column: 'father_name'
    }

}
