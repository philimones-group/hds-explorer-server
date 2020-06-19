<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="user.changePassword.title.label" args="[entityName]" /></title>
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
            <h1><g:message code="user.changePassword.title.label" args="[entityName]" /></h1>
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


            <g:form resource="${this.user}" action="updatePassword" method="PUT">
                <g:hiddenField name="version" value="${this.user?.version}" />
                <fieldset class="form">

                    <g:if test="${this.user?.getFullname()}">
                        <div class="fieldcontain">
                            <label for="fullname" class="property-label">
                                <g:message code="user.fullName.label" />
                            </label>
                            <span class="property-value" aria-labelledby="firstName-label">
                                <g:fieldValue bean="${this.user}" field="fullname" />
                            </span>
                        </div>
                    </g:if>

                    <g:if test="${this.user?.username}">
                        <div class="fieldcontain">
                            <label for="username" class="property-label">
                                <g:message code="user.username.label" default="Username" />
                            </label>
                            <span class="property-value" aria-labelledby="username-label">
                                <g:fieldValue bean="${this.user}" field="username" />
                            </span>
                        </div>
                    </g:if>

                    <g:if test="${this.user?.email}">
                        <div class="fieldcontain">
                            <label for="email" class="property-label">
                                <g:message code="user.email.label" default="Email" />
                            </label>
                            <span class="property-value" aria-labelledby="email-label">
                                <g:fieldValue bean="${this.user}" field="email" />
                            </span>
                        </div>
                    </g:if>

                    <div class="fieldcontain ${hasErrors(bean: this.user, field: 'password', 'error')} required">
                        <label for="password" class="property-label">
                            <g:message code="user.newPassword.label" /><span class="required-indicator">*</span>
                        </label>
                        <span class="property-value" aria-labelledby="password-label">
                            <g:passwordField name="newPassword" required="" value=""/>
                        </span>
                    </div>

                    <div class="fieldcontain ${hasErrors(bean: this.user, field: 'password', 'error')} required">
                        <label for="password" class="property-label">
                            <g:message code="user.confirmPassword.label" /><span class="required-indicator">*</span>
                        </label>
                        <span class="property-value" aria-labelledby="password-label">
                            <g:passwordField name="confirmPassword" required="" value=""/>
                        </span>
                    </div>

                </fieldset>
                <fieldset class="buttons">
                    <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
