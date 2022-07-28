<%@ page import="org.philimone.hds.explorer.server.model.main.Form" %>

<g:javascript>



            $(window).load(
				function () {
					//alert("testing jquery");

					var subjectType = $("#formSubjectType").val();
					var formType = $("#formType").val();

					//Subject Type checks
					if (subjectType=="REGION") {
						$("#divRegion").show();
					} else {
						$("#divRegion").hide();
					}


					if (subjectType=="MEMBER" || subjectType=="HOUSEHOLD_HEAD") {
						$("#divMemberFilters").show();
					} else {
						$("#divMemberFilters").hide();
					}


					//Form Types Checks
					if (formType=="FORM_GROUP") {
					    $("#formId").attr('readonly', true);

					    //call ajax to generate code
						$.ajax({
							url: "${createLink(controller: "form", action: "generateFormGroupIdGsp")}",
							data: "formName=" + this.value,
							cache: false,
							success: function(html) {
								$("#formId").val(html);

							}
                    	});

					} else {
						$("#formId").attr('readonly', false);
					}


					/*
					var regchk = $("#isRegionForm").prop('checked');
					var hhdchk = $("#isHouseholdHeadForm").prop('checked');
					var memchk = $("#isMemberForm").prop('checked') || $("#isHouseholdHeadForm").prop('checked');

					regchk ? $("#divRegion").show() : $("#divRegion").hide();
					memchk ? $("#divMemberFilters").show() : $("#divMemberFilters").hide();
					if (hhdchk) { $("#divRegion").hide(); $("#divMemberFilters").hide(); }
					*/
				}

			);

            $(document).ready(function() {

                $("#formType").change(function() {
					var formType = $("#formType").val();

					//alert(formType);

					if (formType==="FORM_GROUP") {
					    $("#formId").attr('readonly', true);

					    //call ajax to generate code
						$.ajax({
							url: "${createLink(controller: "form", action: "generateFormGroupIdGsp")}",
							data: "formName=" + this.value,
							cache: false,
							success: function(html) {
								$("#formId").val(html);

							}
                    	});

					} else {
						$("#formId").attr('readonly', false);
					}
				});

				$("#formSubjectType").change(function() {
					var subjectType = $("#formSubjectType").val();

					if (subjectType=="REGION") {
						$("#divRegion").show();
					} else {
						$("#divRegion").hide();
					}


					if (subjectType=="MEMBER" || subjectType=="HOUSEHOLD_HEAD") {
						$("#divMemberFilters").show();
					} else {
						$("#divMemberFilters").hide();
					}
				});
            });


</g:javascript>


<div class="col-sm-8 col-sm-offset-0">

	<div class="panel panel-primary">
		<div class="panel-body">
			<!-- form type -->
			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'formType', 'error')} required">
				<label for="formType">
					<g:message code="form.formType.label" default="Form Type" />
					<span class="required-indicator">*</span>
				</label>

				<g:select name="formType" required="" value="${this.form?.formType}" from="${org.philimone.hds.explorer.server.model.enums.FormType.values()}" optionKey="code" valueMessagePrefix="formType" class="many-to-one" />
			</div>
		</div>
	</div>

	<div class="panel panel-primary">
		<div class="panel-body">

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

		</div>
	</div>


	<div class="panel panel-default">
		<div class="panel-body">

			<!-- form subject type -->
			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'formSubjectType', 'error')} required">
				<label for="formType">
					<g:message code="form.formSubjectType.label" default="Form Subject (linked to)" />
					<span class="required-indicator">*</span>
				</label>

				<g:select name="formSubjectType" required="" value="${this.form?.formSubjectType}" from="${org.philimone.hds.explorer.server.model.enums.FormSubjectType.values()}" optionKey="code" valueMessagePrefix="formSubjectType" class="many-to-one" />

			</div>


			<div id="divRegion">
				<div class="fieldcontain ${hasErrors(bean: this.form, field: 'regionLevel', 'error')} required">
					<label for="regionLevel">
						<g:message code="form.regionLevel.label" default="regionLevel" />
						<span class="required-indicator">*</span>
					</label>
					<g:select name="regionLevel" optionKey="level" optionValue="name" from="${regionLevels}" value="${this.form?.regionLevel}"/>
				</div>
			</div>

			<!-- NOT IN USE
			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'isRegionForm', 'error')} ">
				<label for="isRegionForm">
					<g:message code="form.isRegionForm.label" default="Is Region Form" />
				</label>
				<g:checkBox name="isRegionForm" value="${this.form?.isRegionForm}" />
			</div>

			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'isHouseholdForm', 'error')} ">
				<label for="isHouseholdForm">
					<g:message code="form.isHouseholdForm.label" default="Is Household Form" />
				</label>
				<g:checkBox name="isHouseholdForm" value="${this.form?.isHouseholdForm}" />
			</div>

			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'isHouseholdHeadForm', 'error')} ">
				<label for="isHouseholdHeadForm">
					<g:message code="form.isHouseholdHeadForm.label" default="Is Household Head Form" />
				</label>
				<g:checkBox name="isHouseholdHeadForm" value="${this.form?.isHouseholdHeadForm}" />
			</div>

			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'isMemberForm', 'error')} ">
				<label for="isMemberForm">
					<g:message code="form.isMemberForm.label" default="Is Member Form" />
				</label>
				<g:checkBox name="isMemberForm" value="${this.form?.isMemberForm}" />
			</div>
			-->
		</div>
	</div>

	<div id="divMemberFilters">
		<div class="panel panel-default">
			<div class="panel-body">

				<div class="fieldcontain ${hasErrors(bean: this.form, field: 'gender', 'error')} required">
					<label for="gender">
						<g:message code="form.gender.label" default="Gender" />
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

			</div>
		</div>
	</div>

	<div class="panel panel-default">
		<div class="panel-body">
			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'isFollowUpForm', 'error')} ">
				<label for="isFollowUpForm">
					<g:message code="form.isFollowUpForm.label" default="Is a Follow Up Only Form" />
				</label>
				<g:checkBox name="isFollowUpForm" value="${this.form?.isFollowUpForm}" />
			</div>

			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'multiCollPerSession', 'error')} ">
				<label for="multiCollPerSession">
					<g:message code="form.multiCollPerSession.label" default="Multi Coll Per Session" />
				</label>
				<g:checkBox name="multiCollPerSession" value="${this.form?.multiCollPerSession}" />
			</div>

		</div>
	</div>

	<div class="panel panel-default">
		<div class="panel-body">

			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'modules', 'error')} ">
				<label for="modules">
					<g:message code="form.modules.label" default="Modules" />

				</label>
				<g:select name="all_modules.id" multiple="multiple" optionKey="id" from="${org.philimone.hds.explorer.server.model.main.Module.list()}" value="${this.modules}"/>
			</div>

			<div class="fieldcontain ${hasErrors(bean: this.form, field: 'enabled', 'error')} ">
				<label for="enabled">
					<g:message code="form.enabled.label" default="Enabled" />
				</label>
				<g:checkBox name="enabled" value="${this.form?.enabled}" />
			</div>
		</div>
	</div>

</div>


