<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'household.label', default: 'Household')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <dt:defaultResources />
    </head>
    <body>
        <a href="#list-household" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <!-- <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> -->
            </ul>
        </div>
        <div id="list-household" class="content scaffold-list" role="main">
            <h1><g:message code="household.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <div class="whitebox_panel">
                <bi:tableList id="householdTable" class="household" columns="code, name, type, headCode, headName, collectedDate, createdDate" linkAction="show" linkColumn="code" linkId="id" />
            </div>
        </div>

        <dt:loadDatatable name="householdTable" data="${createLink(controller: 'household', action: 'householdList')}" columns="code, name, type, headCode, headName, collectedDate, createdDate" />
    </body>
</html>