package org.philimone.hds.explorer.server.model.main

class DataSetLabel {

    DataSet dataSet
    String name   /* Column Name */
    String label  /* Label of the Column */

    static belongsTo = [dataSet: DataSet]

    String toString(){
        "${name}:${label}"
    }

    static constraints = {
        name blank: false, unique: 'dataSet'
        label blank: false
    }

    static mapping = {
        table 'dataset_label'

        list column: 'dataset_id'
        name column: 'name'
        label column: 'label'
    }
}