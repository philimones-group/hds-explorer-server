/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import grails.util.GrailsNameUtils
import org.grails.validation.DomainClassPropertyComparator
import grails.validation.ConstrainedProperty

includeTargets << grailsScript("Init")
includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")

generateForName = null

target(main: "Generates i18n messages for domain classes") {
    depends(checkVersion, parseArguments, packageApp)
    promptForName(type: "Domain Class")
    generateI18nMessages()
}

setDefaultTarget(main)

target(generateI18nMessages: "The implementation target") {
    try {
        def name = argsMap["params"][0]
        if (!name || name == "*") {
            uberGenerate()
        }
        else {
            generateForName = name
            generateForOne()
        }
    }
    catch (Exception e) {
        logError("Error running generate-i18n-messages", e)
        exit(1)
    }
}

target(generateForOne: "Generates i18n messages for only one domain class") {
    depends(loadApp)

    def name = generateForName
    name = name.indexOf('.') > -1 ? name : GrailsNameUtils.getClassNameRepresentation(name)
    def domainClass = grailsApp.getDomainClass(name)

    if (!domainClass) {
        println "Domain class not found in grails-app/domain, trying hibernate mapped classes..."
        bootstrap()
        domainClass = grailsApp.getDomainClass(name)
    }

    if (domainClass) {
        generateForDomainClass(domainClass)
        event("StatusFinal", ["XFinished generation for domain class ${domainClass.fullName}. Copy messages to appropriate resource bundle(s)"])
    }
    else {
        event("StatusFinal", ["No domain class found for name ${name}. Please try again and enter a valid domain class name"])
    }
}

target(uberGenerate: "Generates i18n messages for all domain classes") {
    depends(loadApp)

    def domainClasses = grailsApp.domainClasses

    if (!domainClasses) {
        println "No domain classes found in grails-app/domain, trying hibernate mapped classes..."
        bootstrap()
        domainClasses = grailsApp.domainClasses
    }

   if (domainClasses) {
        domainClasses.each { domainClass ->
            generateForDomainClass(domainClass)
        }
        event("StatusFinal", ["XXFinished generation for domain classes. Copy messages to appropriate resource bundle(s)"])
    }
    else {
        event("StatusFinal", ["No domain classes found"])
    }
}

def generateForDomainClass(domainClass) {
    // print generic messages for this domain class
    println "# ${domainClass.shortName} messages, Customized by X-47"
    println "${domainClass.propertyName}.label=${domainClass.shortName}"
    println "${domainClass.propertyName}.add.label=Adicionar ${domainClass.shortName}"
    println "${domainClass.propertyName}.import.label=Importar ${domainClass.shortName}"
    println "${domainClass.propertyName}.search.label=Procurar ${domainClass.shortName}"
    println "${domainClass.propertyName}.create.label=Criar ${domainClass.shortName}"
    println "${domainClass.propertyName}.edit.label=Alterar ${domainClass.shortName}"
    println "${domainClass.propertyName}.list.label=Lista de ${domainClass.shortName}"
    println "${domainClass.propertyName}.new.label=Novo ${domainClass.shortName}"
    println "${domainClass.propertyName}.show.label=Visualizar ${domainClass.shortName}"

    println "${domainClass.propertyName}.create.button.label=Registar ${domainClass.shortName}"
    println "${domainClass.propertyName}.update.button.label=Atualizar ${domainClass.shortName}"
    println "${domainClass.propertyName}.delete.button.label=Apagar ${domainClass.shortName}"
    println "${domainClass.propertyName}.edit.button.label=Alterar ${domainClass.shortName}"

    println "${domainClass.propertyName}.created=${domainClass.shortName} {0} foi registado"
    println "${domainClass.propertyName}.updated=${domainClass.shortName} {0} foi actualizado"
    println "${domainClass.propertyName}.deleted=${domainClass.shortName} {0} foi apagado"
    println "${domainClass.propertyName}.not.found=${domainClass.shortName} com id {0} não foi encontrado"
    println "${domainClass.propertyName}.not.deleted=${domainClass.shortName} com id {0} não foi apagado"
    println "${domainClass.propertyName}.optimistic.locking.failure=Outro usuário alterou os dados do formulário ${domainClass.shortName} enquanto estavas editando"

    // print messages for all properties contained by domain class
    props = domainClass.properties.findAll { it.name != 'version' }
    Collections.sort(props, new DomainClassPropertyComparator(domainClass))
    props.each { p ->
        println "${domainClass.propertyName}.${p.name}.label=${p.naturalName}"

        // print messages for inList constaint values
        cp = domainClass.constrainedProperties[p.name]
        if (cp?.inList) {
            cp.inList.each { v ->
                println "${domainClass.propertyName}.${p.name}.${v}=${v}"
            }
        }

        // print error messages for constraints
        cp?.appliedConstraints?.each { c ->
            switch (c.name) {
                case ConstrainedProperty.BLANK_CONSTRAINT:
                    if (!c.parameter)
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] não pode estar em branco"
                    break
                case ConstrainedProperty.CREDIT_CARD_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não é um número válido de cartão de crédito"
                    break
                case ConstrainedProperty.EMAIL_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não é um endereço de email válido"
                    break
                case ConstrainedProperty.IN_LIST_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não pertence a lista [{3}]"
                    break
                case ConstrainedProperty.MATCHES_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não corresponde ao padrão requerido [{3}]"
                    break
                case ConstrainedProperty.MAX_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] excede o valor máximo permitido [{3}]"
                    break
                case ConstrainedProperty.MAX_SIZE_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] excede o tamanho máximo de [{3}]"
                    break
                case ConstrainedProperty.MIN_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] é menor que o valor mínimo [{3}]"
                    break
                case ConstrainedProperty.MIN_SIZE_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] é menor que o tamanho mínimo de [{3}]"
                    break
                case ConstrainedProperty.NOT_EQUAL_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não pode ser igual à [{3}]"
                    break
                case ConstrainedProperty.NULLABLE_CONSTRAINT:
                    if (!c.nullable)
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] não pode estar vazio"
                    break
                case ConstrainedProperty.RANGE_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não pertence ao intervalo de [{3}] á [{4}]"
                    break
                case ConstrainedProperty.SIZE_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não pertence ao intervalo dos tamanhos de [{3}] á [{4}]"
                    break
                //case ConstrainedProperty.UNIQUE_CONSTRAINT: // unique constraint reference not available
                //    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] deve ser unico"
                //    break
                case ConstrainedProperty.URL_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não é uma URL válida"
                    break
                case ConstrainedProperty.VALIDATOR_CONSTRAINT:
                    println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não passou na validação"
                    break


            }
        }
    }
    println ""
}
