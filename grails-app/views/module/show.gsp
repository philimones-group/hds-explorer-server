<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'module.label', default: 'Module')}" />
        <title><g:message code="module.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-module" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="module.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="module.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="add"><g:message code="module.updates.label" /></g:link></li>
            </ul>
        </div>
        <div id="show-module" class="content scaffold-show" role="main">
            <h1><g:message code="module.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>

            <f:display bean="module" order="code, name, description, createdBy, createdDate, updatedBy, updatedDate" />

            <g:form resource="${this.module}" method="DELETE">
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${this.module}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                    <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
