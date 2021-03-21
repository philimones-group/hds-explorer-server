package org.philimone.hds.explorer.taglib

import net.betainteractive.utilities.StringUtil

import java.time.LocalDate
import java.time.LocalDateTime

class GeneralTagLib {

    static namespace = "bi"

    def generalUtilitiesService

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
        out << "    <a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">${attrs.label} <span class=\"caret\"></span></a>"
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

    def messageStatus = {attrs, body ->
        out << generalUtilitiesService.UserMessageStatus();
    }

    def autoComplete = {attrs, body ->

        out << g.hiddenField(name: "${attrs.name}.id") + "\n"
        //out << g.textField(name: "${attrs.name}.name", style: "width: 300px;" )
        out << "<input type=\"text\" name=\"${attrs.name}.name\" style=\"width: 300px;\" value=\"\" id=\"${attrs.name}.name\" onKeyUp=\" resetId();\" />"
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

    def isUsingOpenHDS = { attrs, body ->
        if (generalUtilitiesService.isUsingOpenHDS()){
            out << body()
        }
    }

}
