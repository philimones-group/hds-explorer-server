<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'round.label', default: 'Round')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#edit-round" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="round.add.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-round" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>

            <g:if test="${errorMessages}">
                <ul class="errors" role="alert">
                    <g:each in="${errorMessages}" status="i" var="error">
                        <li data-field-id="${error.text}">${error.text}</li>
                    </g:each>
                </ul>
            </g:if>

            <g:form resource="${this.round}" method="PUT">
                <g:hiddenField name="version" value="${this.round?.version}" />
                <fieldset class="form">
                    <g:hiddenField name="roundNumber" value="${this.round?.roundNumber}" />
                    <bi:field bean="${round}" property="roundNumber" label="round.roundNumber.label" mode="show" />
                    <bi:dateField bean="${round}" property="startDate" label="round.startDate.label" />
                    <bi:dateField bean="${round}" property="endDate" label="round.endDate.label" />
                    <f:field bean="round" property="description" />
                </fieldset>
                <fieldset class="buttons">
                    <input class="save" type="submit" value="${message(code: 'round.update.button.label', default: 'Update')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
