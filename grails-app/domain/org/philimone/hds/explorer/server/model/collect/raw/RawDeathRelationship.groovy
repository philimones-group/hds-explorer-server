package org.philimone.hds.explorer.server.model.collect.raw

class RawDeathRelationship {

    String id

    RawDeath death
    String newMemberCode
    String newRelationshipType

    static belongsTo = [death:RawDeath]

    static constraints = {
        id maxSize: 32

        death nullable: false
        newMemberCode nullable: false
        newRelationshipType nullable: false
    }

    static mapping = {
        table '_raw_death_relationships'

        id column: "id", generator: 'uuid'

        death column: "death_id"
        newMemberCode column: "new_member_code"
        newRelationshipType column: "new_relationship_type"
    }
}