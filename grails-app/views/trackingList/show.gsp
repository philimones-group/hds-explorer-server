
<%@ page import="org.philimone.hds.explorer.server.model.main.TrackingList" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'trackingList.label', default: 'TrackingList')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-trackingList" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="downloadSampleXLS"><g:message code="trackingList.file.sample.download.label" /></g:link></li>
				<li><g:link class="create" action="downloadTemplateXLS"><g:message code="trackingList.file.template.download.label" /></g:link></li>
			</ul>
		</div>
		<div id="show-trackingList" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list trackingList">
			
				<g:if test="${trackingListInstance?.code}">
				<li class="fieldcontain">
					<span id="code-label" class="property-label"><g:message code="trackingList.code.label" default="Code" /></span>
					<span class="property-value" aria-labelledby="code-label"><g:fieldValue bean="${trackingListInstance}" field="code"/></span>
				</li>
				</g:if>
			
				<g:if test="${trackingListInstance?.name}">
				<li class="fieldcontain">
					<span id="name-label" class="property-label"><g:message code="trackingList.name.label" default="Name" /></span>
					<span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${trackingListInstance}" field="name"/></span>
				</li>
				</g:if>
			
				<g:if test="${trackingListInstance?.filename}">
				<li class="fieldcontain">
					<span id="filename-label" class="property-label"><g:message code="trackingList.filename.label" default="Filename" /></span>
					<span class="property-value" aria-labelledby="filename-label"><g:fieldValue bean="${trackingListInstance}" field="filenameOnly"/></span>
				</li>
				</g:if>
			
				<g:if test="${trackingListInstance?.modules}">
				<li class="fieldcontain">
					<span id="module-label" class="property-label"><g:message code="trackingList.modules.label" default="Modules" /></span>

					<span class="property-value" aria-labelledby="modules-label">
						<ul>
							<g:each in="${trackingListInstance.modules}">
								<li class="list-style-type: square;">${it}</li>
							</g:each>
						</ul>
					</span>
				</li>
				</g:if>
				<g:if test="${trackingListInstance?.enabled}">
				<li class="fieldcontain">
					<span id="enabled-label" class="property-label"><g:message code="trackingList.enabled.label" default="Enabled" /></span>
					<span class="property-value" aria-labelledby="enabled-label"><g:formatBoolean boolean="${trackingListInstance?.enabled}" /></span>
				</li>
				</g:if>
			
				<g:if test="${trackingListInstance?.createdBy}">
				<li class="fieldcontain">
					<span id="createdBy-label" class="property-label"><g:message code="trackingList.createdBy.label" default="Created By" /></span>
					<span class="property-value" aria-labelledby="createdBy-label"><g:link controller="user" action="show" id="${trackingListInstance?.createdBy?.id}">${trackingListInstance?.createdBy?.encodeAsHTML()}</g:link></span>
				</li>
				</g:if>
			
				<g:if test="${trackingListInstance?.createdDate}">
				<li class="fieldcontain">
					<span id="createdDate-label" class="property-label"><g:message code="trackingList.createdDate.label" default="Created Date" /></span>
					<span class="property-value" aria-labelledby="createdDate-label"><bi:formatDate date="${trackingListInstance?.createdDate}" /></span>
				</li>
				</g:if>
			
				<g:if test="${trackingListInstance?.updatedBy}">
				<li class="fieldcontain">
					<span id="updatedBy-label" class="property-label"><g:message code="trackingList.updatedBy.label" default="Updated By" /></span>
					<span class="property-value" aria-labelledby="updatedBy-label"><g:link controller="user" action="show" id="${trackingListInstance?.updatedBy?.id}">${trackingListInstance?.updatedBy?.encodeAsHTML()}</g:link></span>
				</li>
				</g:if>
			
				<g:if test="${trackingListInstance?.updatedDate}">
				<li class="fieldcontain">
					<span id="updatedDate-label" class="property-label"><g:message code="trackingList.updatedDate.label" default="Updated Date" /></span>
					<span class="property-value" aria-labelledby="updatedDate-label"><bi:formatDate date="${trackingListInstance?.updatedDate}" /></span>
				</li>
				</g:if>
			
			</ol>
			<g:form url="[resource:trackingListInstance, action:'delete']" method="DELETE">
				<fieldset class="buttons">
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					<g:link class="save" action="get" id="${trackingListInstance.id}"><g:message code="trackingList.download.xls.label" /></g:link>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
