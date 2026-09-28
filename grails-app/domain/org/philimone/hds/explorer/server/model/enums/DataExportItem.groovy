package org.philimone.hds.explorer.server.model.enums

enum DataExportItem {

    REGULAR_TABLE            ("REGULAR_TABLE", "dataExportItem.rawTable", false),
    RESIDENCY_PERSON_TIME    ("RESIDENCY_PERSON_TIME", "dataExportItem.residencyPersonTime", true),
    MATERNAL_PREGNANCY       ("MATERNAL_PREGNANCY", "dataExportItem.maternalPregnancy", true),
    HOUSEHOLD_COMPOSITION    ("HOUSEHOLD_COMPOSITION", "dataExportItem.householdComposition", true),
    INDEPTH_RESIDENCY_PERSON_TIME ("INDEPTH_CORE_RESIDENCY", "dataExportItem.indepthCoreResidency", true),
    INDEPTH_MATERNAL_PREGNANCY ("INDEPTH_MATERNAL_PREGNANCY", "dataExportItem.indepthMaternalPregnancy", true),
    INDEPTH_HOUSEHOLD_COMPOSITION ("INDEPTH_HOUSEHOLD_COMPOSITION", "dataExportItem.indepthHouseholdComposition", true),
    DYNAMIC_FORM             ("DYNAMIC_FORM", "dataExportItem.dynamicForm", false)

    String code
    String name
    boolean isPrejoinedView

    DataExportItem(String code, String name, boolean isPrejoinedView) {
        this.code = code
        this.name = name
        this.isPrejoinedView = isPrejoinedView
    }

    String getId() {
        return code
    }

    String getExactName() {
        return isPrejoinedView ? "dataExportItem.joinedViews" : name
    }

    @Override
    String toString() {
        return name
    }

    static List<DataExportItem> getPrejoinedViews() {
        return values().findAll { it.isPrejoinedView }
    }

    /* Finding Enum by code */
    private static final Map<String, DataExportItem> MAP = new HashMap<>()

    static {
        for (DataExportItem e : values()) {
            MAP.put(e.code, e)
        }
    }

    public static DataExportItem getFrom(String code) {
        return code == null ? null : MAP.get(code)
    }
}
