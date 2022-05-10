<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'region.label', default: 'Region')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>

        <a href="#edit-region" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="region.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="edit-region" class="content scaffold-edit" role="main">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>

            <g:if test="${errorMessages}">
                <ul class="errors" role="alert">
                    <g:each in="${errorMessages}" status="i" var="error">
                        <li data-field-id="${error.field}">${error.text}</li>
                    </g:each>
                </ul>
            </g:if>

            <g:form resource="${this.region}" method="PUT">
                <fieldset class="form">

                    <div class="fieldcontain ${hasErrors(bean: this.region, field: 'code', 'error')} required">
                        <label for="code">
                            <g:message code="region.code.label" default="Region Code" /><span class="required-indicator">*</span>
                        </label>
                        ${this.region.code}
                    </div>

                    <f:field bean="region" property="name" />

                    <div class="fieldcontain ${hasErrors(bean: this.region, field: 'hierarchyLevel', 'error')} required">
                        <label for="hierarchyLevel">
                            <g:message code="region.hierarchyLevel.label" default="Hierarchy Level" /><span class="required-indicator">*</span>
                        </label>

                        ${hierarchyLevel}
                    </div>


                    <div id="divParent">
                        <div class="fieldcontain ${hasErrors(bean: this.region, field: 'parent', 'error')} required">
                            <label id="label_parent" for="parent">
                                <g:message code="region.parent.label" args="" default="Parent region" /><span class="required-indicator">*</span>
                            </label>

                            ${this.region?.parent?.name}

                        </div>
                    </div>

                </fieldset>
                <fieldset class="buttons">
                    <g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                </fieldset>
            </g:form>

        </div>
    </body>
</html>
