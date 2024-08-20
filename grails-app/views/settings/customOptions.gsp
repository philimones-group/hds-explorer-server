<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <title><g:message code="default.menu.settings.coreformoptions.label" /></title>

        <tb:tabulatorResources/>

    </head>
    <body>
        <g:javascript>

            $(window).on('load', function () {
                var formName = $("#form").val();
                if (formName != null) {
                    loadColumns(formName);
                }
            });

            $(document).ready(function() {
                $("#form").change(function() {
                    loadColumns(this.value)
                });

                $("#column").change(function() {
                    renderTabulator(this.value)
                });
            });

            function loadColumns(formName) {
                $.ajax({
                    url: "${createLink(controller: "settings", action: "getCustomOptionsColumns")}",
                    data: "name=" + formName,
                    cache: false,
                    success: function(html) {
                        $("#column").html(html);

                        var selected = $("#column").val();
                        if (selected != null) {
                            renderTabulator(selected)
                        }
                    }
                });
            }

            function renderTabulator(columnName) {
                $.ajax({
                    url: "${createLink(controller: "settings", action: "renderCustomOptionsTable")}",
                    data: "name=" + columnName,
                    cache: false,
                    success: function(html) {
                        $("#optionsTable").html(html);
                    }
                });
            }
        </g:javascript>

        <a href="#list-coreFormExtension" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="export" controller="syncFiles" action="index"><g:message code="default.menu.sync.export.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-coreFormExtension" class="content scaffold-list" role="main">
            <h1><g:message code="default.menu.settings.coreformoptions.label" /></h1>
            <g:if test="${flash.message}">
                <div class="xmessage" role="status">${flash.message}</div>
            </g:if>

            <div class="whitebox_panel">

                <tb:toast id="options_toast" title="Info" message="Test message" />

                <fieldset class="form-group">
                    <div class="fieldcontain required">
                        <label for="formName">
                            <g:message code="default.menu.settings.coreformoptions.form.label" default="Form Name" /><span class="required-indicator">*</span>
                        </label>
                        <g:select name="form" optionKey="value" optionValue="name" from="${forms}" value="${selectedForm}" />
                    </div>

                    <div class="fieldcontain required">
                        <label for="columnName">
                            <g:message code="default.menu.settings.coreformoptions.column.label" default="Column Name" /><span class="required-indicator">*</span>
                        </label>
                        <g:select name="column" from="" />
                    </div>
                </fieldset>

                <tb:tabulator id="optionsTable" name="optionsTable" toastid="options_toast" contextMenu="true"
                              data="${g.createLink(controller: 'settings', action: 'fetchOptionsList')}"
                              update="${createLink(controller: 'settings', action: 'updateCustomOptions')}" boxed="false">

                    <tb:menuBar>
                        <tb:menu label="${message(code: 'settings.coreformoptions.addlabel.label')}" type="add" action="${createLink(controller: 'settings', action: 'createCustomOptions')}" />
                        <tb:menu label="${message(code: 'settings.coreformoptions.remlabel.label')}" type="delete" action="${createLink(controller: 'settings', action: 'deleteCustomOptions')}" />
                    </tb:menuBar>

                    <tb:column name="columnName"  label="${message(code: 'settings.coreformoptions.columnname.label')}" />
                    <tb:column name="ordinal"     label="${message(code: 'settings.coreformoptions.ordinal.label')}" />
                    <tb:column name="optionValue" label="${message(code: 'settings.coreformoptions.optionValue.label')}" hzalign="left" editor="input" />
                    <tb:column name="optionLabel" label="${message(code: 'settings.coreformoptions.optionLabel.label')}" hzalign="left" editor="input" />
                    <tb:column name="optionLabelCode" label="${message(code: 'settings.coreformoptions.messageCode.label')}" hzalign="left" editor="input" />
                </tb:tabulator>

            </div>
        </div>

    </body>
</html>