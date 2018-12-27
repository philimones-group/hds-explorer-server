
<%@ page import="org.philimone.hds.explorer.server.model.main.MappingFormatType; org.philimone.hds.explorer.server.model.main.Form" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'form.label', default: 'Form')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>

        <asset:javascript src="application.js"/>
	</head>
	<body>
    <g:javascript>

        $(document).ready(function() {
            $("#tableName").change(function() {

                $.ajax({
                    url: "${createLink(controller: "form", action: "modelVariables")}",
                    data: "name=" + this.value,
                    cache: false,
                    success: function(html) {
                        $("#columnName").html(html);
                    }
                });
            });

            $("#columnFormat").change(function() {

                $.ajax({
                    url: "${createLink(controller: "form", action: "modelFormatTypes")}",
                    data: "id=" + this.value,
                    cache: false,
                    success: function(html) {

                        var element = document.getElementById("columnFormatValue");
                        element.text = html;
                        element.value = html;

                    }
                });
            });


        });

    </g:javascript>
		<a href="#show-form" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="form.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="form.new.label" args="[entityName]" /></g:link></li>
                <li><g:link class="list" controller="studyModule" action="index"><g:message code="default.menu.updates.modules.label" args="" /></g:link></li>
                <li><g:link class="list" controller="user" action="index"><g:message code="default.menu.users.label" args="" /></g:link></li>
            </ul>
		</div>
		<div id="show-form" class="content scaffold-show" role="main">
			<h1><g:message code="formMapping.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
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
			</ol>

        <!-- Add new Form Mapping Variable -->
            <g:form controller="form" action="saveFormMapping" method="post" >
                <div class="nav2">
                    <fieldset class="form">
                        <g:hiddenField id="form" name="form.id" from="${[formInstance]}" optionKey="id" required="" value="${formMappingInstance?.form?.id}" class="many-to-one"/>
                        <div class="fieldcontain ">
                            <label class="label2"><g:message code="formMapping.formVariableName.label" default="Form Variable Name" /></label>
                            <g:textField name="formVariableName" value="${formMappingInstance?.formVariableName}"/>
                        </div>

                        <div class="fieldcontain ">
                            <label class="label2"><g:message code="formMapping.tableName.label" default="Table Name" /></label>
                            <g:select id="tableName" name="tableName" from="${tableList}"  class="many-to-one"  noSelection="['': '']" />
                        </div>

                        <div class="fieldcontain ">
                            <label class="label2"><g:message code="formMapping.columnName.label" default="Column Name" /></label>
                            <g:select id="columnName" name="columnName" from="" value="${formMappingInstance?.columnName}" />
                        </div>

                        <div class="fieldcontain ">
                            <label class="label2"><g:message code="formMapping.columnFormat.label" default="Column Format Type" /></label>
                            <g:select id="columnFormat" name="columnFormat" optionKey="id" from="${org.philimone.hds.explorer.server.model.main.MappingFormatType.list()}" value="${formMappingInstance?.columnFormat}" noSelection="['': '']" />
                        </div>

                        <div class="fieldcontain ">
                            <label class="label2"><g:message code="formMapping.columnFormatValue.label" default="Column Format Value" /></label>
                            <g:textField id="columnFormatValue" name="columnFormatValue" from="${formMappingInstance?.columnFormatValue}"  />
                        </div>

                        <!--
                        <div id="divColumnConstValue" class="fieldcontain " style="display: none">
                            <label class="label2"><g:message code="formMapping.columnConstantValue.label" default="Column Constant Value" /></label>
                            <g:textField name="columnConstantValue" value="" />
                        </div>
                        -->

                        <br>

                        <div class="fieldcontain ">
                            <label class="label2"><g:message code="formMapping.copyFrom.label" default="Copy from From Form:" /></label>
                            <g:select id="formToCopy" name="formToCopy" from="${formsList}" optionKey="id" />
                        </div>
                    </fieldset>
                </div>

                <div class="nav2">
                    <fieldset class="buttons2">
                        <g:submitButton name="create" class="save" value="Add New Variable" />
                        <g:actionSubmit name="create" class="save" value="Copy variables from " action="copyFrom" />
                    </fieldset>
                </div>
            </g:form>

            <br>

            <div class="nav2">
                <table>
                    <thead>
                    <tr>

                        <th><g:message code="formMapping.form.label" default="Form" /></th>

                        <g:sortableColumn property="formVariableName" title="${message(code: 'formMapping.formVariableName.label', default: 'Form Variable Name')}" />

                        <g:sortableColumn property="tableName" title="${message(code: 'formMapping.tableName.label', default: 'Table Name')}" />

                        <g:sortableColumn property="columnName" title="${message(code: 'formMapping.columnName.label', default: 'Column Name')}" />

                        <g:sortableColumn property="columnName" title="${message(code: 'formMapping.columnFormat.label', default: 'Column Format')}" />


                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${formMappingList}" status="i" var="formMappingInstance">
                        <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                            <td><g:link controller="formMapping" action="show" id="${formMappingInstance.id}">${fieldValue(bean: formMappingInstance, field: "form")}</g:link></td>

                            <td>${fieldValue(bean: formMappingInstance, field: "formVariableName")}</td>

                            <td>${fieldValue(bean: formMappingInstance, field: "tableName")}</td>

                            <td>${fieldValue(bean: formMappingInstance, field: "columnName")}</td>

                            <td>${fieldValue(bean: formMappingInstance, field: "columnFormat")}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>

		</div>
	</body>
</html>
