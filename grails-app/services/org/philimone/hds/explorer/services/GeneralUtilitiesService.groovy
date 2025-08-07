package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.io.LogOutput
import net.betainteractive.utilities.DateUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.authentication.Notification
import org.philimone.hds.explorer.server.model.authentication.User
import org.philimone.hds.explorer.server.model.json.JConstant
import org.philimone.hds.explorer.server.model.json.JLanguage
import org.philimone.hds.explorer.server.model.settings.Codes
import org.springframework.context.i18n.LocaleContextHolder

import java.text.SimpleDateFormat

@Transactional
class GeneralUtilitiesService {

    def springSecurityService
    def mailService
    def applicationParamService
    def grailsApplication

    def List<Locale> locales = [new Locale("pt", ), Locale.ENGLISH, Locale.FRENCH, new Locale("am",)]

    def boolean isUsingMailService(){
        return grailsApplication.config.getProperty("hds.explorer.mail.configured", String.class)
    }

    def String getDefaultMailSender(){
        return grailsApplication.config.getProperty("grails.mail.username", String.class)
    }

    def String getAppVersion(){
        getConfigValue("info.app.version")
    }

    def String getConfigValue(String variable){
        return grailsApplication.config.getProperty("${variable}", String.class)
    }

    def sendTextEmail(String toEmail, String subjectText, String message){
        if (isUsingMailService()){
            def sender = getDefaultMailSender()

            try{
                mailService.sendMail {
                    from sender
                    to toEmail
                    subject subjectText
                    text message
                }
            } catch (Exception ex){
                ex.printStackTrace()
            }


        }
    }

    def String getMessage(String messageCode, Object[] args, String defaultMessage, Locale locale) {
        def appCtx = grailsApplication.getMainContext()
        return appCtx.getMessage(messageCode, args, defaultMessage, locale)
    }

    def String getMessage(String messageCode, Object[] args, String defaultMessage){
        getMessage(messageCode, args, defaultMessage, new Locale(Codes.SYSTEM_LANGUAGE))
    }

    def String getMessage(String messageCode, String defaultMessage){
        getMessage(messageCode, null, defaultMessage, new Locale(Codes.SYSTEM_LANGUAGE))
    }

    def String getMessage(String messageCode) {
        getMessage(messageCode, null, null, new Locale(Codes.SYSTEM_LANGUAGE))
    }

    def String getMessageWeb(String messageCode) {
        def locale = LocaleContextHolder.getLocale();
        return getMessage(messageCode, null, null, locale)
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
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
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

    List<JLanguage> getSystemLanguages() {
        def languages = new ArrayList<JLanguage>()

        locales.each {
            def lang = new JLanguage(it.language, StringUtil.capitalize(it.getDisplayLanguage(it)))
            languages.add(lang)
        }

        return languages
    }

    JLanguage getCurrentSystemLanguage(){
        def currentLanguageCode = applicationParamService.getStringValue(Codes.PARAMS_SYSTEM_LANGUAGE)

        for (Locale it : locales) {
            if (it.language.equals(currentLanguageCode)) {
                return new JLanguage(currentLanguageCode, StringUtil.capitalize(it.getDisplayLanguage(it)))
            }
        }

        return null
    }

    List<JConstant> getSystemSupportedCalendars() {
        def calendars = new ArrayList<JConstant>()

        calendars.add(new JConstant(value: Codes.SYSTEM_SUPPORTED_CALENDAR_GREGORIAN, name: "settings.parameters.calendar.gregorian.label"))
        calendars.add(new JConstant(value: Codes.SYSTEM_SUPPORTED_CALENDAR_ETHIOPIAN, name: "settings.parameters.calendar.ethiopian.label"))

        return calendars;
    }

    DateUtil getDateUtil(){
        def isEthiopian = Codes.SYSTEM_USE_ETHIOPIAN_CALENDAR
        return new DateUtil(isEthiopian ? DateUtil.SupportedCalendar.ETHIOPIAN : DateUtil.SupportedCalendar.GREGORIAN)
    }

}
