package org.philimone.hds.explorer.server.model.main

class TrackingListMapping {

    TrackingList list
    String name   /* Variable Name */
    String label  /* Label of the Variable */

    static belongsTo = [list:TrackingList]

    static constraints = {
        name blank: false, unique: 'list'
        label blank: false
    }

    static mapping = {
        datasource 'main'
        table 'tracking_list_mapping'

        list column: 'trackinglist_id'
        name column: 'name'
        label column: 'label'
    }
}
