package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.enums.DataExportFormat

@Transactional(readOnly = true)
class DataExportScriptsBuilderService {

    DataDictionaryService dataDictionaryService

    static boolean isSyntaxScriptFormat(DataExportFormat format) {
        return format.isSyntaxScriptFormat()
    }

    String getScriptExtension(DataExportFormat format) {
        if (format == DataExportFormat.SPSS) return ".sps"
        if (format == DataExportFormat.R) return ".R"
        if (format == DataExportFormat.SAS) return ".sas"
        if (format == DataExportFormat.STATA) return ".do"
        return ".do"
    }

    // --- Statistical Script Generators ---

    String generateSyntaxScript(ExportRequest request) {
        if (request.hasRepeatTables) {
            return generateMasterSyntaxScript(request)
        }

        def fmt = request.format

        switch (fmt) {
            case DataExportFormat.SPSS:
                return generateSpssScript(request)
            case DataExportFormat.R:
                return generateRScript(request)
            case DataExportFormat.SAS:
                return generateSasScript(request)
            default:
                return generateStataDoScript(request)
        }
    }

    String generateSyntaxScriptForTable(String tableName, ExportRequest request) {
        ExportRequest childReq = new ExportRequest(
                datasetName: tableName,
                format: request.format,
                selectedColumns: []
        )
        return generateSyntaxScript(childReq)
    }

