package org.philimone.hds.explorer.taglib

import groovy.json.JsonSlurper
import net.betainteractive.utilities.StringUtil

class DatatablesUITagLib {

    static namespace = "dt"

    def generalUtilitiesService

    def defaultResources = {

        out << "        " + asset.stylesheet(src: "DataTables/datatables.css") + "\n"
        out << "        " + asset.javascript(src: "DataTables/datatables.js") + "\n"

    }

    def table = { attrs, body ->
        out << "\t\t\t<div class=\"whitebox_panel\">\n"
        out << "\t\t\t\t<table id=\"${attrs.id}\" class=\"display nowrap compact cell-border\">\n"
        out << "\t\t\t\t\t" + body() + "\n"
        out << "\t\t\t\t</table>\n"
        out << "\t\t\t</div>\n"
    }

    def loadDatatable = { attrs, body ->
        def linkColumn = attrs.linkColumn
        def linkAction = attrs.linkAction
        def linkId = attrs.linkId

        out << "<script type=\"text/javascript\">\n"
        out << "    \$(document).ready(function () {\n"
        out << "        \$(\"#${attrs.name}\").DataTable({\n"
        out << "            \"columnDefs\": [\n"
        out << "                {\"className\": \"dt-center\", \"targets\": \"_all\"}\n"
        out << "            ],\n"
        out << "            \"deferRender\": true,\n"

        if (attrs.data != null && !attrs.data.isEmpty()) {
            out << "            \"processing\": true,\n"
            out << "            \"serverSide\": true,\n"
            //out << "            \"order\": [],\n"
            out << "            \"ajax\": {\n"
            out << "                url: \"${attrs.data}\",\n"
            out << "                type: \"POST\",\n"
            //out << "                dataSrc: ''\n"
            out << "            },\n"

            out << "            \"columns\": [\n"

            if (attrs.columns != null) {
                String colstxt = "${attrs.columns}".replaceAll("\\s+","")
                def cols = colstxt.split(",")

                cols.eachWithIndex { col, i ->
                    /*if (linkColumn != null && col.equals(linkColumn)){
                        //its a column that contains a link
                        out << "                  { data: '${col}', render: function(data, type, row) {\n"
                        out << "                      return '<a href=\"${g.createLink(action: linkAction)}/' + row.${linkId} + '\">' + row.${col} + '</a>';\n"
                        out << "                  }}"
                        out << "${i+1==cols.length ? '' : ','}\n"
                    } else {
                        out << "                  { data: '${col}' }${i+1==cols.length ? '' : ','}\n"
                    }*/

                    out << "                  { data: '${col}' }${i+1==cols.length ? '' : ','}\n"

                }
            }

            out << "              ],\n"
        }

        if (attrs.nodetails != null && attrs.nodetails=="true") {
            out << "            \"dom\": 'rt',"
        }
        if (attrs.pageLength != null){
            out << "            \"pageLength\": ${attrs.pageLength}, \n"
        }
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
            //println "nosort"
            out << "            , \"bSort\": false"
        }

        out << "        });\n"
        out << "    });\n"
        out << "</script>\n"

    }

}
