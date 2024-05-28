package org.philimone.hds.explorer.server.settings.generator

public enum CodeGeneratorIncrementalRule {

    FILL_GAPS ("FILL_GAPS", "settings.parameters.codegenerator.incremental.rule.fillgaps.label"),
    INCREMENT_LAST_CODE ("INCREMENT_LAST_CODE", "settings.parameters.codegenerator.incremental.rule.inc_last_code.label")

    String code
    String name

    CodeGeneratorIncrementalRule(String code, String name){
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
    private static final Map<String, CodeGeneratorIncrementalRule> MAP = new HashMap<>()

    static {
        for (CodeGeneratorIncrementalRule e: values()) {
            MAP.put(e.code, e)
        }
    }

    static CodeGeneratorIncrementalRule getFrom(String code) {
        return code==null ? null : MAP.get(code)
    }
}