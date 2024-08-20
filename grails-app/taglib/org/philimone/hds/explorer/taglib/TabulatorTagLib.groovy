package org.philimone.hds.explorer.taglib

import grails.web.mapping.LinkGenerator
import groovy.json.JsonSlurper
import net.betainteractive.utilities.StringUtil

class TabulatorTagLib {

    static namespace = "tb"

    def generalUtilitiesService

    def tabulatorResources = {
        out << "        " + asset.stylesheet(src: "tabulator_site.min.css") + "\n"
        out << "        " + asset.javascript(src: "tabulator.min.js") + "\n"
    }

    def tabulator = { attrs, body ->

        def dataUrl = attrs.data
        def boxed = attrs.boxed
        def contextMenu = attrs.contextMenu
        def paginationSize = attrs.paginationSize
        def hasPopupMenu = contextMenu != null ? "true".equalsIgnoreCase(contextMenu) : false



        def errlabel = attrs.errlabel
        def inflabel = attrs.inflabel
        def bodyJsonText = "${body()?.trim()}"

        //add menus, menu1_label, menu2_label, menu3_label, menu1_action, menu2_action, menu3_action

        if (boxed != null && boxed=="true") {
            out << "\t\t\t<div class=\"whitebox_panel\">\n"
            out << "\t\t\t\t<div id=\"${attrs.name}\"></div>\n"
            out << "\t\t\t</div>\n"
        } else {
            //unboxed
            out << "\t\t\t<div id=\"${attrs.name}\"></div>\n"
        }


        //body get elements from it
        /*
        New rule
        tabulator - contextMenuEnabled="true"
        tabulator body:
          should contain contextMenu and columns
          <menubar>
            <menu label="text" action="url" type="add|delete|update" />
          </menubar>


          <m>menus<m>columns

          output:
          <m>{},{},{}<m>{},{},{},
        */


        try {
            bodyJsonText = bodyJsonText.substring(0, bodyJsonText.length() - 1) //remove last comma from columns
            def columnsJsonText = ""
            def menusJsonText = ""

            if (bodyJsonText.startsWith("<m>")) { //contains menu
                bodyJsonText = bodyJsonText.replaceFirst("<m>", "")
                def spt = bodyJsonText.split("<m>")

                menusJsonText = "[${spt[0]}]" //menus represented as json objects containing a map with menu attributes
                columnsJsonText = "[${spt[1]}]" //columns represented as json objects, containing a map with column attributes

            } else {
                columnsJsonText = "[${bodyJsonText}]" //columns represented as json objects, containing a map with column attributes
            }


            //println bodyJson

            def converter = new JsonSlurper()
            def columnsJson = converter.parseText(columnsJsonText)

            out << "\n"
            out << "<script type=\"text/javascript\">\n"
            out << "    \$(document).ready(function () {\n"

            //create menu for adding and deleting rows
            out << "        var toast = \$(\"#${attrs.toastid}\");\n"
            out << "        var toastIconInfo = \$(\"#toastIconInfo\");\n"
            out << "        var toastIconError = \$(\"#toastIconError\");\n"
            out << "        var toastTitleInfo = \$(\"#dt_toast_info_title\");\n"
            out << "        var toastTitleError = \$(\"#dt_toast_erro_title\");\n"
            out << "        var toastMessage = \$(\"#dt_toast_message\");\n"

            if (hasPopupMenu && !menusJsonText.empty) {

                def menusJson = converter.parseText(menusJsonText)

                //instantiate rowMenu
                out << "        var rowMenu = [\n"

                //create menu items
                menusJson.eachWithIndex { obj, i ->
                    def mlabel = obj.label
                    def maction = obj.action
                    def mtype = obj.type
                    def mdisabledtext = obj.disabled != null ? "disabled:${obj.disabled},\n" : ""
                    def classMenuItem = mtype.equals("add") ? "xsave" : (mtype.equals("remove") || mtype.equals("delete")) ? "xdelete" : "xedit"

                    if (i > 0) { //add separator after the first item is added
                        out << "              {\n"
                        out << "                 separator:true,\n"
                        out << "              },\n"
                    }

                    //add menu item
                    out << "             {\n"
                    out << "                 label:\"<i class='${classMenuItem}'></i> ${mlabel}\",\n"

                    if (!StringUtil.isBlank(mdisabledtext)) {
                        out << "                 ${mdisabledtext}"
                    }

                    out << "                 action:function(e, row){\n"
                    out << "                    var jsonData = { id: '' + row.getData()?.id }; \n"
                    out << "                    \n"
                    out << "                    \$.ajax({\n" //call a remote function and return json
                    out << "                         url: \"${maction}\",\n"
                    out << "                         type: 'POST',\n"
                    out << "                         contentType: 'application/json',\n"
                    out << "                         data: JSON.stringify(jsonData),\n"
                    out << "                         cache: false,\n"
                    out << "                         success: function(json) {\n" //the result is a json containing the new row object or edited object

                    //every action returns JActionResult = [result:'', message:'', data:JSONList]

                    out << "                             var jresult = json.result\n" //[SUCCESS | ERROR]
                    out << "                             var jmessage = json.message\n"
                    out << "                             var jdata = json.data\n"
                    out << "                             \n"
                    out << "                             if (jresult==\"ERROR\") {\n"
                    out << "                                 toastIconInfo.hide();\n"
                    out << "                                 toastTitleInfo.hide();\n"
                    out << "                                 toastIconError.show();\n"
                    out << "                                 toastTitleError.show();\n"
                    out << "                             } else {\n"
                    out << "                                 toastIconInfo.show();\n"
                    out << "                                 toastTitleInfo.show();\n"
                    out << "                                 toastIconError.hide();\n"
                    out << "                                 toastTitleError.hide();\n"
                    //action
                    if (mtype.equals("add")) {
                        out << "                                 table.addRow(jdata); //add new row\n"
                    } else if (mtype.equals("remove") || mtype.equals("delete")) {
                        out << "                                 row.delete();\n"
                    } else if (mtype.equals("update")) {
                        //I would like to update the table with a value returned by json
                        out << "                                 table.updateData(jdata); //update the row\n"
                    } else {
                        out << "                                 //do nothing\n"
                    }

                    out << "                             }\n"
                    out << "                             toastMessage.html(jmessage);\n"
                    out << "                             toast.toast('show');\n"

                    //closing success function and menu brackets
                    def isLastItem = menusJson.size()-1==i
                    out << "                         }\n"
                    out << "                    });\n"
                    out << "                 }\n"
                    out << "              }" + (isLastItem ? "" : ",") + "\n"

                }

                //closing instantiation of rowMenu
                out << "        ]\n\n"
            }

            //create Tabulator on DOM element with id \"example-table\"\n" +

            out << "        var table = new Tabulator('#${attrs.name}', {\n"
            //out << "             height:205, \n"           //set height of table (in CSS or here), this enables the Virtual DOM and improves render speed dramatically (can be any valid css height value)
            out << "             ajaxURL:'${dataUrl}', \n"       //assign data to table
            out << "             layout:'fitColumns', \n"  //fit columns to width of table (optional)

            if (paginationSize != null && !"${paginationSize}".empty) {
                out << "             pagination:'local',                //paginate the data\n"
                out << "             paginationSize:${paginationSize},  //allow X rows per page of data\n"
                out << "             paginationCounter:'rows', "
            }

            //Set context menu
            if (hasPopupMenu) { //set the popup menu
                out << "             rowContextMenu: rowMenu,\n"
            }

            //Defining Columns
            out << "             columns:[ \n"             //Define Table Columns

            columnsJson.eachWithIndex { obj, i ->
                def cname = obj.name
                def clabel = obj.label
                def cedit = obj.editor != null ? "${obj.editor}" : null
                def hzalign = obj.hzalign != null ? "${obj.hzalign}" : null
                def vtalign = obj.vtalign != null ? "${obj.vtalign}" : null
                def display = obj.display != null ? "${obj.display}" : null
                def formatter = obj.formatter != null ? "${obj.formatter}" : null

                def cedittext = StringUtil.isBlank(cedit) ? "" : ", ${cedit}"
                def hzaligntext = StringUtil.isBlank(hzalign) ? "" : ", ${hzalign}"
                def vtaligntext = StringUtil.isBlank(vtalign) ? "" : ", ${vtalign}"
                def displaytext = ""
                def formattertext = StringUtil.isBlank(formatter) ? "" : ", formatter:'${formatter}'"

                //custom cell renderers
                if (!StringUtil.isBlank(display)) {
                    if (display.equalsIgnoreCase("enumType")) { //call object.toString()
                        displaytext = ", formatter:function(cell, formatterParams, onRendered){ return cell.getValue()?.name; }"
                    }
                }

                out << "                 {title:'${clabel}', field:'${cname}'${cedittext}${hzaligntext}${vtaligntext}, headerHozAlign:'center'${formattertext}${displaytext}}${i+1==columnsJson.size() ? '' : ','}\n"

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
            out << "                         toastIconInfo.hide();\n"
            out << "                         toastTitleInfo.hide();\n"
            out << "                         toastIconError.show();\n"
            out << "                         toastTitleError.show();\n"
            out << "                         cell.restoreOldValue();\n"
            out << "                     } else {\n"
            out << "                         toastIconInfo.show();\n"
            out << "                         toastTitleInfo.show();\n"
            out << "                         toastIconError.hide();\n"
            out << "                         toastTitleError.hide();\n"
            out << "                     }\n"
            out << "                     \n"
            out << "                     toastMessage.html(message);\n"
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

    def menuBar = { attrs, body ->
        def bodyJson = "${body()?.trim()}"
        bodyJson = bodyJson.length()>0 ? bodyJson.substring(0, bodyJson.length() - 1) : bodyJson //remove last comma

        out << "<m>${bodyJson}<m>"
    }

    def menu = { attrs, body ->
        /*
        <menubar>
            <menu label="text" action="url" type="add|delete|update" />
        </menubar>
         */

        def label = attrs.label
        def action = attrs.action
        def type = attrs.type
        def disabled = attrs.disabled
        def disabledtext = disabled != null ? ", \"disabled\":\"${disabled}\"" : ""

        out << "{\"label\": \"${label}\", \"action\": \"${action}\", \"type\": \"${type}\"${disabledtext}},"
    }

    def column = {attrs, body ->
        def name = attrs.name
        def label = attrs.label
        def editor = attrs.editor
        def hzalign = attrs.hzalign
        def vtalign = attrs.vtalign
        def display = attrs.display //custom render method for the object
        def formatter = attrs.formatter
        //vertAlign
        def hzaligntext = "hozAlign:" + (hzalign != null ? "'${hzalign}'" : "'center'")
        def vtaligntext = vtalign != null ? "vertAlign:'${vtalign}'" : ""
        def editortext = editor != null ? "editor:'${editor}'" : ""
        def displaytext = display != null ? "${display}" : ""
        def formattertext = formatter != null ? ", \"formatter\": \"${formatter}\"" : ""

        //, hozAlign:"center"

        out << "{\"name\": \"${name}\", \"label\": \"${label}\", \"editor\": \"${editortext}\", \"hzalign\": \"${hzaligntext}\", \"vtalign\": \"${vtaligntext}\", \"display\": \"${displaytext}\"${formattertext}},"
    }

    def toast = {attrs, body ->
        def toastid = attrs.id
        def title = attrs.title
        def message = attrs.message
        def infoUrl = g.createLinkTo(dir: 'images', file: 'information.png')
        def erroUrl = g.createLinkTo(dir: 'images', file: 'exclamation.png')
        def infotitle = generalUtilitiesService.getMessageWeb("tabulator.info.title.label")
        def errotitle = generalUtilitiesService.getMessageWeb("tabulator.error.title.label")

        out << "                <div class=\"position-fixed top-0 right-0 p-4\" style=\"z-index: 9999995; right: 30px; position: relative;\">\n" +
                "                    <div id=\"${toastid}\" class=\"toast hide\" style=\"background-color: #fff; opacity: 1;\" role=\"alert\" aria-live=\"assertive\" aria-atomic=\"true\" data-delay=\"6000\">\n" +
                "                        <div class=\"toast-header\">\n" +
                "                            <img id=\"toastIconInfo\" src=\"${infoUrl}\" class=\"rounded mr-2\" />\n" +
                "                            <img id=\"toastIconError\" src=\"${erroUrl}\" class=\"rounded mr-2\" />\n" +
                "                            <strong id=\"dt_toast_info_title\" class=\"mr-auto\">${infotitle}</strong>\n" +
                "                            <strong id=\"dt_toast_erro_title\" class=\"mr-auto\">${errotitle}</strong>\n" +
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
