<%@ page import="org.philimone.hds.explorer.server.model.main.Dataset" %>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'dataset.label', default: 'Dataset')}" />
		<title><g:message code="dataset.add.label" args="[entityName]" /></title>

		<dt:defaultResources/>
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

		<a href="#create-dataset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="dataset.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="list" controller="module" action="index"><g:message code="default.menu.updates.modules.label" args="" /></g:link></li>
				<li><g:link class="list" controller="form" action="index"><g:message code="default.menu.updates.forms.label" args="" /></g:link></li>
				<li><g:link class="list" controller="user" action="index"><g:message code="default.menu.users.label" args="" /></g:link></li>
			</ul>
		</div>
		<div id="create-dataset" class="content scaffold-create" role="main">
			<h1><g:message code="dataset.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${dataSetInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${dataSetInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>

			<g:uploadForm controller="dataset" action="uploadFile" >
				<fieldset class="form">
					<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'filename', 'error')} " >
						<label for="filename"><g:message code="dataset.file.label" default="Dataset Filename" /></label>
						<input type="file" id="fileUpload" name="fileUpload" style="display:inline;" />
						<g:submitButton name="create" class="button_link" value="${message(code: 'dataset.file.upload.label')}" />
					</div>
				</fieldset>
			</g:uploadForm>

			<g:form controller="dataset" action="save" method="POST" >
				<div class="nav2">
					<fieldset class="form">

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'filename', 'error')} required">
							<label for="filename"><g:message code="dataset.filename.label" default="Filename" /><span class="required-indicator">*</span></label>
							<b>${dataSetInstance?.getFilenameOnly()}</b>
							<g:hiddenField name="filename" value="${dataSetInstance?.filename}" />
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'name', 'error')} ">
							<label for="name"><g:message code="dataset.name.label" default="Name" /></label>
							<g:textField name="name" required="" value="${dataSetInstance?.name}" />
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'label', 'error')} ">
							<label for="name"><g:message code="dataset.label.label" default="Label" /></label>
							<g:textField name="label" required="" value="${dataSetInstance?.label}" />
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'keyColumn', 'error')} required">
							<label for="keyColumn"><g:message code="dataset.keyColumn.label" default="Key Column" /><span class="required-indicator">*</span></label>
							<g:select name="keyColumn" required="" value="${dataSetInstance?.keyColumn}" from="${dataSetColumns}" class="many-to-one"/>
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'tableName', 'error')} required">
							<label for="tableName"><g:message code="dataset.tableName.label" default="Table Name" /><span class="required-indicator">*</span></label>
							<g:select name="tableName" required="" value="${dataSetInstance?.tableName}" from="${tableList}" class="many-to-one"/>
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'tableColumn', 'error')} required">
							<label for="tableColumn"><g:message code="dataset.tableColumn.label" default="Table Column" /><span class="required-indicator">*</span></label>
							<g:select name="tableColumn" required="" value="${dataSetInstance?.tableColumn}" from=""/>
						</div>

                        <div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'modules', 'error')} required">
                            <label for="module"><g:message code="dataset.modules.label" default="Modules" /><span class="required-indicator">*</span></label>
                            <g:select id="module" name="allmodules.id"  multiple="multiple" from="${org.philimone.hds.explorer.server.model.main.Module.list()}" optionKey="id" required="" value="${modules}" class="many-to-one"/>
                        </div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'enabled', 'error')} ">
							<label for="enabled"><g:message code="dataset.enabled.label" default="Enabled" /></label>
							<g:checkBox name="enabled" value="${dataSetInstance?.enabled}" />
						</div>

                        <g:hiddenField name="tableColumnLabels" value="${dataSetInstance?.tableColumnLabels}" />

					</fieldset>
					<fieldset class="buttons">
						<g:submitButton name="create" class="save" value="${message(code: 'dataset.add.label')}" />
					</fieldset>
				</div>
			</g:form>

			<br>

			<div class="nav2">
				<dt:table id="listTable">
					<thead>
					<tr>

						<g:sortableColumn property="name" title="${message(code: 'dataset.name.label', default: 'Name')}" />

						<g:sortableColumn property="keyColumn" title="${message(code: 'dataset.keyColumn.label', default: 'Key Column')}" />

						<g:sortableColumn property="tableName" title="${message(code: 'dataset.tableName.link.label', default: 'Table')}" />

						<g:sortableColumn property="enabled" title="${message(code: 'dataset.enabled.label', default: 'Enabled')}" />

						<th><g:message code="dataset.filename.label" default="Filename" /></th>

					</tr>
					</thead>
					<tbody>
					<g:each in="${dataSetInstanceList}" status="i" var="dataSetInstance">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

							<td><g:link action="show" id="${dataSetInstance.id}">${fieldValue(bean: dataSetInstance, field: "name")}</g:link></td>

							<td>${fieldValue(bean: dataSetInstance, field: "keyColumn")}</td>

							<td>${fieldValue(bean: dataSetInstance, field: "tableName")}</td>

							<td><g:formatBoolean boolean="${dataSetInstance.enabled}" /></td>

							<td>${dataSetInstance.getFilenameOnly()}</td>

						</tr>
					</g:each>
					</tbody>
				</dt:table>
			</div>

			<dt:loadDatatable name="listTable" />
		</div>
	</body>
</html>
