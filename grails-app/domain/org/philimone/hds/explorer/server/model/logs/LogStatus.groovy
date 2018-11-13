package org.philimone.hds.explorer.server.model.logs

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