    private String generateStataDoScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        StringBuilder doScript = new StringBuilder()
        doScript.append("/* Stata Do-File for HDSS Data Export: ${tableName} */\n")
        doScript.append("/* Optional: Set working directory to folder containing CSV data files */\n")
        doScript.append("/* cd \"/path/to/folder\" */\n\n")
        doScript.append("clear all\n")
        doScript.append("set more off\n\n")
        doScript.append("/* Import CSV Dataset */\n")
        doScript.append("import delimited using \"${tableName}_data.csv\", case(lower) clear\n\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta) {
            doScript.append("/* Variable Descriptions */\n")
            meta.columns.each { colName, colMeta ->
                String stCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (label) {
                    doScript.append("capture label variable ${stCol} \"${escapeQuote(label)}\"\n")
                }
            }

            doScript.append("\n/* Date & Timestamp Conversions */\n")
            meta.columns.each { colName, colMeta ->
                String stCol = sanitizeColumnName(colName)
                String dataType = colMeta.dataType ? colMeta.dataType.toUpperCase() : ""
                String label = getDisplayLabel(colMeta)

                boolean isDate = dataType.contains("DATE") && !dataType.contains("TIME")
                boolean isDateTime = dataType.contains("DATETIME") || dataType.contains("TIMESTAMP") || dataType.contains("TIME")

                if (isDate) {
                    doScript.append("capture confirm variable ${stCol}\n")
                    doScript.append("if !_rc {\n")
                    doScript.append("    capture gen ${stCol}_d = date(${stCol}, \"YMD\")\n")
                    doScript.append("    capture format ${stCol}_d %td\n")
                    doScript.append("    capture drop ${stCol}\n")
                    doScript.append("    capture rename ${stCol}_d ${stCol}\n")
                    if (label) {
                        doScript.append("    capture label variable ${stCol} \"${escapeQuote(label)}\"\n")
                    }
                    doScript.append("}\n")
                } else if (isDateTime) {
                    doScript.append("capture confirm variable ${stCol}\n")
                    doScript.append("if !_rc {\n")
                    doScript.append("    capture gen ${stCol}_dt = clock(${stCol}, \"YMDhms\")\n")
                    doScript.append("    capture format ${stCol}_dt %tc\n")
                    doScript.append("    capture drop ${stCol}\n")
                    doScript.append("    capture rename ${stCol}_dt ${stCol}\n")
                    if (label) {
                        doScript.append("    capture label variable ${stCol} \"${escapeQuote(label)}\"\n")
                    }
                    doScript.append("}\n")
                }
            }

            doScript.append("\n/* Categorical Encoding & Value Labels */\n")
            meta.columns.each { colName, colMeta ->
                String stCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        List<DataDictionaryService.EnumOptionMetadata> validOptions = enumType.options.findAll {
                            it.code != null && !it.code.trim().isEmpty()
                        }

                        if (!validOptions.isEmpty()) {
                            String lblName = "${stCol}_lbl"

                            boolean isNumericCodes = validOptions.every { opt ->
                                opt.code != null && opt.code.matches(/^-?\d+$/)
                            }

                            if (isNumericCodes) {
                                doScript.append("capture confirm variable ${stCol}\n")
                                doScript.append("if !_rc {\n")
                                doScript.append("    capture label drop ${lblName}\n")
                                doScript.append("    label define ${lblName}")
                                validOptions.each { opt ->
                                    doScript.append(" ${opt.code} \"${escapeQuote(opt.label)}\"")
                                }
                                doScript.append(", replace\n")
                                doScript.append("    destring ${stCol}, replace\n")
                                doScript.append("    label values ${stCol} ${lblName}\n")
                                doScript.append("}\n")
                            } else {
                                def sortedOpts = validOptions.sort { a, b -> (a.code ?: "").compareTo(b.code ?: "") }
                                doScript.append("capture confirm variable ${stCol}\n")
                                doScript.append("if !_rc {\n")
                                doScript.append("    capture label drop ${lblName}\n")
                                doScript.append("    label define ${lblName}")
                                sortedOpts.eachWithIndex { opt, idx ->
                                    doScript.append(" ${idx + 1} \"${escapeQuote(opt.label)}\"")
                                }
                                doScript.append(", replace\n")
                                doScript.append("    encode ${stCol}, gen(${stCol}_encoded)\n")
                                doScript.append("    label values ${stCol}_encoded ${lblName}\n")
                                doScript.append("    drop ${stCol}\n")
                                doScript.append("    rename ${stCol}_encoded ${stCol}\n")
                                if (label) {
                                    doScript.append("    capture label variable ${stCol} \"${escapeQuote(label)}\"\n")
                                }
                                doScript.append("}\n")
                            }
                        }
                    }
                }
            }
        }

        doScript.append("\n/* Save Stata Data File */\n")
        doScript.append("save \"${tableName}.dta\", replace\n")
        return doScript.toString()
    }

    private String generateSpssScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        StringBuilder spss = new StringBuilder()
        spss.append("/* SPSS Syntax Script for HDSS Data Export: ${tableName} */\n")
        spss.append("/* Optional: Set working directory to folder containing CSV data files */\n")
        spss.append("/* CD '/path/to/folder'. */\n\n")
        spss.append("GET DATA\n  /TYPE=TXT\n  /FILE=\"${tableName}_data.csv\"\n")
        spss.append("  /ARRANGEMENT=DELIMITED\n  /DELCASE=LINE\n  /FIRSTCASE=2\n")
        spss.append("  /DELIMITERS=\",\"\n  /QUALIFIER='\"'\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta && meta.columns && !meta.columns.isEmpty()) {
            spss.append("  /VARIABLES=\n")
            int colCount = meta.columns.size()
            int i = 0
            meta.columns.each { colName, colMeta ->
                i++
                String spCol = sanitizeColumnName(colName)
                String term = (i == colCount) ? "." : ""
                spss.append("    ${spCol} A255${term}\n")
            }
        } else {
            spss.append(".\n")
        }
        spss.append("EXECUTE.\n")
        spss.append("DATASET NAME ${sanitizeColumnName(tableName)} WINDOW=FRONT.\n\n")

        if (meta) {
            spss.append("/* Variable Descriptions */\n")
            spss.append("VARIABLE LABELS\n")
            meta.columns.each { colName, colMeta ->
                String spCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (label) {
                    spss.append("  ${spCol} \"${escapeQuote(label)}\"\n")
                }
            }
            spss.append(".\nEXECUTE.\n\n")

            // Data Type Conversions via ALTER TYPE (Handles blank CSV strings cleanly)
            spss.append("/* Data Type Conversions */\n")
            meta.columns.each { colName, colMeta ->
                String spCol = sanitizeColumnName(colName)
                String dataType = colMeta.dataType ? colMeta.dataType.toUpperCase() : ""

                boolean isInt = dataType.contains("INT") || dataType.contains("LONG") || dataType.contains("SHORT")
                boolean isDouble = dataType.contains("DOUBLE") || dataType.contains("FLOAT") || dataType.contains("DECIMAL") || dataType.contains("NUMERIC")
                boolean isDate = dataType.contains("DATE") && !dataType.contains("TIME")
                boolean isDateTime = dataType.contains("DATETIME") || dataType.contains("TIMESTAMP") || dataType.contains("TIME")

                if (isInt) {
                    spss.append("ALTER TYPE ${spCol} (F12.0).\n")
                } else if (isDouble) {
                    spss.append("ALTER TYPE ${spCol} (F12.4).\n")
                } else if (isDate) {
                    spss.append("ALTER TYPE ${spCol} (SDATE10).\n")
                } else if (isDateTime) {
                    spss.append("ALTER TYPE ${spCol} (DATETIME20).\n")
                }
            }
            spss.append("EXECUTE.\n\n")

            // Value labels for categorical enum fields (filtering out blank option codes)
            spss.append("/* Value Labels for Categorical Variables */\n")
            meta.columns.each { colName, colMeta ->
                String spCol = sanitizeColumnName(colName)
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        List<DataDictionaryService.EnumOptionMetadata> validOptions = enumType.options.findAll {
                            it.code != null && !it.code.trim().isEmpty()
                        }

                        if (!validOptions.isEmpty()) {
                            boolean isNumericCodes = validOptions.every { opt ->
                                opt.code != null && opt.code.matches(/^-?\d+$/)
                            }

                            spss.append("VALUE LABELS ${spCol}\n")
                            validOptions.each { opt ->
                                String valSpec = isNumericCodes ? "${opt.code}" : "'${escapeQuote(opt.code)}'"
                                spss.append("  ${valSpec} \"${escapeQuote(opt.label)}\"\n")
                            }
                            spss.append(".\nEXECUTE.\n\n")
                        }
                    }
                }
            }

            spss.append("/* Save Native SPSS Data File (.sav) */\n")
            spss.append("SAVE OUTFILE=\"${tableName}.sav\".\n")
            spss.append("EXECUTE.\n")
        }

        return spss.toString()
    }

    private String generateRScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        StringBuilder r = new StringBuilder()
        r.append("# R Import Script for HDSS Data Export: ${tableName}\n")
        r.append("# Optional: Set working directory to folder containing CSV data files\n")
        r.append("# setwd(\"/path/to/folder\")\n\n")
        r.append("df <- read.csv(\"${tableName}_data.csv\", stringsAsFactors = FALSE, encoding = \"UTF-8\")\n\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta) {
            r.append("# Variable Labels / Descriptions\n")
            meta.columns.each { colName, colMeta ->
                String rCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (label) {
                    r.append("if (\"${rCol}\" %in% colnames(df)) attr(df\$${rCol}, \"label\") <- \"${escapeQuote(label)}\"\n")
                }
            }

            r.append("\n# Date & Timestamp Conversions\n")
            meta.columns.each { colName, colMeta ->
                String rCol = sanitizeColumnName(colName)
                String dataType = colMeta.dataType ? colMeta.dataType.toUpperCase() : ""

                boolean isDate = dataType.contains("DATE") && !dataType.contains("TIME")
                boolean isDateTime = dataType.contains("DATETIME") || dataType.contains("TIMESTAMP") || dataType.contains("TIME")

                if (isDate) {
                    r.append("if (\"${rCol}\" %in% colnames(df)) df\$${rCol} <- as.Date(df\$${rCol}, format = \"%Y-%m-%d\")\n")
                } else if (isDateTime) {
                    r.append("if (\"${rCol}\" %in% colnames(df)) df\$${rCol} <- as.POSIXct(df\$${rCol}, format = \"%Y-%m-%d %H:%M:%S\")\n")
                }
            }

            r.append("\n# Categorical Factors & Value Labels\n")
            meta.columns.each { colName, colMeta ->
                String rCol = sanitizeColumnName(colName)
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        List<DataDictionaryService.EnumOptionMetadata> validOptions = enumType.options.findAll {
                            it.code != null && !it.code.trim().isEmpty()
                        }
                        if (!validOptions.isEmpty()) {
                            String levels = validOptions.collect { "\"${escapeQuote(it.code)}\"" }.join(", ")
                            String labels = validOptions.collect { "\"${escapeQuote(it.label)}\"" }.join(", ")
                            r.append("if (\"${rCol}\" %in% colnames(df)) df\$${rCol} <- factor(df\$${rCol}, levels = c(${levels}), labels = c(${labels}))\n")
                        }
                    }
                }
            }
        }

        r.append("\nsummary(df)\n")
        return r.toString()
    }

    private String generateSasScript(ExportRequest request) {
        String tableName = request.datasetName ?: "dataset"
        String sasTable = sanitizeColumnName(tableName)
        if (sasTable.length() > 32) sasTable = sasTable.substring(0, 32)

        StringBuilder sas = new StringBuilder()
        sas.append("/* SAS Import Script for HDSS Data Export: ${tableName} */\n")
        sas.append("/* Optional: Set working directory in SAS Studio / SAS Viya / SASApp Server */\n")
        sas.append("/* Option 1 (SAS Studio / Files Home): */\n")
        sas.append("/* %let home_dir = %sysget(HOME); */\n")
        sas.append("/* %let rc = %sysfunc(dlgcdir(\"&home_dir\")); */\n")
        sas.append("/* Option 2 (SAS Viya Studio Auto-detect Program Path): */\n")
        sas.append("/* %let path = %substr(%sysget(SAS_EXECFILEPATH), 1, %eval(%length(%sysget(SAS_EXECFILEPATH)) - %length(%sysget(SAS_EXECFILENAME)))); */\n")
        sas.append("/* Option 3 (SAS Custom Folder Path): */\n")
        sas.append("/* %let path = /your/sas/folder/path; */\n\n")
        sas.append("PROC IMPORT DATAFILE=\"${tableName}_data.csv\"\n")
        sas.append("    OUT=WORK.${sasTable}\n")
        sas.append("    DBMS=CSV REPLACE;\n")
        sas.append("    GETNAMES=YES;\n")
        sas.append("RUN;\n\n")

        DataDictionaryService.TableMetadata meta = dataDictionaryService.getTableMetadata(tableName)
        if (meta) {
            // 1. Categorical Value Formats via PROC FORMAT
            StringBuilder procFormat = new StringBuilder()
            Map<String, String> columnFormatMap = [:]

            meta.columns.each { colName, colMeta ->
                String sasCol = sanitizeColumnName(colName)
                if (colMeta.enumTypeName) {
                    DataDictionaryService.EnumTypeMetadata enumType = dataDictionaryService.getEnumTypeMetadata(colMeta.enumTypeName)
                    if (enumType && !enumType.options.isEmpty()) {
                        List<DataDictionaryService.EnumOptionMetadata> validOptions = enumType.options.findAll {
                            it.code != null && !it.code.trim().isEmpty()
                        }
                        if (!validOptions.isEmpty()) {
                            boolean isNumericCodes = validOptions.every { opt ->
                                opt.code != null && opt.code.matches(/^-?\d+$/)
                            }

                            String fmtName = sasCol.length() > 28 ? sasCol.substring(0, 28) + "_f" : "${sasCol}_f"
                            String fullFmt = isNumericCodes ? "${fmtName}" : "\$${fmtName}"

                            procFormat.append("    VALUE ${fullFmt}\n")
                            validOptions.each { opt ->
                                String valSpec = isNumericCodes ? "${opt.code}" : "'${escapeQuote(opt.code)}'"
                                procFormat.append("        ${valSpec} = \"${escapeQuote(opt.label)}\"\n")
                            }
                            procFormat.append("    ;\n")
                            columnFormatMap.put(sasCol, "${fullFmt}.")
                        }
                    }
                }
            }

            if (procFormat.length() > 0) {
                sas.append("/* Define Value Formats */\n")
                sas.append("PROC FORMAT;\n")
                sas.append(procFormat.toString())
                sas.append("RUN;\n\n")
            }

            // 2. Apply Variable Labels & Value Formats via PROC DATASETS
            sas.append("/* Apply Variable Labels and Value Formats */\n")
            sas.append("PROC DATASETS LIB=WORK NOLIST;\n")
            sas.append("    MODIFY ${sasTable};\n")

            sas.append("    LABEL\n")
            meta.columns.each { colName, colMeta ->
                String sasCol = sanitizeColumnName(colName)
                String label = getDisplayLabel(colMeta)
                if (label) {
                    sas.append("        ${sasCol} = \"${escapeQuote(label)}\"\n")
                }
            }
            sas.append("    ;\n")

            if (!columnFormatMap.isEmpty()) {
                sas.append("    FORMAT\n")
                columnFormatMap.each { col, fmt ->
                    sas.append("        ${col} ${fmt}\n")
                }
                sas.append("    ;\n")
            }

            sas.append("QUIT;\n")
        }

        return sas.toString()
    }

    // --- Master Script Generators (Single Master Script for Root + All Descendant Repeat Tables) ---

    private String generateMasterSyntaxScript(ExportRequest request) {
        def fmt = request.format
        switch (fmt) {
            case DataExportFormat.SPSS:
                return generateMasterSpssScript(request)
            case DataExportFormat.R:
                return generateMasterRScript(request)
            case DataExportFormat.SAS:
                return generateMasterSasScript(request)
            default:
                return generateMasterStataDoScript(request)
        }
    }

    private String generateMasterStataDoScript(ExportRequest request) {
        List<String> allTables = getAllExportTableNames(request)
        StringBuilder doScript = new StringBuilder()
        doScript.append("/* Master Stata Do-File for HDSS Data Export: ${request.datasetName} */\n")
        doScript.append("/* Optional: Set working directory to folder containing CSV data files */\n")
        doScript.append("/* cd \"/path/to/folder\" */\n\n")
        doScript.append("clear all\n")
        doScript.append("set more off\n\n")

        allTables.eachWithIndex { tableName, idx ->
            doScript.append("/* ==================================================================== */\n")
            doScript.append("/* Table ${idx + 1}: ${tableName} */\n")
            doScript.append("/* ==================================================================== */\n")

            ExportRequest tableReq = new ExportRequest(datasetName: tableName, format: request.format)
            String singleTableScript = generateStataDoScript(tableReq)

            // Strip header "clear all\nset more off\n\n" and cd comments from individual script block
            String cleanedBlock = singleTableScript.replace("clear all\nset more off\n\n", "")
            cleanedBlock = cleanedBlock.replace("/* Optional: Set working directory to folder containing CSV data files */\n/* cd \"/path/to/folder\" */\n\n", "")
            doScript.append(cleanedBlock).append("\n\n")
        }

        return doScript.toString()
    }

    private String generateMasterSpssScript(ExportRequest request) {
        List<String> allTables = getAllExportTableNames(request)
        StringBuilder spss = new StringBuilder()
        spss.append("/* Master SPSS Syntax Script for HDSS Data Export: ${request.datasetName} */\n")
        spss.append("/* Optional: Set working directory to folder containing CSV data files */\n")
        spss.append("/* CD '/path/to/folder'. */\n\n")

        allTables.eachWithIndex { tableName, idx ->
            spss.append("/* ==================================================================== */\n")
            spss.append("/* Table ${idx + 1}: ${tableName} */\n")
            spss.append("/* ==================================================================== */\n")

            ExportRequest tableReq = new ExportRequest(datasetName: tableName, format: request.format)
            String singleTableScript = generateSpssScript(tableReq)
            spss.append(singleTableScript).append("\n\n")
        }

        return spss.toString()
    }

    private String generateMasterRScript(ExportRequest request) {
        List<String> allTables = getAllExportTableNames(request)
        StringBuilder r = new StringBuilder()
        r.append("# Master R Import Script for HDSS Data Export: ${request.datasetName}\n")
        r.append("# Optional: Set working directory to folder containing CSV data files\n")
        r.append("# setwd(\"/path/to/folder\")\n\n")

        List<String> rDataFrames = []

        allTables.eachWithIndex { tableName, idx ->
            String varName = "df_${sanitizeColumnName(tableName)}"
            rDataFrames.add(varName)

            r.append("# ====================================================================\n")
            r.append("# Table ${idx + 1}: ${tableName}\n")
            r.append("# ====================================================================\n")

            ExportRequest tableReq = new ExportRequest(datasetName: tableName, format: request.format)
            String singleTableScript = generateRScript(tableReq)

            // Strip header comments from individual block
            String cleanedBlock = singleTableScript.replace("# Optional: Set working directory to folder containing CSV data files\n# setwd(\"/path/to/folder\")\n\n", "")

            // Customize data frame variable name from df to df_<tableName>
            String customizedScript = cleanedBlock
                    .replace("df <- read.csv(\"${tableName}_data.csv\"", "${varName} <- read.csv(\"${tableName}_data.csv\"")
                    .replace("df\$", "${varName}\$")
                    .replace("summary(df)", "summary(${varName})")

            r.append(customizedScript).append("\n\n")
        }

        if (!rDataFrames.isEmpty()) {
            String dfListStr = rDataFrames.join(", ")
            r.append("# Save all data frames into a single R Workspace file\n")
            r.append("save(${dfListStr}, file = \"${request.datasetName}_workspace.RData\")\n")
        }

        return r.toString()
    }

    private String generateMasterSasScript(ExportRequest request) {
        List<String> allTables = getAllExportTableNames(request)
        StringBuilder sas = new StringBuilder()
        sas.append("/* Master SAS Import Script for HDSS Data Export: ${request.datasetName} */\n")
        sas.append("/* Optional: Set working directory in SAS Studio / SAS Viya / SASApp Server */\n")
        sas.append("/* Option 1 (SAS Studio / Files Home): */\n")
        sas.append("/* %let home_dir = %sysget(HOME); */\n")
        sas.append("/* %let rc = %sysfunc(dlgcdir(\"&home_dir\")); */\n")
        sas.append("/* Option 2 (SAS Viya Studio Auto-detect Program Path): */\n")
        sas.append("/* %let path = %substr(%sysget(SAS_EXECFILEPATH), 1, %eval(%length(%sysget(SAS_EXECFILEPATH)) - %length(%sysget(SAS_EXECFILENAME)))); */\n")
        sas.append("/* Option 3 (SAS Custom Folder Path): */\n")
        sas.append("/* %let path = /your/sas/folder/path; */\n\n")

        allTables.eachWithIndex { tableName, idx ->
            sas.append("/* ==================================================================== */\n")
            sas.append("/* Table ${idx + 1}: ${tableName} */\n")
            sas.append("/* ==================================================================== */\n")

            ExportRequest tableReq = new ExportRequest(datasetName: tableName, format: request.format)
            String singleTableScript = generateSasScript(tableReq)
            sas.append(singleTableScript).append("\n\n")
        }

        return sas.toString()
    }

    // --- Helper Utilities ---

    private List<String> getAllExportTableNames(ExportRequest request) {
        List<String> tableNames = [request.datasetName]
        if (request.hasRepeatTables) {
            List<DataDictionaryService.TableMetadata> descendants = dataDictionaryService.getAllDescendantRepeatTables(request.datasetName)
            if (descendants) {
                descendants.each { tableNames.add(it.tableName) }
            }
        }
        return tableNames
    }

    private static String getDisplayLabel(DataDictionaryService.ColumnMetadata colMeta) {
        if (!colMeta) return ""
        if (colMeta.label && colMeta.label != colMeta.columnName) {
            return colMeta.label
        }
        String name = colMeta.columnName ?: ""
        return name.split("_").collect { it.capitalize() }.join(" ")
    }

    private static String sanitizeColumnName(String colName) {
        if (!colName) return "col"
        String sanitized = colName.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase()
        if (sanitized.length() > 32) {
            sanitized = sanitized.substring(0, 32)
        }
        return sanitized
    }

    private static String escapeCsv(String val) {
        if (val == null) return ""
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\""
        }
        return val
    }

    private static String escapeQuote(String val) {
        return val ? val.replace("\"", "\\\"") : ""
    }

}
