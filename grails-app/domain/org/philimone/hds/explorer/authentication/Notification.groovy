package org.philimone.hds.explorer.authentication

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
}
