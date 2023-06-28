<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <dt:defaultResources />

    </head>
    <body>
        <a href="#list-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-user" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <br>

            <dt:table id="usersTable">
                <thead>
                <tr>
                    <th><g:message code="user.code.label" default="Code"/></th>

                    <th><g:message code="user.username.label" default="Username"/></th>

                    <th><g:message code="user.fullName.label" default="Full Name"/></th>

                    <th><g:message code="user.email.label" default="Email"/></th>

                    <th><g:message code="user.createdBy.label" default="Created By"/></th>

                    <th><g:message code="user.accountLocked.label" default="Account Locked" /></th>

                    <th><g:message code="user.enabled.label" default="Enabled"/></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${userList}" status="i" var="userInstance">
                    <tr>

                        <td><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "code")}</g:link></td>

                        <td><g:link action="show" id="${userInstance.id}">${fieldValue(bean: userInstance, field: "username")}</g:link></td>

                        <td>${userInstance.getFullname()}</td>

                        <td>${userInstance.email}</td>

                        <td>${fieldValue(bean: userInstance, field: "createdBy")}</td>

                        <td><g:checkBox name="accountLocked" value="${userInstance.accountLocked}" /></td>

                        <td><g:checkBox name="enabled" value="${userInstance.enabled}" /></td>

                    </tr>
                </g:each>
                </tbody>
            </dt:table>

        </div>

        <dt:loadDatatable name="usersTable" />

    </body>
</html>