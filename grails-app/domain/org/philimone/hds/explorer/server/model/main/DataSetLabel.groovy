package org.philimone.hds.explorer.server.model.main

class DataSetLabel {

    String id
    DataSet dataSet
    String name   /* Column Name */
    String label  /* Label of the Column */

    static belongsTo = [dataSet: DataSet]

    String toString(){
        "${name}:${label}"
    }

    static constraints = {
        id maxSize: 32
        name blank: false, unique: 'dataSet'
        label blank: false
    }

    static mapping = {
        table 'dataset_label'

        id column: "uuid", generator: 'uuid'

        list column: 'dataset_id'
        name column: 'name'
        label column: 'label'
    }
}