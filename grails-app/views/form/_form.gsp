<%@ page import="org.philimone.hds.explorer.server.model.main.Form" %>



<div class="fieldcontain ${hasErrors(bean: this.form, field: 'formId', 'error')} required">
	<label for="formId">
		<g:message code="form.formId.label" default="Form Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="formId" required="" value="${this.form?.formId}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'formName', 'error')} required">
	<label for="formName">
		<g:message code="form.formName.label" default="Form Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="formName" required="" value="${this.form?.formName}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'formDescription', 'error')} required">
	<label for="formDescription">
		<g:message code="form.formDescription.label" default="Form Description" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="formDescription" required="" value="${this.form?.formDescription}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'gender', 'error')} required">
    <label for="gender">
        <g:message code="extraForm.gender.label" default="Gender" />
        <span class="required-indicator">*</span>
    </label>
    <g:select name="gender" from="['M','F','ALL']" value="${this.form?.gender}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'minAge', 'error')} required">
	<label for="minAge">
		<g:message code="form.minAge.label" default="Min Age" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="minAge" type="number" min="0" max="120" value="${this.form?.minAge}" required=""/>

</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'maxAge', 'error')} required">
	<label for="maxAge">
		<g:message code="form.maxAge.label" default="Max Age" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="maxAge" type="number" min="0" max="120" value="${this.form?.maxAge}" required=""/>

</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'modules', 'error')} ">
    <label for="modules">
        <g:message code="form.modules.label" default="Modules" />

    </label>
    <g:select name="all_modules.id"  multiple="multiple" optionKey="id" from="${org.philimone.hds.explorer.server.model.main.StudyModule.list()}" value="${this.form?.modules}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'isHouseholdForm', 'error')} ">
	<label for="isHouseholdForm">
		<g:message code="form.isHouseholdForm.label" default="Is Household Form" />
	</label>
	<g:checkBox name="isHouseholdForm" value="${this.form?.isHouseholdForm}" />
</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'isHouseholdHeadForm', 'error')} ">
	<label for="isHouseholdHeadForm">
		<g:message code="form.isHouseholdForm.label" default="Is Household Head Form" />
	</label>
	<g:checkBox name="isHouseholdHeadForm" value="${this.form?.isHouseholdHeadForm}" />
</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'isMemberForm', 'error')} ">
	<label for="isMemberForm">
		<g:message code="form.isMemberForm.label" default="Is Member Form" />
	</label>
	<g:checkBox name="isMemberForm" value="${this.form?.isMemberForm}" />
</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'isFollowUpForm', 'error')} ">
	<label for="isFollowUpForm">
		<g:message code="form.isFollowUpForm.label" default="Is a Follow Up Only Form" />
	</label>
	<g:checkBox name="isFollowUpForm" value="${this.form?.isFollowUpForm}" />
</div>

<div class="fieldcontain ${hasErrors(bean: this.form, field: 'enabled', 'error')} ">
	<label for="enabled">
		<g:message code="form.enabled.label" default="Enabled" />
	</label>
	<g:checkBox name="enabled" value="${this.form?.enabled}" />
</div>
