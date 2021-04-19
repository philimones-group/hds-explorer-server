package org.philimone.hds.explorer.openhds.model

class Relationship {
    //static mapWith = "none"

    String uuid
    Boolean deleted
    Date insertDate
    Date voidDate
    String voidReason
    String status
    String statusMessage
    String aisToB
    Date endDate
    String endType
    Date startDate
    Individual individualB
    User voidBy
    Fieldworker fieldworker
    Individual individualA
    User insertBy

    static belongsTo = [Fieldworker, Individual, User]

    static mapping = {
        datasource 'openhds'
        table 'relationship'

        id name: "uuid", generator: "assigned"
        version false
        uuid column:'uuid'
        deleted column:'deleted'
        insertDate column:'insertDate'
        voidDate column:'voidDate'
        voidReason column:'voidReason'
        status column:'status'
        statusMessage column:'statusMessage'
        aisToB column:'aIsToB'
        endDate column:'endDate'
        endType column:'endType'
        startDate column:'startDate'

        fieldworker column: 'collectedBy_uuid'
        individualA column: 'individualA_uuid'
        individualB column: 'individualB_uuid'
        voidBy column: 'voidBy_uuid'
        insertBy column: 'insertBy_uuid'
    }

    static constraints = {
        uuid maxSize: 32
        insertDate nullable: true
        voidDate nullable: true
        voidReason nullable: true
        status nullable: true
        statusMessage nullable: true
        aisToB nullable: true
        endDate nullable: true
        endType nullable: true
    }
}
