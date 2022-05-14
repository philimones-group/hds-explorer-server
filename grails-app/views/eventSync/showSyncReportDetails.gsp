<%@ page import="org.philimone.hds.explorer.server.model.logs.LogReport" %>
<%@ page import="org.philimone.hds.explorer.server.model.logs.LogReportFile" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="reportName" value="${message(code: logReportFileInstance?.logReport.description)}" />
		<g:set var="entityName" value="${message(code: 'logReport.label', default: 'LogReport')}" />

		<title><g:message code="default.show.label" args="[entityName]" /></title>

		<style>
		.message_collapsed {
			background-color: #777;
			color: white;
			cursor: pointer;
			padding: 18px;
			width: 100%;
			border: none;
			text-align: left;
			outline: none;
			font-size: 15px;
		}

		.active, .message_collapsed:hover {
			background-color: #555;
		}

		.message_content {
			padding: 0 18px;
			display: block;
			overflow: hidden;
			background-color: #f1f1f1;
		}
		</style>

		<dt:defaultResources />

	</head>
	<body>
	<g:javascript>
		var coll = document.getElementsByClassName("message_colapsed");
		var i;

		for (i = 0; i < coll.length; i++) {
			coll[i].addEventListener("click", function() {
				this.classList.toggle("active");
				var content = this.nextElementSibling;
				if (content.style.display === "block") {
					content.style.display = "none";
				} else {
					content.style.display = "block";
				}
			});
		}
	</g:javascript>

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


				<g:if test="${errorLogs}">

					<table id="reportsTable" class="display nowrap compact cell-border" style="width:100%" cellpadding="0">
						<thead>
						<tr>

							<td><g:message code="logreport.sync.error.event" /> </td>

							<td><g:message code="logreport.sync.error.uuid" /> </td>

							<td><g:message code="logreport.sync.error.column" /> </td>

							<td><g:message code="logreport.sync.error.code" /> </td>

							<td><g:message code="logreport.sync.error.creationDate" /> </td>

							<td><g:message code="logreport.sync.error.errorMessage" /> </td>

						</tr>
						</thead>
						<tbody>
						<g:each in="${errorLogs}" status="i" var="errorLogInstance">

							<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

								<td><g:link controller="rawDomain" action="editDomain" id="${errorLogInstance.uuid}"><g:message code="${errorLogInstance.entity.name}" /> </g:link></td>

								<td>${errorLogInstance.uuid}</td>

								<td>${errorLogInstance.columnName}</td>

								<td>${errorLogInstance.code}</td>

								<td><bi:formatDate date="${errorLogInstance.createdDate}" format="yyyy-MM-dd HH:mm:ss" /></td>

								<td style="word-wrap: break-word;">
									${errorLogInstance.messageText}
								</td>

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
