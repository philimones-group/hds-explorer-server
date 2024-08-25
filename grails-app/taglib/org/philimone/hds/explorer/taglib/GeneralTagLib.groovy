package org.philimone.hds.explorer.taglib

import net.betainteractive.utilities.StringUtil

import java.time.LocalDate
import java.time.LocalDateTime

class GeneralTagLib {

    static namespace = "bi"

    def generalUtilitiesService
    def dataModelsService

    /*Menu*/
    def menuBar = {attrs, body ->
        out << '<div class="navbar-collapse collapse" aria-expanded="false" style="height: 0.8px;">'
        out << '    <ul class="nav navbar-nav navbar-right">'

        out << '        ' + body()

        out << '    </ul>'
        out << '</div>'
    }

    def menu = {attrs, body ->

        String strClass = attrs.style
        String link = attrs.link
        String label = attrs.label

        if (strClass == null || strClass.isEmpty()){
            out << '<li>'
        }else{
            out << "<li class=\"${strClass}\"  style=\"background-color: #4e8908;\" >"
        }

        out << "<a href=\"${link}\">${label}</a>"
        out << '</li>'
    }

    def dropmenu = {attrs, body ->
        out << '<li class="dropdown">'
        out << "    <a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"true\">${attrs.label} <span class=\"caret\"></span></a>"
        out << "    <ul class=\"dropdown-menu\">"
        out << "        " + body()
        out << '    </ul>'
        out << '</li>'
    }

    def menuseparator = { attrs, body ->
        out << "<li role=\"separator\" class=\"divider\"></li>"
    }

    def menuheader = { attrs, body ->
        out << "<li class=\"dropdown-header\">${attrs.label}</li>"
    }

    //Format LocalDateTime
    def formatDate = {attrs, body ->

        def date = attrs.date
        String format = attrs.format
        def output = "${date}"

        LocalDate localDate = (date instanceof LocalDate) ? (LocalDate) date : null
        LocalDateTime localDateTime = (date instanceof LocalDateTime) ? (LocalDateTime) date : null

        if (localDate != null){
            output = "${StringUtil.format(localDate, format)}"
        } else if (localDateTime != null){
            output = "${StringUtil.format(localDateTime, format)}"
        }

        out << "${output}"
    }

    def showLoggedUser = {attrs, body ->
        out << generalUtilitiesService.userFullName();
    }

    String getObjectValue(def propertyValue){
        if (propertyValue instanceof LocalDate || propertyValue instanceof LocalDateTime) {
            LocalDate localDate = (propertyValue instanceof LocalDate) ? (LocalDate) propertyValue : null
            LocalDateTime localDateTime = (propertyValue instanceof LocalDateTime) ? (LocalDateTime) propertyValue : null

            if (localDate != null){
                return "${StringUtil.format(localDate, "yyyy-MM-dd")}"
            } else if (localDateTime != null){
                return "${StringUtil.format(localDateTime, "yyyy-MM-dd HH:mm:ss")}"
            }

        } else {
            return "${propertyValue}"
        }
    }

    def field = {attrs, body ->

        def beanInstance = attrs.get("bean")
        def propertyName = attrs.property
        def label = attrs.label
        def mode = attrs.mode //edit|show/display
        def options = attrs.options
        def nullable = attrs.nullable
        def valueMessage = attrs.valueMessage

        if ("show".equalsIgnoreCase("${mode}")){
            //output display, getmessages for label

            def propertyValue = beanInstance."${propertyName}"
            def propertyDefaultLabel = StringUtil.removePascalCase(propertyName)
            def labelText = g.message(code: label, default: propertyDefaultLabel)
            def objValue = getObjectValue(propertyValue)

            if ("true".equalsIgnoreCase("${valueMessage}")) {
                objValue = g.message(code: objValue, default: objValue)
            }

            out << "            <div class=\"fieldcontain required\">\n"
            out << "                <label for=\"${propertyName}\">\n"
            out << "                    ${labelText}\n"
            out << "                </label>\n"
            out << "                ${propertyValue==null ? '' : objValue} \n"
            out << "            </div>\n"

        } else {
            //edit mode

            if (options != null) { //its a select
                if (dataModelsService.isRegisteredEnumType("${options}")) {
                    def opts = dataModelsService.getEnumValuesArray("${options}")
                    //println "${options}, ${opts}"

                    def propertyValue = beanInstance."${propertyName}"
                    def propertyDefaultLabel = StringUtil.removePascalCase(propertyName)
                    def labelText = g.message(code: label, default: propertyDefaultLabel)

                    out << "            <div class=\"fieldcontain required\">\n"
                    out << "                <label for=\"${propertyName}\">\n"
                    out << "                    ${labelText}\n"
                    out << "                </label>\n"

                    if (nullable != null && "true".equalsIgnoreCase("${nullable}")) {
                        def emptyMap = [null: '']
                        out << "                ${g.select(name: propertyName, value: propertyValue, optionKey:'id', optionValue:'name', from: opts, noSelection: emptyMap)} \n"
                    } else {
                        out << "                ${g.select(name: propertyName, value: propertyValue, optionKey:"id", optionValue:"name", from: opts)}\n"
                    }


                    out << "            </div>\n"

                }
            } else {
                out << f.field(bean: beanInstance, property: propertyName, value: beanInstance?."${propertyName}")
            }



        }
    }

