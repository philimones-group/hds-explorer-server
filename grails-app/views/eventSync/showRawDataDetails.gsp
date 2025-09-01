<%@ page import="org.philimone.hds.explorer.server.model.logs.LogReport" %>
<%@ page import="org.philimone.hds.explorer.server.model.logs.LogReportFile" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="reportName" value="${message(code: logReportFileInstance?.logReport?.description)}" />
		<g:set var="entityName" value="${message(code: 'logReport.label', default: 'LogReport')}" />

		<title><g:message code="logreport.show.raw.data.label" args="[entityName]" /></title>

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
			<h1><g:message code="logreport.show.raw.data.label" args="[reportName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>


			<div class="whitebox_panel">
				<fieldset class="form-group">
					<div class="fieldcontain required">
						<label for="rawDomain">
							<g:message code="logreport.show.raw.entity.label"/><span class="required-indicator">*</span>
						</label>

						<select id="rawEventType" name="rawEventType" class="form-select" required="" value="${selectedRawEvent}" optionKey="code" optionValue="name" class="many-to-one">
							<g:each in="${org.philimone.hds.explorer.server.model.enums.RawEventType.values()}" var="rawEvent">

								<g:if test="${rawEvent.code?.equals(selectedRawEvent.code)}">
									<option value="${rawEvent.code}" selected><g:message code="${rawEvent.name}" /></option>
								</g:if>
								<g:else>
									<option value="${rawEvent.code}" ><g:message code="${rawEvent.name}" /></option>
								</g:else>
							</g:each>
						</select>

					</div>
				</fieldset>

				<dt:table id="rawDataTable">
					<thead>
					<tr>

						<th data-ordable="true">${message(code: 'logreport.sync.error.event')}</th>

						<th data-ordable="false">${message(code: 'logreport.sync.error.uuid')}</th>

						<th data-ordable="true">${message(code: 'logreport.sync.error.description')}</th>

						<th data-ordable="true">${message(code: 'logreport.sync.error.collectedDate')}</th>

						<th data-ordable="true">${message(code: 'logreport.sync.error.uploadedDate')}</th>

						<th data-ordable="true">${message(code: 'logreport.sync.error.processed')}</th>

						<th data-orderable="false" >${message(code: 'logreport.sync.error.errorMessage')}</th>
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

		</div>

		<dt:loadDatatable name="rawDataTable" data="${createLink(controller: 'eventSync', action: 'rawDomainsList', id: selectedRawEvent.code)}" columns="event, uuid, description, collectedDate, uploadedDate, processed, errorMessage" />

		<g:javascript>
			$(document).ready(function() {

				var table = $.fn.dataTable.isDataTable('#rawDataTable')
						? $('#rawDataTable').DataTable()
						: $('#rawDataTable').data('dt-instance');

				$("#rawEventType").change(function() {
					var code = this.value;
					var url  = "${createLink(controller: 'eventSync', action: 'rawDomainsList')}/"+code;
                    console.log(code+" - "+url);

					// Swap URL and reload data
					table.ajax.url(url).load();
				});

			});
		</g:javascript>

	</body>
</html>
