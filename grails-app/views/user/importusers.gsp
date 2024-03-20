<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="user.import.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-user" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="create-user" class="content scaffold-create" role="main">
            <h1><g:message code="user.import.label" args="[entityName]" /></h1>
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

            <g:if test="${errorMessages}">
                <div class="rerrors_div">
                    <ul class="rerrors" role="alert">
                        <g:each in="${errorMessages}" status="i" var="error">
                            <li data-field-id="${error.text}">${error.text}</li>
                        </g:each>
                    </ul>
                </div>
                <br>
            </g:if>

            <g:uploadForm controller="user" action="uploadUsersFile" >
                <fieldset class="form">
                    <div class="fieldcontain " >
                        <label for="filename"><g:message code="user.file.label" /></label>
                        <input type="file" id="fileUpload" name="fileUpload" style="display:inline;" />
                        <g:submitButton name="create" class="button_link" value="${message(code: 'user.file.upload.label')}" />
                    </div>
                </fieldset>
            </g:uploadForm>

            <g:form resource="${this.user}" action="saveUsers" method="POST">
                <fieldset class="form">

                    <div class="fieldcontain">
                        <label for="filename"><g:message code="user.file.uploaded.label" default="Filename" /><span class="required-indicator">*</span></label>
                        <b>${uploadedFilename}</b>
                        <g:hiddenField name="absoluteFilename" value="${absoluteFilename}" />
                    </div>

                    <bi:field bean="${userInstance}" property="accountLocked" label="user.accountLocked.label" />
                    <bi:field bean="${userInstance}" property="enabled" label="user.enabled.label" />

                    <div class="fieldcontain ">
                        <label for="roles">
                            <g:message code="user.modules.label" />
                        </label>
                        <g:select name="modules" multiple="multiple" optionKey="id" from="${org.philimone.hds.explorer.server.model.main.Module.list()}" />
                    </div>

                    <div class="fieldcontain ">
                        <label for="roles">
                            <g:message code="user.authorities.label" />
                        </label>
                        <g:select name="roles.id" id="roles.id" multiple="multiple" optionKey="id" optionValue="${{message(code: it.name)}}" from="${org.philimone.hds.explorer.server.model.authentication.Role.list()}" value="${userRoles}" />
                    </div>

                </fieldset>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'user.import.label', default: 'Create')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
