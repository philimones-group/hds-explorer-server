package org.philimone.hds.explorer.server.model.main

class TrackingListMapping {

    String id
    TrackingList list
    String name   /* Variable Name */
    String label  /* Label of the Variable */

    static belongsTo = [list:TrackingList]

    static constraints = {
        id maxSize: 32
        name blank: false, unique: 'list'
        label blank: false
    }

    static mapping = {
        table 'tracking_list_mapping'

        id column: "id", generator: 'uuid'

        list column: 'trackinglist_id'
        name column: 'name'
        label column: 'label'
    }
}
