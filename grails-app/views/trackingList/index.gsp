
<%@ page import="org.philimone.hds.explorer.server.model.main.TrackingList" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'trackingList.label', default: 'TrackingList')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>

		<dt:defaultResources/>
	</head>
	<body>
		<a href="#list-trackingList" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="downloadSampleXLS"><g:message code="trackingList.file.sample.download.label" /></g:link></li>
				<li><g:link class="create" action="downloadTemplateXLS"><g:message code="trackingList.file.template.download.label" /></g:link></li>
			</ul>
		</div>
		<div id="list-trackingList" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<dt:table id="listTable">
				<thead>
					<tr>
					
						<g:sortableColumn property="code" title="${message(code: 'trackingList.code.label', default: 'Code')}" />
					
						<g:sortableColumn property="name" title="${message(code: 'trackingList.name.label', default: 'Name')}" />
					
						<th><g:message code="trackingList.modules.label" default="Module" /></th>

						<g:sortableColumn property="filename" title="${message(code: 'trackingList.filename.label', default: 'Filename')}" />
					
						<g:sortableColumn property="enabled" title="${message(code: 'trackingList.enabled.label', default: 'Enabled')}" />

						<th><g:message code="trackingList.download.xls.label"/></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${trackingListList}" status="i" var="trackingListInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${trackingListInstance.id}">${fieldValue(bean: trackingListInstance, field: "code")}</g:link></td>
					
						<td>${fieldValue(bean: trackingListInstance, field: "name")}</td>
					
						<td>${fieldValue(bean: trackingListInstance, field: "modules")}</td>

						<td>${fieldValue(bean: trackingListInstance, field: "filenameOnly")}</td>
					
						<td><g:formatBoolean boolean="${trackingListInstance.enabled}" /></td>

						<td><g:link class="create" action="get" id="${trackingListInstance.id}"><g:message code="trackingList.download.label" /></g:link></td>
					
					</tr>
				</g:each>
				</tbody>
			</dt:table>

			<dt:loadDatatable name="listTable" />
		</div>
	</body>
</html>
