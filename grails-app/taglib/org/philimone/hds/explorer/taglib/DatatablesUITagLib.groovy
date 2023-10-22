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

    def tabulator = { attrs, body ->

        def dataUrl = attrs.data
        def boxed = attrs.boxed
        def addlabel = attrs.addlabel
        def remlabel = attrs.remlabel
        def errlabel = attrs.errlabel
        def inflabel = attrs.inflabel
        def bodyJson = "${body()?.trim()}"

        if (boxed != null && boxed=="true") {
            out << "\t\t\t<div class=\"whitebox_panel\">\n"
            out << "\t\t\t\t<div id=\"${attrs.name}\"></div>\n"
            out << "\t\t\t</div>\n"
        } else {
            //unboxed
            out << "\t\t\t<div id=\"${attrs.name}\"></div>\n"
        }


        //body get elements from it

        try {
            bodyJson = bodyJson.substring(0, bodyJson.length() - 1)
            bodyJson = "[${bodyJson}]"

            //println bodyJson

            def converter = new JsonSlurper()
            def json = converter.parseText(bodyJson)

            out << "\n"
            out << "<script type=\"text/javascript\">\n"
            out << "    \$(document).ready(function () {\n"

            //create menu for adding and deleting rows
            out << "        var toast = \$(\"#${attrs.toastid}\");\n"
            out << "        var toastTitle = \$(\"#dt_toast_title\");\n"
            out << "        var toastMessage = \$(\"#dt_toast_message\");\n"
            out << "        var rowMenu = [\n"
            out << "             {\n"
            out << "                 label:\"<i class='xsave'></i> ${addlabel}\",\n"
            out << "                 action:function(e, row){\n"
            out << "                    //add new row\n" //call a remote function to add new row based on the columnName of row, then catch the json and insert with table.addRow
            out << "                    var jsonData = { id: '' + row.getData()?.id }; \n"
            out << "                    \n"
            out << "                    \$.ajax({\n"
            out << "                         url: \"${attrs.createrow}\",\n"
            out << "                         type: 'POST',\n"
            out << "                         contentType: 'application/json',\n"
            out << "                         data: JSON.stringify(jsonData),\n"
            out << "                         cache: false,\n"
            out << "                         success: function(json) {\n" //the result is a json containing the new row object
            out << "                             table.addRow(json); \n"
            out << "                         }\n"
            out << "                    });\n"
            out << "                 }\n"
            out << "              },\n"
            out << "              {\n"
            out << "                 separator:true,\n"
            out << "              },\n"
            out << "              {\n"
            out << "                 label:\"<i class='xdelete'></i> ${remlabel}\",\n"
            out << "                 action:function(e, row){\n"
            out << "                    var jsonData = { id: '' + row.getData()?.id }; \n"
            out << "                    \n"
            out << "                    \$.ajax({\n"
            out << "                         url: \"${attrs.deleterow}\",\n"
            out << "                         type: 'POST',\n"
            out << "                         contentType: 'application/json',\n"
            out << "                         data: JSON.stringify(jsonData),\n"
            out << "                         cache: false,\n"
            out << "                         success: function(json) {\n" //the result is a json containing the new row object
            out << "                             var result = json.result\n"
            out << "                             var message = json.message\n"
            out << "                             if (result==\"ERROR\") {\n"
            out << "                                 toastTitle.html('${errlabel}')\n"
            out << "                             } else {\n"
            out << "                                 toastTitle.html('${inflabel}')\n"
            out << "                                 row.delete();\n"
            out << "                             }\n"
            out << "                            toastMessage.html(message);\n"
            out << "                            toast.toast('show');\n"
            out << "                         }\n"
            out << "                    });\n"
            out << "                 }\n"
            out << "              }\n"
            out << "        ]\n\n"

            //create Tabulator on DOM element with id \"example-table\"\n" +

            out << "        var table = new Tabulator('#${attrs.name}', {\n"
            //out << "             height:205, \n"           //set height of table (in CSS or here), this enables the Virtual DOM and improves render speed dramatically (can be any valid css height value)
            out << "             ajaxURL:'${dataUrl}', \n"       //assign data to table
            out << "             layout:'fitColumns', \n"  //fit columns to width of table (optional)
            out << "             rowContextMenu: rowMenu,\n"
            out << "             columns:[ \n"             //Define Table Columns

            json.eachWithIndex { obj, i ->
                def cname = obj.name
                def clabel = obj.label
                def cedit = obj.editor != null ? "${obj.editor}" : null
                def hzalign = obj.hzalign != null ? "${obj.hzalign}" : null
                def vtalign = obj.vtalign != null ? "${obj.vtalign}" : null
                def cedittext = StringUtil.isBlank(cedit) ? "" : ", ${cedit}"
                def hzaligntext = StringUtil.isBlank(hzalign) ? "" : ", ${hzalign}"
                def vtaligntext = StringUtil.isBlank(vtalign) ? "" : ", ${vtalign}"

                out << "                 {title:'${clabel}', field:'${cname}'${cedittext}${hzaligntext}${vtaligntext}, headerHozAlign:'center' }${i+1==json.size() ? '' : ','}\n"

                // Do something with the values
                //println "Name: $cname, Label: $clabel"
            }

            out << "             ],\n"
            out << "        });\n"
            out << "        \n"

            //Function to update database when cell is edited
            //Needs to call a method that receives a json{id:"aaa", value:"xxx"} and updates the system
            out << "        table.on(\"cellEdited\", function(cell){\n"
            out << "            var jsonData = {\n"
            out << "               id: \"\" + cell.getRow()?.getData()?.id, \n"
            out << "               column: \"\" + cell.getColumn().getField(),\n"
            out << "               value: \"\" + cell.getValue()\n"
            out << "            };\n"
            out << "            \n"
            out << "            \$.ajax({\n"
            out << "                 url: \"${attrs.update}\",\n"
            out << "                 type: 'POST',\n"
            out << "                 contentType: 'application/json',\n"
            out << "                 data: JSON.stringify(jsonData),\n"
            out << "                 cache: false,\n"
            out << "                 success: function(json) {\n"
            out << "                     var result = json.result\n"
            out << "                     var message = json.message\n"
            out << "                     if (result==\"ERROR\") {\n"
            out << "                         toastTitle.html('${errlabel}')\n"
            out << "                         toastMessage.html(message);\n"
            out << "                         cell.restoreOldValue();\n"
            out << "                     } else {\n"
            out << "                         toastTitle.html('${inflabel}')\n"
            out << "                         toastMessage.html(message);\n"
            out << "                     }\n"
            out << "                     toast.toast('show');\n"
            out << "                 }\n"
            out << "            });\n"
            out << "        });\n"

            out << "    });\n"
            out << "</script>\n"

        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    def column = {attrs, body ->
        def name = attrs.name
        def label = attrs.label
        def editor = attrs.editor
        def hzalign = attrs.hzalign
        def vtalign = attrs.vtalign
        //vertAlign
        def hzaligntext = "hozAlign:" + (hzalign != null ? "'${hzalign}'" : "'center'")
        def vtaligntext = vtalign != null ? "vertAlign:'${vtalign}'" : ""
        def editortext = editor != null ? "editor:'${editor}'" : ""

        //, hozAlign:"center"

        out << "{\"name\": \"${name}\", \"label\": \"${label}\", \"editor\": \"${editortext}\", \"hzalign\": \"${hzaligntext}\", \"vtalign\": \"${vtaligntext}\"},"
    }

    def toast = {attrs, body ->
        def toastid = attrs.id
        def title = attrs.title
        def message = attrs.message

        out << "                <div class=\"position-fixed top-0 right-0 p-4\" style=\"z-index: 9999995; right: 30px; position: relative;\">\n" +
               "                    <div id=\"${toastid}\" class=\"toast hide\" style=\"background-color: #fff; opacity: 1;\" role=\"alert\" aria-live=\"assertive\" aria-atomic=\"true\" data-delay=\"4000\">\n" +
               "                        <div class=\"toast-header\">\n" +
               "                            <strong id=\"dt_toast_title\" class=\"mr-auto\">${title}</strong>\n" +
               "                            <small></small>\n" +
               "                            <button type=\"button\" class=\"ml-2 mb-1 close\" data-dismiss=\"toast\" aria-label=\"Close\">\n" +
               "                                <span aria-hidden=\"true\">&times;</span>\n" +
               "                            </button>\n" +
               "                        </div>\n" +
               "                        <div class=\"toast-body\">\n" +
               "                            <b id=\"dt_toast_message\">${message}</b>\n" +
               "                        </div>\n" +
               "                    </div>\n" +
               "                </div>"
    }

}