    def dateField = { attrs, body ->
        def beanInstance = attrs.get("bean")
        def propertyName = attrs.property
        def label = attrs.label
        def mode = attrs.mode

        def propertyDefaultLabel = StringUtil.removePascalCase(propertyName)
        def labelText = g.message(code: label, default: propertyDefaultLabel)
        def propertyValue = beanInstance?."${propertyName}"


        if ("show".equalsIgnoreCase("${mode}")){
            def objValue = getObjectValue(propertyValue)

            out << "            <div class=\"fieldcontain required\">\n"
            out << "                <label for=\"${propertyName}\">\n"
            out << "                    ${labelText}\n"
            out << "                </label>\n"
            out << "                ${propertyValue==null ? '' : objValue} \n"
            out << "            </div>\n"
        } else {
            def toDate = getDateValue(propertyValue)

            out << "            <div class=\"fieldcontain required\">\n"
            out << "                <label for=\"${propertyName}\">\n"
            out << "                    ${labelText}\n"
            out << "                </label>\n"
            out << "                ${g.datePicker(name: propertyName, precision: 'day', value: toDate)} \n"
            out << "            </div>\n"
        }

    }

    def tableList = { attrs, body ->
        def id = attrs.id
        def className = attrs.class
        def collectionObjs = (List) attrs.get("collection")
        def linkColumn = attrs.linkColumn
        def linkAction = attrs.linkAction
        def linkId = attrs.linkId
        def columns = attrs.columns
        def messageColumns = attrs.messageColumns
        def strcolumns = columns==null ? "" : columns.replaceAll("\\s+","")
        def strmessages= messageColumns==null ? "" : messageColumns.replaceAll("\\s+","")
        def columnsList = strcolumns.empty ? new ArrayList<String>() : strcolumns.split(",").toList()
        def messageColsList = strmessages.empty ? new ArrayList<String>() : strmessages.split(",").toList()

        println "col objs: " + collectionObjs?.size()
        println "col: ${strcolumns}, ${columns}"

        out << "            <table id=\"${id}\" class=\"display nowrap compact cell-border\">\n"
        out << "                <thead>\n"
        out << "                <tr>\n"

        columnsList.each { column ->
            def messageCode = "${className}.${column}.label"
            out << "                    ${g.sortableColumn(property: column, title: g.message(code: messageCode))}\n"
        }

        out << "                </tr>\n"
        out << "                </thead>\n"
        out << "                <tbody>\n"

        collectionObjs?.eachWithIndex { obj, i ->

            out << "                    <tr class=\"${(i % 2) == 0 ? 'even' : 'odd'}\">\n"

            columnsList.each { column ->

                def hasLink = linkColumn != null && column.toString().equals(linkColumn.toString())

                def columnValue = obj?."${column}"

                out << "                        <td>"

                def textValue = ""

                if (columnValue instanceof LocalDate) {

                    textValue = StringUtil.format(columnValue, "yyyy-MM-dd")

                } else if (columnValue instanceof LocalDateTime) {

                    textValue = StringUtil.format(columnValue, "yyyy-MM-dd HH:mm:ss")

                } else {

                    if (messageColsList.contains(column)) {
                        //message variable
                        textValue = "" + message(code: columnValue)
                    } else {
                        //normal variable
                        textValue = columnValue==null ? "" : "" + columnValue
                    }

                }

                if (hasLink) {
                    out << "${g.link( action: linkAction, id: obj."${linkId}") { textValue }}"
                } else {
                    out << textValue
                }

                out << "</td>\n"
            }

        }

        out << "                </tbody>\n"
        out << "            </table>"
    }

