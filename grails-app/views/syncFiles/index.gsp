<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<title><g:message code="default.menu.sync.label" /></title>
	</head>
	<body>

		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="import" controller="importOpenHDS" action="index"><g:message code="default.menu.sync.import_openhds.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-user" class="content scaffold-list" role="main">
			<h1><g:message code="default.menu.sync.execute.title" /></h1>
			<br>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>

						<th><g:message code="" default="Id" /></th>
						<th><g:message code="logreport.description.label" default="Process Description" /></th>
						<th><g:message code="logreport.start.label" default="Last Synch Start" /></th>
						<th><g:message code="logreport.end.label" default="Last Synch End" /></th>
						<th><g:message code="logreport.status.label" default="Current Status" /></th>
						<th><g:message code="" default="" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${logReports}" status="i" var="logReport">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

						<td><g:link controller="logReport" action="show" id="${logReport.id}">${fieldValue(bean: logReport, field: "reportId")}</g:link></td>

						<td><g:message code="${logReport.description}" default="${logReport.description}" /></td>

						<td><g:formatDate date="${logReport.start}" format="yyyy-MM-dd HH:mm" /></td>

						<td><g:formatDate date="${logReport.end}" format="yyyy-MM-dd HH:mm" /></td>

						<td><g:message code="${logReport.status}" default="${logReport.status}" /></td>

						<td>
							<g:link class="edit" controller="syncFiles" action="export" id="${logReport.id}">
								<g:message code="default.execute.label" default="Execute" />
							</g:link>
						</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>

			<fieldset class="buttons">

			</fieldset>
		</div>
	</body>
</html>
