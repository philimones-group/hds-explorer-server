package org.philimone.hds.explorer.taglib

import groovy.xml.MarkupBuilder
import net.betainteractive.utilities.StringUtil
import org.springframework.web.servlet.support.RequestContextUtils

class LanguageSelectorTagLib {

    static namespace = 'language'
    static defaultEncodeAs = [taglib:'html']

    List<Locale> locales = [new Locale("pt"), Locale.ENGLISH]

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


}
