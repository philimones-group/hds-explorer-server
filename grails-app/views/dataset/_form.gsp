<%@ page import="org.philimone.hds.explorer.server.model.main.Dataset" %>


<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'name', 'error')} ">
	<label for="name"><g:message code="dataset.name.label" default="Name" /></label>
	<g:textField name="name" value="${dataSetInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'label', 'error')} ">
	<label for="label"><g:message code="dataset.label.label" default="Label" /></label>
	<g:textField name="label" value="${dataSetInstance?.label}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'keyColumn', 'error')} required">
	<label for="keyColumn"><g:message code="dataset.keyColumn.label" default="Key Column" /><span class="required-indicator">*</span></label>
	<g:textField name="keyColumn" required="" value="${dataSetInstance?.keyColumn}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'tableName', 'error')} required">
	<label for="tableName"><g:message code="dataset.tableName.label" default="Table Name" /><span class="required-indicator">*</span></label>
	<g:textField name="tableName" required="" value="${dataSetInstance?.tableName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'tableColumn', 'error')} required">
	<label for="tableColumn"><g:message code="dataset.tableColumn.label" default="Table Column" /><span class="required-indicator">*</span></label>
	<g:textField name="tableColumn" required="" value="${dataSetInstance?.tableColumn}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'modules', 'error')} required">
	<label for="module"><g:message code="dataset.modules.label" default="Modules" /><span class="required-indicator">*</span></label>
	<g:select id="module" name="modules.id"  multiple="multiple" from="${org.philimone.hds.explorer.server.model.main.Module.list()}" optionKey="id" required="" value="${modules}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'enabled', 'error')} ">
	<label for="enabled"><g:message code="dataset.enabled.label" default="Enabled" /></label>
	<g:checkBox name="enabled" value="${dataSetInstance?.enabled}" />
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'filename', 'error')} required">
	<label for="filename"><g:message code="dataset.filename.label" default="Filename" /><span class="required-indicator">*</span></label>
	<g:textField name="filename" required="" value="${dataSetInstance?.filename}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'mappingLabels', 'error')} ">
	<label for="mappingLabels"><g:message code="dataset.mappingLabels.label" default="Mapping Labels" /></label>
	
	<ul class="one-to-many">
		<g:each in="${dataSetInstance?.mappingLabels?}" var="m">
			<li><g:link controller="dataSetLabel" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
		</g:each>
		<li class="add">
			<g:link controller="dataSetLabel" action="create" params="['dataset.id': dataSetInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'datasetLabel.label', default: 'DatasetLabel')])}</g:link>
		</li>
	</ul>
</div>

