package org.philimone.hds.explorer.server.model.main.extension

class InmigrationExt {

    String collected_id
    String visit_code
    String member_code
    String member_name
    String migration_type
    String ext_migration_type

    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        member_code nullable: false
        member_name nullable: false
        migration_type nullable: false
        ext_migration_type nullable: false
    }

    static mapping = {
        table 'in_migration_ext'

        version false

        collected_id column: 'collected_id'
        visit_code column: 'visit_code', index: 'idx_visit_code'
        member_code column: 'member_code', index: 'idx_member_code'
        member_name column: 'member_name'
        migration_type column: 'migration_type'
        ext_migration_type column: 'ext_migration_type'
    }

}
