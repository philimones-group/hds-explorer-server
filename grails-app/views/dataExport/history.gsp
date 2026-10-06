<%@ page import="org.philimone.hds.explorer.server.model.enums.DataExportFormat; org.philimone.hds.explorer.server.model.enums.DataExportStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Data Export History | HDS-Explorer Server</title>
    <!-- FontAwesome 6 Pro / Free CDN -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"/>
    <style>
        :root {
            --bg-canvas: #f5f7fb;
            --card-bg: #ffffff;
            --border-color: #e5e9f2;
            --primary-color: #2563eb;
            --success-color: #22c55e;
            --warning-color: #f59e0b;
            --danger-color: #ef4444;
            --text-primary: #1e293b;
            --text-secondary: #64748b;
        }

        body {
            background-color: var(--bg-canvas);
            font-family: 'Inter', system-ui, -apple-system, sans-serif;
            color: var(--text-primary);
        }

        /* Card System */
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

        /* Hero Section Card */
        .hero-card {
            padding: 12px 32px;
            margin-bottom: 14px;
        }
        .hero-icon-container {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background-color: rgba(37,99,235,.08);
            color: var(--primary-color);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            margin-bottom: 0px;
        }
        .hero-title {
            font-size: 16px;
            font-weight: 700;
            color: var(--text-primary);
            margin-bottom: 6px;
        }
        .hero-subtitle {
            font-size: 10px;
            color: var(--text-secondary);
            max-width: 350px;
            line-height: 1.3;
        }

        /* Active Export Card */
        .active-export-card {
            min-height: 80px;
            padding: 10px 24px;
            margin-bottom: 14px;
            border-left: 4px solid var(--primary-color);
            background: #fafcff;
        }
        .active-spinner-circle {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            background-color: rgba(37,99,235,.1);
            color: var(--primary-color);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 22px;
            flex-shrink: 0;
        }

        /* Sticky Sidebar */
        .sticky-sidebar-custom {
            position: sticky;
            top: 20px;
            height: fit-content;
        }

        /* Table Styling */
        .history-table  > thead > tr > th {
            background-color: #f8fafc !important;
            height: 40px;
            font-size: 12px;
            font-weight: 700;
            color: var(--text-secondary);
            border-bottom: 1px solid var(--border-color);
            vertical-align: middle;
            text-align: center !important;
        }
        .history-table > tbody > tr > td {
            padding: 10px 12px;
            vertical-align: middle !important;
            border-bottom: 1px solid #f1f5f9;
            text-align: center !important;
        }
        .history-table tbody tr:hover {
            background-color: #f8fafc;
        }

        /* Dataset Icon Square */
        .dataset-icon-square {
            width: 42px;
            height: 42px;
            border-radius: 8px;
            background-color: rgba(37,99,235,.08);
            color: var(--primary-color);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 18px;
            flex-shrink: 0;
        }

        /* Format Badges */
        .badge-format-csv { background-color: #dcfce7; color: #15803d; }
        .badge-format-xlsx { background-color: #f3e8ff; color: #6b21a8; }
        .badge-format-spss { background-color: #dbeafe; color: #1e40af; }
        .badge-format-do { background-color: #ffedd5; color: #c2410c; }
        .badge-format-r { background-color: #cff4fc; color: #0e7490; }

        .modal-header-bevel {
            background: linear-gradient(to bottom, #ffffff 0%, #f0f2f5 100%);
        }

        /* Animation */
        @keyframes spin {
            from { transform: rotate(0deg); }
            to { transform: rotate(360deg); }
        }
        .spin-anim {
            animation: spin 1s linear infinite;
        }
    </style>
</head>
<body>

<div class="container-fluid px-4 py-4">

    <!-- BREADCRUMB HEADER CARD -->
    <div class="card-custom breadcrumb-header-card">
        <div class="d-flex align-items-center gap-4">
            <div class="hero-icon-container mb-0">
                <i class="fa-solid fa-clock-rotate-left"></i>
            </div>
            <div>
                <h1 class="hero-title m-0 mb-1">Data Export History</h1>
                <p class="hero-subtitle mb-0">
                    View and manage your past and ongoing data exports. Download generated files once processing is complete.
                </p>
            </div>
        </div>

        <div class="d-flex gap-2">
            <button type="button" class="btn btn-outline-secondary" style="padding-left: 40px; padding-right: 40px;" onclick="window.location.reload()">
                <i class="bi bi-arrow-clockwise me-1"></i> Refresh
            </button>
            <g:link action="index" class="btn btn-outline-primary">
                <i class="fa-solid fa-arrow-left me-1"></i> Back to Export Tool
            </g:link>
        </div>
    </div>

    <!-- MAIN 2-COLUMN LAYOUT (75% / 25%) -->
    <div class="row g-4">

        <!-- MAIN AREA (75%) -->
        <div class="col-lg-9">

            <!-- ACTIVE EXPORT CARD (IF EXECUTING)  -->
            <g:if test="${activeReport}">
                <div class="card-custom active-export-card">
                    <div class="row align-items-center g-3">
                        <!-- Left Area -->
                        <div class="col-md-5"  >
                            <div class="d-flex align-items-center gap-3" >
                                <div class="active-spinner-circle" >
                                    <i class="fa-solid fa-spinner spin-anim"></i>
                                </div>
                                <div>
                                    <div class="fw-bold fs-6 text-dark">${activeReport.datasetLabel} – Recent Export</div>
                                    <div class="text-muted" style="font-size: 13px">
                                        Started on ${activeReport.createdDate?.toString()?.replace('T', ' ')?.substring(0, 16)} • Requested by ${activeReport.createdBy ?: 'Paulo Filimone'}
                                    </div>
                                    <span class="badge bg-primary rounded-pill mt-1"><i class="fa-solid fa-spinner spin-anim me-1"></i> Exporting...</span>
                                </div>
                            </div>
                        </div>

                        <!-- Center Progress Area -->
                        <div class="col-md-4" >
                            <div class="d-flex justify-content-between  fw-bold text-primary mb-1" style="font-size: 13px">
                                <span id="activeStepText">${activeReport.currentStep ?: 'Processing steps...'}</span>
                                <span id="activePctText">${activeReport.progressPercent ?: 68}%</span>
                            </div>
                            <div class="progress" style="height: 10px; border-radius: 999px;">
                                <div class="progress-bar progress-bar-striped progress-bar-animated bg-primary" id="activeProgressBar" role="progressbar" style="width: ${activeReport.progressPercent ?: 68}%;"></div>
                            </div>
                            <div class="text-muted mt-1" style="font-size: 13px">
                                <i class="fa-solid fa-clock me-1"></i> Estimated time remaining: ~2 minutes
                            </div>
                        </div>

                        <!-- Right Area Button -->
                        <div class="col-md-3 text-end">
                            <button type="button" class="btn btn-outline-primary btn-sm" onclick="openDetailsById('${activeReport.id}')">
                                <i class="fa-solid fa-eye me-1"></i> View Details
                            </button>
                        </div>
                    </div>
                </div>
            </g:if>

            <!-- PREVIOUS EXPORTS SECTION -->
            <div class="card-custom p-0 overflow-hidden">

                <!-- Section Header Toolbar -->
                <div class="py-3 px-5 border-bottom d-flex flex-wrap align-items-center justify-content-between gap-3" >
                    <h2 class="m-0 fw-bold text-dark" style="font-size: 14px;">Previous Exports (${reports?.size() ?: 0})</h2>

                    <div class="d-flex flex-wrap align-items-center gap-2">
                        <!-- Search Input -->
                        <div class="input-group input-group-sm" style="width: 250px;">
                            <span class="input-group-text bg-white border-end-0"><i class="fa-solid fa-magnifying-glass text-muted"></i></span>
                            <input type="text" id="searchInput2" class="form-control border-start-0" placeholder="Search exports..." onkeyup="filterHistoryTable()"/>
                        </div>

                        <!-- Status Filter -->
                        <div class="input-group input-group-sm" style="width: 170px;">
                            <span class="input-group-text bg-white border-end-0"><i class="fa-solid fa-filter text-muted"></i></span>
                            <select id="statusFilter2" class="form-select form-select-sm border-start-0" onchange="filterHistoryTable()">
                                <option value="ALL">All Statuses</option>
                                <option value="EXECUTING">Exporting</option>
                                <option value="COMPLETED">Completed</option>
                                <option value="FAILED">Failed</option>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- PREVIOUS EXPORTS TABLE -->
                <div class="table-responsive">
                    <table class="table table-hover history-table mb-1" id="historyTable2" style="margin-top: 0px">
                        <thead>
                            <tr>
                                <th style="width: 50px;" class="ps-4">#</th>
                                <th>Dataset / Description</th>
                                <th>Export Type</th>
                                <th>Created By</th>
                                <th>Started On</th>
                                <th>Status</th>
                                <th>Files</th>
                                <th class="text-end pe-4" style="width: 140px;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <g:if test="${reports && !reports.isEmpty()}">
                                <g:each in="${reports}" var="r" status="idx">
                                    <tr class="history-row-item" data-name="${r.datasetName?.toLowerCase()}" data-status="${r.status?.code}">
                                        <!-- Column 1: Row Number -->
                                        <td class="ps-4 fw-bold text-muted fs-7">${idx + 1}</td>

                                        <!-- Column 2: Dataset / Description -->
                                        <td style="vertical-align: middle; justify-content: center;">
                                            <div class="d-flex align-items-center gap-3" style="text-align: left !important; margin-left: 10px;">
                                                <div class="dataset-icon-square">
                                                    <i class="fa-solid ${r.exportItem?.code == 'PREJOINED_DSS' ? 'fa-table' : (r.exportItem?.code == 'DYNAMIC_FORM' ? 'fa-clipboard-list' : 'fa-database')}"></i>
                                                </div>
                                                <div>
                                                    <div class="fw-semibold text-dark fs-6">${r.datasetLabel}</div>
                                                    <div class="text-muted fs-7"><g:message code="${r.exportItem?.getExactName()}" default="Household residency data" /></div>
                                                </div>
                                            </div>
                                        </td>

                                        <!-- Column 3: Export Type -->
                                        <td style="vertical-align: middle; justify-content: center;">
                                            <%
                                                def fmtObj = DataExportFormat.getFrom(r.format)
                                            %>
                                            <div class="d-flex align-items-center gap-2" style="white-space: nowrap; text-align: left !important; margin-left: 20px;">
                                                <asset:image src="skin/${fmtObj ? fmtObj.icon : 'data_export_csv.png'}" alt="${r.format}" width="22" height="22" style="flex-shrink:0;"/>
                                                <div>
                                                    <div class="fw-semibold text-dark fs-7"><g:message code="${fmtObj?.name}" default="${fmtObj?.title ?: r.format}"/></div>
                                                    <div class="text-muted fs-8" style="width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${fmtObj.desc}</div>
                                                </div>
                                            </div>
                                        </td>

                                        <!-- Column 4: Created By -->
                                        <td>
                                            <div class="fw-medium text-dark fs-7">${r.createdBy ?: 'Paulo Filimone'}</div>
                                        </td>

                                        <!-- Column 5: Started On -->
                                        <td style="white-space: nowrap;">
                                            <span class="fs-7 text-dark fw-medium">${r.createdDate?.toString()?.replace('T', ' ')?.substring(0, 16)}</span>
                                        </td>

                                        <!-- Column 6: Status -->
                                        <td>
                                            <g:if test="${r.status == DataExportStatus.EXECUTING}">
                                                <span class="badge bg-primary rounded-pill px-2 py-1"><i class="fa-solid fa-spinner spin-anim me-1"></i> Exporting...</span>
                                            </g:if>
                                            <g:elseif test="${r.status == DataExportStatus.COMPLETED}">
                                                <span class="badge bg-success rounded-pill px-2 py-1"><i class="fa-solid fa-circle-check me-1"></i> Completed</span>
                                            </g:elseif>
                                            <g:else>
                                                <span class="badge bg-danger rounded-pill px-2 py-1"><i class="fa-solid fa-circle-exclamation me-1"></i> Failed</span>
                                            </g:else>
                                        </td>

                                        <!-- Column 7: Files -->
                                        <td>
                                            <g:if test="${r.status == DataExportStatus.COMPLETED}">
                                                <div class="fs-7 text-dark" style="white-space: nowrap;">
                                                    <i class="fa-solid fa-file-csv text-primary me-1"></i>
                                                    ${r.zipFileName ? '(2 files)' : '(1 file)'}
                                                </div>
                                            </g:if>
                                            <g:else>
                                                <span class="text-muted">-</span>
                                            </g:else>
                                        </td>

                                        <!-- Column 8: Actions -->
                                        <td class="text-end pe-4" style="white-space: nowrap;">
                                            <div class="d-flex align-items-center justify-content-end gap-2">
                                                <g:if test="${r.status == DataExportStatus.COMPLETED}">
                                                    <g:link action="downloadReportFile" id="${r.id}" class="btn btn-primary btn-sm px-3 d-inline-flex align-items-center gap-1" style="width: 110px;"  >
                                                        <i class="fa-solid fa-download me-1"></i>
                                                        <span>Download</span>
                                                    </g:link>
                                                </g:if>
                                                <g:else>
                                                    <button type="button" class="btn btn-secondary btn-sm px-3 d-inline-flex align-items-center gap-1" style="width: 110px;" onclick="openDetailsById('${r.id}')">
                                                        <i class="fa-solid fa-eye me-1"></i>
                                                        <span>View details</span>
                                                    </button>
                                                </g:else>

                                                <!-- Overflow Menu -->
                                                <div class="dropdown">
                                                    <button class="btn btn-link text-muted p-0 ms-1" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                                        <i class="fa-solid fa-ellipsis-vertical fs-5"></i>
                                                    </button>
                                                    <ul class="dropdown-menu dropdown-menu-end shadow-sm">
                                                        <li>
                                                            <a class="dropdown-menu-item dropdown-item fs-7" href="#" onclick="openDetailsById('${r.id}'); return false;">
                                                                <i class="fa-solid fa-eye me-2"></i> View Details
                                                            </a>
                                                        </li>
                                                        <li><g:link action="retryExport" id="${r.id}" class="dropdown-item fs-7"><i class="fa-solid fa-arrow-rotate-right me-2"></i> Re-run Export</g:link></li>
                                                    </ul>
                                                </div>
                                            </div>
                                        </td>
                                    </tr>
                                </g:each>
                            </g:if>
                            <g:else>
                                <tr>
                                    <td colspan="8" class="text-center py-5">
                                        <i class="fa-solid fa-folder-open fs-1 text-muted mb-2"></i>
                                        <h5 class="fw-bold text-dark">No Exports Yet</h5>
                                        <p class="text-muted fs-7">Generate your first export using the Data Export Tool.</p>
                                        <g:link action="index" class="btn btn-primary btn-sm mt-2"><i class="fa-solid fa-plus me-1"></i> Create Export</g:link>
                                    </td>
                                </tr>
                            </g:else>
                        </tbody>
                    </table>
                </div>

            </div>

        </div>

        <!-- STICKY SIDEBAR (25%) -->
        <div class="col-lg-3">
            <div class="card-custom p-3 sticky-sidebar-custom">

                <g:if test="${activeReport}">
                    <!-- SIDEBAR HEADER CARD (ACTIVE JOB) -->
                    <div class="card-custom p-3 mb-3">
                        <div class="d-flex align-items-center justify-content-between">
                            <div class="d-flex align-items-center gap-3">
                                <div class="d-flex align-items-center justify-content-center text-white bg-primary rounded-circle" style="width: 42px; height: 42px;">
                                    <i class="fa-solid fa-database"></i>
                                </div>
                                <div>
                                    <div class="fw-bold text-dark fs-6" style="font-size: 0.95rem;">${activeReport.datasetLabel}</div>
                                    <div class="text-muted fs-8" style="font-size: 0.9rem;">Active Export Job</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- STATUS / PROGRESS CARD -->
                    <div class="card-custom p-3 mb-3" style="background: #eff6ff; border-color: #bfdbfe;">
                        <div class="d-flex align-items-center gap-2 mb-2 text-primary fw-bold" style="font-size: 13px;">
                            <i class="fa-solid fa-spinner spin-anim"></i>
                            <span>Exporting...</span>
                        </div>

                        <p class="text-muted mb-3" style="font-size: 13px;" id="activeStepDesc">
                            ${activeReport.currentStep ?: 'This dataset is currently being generated.'}
                        </p>

                        <div class="d-flex justify-content-between fw-bold text-primary mb-1" style="font-size: 13px;">
                            <span>Progress</span>
                            <span id="activePctText">${activeReport.progressPercent ?: 15}%</span>
                        </div>

                        <div class="progress mb-2" style="height: 8px;">
                            <div class="progress-bar bg-primary" id="activeProgressBar" role="progressbar" style="width: ${activeReport.progressPercent ?: 15}%;"></div>
                        </div>

                        <div class="text-muted" style="font-size: 13px;">
                            <i class="fa-solid fa-clock me-1"></i> Processing dataset
                        </div>
                    </div>

                    <!-- EXPORT DETAILS CARD -->
                    <div class="card-custom p-3 mb-3">
                        <h6 class="fw-bold text-dark fs-6 mb-3" style="font-size: 0.95rem;">Active Export Details</h6>

                        <div class="d-flex flex-column gap-2" style="font-size: 0.9rem;">
                            <div class="d-flex align-items-center gap-2 text-muted">
                                <i class="fa-solid fa-calendar w-20"></i> <span>Started On:</span> <b class="text-dark ms-auto">${activeReport.createdDate?.toString()?.replace('T', ' ')?.substring(0, 16)}</b>
                            </div>
                            <div class="d-flex align-items-center gap-2 text-muted">
                                <i class="fa-solid fa-user w-20"></i> <span>Requested By:</span> <b class="text-dark ms-auto">${activeReport.createdBy ?: 'admin'}</b>
                            </div>
                            <div class="d-flex align-items-center gap-2 text-muted">
                                <i class="fa-solid fa-file-csv w-20"></i> <span>Export Format:</span> <b class="text-dark ms-auto">${activeReport.format}</b>
                            </div>
                            <div class="d-flex align-items-center gap-2 text-muted">
                                <i class="fa-solid fa-database w-20"></i> <span>Dataset:</span> <b class="text-dark ms-auto">${activeReport.datasetName}</b>
                            </div>
                            <div class="d-flex align-items-center gap-2 text-muted">
                                <i class="fa-solid fa-filter w-20"></i> <span>Columns:</span> <b class="text-dark ms-auto">${activeReport.totalColumns ?: 0} Cols</b>
                            </div>
                            <div class="d-flex align-items-center gap-2 text-muted">
                                <i class="fa-solid fa-location-dot w-20"></i> <span>Region:</span> <b class="text-dark ms-auto">${activeReport.regionCode ?: 'All Regions'}</b>
                            </div>
                            <div class="d-flex align-items-center gap-2 text-muted">
                                <i class="fa-solid fa-users w-20"></i> <span>Gender Cohort:</span> <b class="text-dark ms-auto">${activeReport.gender ?: 'Both Genders'}</b>
                            </div>
                        </div>
                    </div>
                </g:if>

                <g:else>
                    <!-- IDLE SYSTEM STATUS CARD -->
                    <div class="card-custom p-3 mb-3" style="background: #f0fdf4; border-color: #bbf7d0;">
                        <div class="d-flex align-items-center justify-content-between mb-2">
                            <span class="fw-bold text-success fs-7" style="font-size: 0.95rem;">
                                <i class="fa-solid fa-circle-check me-1"></i> System Status
                            </span>
                            <span class="badge bg-success rounded-pill px-2 py-1">READY</span>
                        </div>

                        <div class="fw-bold text-dark fs-6 mb-1" style="font-size: 0.9rem;">Export Engine Idle</div>
                        <p class="text-muted fs-8 mb-3" style="font-size: 0.9rem;">
                            No active export jobs currently in queue. You can configure and run a new dataset export anytime.
                        </p>

                        <g:link action="index" class="btn btn-success btn-sm w-100">
                            <i class="fa-solid fa-plus me-1"></i> Start New Export
                        </g:link>
                    </div>

                    <!-- REAL STORAGE & USAGE STATS -->
                    <div class="card-custom p-3 mb-3">
                        <h6 class="fw-bold text-dark fs-7 mb-3" style="font-size: 0.95rem;">
                            <i class="fa-solid fa-chart-pie me-2 text-primary"></i> Storage & Usage Stats
                        </h6>

                        <div class="d-flex flex-column gap-2 fs-7" style="font-size: 0.9rem;">
                            <div class="d-flex justify-content-between py-1 border-bottom">
                                <span class="text-muted">Total Exports Run:</span>
                                <b class="text-dark">${totalExports ?: 0}</b>
                            </div>
                            <div class="d-flex justify-content-between py-1 border-bottom">
                                <span class="text-muted">Files Generated Today:</span>
                                <b class="text-dark">${filesGeneratedToday ?: 0}</b>
                            </div>
                            <div class="d-flex justify-content-between py-1 border-bottom">
                                <span class="text-muted">Records Exported Today:</span>
                                <b class="text-dark">${recordsToday ? Number(recordsToday).toLocaleString() : 0}</b>
                            </div>
                            <div class="d-flex justify-content-between py-1 border-bottom">
                                <span class="text-muted">Total Storage Used:</span>
                                <b class="text-dark">${totalStorageBytes ? (totalStorageBytes / 1048576.0).round(1) : 0} MB</b>
                            </div>
                            <div class="d-flex justify-content-between py-1">
                                <span class="text-muted">File Retention:</span>
                                <b class="text-dark">7 Days (Auto-cleanup)</b>
                            </div>
                        </div>
                    </div>
                </g:else>

                <!-- DATA GOVERNANCE ALERT -->
                <div class="card-custom p-3" style="background: #eff6ff; border-color: #bfdbfe;">
                    <div class="d-flex align-items-start gap-2 text-primary-emphasis fs-8" style="font-size: 0.9rem;">
                        <i class="fa-solid fa-shield-halved fs-6 mt-1 text-primary"></i>
                        <div>
                            <b>Data Governance Policy:</b> Generated dataset files are securely retained for 7 days before automatic permanent deletion. Ensure PII anonymization guidelines are followed.
                        </div>
                    </div>
                </div>

            </div>
        </div>

    </div>

</div>

<!-- DATA EXPORT DETAILS MODAL DIALOG -->
<div class="modal fade" id="exportDetailsModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered" style="max-width: 480px;">
        <div class="modal-content border-0 shadow-lg rounded-3">
            <div class="modal-header rounded-top-3 py-3 modal-header-bevel">
                <h5 class="modal-title fw-bold fs-6 m-0">
                    <i class="fa-solid fa-circle-info text-info me-2"></i> Export Details
                </h5>
                <button type="button" class="btn-close btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body p-4">
                <!-- EXPORT DETAILS CARD CONTENT (Matches Sidebar Style) -->
                <div class="d-flex flex-column gap-3" style="font-size: 13px;">
                    <div class="d-flex align-items-center gap-2 text-muted pb-2 border-bottom">
                        <i class="fa-solid fa-database text-primary" style="width: 20px;"></i>
                        <span>Dataset:</span>
                        <b class="text-dark ms-auto" id="detDatasetName">Residency</b>
                    </div>

                    <div class="d-flex align-items-center gap-2 text-muted pb-2 border-bottom">
                        <i class="fa-solid fa-calendar text-primary" style="width: 20px;"></i>
                        <span>Started On:</span>
                        <b class="text-dark ms-auto" id="detCreatedDate">09 Aug 2026, 14:32</b>
                    </div>

                    <div class="d-flex align-items-center gap-2 text-muted pb-2 border-bottom">
                        <i class="fa-solid fa-user text-primary" style="width: 20px;"></i>
                        <span>Requested By:</span>
                        <b class="text-dark ms-auto" id="detCreatedBy">Paulo Filimone</b>
                    </div>

                    <div class="d-flex align-items-center gap-2 text-muted pb-2 border-bottom">
                        <i class="fa-solid fa-file-csv text-primary" style="width: 20px;"></i>
                        <span>Export Format:</span>
                        <b class="text-dark ms-auto" id="detFormat">CSV</b>
                    </div>

                    <div class="d-flex align-items-center gap-2 text-muted pb-2 border-bottom">
                        <i class="fa-solid fa-filter text-primary" style="width: 20px;"></i>
                        <span>Query / Columns:</span>
                        <b class="text-dark ms-auto" id="detColumns">24 Columns</b>
                    </div>

                    <div class="d-flex align-items-center gap-2 text-muted pb-2 border-bottom">
                        <i class="fa-solid fa-location-dot text-primary" style="width: 20px;"></i>
                        <span>Region / Zone:</span>
                        <b class="text-dark ms-auto" id="detRegion">All Study Regions</b>
                    </div>

                    <div class="d-flex align-items-center gap-2 text-muted pb-2 border-bottom">
                        <i class="fa-solid fa-users text-primary" style="width: 20px;"></i>
                        <span>Gender Cohort:</span>
                        <b class="text-dark ms-auto" id="detGender">Both Genders</b>
                    </div>

                    <div class="d-flex align-items-center gap-2 text-muted pb-2 border-bottom">
                        <i class="fa-solid fa-clock text-primary" style="width: 20px;"></i>
                        <span>Duration:</span>
                        <b class="text-dark ms-auto" id="detDuration">1.4s</b>
                    </div>

                    <div class="d-flex align-items-center gap-2 text-muted">
                        <i class="fa-solid fa-shield-halved text-success" style="width: 20px;"></i>
                        <span>Status:</span>
                        <b class="ms-auto"><span class="badge bg-success text-uppercase px-2 py-1" id="detStatusBadge">COMPLETED</span></b>
                    </div>
                </div>
            </div>

            <div class="modal-footer bg-light rounded-bottom-3 py-2">
                <button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Close</button>
                <div id="detDownloadBtnBox"></div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    function openDetailsById(jobId) {
        if (!jobId) return;

        fetch("${createLink(action: 'getJobDetails')}?id=" + jobId, {
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
                if (!data || !data.success || !data.job) return;
                var j = data.job;

                var nameEl = document.getElementById('detDatasetName');
                if (nameEl) nameEl.innerText = j.datasetName || '';
                var fmtEl = document.getElementById('detFormat');
                if (fmtEl) fmtEl.innerText = j.format || '';
                var usrEl = document.getElementById('detCreatedBy');
                if (usrEl) usrEl.innerText = j.createdBy || '';
                var dateEl = document.getElementById('detCreatedDate');
                if (dateEl) dateEl.innerText = j.createdDate || '';
                var durEl = document.getElementById('detDuration');
                if (durEl) durEl.innerText = (j.executionTimeMs || '0') + 's';
                var colsEl = document.getElementById('detColumns');
                if (colsEl) colsEl.innerText = (j.totalColumns || '0') + ' Columns';
                var regEl = document.getElementById('detRegion');
                if (regEl) regEl.innerText = j.regionCode || 'All Study Regions';
                var genEl = document.getElementById('detGender');
                if (genEl) genEl.innerText = j.gender || 'Both Genders';

                var badgeEl = document.getElementById('detStatusBadge');
                if (badgeEl) {
                    badgeEl.innerText = j.status;
                    badgeEl.className = 'badge px-2 py-1 ' + (j.status === 'COMPLETED' ? 'bg-success' : (j.status === 'EXECUTING' ? 'bg-primary' : 'bg-danger'));
                }

                var dlBox = document.getElementById('detDownloadBtnBox');
                if (dlBox) {
                    if (j.status === 'COMPLETED' && j.downloadUrl) {
                        dlBox.innerHTML = '<a href="' + j.downloadUrl + '" class="btn btn-primary btn-sm"><i class="fa-solid fa-download me-1"></i> Download File</a>';
                    } else {
                        dlBox.innerHTML = '';
                    }
                }

                var el = document.getElementById('exportDetailsModal');
                if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
                    var modal = bootstrap.Modal.getOrCreateInstance(el);
                    modal.show();
                } else if (typeof $ !== 'undefined' && $(el).modal) {
                    $(el).modal('show');
                } else {
                    el.style.display = 'block';
                    el.classList.add('show');
                }
            })
            .catch(e => console.log(e));
    }

    function filterHistoryTable() {
        var query = document.getElementById('searchInput2').value.toLowerCase();
        var status = document.getElementById('statusFilter2').value;

        document.querySelectorAll('.history-row-item').forEach(row => {
            var name = row.getAttribute('data-name') || '';
            var cardStatus = row.getAttribute('data-status') || '';

            var matchesSearch = query === '' || name.indexOf(query) !== -1;
            var matchesStatus = status === 'ALL' || cardStatus === status;

            if (matchesSearch && matchesStatus) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    function pollActiveJobs() {
        fetch("${createLink(action: 'getJobStatus')}", {
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        })
            .then(r => {
                if (r.status === 401 || r.redirected) {
                    return null;
                }
                return r.json();
            })
            .then(data => {
                if (!data || !data.success) return;
                if (!data.activeJobs || data.activeJobs.length === 0) {
                    var activeStepText = document.getElementById('activeStepText');
                    if (activeStepText) {
                        window.location.reload();
                    }
                } else {
                    data.activeJobs.forEach(job => {
                        if (job.progressPercent >= 100 || job.status === 'COMPLETED') {
                            window.location.reload();
                        } else {
                            var activeStepText = document.getElementById('activeStepText');
                            var activePctText = document.getElementById('activePctText');
                            var activeProgressBar = document.getElementById('activeProgressBar');

                            if (activeStepText) activeStepText.innerText = job.currentStep;
                            if (activePctText) activePctText.innerText = job.progressPercent + '%';
                            if (activeProgressBar) activeProgressBar.style.width = job.progressPercent + '%';
                        }
                    });
                }
            })
            .catch(e => console.log(e));
    }

    document.addEventListener("DOMContentLoaded", function() {
        setInterval(pollActiveJobs, 1500);
    });
</script>

</body>
</html>
