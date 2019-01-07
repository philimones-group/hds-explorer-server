<%@ page import="org.philimone.hds.explorer.server.model.main.DataSet" %>

<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'dataSet.label', default: 'DataSet')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>

		<asset:javascript src="application.js"/>
		<asset:javascript src="bootstrap.js"/>
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

		$(window).load(
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

		<a href="#create-dataSet" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="list" controller="studyModule" action="index"><g:message code="default.menu.updates.modules.label" args="" /></g:link></li>
				<li><g:link class="list" controller="form" action="index"><g:message code="default.menu.updates.forms.label" args="" /></g:link></li>
				<li><g:link class="list" controller="user" action="index"><g:message code="default.menu.users.label" args="" /></g:link></li>
			</ul>
		</div>
		<div id="create-dataSet" class="content scaffold-create" role="main">
			<h1><g:message code="dataSet.create.label" args="[entityName]" /></h1>
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

			<g:uploadForm controller="dataSet" action="uploadFile" >
				<fieldset class="form">
					<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'filename', 'error')} " >
						<label for="filename"><g:message code="dataSet.file.label" default="Dataset Filename" /></label>
						<input type="file" id="fileUpload" name="fileUpload" style="display:inline;" />
						<g:submitButton name="create" class="button_link" value="${message(code: 'dataSet.file.upload.label')}" />
					</div>
				</fieldset>
			</g:uploadForm>

			<g:form controller="dataSet" action="save" method="POST" >
				<div class="nav2">
					<fieldset class="form">

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'filename', 'error')} required">
							<label for="filename"><g:message code="dataSet.filename.label" default="Filename" /><span class="required-indicator">*</span></label>
							<b>${dataSetInstance?.getFilenameOnly()}</b>
							<g:hiddenField name="filename" value="${dataSetInstance?.filename}" />
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'name', 'error')} ">
							<label for="name"><g:message code="dataSet.name.label" default="Name" /></label>
							<g:textField name="name" required="" value="${dataSetInstance?.name}" />
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'keyColumn', 'error')} required">
							<label for="keyColumn"><g:message code="dataSet.keyColumn.label" default="Key Column" /><span class="required-indicator">*</span></label>
							<g:select name="keyColumn" required="" value="${dataSetInstance?.keyColumn}" from="${dataSetColumns}" class="many-to-one"/>
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'tableName', 'error')} required">
							<label for="tableName"><g:message code="dataSet.tableName.label" default="Table Name" /><span class="required-indicator">*</span></label>
							<g:select name="tableName" required="" value="${dataSetInstance?.tableName}" from="${tableList}" class="many-to-one"/>
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'tableColumn', 'error')} required">
							<label for="tableColumn"><g:message code="dataSet.tableColumn.label" default="Table Column" /><span class="required-indicator">*</span></label>
							<g:select name="tableColumn" required="" value="${dataSetInstance?.tableColumn}" from=""/>
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'enabled', 'error')} ">
							<label for="enabled"><g:message code="dataSet.enabled.label" default="Enabled" /></label>
							<g:checkBox name="enabled" value="${dataSetInstance?.enabled}" />
						</div>

						<div class="fieldcontain ${hasErrors(bean: dataSetInstance, field: 'mappingLabels', 'error')} ">
							<label for="mappingLabels"><g:message code="dataSet.mappingLabels.label" default="Mapping Labels" /></label>

							<ul class="one-to-many">
								<g:each in="${dataSetInstance?.mappingLabels?}" var="m">
									<li><g:link controller="dataSetLabel" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
								</g:each>
								<li class="add">
									<g:link controller="dataSetLabel" action="create" params="['dataSet.id': dataSetInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'dataSetLabel.label', default: 'DataSetLabel')])}</g:link>
								</li>
							</ul>
						</div>
					</fieldset>
					<fieldset class="buttons">
						<g:submitButton name="create" class="save" value="${message(code: 'dataSet.add.label')}" />
					</fieldset>
				</div>
			</g:form>

			<br>

			<div class="nav2">
				<table>
					<thead>
					<tr>

						<g:sortableColumn property="name" title="${message(code: 'dataSet.name.label', default: 'Name')}" />

						<g:sortableColumn property="keyColumn" title="${message(code: 'dataSet.keyColumn.label', default: 'Key Column')}" />

						<g:sortableColumn property="tableName" title="${message(code: 'dataSet.tableName.link.label', default: 'Table')}" />

						<g:sortableColumn property="enabled" title="${message(code: 'dataSet.enabled.label', default: 'Enabled')}" />

						<th><g:message code="dataSet.filename.label" default="Filename" /></th>

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
				</table>
				<div class="pagination">
					<g:paginate total="${dataSetInstanceCount ?: 0}" />
				</div>
			</div>

		</div>
	</body>
</html>
