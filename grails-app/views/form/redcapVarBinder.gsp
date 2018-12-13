
<%@ page import="net.manhica.dss.explorer.DssExtraForm" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'extraForm.label', default: 'ExtraForm')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-extraForm" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="extraForm.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="extraForm.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="list" controller="dssExplorer" action="index"><g:message code="default.menu.dss.explorer.label" args="" /></g:link></li>
                <li><g:link class="list" controller="dssStudyModule" action="index"><g:message code="default.menu.dss.explorer.studyModule.label" args="" /></g:link></li>
                <li><g:link class="list" controller="dssEUser" action="index"><g:message code="default.menu.dss.explorer.euser.label" args="" /></g:link></li>
            </ul>
		</div>
		<div id="show-extraForm" class="content scaffold-show" role="main">
			<h1><g:message code="dssRedcapMap.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list extraForm">
			
				<g:if test="${extraFormInstance?.formId}">
				<li class="fieldcontain">
					<span id="formId-label" class="property-label"><g:message code="extraForm.formId.label" default="Form Id" /></span>
					<span class="property-value" aria-labelledby="formId-label">
                        <g:link action="show" id="${extraFormInstance.id}">${extraFormInstance.formId}</g:link>
                        <!-- <g:fieldValue bean="${extraFormInstance}" field="formId"/> -->
                    </span>
				</li>
				</g:if>
			
				<g:if test="${extraFormInstance?.formName}">
				<li class="fieldcontain">
					<span id="formName-label" class="property-label"><g:message code="extraForm.formName.label" default="Form Name" /></span>
				    <span class="property-value" aria-labelledby="formName-label"><g:fieldValue bean="${extraFormInstance}" field="formName"/></span>
				</li>
				</g:if>
			</ol>

            <h2><g:message code="dssRedcapApi.label" args="[entityName]" /></h2>
            <!-- Set REDCap API Variable -->
            <g:hasErrors bean="${dssRedcapApiInstance}">
                <ul class="errors" role="alert">
                    <g:eachError bean="${dssRedcapApiInstance}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>
            <g:form controller="dssExtraForm" action="saveRedcapApi" method="post" >
                <div class="nav4">
                    <fieldset class="form">
                        <g:hiddenField id="id" name="id" value="${dssRedcapApiInstance?.id}"/>
                        <g:hiddenField id="form" name="form.id" value="${dssRedcapApiInstance?.form?.id}"/>

                        <div class="fieldcontain4 ${hasErrors(bean: dssRedcapApiInstance, field: 'url', 'error')} required">
                            <label for="url"><g:message code="dssRedcapApi.url.label" default="Url" /><span class="required-indicator">*</span></label>
                            <g:textField name="url" required="" value="${dssRedcapApiInstance?.url}"/>
                        </div>

                        <div class="fieldcontain4 ${hasErrors(bean: dssRedcapApiInstance, field: 'token', 'error')} required">
                            <label for="token"><g:message code="dssRedcapApi.token.label" default="Token" /><span class="required-indicator">*</span></label>
                            <g:textField name="token" required="" value="${dssRedcapApiInstance?.token}"/>
                        </div>

                        <div class="fieldcontain4 ${hasErrors(bean: dssRedcapApiInstance, field: 'token', 'error')} required">
                            <label></label>
                            <fieldset class="buttons3">
                                <g:submitButton name="create" class="save" value="Update API Parameters"/>
                            </fieldset>
                        </div>

                    </fieldset>

                </div>
            </g:form>

            <h2><g:message code="dssRedcapMap.add.label" args="[entityName]" /></h2>
            <!-- Add new REDCap Map Variable -->
            <g:hasErrors bean="${dssRedcapMapInstance}">
                <ul class="errors" role="alert">
                    <g:eachError bean="${dssRedcapMapInstance}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>
            <g:form controller="dssExtraForm" action="saveRedcapMap" method="post" >
                <div class="nav3">
                    <g:hiddenField id="form" name="form.id" value="${formBindMapInstance?.form?.id}"/>
                    <table cellspacing="0" cellpadding="0">
                        <td>
                            <div class="fieldcontain2 ${hasErrors(bean: dssRedcapMapInstance, field: 'odkColumnName', 'error')} required">
                                <label for="odkColumnName"><g:message code="dssRedcapMap.odkColumnName.label" default="Odk Column Name"/><span class="required-indicator">*</span></label>
                                <g:textField name="odkColumnName" required="" value="${dssRedcapMapInstance?.odkColumnName}"/>
                            </div>
                        </td>
                        <td>
                            <div class="fieldcontain2 ${hasErrors(bean: dssRedcapMapInstance, field: 'rcpColumnName', 'error')} required">
                                <label for="rcpColumnName"><g:message code="dssRedcapMap.rcpColumnName.label" default="Rcp Column Name"/><span class="required-indicator">*</span></label>
                                <g:textField name="rcpColumnName" required="" value="${dssRedcapMapInstance?.rcpColumnName}"/>
                            </div>
                        </td>
                        <td>
                            <div class="fieldcontain2 ${hasErrors(bean: dssRedcapMapInstance, field: 'rcpColumnFormat', 'error')} ">
                                <label for="rcpColumnFormat"><g:message code="dssRedcapMap.rcpColumnFormat.label" default="Rcp Column Format"/></label>
                                <g:select from="${formatList}" name="rcpColumnFormat" value="${dssRedcapMapInstance?.rcpColumnFormat}" noSelection="['':'']"/>
                            </div>
                        </td>
                        <td>
                            <fieldset class="buttons2">
                                <g:submitButton name="create" class="save" value="Add New Variable"/>
                            </fieldset>
                        </td>
                    </table>
                </div>
            </g:form>

            <br>

            <div class="nav2">
                <table>
                    <thead>
                    <tr>

                        <th><g:message code="dssRedcapMap.form.label" default="Form" /></th>

                        <g:sortableColumn property="formVariableName" title="${message(code: 'dssRedcapMap.odkColumnName.label', default: 'ODK Column')}" />

                        <g:sortableColumn property="tableName" title="${message(code: 'dssRedcapMap.rcpColumnName.label', default: 'REDCap Column')}" />

                        <g:sortableColumn property="columnName" title="${message(code: 'dssRedcapMap.rcpColumnFormat.label', default: 'Format')}" />

                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${dssRedcapMapInstanceList}" status="i" var="dssRedcapMapInstance">
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                            <td><g:link controller="dssRedcapMap" action="show" id="${dssRedcapMapInstance.id}">${fieldValue(bean: dssRedcapMapInstance, field: "form")}</g:link></td>

                            <td>${fieldValue(bean: dssRedcapMapInstance, field: "odkColumnName")}</td>

                            <td>${fieldValue(bean: dssRedcapMapInstance, field: "rcpColumnName")}</td>

                            <td>${fieldValue(bean: dssRedcapMapInstance, field: "rcpColumnFormat")}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>

		</div>
	</body>
</html>
