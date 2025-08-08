package org.philimone.hds.explorer.taglib


import groovy.json.JsonSlurper
import net.betainteractive.utilities.StringUtil

class TabulatorTagLib {

    static namespace = "tb"

    def generalUtilitiesService
    def dataModelsService

    def tabulatorResources = {
        out << "        " + asset.stylesheet(src: "tabulator_site.min.css") + "\n"
        out << "        " + asset.javascript(src: "tabulator.min.js") + "\n"
    }

    def luxonResources = {
        out << "        " + asset.javascript(src: "luxon.min.js") + "\n"
    }

    def jquiResources = {
        out << "        " + asset.stylesheet(src: "jquery-ui.min.css") + "\n"
        out << "        " + asset.javascript(src: "jquery-ui.min.js") + "\n"
    }

    def kwCalendarResources = {
        //out << "        " + asset.stylesheet(src: "ui-pepper-grinder.calendars.picker.css") + "\n"
        out << "        " + asset.stylesheet(src: "smoothness.calendars.picker.css") + "\n"
        //out << "        " + asset.stylesheet(src: "humanity.calendars.picker.css") + "\n"
        //out << "        " + asset.stylesheet(src: "jquery.calendars.picker.css") + "\n"
        out << "        " + asset.javascript(src: "jquery.plugin.min.js") + "\n"
        out << "        " + asset.javascript(src: "jquery.calendars.min.js") + "\n"
        out << "        " + asset.javascript(src: "jquery.calendars.plus.min.js") + "\n"
        out << "        " + asset.javascript(src: "jquery.calendars.picker.min.js") + "\n"
        out << "        " + asset.javascript(src: "jquery.calendars.ethiopian.min.js") + "\n"
        out << "        " + asset.javascript(src: "jquery.calendars.lang.min.js") + "\n"
        out << "        " + asset.javascript(src: "jquery.calendars.picker.lang.min.js") + "\n"
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

            //create autocomplete editor
            out << "        //Autocomplete Editor for Tabulator\n"
            out << "        var tabulatorAutocompleteEditor = function(cell, onRendered, success, cancel, editorParams) {\n"
            out << "            var editor = \$(\"<input type='text'>\"); //document.createElement(\"input\");\n"
            out << "            // Set current cell value\n"
            out << "            editor.val(cell.getValue());\n"
            out << "\n"
            out << "            // Initialize jQuery UI Autocomplete\n"
            out << "            editor.autocomplete({\n"
            out << "                source: function(request, response) {\n"
            out << "                    if (request.term.length >= 3) {\n"
            out << "                        \$.ajax({\n"
            out << "                            url: \"\"+editorParams.url+\"\",\n"
            out << "                            type: \"GET\",\n"
            out << "                            data: { term: request.term },\n"
            out << "                            success: function(data) {\n"
            out << "                                response(data);\n"
            out << "                            }\n"
            out << "                         });\n"
            out << "                    } else {\n"
            out << "                        response([]);\n"
            out << "                    }\n"
            out << "                },\n"
            //out << "                minLength: 3,  // Start autocomplete after typing 3 characters\n"
            out << "                select: function(event, ui) {\n"
            out << "                    success(ui.item.value);  // Use the selected value\n"
            out << "                },\n"
            out << "                close: function() {\n"
            out << "                    //cancel();\n"
            out << "                }\n"
            out << "            });\n"
            out << "\n"
            out << "            editor.css({\n"
            out << "                width: \"100%\",\n"
            out << "                height: \"100%\",\n"
            out << "                padding: \"4px\",\n"
            out << "                boxSizing: \"border-box\"\n"
            out << "            });\n"
            out << "\n"
            out << "            // Focus the input after rendering\n"
            out << "            onRendered(function() {\n"
            out << "                editor.appendTo(cell.getElement());\n"
            out << "                editor.focus();\n"
            //out << "                editor.select();\n"
            out << "            });\n"
            out << "\n"
            //out << "            // Attach the input to the cell\n"
            //out << "            \$(cell.getElement()).append(editor);\n"
            out << "\n"
            out << "            // Handle loss of focus to submit or cancel\n"
            out << "            editor.on(\"keydown\", function(e) {\n"
            out << "                if (e.keyCode === 13) { // Enter pressed\n"
            out << "                    success(editor.val());\n"
            out << "                } else if (e.keyCode === 27) { // Escape pressed\n"
            out << "                    cancel();\n"
            out << "                }\n"
            out << "            });\n"
            out << "\n"
            out << "            return editor.get(0);\n"
            out << "        };\n\n"

            //create menu for adding and deleting rows
            out << "        var toast = \$(\"#${attrs.toastid}\");\n"
            out << "        var toastIconInfo = \$(\"#${attrs.toastid}_icon_info\");\n"
            out << "        var toastIconError = \$(\"#${attrs.toastid}_icon_error\");\n"
            out << "        var toastTitleInfo = \$(\"#${attrs.toastid}_info_title\");\n"
            out << "        var toastTitleError = \$(\"#${attrs.toastid}_erro_title\");\n"
            out << "        var toastMessage = \$(\"#${attrs.toastid}_message\");\n"

            if (hasPopupMenu && !menusJsonText.empty) {

                def menusJson = converter.parseText(menusJsonText)

                //instantiate rowMenu
                out << "        var rowMenu = [\n"

                //create menu items
                menusJson.eachWithIndex { obj, i ->
                    def mlabel = obj.label
                    def maction = obj.action
                    def mtype = obj.type as String
                    def mdisabledtext = obj.disabled != null ? "disabled:${obj.disabled},\n" : ""
                    def confirmDialogText = obj.confirmDialog != null ? "${obj.confirmDialog}" : null
                    def addToTableId = obj.add_to_table != null ? "${obj.add_to_table}" : null
                    def classMenuItem = mtype?.equalsIgnoreCase("add") ? "xsave" : (mtype?.equalsIgnoreCase("remove") || mtype?.equalsIgnoreCase("delete")) ? "xdelete" : mtype?.equalsIgnoreCase("update") ? "xedit" : ""

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
                    out << "                    var rowTable = row.getTable(); \n"
                    out << "                    \n"

                    //confirm dialog
                    if (!StringUtil.isBlank(confirmDialogText)){

                        confirmDialogText = generalUtilitiesService.getMessage(confirmDialogText, confirmDialogText)

                        out << "                    if (confirm('${confirmDialogText}')==false) {\n"
                        out << "                        return;\n"
                        out << "                    }\n"
                    }

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
                    if (mtype?.equalsIgnoreCase("add")) {

                        if (addToTableId != null){
                            //override with other table
                            out << "                                 rowTable = Tabulator.findTable(\"#${addToTableId}\")[0];\n"
                        }

                        out << "                                 rowTable.addRow(jdata); //add new row\n"

                    } else if (mtype?.equalsIgnoreCase("remove") || mtype?.equalsIgnoreCase("delete")) {
                        out << "                                 row.delete();\n"
                    } else if (mtype?.equalsIgnoreCase("update")) {
                        //I would like to update the table with a value returned by json
                        out << "                                 rowTable.updateData(jdata); //update the row\n"
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
                def ceditopts = obj.editorOptions != null ? "${obj.editorOptions}" : null
                def hzalign = obj.hzalign != null ? "${obj.hzalign}" : null
                def vtalign = obj.vtalign != null ? "${obj.vtalign}" : null
                def display = obj.display != null ? "${obj.display}" : null
                def formatter = obj.formatter != null ? "${obj.formatter}" : null
                def link = obj.link != null ? "${obj.link}" : ""

                def final_cedit = StringUtil.isBlank(cedit) ? "" : cedit.equalsIgnoreCase("autocomplete") ? "tabulatorAutocompleteEditor" : "'${cedit}'"
                def cedittext = StringUtil.isBlank(cedit) ? "" : ", editor:${final_cedit}"
                def ceditoptstext = ""
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

                if (!StringUtil.isBlank(link)) {
                    //override the formmtter
                    displaytext = ", formatter:function(cell, formatterParams, onRendered){ return \"<a href='${link}/\" + cell.getValue() + \"'>\" + cell.getValue() + \"</a>\"; }"
                }

                if (!StringUtil.isBlank(cedit)) {

                    ceditoptstext = ", editorParams:{\n"

                    if (cedit.equalsIgnoreCase("list") || cedit.equalsIgnoreCase("select")) {
                        def opts = dataModelsService.isRegisteredEnumType(ceditopts) ? dataModelsService.getEnumValuesJSON(ceditopts) : ceditopts
                        ceditoptstext += "                      values:${opts}\n"

                    } else if (cedit.equalsIgnoreCase("date")) {
                        ceditoptstext += "                      format:\"yyyy-MM-dd\""

                    } else if (cedit.equalsIgnoreCase("autocomplete")) {

                        def fetchUrl = dataModelsService.getLookupValuesUrl(ceditopts)

                        ceditoptstext += "                      url: \"${fetchUrl}\", \n"
                                     /*+ "                 emptyValue:null,\n" +
                                         "                 filterRemote:true, //pass filter term\n" +
                                         "                 filterDelay:100,\n" +
                                         "                 autocomplete:true,\n" +
                                         "                 allowEmpty:true,\n" +
                                         "                 freetext:true,\n" +
                                         "                 listOnSelect: function (e, cell, value, item) {\n" +
                                         "                     //Override to prevent premature selection\n" +
                                         "                     e.stopPropagation(); // Prevent auto-selection\n" +
                                         "                 },\n" +
                                         "                 itemFormatter: function (value, title) {\n" +
                                         "                     return value;\n" +
                                         "                 },\n"*/
                    }

                    ceditoptstext += "                  }\n                 "

                }


                out << "                 {title:'${clabel}', field:'${cname}'${cedittext}${hzaligntext}${vtaligntext}, headerHozAlign:'center'${formattertext}${displaytext}${ceditoptstext}}${i+1==columnsJson.size() ? '' : ','}\n"

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
        def confirmDialog = attrs.confirmDialog
        def addToTable = attrs.add_to_table
        def disabledtext = disabled != null ? ", \"disabled\":\"${disabled}\"" : ""
        def confirmtext = confirmDialog != null ? ", \"confirmDialog\":\"${confirmDialog}\"" : ""
        def addToTableText = addToTable != null ? ", \"add_to_table\":\"${addToTable}\"" : ""

        out << "{\"label\": \"${label}\", \"action\": \"${action}\", \"type\": \"${type}\"${disabledtext}${confirmtext}${addToTableText}},"
    }

    def column = {attrs, body ->
        def name = attrs.name
        def label = attrs.label
        def editor = attrs.editor
        def editorOptions = attrs.editorOptions
        def hzalign = attrs.hzalign
        def vtalign = attrs.vtalign
        def display = attrs.display //custom render method for the object
        def formatter = attrs.formatter
        def link = attrs.link
        //vertAlign
        def hzaligntext = "hozAlign:" + (hzalign != null ? "'${hzalign}'" : "'center'")
        def vtaligntext = vtalign != null ? "vertAlign:'${vtalign}'" : ""
        def editortext = editor != null ? "${editor}" : ""
        def editoropstext = editorOptions != null ? ", \"editorOptions\": \"${editorOptions}\"" : ""
        def displaytext = display != null ? "${display}" : ""
        def formattertext = formatter != null ? ", \"formatter\": \"${formatter}\"" : ""
        def linktext = link != null ? ", \"link\": \"${link}\"" : ""

        //, hozAlign:"center"

        out << "{\"name\": \"${name}\", \"label\": \"${label}\", \"editor\": \"${editortext}\", \"hzalign\": \"${hzaligntext}\", \"vtalign\": \"${vtaligntext}\", \"display\": \"${displaytext}\"${formattertext}${editoropstext}${linktext}},"
    }

    def toast = {attrs, body ->
        def toastid = attrs.id
        def title = attrs.title
        def message = attrs.message
        def position = attrs.position
        def infoUrl = g.createLinkTo(dir: 'images', file: 'information.png')
        def erroUrl = g.createLinkTo(dir: 'images', file: 'exclamation.png')
        def infotitle = generalUtilitiesService.getMessageWeb("tabulator.info.title.label")
        def errotitle = generalUtilitiesService.getMessageWeb("tabulator.error.title.label")

        if (position != null && "top_center".equalsIgnoreCase("${position}")) {
            out << "                <div class=\"position-fixed p-0\" style=\"z-index: 9999995; left: 50%; transform: translateX(-50%); position: relative;\">\n"
        } else {
            //top right
            out << "                <div class=\"position-fixed p-4\" style=\"z-index: 9999995; right: 30px; position: relative;\">\n"
        }

        out <<  "                    <div id=\"${toastid}\" class=\"toast hide\" style=\"background-color: #fff; opacity: 1;\" role=\"alert\" aria-live=\"assertive\" aria-atomic=\"true\" data-autohide=\"false\" >\n" + //data-delay=\"6000\"
                "                        <div class=\"toast-header\">\n" +
                "                            <img id=\"${toastid}_icon_info\" src=\"${infoUrl}\" class=\"rounded mr-2\" />\n" +
                "                            <img id=\"${toastid}_icon_error\" src=\"${erroUrl}\" class=\"rounded mr-2\" />\n" +
                "                            <strong id=\"${toastid}_info_title\" class=\"mr-auto\">${infotitle}</strong>\n" +
                "                            <strong id=\"${toastid}_erro_title\" class=\"mr-auto\">${errotitle}</strong>\n" +
                "                            <small></small>\n" +
                "                            <button type=\"button\" class=\"ml-2 mb-1 close\" data-dismiss=\"toast\" aria-label=\"Close\">\n" +
                "                                <span aria-hidden=\"true\">&times;</span>\n" +
                "                            </button>\n" +
                "                        </div>\n" +
                "                        <div class=\"toast-body\">\n" +
                "                            <b id=\"${toastid}_message\">${message}</b>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>"
    }

}
