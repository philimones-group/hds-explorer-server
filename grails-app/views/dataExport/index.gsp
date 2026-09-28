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

        <div class="workspace-row">
            <!-- LEFT SECTION (~75% WIDTH) -->
            <div class="left-workspace">

                <!-- SECTION 1: DATASET SELECTION -->
                <div class="enterprise-card">
                    <div class="enterprise-card-title">
                        <span>Select Dataset Category</span>
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
                    </div>

                    <!-- SEARCHABLE DROPDOWN + VIEW DESCRIPTION -->
                    <div style="display: flex; gap: 15px; align-items: center;">
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
                <div class="enterprise-card">
                    <div class="enterprise-card-title">
                        <span>Select Columns to Export</span>
                        <span style="font-size: 0.85rem; font-weight: 500; color: #64748b;" id="selectedCountBadge">Selected Columns (0)</span>
                    </div>

                    <!-- Top Toolbar -->
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                        <input type="text" id="columnSearchInput" class="form-control" style="max-width: 300px;" placeholder="🔍 Search Columns..." onkeyup="filterAvailableColumns(this.value)"/>
                        <div>
                            <button type="button" class="btn btn-sm btn-outline-secondary" onclick="clearAllColumns()">Clear All</button>
                            <button type="button" class="btn btn-sm btn-outline-primary" onclick="saveColumnSet()">Save Column Set</button>
                        </div>
                    </div>

                    <!-- Dual List Selector -->
                    <div class="duallist-container">
                        <!-- Left Panel: Available Columns -->
                        <div class="duallist-box">
                            <div class="duallist-header">
                                <span>Available Columns</span>
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
                                <span>Selected Columns</span>
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
                        <span>Data Preview (First 10 Records)</span>
                        <button type="button" class="btn btn-sm btn-info" onclick="fetchPreview()">🔄 Refresh Preview</button>
                    </div>

                    <div style="font-size: 0.85rem; color: #475569; margin-bottom: 10px; font-weight: 600;" id="previewMetrics">
                        Records: <span id="metricRecords">0</span> | Selected Columns: <span id="metricCols">0</span>
                    </div>

                    <div id="previewTableGrid"></div>
                </div>

            </div>

            <!-- RIGHT STICKY SIDEBAR (~25% WIDTH) -->
            <div class="right-sidebar">
                <div class="enterprise-card">
                    <div style="font-size: 0.95rem; font-weight: 700; color: #1e293b; margin-bottom: 15px; border-bottom: 1px solid #cbd5e1; padding-bottom: 8px;">
                        🎯 Cohort Scope & Spatial Filters
                    </div>

                    <!-- Baseline Date -->
                    <div class="filter-control">
                        <div class="filter-label">📅 Reference Date:</div>
                        <input type="date" name="referenceDate" class="form-control form-control-sm" value="${java.time.LocalDate.now()}"/>
                    </div>

                    <!-- Spatial Scope -->
                    <div class="filter-control">
                        <div class="filter-label">📍 Region / Zone:</div>
                        <select name="regionCode" class="form-control form-control-sm">
                            <option value="">All Study Regions</option>
                            <g:each in="${regions}" var="r">
                                <option value="${r.code}">${r.code} - ${r.name}</option>
                            </g:each>
                        </select>
                    </div>

                    <!-- Gender Cohort -->
                    <div class="filter-control">
                        <div class="filter-label">👥 Gender Cohort:</div>
                        <select name="gender" class="form-control form-control-sm">
                            <option value="ALL">Both Genders</option>
                            <option value="MALE">Male Only</option>
                            <option value="FEMALE">Female Only</option>
                        </select>
                    </div>

                    <!-- Age Range -->
                    <div class="filter-control">
                        <div class="filter-label">🎂 Age Range (Years):</div>
                        <div style="display: flex; gap: 6px;">
                            <input type="number" name="ageMin" class="form-control form-control-sm" placeholder="Min" min="0"/>
                            <input type="number" name="ageMax" class="form-control form-control-sm" placeholder="Max" min="120"/>
                        </div>
                    </div>

                    <!-- Random Sub-sampling -->
                    <div class="filter-control">
                        <div class="filter-label">🎲 Sub-sampling (%):</div>
                        <input type="number" name="randomSamplePercent" class="form-control form-control-sm" value="100.0" min="1" max="100"/>
                    </div>

                    <!-- Active Residents Toggle -->
                    <div class="filter-control" style="margin-top: 10px;">
                        <div class="form-check form-switch">
                            <input class="form-check-input" type="checkbox" name="activeResidentsOnly" value="true" checked="checked" id="activeResidentsSwitch"/>
                            <label class="form-check-label filter-label" for="activeResidentsSwitch">Latest Residency Only</label>
                        </div>
                    </div>
                </div>

                <!-- EXPORT ACTION CARD -->
                <div class="export-action-card">
                    <div class="icon-ready"><asset:image src="skin/data_export_ready_icon.png" alt="Tables" width="36" height="36" /></div>
                    <div style="font-weight: 700; font-size: 0.9rem; margin-bottom: 4px;">Ready to Export</div>
                    <div style="font-size: 0.75rem; color: #69708f; margin-bottom: 12px; line-height: 1.4; max-width: 200px;">
                        Configure your export settings and click the button below to continue.
                    </div>

                    <button type="button" class="export-gradient-btn" onclick="openExportModal()">
                        <asset:image src="skin/data_export_download.png" alt="Download" width="14" height="14" style="vertical-align: middle; margin-right: 6px;"/>
                        Export Dataset
                    </button>
                </div>
            </div>
        </div>

        <!-- EXPORT MODAL DIALOG (COMPACT) -->
        <div class="modal fade" id="exportModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-compact modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header border-bottom border-2 shadow-sm modal-header-bevel" >
                        <h5 class="modal-title">🚀 Export Dataset & Privacy Configuration</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>

                    <div class="modal-body" style="padding: 20px;">
                        <div class="row">
                            <!-- LEFT PANEL: EXPORT FORMAT -->
                            <div class="col-md-5 modal-panel-left">
                                <h6 style="font-weight: 700; color: #1e293b; margin-bottom: 12px;">Export Format</h6>

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
                                    <label style="font-size: 0.85rem;"><input type="checkbox" name="includeDictionary" value="true" checked="checked"/> Attach Data Dictionary</label>
                                </div>
                            </div>

                            <!-- RIGHT PANEL: SENSITIVE DATA CONTROLS (STACKED STYLED CARDS) -->
                            <div class="col-md-7" style="padding-left: 15px;">
                                <h6 style="font-weight: 700; color: #1e293b; margin-bottom: 12px;">Sensitive Data Controls</h6>

                                <div style="max-height: 460px; overflow-y: auto; padding-right: 5px;">
                                    <!-- 1. Names Control -->
                                    <div class="privacy-card-box">
                                        <div class="privacy-card-title">👤 Direct Identifiers (Names)</div>
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
                                        <div class="privacy-card-title">📞 Contact Information (Phone)</div>
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
                                        <div class="privacy-card-title">📍 Spatial & GPS Precision</div>
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
                                        <div class="privacy-card-title">📅 Temporal & Date Granularity</div>
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
                        <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success btn-sm font-weight-bold">Confirm & Download Export</button>
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

    function selectCategory(catType, cardId) {
        document.querySelectorAll('.category-card').forEach(c => c.classList.remove('selected'));
        document.getElementById(cardId).classList.add('selected');
        document.getElementById('datasetTypeInput').value = catType;

        document.getElementById('selectContainerRaw').style.display = (catType === 'REGULAR_TABLE') ? 'block' : 'none';
        document.getElementById('selectContainerPrejoined').style.display = (catType === 'PREJOINED_DSS') ? 'block' : 'none';
        document.getElementById('selectContainerForms').style.display = (catType === 'DYNAMIC_FORM') ? 'block' : 'none';

        var name = getActiveDatasetName();
        onDatasetNameChanged(name);
    }

    function getActiveDatasetName() {
        var type = document.getElementById('datasetTypeInput').value;
        if (type === 'REGULAR_TABLE') return document.getElementById('regularTableSelect').value;
        if (type === 'PREJOINED_DSS') return document.getElementById('prejoinedSelect').value;
        if (type === 'DYNAMIC_FORM') return document.getElementById('dynamicFormSelect').value;
        return 'member';
    }

    function onDatasetNameChanged(name) {
        document.getElementById('datasetNameInput').value = name;
        var url = "${createLink(controller: 'dataExport', action: 'getColumns')}?datasetName=" + name;

        fetch(url)
            .then(r => r.json())
            .then(data => {
                if (data.success && data.columns) {
                    availableCols = data.columns;
                    selectedCols = [...data.columns];
                    renderDualLists();
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
        document.getElementById('selectedCountBadge').innerText = "Selected Columns (" + selectedCols.length + ")";
        //document.getElementById('summaryCols').innerText = selectedCols.length + "";
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
            body: formData
        })
        .then(r => r.json())
        .then(data => {
            if (data.success && data.rows && data.rows.length > 0) {
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
                alert("No preview records found matching the filters.");
            }
        });
    }

    function viewDatasetDescription() {
        var name = getActiveDatasetName();
        alert("Dataset: " + name + "\nDescription: Longitudinal surveillance dataset.");
    }

    document.addEventListener("DOMContentLoaded", function() {
        onDatasetNameChanged(getActiveDatasetName());
    });
</g:javascript>

</body>
</html>
