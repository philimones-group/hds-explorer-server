package org.philimone.hds.explorer.server.model.main

class TrackingListGroup {

    TrackingList trackingList

    String groupCode
    String groupName
    String groupTitle
    String groupDetails

    static belongsTo = [trackingList:TrackingList]

    static hasMany = [lists:TrackingListMapping]

    static constraints = {
        groupCode unique: 'trackingList'
        groupName blank: false, nullable: false
        groupTitle blank: false, nullable: false
        groupDetails blank: true, nullable: true
    }

    static mapping = {
        table 'tracking_list_group'

        trackingList column: "tracking_list_uuid"

        groupCode column: "group_code"
        groupName column: "group_name"
        groupTitle column: "group_title"
        groupDetails column: "group_details"
    }
}