    Date getDateValue(def propertyValue){
        if (propertyValue == null) return null;

        if (propertyValue instanceof LocalDate) {
            return StringUtil.toDate(StringUtil.formatLocalDate(propertyValue))
        } else if (propertyValue instanceof LocalDateTime) {
            return StringUtil.toDate(StringUtil.format(propertyValue), "yyyy-MM-dd HH:mm:ss")
        }

        return null
    }

    String getFieldFromMessageCode(String messageCode) {
        messageCode = messageCode.replaceAll(".label", "")
        def str = messageCode.contains(".") ? messageCode.split("\\.")[1] : messageCode //rawDomain.fieldName
        return str
    }

    String getDefaultDomainPropertyLabel(String messageCode){
        def fieldName = getFieldFromMessageCode(messageCode)
        def str = StringUtil.removePascalCase(fieldName)

        return str
    }

    def messageStatus = {attrs, body ->
        out << generalUtilitiesService.UserMessageStatus();
    }

    def autoComplete = {attrs, body ->

        def inputValue = attrs.value ? attrs.value : ""

        out << g.hiddenField(name: "${attrs.name}.id") + "\n"
        //out << g.textField(name: "${attrs.name}.name", style: "width: 300px;" )
        out << "<input type=\"text\" name=\"${attrs.name}.name\" style=\"width: 300px;\" value=\"${inputValue}\" id=\"${attrs.name}.name\" onKeyUp=\" resetId();\" style=\"display: none\" />"
        out << "\n"
        out << "<script type=\"text/javascript\">\n"
        out << "        \$(document).ready(function () {\n"
        out << "            \$(\"#${attrs.name}\\\\.name\").autocomplete({\n"
        out << "                source: function (request, response) {\n"
        out << "                    \$.ajax({\n"
        out << "                        url: \"${createLink(controller: "${attrs.controller}", action: "${attrs.action}")}\", // remote datasource\n"
        out << "                        data: request,\n"
        out << "                        success: function (data) {\n"
        out << "                            response(data); // set the response\n"
        out << "                        },\n"
        out << "                        error: function () { // handle server errors\n"
        out << "                            \$.jGrowl(\"Unable to retrieve Companies\", {\n"
        out << "                                theme: 'ui-state-error ui-corner-all'\n"
        out << "                            });\n"
        out << "                        }\n"
        out << "                    });\n"
        out << "                },\n"
        out << "                minLength: 2, // triggered only after minimum 2 characters have been entered.\n"
        out << "                select: function (event, ui) { // event handler when user selects a company from the list.\n"
        out << "                    \$(\"#${attrs.name}\\\\.id\").val(ui.item.id); // update the hidden field.\n"
        out << "                },\n"
        out << "            });\n"
        out << "       });\n"
        out << "       function resetId(){ \$(\"#${attrs.name}\\\\\\.id\").val(''); }\n"
        out << "</script>\n"

    }

    def jqgridResources = {
        /*
        out << "        " + g.external(dir: "css", file: "jqueryui-1.10.4/themes/redmond/jquery-ui.css") + "\n"

        out << "        " + g.external(dir: "css", file: "jqgrid-3.8.2/ui.jqgrid.css") + "\n"
        out << "\n"
        out << "        " + g.external(dir: "js", file: "jqgrid-3.8.2/src/i18n/grid.locale-en.js") + "\n"
        out << "        " + g.external(dir: "js", file: "jqgrid-3.8.2/src/grid.base.js") + "\n"
        out << "        " + g.external(dir: "js", file: "jqgrid-3.8.2/src/grid.common.js") + "\n"
        out << "        " + g.external(dir: "js", file: "jqgrid-3.8.2/src/grid.formedit.js") + "\n"
        out << "        " + g.external(dir: "js", file: "jqgrid-3.8.2/src/grid.inlinedit.js") + "\n"
        out << "        " + g.external(dir: "js", file: "jqgrid-3.8.2/src/grid.custom.js") + "\n"
        out << "        " + g.external(dir: "js", file: "jqgrid-3.8.2/src/jquery.fmatter.js") + "\n"
        out << "        " + g.external(dir: "js", file: "jqgrid-3.8.2/src/jquery.searchFilter.js") + "\n"
        out << "        " + g.external(dir: "js", file: "jqgrid-3.8.2/src/grid.jqueryui.js") + "\n"
        */

        out << "        " + asset.stylesheet(src: "jqueryui-1.10.4/themes/redmond/jquery-ui.css") + "\n"

        out << "        " + asset.stylesheet(src: "jqgrid-3.8.2/ui.jqgrid.css") + "\n"
        out << "\n"
        out << "        " + asset.javascript(src: "jqgrid-3.8.2/src/i18n/grid.locale-en.js") + "\n"
        out << "        " + asset.javascript(src: "jqgrid-3.8.2/src/grid.base.js") + "\n"
        out << "        " + asset.javascript(src: "jqgrid-3.8.2/src/grid.common.js") + "\n"
        out << "        " + asset.javascript(src: "jqgrid-3.8.2/src/grid.formedit.js") + "\n"
        out << "        " + asset.javascript(src: "jqgrid-3.8.2/src/grid.inlinedit.js") + "\n"
        out << "        " + asset.javascript(src: "jqgrid-3.8.2/src/grid.custom.js") + "\n"
        out << "        " + asset.javascript(src: "jqgrid-3.8.2/src/jquery.fmatter.js") + "\n"
        out << "        " + asset.javascript(src: "jqgrid-3.8.2/src/jquery.searchFilter.js") + "\n"
        out << "        " + asset.javascript(src: "jqgrid-3.8.2/src/grid.jqueryui.js") + "\n"

    }

