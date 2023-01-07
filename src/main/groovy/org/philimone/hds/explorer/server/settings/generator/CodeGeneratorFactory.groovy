package org.philimone.hds.explorer.server.settings.generator

import org.philimone.hds.explorer.server.model.settings.Codes

class CodeGeneratorFactory {

    static CodeGenerator newInstance(){
        def codeGeneratorClassName = Codes.SYSTEM_CODE_GENERATOR
        def codeGenClass = Class.forName("${codeGeneratorClassName}")
        def result = (CodeGenerator) codeGenClass.newInstance()

        println "generator: ${codeGeneratorClassName}, result: ${result}"

        return result //to use a different code patterns, implements CodeGenerator and instatiate the class here
    }

}
