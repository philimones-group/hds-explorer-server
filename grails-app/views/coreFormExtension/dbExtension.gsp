<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'coreFormExtension.label', default: 'CoreFormExtension')}" />
        <title><g:message code="coreFormExtension.dbExtension.page.title"/><g:message code="${coreFormExtension.coreForm.name}" /></title>

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
            <h1><g:message code="coreFormExtension.dbExtension.page.title"/><g:message code="${coreFormExtension.coreForm.name}" /></h1>
            <br>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:if test="${errorMessages}">
                <div class="rerrors_div">
                    <ul class="rerrors" role="alert">
                        <g:each in="${errorMessages}" status="i" var="error">
                            <li data-field-id="error">${error}</li>
                        </g:each>
                    </ul>
                </div>
                <br>
            </g:if>

            <dt:table id="extensionTable">
                <thead>
                <tr>
                    <th></th>

                    <th><g:message code="coreFormExtension.mapping.dbColumnTable" /></th>

                    <th><g:message code="coreFormExtension.mapping.dbColumnName" /></th>

                    <th><g:message code="coreFormExtension.mapping.dbColumnType" /></th>

                    <th><g:message code="coreFormExtension.mapping.dbColumnSize" /></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${columnsList}" status="i" var="dbColumn">
                    <tr>

                        <td></td>

                        <td class="align-middle">${dbColumn.table}</td>

                        <td class="align-middle">${dbColumn.name}</td>

                        <td class="align-middle">${dbColumn.type}</td>

                        <td class="align-middle">${dbColumn.size}</td>

                    </tr>
                </g:each>
                </tbody>
            </dt:table>

        </div>

        <dt:loadDatatable name="extensionTable" pageLength="50" />

    </body>
</html>