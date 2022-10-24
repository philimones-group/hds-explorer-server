package org.philimone.hds.explorer.taglib

import groovy.xml.MarkupBuilder
import net.betainteractive.utilities.StringUtil
import org.springframework.web.servlet.support.RequestContextUtils

class LanguageSelectorTagLib {

    static namespace = 'language'

    List<Locale> locales = [new Locale("pt", ), Locale.ENGLISH, Locale.FRENCH]

    /**
     * Renders a locale selector.
     * Adds the class <code>active</code> to the list-element of the current language.
     */

    def selector = {
        Locale requestLocale = RequestContextUtils.getLocale(request)

        MarkupBuilder mb = new MarkupBuilder(out)
        mb.ul('id': 'locale-selector', 'class':'lang-dropdown-ul') {
            locales.each { Locale locale ->
                li(requestLocale.language == locale.language ? ['class': 'xactive'] : [:]) {
                    mb.yield(
                            link( controller: controllerName, action: actionName, params: params + [lang: locale.language], { StringUtil.capitalize(locale.getDisplayLanguage(locale)) } ).toString(),
                            false
                    )
                }
            }
        }
    }

    def selectMenu = {
        Locale requestLocale = RequestContextUtils.getLocale(request)

        out << '<li>'

        locales.each { Locale locale ->
            def link = "${request.forwardURI}?lang=${locale.language}"
            def label = StringUtil.capitalize(locale.getDisplayLanguage(locale))
            out << "<a href=\"${link}\" ${requestLocale.language == locale.language ? 'style="background-color: #4e8908;"' : ''}>${label}</a>"
        }

        out << '</li>'
    }


}
