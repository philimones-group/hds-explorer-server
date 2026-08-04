<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'parameters.label', default: 'Module')}" />
        <title><g:message code="settings.parameters.label" args="[entityName]" /></title>

        <style>
        .settings-tabs {
            display: flex;
            list-style: none;
            padding: 0;
            margin: 0 0 20px 0;
            border-bottom: 1px solid #ddd;
        }
        .settings-tabs li {
            padding: 10px 25px;
            cursor: pointer;
            border: 1px solid transparent;
            border-bottom: none;
            margin-bottom: -1px;
            color: #666;
        }
        .settings-tabs li.active {
            background: #fff;
            border-color: #ddd;
            border-bottom: 1px solid #fff;
            font-weight: bold;
            color: #cc0000;
        }
        .settings-tab-content {
            display: none;
        }
        .settings-tab-content.active {
            display: block;
        }
        .schedule-container {
            margin-top: 15dp;
            padding: 15dp;
            background: #f9f9f9;
            border: 1px solid #eee;
        }
        .schedule-container h3 {
            font-size: 1.1em;
            margin-bottom: 10px;
            color: #333;
        }
        </style>

    </head>
    <body>
        <g:javascript>
            $(document).ready(function() {
                $(".settings-tabs li").click(function() {
                    var tabId = $(this).attr("data-tab");
                    $(".settings-tabs li").removeClass("active");
                    $(this).addClass("active");
                    $(".settings-tab-content").removeClass("active");
                    $("#" + tabId).addClass("active");
                });

                $("#maxAntepartumVisits, #maxPostpartumVisits").on("input change", function() {
                    renderPregnancySchedules();
                });

                renderPregnancySchedules();
            });

            function renderPregnancySchedules() {
                var maxAnt = parseInt($("#maxAntepartumVisits").val()) || 0;
                var maxPost = parseInt($("#maxPostpartumVisits").val()) || 0;
                var antSchedStr = "${antSchedule}";
                var postSchedStr = "${postSchedule}";

                var antSched = antSchedStr ? antSchedStr.split(",") : [];
                var postSched = postSchedStr ? postSchedStr.split(",") : [];

                renderScheduleInputs("antepartumScheduleContainer", "antSchedule", maxAnt, antSched, "${message(code: 'settings.pregnancy.antepartumSchedule.label')}", "Weeks");
                renderScheduleInputs("postpartumScheduleContainer", "postSchedule", maxPost, postSched, "${message(code: 'settings.pregnancy.postpartumSchedule.label')}", "Days");
            }

            function renderScheduleInputs(containerId, namePrefix, count, currentValues, label, unit) {
                var container = $("#" + containerId);
                container.empty();

                if (count > 0) {
                    container.append("<h3>" + label + "</h3>");
                    for (var i = 0; i < count; i++) {
                        var val = currentValues[i] || "";
                        var html = '<div class="fieldcontain required">' +
                                   '<label>' + "${message(code: 'settings.pregnancy.visit.label')}" + ' ' + (i+1) + '</label>' +
                                   '<input type="number" name="' + namePrefix + '_' + i + '" value="' + val + '" required="" style="width: 100px; display: inline-block;" /> ' + unit +
                                   '</div>';
                        container.append(html);
                    }
                }
            }

        </g:javascript>

        <a href="#create-parameters" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
            </ul>
        </div>
        <div id="create-parameters" class="content scaffold-create" role="main">
            <h1><g:message code="settings.parameters.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <g:if test="${errorMessages.size()>0}">
                <ul class="errors" role="alert">
                    <g:each in="${errorMessages}" status="i" var="errorMessage" >
                        <li data-field-id="systemLanguage}">${errorMessage}</li>
                    </g:each>
                </ul>
            </g:if>

            <br>
            <div class="whitebox_panel">
                <ul class="settings-tabs">
                    <li class="active" data-tab="core-options"><g:message code="settings.parameters.tab.system.label" /></li>
                    <li data-tab="pregnancy-surveillance"><g:message code="settings.parameters.tab.pregnancy.label" /></li>
                </ul>

                <div id="core-options" class="settings-tab-content active">
                    <g:form controller="settings" action="updateParameters" method="POST">
                        <fieldset class="form">

                            <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                                <label for="systemLanguage" title="${message(code: 'settings.parameters.language.description.label')}">
                                    <g:message code="settings.parameters.language.label" />
                                    <span class="required-indicator">*</span>
                                </label>

                                <g:select name="systemLanguage" required="" value="${selectedLanguage}" from="${languages}" optionKey="language" optionValue="displayLanguage" class="many-to-one"/>

                            </div>

                            <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                                <label for="systemInputCalendar" title="${message(code: 'settings.parameters.calendar.description.label')}">
                                    <g:message code="settings.parameters.calendar.label" />
                                    <span class="required-indicator">*</span>
                                </label>

                                <select id="systemInputCalendar" name="systemInputCalendar" required="" value="${selectedCalendar}" optionKey="value" optionValue="name" class="many-to-one">
                                    <g:each in="${calendars}" var="cal">
                                        <g:if test="${cal.value?.equals(selectedCalendar.value)}">
                                            <option value="${cal.value}" selected><g:message code="${cal.name}" /> </option>
                                        </g:if>
                                        <g:else>
                                            <option value="${cal.value}" ><g:message code="${cal.name}" /></option>
                                        </g:else>
                                    </g:each>
                                </select>

                            </div>

                            <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                                <label for="codeGenerator" title="${message(code: 'settings.parameters.codegenerator.description.label')}">
                                    <g:message code="settings.parameters.codegenerator.label" />
                                    <span class="required-indicator">*</span>
                                </label>

                                <g:select name="codeGenerator" required="" value="${selectedCodeGenerator}" from="${codeGenerators}" optionKey="value" optionValue="name" class="many-to-one"/>

                            </div>

                            <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                                <label for="codeGeneratorIncRule" title="${message(code: 'settings.parameters.codegenerator.incremental.rule.label')}">
                                    <g:message code="settings.parameters.codegenerator.incremental.rule.label" />
                                    <span class="required-indicator">*</span>
                                </label>

                                <select id="codeGeneratorIncRule" name="codeGeneratorIncRule" required="" value="${selectedCodeGeneratorIncRule}" class="many-to-one">
                                    <g:each in="${codeGeneratorsRules}" var="rule">
                                        <g:if test="${rule.value?.equals(selectedCodeGeneratorIncRule)}">
                                            <option value="${rule.value}" selected><g:message code="${rule.name}" /> </option>
                                        </g:if>
                                        <g:else>
                                            <option value="${rule.value}" ><g:message code="${rule.name}" /></option>
                                        </g:else>

                                    </g:each>
                                </select>

                            </div>

                            <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                                <label for="exportHistoryMode" title="${message(code: 'settings.parameters.export.history.mode.label')}">
                                    <g:message code="settings.parameters.export.history.mode.label" />
                                    <span class="required-indicator">*</span>
                                </label>

                                <select id="exportHistoryMode" name="exportHistoryMode" required="" value="${selectedExportHistoryMode}" from="${exportHistoryModes}" optionKey="code" optionValue="name" class="many-to-one">
                                    <g:each in="${exportHistoryModes}" var="mode">
                                        <g:if test="${mode.equals(selectedExportHistoryMode)}">
                                            <option value="${mode.code}" selected><g:message code="${mode.name}" /> </option>
                                        </g:if>
                                        <g:else>
                                            <option value="${mode.code}" ><g:message code="${mode.name}" /></option>
                                        </g:else>

                                    </g:each>
                                </select>
                            </div>

                            <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                                <label for="regionHeadSupport" title="${message(code: 'settings.parameters.region.head.support.label')}">
                                    <g:message code="settings.parameters.region.head.support.label" />
                                    <span class="required-indicator">*</span>
                                </label>

                                <g:checkBox name="regionHeadSupport" value="${selectedRegionHeadSupport}" />

                            </div>

                            <div class="fieldcontain ${hasErrors(bean: this.parameters, field: 'code', 'error')} ">
                                <label for="gpsRequired" title="${message(code: 'settings.parameters.visit.gps.required.label')}">
                                    <g:message code="settings.parameters.visit.gps.required.label" />
                                    <span class="required-indicator">*</span>
                                </label>

                                <g:checkBox name="gpsRequired" value="${selectedGpsRequired}" />

                            </div>

                        </fieldset>
                        <fieldset class="buttons">
                            <g:submitButton name="create" class="save" value="${message(code: 'settings.parameters.update.label')}" />
                        </fieldset>
                    </g:form>
                </div>

                <div id="pregnancy-surveillance" class="settings-tab-content">
                    <g:form action="updatePregnancyParameters" method="POST">
                        <fieldset class="form">
                            <div class="fieldcontain required">
                                <label for="maxAntepartumVisits"><g:message code="settings.pregnancy.maxAntepartumVisits.label" /></label>
                                <g:field type="number" id="maxAntepartumVisits" name="maxAntepartumVisits" value="${maxAntVisits}" min="1" max="20" required="" />
                            </div>

                            <div id="antepartumScheduleContainer" class="schedule-container">
                                <!-- Dynamic inputs -->
                            </div>

                            <div class="fieldcontain required" style="margin-top: 20px;">
                                <label for="maxPostpartumVisits"><g:message code="settings.pregnancy.maxPostpartumVisits.label" /></label>
                                <g:field type="number" id="maxPostpartumVisits" name="maxPostpartumVisits" value="${maxPostVisits}" min="1" max="20" required="" />
                            </div>

                            <div id="postpartumScheduleContainer" class="schedule-container">
                                <!-- Dynamic inputs -->
                            </div>
                        </fieldset>
                        <fieldset class="buttons">
                            <g:submitButton name="update" class="save" value="${message(code: 'default.button.update.label')}" />
                        </fieldset>
                    </g:form>
                </div>
            </div>

        </div>
    </body>
</html>
