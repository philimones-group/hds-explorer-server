package org.philimone.hds.explorer.services.errors

import grails.artefact.DomainClass
import grails.gorm.transactions.Transactional
import org.grails.datastore.gorm.GormEntity
import org.springframework.context.i18n.LocaleContextHolder

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

    String formatErrors(GormEntity domain){
        def errors = ""

        domain.errors.allErrors.each { obj ->
            def err = messageSource.getMessage(obj, LocaleContextHolder.getLocale())
            err = removeClassDefFromErrDetails(err)
            errors += err + "\n"
        }

        return errors
    }
}
