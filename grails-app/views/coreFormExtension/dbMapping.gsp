<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'coreFormExtension.label', default: 'CoreFormExtension')}" />
        <title><g:message code="coreFormExtension.mapping.title.label" /></title>

        <dt:tabulatorResources />
    </head>
    <body>
        <a href="#show-coreFormExtension" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-coreFormExtension" class="content scaffold-list" role="main">
            <h1><g:message code="coreFormExtension.mapping.title.label" args="[entityName]" /></h1>
            <br>
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

            <div class="whitebox_panel">

                <dt:toast id="models_toast" title="" message="Test message" />

                <ol class="property-list form">

                    <g:if test="${this.coreFormExtension?.formName}">
                        <li class="fieldcontain">
                            <span id="formName-label" class="property-label"><g:message code="coreFormExtension.formName.label"  /></span>
                            <span class="property-value" aria-labelledby="formName-label"><g:message code="${this.coreFormExtension?.formName}" /></span>
                        </li>
                    </g:if>

                    <g:if test="${this.coreFormExtension?.formId}">
                        <li class="fieldcontain">
                            <span id="formId-label" class="property-label"><g:message code="coreFormExtension.formId.label" /></span>
                            <span class="property-value" aria-labelledby="formId-label"><g:fieldValue bean="${this.coreFormExtension}" field="formId" /></span>
                        </li>
                    </g:if>

                    <g:if test="${this.coreFormExtension?.extFormId}">
                        <li class="fieldcontain">
                            <span id="extFormId-label" class="property-label"><g:message code="coreFormExtension.extFormId.label" /></span>
                            <span class="property-value" aria-labelledby="extFormId-label"><g:fieldValue bean="${this.coreFormExtension}" field="extFormId" /></span>
                        </li>
                    </g:if>


                    <li class="fieldcontain">
                        <span id="extFormDefinition-label" class="property-label"><g:message code="coreFormExtension.extFormDefinition.label" /></span>
                        <span class="property-value" aria-labelledby="extFormDefinition-label">
                            <g:if test="${this.coreFormExtension?.extFormDefinition==null}" >
                                <b><g:message code="coreFormExtension.notuploaded.label"/></b>
                            </g:if>
                            <g:else>
                                <b><g:message code="coreFormExtension.uploaded.label"/></b> (<g:link action="downloadFormDef" id="${this.coreFormExtension?.id}"><g:message code="coreFormExtension.download.label" /></g:link>)
                            </g:else>
                        </span>

                    </li>
                </ol>
            </div>

            <dt:tabulator id="modelsTable" name="modelsTable" toastid="models_toast" boxed="true"
                          addlabel="${message(code: 'settings.coreformoptions.addlabel.label')}"
                          remlabel="${message(code: 'settings.coreformoptions.remlabel.label')}"
                          data="${g.createLink(controller: 'coreFormExtension', action: 'fetchDataModels', id: coreFormExtension?.id)}"
                          update="${createLink(controller: 'coreFormExtension', action: 'updateCustomOptions')}"
                          createrow="${createLink(controller: 'coreFormExtension', action: 'createCustomOptions')}"
                          deleterow="${createLink(controller: 'coreFormExtension', action: 'deleteCustomOptions')}">

                <dt:column name="dbColumnIndex"  label="${message(code: 'coreFormExtension.mapping.dbColumnIndex')}" />
                <dt:column name="dbColumnTable"     label="${message(code: 'coreFormExtension.mapping.dbColumnTable')}" />
                <dt:column name="dbColumnName" label="${message(code: 'coreFormExtension.mapping.dbColumnName')}" hzalign="left" editor="input" />
                <dt:column name="dbColumnType" label="${message(code: 'coreFormExtension.mapping.dbColumnType')}" hzalign="center" display="enumType" />
                <dt:column name="dbColumnSize" label="${message(code: 'coreFormExtension.mapping.dbColumnSize')}" hzalign="center" editor="input" />
                <dt:column name="formColumnName" label="${message(code: 'coreFormExtension.mapping.formColumnName')}" hzalign="left" />
                <dt:column name="formColumnType" label="${message(code: 'coreFormExtension.mapping.formColumnType')}" hzalign="center" display="enumType" />
                <dt:column name="formRepeatGroup" label="${message(code: 'coreFormExtension.mapping.formRepeatGroup')}" hzalign="center" />
                <dt:column name="formRepeatLength" label="${message(code: 'coreFormExtension.mapping.formRepeatLength')}" hzalign="center" editor="input" />
                <dt:column name="formChoiceValue" label="${message(code: 'coreFormExtension.mapping.formChoiceValue')}" hzalign="center" />

            </dt:tabulator>

            <fieldset class="navbar navbar-dark bg-dark">
                <g:form controller="coreFormExtension" method="POST">
                    <g:hiddenField name="id" value="${this.coreFormExtension?.id}" />
                    <g:if test="${this.coreFormExtension?.extFormDefinition != null}" >
                        <g:actionSubmit name="btinit" class="btn btn-danger" action="generateDatabaseModel" value="${g.message(code: 'coreFormExtension.mapping.button.init.label')}" />
                    </g:if>
                    &nbsp;
                    <g:if test="${hasExtensionModels}" >
                        <g:actionSubmit name="btcreate" class="btn btn-primary" action="dbColumns" resource="${this.coreFormExtension}" value="${g.message(code: 'coreFormExtension.mapping.button.create.label')}" />
                    </g:if>
                    &nbsp;
                    <g:actionSubmit name="btsave" class="btn btn-primary" action="dbExtension" resource="${this.coreFormExtension}" value="${g.message(code: 'coreFormExtension.mapping.button.extension.label')}" />
                </g:form>
            </fieldset>
            <br>
        </div>
    </body>
</html>
