<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'parameters.label', default: 'Module')}" />
        <title><g:message code="settings.parameters.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-parameters" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
            </ul>
        </div>
        <div id="create-parameters" class="content scaffold-create" role="main">
            <h1><g:message code="settings.parameters.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:if test="${errorMessage}">
                <ul class="errors" role="alert">
                    <li data-field-id="systemLanguage}">${errorMessage}</li>
                </ul>
            </g:if>
            <g:form controller="settings" action="updateParameters" method="POST">
                <fieldset class="form">

                    <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                        <label for="systemLanguage" title="${message(code: 'settings.parameters.language.description.label')}">
                            <g:message code="settings.parameters.language.label" />
                            <span class="required-indicator">*</span>
                        </label>

                        <g:select name="systemLanguage" required="" value="${selectedLanguage}" from="${languages}" optionKey="language" optionValue="displayLanguage" class="many-to-one"/>

                    </div>

                </fieldset>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'settings.parameters.update.label')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
