package org.philimone.hds.explorer.server.settings.generator

import org.philimone.hds.explorer.server.model.settings.Codes

class CodeGeneratorFactory {

    public static CodeGeneratorIncrementalRule INCREMENTAL_RULE = CodeGeneratorIncrementalRule.FILL_GAPS;

    static CodeGenerator newInstance(){
        def codeGeneratorClassName = Codes.SYSTEM_CODE_GENERATOR
        def codeGenClass = Class.forName("${codeGeneratorClassName}")
        def result = (CodeGenerator) codeGenClass.newInstance()


        INCREMENTAL_RULE = CodeGeneratorIncrementalRule.getFrom(Codes.SYSTEM_CODE_GENERATOR_INCREMENTAL_RULE)

        println "generator: ${codeGeneratorClassName}, result: ${result}, rule: ${INCREMENTAL_RULE.code}"

        return result //to use a different code patterns, implements CodeGenerator and instatiate the class here
    }

}
