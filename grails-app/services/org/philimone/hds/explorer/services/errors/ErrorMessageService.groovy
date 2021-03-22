package org.philimone.hds.explorer.services.errors

import grails.artefact.DomainClass
import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.grails.datastore.gorm.GormEntity
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.ObjectError

@Transactional
class ErrorMessageService {

    def messageSource

    String removeClassDefFromErrDetails(String text){
        def st1 = "org.philimone.hds.explorer.server.model."
        def st2 = "org.philimone.hds.explorer.openhds.model."
        def st3 = "org.philimone.hds.explorer.odk.model."
        //def st4 = "org.philimone.hds.explorer.odk.model."
        //def st5 = "org.philimone.hds.explorer.openhds."


        text = text.replaceAll(st1, "")
        text = text.replaceAll(st2, "")
        text = text.replaceAll(st3, "")
        //text = text.replaceAll(st4, "")
        //text = text.replaceAll(st5, "")

        return text
    }

    String removeFromLastPoint(String text){
        def packageExp = "([a-zA-Z_]+\\.[a-zA-Z_]+)*\\."

        return text.replaceAll(packageExp, "")
    }

    String formatErrors(GormEntity domain){
        def errors = ""

        domain.errors.allErrors.each { obj ->
            def err = messageSource.getMessage(obj, LocaleContextHolder.getLocale())
            //err = removeClassDefFromErrDetails(err)
            err = StringUtil.removePackageNames(err)
            errors += err + "\n"
        }

        return errors
    }

    List<RawMessage> getRawMessages(RawEntity entity, GormEntity domain){
        def errors = new ArrayList<RawMessage>()

        domain.errors.fieldErrors.each { obj ->
            def msg = messageSource.getMessage(obj, LocaleContextHolder.getLocale())
            //err = removeClassDefFromErrDetails(err)
            msg = StringUtil.removePackageNames(msg)

            errors << new RawMessage(msg, [obj.field])
        }

        return errors
    }

    RawMessage getRawMessage(RawEntity entity, String messageCode, String[] args, String[] fields){
        def msg = messageSource.getMessage(messageCode, args, LocaleContextHolder.getLocale())
        new RawMessage(entity, msg, fields)
    }

    RawMessage getRawMessage(RawEntity entity, String messageCode, List<String> args, List<String> fields){
        def msg = messageSource.getMessage(messageCode, args!=null ? args.toArray() : null, LocaleContextHolder.getLocale())
        new RawMessage(entity, msg, fields)
    }
}
