package org.philimone.hds.explorer.server.model.main.extension

class MaritalRelationshipExt {

    String collected_id
    String visit_code
    String member_a
    String member_a_name
    String member_b
    String member_b_name

    static constraints = {
        collected_id unique: true, nullable: false
        visit_code nullable: false
        member_a nullable: false
        member_a_name nullable: false
        member_b nullable: false
        member_b_name nullable: false
    }

    static mapping = {
        table 'marital_relationship_ext'

        collected_id column: 'collected_id'
        visit_code column: 'visit_code', index: 'idx_visit_code'
        member_a column: 'member_a'
        member_a_name column: 'member_a_name'
        member_b column: 'member_b'
        member_b_name column: 'member_b_name'
    }

}
