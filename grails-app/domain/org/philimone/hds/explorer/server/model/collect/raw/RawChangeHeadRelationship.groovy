package org.philimone.hds.explorer.server.model.collect.raw

class RawChangeHeadRelationship {

    String id

    RawChangeHead changeHead
    String newMemberCode
    String newRelationshipType

    static belongsTo = [changeHead:RawChangeHead]

    static constraints = {
        id maxSize: 32

        changeHead nullable: false
        newMemberCode nullable: false
        newRelationshipType nullable: false
    }

    static mapping = {
        table '_raw_change_head_relationship'

        id column: "id", generator: 'uuid'

        changeHead column: "change_head_id"
        newMemberCode column: "new_member_code", index: "idx_new_membercode"
        newRelationshipType column: "new_relationship_type"
    }

}