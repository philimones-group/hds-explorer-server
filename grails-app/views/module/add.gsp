<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'modulesMapping.label', default: 'ModulesMapping')}" />
		<title><g:message code="module.updates.label" args="[entityName]" /></title>

		<asset:javascript src="application.js"/>
		<asset:javascript src="bootstrap.js"/>

		<g:javascript>

			$(window).on('load',
				function () {
					//alert("testing jquery");

					var mode = $('#grantMode option:selected').val()

					if (mode == "0") { //CSV File mode
						$("#panelUpload").show();
						$("#divBreak").hide();
						$("#divEntity").hide();
						$("#grantModeValue").val("0");
					} else {
						$("#panelUpload").hide();
						$("#divBreak").show();
						$("#divEntity").show();
						$("#grantModeValue").val("1");
					}
				}
			);

			$(document).ready(function() {

                $("#grantMode").change(function() {

                    //parent
                    var mode = this.value;

                    if (mode == "0") { //CSV File mode
                        $("#panelUpload").show();
						$("#divBreak").hide();
						$("#divEntity").hide();
						$("#grantModeValue").val("0");
                    } else {
                        $("#panelUpload").hide();
						$("#divBreak").show();
						$("#divEntity").show();
						$("#grantModeValue").val("1");
                    }

                });
			});

		</g:javascript>

	</head>
	<body>

		<a href="#create-module" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="module.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="list" controller="module" action="index"><g:message code="default.menu.updates.modules.label" args="" /></g:link></li>
				<li><g:link class="list" controller="form" action="index"><g:message code="default.menu.updates.forms.label" args="" /></g:link></li>
				<li><g:link class="list" controller="user" action="index"><g:message code="default.menu.users.label" args="" /></g:link></li>
			</ul>
		</div>
		<div id="create-module" class="content scaffold-create" role="main">
			<h1><g:message code="module.updates.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>

			<br>

			<div class="fieldcontain false">
				<label for="grantMode"><g:message code="module.updates.grantMode.label" /></label>
				<g:select name="grantMode" from="${grantModes}" value="${grantModeValue}" optionKey="value" optionValue="name" />
			</div>

			<div id="divBreak">
				<br>
			</div>

			<fieldset id="panelUpload" class="form">
				<g:uploadForm controller="module" action="uploadCodesFile" >

					<div class="fieldcontain " >
						<label for="filename"><g:message code="module.updates.filename.label" /></label>
						<input type="file" id="fileUpload" name="fileUpload" style="display:inline;" />
						<g:submitButton name="create" class="button_link" value="${message(code: 'module.updates.upload.label')}" />
					</div>

				</g:uploadForm>

				<div class="fieldcontain required">
					<label for="filename"><g:message code="module.updates.uploaded.label" default="Filename" /><span class="required-indicator">*</span></label>
					<b>${modulesShortFilename}</b>
				</div>

				<div class="fieldcontain required">
					<label for="regions"><g:message code="module.updates.regions.label" /><span class="required-indicator">*</span></label>
					<b>${totalRegions}</b>
				</div>

				<div class="fieldcontain required">
					<label for="households"><g:message code="module.updates.households.label" /><span class="required-indicator">*</span></label>
					<b>${totalHouseholds}</b>
				</div>

				<div class="fieldcontain required">
					<label for="members"><g:message code="module.updates.members.label" /><span class="required-indicator">*</span></label>
					<b>${totalMembers}</b>
				</div>

			</fieldset>

			<g:form controller="module" action="saveModuleMappings" method="POST" >
				<div class="nav2">
					<fieldset class="form">

						<g:hiddenField name="grantModeValue" value="${grantModeValue}" />
						
						<g:hiddenField name="filename" value="${modulesFilename}" />

						<div id="divEntity" class="fieldcontain required">
							<label for="entity"><g:message code="module.updates.list.entity.label" default="Key Column" /><span class="required-indicator">*</span></label>
							<g:select name="entity" required="" value="${selectedEntity}" from="${entities}" class="many-to-one"/>
						</div>

						<!-- select modules -->
						<div class="fieldcontain false">
							<label for="modules"><g:message code="module.updates.modules.label" /></label>
							<g:select name="modules" multiple="multiple" optionKey="id" from="${org.philimone.hds.explorer.server.model.main.Module.list()}" value="${selectedModules}" />
						</div>


					</fieldset>
					<fieldset class="buttons">
						<g:submitButton name="create" class="save" value="${message(code: 'module.updates.update.label')}" />
					</fieldset>
				</div>
			</g:form>

			<br>

			<div class="nav2">
				<table>
					<thead>
					<tr>

						<g:sortableColumn property="entity" title="${message(code: 'module.updates.list.entity.label')}" />

						<g:sortableColumn property="records" title="${message(code: 'module.updates.list.records.label')}" />

						<g:sortableColumn property="modules" title="${message(code: 'module.updates.list.modules.label')}" />

						<g:sortableColumn property="nomodules" title="${message(code: 'module.updates.list.nomodules.label')}" />

					</tr>
					</thead>
					<tbody>
					<g:each in="${groupModulesMappings}" status="i" var="modulesData">
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

							<td>${modulesData[0]}</td>

							<td>${modulesData[1]}</td>

							<td>${modulesData[2]}</td>

							<td>${modulesData[3]}</td>

						</tr>
					</g:each>
					</tbody>
				</table>
				<div class="pagination">
					<g:paginate total="${4}" />
				</div>
			</div>

		</div>
	</body>
</html>
