<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'module.label', default: 'Module')}" />
        <title><g:message code="module.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#create-module" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="module.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="add"><g:message code="module.updates.label" /></g:link></li>
            </ul>
        </div>
        <div id="create-module" class="content scaffold-create" role="main">
            <h1><g:message code="module.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${this.module}">
            <ul class="errors" role="alert">
                <g:eachError bean="${this.module}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
            </g:hasErrors>
            <g:form resource="${this.module}" method="POST">
                <fieldset class="form">
                    <g:hiddenField name="code" value="${this.module?.code}" />
                    <div class="fieldcontain ${hasErrors(bean: this.module, field: 'code', 'error')} ">
                        <label for="code">
                            <g:message code="module.code.label" default="Code" />
                            <span class="required-indicator">*</span>
                        </label>
                        <f:display bean="module" property="code" displayStyle="table" />
                    </div>

                    <f:field bean="module" property="name" />
                    <f:field bean="module" property="description" />
                </fieldset>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
