<%@ page import="org.philimone.hds.explorer.server.model.main.DataSet" %>


<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'name', 'error')} ">
	<label for="name"><g:message code="dataSet.name.label" default="Name" /></label>
	<g:textField name="name" value="${dataSetInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'keyColumn', 'error')} required">
	<label for="keyColumn"><g:message code="dataSet.keyColumn.label" default="Key Column" /><span class="required-indicator">*</span></label>
	<g:textField name="keyColumn" required="" value="${dataSetInstance?.keyColumn}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'tableName', 'error')} required">
	<label for="tableName"><g:message code="dataSet.tableName.label" default="Table Name" /><span class="required-indicator">*</span></label>
	<g:textField name="tableName" required="" value="${dataSetInstance?.tableName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'tableColumn', 'error')} required">
	<label for="tableColumn"><g:message code="dataSet.tableColumn.label" default="Table Column" /><span class="required-indicator">*</span></label>
	<g:textField name="tableColumn" required="" value="${dataSetInstance?.tableColumn}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'enabled', 'error')} ">
	<label for="enabled"><g:message code="dataSet.enabled.label" default="Enabled" /></label>
	<g:checkBox name="enabled" value="${dataSetInstance?.enabled}" />
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'filename', 'error')} required">
	<label for="filename"><g:message code="dataSet.filename.label" default="Filename" /><span class="required-indicator">*</span></label>
	<g:textField name="filename" required="" value="${dataSetInstance?.filename}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'mappingLabels', 'error')} ">
	<label for="mappingLabels"><g:message code="dataSet.mappingLabels.label" default="Mapping Labels" /></label>
	
	<ul class="one-to-many">
		<g:each in="${dataSetInstance?.mappingLabels?}" var="m">
			<li><g:link controller="dataSetLabel" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
		</g:each>
		<li class="add">
			<g:link controller="dataSetLabel" action="create" params="['dataSet.id': dataSetInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'dataSetLabel.label', default: 'DataSetLabel')])}</g:link>
		</li>
	</ul>
</div>

