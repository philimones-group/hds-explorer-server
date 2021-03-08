
<%@ page import="org.philimone.hds.explorer.server.model.main.DataSet" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'dataSet.label', default: 'DataSet')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-dataSet" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-dataSet" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list dataSet">
			
				<g:if test="${dataSetInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="dataSet.name.label" default="Name" /></span>
					
						<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${dataSetInstance}" field="name"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.keyColumn}">
				<li class="fieldcontain">
					<span id="keyColumn-label" class="property-label"><g:message code="dataSet.keyColumn.label" default="Key Column" /></span>
					
						<span class="property-value" aria-labelledby="keyColumn-label"><g:fieldValue bean="${dataSetInstance}" field="keyColumn"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.tableName}">
				<li class="fieldcontain">
					<span id="tableName-label" class="property-label"><g:message code="dataSet.tableName.label" default="Table Name" /></span>
					
						<span class="property-value" aria-labelledby="tableName-label"><g:fieldValue bean="${dataSetInstance}" field="tableName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.tableColumn}">
				<li class="fieldcontain">
					<span id="tableColumn-label" class="property-label"><g:message code="dataSet.tableColumn.label" default="Table Column" /></span>
					
						<span class="property-value" aria-labelledby="tableColumn-label"><g:fieldValue bean="${dataSetInstance}" field="tableColumn"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.enabled}">
				<li class="fieldcontain">
					<span id="enabled-label" class="property-label"><g:message code="dataSet.enabled.label" default="Enabled" /></span>
					
						<span class="property-value" aria-labelledby="enabled-label"><g:formatBoolean boolean="${dataSetInstance?.enabled}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.createdBy}">
				<li class="fieldcontain">
					<span id="createdBy-label" class="property-label"><g:message code="dataSet.createdBy.label" default="Created By" /></span>
					
						<span class="property-value" aria-labelledby="createdBy-label"><g:link controller="user" action="show" id="${dataSetInstance?.createdBy?.id}">${dataSetInstance?.createdBy?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.createdDate}">
				<li class="fieldcontain">
					<span id="createdDate-label" class="property-label"><g:message code="dataSet.createdDate.label" default="Created Date" /></span>
					
						<span class="property-value" aria-labelledby="createdDate-label"><bi:formatDate date="${dataSetInstance?.createdDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.updatedBy}">
				<li class="fieldcontain">
					<span id="updatedBy-label" class="property-label"><g:message code="dataSet.updatedBy.label" default="Updated By" /></span>
					
						<span class="property-value" aria-labelledby="updatedBy-label"><g:link controller="user" action="show" id="${dataSetInstance?.updatedBy?.id}">${dataSetInstance?.updatedBy?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.updatedDate}">
				<li class="fieldcontain">
					<span id="updatedDate-label" class="property-label"><g:message code="dataSet.updatedDate.label" default="Updated Date" /></span>
					
						<span class="property-value" aria-labelledby="updatedDate-label"><bi:formatDate date="${dataSetInstance?.updatedDate}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.filename}">
				<li class="fieldcontain">
					<span id="filename-label" class="property-label"><g:message code="dataSet.filename.label" default="Filename" /></span>
					
						<span class="property-value" aria-labelledby="filename-label"><g:fieldValue bean="${dataSetInstance}" field="filenameOnly"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${dataSetInstance?.mappingLabels}">
				<li class="fieldcontain">
					<span id="mappingLabels-label" class="property-label"><g:message code="dataSet.mappingLabels.label" default="Mapping Labels" /></span>
					
						<g:each in="${dataSetInstance.mappingLabels}" var="m">
						<span class="property-value" aria-labelledby="mappingLabels-label"><g:link controller="dataSetLabel" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:dataSetInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${dataSetInstance}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
