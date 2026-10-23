<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="dataExport.title" default="HDS-Explorer Server Data Export Tool"/></title>
    <asset:stylesheet src="tabulator_site.min.css"/>
    <asset:javascript src="tabulator.min.js"/>
    <style>

    .card-custom {
        background-color: var(--card-bg);
        border: 1px solid var(--border-color);
        border-radius: 12px;
        box-shadow: 0 2px 6px rgba(0,0,0,0.04), 0 10px 25px rgba(0,0,0,0.03);
        transition: transform 0.2s ease, box-shadow 0.2s ease;
    }

    /* Breadcrumb Header Card */
    .breadcrumb-header-card {
        height: 72px;
        border-radius: 12px;
        padding: 0 24px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 14px;
    }
    .breadcrumb-title {
        font-size: 14px;
        font-weight: 600;
        color: var(--text-primary);
    }

        /* Progress Stepper Header */
        .stepper-container {
            display: flex;
            justify-content: space-between;
            background: #ffffff;
            padding: 16px 30px;
            border-bottom: 1px solid #e2e8f0;
            margin-bottom: 20px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);

            padding: 16px 30px; /* 16px top/bottom, 12% space on the left and right */
        }
        .step-item {
            display: flex;
            align-items: center;
            font-weight: 600;
            color: #94a3b8;
            font-size: 0.95rem;
        }
        .step-item.active {
            color: #2563eb;
        }
        .step-number {
            width: 28px;
            height: 28px;
            border-radius: 50%;
            background: #cbd5e1;
            color: #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 10px;
            font-size: 0.85rem;
        }
        .step-item.active .step-number {
            background: #2563eb;
        }

        /* Layout Grid */
        .workspace-row {
            display: flex;
            /* This creates a perfect 20px border padding around the whole layout */
            padding-left: 20px;
            gap: 20px;
            box-sizing: border-box;
        }
        .left-workspace {
            flex: 0 0 76%;
            max-width: 76%;
        }
        .right-sidebar {
            flex: 0 0 22%;
            max-width: 22%;
            position: sticky;
            top: 20px;
            height: fit-content;
        }

        /* Section Cards */
        .enterprise-card {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
        }
        .enterprise-card-title {
            font-size: 1.1rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        /* Category Selection Cards */
        .category-card-grid {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
        }
        .category-card {
            flex: 1;
            border: 2px solid #e2e8f0;
            border-radius: 10px;
            padding: 16px 0px 16px 26px;
            background: #ffffff;
            cursor: pointer;
            transition: all 0.2s ease-in-out;
            position: relative;

            /* 🛠️ Modern Side-by-Side Flex Layout */
            display: flex;
            flex-direction: row;      /* Places children horizontally next to each other */
            align-items: flex-start;  /* Keeps the icon pinned to the top line if desc is long */
            gap: 14px;                /* Creates a clean 14px gap between the icon and text block */
        }
        .category-card:hover {
            border-color: #93c5fd;
        }
        .category-card.selected {
            border-color: #2563eb;
            background: rgba(239, 246, 255, 0.37);
        }
        .category-card .card-content {
            flex: 1;                  /* Allows the text box to fill the rest of the card width */
            display: flex;
            flex-direction: column;   /* Keeps the title on top and the description underneath */

            max-width: 200px;
        }
        .category-card .icon-box {
            font-size: 1.8rem;
            margin-bottom: 8px;

            /* 🛠️ Rounded Badge Magic */
            display: flex;
            align-items: center;
            justify-content: center;
            width: 56px;
            height: 56px;
            border-radius: 12px;
            background: #f1f5f9;
            border: 1px solid #e2e8f0;
            flex-shrink: 0;
            transition: all 0.2s ease-in-out;
        }
        .category-card .card-title {
            font-size: 1rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 4px;
        }
        .category-card .card-desc {
            font-size: 0.8rem;
            color: #64748b;
            line-height: 1.3;

            /* 🛠️ Force Text Wrapping Magic */
            white-space: normal;       /* Allows text to break lines naturally */
            word-break: break-word;    /* Breaks extra-long words if they don't fit */
            overflow-wrap: break-word; /* Standard modern property for wrapping text */
        }
        .checkmark-badge {
            position: absolute;
            top: 10px;
            right: 10px;
            background: #2563eb;
            color: #ffffff;
            border-radius: 50%;
            width: 20px;
            height: 20px;
            display: none;
            align-items: center;
            justify-content: center;
            font-size: 0.75rem;
        }
        .category-card.selected .checkmark-badge {
            display: flex;
        }

        /* AI Prompt Option Styling */
        .category-card-ai .icon-box {
            color: #7c3aed;
            background: #f3e8ff;
            border-color: #e9d5ff;
        }
        .category-card-ai.selected {
            border-color: #8b5cf6;
            background: rgba(245, 243, 255, 0.75);
        }
        .category-card-ai:hover {
            border-color: #a78bfa;
        }
        .category-card-ai .card-title {
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .ai-option-pill {
            display: inline-flex;
            align-items: center;
            border-radius: 999px;
            padding: 0px 7px;
            color: #6d28d9;
            background: #ede9fe;
            font-size: 0.65rem;
            font-weight: 700;
            letter-spacing: .03em;
        }
        .ai-prompt-panel {
            display: none;
            margin-top: 16px;
            padding: 18px;
            border: 1px solid #ddd6fe;
            border-radius: 12px;
            background: linear-gradient(135deg, #ffffff 0%, #faf7ff 100%);
        }
        .ai-prompt-panel.is-visible {
            display: block;
        }
        .ai-prompt-heading {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 8px;
            color: #4c1d95;
            font-weight: 700;
            font-size: 0.98rem;
        }
        .ai-prompt-heading-icon {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 36px;
            height: 36px;
            border-radius: 10px;
            background: #ede9fe;
            color: #7c3aed;
            font-size: 1.15rem;
        }
        .ai-prompt-textarea {
            min-height: 90px;
            resize: vertical;
            border: 1px solid #c4b5fd;
            border-radius: 9px;
            padding: 12px 14px;
            font-size: 0.9rem;
            line-height: 1.55;
            box-shadow: none;
        }
        .ai-prompt-textarea:focus {
            border-color: #8b5cf6;
            box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.13);
        }
        .ai-prompt-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;
            margin-top: 10px;
            color: #64748b;
            font-size: 0.75rem;
        }
        .ai-prompt-examples {
            display: flex;
            flex-wrap: wrap;
            gap: 7px;
            margin-top: 10px;
        }
        .ai-prompt-example {
            border: 1px solid #ddd6fe;
            border-radius: 999px;
            background: #ffffff;
            color: #6d28d9;
            padding: 5px 10px;
            font-size: 0.75rem;
            cursor: pointer;
        }
        .ai-prompt-example:hover {
            background: #f3e8ff;
            border-color: #c4b5fd;
        }
        @media (max-width: 1200px) {
            .category-card-grid { flex-wrap: wrap; gap: 10px; }
            .category-card { flex: 1 1 calc(50% - 8px); }
        }
        @media (max-width: 767px) {
            .category-card { flex-basis: 100%; }
            .ai-prompt-footer { align-items: flex-start; flex-direction: column; }
        }

        /* Tabulator - Clean White Tabulator Theme for Data Preview */
        #previewTableGrid.tabulator {
            font-size: 0.8rem;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            background-color: #ffffff;
        }
        #previewTableGrid .tabulator-header {
            background-color: #f8fafc !important;
            color: #1e293b !important;
            font-weight: 700;
            font-size: 0.8rem;
            border-bottom: 2px solid #cbd5e1 !important;
        }
        #previewTableGrid .tabulator-header .tabulator-col {
            background-color: #f8fafc !important;
            color: #1e293b !important;
            border-right: 1px solid #e2e8f0 !important;
        }
        #previewTableGrid .tabulator-row {
            background-color: #ffffff;
            border-bottom: 1px solid #f1f5f9;
            color: #334155;
            font-size: 0.78rem;
        }
        #previewTableGrid .tabulator-row.tabulator-row-even {
            background-color: #f8fafc;
        }
        #previewTableGrid .tabulator-row:hover {
            background-color: #eff6ff !important;
        }
        #previewTableGrid .tabulator-cell {
            padding: 6px 10px;
            border-right: 1px solid #f1f5f9;
        }
        #previewTableGrid .tabulator-footer {
            background-color: #ffffff;
            border-top: 1px solid #e2e8f0;
            font-size: 0.8rem;
            padding: 6px 10px;
        }
        #previewTableGrid .tabulator-page {
            border-radius: 4px;
            font-size: 0.78rem;
            padding: 3px 8px;
        }
        #previewTableGrid .tabulator-page.active {
            background: #2563eb;
            color: #ffffff;
        }
        /*Tabulator ends */

        /* Dual-List Selector */
        .duallist-container {
            display: flex;
            gap: 15px;
            align-items: center;
            margin-top: 15px;
        }
        .duallist-box {
            flex: 1;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            background: #fafafa;
            height: 260px;
            display: flex;
            flex-direction: column;
        }
        .duallist-header {
            padding: 10px 14px;
            background: #f1f5f9;
            border-bottom: 1px solid #cbd5e1;
            font-weight: 700;
            font-size: 0.85rem;
            color: #334155;
            display: flex;
            justify-content: space-between;
        }
        .duallist-list {
            flex: 1;
            overflow-y: auto;
            padding: 8px;
        }
        .duallist-item {
            padding: 8px 12px;
            margin-bottom: 4px;
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            cursor: pointer;
            font-size: 0.85rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .duallist-item:hover {
            background: #f8fafc;
            border-color: #cbd5e1;
        }
        .duallist-controls {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        .duallist-btn {
            width: 36px;
            height: 36px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            background: #ffffff;
            font-weight: 700;
            cursor: pointer;
        }
        .duallist-btn:hover {
            background: #f1f5f9;
        }

        /* Right Sidebar Controls */
        .filter-label {
            font-size: 0.82rem;
            font-weight: 700;
            color: #475569;
            margin-bottom: 4px;
        }
        .filter-control {
            margin-bottom: 12px;
        }

        /* Export Action Card & Gradient Button */
        .export-action-card {
            background: #ffffff;
            color: #1e1d1d;
            border-radius: 10px;
            padding: 18px;
            margin-top: 15px;

            /* 🛠️ Added: Perfect Flexbox Centering Stack */
            display: flex;
            flex-direction: column;   /* Stacks everything vertically */
            align-items: center;      /* Centers the title, summary box, and button horizontally */
            text-align: center;       /* Fallback text alignment centering */
        }
        .export-action-card .icon-ready {
            margin-bottom: 8px;

            /* 🛠️ Rounded Badge Magic */
            display: flex;
            align-items: center;
            justify-content: center;
            width: 64px;
            height: 64px;
            border-radius: 100%;
            background: #f1f5f9;
            border: 1px solid #e2e8f0;
            flex-shrink: 0;
            transition: all 0.2s ease-in-out;
        }
        .export-gradient-btn {
            width: 100%;
            padding: 8px 8px;
            border-radius: 8px;
            border: none;
            background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
            color: #ffffff;
            font-weight: 700;
            font-size: 0.9rem;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
            transition: transform 0.1s ease;
        }
        .export-gradient-btn:hover {
            transform: translateY(-1px);
        }

        /* Compact Export Modal Dialog */
        .modal-compact {
            max-width: 800px !important;
        }
        .modal-header-bevel {
            background: linear-gradient(to bottom, #ffffff 0%, #f0f2f5 100%);
        }
        .modal-panel-left {
            border-right: 1px solid #e2e8f0;
            padding-right: 20px;
        }
        .format-option-card {
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 10px 12px;
            margin-bottom: 8px;
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .format-option-card img {
            width: 28px;
            height: 32px;
            object-fit: contain;
            flex-shrink: 0; /* Prevents image from shrinking in flexbox */
        }
        .format-option-card:hover {
            border-color: #3b82f6;
            background: #f8fafc;
        }
        .format-option-card.selected {
            border-color: #2563eb;
            background: #eff6ff;
        }
        .format-option-title {
            font-weight: 700;
            font-size: 0.88rem;
            line-height: 1.1;
            margin-bottom: 6px;
        }
        .format-option-desc {
            font-size: 0.72rem;
            color: #64748b;
            line-height: 1.1;
            margin-top: 0;
        }
        .privacy-card-box {
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 12px;
            margin-bottom: 8px;
            background: #fafafa;
        }
        .privacy-card-box .fselect {
            margin-bottom: 5px;
            margin-left: -5px;
        }
        .privacy-card-title {
            font-weight: 700;
            font-size: 0.80rem;
            color: #1e293b;
            margin-top: -4px;
            margin-bottom: 5px;
        }
    </style>
</head>
<body>

<div class="nav" style="margin-bottom: 22px;">

</div>

<div class="content scaffold-list" role="main">

    <!-- STEP PROGRESS INDICATOR -->
    <div class="card-custom breadcrumb-header-card stepper-container w-auto mx-4 w-100 align-items-center">

        <div class="d-flex justify-content-between" style="width: 75%; padding: 0 10%;">
            <div class="step-item" id="stepIndicator1">
                <span class="step-number">1</span>
                <g:message code="dataExport.step1.short" default="Select Dataset"/>
            </div>
            <div class="step-item" id="stepIndicator2">
                <span class="step-number">2</span>
                <g:message code="dataExport.step2.short" default="Select Columns"/>
            </div>
            <div class="step-item active" id="stepIndicator3">
                <span class="step-number">3</span>
                <g:message code="dataExport.step3.short" default="Preview & Export"/>
            </div>
        </div>

        <div class="d-flex justify-content-end" style="width: 25%;">
            <g:link action="history" class="btn btn-outline-primary">
                <i class="fa-solid fa-arrow-left me-1"></i> <g:message code="dataExport.historyLink" />
            </g:link>
        </div>

    </div>

    <div id="exportForm2">
    <g:form action="download" method="POST">
        <input type="hidden" name="datasetType" id="datasetTypeInput" value="REGULAR_TABLE"/>
        <input type="hidden" name="datasetName" id="datasetNameInput" value="member"/>
        <input type="hidden" name="format" id="formatInput" value="CSV"/>
        <input type="hidden" name="aiPromptMode" id="aiPromptModeInput" value="false"/>
        <input type="hidden" name="generatedSql" id="generatedSqlInput" value=""/>
        <input type="hidden" name="sqlParamValues" id="sqlParamValuesInput" value=""/>

        <div class="workspace-row">
            <!-- LEFT SECTION (~75% WIDTH) -->
            <div class="left-workspace">

                <!-- SECTION 1: DATASET SELECTION -->
                <div class="enterprise-card">
                    <div class="enterprise-card-title">
                        <span><g:message code="dataExport.step1.categoryTitle" default="Select Dataset Category"/></span>
                    </div>

                    <div class="category-card-grid">
                        <!-- Card 1: Raw Domain Tables -->
                        <div class="category-card selected" id="cardRawTables" onclick="selectCategory('REGULAR_TABLE', 'cardRawTables')">
                            <div class="checkmark-badge">✓</div>
                            <div class="icon-box"><asset:image src="skin/data_export_category_tables.png" alt="Tables" width="30" height="32"/></div>
                            <div class="card-content">
                                <div class="card-title"><g:message code="dataExport.step1.finalTables" /></div>
                                <div class="card-desc"><g:message code="dataExport.step1.rawTablesDesc" /></div>
                            </div>
                        </div>

                        <!-- Card 2: Pre-Joined DSS Views -->
                        <div class="category-card" id="cardPrejoined" onclick="selectCategory('PREJOINED_DSS', 'cardPrejoined')">
                            <div class="checkmark-badge">✓</div>
                            <div class="icon-box"><asset:image src="skin/data_export_category_joined_views.png" alt="Joined Views" width="30" height="32"/></div>
                            <div class="card-content">
                                <div class="card-title"><g:message code="dataExport.step1.prejoinedViews" /></div>
                                <div class="card-desc"><g:message code="dataExport.step1.prejoinedDesc" /></div>
                            </div>
                        </div>

                        <!-- Card 3: Dynamic Form Extensions -->
                        <div class="category-card" id="cardDynamicForms" onclick="selectCategory('DYNAMIC_FORM', 'cardDynamicForms')">
                            <div class="checkmark-badge">✓</div>
                            <div class="icon-box"><asset:image src="skin/data_export_category_forms.png" alt="Dynamic Forms" width="30" height="32"/></div>
                            <div class="card-content">
                                <div class="card-title"><g:message code="dataExport.step1.dynamicForms" /></div>
                                <div class="card-desc"><g:message code="dataExport.step1.dynamicFormsDesc" /></div>
                            </div>
                        </div>

                        <!-- Card 4: AI Prompt -->
                        <div class="category-card category-card-ai" id="cardAIPrompt" onclick="selectCategory('AI_PROMPT', 'cardAIPrompt')" role="button" tabindex="0" aria-controls="aiPromptPanel" aria-label="Use AI Prompt to describe the dataset in natural language">
                            <div class="checkmark-badge">✓</div>
                            <div class="icon-box"><asset:image src="skin/data_export_category_ai_prompt.png" alt="AI Prompt" width="30" height="32"/></div>
                            <div class="card-content">
                                <div class="card-title">AI Prompt <span class="ai-option-pill">AI</span></div>
                                <div class="card-desc">Export data using natural language query (powered with AI).</div>
                            </div>
                        </div>
                    </div>

                    <!-- AI PROMPT INPUT: shown only when the AI Prompt option is selected -->
                    <div id="aiPromptPanel" class="ai-prompt-panel" aria-hidden="true">
                        <div class="ai-prompt-heading">
                            <span class="ai-prompt-heading-icon">
                                <i class="fa-solid fa-wand-magic-sparkles" aria-hidden="true"></i>
                                <asset:image src="skin/data_export_category_ai_prompt.png" alt="AI Prompt" width="22" height="22"/>
                            </span>
                            <div>
                                <div style="margin-bottom: -4px;">Describe what data you want to export</div>
                                <div style="font-size:0.76rem; font-weight:500; color:#7c6b99;">Write your request in natural language — no SQL required | Be specific about the population, fields, date range, filters, and privacy requirements.</div>
                            </div>
                        </div>
                        <textarea id="aiPromptText" name="aiPrompt" class="form-control ai-prompt-textarea" maxlength="5000"
                                  placeholder="Example: Export all active residents in Maputo Province who are 18–49 years old, including their household code, sex, age, residency status, and village. Exclude names and phone numbers."
                                  oninput="updateAIPromptCounter()" aria-describedby="aiPromptHelp"></textarea>

                        <!-- TRANSLATION ACTION BUTTON -->
                        <div style="margin-top: 15px; display: flex; gap: 10px; align-items: center;">
                            <button type="button" id="btnGenerateSql" class="btn btn-primary" onclick="generateSqlFromPrompt()">
                                ✨ <g:message code="dataExport.ai.generateSql" default="Generate SQL Query"/>
                            </button>
                        </div>

                        <!-- AI TRANSLATION RESULT PANEL -->
                        <div id="aiResultPanel" style="display: none; margin-top: 18px; padding: 16px; border: 1px solid #c4b5fd; border-radius: 10px; background: #fdfcff;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                                <span class="badge bg-success" style="font-size: 0.78rem; padding: 6px 10px;">
                                    🛡️ <g:message code="dataExport.ai.readOnlyValidated" default="Read-Only Validated"/>
                                </span>
                                <button type="button" class="btn btn-sm btn-outline-secondary" onclick="copyGeneratedSql()">
                                    📋 <g:message code="dataExport.ai.copySql" default="Copy SQL"/>
                                </button>
                            </div>

                            <!-- Logic Explanation -->
                            <div style="font-size: 0.85rem; color: #4c1d95; margin-bottom: 8px;" id="aiLogicExplanation"></div>

                            <!-- Read-Only Code Box -->
                            <pre id="aiSqlCodeBox" style="background: #1e293b; color: #38bdf8; padding: 12px 16px; border-radius: 8px; font-family: monospace; font-size: 0.82rem; white-space: pre-wrap; word-break: break-all; margin-bottom: 0;"></pre>
                        </div>

                        <div class="alert alert-light border mt-3 mb-0 py-2" style="font-size:0.78rem; color:#6b5b83;">
                            <i class="fa-solid fa-shield-halved me-1" aria-hidden="true"></i>
                            Review the generated dataset and privacy settings before exporting. The natural-language request will be submitted with the export form.
                        </div>
                    </div>

                    <!-- SEARCHABLE DROPDOWN + VIEW DESCRIPTION -->
                    <div id="datasetSelectorControls" style="display: flex; gap: 15px; align-items: center;">
                        <div style="flex: 1;" id="selectContainerRaw">
                            <label class="filter-label"><g:message code="dataExport.step1.selectTable" default="Select Table:"/></label>
                            <select id="regularTableSelect" class="form-control" onchange="onDatasetNameChanged(this.value)">
                                <g:each in="${tables}" var="t">
                                    <option value="${t.tableName}">${t.domainName} (${t.tableName})</option>
                                </g:each>
                            </select>
                        </div>

                        <div style="flex: 1; display: none;" id="selectContainerPrejoined">
                            <label class="filter-label"><g:message code="dataExport.step1.selectPrejoined" default="Select Analytical View:"/></label>
                            <select id="prejoinedSelect" class="form-control" onchange="onDatasetNameChanged(this.value)">
                                <g:each in="${prejoinedViews}" var="v">
                                    <option value="${v.code}"><g:message code="${v.name}" default="${v.code}"/></option>
                                </g:each>
                            </select>
                        </div>

                        <div style="flex: 1; display: none;" id="selectContainerForms">
                            <label class="filter-label"><g:message code="dataExport.step1.selectDynamicForm" default="Select Dynamic XLS HForm:"/></label>
                            <select id="dynamicFormSelect" class="form-control" onchange="onDatasetNameChanged(this.value)">
                                <g:each in="${customForms}" var="f">
                                    <option value="${f.extFormId}"><g:message code="${f.formName}" default="${f.formName}"/> (${f.extFormId})</option>
                                </g:each>
                            </select>
                        </div>

                        <div style="padding-top: 28px;">
                            <button type="button" class="btn btn-outline-secondary" onclick="viewDatasetDescription()">
                                ℹ️ <g:message code="dataExport.viewDescription" />
                            </button>
                        </div>
                    </div>
                </div>

                <!-- SECTION 2: COLUMN SELECTION (DUAL-LIST SELECTOR) -->
                <div class="enterprise-card" id="columnSelectionCard">
                    <div class="enterprise-card-title">
                        <span><g:message code="dataExport.step2.selectedColumns" default="Selected Columns"/></span>
                        <span style="font-size: 0.85rem; font-weight: 500; color: #64748b;" id="selectedCountBadge"><g:message code="dataExport.step2.selectedColumns" default="Selected Columns"/> (0)</span>
                    </div>

                    <!-- Top Toolbar -->
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                        <input type="text" id="columnSearchInput" class="form-control" style="max-width: 300px;" placeholder="🔍 Search Columns..." onkeyup="filterAvailableColumns(this.value)"/>
                        <div>
                            <button type="button" class="btn btn-sm btn-outline-secondary" onclick="clearAllColumns()"><g:message code="default.button.clearAll" default="Clear All"/></button>
                            <button type="button" class="btn btn-sm btn-outline-primary" onclick="saveColumnSet()"><g:message code="dataExport.button.saveColumnSet" default="Save Column Set"/></button>
                        </div>
                    </div>

                    <!-- Dual List Selector -->
                    <div class="duallist-container">
                        <!-- Left Panel: Available Columns -->
                        <div class="duallist-box">
                            <div class="duallist-header">
                                <span><g:message code="dataExport.step2.availableColumns" default="Available Columns"/></span>
                                <span id="availCount">0</span>
                            </div>
                            <div class="duallist-list" id="availableColumnsList">
                                <!-- Populated dynamically via JS -->
                            </div>
                        </div>

                        <!-- Center Controls -->
                        <div class="duallist-controls">
                            <button type="button" class="duallist-btn" onclick="moveSelected(true)">&gt;</button>
                            <button type="button" class="duallist-btn" onclick="moveSelected(false)">&lt;</button>
                            <button type="button" class="duallist-btn" onclick="moveAll(true)">&gt;&gt;</button>
                            <button type="button" class="duallist-btn" onclick="moveAll(false)">&lt;&lt;</button>
                        </div>

                        <!-- Right Panel: Selected Columns -->
                        <div class="duallist-box">
                            <div class="duallist-header">
                                <span><g:message code="dataExport.step2.selectedColumns" default="Selected Columns"/></span>
                                <span id="selectedCount">0</span>
                            </div>
                            <div class="duallist-list" id="selectedColumnsList">
                                <!-- Populated dynamically via JS -->
                            </div>
                        </div>
                    </div>
                </div>

                <!-- SECTION 3: DATA PREVIEW -->
                <div class="enterprise-card">
                    <div class="enterprise-card-title">
                        <span><g:message code="dataExport.step4.livePreview" default="Data Preview (First 10 Records)"/></span>
                        <button type="button" class="btn btn-sm btn-info" onclick="fetchPreview()">🔄 <g:message code="dataExport.preview.refresh" default="Refresh Preview"/></button>
                    </div>

                    <div style="font-size: 0.85rem; color: #475569; margin-bottom: 10px; font-weight: 600;" id="previewMetrics">
                        <g:message code="dataExport.preview.records" default="Records:"/> <span id="metricRecords">0</span> | <g:message code="dataExport.step2.selectedColumns" default="Selected Columns"/>: <span id="metricCols">0</span>
                    </div>

                    <div id="previewTableGrid"></div>
                </div>

            </div>

            <!-- RIGHT STICKY SIDEBAR (~25% WIDTH) -->
            <div class="right-sidebar">
                <div class="enterprise-card" id="filtersCard">
                    <div style="font-size: 0.95rem; font-weight: 700; color: #1e293b; margin-bottom: 15px; border-bottom: 1px solid #cbd5e1; padding-bottom: 8px;">
                        🎯 <g:message code="dataExport.filter.scopeTitle" default="Cohort Scope & Spatial Filters"/>
                    </div>

                    <!-- Baseline Date -->
                    <div class="filter-control">
                        <div class="filter-label">📅 <g:message code="dataExport.filter.referenceDate" default="Reference Date:"/></div>
                        <input type="date" name="referenceDate" class="form-control form-control-sm" value="${java.time.LocalDate.now()}"/>
                    </div>

                    <!-- Spatial Scope -->
                    <div class="filter-control" id="regionControl">
                        <div class="filter-label">📍 <g:message code="dataExport.filter.region" default="Region / Zone:"/></div>
                        <select name="regionCode" class="form-control form-control-sm">
                            <option value=""><g:message code="dataExport.filter.allRegions" default="All Study Regions"/></option>
                            <g:each in="${regions}" var="r">
                                <option value="${r.code}">${r.code} - ${r.name}</option>
                            </g:each>
                        </select>
                    </div>

                    <!-- Gender Cohort -->
                    <div class="filter-control" id="genderControl">
                        <div class="filter-label">👥 <g:message code="dataExport.filter.gender" default="Gender Cohort:"/></div>
                        <select name="gender" class="form-control form-control-sm">
                            <option value="ALL"><g:message code="dataExport.filter.bothGenders" default="Both Genders"/></option>
                            <option value="M"><g:message code="dataExport.filter.maleOnly" default="Male Only"/></option>
                            <option value="F"><g:message code="dataExport.filter.femaleOnly" default="Female Only"/></option>
                        </select>
                    </div>

                    <!-- Age Range -->
                    <div class="filter-control" id="ageRangeControl">
                        <div class="filter-label">🎂 <g:message code="dataExport.filter.ageRange" default="Age Range (Years):"/></div>
                        <div style="display: flex; gap: 6px;">
                            <input type="number" name="ageMin" class="form-control form-control-sm" placeholder="Min" min="0"/>
                            <input type="number" name="ageMax" class="form-control form-control-sm" placeholder="Max" min="1"/>
                        </div>
                    </div>

                    <!-- Random Sub-sampling -->
                    <div class="filter-control">
                        <div class="filter-label">🎲 <g:message code="dataExport.filter.sampling" default="Sub-sampling (%):"/></div>
                        <input type="number" name="randomSamplePercent" class="form-control form-control-sm" value="100.0" min="1" max="100"/>
                    </div>

                    <!-- Active Residents Toggle -->
                    <div class="filter-control" style="margin-top: 10px;">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" name="activeResidentsOnly" value="true" checked="checked" id="activeResidentsSwitch"/>
                            <label class="form-check-label filter-label" for="activeResidentsSwitch"><g:message code="dataExport.filter.activeResidents" default="Active Residents Only"/></label>
                        </div>
                    </div>
                </div>

                <!-- EXPORT ACTION CARD -->
                <div class="export-action-card">
                    <div class="icon-ready"><asset:image src="skin/data_export_ready_icon.png" alt="Tables" width="36" height="36" /></div>
                    <div style="font-weight: 700; font-size: 0.9rem; margin-bottom: 4px;"><g:message code="dataExport.ready.title" default="Ready to Export"/></div>
                    <div style="font-size: 0.75rem; color: #69708f; margin-bottom: 12px; line-height: 1.4; max-width: 200px;">
                        <g:message code="dataExport.ready.desc" default="Configure your export settings and click the button below to continue."/>
                    </div>

                    <button type="button" class="export-gradient-btn" onclick="openExportModal()">
                        <asset:image src="skin/data_export_download.png" alt="Download" width="14" height="14" style="vertical-align: middle; margin-right: 6px;"/>
                        <g:message code="dataExport.button.export" default="Export Dataset"/>
                    </button>
                </div>
            </div>
        </div>

        <!-- EXPORT MODAL DIALOG (COMPACT) -->
        <div class="modal fade" id="exportModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-compact modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header border-bottom border-2 shadow-sm modal-header-bevel" >
                        <h5 class="modal-title">🚀 <g:message code="dataExport.modal.title" default="Export Dataset & Privacy Configuration"/></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>

                    <div class="modal-body" style="padding: 20px;">
                        <div class="row">
                            <!-- LEFT PANEL: EXPORT FORMAT -->
                            <div class="col-md-5 modal-panel-left">
                                <h6 style="font-weight: 700; color: #1e293b; margin-bottom: 12px;"><g:message code="dataExport.modal.format" default="Export Format"/></h6>

                                <g:each in="${exportFormats}" var="fmt" status="idx">
                                    <div class="format-option-card ${idx == 0 ? 'selected' : ''}" onclick="selectFormatOption('${fmt.code}', this)">
                                        <input type="radio" name="modalFormat" value="${fmt.code}" ${idx == 0 ? 'checked="checked"' : ''}/>
                                        <asset:image src="skin/${fmt.icon}" alt="${fmt.code}"/>
                                        <div>
                                            <div class="format-option-title"><g:message code="${fmt.name}" default="${fmt.title}"/></div>
                                            <div class="format-option-desc"><g:message code="${fmt.description}" default="${fmt.desc}"/></div>
                                        </div>
                                    </div>
                                </g:each>

                                <div style="margin-top: 10px;">
                                    <label style="font-size: 0.85rem;"><input type="checkbox" name="includeDictionary" value="true" checked="checked"/> <g:message code="dataExport.modal.attachDict" default="Attach Data Dictionary"/></label>
                                    <br>
                                    <label style="font-size: 0.85rem;"><input type="checkbox" name="includeMediaFiles" value="true" /> <g:message code="dataExport.modal.attachMedia" default="Attach Media Files"/></label>
                                </div>
                            </div>

                            <!-- RIGHT PANEL: SENSITIVE DATA CONTROLS (STACKED STYLED CARDS) -->
                            <div class="col-md-7" style="padding-left: 15px;">
                                <h6 style="font-weight: 700; color: #1e293b; margin-bottom: 12px;"><g:message code="dataExport.modal.privacyTitle" default="Sensitive Data Controls"/></h6>

                                <div style="max-height: 460px; overflow-y: auto; padding-right: 5px;">
                                    <!-- 1. Names Control -->
                                    <div class="privacy-card-box">
                                        <div class="privacy-card-title">👤 <g:message code="dataExport.privacy.names" default="Direct Identifiers (Names)"/></div>
                                        <div class="form-check fselect">
                                            <select name="nameAnonymizationMode" class="form-select form-select-sm">
                                                <g:each in="${nameModes}" var="m">
                                                    <option value="${m.code}"><g:message code="${m.name}" default="${m.code}"/></option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>

                                    <!-- 2. Phone Control -->
                                    <div class="privacy-card-box">
                                        <div class="privacy-card-title">📞 <g:message code="dataExport.privacy.phones" default="Contact Information (Phone)"/></div>
                                        <div class="form-check fselect">
                                            <select name="phoneAnonymizationMode" class="form-select form-select-sm">
                                                <g:each in="${phoneModes}" var="m">
                                                    <option value="${m.code}"><g:message code="${m.name}" default="${m.code}"/></option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>

                                    <!-- 3. GPS Control -->
                                    <div class="privacy-card-box">
                                        <div class="privacy-card-title">📍 <g:message code="dataExport.privacy.gps" default="Spatial & GPS Precision"/></div>
                                        <div class="form-check fselect">
                                            <select name="gpsAnonymizationMode" class="form-select form-select-sm">
                                                <g:each in="${gpsModes}" var="m">
                                                    <option value="${m.code}"><g:message code="${m.name}" default="${m.code}"/></option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>

                                    <!-- 4. Date Control -->
                                    <div class="privacy-card-box">
                                        <div class="privacy-card-title">📅 <g:message code="dataExport.privacy.dates" default="Temporal & Date Granularity"/></div>
                                        <div class="form-check fselect">
                                            <select name="dateAnonymizationMode" class="form-select form-select-sm">
                                                <g:each in="${dateModes}" var="m">
                                                    <option value="${m.code}"><g:message code="${m.name}" default="${m.code}"/></option>
                                                </g:each>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="modal-footer" style="background: #f8fafc; padding: 12px 20px;">
                        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal"><g:message code="default.button.cancel" default="Cancel"/></button>
                        <button type="submit" class="btn btn-success btn-sm font-weight-bold"><g:message code="dataExport.modal.confirm" default="Confirm & Download Export"/></button>
                    </div>
                </div>
            </div>
        </div>

        <!-- BOOTSTRAP ALERT / NOTIFICATION MODAL DIALOG -->
        <div class="modal fade" id="aiAlertModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header border-bottom shadow-sm py-2">
                        <h5 class="modal-title" id="aiAlertModalTitle">⚠️ Notice</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body" id="aiAlertModalBody" style="padding: 20px; font-size: 0.95rem; line-height: 1.5; color: #334155;">
                    </div>
                    <div class="modal-footer py-2">
                        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">OK</button>
                    </div>
                </div>
            </div>
        </div>
    </g:form>
    </div>
</div>

<g:javascript>
    var availableCols = [];
    var selectedCols = [];
    var tabulatorGrid = null;

    var MEMBER_FILTER_TABLES = ['member', 'marital_relationship', 'enumeration', 'in_migration', 'out_migration', 'death', 'pregnancy_registration', 'pregnancy_outcome', 'pregnancy_child', 'pregnancy_visit', 'pregnancy_visit_child', 'residency', 'head_relationship',
                                'member_ext', 'marital_relationship_ext', 'in_migration_ext', 'out_migration_ext', 'death_ext', 'pregnancy_registration_ext', 'pregnancy_outcome_ext', 'pregnancy_child_ext', 'pregnancy_visit_ext', 'pregnancy_visit_child_ext'];
    var ACTIVE_RESIDENTS_TABLES = ['member', 'member_ext', 'residency', 'head_relationship', 'marital_relationship'];

    function updateFilterControls(catType, name) {
        var isAIPrompt = catType === 'AI_PROMPT';
        var filtersCard = document.getElementById('filtersCard');

        if (isAIPrompt) {
            if (filtersCard) {
                filtersCard.style.opacity = '0.45';
                filtersCard.style.pointerEvents = 'none';
                filtersCard.querySelectorAll('input, select').forEach(function(el) { el.disabled = true; });
            }
            return;
        }

        if (filtersCard) {
            filtersCard.style.opacity = '1';
            filtersCard.style.pointerEvents = 'auto';
            filtersCard.querySelectorAll('input, select').forEach(function(el) { el.disabled = false; });
        }

        var isMemberBased = MEMBER_FILTER_TABLES.includes(name) || catType === 'PREJOINED_DSS';
        toggleFilterControl('genderControl', 'gender', isMemberBased);
        toggleFilterControl('ageRangeControl', ['ageMin', 'ageMax'], isMemberBased);

        var isRegionSupported = (name !== 'round');
        toggleFilterControl('regionControl', 'regionCode', isRegionSupported);

        var isResidencySupported = (catType === 'REGULAR_TABLE') && ACTIVE_RESIDENTS_TABLES.includes(name);
        var switchEl = document.getElementById('activeResidentsSwitch');
        var switchContainer = switchEl ? switchEl.closest('.filter-control') : null;
        if (switchEl && switchContainer) {
            switchEl.disabled = !isResidencySupported;
            if (!isResidencySupported) switchEl.checked = false;
            switchContainer.style.opacity = isResidencySupported ? '1' : '0.4';
            switchContainer.style.pointerEvents = isResidencySupported ? 'auto' : 'none';
        }
    }

    function toggleFilterControl(controlId, inputNames, isEnabled) {
        var container = document.getElementById(controlId);
        if (!container) return;

        container.style.opacity = isEnabled ? '1' : '0.4';
        container.style.pointerEvents = isEnabled ? 'auto' : 'none';

        var names = Array.isArray(inputNames) ? inputNames : [inputNames];
        names.forEach(function(n) {
            var el = document.querySelector('[name="' + n + '"]');
            if (el) el.disabled = !isEnabled;
        });
    }

    function selectCategory(catType, cardId) {
        document.querySelectorAll('.category-card').forEach(c => c.classList.remove('selected'));
        document.getElementById(cardId).classList.add('selected');
        document.getElementById('datasetTypeInput').value = catType;

        var isAIPrompt = catType === 'AI_PROMPT';
        document.getElementById('selectContainerRaw').style.display = (!isAIPrompt && catType === 'REGULAR_TABLE') ? 'block' : 'none';
        document.getElementById('selectContainerPrejoined').style.display = (!isAIPrompt && catType === 'PREJOINED_DSS') ? 'block' : 'none';
        document.getElementById('selectContainerForms').style.display = (!isAIPrompt && catType === 'DYNAMIC_FORM') ? 'block' : 'none';
        document.getElementById('datasetSelectorControls').style.display = isAIPrompt ? 'none' : 'flex';
        document.getElementById('aiPromptPanel').classList.toggle('is-visible', isAIPrompt);
        document.getElementById('aiPromptPanel').setAttribute('aria-hidden', isAIPrompt ? 'false' : 'true');
        document.getElementById('aiPromptModeInput').value = isAIPrompt ? 'true' : 'false';

        var hasGeneratedSql = document.getElementById('generatedSqlInput').value.trim().length > 0;
        document.getElementById('columnSelectionCard').style.display = (isAIPrompt && !hasGeneratedSql) ? 'none' : '';

        if (isAIPrompt) {
            document.getElementById('datasetNameInput').value = 'AI_PROMPT';
            updateFilterControls('AI_PROMPT', 'AI_PROMPT');
            document.getElementById('aiPromptText').focus();
            updateAIPromptCounter();
            return;
        }

        var name = getActiveDatasetName();
        onDatasetNameChanged(name);
    }

    function showAiModalAlert(title, message, isError) {
        var titleEl = document.getElementById('aiAlertModalTitle');
        var bodyEl = document.getElementById('aiAlertModalBody');

        if (titleEl) {
            titleEl.innerHTML = isError ?
                '<span class="text-danger fw-bold"><i class="fa-solid fa-circle-exclamation me-2"></i>' + (title || 'AI Translation Error') + '</span>' :
                '<span class="text-primary fw-bold"><i class="fa-solid fa-circle-info me-2"></i>' + (title || 'Notification') + '</span>';
        }

        if (bodyEl) {
            var alertClass = isError ? 'alert-danger border-danger-subtle' : 'alert-info border-info-subtle';
            bodyEl.innerHTML =
                '<div class="alert ' + alertClass + ' mb-0 shadow-sm" style="border-radius: 10px; padding: 16px 18px; font-size: 0.92rem; line-height: 1.6;">' +
                    message +
                '</div>';
        }

        var modalEl = document.getElementById('aiAlertModal');
        if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
            var modal = bootstrap.Modal.getOrCreateInstance(modalEl);
            modal.show();
        } else if (typeof $ !== 'undefined' && $(modalEl).modal) {
            $(modalEl).modal('show');
        } else {
            alert(message);
        }
    }

    function generateSqlFromPrompt() {
        var promptText = document.getElementById('aiPromptText').value.trim();
        if (!promptText) {
            showAiModalAlert('⚠️ Prompt Required', 'Please enter a data request prompt.', true);
            document.getElementById('aiPromptText').focus();
            return;
        }

        // Clear and hide previous result panel and hidden SQL inputs
        document.getElementById('aiResultPanel').style.display = 'none';
        document.getElementById('aiLogicExplanation').innerText = '';
        document.getElementById('aiSqlCodeBox').innerText = '';
        document.getElementById('generatedSqlInput').value = '';
        document.getElementById('sqlParamValuesInput').value = '';

        var btn = document.getElementById('btnGenerateSql');
        var originalBtnHtml = btn.innerHTML;
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Translating request to SQL...';

        var formData = new FormData();
        formData.append('aiPrompt', promptText);
        var refDateEl = document.querySelector('input[name="referenceDate"]');
        if (refDateEl) formData.append('referenceDate', refDateEl.value);
        var regCodeEl = document.querySelector('select[name="regionCode"]');
        if (regCodeEl) formData.append('regionCode', regCodeEl.value);

        fetch("${createLink(controller: 'dataExport', action: 'translatePrompt')}", {
            method: 'POST',
            headers: { 'X-Requested-With': 'XMLHttpRequest' },
            body: formData,
            signal: (typeof AbortSignal !== 'undefined' && AbortSignal.timeout) ? AbortSignal.timeout(180000) : null
        })
        .then(r => r.json())
        .then(data => {
            btn.disabled = false;
            btn.innerHTML = originalBtnHtml;

            if (data && data.success && data.sql) {
                document.getElementById('generatedSqlInput').value = data.sql;
                document.getElementById('sqlParamValuesInput').value = JSON.stringify(data.paramValues || []);
                document.getElementById('aiLogicExplanation').innerText = data.explanation || 'SQL query generated successfully based on your prompt.';
                document.getElementById('aiSqlCodeBox').innerText = data.sql;
                document.getElementById('aiResultPanel').style.display = 'block';
                document.getElementById('columnSelectionCard').style.display = 'block';

                if (data.columns && data.columns.length > 0) {
                    availableCols = data.columns;
                    selectedCols = [...data.columns];
                    renderDualLists();
                }

                fetchPreview();
            } else {
                showAiModalAlert('⚠️ Translation Failed', data.message || 'Failed translating prompt to SQL. Please refine your request and try again.', true);
            }
        })
        .catch(err => {
            btn.disabled = false;
            btn.innerHTML = originalBtnHtml;
            showAiModalAlert('⚠️ Translation Error', 'An error occurred during AI prompt translation: ' + err.message, true);
        });
    }

    function copyGeneratedSql() {
        var sql = document.getElementById('generatedSqlInput').value;
        if (sql) {
            navigator.clipboard.writeText(sql).then(() => {
                showAiModalAlert('📋 Copied', 'SQL Query copied to clipboard!', false);
            });
        }
    }

    function getActiveDatasetName() {
        var type = document.getElementById('datasetTypeInput').value;
        if (type === 'REGULAR_TABLE') return document.getElementById('regularTableSelect').value;
        if (type === 'PREJOINED_DSS') return document.getElementById('prejoinedSelect').value;
        if (type === 'DYNAMIC_FORM') return document.getElementById('dynamicFormSelect').value;
        if (type === 'AI_PROMPT') return 'AI_PROMPT';
        return 'member';
    }

    function updateAIPromptCounter() {
        var prompt = document.getElementById('aiPromptText');
        var counter = document.getElementById('aiPromptCharCount');
        if (prompt && counter) counter.innerText = prompt.value.length;
    }

    function useAIPromptExample(example) {
        var prompt = document.getElementById('aiPromptText');
        if (!prompt) return;
        prompt.value = example;
        updateAIPromptCounter();
        prompt.focus();
    }

    var currentDatasetDescription = "Longitudinal surveillance dataset.";
    var currentDatasetCategoryName = "";

    function onDatasetNameChanged(name) {
        document.getElementById('datasetNameInput').value = name;
        var catType = document.getElementById('datasetTypeInput').value;
        if (catType === 'AI_PROMPT') return;
        updateFilterControls(catType, name);

        var url = "${createLink(controller: 'dataExport', action: 'getColumns')}?datasetName=" + name;

        fetch(url, {
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        })
            .then(r => {
                if (r.status === 401 || r.redirected) {
                    window.location.href = "${createLink(controller: 'login', action: 'auth')}";
                    return;
                }
                return r.json();
            })
            .then(data => {
                if (data && data.success) {
                    currentDatasetDescription = data.description || "Longitudinal surveillance dataset.";
                    currentDatasetCategoryName = data.categoryName || document.getElementById('datasetTypeInput').value;
                    if (data.columns) {
                        availableCols = data.columns;
                        selectedCols = [...data.columns];
                        renderDualLists();
                    }
                }
            });
    }

    function renderDualLists() {
        var availList = document.getElementById('availableColumnsList');
        var selList = document.getElementById('selectedColumnsList');

        var filterText = document.getElementById('columnSearchInput').value.toLowerCase();

        var availHtml = '';
        availableCols.forEach(function(c) {
            var isSelected = selectedCols.some(function(s) { return s.columnName === c.columnName; });
            if (!isSelected) {
                var colName = c.columnName || '';
                var colLabel = c.label || '';
                if (filterText === '' || colName.toLowerCase().indexOf(filterText) !== -1 || colLabel.toLowerCase().indexOf(filterText) !== -1) {
                    availHtml += '<div class="duallist-item" onclick="addSingleCol(\'' + colName + '\')"><span>' + colName + '</span> <small class="text-muted">' + colLabel + '</small></div>';
                }
            }
        });
        availList.innerHTML = availHtml;

        var selHtml = '';
        selectedCols.forEach(function(c) {
            var colName = c.columnName || '';
            selHtml += '<div class="duallist-item" onclick="removeSingleCol(\'' + colName + '\')"><span><input type="checkbox" name="selectedColumns" value="' + colName + '" checked="checked" onclick="event.stopPropagation()"/> <b>' + colName + '</b></span> <span>⋮⋮</span></div>';
        });
        selList.innerHTML = selHtml;

        document.getElementById('availCount').innerText = availableCols.length - selectedCols.length;
        document.getElementById('selectedCount').innerText = selectedCols.length + "";
        document.getElementById('selectedCountBadge').innerText = "${g.message(code: 'dataExport.step2.selectedColumns', default: 'Selected Columns')} (" + selectedCols.length + ")";
        document.getElementById('metricCols').innerText = selectedCols.length + "";
    }

    function addSingleCol(colName) {
        var item = availableCols.find(c => c.columnName === colName);
        if (item && !selectedCols.some(s => s.columnName === colName)) {
            selectedCols.push(item);
            renderDualLists();
        }
    }

    function removeSingleCol(colName) {
        selectedCols = selectedCols.filter(c => c.columnName !== colName);
        renderDualLists();
    }

    function moveAll(toRight) {
        selectedCols = toRight ? [...availableCols] : [];
        renderDualLists();
    }

    function clearAllColumns() {
        selectedCols = [];
        renderDualLists();
    }

    function filterAvailableColumns(text) {
        renderDualLists();
    }

    function selectFormatOption(fmt, cardEl) {
        document.querySelectorAll('.format-option-card').forEach(c => c.classList.remove('selected'));
        cardEl.classList.add('selected');
        var radioInput = cardEl.querySelector('input[type="radio"]');
        if (radioInput) {
            radioInput.checked = true;
        }
        if (document.getElementById('formatInput')) {
            document.getElementById('formatInput').value = fmt;
        }
        if (document.getElementById('summaryFmt')) {
            document.getElementById('summaryFmt').innerText = fmt;
        }
    }

    function openExportModal() {
        var isAIPrompt = document.getElementById('datasetTypeInput').value === 'AI_PROMPT';
        if (isAIPrompt) {
            var promptText = document.getElementById('aiPromptText').value.trim();
            if (!promptText) {
                document.getElementById('aiPromptText').focus();
                document.getElementById('aiPromptText').classList.add('is-invalid');
                showAiModalAlert('⚠️ Prompt Required', 'Please describe the dataset you want to export before proceeding.', true);
                return;
            }
            document.getElementById('aiPromptText').classList.remove('is-invalid');
        }
        var el = document.getElementById('exportModal');
        if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
            var modal = bootstrap.Modal.getOrCreateInstance(el);
            modal.show();
        } else if (typeof $ !== 'undefined' && $(el).modal) {
            $(el).modal('show');
        } else {
            el.style.display = 'block';
            el.classList.add('show');
        }
    }

    function fetchPreview() {
        var container = document.getElementById('exportForm2');
        var formData = new FormData();

        container.querySelectorAll('input, select, textarea').forEach(el => {
            if (el.name) {
                if (el.type === 'checkbox' || el.type === 'radio') {
                    if (el.checked) {
                        formData.append(el.name, el.value);
                    }
                } else {
                    formData.append(el.name, el.value);
                }
            }
        });

        fetch("${createLink(controller: 'dataExport', action: 'preview')}", {
            method: 'POST',
            headers: { 'X-Requested-With': 'XMLHttpRequest' },
            body: formData
        })
        .then(r => {
            if (r.status === 401 || r.redirected) {
                window.location.href = "${createLink(controller: 'login', action: 'auth')}";
                return;
            }
            return r.json();
        })
        .then(data => {
            if (data && data.success && data.rows && data.rows.length > 0) {
                document.getElementById('metricRecords').innerText = data.rows.length;
                var cols = Object.keys(data.rows[0]).map(k => ({ title: k, field: k }));
                tabulatorGrid = new Tabulator("#previewTableGrid", {
                    data: data.rows,
                    columns: cols,
                    layout: "fitDataFill",
                    pagination: "local",
                    paginationSize: 10
                });
            } else {
                showAiModalAlert('ℹ️ No Records Found', 'No preview records found matching the current filters.', false);
            }
        });
    }

    function viewDatasetDescription() {
        var name = getActiveDatasetName();
        var categoryName = currentDatasetCategoryName || document.getElementById('datasetTypeInput').value;

        var msgHtml = '<strong>Dataset:</strong> ' + name + '<br><br>' +
                      '<strong>Category:</strong> ' + categoryName + '<br><br>' +
                      '<strong>Description:</strong> ' + currentDatasetDescription;

        showAiModalAlert('Dataset Description: ' + name, msgHtml, false);
    }

    document.addEventListener("DOMContentLoaded", function() {
        onDatasetNameChanged(getActiveDatasetName());
    });
</g:javascript>

</body>
</html>
