<%@ page import="org.philimone.hds.explorer.server.model.main.Dataset" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'dataSet.label', default: 'Dataset')}" />
		<title><g:message code="dataset.edit.label" args="[entityName]" /></title>
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
                        $("#tableColumn").html(html);
                    }
                });
            });

        });

		$(window).on('load',
			function () {
				//alert("testing jquery");

				var id = $("#tableName").val();
				if (id != undefined){
					$.ajax({
                    	url: "${createLink(controller: "form", action: "modelVariables")}",
                    	data: "name=" + id,
                    	cache: false,
                    	success: function(html) {
                        	$("#tableColumn").html(html);
                    	}
                	});
				}

			}
		);

	</g:javascript>

		<a href="#edit-dataSet" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="dataset.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="dataset.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="dataset.create.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="edit-dataSet" class="content scaffold-edit" role="main">
			<h1><g:message code="dataset.edit.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${datasetInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${datasetInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>

			<g:uploadForm controller="dataset" action="changeUploadFile" >
				<fieldset class="form">
					<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'filename', 'error')} " >
						<g:hiddenField name="datasetId" value="${datasetInstance?.id}" />
						<label for="filename"><g:message code="dataset.file.label" default="Dataset Filename" /></label>
						<input type="file" id="fileUpload" name="fileUpload" style="display:inline;" />
						<g:submitButton name="create" class="button_link" value="${message(code: 'dataset.file.change.upload.label')}" />
					</div>
				</fieldset>
			</g:uploadForm>

			<g:form url="[resource:datasetInstance, action:'update']" method="PUT" >
				<g:hiddenField name="version" value="${datasetInstance?.version}" />
				<fieldset class="form">
					<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'filename', 'error')} required">
						<label for="filename"><g:message code="dataset.filename.label" default="Filename" /><span class="required-indicator">*</span></label>
						<b>${datasetInstance?.getFilenameOnly()}</b>
						<g:hiddenField name="filename" value="${datasetInstance?.filename}" />
					</div>

					<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'name', 'error')} ">
						<label for="name"><g:message code="dataset.name.label" default="Name" /></label>
						<g:textField name="name" required="" value="${datasetInstance?.name}" />
					</div>

					<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'label', 'error')} ">
						<label for="name"><g:message code="dataset.label.label" default="Label" /></label>
						<g:textField name="label" required="" value="${datasetInstance?.label}" />
					</div>

					<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'keyColumn', 'error')} required">
						<label for="keyColumn"><g:message code="dataset.keyColumn.label" default="Key Column" /><span class="required-indicator">*</span></label>
						<g:select name="keyColumn" required="" value="${datasetInstance?.keyColumn}" from="${dataSetColumns}" class="many-to-one"/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'tableName', 'error')} required">
						<label for="tableName"><g:message code="dataset.tableName.label" default="Table Name" /><span class="required-indicator">*</span></label>
						<g:select name="tableName" required="" value="${datasetInstance?.tableName}" from="${tableList}" class="many-to-one"/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'tableColumn', 'error')} required">
						<label for="tableColumn"><g:message code="dataset.tableColumn.label" default="Table Column" /><span class="required-indicator">*</span></label>
						<g:select name="tableColumn" required="" value="${datasetInstance?.tableColumn}" from=""/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'modules', 'error')} required">
						<label for="module"><g:message code="dataset.modules.label" default="Modules" /><span class="required-indicator">*</span></label>
						<g:select id="module" name="allmodules.id"  multiple="multiple" from="${org.philimone.hds.explorer.server.model.main.Module.list()}" optionKey="id" required="" value="${modules}" class="many-to-one"/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: datasetInstance, field: 'enabled', 'error')} ">
						<label for="enabled"><g:message code="dataset.enabled.label" default="Enabled" /></label>
						<g:checkBox name="enabled" value="${datasetInstance?.enabled}" />
					</div>

					<g:hiddenField name="tableColumnLabels" value="${datasetInstance?.tableColumnLabels}" />
				</fieldset>
				<fieldset class="buttons">
					<g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
