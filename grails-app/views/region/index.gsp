<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'region.label', default: 'Region')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <dt:defaultResources />
    </head>
    <body>
        <a href="#list-region" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="region.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-region" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <dt:table id="regionTable">
                <thead>
                <tr>
                    <th><g:message code="region.code.label" /></th>

                    <th><g:message code="region.name.label" /></th>

                    <th><g:message code="region.hierarchyLevel.label" /></th>

                    <th><g:message code="region.hierarchyName.label" /></th>

                    <th><g:message code="region.parent.label"/></th>

                    <th><g:message code="region.createdBy.label" /></th>

                    <th><g:message code="region.createdDate.label" /></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${regionList}" status="i" var="regionInstance">
                    <tr>

                        <td><g:link action="show" id="${regionInstance.id}">${fieldValue(bean: regionInstance, field: "code")}</g:link></td>

                        <td><g:link action="show" id="${regionInstance.id}">${fieldValue(bean: regionInstance, field: "name")}</g:link></td>

                        <td><g:message code="${regionInstance.hierarchyLevel.name}" /></td>

                        <td>${hierarchyLevelsMap.get(regionInstance.hierarchyLevel.code)}</td>

                        <td>${regionInstance.parent?.name}</td>

                        <td>${fieldValue(bean: regionInstance, field: "createdBy")}</td>

                        <td>${regionInstance.createdDate}</td>

                    </tr>
                </g:each>
                </tbody>
            </dt:table>
        </div>

    <dt:loadDatatable name="regionTable" />
    
    </body>
</html>