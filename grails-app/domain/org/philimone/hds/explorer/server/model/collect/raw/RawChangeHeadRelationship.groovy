package org.philimone.hds.explorer.server.model.collect.raw

import java.time.LocalDateTime

class RawChangeHeadRelationship {

    String id

    RawChangeHead changeHead
    String newMemberCode
    String newRelationshipType

    Integer ordinal
    LocalDateTime createdDate = LocalDateTime.now()

    static belongsTo = [changeHead:RawChangeHead]

    @Override
    String toString() {
        "${String.format("%02d", ordinal)} : ${newMemberCode} - [${newRelationshipType}]"
    }

    static constraints = {
        id maxSize: 32

        changeHead nullable: false
        newMemberCode nullable: false
        newRelationshipType nullable: false

        ordinal nullable: false
        createdDate nullable: true
    }

    static mapping = {
        table '_raw_change_head_relationship'

        id column: "id", generator: 'uuid'

        changeHead column: "change_head_id"
        newMemberCode column: "new_member_code", index: "idx_new_membercode"
        newRelationshipType column: "new_relationship_type"

        ordinal column: "ordinal", defaultValue: "'0'"
        createdDate column: "created_date"
    }

}