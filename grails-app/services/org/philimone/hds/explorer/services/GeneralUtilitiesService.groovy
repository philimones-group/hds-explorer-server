package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.io.LogOutput
import org.philimone.hds.explorer.authentication.Notification
import org.philimone.hds.explorer.authentication.User
import org.springframework.context.i18n.LocaleContextHolder

import java.text.SimpleDateFormat

@Transactional
class GeneralUtilitiesService {

    def springSecurityService
    def grailsApplication

    def boolean isUsingOpenHDS(){
        return grailsApplication.config.openva.database.using_openhds
    }

    def String getMessage(String messageCode, Object[] args, String defaultMessage, Locale locale) {
        def appCtx = grailsApplication.getMainContext()
        return appCtx.getMessage(messageCode, args, defaultMessage, locale)
    }

    def String getMessage(String messageCode, Object[] args, String defaultMessage){
        getMessage(messageCode, args, defaultMessage, LocaleContextHolder.getLocale())
    }

    def String getMessage(String messageCode, String defaultMessage){
        getMessage(messageCode, null, defaultMessage, LocaleContextHolder.getLocale())
    }

    def String userFullName() {
        return currentUser().toString()
    }

    def User currentUser() {
        User u = (User) springSecurityService.currentUser
        return u
    }

    def String UserMessageStatus(){
        int total = 0;
        int unread = 0;

        User user = currentUser();

        def messages = Notification.findAllByUserAndReaded(user, false)

        unread = messages.size()

        def args = [ unread ].toArray()

        return getMessage(Notification.LABEL_STATUS, args, "${unread} unreaded notifications");
    }

    public String getTimeStr(){
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date());
    }


    public LogOutput getOutput(String path, String fileName){
        String logFile = path+File.separator+fileName+"-"+getTimeStr()+".log";
        PrintStream output = null;
        println logFile

        try {
            output = new PrintStream(new FileOutputStream(logFile), true );
            output.println("Creating Error Log File\r"+logFile);
        } catch (FileNotFoundException ex) {
            System.out.println("Couldnt create log file "+logFile);
            return null;
        }

        return new LogOutput(output: output, logFileName: logFile);
    }
}
