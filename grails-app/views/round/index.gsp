<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'round.label', default: 'Round')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <dt:defaultResources />
    </head>
    <body>
        <a href="#list-round" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="round.add.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-round" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <bi:tableList id="roundsTable" class="round" collection="${roundList}" columns="roundNumber, startDate, endDate, description, createdBy, createdDate, updatedBy, updatedDate"
                          linkAction="show" linkColumn="roundNumber" linkId="id"/>

            <div class="pagination">
                <g:paginate total="${roundCount ?: 0}" />
            </div>
        </div>

        <dt:loadDatatable name="roundsTable" />
    </body>
</html>