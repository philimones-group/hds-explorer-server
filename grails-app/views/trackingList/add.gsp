<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'trackingList.label', default: 'TrackingList')}" />
		<title><g:message code="trackingList.add.label" args="[entityName]" /></title>

		<asset:javascript src="application.js"/>
		<asset:javascript src="bootstrap.js"/>

        <style>

        .rerrors {
            font-size: 1em;
            line-height: 2;
            margin: 1em 2em;
            padding: 0.25em;
        }

        .rerrors {
            background: #d6e7d0;
            border: 2px solid #ffaaaa;
            color: #cc0000;
            -moz-box-shadow: 0 0 0.25em #ff8888;
            -webkit-box-shadow: 0 0 0.25em #ff8888;
            box-shadow: 0 0 0.25em #ff8888;
        }

        .rerrors_div {
            color: #666666;
            border-bottom: 1px solid #CCCCCC;
            margin: 0.8em 1.3em 0.3em;
        }

        .rerrors_title {
            color: #666666;
            border-bottom: 1px solid #CCCCCC;
            padding: 0 0.3em;
        }

        .rerrors ul {
            padding: 1em;
        }

        .errors li {
            list-style: none;
            background: transparent url(../images/skin/exclamation.png) 0.5em 50% no-repeat;
            text-indent: 2.2em;
            padding: 0 0.25em;
        }

        </style>
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

		<a href="#create-trackingList" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="index"><g:message code="trackingList.list.label" /></g:link></li>
				<li><g:link class="list" controller="module" action="index"><g:message code="default.menu.updates.modules.label" args="" /></g:link></li>
				<li><g:link class="list" controller="form" action="index"><g:message code="default.menu.updates.forms.label" args="" /></g:link></li>
				<li><g:link class="list" controller="user" action="index"><g:message code="default.menu.users.label" args="" /></g:link></li>
				<li><g:link class="create" action="downloadSampleXLS"><g:message code="trackingList.file.sample.download.label" /></g:link></li>
				<li><g:link class="create" action="downloadTemplateXLS"><g:message code="trackingList.file.template.download.label" /></g:link></li>
			</ul>
		</div>
		<div id="create-trackingList" class="content scaffold-create" role="main">

            <g:if test="${isUpdating}">
                <h1><g:message code="trackingList.update.label" /></h1>
            </g:if>
            <g:else>
                <h1><g:message code="trackingList.create.label" /></h1>
            </g:else>


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

			<g:if test="${errorMessages}">
				<div class="rerrors_div">
					<ul class="rerrors" role="alert">
						<g:each in="${errorMessages}" status="i" var="error">
							<li data-field-id="${error.text}">${error.text}</li>
						</g:each>
					</ul>
				</div>
				<br>
			</g:if>

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
						<b>${trackingListInstance?.filename}</b>
						<g:hiddenField name="filename" value="${trackingListInstance?.filename}" />
                        <g:hiddenField name="absoluteFilename" value="${absoluteFilename}" />
					</div>

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'code', 'error')} required">
						<label for="code"><g:message code="trackingList.code.label" default="Code" /><span class="required-indicator">*</span></label>
						<g:textField name="code" required="" value="${trackingListInstance?.code}" readonly="readonly"/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'name', 'error')} required">
						<label for="name"><g:message code="trackingList.name.label" default="Name" /><span class="required-indicator">*</span></label>
						<g:textField name="name" required="" value="${trackingListInstance?.name}"/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'modules', 'error')} required">
						<label for="module"><g:message code="trackingList.modules.label" default="Modules" /><span class="required-indicator">*</span></label>
						<g:select id="modules" name="modules"  multiple="multiple" from="${org.philimone.hds.explorer.server.model.main.Module.list()}" optionKey="code" required="" value="${modules}" class="many-to-one"/>
					</div>

					<div class="fieldcontain ${hasErrors(bean: trackingListInstance, field: 'enabled', 'error')} ">
						<label for="enabled"><g:message code="trackingList.enabled.label" default="Enabled" /></label>
						<g:checkBox name="enabled" value="${trackingListInstance?.enabled}" />
					</div>

				</fieldset>
				<fieldset class="buttons">
                    <g:if test="${isUpdating}">
                        <g:submitButton name="create" class="save" value="${message(code: 'trackingList.update.button.label', default: 'Update')}" />
                    </g:if>
                    <g:else>
                        <g:submitButton name="create" class="save" value="${message(code: 'trackingList.add.label', default: 'Add')}" />
                    </g:else>

				</fieldset>
			</g:form>

			<br>

		</div>
	</body>
</html>
