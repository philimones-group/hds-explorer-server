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
        out << "            ],\n"
        out << "            \"language\": {\n" +
               "                \"emptyTable\": \"${g.message(code: 'default.datatables.ui.emptyTable')}\",\n" +
               "                \"info\": \"${g.message(code: 'default.datatables.ui.info')}\",\n" +
               "                \"infoEmpty\": \"${g.message(code: 'default.datatables.ui.infoEmpty')}\",\n" +
               "                \"infoFiltered\": \"${g.message(code: 'default.datatables.ui.infoFiltered')}\",\n" +
               "                \"infoThousands\": \",\",\n" +
               "                \"lengthMenu\": \"${g.message(code: 'default.datatables.ui.lengthMenu')}\",\n" +
               "                \"loadingRecords\": \"${g.message(code: 'default.datatables.ui.loadingRecords')}\",\n" +
               "                \"processing\": \"${g.message(code: 'default.datatables.ui.processing')}\",\n" +
               "                \"search\": \"${g.message(code: 'default.datatables.ui.search')}\",\n" +
               "                \"zeroRecords\": \"${g.message(code: 'default.datatables.ui.zeroRecords')}\",\n" +
               "                \"thousands\": \",\",\n" +
               "                \"paginate\": {\n" +
               "                    \"first\": \"${g.message(code: 'default.datatables.ui.paginate.first')}\",\n" +
               "                    \"last\": \"${g.message(code: 'default.datatables.ui.paginate.last')}\",\n" +
               "                    \"next\": \"${g.message(code: 'default.datatables.ui.paginate.next')}\",\n" +
               "                    \"previous\": \"${g.message(code: 'default.datatables.ui.paginate.previous')}\"\n" +
               "                }\n" +
               "            }\n"

        if (attrs.nosort != null && attrs.nosort=="true"){
            println "nosort"
            out << "            , \"bSort\": false"
        }

        out << "        });\n"
        out << "    });\n"
        out << "</script>\n"

    }

}
