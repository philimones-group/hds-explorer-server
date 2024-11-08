<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <title><g:message code="applicationParams.hierachylevel.configure.label" args="[entityName]" /></title>

        <dt:defaultResources />
    </head>
    <body>


    <g:javascript>
        $(document).ready(function() {

            //Listen to all checkboxes in the table
            $("#formsTable").on("change", ':checkbox', function(e) {
                var name = this.name.toString();
                var value = "" + this.checked;

                //console.log("checked "+this.name+" value="+value)

                if (name.startsWith("enabled.")) {
                    var elemName = name.replace("enabled.", "value\\.");
                    var element = $("#"+elemName)       ;
                    value = value + "." + element.val();

                    if (!this.checked) {
                        //clear the text field
                        element.val("");
                    }

                    //console.log("element name = "+("#"+elemName)+" value="+elemValue)
                }

                $.ajax({
                    url: "${createLink(controller: "applicationParam", action: "updateHierarchiesTable")}", data: { "name" : name + ":" + value}, cache: false,
                    success: function(html) {  }
                });

            });
        });
    </g:javascript>

        <a href="#list-applicationParam" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-applicationParam" class="content scaffold-list" role="main">
            <h1><g:message code="applicationParams.hierachylevel.configure.label" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>


            <g:form controller="applicationParam" method="post">

                <br>
                <fieldset class="buttons">
                    <g:actionSubmit name="create" class="save" action="updateLevels" value="${message(code: 'applicationParams.hierachylevel.configure.update.label', default: 'Update')}" />
                </fieldset>
                <br>

                <dt:table id="formsTable">
                    <thead>
                    <tr>
                        <th></th>

                        <th><g:message code="applicationParams.hierachylevel.name.label" /></th>

                        <th><g:message code="applicationParams.hierachylevel.value.label" /></th>

                        <th><g:message code="applicationParams.hierachylevel.head.label" /></th>

                        <th><g:message code="applicationParams.hierachylevel.enabled.label" /></th>

                        <th></th>

                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${applicationParamList}" status="i" var="appParam">
                        <tr>
                            <td></td>

                            <td><g:message code="${appParam.i18nName}" /></td>

                            <td><g:textField name="value.${appParam.name}" value="${appParam.value}" /> </td>

                            <td><g:checkBox name="head.${appParam.name}" value="${appParam.head!=null && appParam.head.equals('true')}" /></td>

                            <td><g:checkBox name="enabled.${appParam.name}" value="${appParam.value!=null}" /></td>

                            <td></td>
                        </tr>
                    </g:each>
                    </tbody>
                </dt:table>

            </g:form>


        </div>

        <dt:loadDatatable name="formsTable" />


    </body>
</html>