    def gridWrapper = { attrs, body ->
        out << g.hiddenField(name: "${attrs.name}.selected") + "\n"
        out << "<table id=\"${attrs.name}\"><tr><td></td></tr></table>" + "\n"
        out << "<div id=\"${attrs.name}Pager\"/>" + "\n"
        //out << "\n"

    }

    def gridSelection = { attrs, body ->

        out << g.hiddenField(name: "${attrs.name}.selected") + "\n"
        out << "<table id=\"${attrs.name}\"><tr><td></td></tr></table>" + "\n"
        out << "<div id=\"${attrs.name}Pager\"/>" + "\n"
        out << "\n"
        out << "<script type=\"text/javascript\">\n"
        out << "      \$(document).ready(function () {\n"
        out << "           var grid = \$(\"#${attrs.name}\");\n"
        out << "\n"
        out << "           grid.jqGrid({\n"
        out << "               url: \"${createLink(controller: "${attrs.controller}", action: "${attrs.action}", id: "${attrs.paramsId}")}\",\n"
        out << "               datatype: 'json',\n"
        out << "               mtype: 'GET',\n"
        out << "               loadonce: true,\n"
        out << "               jsonReader: { repeatitems: false },\n"
        out << "               colModel: ["

        def String text = body()
        text = text.substring(0,text.lastIndexOf(','))

        out << text << "\n"
        out << "               ],\n"
        out << "               rowNum: ${attrs.rowNum ? attrs.rowNum : 10},\n"
        out << "               rowList: [10, 20, 30, 40, 50, 300],\n"
        out << "               multiselect: true,\n"
        out << "               pager: \"#${attrs.name}Pager\",\n"
        out << "               viewrecords: true,\n"
        out << "               gridview: true,\n"
        out << "               rownumbers: true,\n"
        out << "               height: ${attrs.height},\n"
        if (attrs.width){
            out << "               width: ${attrs.width},\n"
        }
        if (attrs.autowidth){
            out << "               autowidth: ${attrs.autowidth},\n"
        }
        out << "               caption: '${attrs.title}',\n"
        out << "               loadComplete: function(data){  selectGridColumns('${attrs.values}'); },\n"
        out << "               onSelectRow: function(id){ onFinishSelectionGrid(); grid.jqGrid('editRow', id); }\n"
        out << "           });"
        out << "\n"
        out << "       });\n"
        out << "\n"
        out << "       function onFinishSelectionGrid(){\n"
        out << "           var gridP = \$(\"#${attrs.name}\");\n"
        out << "           var ids = gridP.jqGrid('getGridParam','selarrrow');\n"
        out << "           var hField = \$(\"#${attrs.name}\\\\.selected\");\n"
        out << "           var names = [];\n"
        out << "           var fields = '${attrs.getFields}'.split(\",\"); \n"
        out << "\n"
        out << "           if (ids.length>0) {\n"
        out << "               for (var i = 0; i < ids.length; i++) {\n"
        out << "                    var name = gridP.jqGrid('getCell', ids[i], '${attrs.idField}');\n"
        //out << "                    for (var j = 0; j < fields.length; j++) { \n"
        //out << "                        var field = gridP.jqGrid('getCell', ids[i], fields[j]);\n"
        // out << "                        name = name +'#'+field.text(); \n"
        //out << "                    }\n"
        out << "                    names.push(name);\n"
        out << "               }\n"
        out << "           }\n"
        out << "\n"
        out << "           hField.val(names.join(\", \"));\n"
        out << "      };"
        out << "\n"

        out << "      function selectGridColumns(selectItems){\n"
        out << "           var gridP = \$(\"#${attrs.name}\");\n"
        out << "           var ids = gridP.jqGrid('getDataIDs');\n"
        out << "\n"
        //out << "           grid.jqGrid('resetSelection');\n"
        out << "\n"
        out << "           for (var i=0; i < ids.length; i++) {\n"
        out << "                var extId = gridP.jqGrid('getCell', ids[i], 'extId');\n"
        out << "                if (selectItems.indexOf(extId)>-1){\n"
        out << "                    gridP.jqGrid('setSelection',ids[i], true);\n"
        out << "                }\n"
        out << "           }\n"
        out << "      };\n"

        out << "</script>\n"
    }

