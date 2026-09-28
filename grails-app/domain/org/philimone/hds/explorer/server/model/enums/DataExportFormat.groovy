package org.philimone.hds.explorer.server.model.enums

enum DataExportFormat {

    CSV    ("CSV", "dataExportFormat.csv", "dataExportFormat.csv.desc", "CSV / TSV", "Universal plain text comma/tab delimited file.", "data_export_csv.png"),
    EXCEL  ("EXCEL", "dataExportFormat.excel", "dataExportFormat.excel.desc", "Excel (.xlsx)", "Streaming Excel spreadsheet with Data Dictionary tab.", "data_export_excel.png"),
    STATA  ("STATA", "dataExportFormat.stata", "dataExportFormat.stata.desc", "Stata (.do)", "Do-file import script + CSV dataset.", "data_export_stata.png"),
    SPSS   ("SPSS", "dataExportFormat.spss", "dataExportFormat.spss.desc", "SPSS (.sps)", "Syntax file with variable and value labels.", "data_export_spss.png"),
    R      ("R", "dataExportFormat.r", "dataExportFormat.r.desc", "R (.R)", "R script setting up data frame factors.", "data_export_r.png"),
    SAS    ("SAS", "dataExportFormat.sas", "dataExportFormat.sas.desc", "SAS (.sas)", "SAS PROC IMPORT syntax script.", "data_export_sas.png")

    String code
    String name        // i18n title key
    String description // i18n description key
    String title       // default title
    String desc        // default description
    String icon        // asset image filename

    DataExportFormat(String code, String name, String description, String title, String desc, String icon) {
        this.code = code
        this.name = name
        this.description = description
        this.title = title
        this.desc = desc
        this.icon = icon
    }

    String getId() {
        return code
    }

    @Override
    String toString() {
        return name
    }

    private static final Map<String, DataExportFormat> MAP = new HashMap<>()

    static {
        for (DataExportFormat e : values()) {
            MAP.put(e.code, e)
            MAP.put(e.code.toLowerCase(), e)
        }
    }

    public static DataExportFormat getFrom(String code) {
        if (code == null) return null
        String key = code.trim().toUpperCase()
        return MAP.get(key)
    }
}
