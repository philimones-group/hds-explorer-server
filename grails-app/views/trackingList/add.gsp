<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'trackingList.label', default: 'TrackingList')}" />
		<title><g:message code="trackingList.add.label" args="[entityName]" /></title>

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

		<a href="#create-trackingList" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="list" controller="module" action="index"><g:message code="default.menu.updates.modules.label" args="" /></g:link></li>
				<li><g:link class="list" controller="form" action="index"><g:message code="default.menu.updates.forms.label" args="" /></g:link></li>
				<li><g:link class="list" controller="user" action="index"><g:message code="default.menu.users.label" args="" /></g:link></li>
			</ul>
		</div>
		<div id="create-trackingList" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${trackingListInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${trackingListInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>

			<g:uploadForm controller="trackingList" action="uploadFile" >
				<fieldset class="form">
					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'filename', 'error')} " >
						<label for="filename"><g:message code="trackingList.file.label" default="Follow-up List Filename" /></label>
						<input type="file" id="fileUpload" name="fileUpload" style="display:inline;" />
						<g:submitButton name="create" class="button_link" value="${message(code: 'trackingList.file.upload.label')}" />
					</div>
				</fieldset>
			</g:uploadForm>

			<g:form url="[resource:trackingListInstance, action:'save']" >
				<fieldset class="form">

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'filename', 'error')} required">
						<label for="filename"><g:message code="trackingList.filename.label" default="Filename" /><span class="required-indicator">*</span></label>
						<b>${trackingListInstance?.getFilenameOnly()}</b>
						<g:hiddenField name="filename" value="${trackingListInstance?.filename}" />
					</div>

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'code', 'error')} required">
						<label for="code"><g:message code="trackingList.code.label" default="Code" /><span class="required-indicator">*</span></label>
						<g:textField name="code" required="" value="${trackingListInstance?.code}" readonly="readonly"/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'name', 'error')} required">
						<label for="name"><g:message code="trackingList.name.label" default="Name" /><span class="required-indicator">*</span></label>
						<g:textField name="name" required="" value="${trackingListInstance?.name}"/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'module', 'error')} required">
						<label for="module"><g:message code="trackingList.module.label" default="Module" /><span class="required-indicator">*</span></label>
						<g:select id="module" name="module.id" from="${org.philimone.hds.explorer.server.model.main.Module.list()}" optionKey="id" required="" value="${trackingListInstance?.module?.id}" class="many-to-one"/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'enabled', 'error')} ">
						<label for="enabled"><g:message code="trackingList.enabled.label" default="Enabled" /></label>
						<g:checkBox name="enabled" value="${trackingListInstance?.enabled}" />
					</div>

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'mappings', 'error')} ">
						<label for="mappings"><g:message code="trackingList.mappings.label" default="Mappings" /></label>

						<ul class="one-to-many">
							<g:each in="${trackingListInstance?.mappings?}" var="m">
								<li><g:link controller="trackingListMapping" action="show" id="${m.id}">${m?.encodeAsHTML()}</g:link></li>
							</g:each>
							<li class="add">
								<g:link controller="trackingListMapping" action="create" params="['trackingList.id': trackingListInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'trackingListMapping.label', default: 'TrackingListMapping')])}</g:link>
							</li>
						</ul>

					</div>

				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'trackingList.add.label', default: 'Add')}" />
				</fieldset>
			</g:form>

			<br>

			<div class="nav2">
				<table>
				<thead>
				<tr>

					<g:sortableColumn property="code" title="${message(code: 'trackingList.code.label', default: 'Code')}" />

					<g:sortableColumn property="name" title="${message(code: 'trackingList.name.label', default: 'Name')}" />

					<th><g:message code="trackingList.module.label" default="Module" /></th>

					<g:sortableColumn property="filename" title="${message(code: 'trackingList.filename.label', default: 'Filename')}" />

					<g:sortableColumn property="enabled" title="${message(code: 'trackingList.enabled.label', default: 'Enabled')}" />

				</tr>
				</thead>
				<tbody>
				<g:each in="${trackingListInstanceList}" status="i" var="trackingListInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

						<td><g:link action="show" id="${trackingListInstance.id}">${fieldValue(bean: trackingListInstance, field: "code")}</g:link></td>

						<td>${fieldValue(bean: trackingListInstance, field: "name")}</td>

						<td>${fieldValue(bean: trackingListInstance, field: "module")}</td>

						<td>${fieldValue(bean: trackingListInstance, field: "filenameOnly")}</td>

						<td><g:formatBoolean boolean="${trackingListInstance.enabled}" /></td>

					</tr>
				</g:each>
				</tbody>
			</table>
			</div>

		</div>
	</body>
</html>
