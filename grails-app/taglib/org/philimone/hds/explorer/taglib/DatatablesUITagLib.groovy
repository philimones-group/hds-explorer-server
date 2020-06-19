package org.philimone.hds.explorer.taglib

class DatatablesUITagLib {

    static namespace = "dt"

    def generalUtilitiesService

    def defaultResources = {

        out << "        " + asset.stylesheet(src: "DataTables/datatables.css") + "\n"
        out << "        " + asset.javascript(src: "DataTables/datatables.js") + "\n"

    }

    def loadDatatable = { attrs, body ->

        out << "<script type=\"text/javascript\">\n"
        out << "    \$(document).ready(function () {\n"
        out << "        \$(\"#${attrs.name}\").DataTable({\n"
        out << "            \"columnDefs\": [\n"
        out << "                {\"className\": \"dt-center\", \"targets\": \"_all\"}\n"
        out << "            ]\n"

        if (attrs.nosort != null && attrs.nosort=="true"){
            println "nosort"
            out << "            , \"bSort\": false"
        }

        out << "        });\n"
        out << "    });\n"
        out << "</script>\n"

    }

}
