package org.philimone.hds.explorer.server.model.settings.generator

class CodeGeneratorFactory {

    static CodeGenerator newInstance(){
        return new DefaultCodeGenerator() //to use a different code patterns, implements CodeGenerator and instatiate the class here
    }

}
