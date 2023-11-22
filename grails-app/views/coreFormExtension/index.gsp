<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'coreFormExtension.label', default: 'CoreFormExtension')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>

        <dt:defaultResources />
    </head>
    <body>

    <g:javascript>
        $(document).ready(function() {

            const forms = ["region_ext", "household_ext", "visit_ext", "member_ext", "marital_relationship_ext", "inmigration_ext", "outmigration_ext", "pregnancy_registration_ext", "pregnancy_outcome_ext", "death_ext", "change_head_ext", "incomplete_visit_ext"];

            //Listen to all checkboxes in the table
            $("#formsTable").on("change", ':checkbox', function(e) {
                var name = this.name;
                var checked = this.checked;

                //console.log("checked "+this.name+" value="+this.checked)

                $.ajax({
                    url: "${createLink(controller: "coreFormExtension", action: "updateFormsTable")}", data: { "name" : name + ":" + checked}, cache: false,
                    success: function(html) {

                    }
                });

            });
        });
    </g:javascript>

        <a href="#list-coreFormExtension" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-coreFormExtension" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <g:if test="${errorMessages}">
                <div class="rerrors_div">
                    <ul class="rerrors" role="alert">
                        <g:each in="${errorMessages}" status="i" var="error">
                            <li data-field-id="error">${error}</li>
                        </g:each>
                    </ul>
                </div>
                <br>
            </g:if>

            <dt:table id="formsTable">
                <thead>
                <tr>
                    <th></th>

                    <th><g:message code="coreFormExtension.formName.label" /></th>

                    <th><g:message code="coreFormExtension.formId.label" /></th>

                    <th><g:message code="coreFormExtension.extFormId.label" /></th>

                    <th><g:message code="coreFormExtension.extFormDefinition.label" /></th>

                    <th><g:message code="coreFormExtension.uploadFormDefinition.label" /></th>

                    <th><g:message code="coreFormExtension.required.label" /></th>

                    <th><g:message code="coreFormExtension.enabled.label" /></th>

                    <th></th>

                </tr>
                </thead>
                <tbody>
                <g:each in="${coreFormExtensionList}" status="i" var="formInstance">
                    <tr>

                        <td></td>

                        <td><g:message code="${formInstance.formName}" /></td>

                        <td>${formInstance.formId}</td>

                        <td>${formInstance.extFormId}</td>

                        <td>
                            <g:if test="${formInstance.extFormDefinition==null}" >
                                <b><g:message code="coreFormExtension.notuploaded.label"/></b>
                            </g:if>
                            <g:else>
                                <b><g:message code="coreFormExtension.uploaded.label"/></b> (<g:link action="downloadFormDef" id="${formInstance.id}"><g:message code="coreFormExtension.download.label" /></g:link>)
                            </g:else>
                        </td>

                        <td>
                            <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#upload_${formInstance.extFormId}">
                                <g:message code="coreFormExtension.uploadFormDefinition.button.label"/>
                            </button>

                            <!-- Modal -->
                            <div id="upload_${formInstance.extFormId}" class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
                                <div class="modal-dialog modal-dialog-centered">
                                    <div class="modal-content">
                                        <g:uploadForm controller="coreFormExtension" action="uploadFormDef" >
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="staticBackdropLabel"><g:message code="coreFormExtension.uploadFormDefinition.title.label" /></h5>
                                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                                    <span aria-hidden="true">&times;</span>
                                                </button>
                                            </div>
                                            <div class="modal-body">
                                                <g:message code="coreFormExtension.uploadFormDefinition.text.label" />:
                                                <g:hiddenField name="formId" value="${formInstance.extFormId}" />
                                                <input type="file" id="fileUpload" name="fileUpload" style="display:inline;" />
                                                <br>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                                <g:submitButton name="create" class="btn btn-primary" value="${message(code: 'trackingList.file.uploadFormDefinition.save.label')}" />
                                            </div>
                                        </g:uploadForm>
                                    </div>
                                </div>
                            </div>

                        </td>

                        <td><g:checkBox name="required.${formInstance.extFormId}" value="${formInstance.required}" /></td>

                        <td><g:checkBox name="enabled.${formInstance.extFormId}" value="${formInstance.enabled}" /></td>

                        <td><g:link action="downloadFormXLS" id="${formInstance.id}"><g:message code="coreFormExtension.downloadSampleXls.button.label"/></g:link></td>

                    </tr>
                </g:each>
                </tbody>
            </dt:table>

        </div>

        <dt:loadDatatable name="formsTable" pageLength="25" />

    </body>
</html>