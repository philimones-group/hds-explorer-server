<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'module.label', default: 'Module')}" />
        <title><g:message code="module.list.label" args="[entityName]" /></title>

        <dt:defaultResources />
    </head>
    <body>
        <a href="#list-module" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="module.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="add"><g:message code="module.updates.label" /></g:link></li>
            </ul>
        </div>
        <div id="list-module" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <div class="whitebox_panel">
                <bi:tableList id="modulesTable" class="module" collection="${moduleList}" columns="code, name, description, createdBy, createdDate, updatedBy, updatedDate" />
            </div>

        </div>

        <dt:loadDatatable name="modulesTable" />
    </body>
</html>