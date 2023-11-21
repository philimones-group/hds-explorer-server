package org.philimone.hds.explorer.server.model.main.extension

class PregnancyRegistrationExt {

    String collected_id
    String visit_code
    String pregnancy_code
    String mother_code
    String mother_name
    String pregnancy_status

    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        pregnancy_code nullable: false
        mother_code nullable: false
        mother_name nullable: false
        pregnancy_status nullable: false
    }

    static mapping = {
        table 'pregnancy_registration_ext'

        collected_id column: 'collected_id'
        visit_code column: 'visit_code', index: 'idx_visit_code'
        pregnancy_code column: 'pregnancy_code', index: 'idx_pregnancy_code'
        mother_code column: 'mother_code', index: 'idx_mother_code'
        mother_name column: 'mother_name'
        pregnancy_status column: 'pregnancy_status'
    }

}
