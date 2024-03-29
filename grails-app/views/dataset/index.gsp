
<%@ page import="org.philimone.hds.explorer.server.model.main.Dataset" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'dataset.label', default: 'Dataset')}" />
		<title><g:message code="dataset.list.label" args="[entityName]" /></title>

		<dt:defaultResources />
	</head>
	<body>
		<a href="#list-dataset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="dataset.create.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-dataset" class="content scaffold-list" role="main">
			<h1><g:message code="dataset.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>

			<dt:table id="listTable">
				<thead>
				<tr>

					<g:sortableColumn property="name" title="${message(code: 'dataset.name.label', default: 'Name')}" />

					<g:sortableColumn property="label" title="${message(code: 'dataset.label.label', default: 'Label')}" />

					<g:sortableColumn property="keyColumn" title="${message(code: 'dataset.keyColumn.label', default: 'Key Column')}" />

					<g:sortableColumn property="tableName" title="${message(code: 'dataset.tableName.link.label', default: 'Table')}" />

					<th><g:message code="dataset.filename.label" default="Filename" /></th>

					<g:sortableColumn property="enabled" title="${message(code: 'dataset.enabled.label', default: 'Enabled')}" />

					<th>${message(code: "dataset.createdDate.label", default:"Created Date")}</th>

				</tr>
				</thead>
				<tbody>
				<g:each in="${datasetList}" status="i" var="dataSetInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

						<td><g:link action="show" id="${dataSetInstance.id}">${fieldValue(bean: dataSetInstance, field: "name")}</g:link></td>

						<td>${fieldValue(bean: dataSetInstance, field: "label")}</td>

						<td>${fieldValue(bean: dataSetInstance, field: "keyColumn")}</td>

						<td>${fieldValue(bean: dataSetInstance, field: "tableName")}</td>

						<td>${dataSetInstance.getFilenameOnly()}</td>

						<td><g:formatBoolean boolean="${dataSetInstance.enabled}" /></td>

						<td><bi:formatDate date="${dataSetInstance.createdDate}" /></td>

					</tr>
				</g:each>
				</tbody>
			</dt:table>

			<dt:loadDatatable name="listTable" />
		</div>
	</body>
</html>
