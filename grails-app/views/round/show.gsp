<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'round.label', default: 'Round')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-round" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="round.add.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="show-round" class="content scaffold-show" role="main">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>

            <bi:field bean="${round}" property="roundNumber"  label="round.roundNumber.label" mode="show" />
            <bi:field bean="${round}" property="startDate" label="round.startDate.label" mode="show" />
            <bi:field bean="${round}" property="endDate" label="round.endDate.label" mode="show" />
            <bi:field bean="${round}" property="description" label="round.description.label" mode="show" />
            <bi:field bean="${round}" property="createdBy" label="round.createdBy.label" mode="show" />
            <bi:field bean="${round}" property="createdDate" label="round.createdDate.label" mode="show" />
            <bi:field bean="${round}" property="updatedBy" label="round.updatedBy.label" mode="show" />
            <bi:field bean="${round}" property="updatedDate" label="round.updatedDate.label" mode="show" />

            <g:form resource="${this.round}" method="DELETE">
                <fieldset class="buttons">
                    <g:link class="edit" action="edit" resource="${this.round}"><g:message code="round.edit.button.label" default="Edit" /></g:link>
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
