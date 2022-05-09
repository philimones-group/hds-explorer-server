package org.philimone.hds.explorer.server.model.authentication

import java.time.LocalDateTime

/**
 * A Notification represents a message sent to a User, its part of the quick message system that keeps the Users notified about things of interest in the app
 */
class Notification {

    static String LABEL_STATUS = "notification.status.label"

    String id
    User user
    String subject
    String message
    LocalDateTime date
    boolean readed

    String getShortMessage(){
        int max = 20;
        int count = message.length()

        String str = (count >= max) ? message.substring(0,max) : message

        return str+"...";
    }

    static transients = ['shortMessage']

    static constraints = {
        id maxSize: 32

        user nullable:false
        subject blank:true, nullable:false
        message blank:false, nullable:false, widget: 'textarea'
        date nullable:false
    }

    static mapping = {
        table '_notification'

        id column: "id", generator: 'uuid'

        user column: "user_id"
        subject column: "subject"
        message column: "message"
        date column: "date"
        readed column: "readed"
    }

}
