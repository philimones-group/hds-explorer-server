package org.philimone.hds.explorer.server.model.enums.settings

enum SettingsExportHistoryMode {
    FULL       ("FULL",   "settings.parameters.export.history.mode.full.label") ,        //Full History
    LAST_STATE ("LAST_STATE","settings.parameters.export.history.mode.last_state.label") //Last State Only (Recommended for large sites)

    String code
    String name

    SettingsExportHistoryMode(String code, String name){
        this.code = code
        this.name = name
    }

    String getId(){
        return code
    }

    @Override
    String toString() {
        return name
    }

    /* Finding Enum by code */
    private static final Map<String, SettingsExportHistoryMode> MAP = new HashMap<>()

    static {
        for (SettingsExportHistoryMode e: values()) {
            MAP.put(e.code, e)
        }
    }

    static SettingsExportHistoryMode getFrom(String code) {
        return code==null ? FULL : MAP.get(code)
    }
}