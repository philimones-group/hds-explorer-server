package org.philimone.hds.explorer.server.model.collect.raw

import net.betainteractive.utilities.StringUtil

import java.time.LocalDateTime

class RawDeathRelationship {

    String id

    RawDeath death
    String newMemberCode
    String newRelationshipType

    Integer ordinal
    LocalDateTime createdDate = LocalDateTime.now()

    static belongsTo = [death:RawDeath]

    @Override
    String toString() {
        "${String.format("%02d", ordinal)} : ${newMemberCode} - [${newRelationshipType}]"
    }

    static constraints = {
        id maxSize: 32

        death nullable: false
        newMemberCode nullable: false
        newRelationshipType nullable: false

        ordinal nullable: false
        createdDate nullable: true
    }

    static mapping = {
        table '_raw_death_relationships'

        id column: "id", generator: 'uuid'

        death column: "death_id"
        newMemberCode column: "new_member_code", index: "idx_new_member_code"
        newRelationshipType column: "new_relationship_type"

        ordinal column: "ordinal", defaultValue: "'0'"
        createdDate column: "created_date"
    }
}