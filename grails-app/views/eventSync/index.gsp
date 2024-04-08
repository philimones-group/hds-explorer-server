<!DOCTYPE html>
<%@ page import="org.philimone.hds.explorer.server.model.enums.settings.LogReportCode" %>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="default.menu.sync.label" /></title>

		<dt:defaultResources/>
	</head>
	<body>

		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
				<g:if test="${advanced==false}">
					<li><g:link class="create" action="advancedindex"><g:message code="default.menu.sync.syncdss.advanced.label" /></g:link></li>
				</g:if>
			</ul>
		</div>
		<div id="list-user" class="content scaffold-list" role="main">
			<h1><g:message code="logreport.sync.syncdss" /></h1>

			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>

			<dt:table id="logTable">
				<thead>
					<tr>
						<th><g:message code="" default="Id" /></th>
						<th><g:message code="logreport.description.label" default="Process Description" /></th>
						<th><g:message code="logreport.start.label" default="Last Synch Start" /></th>
						<th><g:message code="logreport.end.label" default="Last Synch End" /></th>
						<th><g:message code="logreport.status.label" default="Current Status" /></th>
						<g:if test="${advanced==true}">
							<th><g:message code="logreport.execute.limit.label" default="Execution Limit" /></th>
						</g:if>
						<th><g:message code="" default="" /></th>
					</tr>
				</thead>
				<tbody>
				<g:each in="${logReports}" status="i" var="logReport">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

						<td class="align-middle"><g:link controller="eventSync" action="showSyncReport" id="${logReport.id}">${fieldValue(bean: logReport, field: "reportId")}</g:link></td>

						<td class="align-middle" style="text-align: left"><g:message code="${logReport.description}" default="${logReport.description}" /></td>

						<td class="align-middle"><bi:formatDate date="${logReport.start}" format="yyyy-MM-dd HH:mm:ss" /></td>

						<td class="align-middle"><bi:formatDate date="${logReport.end}" format="yyyy-MM-dd HH:mm:ss" /></td>

						<td class="align-middle"><g:message code="${logReport.status}" default="${logReport.status}" /></td>

						<g:form controller="eventSync" method="POST" >
							<g:set var="isExecuteEvent" value="${logReport.reportId==LogReportCode.REPORT_SYNC_MANAGER_EXECUTE_ALL_EVENTS || logReport.reportId==LogReportCode.REPORT_SYNC_MANAGER_EXECUTE_EVENTS}" />

							<g:hiddenField name="id" value="${logReport.id}" />
							<g:if test="${advanced==true}">
								<td class="align-middle">
									<g:if test="${isExecuteEvent}">
										<g:textField name="executionLimit" value="0" />
									</g:if>
								</td>
							</g:if>
							<td class="align-middle">
								<g:actionSubmit class="btn btn-primary" value="${g.message(code: 'default.execute.label')}" action="execute" name="submitButton" />
							</td>

						</g:form>
					</tr>
				</g:each>
				</tbody>
			</dt:table>

			<!-- Status of Data Transfer -->

			<div id="list-user" class="content scaffold-list" role="main">
				<h1><g:message code="syncdss.sync.status.label" /></h1>

				<dt:table id="statusTable">
					<thead>
					<tr>
						<th colspan="1" scope="colgroup"></th>
						<th colspan="1" scope="colgroup" style="text-align: center; vertical-align: middle;"><g:message code="syncdss.sync.status.main.label" /></th>
						<th colspan="4" scope="colgroup" style="text-align: center; vertical-align: middle;"><g:message code="syncdss.sync.status.lastexecution.label" /></th>
					</tr>
					<tr>
						<th style="text-align: center; vertical-align: middle;"><g:message code="syncdss.sync.status.table.label" /></th>
						<th style="text-align: center; vertical-align: middle;"><g:message code="syncdss.sync.status.total.records.label" /></th>
						<th style="text-align: center; vertical-align: middle;"><g:message code="syncdss.sync.status.total.processed.label" /></th>
						<th style="text-align: center; vertical-align: middle;"><g:message code="syncdss.sync.status.total.processed_with_error.label" /></th>
						<th style="text-align: center; vertical-align: middle;"><g:message code="syncdss.sync.status.total.not_processed.label" /></th>
						<th style="text-align: center; vertical-align: middle;"><g:message code="syncdss.sync.status.total.invalidated.label" /></th>

					</tr>
					</thead>
					<tbody>
					<g:each in="${syncProcesses}" status="i" var="syncProcess">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

							<td style="text-align: center; vertical-align: middle;"><g:message code="${syncProcess.name}" default="Execute" /></td>

							<td style="text-align: center; vertical-align: middle;">${syncProcess.format(syncProcess.totalRecords)}</td>

							<td style="text-align: center; vertical-align: middle;">${syncProcess.format(syncProcess.processed)}</td>

							<td style="text-align: center; vertical-align: middle;">${syncProcess.format(syncProcess.processedWithError)}</td>

							<td style="text-align: center; vertical-align: middle;">${syncProcess.format(syncProcess.notProcessed)}</td>

							<td style="text-align: center; vertical-align: middle;">-</td>

						</tr>
					</g:each>
					</tbody>
				</dt:table>

			<fieldset class="buttons">

			</fieldset>

			<dt:loadDatatable name="logTable" nodetails="true"/>

			<dt:loadDatatable name="statusTable" pageLength="20" nodetails="true" nosort="true" />

		</div>
	</body>
</html>
