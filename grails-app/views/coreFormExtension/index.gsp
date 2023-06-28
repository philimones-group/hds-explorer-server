<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'coreFormExtension.label', default: 'CoreFormExtension')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <dt:defaultResources />
    </head>
    <body>
        <a href="#list-coreFormExtension" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-coreFormExtension" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>


            <g:form controller="coreFormExtension" method="post">

                <br>
                <fieldset class="buttons">
                    <g:actionSubmit name="create" class="save" action="updateForms" value="${message(code: 'coreFormExtension.updateforms.label', default: 'Add')}" />
                </fieldset>
                <br>

                <dt:table id="formsTable">
                    <thead>
                    <tr>
                        <th></th>

                        <th><g:message code="coreFormExtension.formName.label" /></th>

                        <th><g:message code="coreFormExtension.formId.label" /></th>

                        <th><g:message code="coreFormExtension.extFormId.label" /></th>

                        <th><g:message code="coreFormExtension.required.label" /></th>

                        <th><g:message code="coreFormExtension.enabled.label" /></th>


                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${coreFormExtensionList}" status="i" var="formInstance">
                        <tr>

                            <td></td>

                            <td><g:message code="${formInstance.formName}" /></td>

                            <td>${formInstance.formId}</td>

                            <td><g:link action="downloadFormXLS" id="${formInstance.id}">${formInstance.extFormId}</g:link></td>

                            <td><g:checkBox name="required.${formInstance.extFormId}" value="${formInstance.required}" /></td>

                            <td><g:checkBox name="enabled.${formInstance.extFormId}" value="${formInstance.enabled}" /></td>

                        </tr>
                    </g:each>
                    </tbody>
                </dt:table>

            </g:form>


        </div>

        <dt:loadDatatable name="formsTable" pageLength="25" />


    </body>
</html>