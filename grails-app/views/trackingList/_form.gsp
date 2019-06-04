<%@ page import="org.philimone.hds.explorer.server.model.main.TrackingList" %>



<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'code', 'error')} required">
	<label for="code">
		<g:message code="trackingList.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="code" required="" value="${trackingListInstance?.code}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="trackingList.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" required="" value="${trackingListInstance?.name}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'filename', 'error')} required">
	<label for="filename">
		<g:message code="trackingList.filename.label" default="Filename" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="filename" required="" value="${trackingListInstance?.filename}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'module', 'error')} required">
	<label for="module">
		<g:message code="trackingList.module.label" default="Module" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="module" name="module.id" from="${org.philimone.hds.explorer.server.model.main.StudyModule.list()}" optionKey="id" required="" value="${trackingListInstance?.module?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'enabled', 'error')} ">
	<label for="enabled">
		<g:message code="trackingList.enabled.label" default="Enabled" />
		
	</label>
	<g:checkBox name="enabled" value="${trackingListInstance?.enabled}" />

</div>

<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'mappings', 'error')} ">
	<label for="mappings">
		<g:message code="trackingList.mappings.label" default="Mappings" />
		
	</label>
	
	<ul class="one-to-many">
		<g:each in="${trackingListInstance?.mappings?}" var="m">
			<li><g:link controller="trackingListMapping" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
		</g:each>
		<li class="add">
			<g:link controller="trackingListMapping" action="create" params="['trackingList.id': trackingListInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'trackingListMapping.label', default: 'TrackingListMapping')])}</g:link>
		</li>
	</ul>

</div>

