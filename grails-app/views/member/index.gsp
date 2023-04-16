<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'member.label', default: 'Member')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <dt:defaultResources />
    </head>
    <body>
        <a href="#list-member" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <!-- <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li> -->
            </ul>
        </div>
        <div id="list-member" class="content scaffold-list" role="main">
            <h1><g:message code="member.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <bi:tableList id="memberTable" class="member" collection="${memberList}" columns="code, name, gender, dob, householdCode, collectedDate, createdDate" messageColumns="gender"/>
            
            <div class="pagination">
                <g:paginate total="${memberCount ?: 0}" />
            </div>

            <dt:loadDatatable name="memberTable" />
        </div>
    </body>
</html>