    def gridSelect = { attrs, body ->

        out << g.hiddenField(name: "${attrs.name}.selected") + "\n"
        out << "<table id=\"${attrs.name}\"><tr><td></td></tr></table>" + "\n"
        out << "<div id=\"${attrs.name}Pager\"/>" + "\n"
        out << "\n"
        out << "<script type=\"text/javascript\">\n"
        out << "      \$(document).ready(function () {\n"
        out << "           var grid = \$(\"#${attrs.name}\");\n"
        out << "\n"
        out << "           grid.jqGrid({\n"
        out << "               url: \"${createLink(controller: "${attrs.controller}", action: "${attrs.action}", id: "${attrs.paramsId}")}\",\n"
        out << "               datatype: 'json',\n"
        out << "               mtype: 'GET',\n"
        out << "               loadonce: true,\n"
        out << "               jsonReader: { repeatitems: false },\n"
        out << "               colModel: ["

        def String text = body()
        text = text.substring(0,text.lastIndexOf(','))

        out << text << "\n"
        out << "               ],\n"
        out << "               rowNum: ${attrs.rowNum ? attrs.rowNum : 10},\n"
        out << "               rowList: [10, 20, 30, 40, 50, 300],\n"
        out << "               multiselect: false,\n"
        out << "               pager: \"#${attrs.name}Pager\",\n"
        out << "               viewrecords: true,\n"
        out << "               gridview: true,\n"
        out << "               rownumbers: true,\n"
        out << "               height: ${attrs.height},\n"
        if (attrs.width){
            out << "               width: ${attrs.width},\n"
        }
        if (attrs.autowidth){
            out << "               autowidth: ${attrs.autowidth},\n"
        }
        out << "               caption: '${attrs.title}',\n"
        out << "               loadComplete: function(data){  selectGridColumns('${attrs.values}'); },\n"
        out << "               onSelectRow: function(id){ onFinishSelectionGrid(); grid.jqGrid('editRow', id); }\n"
        out << "           });"
        out << "\n"
        out << "       });\n"
        out << "\n"
        out << "       function onFinishSelectionGrid(){\n"
        out << "           var gridP = \$(\"#${attrs.name}\");\n"
        out << "           var rowid = gridP.jqGrid('getGridParam','selrow');\n"
        out << "           var hField = \$(\"#${attrs.name}\\\\.selected\");\n"
        out << "           var names = [];\n"
        out << "           var fields = '${attrs.getFields}'.split(\",\"); \n"
        out << "\n"
        out << "           var value = gridP.jqGrid('getCell', rowid, '${attrs.idField}');\n"
        out << "           names.push(value);\n"
        out << "\n"
        out << "           hField.val(value);\n"
        out << "      };"
        out << "\n"

        out << "      function selectGridColumns(selectItems){\n"
        out << "           var gridP = \$(\"#${attrs.name}\");\n"
        out << "           var ids = gridP.jqGrid('getDataIDs');\n"
        out << "\n"
        //out << "           grid.jqGrid('resetSelection');\n"
        out << "\n"
        out << "           for (var i=0; i < ids.length; i++) {\n"
        out << "                var extId = gridP.jqGrid('getCell', ids[i], 'extId');\n"
        out << "                if (selectItems.indexOf(extId)>-1){\n"
        out << "                    gridP.jqGrid('setSelection',ids[i], true);\n"
        out << "                }\n"
        out << "           }\n"
        out << "      };\n"

        out << "</script>\n"
    }

    def gridColumn = { attrs, body ->

        out << "{ "

        int asize = attrs.size()
        int i = 0

        attrs.each { k,v ->
            def comma = (i==asize-1) ? '' : ', '
            out << k << ":" << quote(v) << "" << comma
            i++
        }

        out << " },"
    }

    def quote(String v){

        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false") || v.matches("[0-9]+")){
            return v;
        }

        return "'"+v+"'"
    }

}
