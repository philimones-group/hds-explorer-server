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
            function onAiProviderChanged(provider) {
                var isOllama = provider === 'ollama';
                $("#aiOllamaUrlContainer").toggle(isOllama);
                $("#aiApiKeyContainer").toggle(!isOllama);
            }

            function toggleApiKeyVisibility() {
                var input = document.getElementById('aiApiKey');
                if (input) {
                    input.type = (input.type === 'password') ? 'text' : 'password';
                }
            }

            function testAiServerConnection() {
                var btn = $("#btnTestAiConn");
                var badge = $("#aiConnTestResult");
                btn.prop('disabled', true).text("${g.message(code: 'settings.ai.testingConnection', default: 'Testing connection...')}");
                badge.text("${g.message(code: 'settings.ai.connecting', default: 'Connecting to AI provider...')}").css("color", "#0284c7");

                $.ajax({
                    url: "${createLink(controller: 'settings', action: 'testAiConnection')}",
                    type: "POST",
                    dataType: "json",
                    success: function(data) {
                        btn.prop('disabled', false).html("🔌 " + "${g.message(code: 'settings.ai.testConnectionBtn', default: 'Test AI Connection')}");
                        if (data && data.success) {
                            badge.text("✓ " + data.message).css("color", "#16a34a");
                        } else {
                            badge.text("❌ " + (data.message || "${g.message(code: 'settings.ai.connectionFailed', default: 'Connection failed')}")).css("color", "#dc2626");
                        }
                    },
                    error: function(err) {
                        btn.prop('disabled', false).html("🔌 " + "${g.message(code: 'settings.ai.testConnectionBtn', default: 'Test AI Connection')}");
                        badge.text("❌ " + "${g.message(code: 'settings.ai.connectionError', default: 'Connection error:')} " + err.statusText).css("color", "#dc2626");
                    }
                });
            }

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

                var currentProv = $("#aiProvider").val();
                onAiProviderChanged(currentProv);
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
                    <li data-tab="ai-prompt-settings"><g:message code="settings.parameters.tab.ai.label" default="AI Prompt Dataset Export Tool" /></li>
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

                <div id="ai-prompt-settings" class="settings-tab-content">
                    <g:form action="updateAiParameters" method="POST">
                        <fieldset class="form">
                            <!-- 1. AI Provider -->
                            <div class="fieldcontain required">
                                <label for="aiProvider">
                                    <g:message code="settings.ai.provider.label" default="AI Provider" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <select id="aiProvider" name="aiProvider" class="many-to-one" onchange="onAiProviderChanged(this.value)">
                                    <option value="ollama" ${selectedAiProvider == 'ollama' ? 'selected="selected"' : ''}><g:message code="settings.ai.provider.ollama" default="Ollama (Local)" /></option>
                                    <option value="groq" ${selectedAiProvider == 'groq' ? 'selected="selected"' : ''}><g:message code="settings.ai.provider.groq" default="Groq AI" /></option>
                                    <option value="gemini" ${selectedAiProvider == 'gemini' ? 'selected="selected"' : ''}><g:message code="settings.ai.provider.gemini" default="Google Gemini" /></option>
                                    <option value="openai" ${selectedAiProvider == 'openai' ? 'selected="selected"' : ''}><g:message code="settings.ai.provider.openai" default="OpenAI" /></option>
                                    <option value="openrouter" ${selectedAiProvider == 'openrouter' ? 'selected="selected"' : ''}><g:message code="settings.ai.provider.openrouter" default="OpenRouter" /></option>
                                </select>
                            </div>

                            <!-- 2. Ollama Base URL -->
                            <div class="fieldcontain" id="aiOllamaUrlContainer">
                                <label for="aiOllamaUrl">
                                    <g:message code="settings.ai.ollamaUrl.label" default="Ollama Base URL" />
                                </label>
                                <input type="text" id="aiOllamaUrl" name="aiOllamaUrl" value="${selectedAiOllamaUrl}" placeholder="http://localhost:11434" style="width: 350px;" />
                                <span class="help-block" style="font-size: 0.8rem; color: #666; margin-left: 10px;"><g:message code="settings.ai.ollamaUrl.hint" default="Hint: For Docker, use http://host.docker.internal:11434" /></span>
                            </div>

                            <!-- 3. Cloud Provider API Key -->
                            <div class="fieldcontain" id="aiApiKeyContainer" style="display: none;">
                                <label for="aiApiKey">
                                    <g:message code="settings.ai.apiKey.label" default="AI Provider API Key" />
                                </label>
                                <div style="display: inline-block;">
                                    <input type="password" id="aiApiKey" name="aiApiKey" value="${selectedAiApiKey}" autocomplete="new-password" style="width: 350px;" placeholder="${message(code: 'settings.ai.apiKey.placeholder', default: 'Enter Cloud API Key (e.g. gsk_... or sk-...)')}" />
                                    <button type="button" class="button" onclick="toggleApiKeyVisibility()" style="margin-left: 5px;"><g:message code="settings.ai.toggleKey" default="Show/Hide" /></button>
                                </div>
                            </div>

                            <!-- 4. AI Model Name -->
                            <div class="fieldcontain required">
                                <label for="aiModelName">
                                    <g:message code="settings.ai.modelName.label" default="AI Model Name" />
                                    <span class="required-indicator">*</span>
                                </label>
                                <input type="text" id="aiModelName" name="aiModelName" value="${selectedAiModelName}" required="" placeholder="${message(code: 'settings.ai.modelName.placeholder', default: 'e.g. sqlcoder, llama-3.3-70b-versatile, gemini-1.5-flash, gpt-4o')}" style="width: 350px;" />
                            </div>

                            <!-- Test Connection Result Status Badge -->
                            <div class="fieldcontain" style="margin-top: 10px;">
                                <label></label>
                                <button type="button" id="btnTestAiConn" class="button" onclick="testAiServerConnection()"><g:message code="settings.ai.testConnectionBtn" default="Test AI Connection" /></button>
                                <span id="aiConnTestResult" style="margin-left: 15px; font-weight: bold; font-size: 0.9rem;"></span>
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
