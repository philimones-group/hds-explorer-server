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

            <g:if test="${errorMessages.size()>0}">
                <ul class="errors" role="alert">
                    <g:each in="${errorMessages}" status="i" var="errorMessage" >
                        <li data-field-id="systemLanguage}">${errorMessage}</li>
                    </g:each>
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

                    <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                        <label for="systemInputCalendar" title="${message(code: 'settings.parameters.calendar.description.label')}">
                            <g:message code="settings.parameters.calendar.label" />
                            <span class="required-indicator">*</span>
                        </label>

                        <select id="systemInputCalendar" name="systemInputCalendar" required="" value="${selectedCalendar}" optionKey="value" optionValue="name" class="many-to-one">
                            <g:each in="${calendars}" var="cal">
                                <g:if test="${cal.value?.equals(selectedCalendar.value)}">
                                    <option value="${cal.value}" selected><g:message code="${cal.name}" /> </option>
                                </g:if>
                                <g:else>
                                    <option value="${cal.value}" ><g:message code="${cal.name}" /></option>
                                </g:else>
                            </g:each>
                        </select>

                    </div>

                    <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                        <label for="codeGenerator" title="${message(code: 'settings.parameters.codegenerator.description.label')}">
                            <g:message code="settings.parameters.codegenerator.label" />
                            <span class="required-indicator">*</span>
                        </label>

                        <g:select name="codeGenerator" required="" value="${selectedCodeGenerator}" from="${codeGenerators}" optionKey="value" optionValue="name" class="many-to-one"/>

                    </div>

                    <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                        <label for="codeGeneratorIncRule" title="${message(code: 'settings.parameters.codegenerator.incremental.rule.label')}">
                            <g:message code="settings.parameters.codegenerator.incremental.rule.label" />
                            <span class="required-indicator">*</span>
                        </label>

                        <select id="codeGeneratorIncRule" name="codeGeneratorIncRule" required="" value="${selectedCodeGeneratorIncRule}" class="many-to-one">
                            <g:each in="${codeGeneratorsRules}" var="rule">
                                <g:if test="${rule.value?.equals(selectedCodeGeneratorIncRule)}">
                                    <option value="${rule.value}" selected><g:message code="${rule.name}" /> </option>
                                </g:if>
                                <g:else>
                                    <option value="${rule.value}" ><g:message code="${rule.name}" /></option>
                                </g:else>

                            </g:each>
                        </select>

                    </div>

                    <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                        <label for="regionHeadSupport" title="${message(code: 'settings.parameters.region.head.support.label')}">
                            <g:message code="settings.parameters.region.head.support.label" />
                            <span class="required-indicator">*</span>
                        </label>

                        <g:checkBox name="regionHeadSupport" value="${selectedRegionHeadSupport}" />

                    </div>

                    <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                        <label for="gpsRequired" title="${message(code: 'settings.parameters.visit.gps.required.label')}">
                            <g:message code="settings.parameters.visit.gps.required.label" />
                            <span class="required-indicator">*</span>
                        </label>

                        <g:checkBox name="gpsRequired" value="${selectedGpsRequired}" />

                    </div>

                </fieldset>
                <fieldset class="buttons">
                    <g:submitButton name="create" class="save" value="${message(code: 'settings.parameters.update.label')}" />
                </fieldset>
            </g:form>
        </div>
    </body>
</html>
