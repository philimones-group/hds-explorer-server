<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'form.label', default: 'Form')}" />
		<title><g:message code="form.create.label" args="[entityName]" /></title>

		<asset:javascript src="application.js"/>
		<asset:javascript src="bootstrap.js"/>
	</head>
	<body>
		<a href="#create-form" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="form.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="list" controller="module" action="index"><g:message code="default.menu.updates.modules.label" args="" /></g:link></li>
                <li><g:link class="list" controller="user" action="index"><g:message code="default.menu.users.label" args="" /></g:link></li>
			</ul>
		</div>
		<div id="create-form" class="content scaffold-create" role="main">
			<h1><g:message code="form.add.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${this.form}">
			<ul class="errors" role="alert">
				<g:eachError bean="${this.form}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form resource="${this.form}" method="POST"  >
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'form.create.button.label', default: 'Create')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
