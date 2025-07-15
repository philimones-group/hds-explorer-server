package org.philimone.hds.explorer.server.model.main.extension

class PregnancyVisitExt {

    String collected_id
    String visit_code
    String pregnancy_code
    String mother_code
    String mother_name
    String visit_type
    String visit_number
    String status

    static hasMany = [childs:PregnancyVisitChildExt]

    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        pregnancy_code nullable: false
        mother_code nullable: false
        mother_name nullable: false
        visit_type nullable: false
        visit_number nullable: false
        status nullable: false
    }

    static mapping = {
        table 'pregnancy_visit_ext'

        version false

        collected_id column: 'collected_id'
        visit_code column: 'visit_code', index: 'idx_visit_code'
        pregnancy_code column: 'pregnancy_code', index: 'idx_pregnancy_code'
        mother_code column: 'mother_code', index: 'idx_mother_code'
        mother_name column: 'mother_name'
        visit_type column: 'visit_type', index: 'idx_visit_type'
        visit_number column: 'visit_number'
        status column: 'status'
    }

}
