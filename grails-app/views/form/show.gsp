
<%@ page import="org.philimone.hds.explorer.server.model.main.Form" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'form.label', default: 'Form')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-form" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="form.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="form.new.label" args="[entityName]" /></g:link></li>
				<li><g:link class="list" controller="module" action="index"><g:message code="default.menu.updates.modules.label" args="" /></g:link></li>
				<li><g:link class="list" controller="user" action="index"><g:message code="default.menu.users.label" args="" /></g:link></li>
			</ul>
		</div>
		<div id="show-form" class="content scaffold-show" role="main">
			<h1><g:message code="form.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list form">
			
				<g:if test="${this.form?.formId}">
				<li class="fieldcontain">
					<span id="formId-label" class="property-label"><g:message code="form.formId.label" default="Form Id" /></span>
					
					<span class="property-value" aria-labelledby="formId-label"><g:fieldValue bean="${this.form}" field="formId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${this.form?.formName}">
				<li class="fieldcontain">
					<span id="formName-label" class="property-label"><g:message code="form.formName.label" default="Form Name" /></span>
					
						<span class="property-value" aria-labelledby="formName-label"><g:fieldValue bean="${this.form}" field="formName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${this.form?.formDescription}">
				<li class="fieldcontain">
					<span id="formDescription-label" class="property-label"><g:message code="form.formDescription.label" default="Form Description" /></span>
					
						<span class="property-value" aria-labelledby="formDescription-label"><g:fieldValue bean="${this.form}" field="formDescription"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${this.form?.gender}">
				<li class="fieldcontain">
					<span id="gender-label" class="property-label"><g:message code="form.gender.label" default="Gender" /></span>
					
						<span class="property-value" aria-labelledby="gender-label"><g:fieldValue bean="${this.form}" field="gender"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${this.form?.minAge}">
				<li class="fieldcontain">
					<span id="minAge-label" class="property-label"><g:message code="form.minAge.label" default="Min Age" /></span>
					
						<span class="property-value" aria-labelledby="minAge-label"><g:fieldValue bean="${this.form}" field="minAge"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${this.form?.maxAge}">
				<li class="fieldcontain">
					<span id="maxAge-label" class="property-label"><g:message code="form.maxAge.label" default="Max Age" /></span>
					
						<span class="property-value" aria-labelledby="maxAge-label"><g:fieldValue bean="${this.form}" field="maxAge"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${this.form?.modules}">
				<li class="fieldcontain">
					<span id="modules-label" class="property-label"><g:message code="form.modules.label" default="Modules" /></span>
					
					<span class="property-value" aria-labelledby="modules-label"><g:fieldValue bean="${this.form}" field="modules"/></span>
					
				</li>
				</g:if>

				<li class="fieldcontain">
					<span id="isRegionForm-label" class="property-label"><g:message code="form.isRegionForm.label" default="Is Region Form" /></span>
					<span class="property-value" aria-labelledby="isRegionForm-label"><g:formatBoolean boolean="${this.form?.isRegionForm}" /></span>
				</li>

				<li class="fieldcontain">
					<span id="regionLevel-label" class="property-label"><g:message code="form.regionLevel.label" default="Region Level" /></span>
					<span class="property-value" aria-labelledby="regionLevel-label">${formService.getRegionLevelName(this.form.regionLevel)}</span>
				</li>

				<li class="fieldcontain">
					<span id="isHouseholdForm-label" class="property-label"><g:message code="form.isHouseholdForm.label" default="Is Household Form" /></span>
					<span class="property-value" aria-labelledby="isHouseholdForm-label"><g:formatBoolean boolean="${this.form?.isHouseholdForm}" /></span>
				</li>

				<li class="fieldcontain">
					<span id="isHouseholdHeadForm-label" class="property-label"><g:message code="form.isHouseholdHeadForm.label" default="Is Household Head Form" /></span>
					<span class="property-value" aria-labelledby="isHouseholdHeadForm-label"><g:formatBoolean boolean="${this.form?.isHouseholdHeadForm}" /></span>
				</li>

				<li class="fieldcontain">
					<span id="isMemberForm-label" class="property-label"><g:message code="form.isMemberForm.label" default="Is Member Form" /></span>
					<span class="property-value" aria-labelledby="isMemberForm-label"><g:formatBoolean boolean="${this.form?.isMemberForm}" /></span>
				</li>

				<li class="fieldcontain">
					<span id="isFollowUpForm-label" class="property-label"><g:message code="form.isFollowUpForm.label" default="Is Follow-Up Only Form" /></span>
					<span class="property-value" aria-labelledby="isFollowUpForm-label"><g:formatBoolean boolean="${this.form?.isFollowUpForm}" /></span>
				</li>


				<li class="fieldcontain">
					<span id="enabled-label" class="property-label"><g:message code="form.enabled.label" default="Enabled" /></span>
					<span class="property-value" aria-labelledby="enabled-label"><g:formatBoolean boolean="${this.form?.enabled}" /></span>
				</li>

			
			</ol>
			<g:form resource="${this.form}" method="DELETE" >
				<fieldset class="buttons">
					<g:link class="edit" action="edit" resource="${this.form}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'form.button.delete.confirm.message', default: 'Are you sure?')}');" />
					<g:link class="save" action="formMapping" resource="${this.form}"><g:message code="formMapping.label" default="Form Mapping" /></g:link>
					<g:link class="save" action="redcapVarBinder" resource="${this.form}"><g:message code="redcapMapping.label" default="REDCap Mapping" /></g:link>
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
