package org.philimone.hds.explorer.taglib

import net.betainteractive.utilities.StringUtil
import org.springframework.web.servlet.support.RequestContextUtils
import java.time.LocalDate
import java.time.LocalDateTime

class MenuTagLib {

    static namespace = "hds"

    def generalUtilitiesService

    List<Locale> locales = [new Locale("pt", ), Locale.ENGLISH, Locale.FRENCH]

    /*Menu*/
    def menuBar = {attrs, body ->
        out << '<ul class="hmenu">'

        out << '    ' + body()

        out << '</ul>'
    }

    def menu = {attrs, body ->

        String background = attrs.background
        String link = attrs.link
        String label = attrs.label
        String xclass = attrs.class

        if (background == null || background.isEmpty()){
            out << '<li>'
        }else{
            out << "<li style=\"background-color: #4e8908;\" >"
        }

        if (xclass == null || xclass.isEmpty()) {
            out << "<a href=\"${link}\">${label}</a>"
        } else {
            out << "<a href=\"${link}\" class=\"${xclass}\">${label}</a>"
        }
        out << '</li>'
    }

    def dropmenu = {attrs, body ->
        out << '<li>'
        out << "    <a href=\"#\">${attrs.label} <span class=\"hmenu-caret\"></span></a>"
        out << "    <ul>"
        out << "        " + body()
        out << '    </ul>'
        out << '</li>'
    }

    def menuResources = {

        out << "        " + asset.stylesheet(src: "menu.css") + "\n"

    }

    def selectLanguageMenu = {attrs, body ->

        String xclass = attrs.class

        Locale requestLocale = RequestContextUtils.getLocale(request)

        locales.each { Locale locale ->
            def link = "${request.forwardURI}?lang=${locale.language}"
            def label = StringUtil.capitalize(locale.getDisplayLanguage(locale))

            out << '<li>'
            if (xclass == null || xclass.isEmpty()) {
                out << "<a href=\"${link}\" ${requestLocale.language == locale.language ? 'style="background-color: #4e8908;"' : ''}>${label}</a>"
            } else {
                out << "<a href=\"${link}\" class=\"${xclass}\" ${requestLocale.language == locale.language ? 'style="background-color: #4e8908;"' : ''}>${label}</a>"
            }
            out << '</li>' + "\n"
        }


    }
}
