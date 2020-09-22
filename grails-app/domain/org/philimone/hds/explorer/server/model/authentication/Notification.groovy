package org.philimone.hds.explorer.server.model.authentication

/**
 * A Notification represents a message sent to a User, its part of the quick message system that keeps the Users notified about things of interest in the app
 */
class Notification {

    static String LABEL_STATUS = "notification.status.label"

    User user
    String subject
    String message
    Date date
    boolean readed

    String getShortMessage(){
        int max = 20;
        int count = message.length()

        String str = (count >= max) ? message.substring(0,max) : message

        return str+"...";
    }

    static transients = ['shortMessage']

    static constraints = {
        user nullable:false
        subject blank:true, nullable:false
        message blank:false, nullable:false, widget: 'textarea'
        date nullable:false
    }

    static mapping = {
        datasource 'main'
    }
}
