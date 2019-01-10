
<%@ page import="org.philimone.hds.explorer.server.model.main.Form" %>
<%@ page import="org.philimone.hds.explorer.server.model.main.StudyModule" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'form.label', default: 'Form')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-form" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="form.new.label" args="[entityName]" /></g:link></li>
				<li><g:link class="list" controller="studyModule" action="index"><g:message code="default.menu.updates.modules.label" args="" /></g:link></li>
				<li><g:link class="list" controller="user" action="index"><g:message code="default.menu.users.label" args="" /></g:link></li>
			</ul>
		</div>
		<div id="list-form" class="content scaffold-list" role="main">
			<h1><g:message code="form.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>

			<g:form controller="form" action="index" method="post" >

				<div class="nav">
					<fieldset class="form">
						<div class="fieldcontain ">
							<fieldset class="buttons">
								<label class="label"><g:message code="studyModule.label" default="Study Module" /></label>
								<g:select id="modules.id" name="modules.id" optionKey="id" from="${StudyModule.list()}" value="${currentModule}" noSelection="['':'All']" />
								<g:submitButton name="create" class="save" value="${message(code: 'form.viewlist')}" />
							</fieldset>
						</div>
					</fieldset>
				</div>


			<br>

			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="formId" title="${message(code: 'form.formId.label', default: 'Form Id')}" />
					
						<g:sortableColumn property="formName" title="${message(code: 'form.formName.label', default: 'Form Name')}" />
					
						<g:sortableColumn property="formDescription" title="${message(code: 'form.formDescription.label', default: 'Form Description')}" />
					
						<g:sortableColumn property="gender" title="${message(code: 'form.gender.label', default: 'Gender')}" />
					
						<g:sortableColumn property="minAge" title="${message(code: 'form.minAge.label', default: 'Min Age')}" />
					
						<g:sortableColumn property="maxAge" title="${message(code: 'form.maxAge.label', default: 'Max Age')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${formList}" status="i" var="formInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${formInstance.id}">${fieldValue(bean: formInstance, field: "formId")}</g:link></td>
					
						<td>${fieldValue(bean: formInstance, field: "formName")}</td>
					
						<td>${fieldValue(bean: formInstance, field: "formDescription")}</td>
					
						<td>${fieldValue(bean: formInstance, field: "gender")}</td>
					
						<td>${fieldValue(bean: formInstance, field: "minAge")}</td>
					
						<td>${fieldValue(bean: formInstance, field: "maxAge")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${formCount}" id="${currentModule}"  />
			</div>

			</g:form>
		</div>
	</body>
</html>