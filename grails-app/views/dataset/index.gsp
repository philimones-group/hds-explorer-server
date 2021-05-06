
<%@ page import="org.philimone.hds.explorer.server.model.main.Dataset" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'dataSet.label', default: 'Dataset')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-dataSet" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-dataSet" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
			<thead>
					<tr>
					
						<g:sortableColumn property="name" title="${message(code: 'dataSet.name.label', default: 'Name')}" />
					
						<g:sortableColumn property="keyColumn" title="${message(code: 'dataSet.keyColumn.label', default: 'Key Column')}" />
					
						<g:sortableColumn property="tableName" title="${message(code: 'dataSet.tableName.label', default: 'Table Name')}" />
					
						<g:sortableColumn property="tableColumn" title="${message(code: 'dataSet.tableColumn.label', default: 'Table Column')}" />
					
						<g:sortableColumn property="enabled" title="${message(code: 'dataSet.enabled.label', default: 'Enabled')}" />
					
						<th><g:message code="dataSet.createdBy.label" default="Created By" /></th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${dataSetList}" status="i" var="dataSetInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${dataSetInstance.id}">${fieldValue(bean: dataSetInstance, field: "name")}</g:link></td>
					
						<td>${fieldValue(bean: dataSetInstance, field: "keyColumn")}</td>
					
						<td>${fieldValue(bean: dataSetInstance, field: "tableName")}</td>
					
						<td>${fieldValue(bean: dataSetInstance, field: "tableColumn")}</td>
					
						<td><g:formatBoolean boolean="${dataSetInstance.enabled}" /></td>
					
						<td>${fieldValue(bean: dataSetInstance, field: "createdBy")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${dataSetInstanceCount ?: 0}" />
			</div>
		</div>
	</body>
</html>
