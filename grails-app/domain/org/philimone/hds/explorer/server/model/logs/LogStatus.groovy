package org.philimone.hds.explorer.server.model.logs

/**
 * A Log Status represents four different states of a task, that can be (Started, Finished, With Error and Not Started)
 */
class LogStatus {

    static String STARTED = "logstatus.started.label"
    static String FINISHED = "logstatus.finished.label"
    static String ERROR = "logstatus.error.label"
    static String NOT_STARTED = "logstatus.not.started.label"

    String name

    String toString(){
        name
    }

    static constraints = {
        name blank: true, unique: true
    }

}
