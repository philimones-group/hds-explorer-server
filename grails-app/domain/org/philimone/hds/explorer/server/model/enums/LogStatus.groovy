package org.philimone.hds.explorer.server.model.enums

/**
 * A Log Status represents four different states of a task, that can be (Started, Finished, With Error and Not Started)
 */
enum LogStatus {

    STARTED ("logstatus.started.label"),
    FINISHED ("logstatus.finished.label"),
    ERROR ("logstatus.error.label"),
    NOT_STARTED ("logstatus.not.started.label")

    String name

    LogStatus(String name){
        this.name = name
    }

    String toString(){
        name
    }
}
