
<%@ page import="org.philimone.hds.explorer.server.model.main.MappingFormatType; org.philimone.hds.explorer.server.model.main.Form" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'form.label', default: 'Form')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>

        <asset:javascript src="application.js"/>
        <asset:javascript src="bootstrap.js"/>

	</head>
	<body>
    <g:javascript>

        $(window).on('load', function() {

            var formCollectType = $("#formCollectType").val();

            if (formCollectType == "CALCULATE_EXPRESSION") {
                $("#divCollection").show();
            } else {
                $("#divCollection").hide();
            }

        });

        $(document).ready(function() {


            $("#formCollectType").change(function() {

                var formCollectType = $("#formCollectType").val();

                if (formCollectType == "CALCULATE_EXPRESSION") {
                    $("#divCollection").show();
                } else {
                    $("#divCollection").hide();
                }

            });


        });

    </g:javascript>
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
			<h1><g:message code="formGroupMapping.edit.label" default="Edit Mapping Forms to Group" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
            <g:hasErrors bean="${formGroupMappingInstance}">
                <ul class="errors" role="alert">
                    <g:eachError bean="${formGroupMappingInstance}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                    </g:eachError>
                </ul>
            </g:hasErrors>
            <fieldset class="form">
                <div class="col-sm-8 col-sm-offset-0">

                    <div class="panel panel-primary">
                        <div class="panel-body">

                            <ol class="property-list form">

                                <g:if test="${formInstance?.formId}">
                                <li class="fieldcontain">
                                    <span id="formId-label" class="property-label"><g:message code="form.formId.label" default="Form Id" /></span>
                                    <span class="property-value" aria-labelledby="formId-label">
                                        <g:link action="show" id="${formInstance.id}">${formInstance.formId}</g:link>
                                        <!-- <g:fieldValue bean="${formInstance}" field="formId"/> -->
                                    </span>
                                </li>
                                </g:if>

                                <g:if test="${formInstance?.formName}">
                                <li class="fieldcontain">
                                    <span id="formName-label" class="property-label"><g:message code="form.formName.label" default="Form Name" /></span>
                                    <span class="property-value" aria-labelledby="formName-label"><g:fieldValue bean="${formInstance}" field="formName"/></span>
                                </li>
                                </g:if>

                                <g:if test="${formInstance?.formDescription}">
                                    <li class="fieldcontain">
                                        <span id="formDescription-label" class="property-label"><g:message code="form.formDescription.label" default="Form Description" /></span>
                                        <span class="property-value" aria-labelledby="formDescription-label"><g:fieldValue bean="${formInstance}" field="formDescription"/></span>
                                    </li>
                                </g:if>
                            </ol>
                        </div>
                    </div>

                    <div class="panel panel-primary">
                        <div class="panel-body">

                            <!-- Add new Form Mapping Variable -->
                            <g:form controller="form" action="updateFormGroupMapping" method="post" >
                                <div class="nav2">
                                    <fieldset class="form">
                                        <g:hiddenField name="id" value="${formGroupMappingInstance.id}" required="" class="many-to-one"/>

                                        <g:hiddenField name="groupForm" value="${formInstance.id}" required="" class="many-to-one"/>

                                        <div class="fieldcontain ">
                                            <label class="label2"><g:message code="formGroupMapping.ordinal.label" default="Ordinal" /></label>
                                            <g:textField name="ordinal" value="${formGroupMappingInstance?.ordinal}" readonly="readonly"/>
                                        </div>

                                        <div class="fieldcontain ">
                                            <label class="label2"><g:message code="formGroupMapping.form.label" default="Grouped Form" /></label>
                                            <bi:autoComplete name="form" controller="form" action="formsList" value="${formGroupMappingInstance?.formId}"/>
                                        </div>

                                        <div class="fieldcontain ">
                                            <label class="label2"><g:message code="formGroupMapping.formRequired.label" default="Is Required" /></label>
                                            <g:checkBox name="formRequired" value="${formGroupMappingInstance?.formRequired}" />
                                        </div>

                                        <div class="fieldcontain ">
                                            <label class="label2"><g:message code="formGroupMapping.formCollectType.label" default="Collection Mode" /></label>
                                            <g:select name="formCollectType" from="${org.philimone.hds.explorer.server.model.enums.FormCollectType.values()}" value="${formGroupMappingInstance?.formCollectType}" optionKey="code" valueMessagePrefix="formCollectType"/>
                                        </div>

                                        <div id="divCollection">

                                            <div class="fieldcontain " >
                                                <label class="label2"><g:message code="formGroupMapping.formCollectCondition.label" default="Collect Condition" /></label>
                                                <g:textField name="formCollectCondition" value="${formGroupMappingInstance?.formCollectCondition}" />
                                            </div>

                                            <div class="fieldcontain ">
                                                <label class="label2"><g:message code="formGroupMapping.formCollectLabel.label" default="Collect Condition Label" /></label>
                                                <g:textField name="formCollectLabel" value="${formGroupMappingInstance?.formCollectLabel}" />
                                            </div>

                                        </div>

                                    </fieldset>
                                </div>

                                <br>

                                <div class="nav2">
                                    <fieldset class="buttons2">
                                        <g:submitButton name="create" class="save" value="${message(code: 'formGroupMapping.update.label', default: 'Update Form Group')}" />
                                    </fieldset>
                                </div>
                            </g:form>
                        </div>
                    </div>

                </div>
            </fieldset>
		</div>

	</body>
</html>
