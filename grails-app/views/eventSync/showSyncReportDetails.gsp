<%@ page import="org.philimone.hds.explorer.server.model.logs.LogReport" %>
<%@ page import="org.philimone.hds.explorer.server.model.logs.LogReportFile" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="reportName" value="${message(code: logReportFileInstance?.logReport.description)}" />
		<g:set var="entityName" value="${message(code: 'logReport.label', default: 'LogReport')}" />

		<title><g:message code="default.show.label" args="[entityName]" /></title>

		<dt:defaultResources />
	</head>
	<body>

		<a href="#show-logReport" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>

		<div id="show-logReport" class="content scaffold-show" role="main">
			<h1><g:message code="logreport.show.details.label" args="[reportName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list logReport">
				<g:if test="${logReportFileInstance?.logReport.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="logreport.description.label" default="Description" /></span>
					<span class="property-value" aria-labelledby="description-label"><g:message code="${logReportFileInstance.logReport.description}" /></span>
				</li>
				</g:if>

				<g:if test="${logReportFileInstance?.start}">
				<li class="fieldcontain">
					<span id="start-label" class="property-label"><g:message code="logreport.start.label" default="Start" /></span>
					<span class="property-value" aria-labelledby="start-label"><bi:formatDate date="${logReportFileInstance?.start}" format="yyyy-MM-dd HH:mm:ss" /></span>
				</li>
				</g:if>
			
				<g:if test="${logReportFileInstance?.end}">
				<li class="fieldcontain">
					<span id="end-label" class="property-label"><g:message code="logreport.end.label" default="End" /></span>
					<span class="property-value" aria-labelledby="end-label"><bi:formatDate date="${logReportFileInstance?.end}" format="yyyy-MM-dd HH:mm:ss" /></span>
				</li>
				</g:if>

				<g:if test="${errorLogsCount}">
					<li class="fieldcontain">
						<span id="errorscount-label" class="property-label"><g:message code="logReportFile.errorsCount.label" /></span>
						<span class="property-value" aria-labelledby="description-label">${errorLogsCount}</span>
					</li>
				</g:if>
			</ol>

			<dt:table id="reportsTable">
				<thead>
				<tr>

					<g:sortableColumn property="event" title="${message(code: 'logreport.sync.error.event')}" />

					<g:sortableColumn property="uuid" title="${message(code: 'logreport.sync.error.uuid')}" />

					<g:sortableColumn property="uuid" title="${message(code: 'logreport.sync.error.column')}" />

					<g:sortableColumn property="uuid" title="${message(code: 'logreport.sync.error.code')}" />

					<g:sortableColumn property="uuid" title="${message(code: 'logreport.sync.error.creationDate')}" />

					<g:sortableColumn property="uuid" title="${message(code: 'logreport.sync.error.errorMessage')}" />
				</tr>
				</thead>
				<tbody>
				</tbody>
			</dt:table>
			

			<g:form url="#" method="DELETE">
				<fieldset class="buttons">
					<g:actionSubmit class="delete" value="${message(code:'logreport.update.label', default:'Refresh')}" />
				</fieldset>
			</g:form>
		</div>

		<dt:loadDatatable name="reportsTable" nosort="true" data="${createLink(controller: 'eventSync', action: 'errorLogList', id: logReportFileInstance.id)}" columns="event, uuid, column, code, creationDate, errorMessage" />

	</body>
</html>
