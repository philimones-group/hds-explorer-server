
<%@ page import="org.philimone.hds.explorer.server.model.logs.LogReport" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="reportName" value="${message(code: logReportInstance?.description)}" />
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
			<h1><g:message code="logreport.show.label" args="[reportName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list logReport">
			
				<g:if test="${logReportInstance?.reportId}">
				<li class="fieldcontain">
					<span id="reportId-label" class="property-label"><g:message code="logreport.reportId.label" default="Report Id" /></span>
					<span class="property-value" aria-labelledby="reportId-label"><g:fieldValue bean="${logReportInstance}" field="reportId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${logReportInstance?.group}">
				<li class="fieldcontain">
					<span id="group-label" class="property-label"><g:message code="logreport.group.label" default="Group" /></span>
					<span class="property-value" aria-labelledby="group-label">
						<b>${logReportInstance?.group?.encodeAsHTML()}</b>
					</span>
					
				</li>
				</g:if>
			
				<g:if test="${logReportInstance?.description}">
				<li class="fieldcontain">
					<span id="description-label" class="property-label"><g:message code="logreport.description.label" default="Description" /></span>
					<span class="property-value" aria-labelledby="description-label"><g:message code="${logReportInstance.description}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${logReportInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="logreport.status.label" default="Status" /></span>
					<span class="property-value" aria-labelledby="status-label">
						<b><g:message code="${logReportInstance?.status}" /></b>
					</span>
					
				</li>
				</g:if>
			
				<g:if test="${logReportInstance?.start}">
				<li class="fieldcontain">
					<span id="start-label" class="property-label"><g:message code="logreport.start.label" default="Start" /></span>
					<span class="property-value" aria-labelledby="start-label"><bi:formatDate date="${logReportInstance?.start}" format="yyyy-MM-dd HH:mm:ss" /></span>
				</li>
				</g:if>
			
				<g:if test="${logReportInstance?.end}">
				<li class="fieldcontain">
					<span id="end-label" class="property-label"><g:message code="logreport.end.label" default="End" /></span>
					<span class="property-value" aria-labelledby="end-label"><bi:formatDate date="${logReportInstance?.end}" format="yyyy-MM-dd HH:mm:ss" /></span>
				</li>
				</g:if>


				<h1><g:message code="logreport.sync.lists.label" /></h1>

				<g:if test="${logReportInstance?.logFiles}">

					<g:set var="keyGroup" value="${ (logReportInstance.keyTimestamp!=null) ? logReportInstance.keyTimestamp : "" }" />

					<table id="reportsTable" class="display nowrap compact cell-border" style="width:100%" cellpadding="0">
						<thead>
						<tr>
							<td></td>

							<g:sortableColumn property="fileName" title="${message(code: 'logReportFile.fileName.label', default: 'File Name')}" />

							<g:sortableColumn property="processedCount" title="${message(code: 'logReportFile.processedCount.label', default: 'Processed Count')}" />

							<g:sortableColumn property="errorsCount" title="${message(code: 'logReportFile.errorsCount.label', default: 'Errors Count')}" />

							<g:sortableColumn property="syncDate" title="${message(code: 'logReportFile.creationDate.label', default: 'Creation Date')}" />

							<g:sortableColumn property="start" title="${message(code: 'logReportFile.start.label', default: 'Process Started')}" />

							<g:sortableColumn property="end" title="${message(code: 'logReportFile.end.label', default: 'Process Ended')}" />

						</tr>
						</thead>
						<tbody>
						<g:each in="${logFiles}" status="i" var="logFileInstance">


							<g:if test="${logFileInstance.keyTimestamp != keyGroup}">
								<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
									<td><b>Synched on: ${logFileInstance.start}</b></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
									<td></td>
								</tr>
							</g:if>


							<g:set var="keyGroup" value="${ logFileInstance.keyTimestamp }" />

							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

								<td><g:link action="showSyncReportDetails" id="${logFileInstance.id}"><g:message code="logreport.sync.show.error.label" /> </g:link></td>

								<td><g:link action="downloadLogFile" id="${logFileInstance.id}">${fieldValue(bean: logFileInstance, field: "fileName")}</g:link></td>

								<td>${fieldValue(bean: logFileInstance, field: "processedCount")}</td>

								<td>${fieldValue(bean: logFileInstance, field: "errorsCount")}</td>

								<td><bi:formatDate date="${logFileInstance.creationDate}" format="yyyy-MM-dd HH:mm:ss" /></td>

								<td><bi:formatDate date="${logFileInstance.start}" format="yyyy-MM-dd HH:mm:ss" /></td>

								<td><bi:formatDate date="${logFileInstance.end}" format="yyyy-MM-dd HH:mm:ss" /></td>

							</tr>

						</g:each>
						</tbody>
					</table>

				</g:if>
			
			</ol>
			<g:form url="#" method="DELETE">
				<fieldset class="buttons">
					<g:actionSubmit class="delete" value="Refresh" />
				</fieldset>
			</g:form>
		</div>

		<dt:loadDatatable name="reportsTable" nosort="true" />

	</body>
</html>
