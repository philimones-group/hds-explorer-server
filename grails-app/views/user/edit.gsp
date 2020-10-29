<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#edit-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-user" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.user}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.user}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form resource="${this.user}" method="PUT">
                <g:hiddenField name="version" value="${this.user?.version}" />
                <fieldset class="form">

                    <f:field bean="user" property="firstName" />
                    <f:field bean="user" property="lastName" />
                    <f:field bean="user" property="username" />
                    <f:field bean="user" property="email"/>

                    <f:field bean="user" property="modules" input-domainList="${org.philimone.hds.explorer.server.model.main.StudyModule.list()}">
                        <g:select name="modules"  multiple="multiple" optionKey="id" from="${org.philimone.hds.explorer.server.model.main.StudyModule.list()}" value="${this.user?.modules}"/>
                    </f:field>

                    <f:field bean="user" property="accountLocked" />
                    <f:field bean="user" property="accountExpired" />
                    <f:field bean="user" property="passwordExpired" />
                    <f:field bean="user" property="enabled" />

                    <div class="fieldcontain ${hasErrors(bean: this.user, field: 'authorities', 'error')} ">
                        <label for="roles">
                            <g:message code="user.authorities.label" />
                        </label>
                        <g:select name="roles.id" id="roles.id" multiple="multiple" optionKey="id" optionValue="${{message(code: it.name)}}" from="${org.philimone.hds.explorer.server.model.authentication.Role.list()}" value="${userRoles}" />
                    </div>

                </fieldset>
                <fieldset class="buttons">
                    <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                    <g:link class="edit" action="changePassword" resource="${this.user}"><g:message code="user.changePassword.label" /></g:link>
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
