package org.philimone.hds.explorer.server


import grails.dev.commands.*
import grails.core.GrailsApplication
import grails.gorm.validation.ConstrainedProperty
import grails.util.GrailsNameUtils
import org.grails.validation.DomainClassPropertyComparator

class MessagesCommand implements GrailsApplicationCommand {

    GrailsApplication grailsApp
    def currentLanguage = I18nLanguage.ENGLISH
    def log = org.slf4j.LoggerFactory.getLogger('logger');


    boolean handle() {

        def command = executionContext.commandLine

        //println command.remainingArgs

        def lang = getLanguageFromArguments(command.remainingArgs)
        def args = removeLanguageFromArguments(command.remainingArgs)


        if (lang == I18nLanguage.ALL) {

            I18nLanguage.values().each { language ->
                if (language != I18nLanguage.ALL) {
                    currentLanguage = language
                    startGenerator(args)
                }
            }


        } else {
            currentLanguage = lang
            startGenerator(args)
        }



        return true
    }

    def logError(message, Exception e){
        log.info(message)
        e.printStackTrace()
    }

    def exit(int code){
        System.exit(code)
    }

    List<String> removeLanguageFromArguments(List<String> arguments){

        List<String> args = []

        args.addAll(arguments)

        def remove = []
        I18nLanguage.values().each {
            remove.add(it.name)
        }

        args.removeAll(remove)

        return args
    }

    I18nLanguage getLanguageFromArguments(List<String> args){

        if (args.size()>0) {
            def lang = I18nLanguage.getLanguage(args[0])

            if (lang != null) return lang;
        }

        return I18nLanguage.ENGLISH
    }



    def startGenerator(args){
        println "Starting i18n-messages generator"

        try {

            if (args.size==0){
                generateForAllDomains() //In English
            }

            //First Argument should be the language

            if (args.size==1){
                //one domain
                if (args[0]=="*"){
                    generateForAllDomains()
                } else {
                    generateForOne(args[0])
                }

            } else {
                //generate for specified domains

                args.each { domain ->
                    generateForOne(domain)
                }
            }
        }
        catch (Exception e) {
            logError("Error running messages en", e)
            exit(0)
        }
    }


    def generateForAllDomains(){
        def domainClasses = grailsApp.domainClasses

        if (!domainClasses) {
            println "No domain classes found in grails-app/domain, trying hibernate mapped classes..."
            domainClasses = grailsApp.domainClasses
        }

        if (domainClasses) {
            domainClasses.each { domainClass ->
                generateForDomainClass(domainClass)
            }
            println "Finished generation for domain classes. Copy messages to appropriate resource bundle(s)"
        }
        else {
            println "No domain classes found"
        }
    }

    def generateForOne(domainName){

        def name = domainName
        name = name.indexOf('.') > -1 ? name : GrailsNameUtils.getClassNameRepresentation(name)
        def domainClass = grailsApp.getDomainClass(name)

        if (!domainClass) {
            println "Domain class not found in grails-app/domain, trying hibernate mapped classes..."
            //bootstrap()
            domainClass = grailsApp.getDomainClass(name)
        }

        if (domainClass) {
            generateForDomainClass(domainClass)
            println "Finished generation for domain class ${domainClass.fullName}. Copy messages to appropriate resource bundle(s)"
        }
        else {
            println "No domain class found for name ${name}. Please try again and enter a valid domain class name"
        }
    }

    def generateForDomainClass(domainClass) {

        if (currentLanguage == I18nLanguage.ENGLISH){
            generateForDomainClassEn(domainClass)
        }

        if (currentLanguage == I18nLanguage.PORTUGUESE){
            generateForDomainClassPt(domainClass)
        }

        if (currentLanguage == I18nLanguage.FRENCH){
            generateForDomainClassFr(domainClass)
        }
    }

    def generateForDomainClassEn(domainClass) {
        // print generic messages for this domain class
        println "# ${domainClass.shortName} messages, Customized by X-47"
        println "${domainClass.propertyName}.label=${domainClass.shortName}"
        println "${domainClass.propertyName}.add.label=Add ${domainClass.shortName}"
        println "${domainClass.propertyName}.import.label=Import ${domainClass.shortName}"
        println "${domainClass.propertyName}.search.label=Search ${domainClass.shortName}"
        println "${domainClass.propertyName}.create.label=Create ${domainClass.shortName}"
        println "${domainClass.propertyName}.edit.label=Edit ${domainClass.shortName}"
        println "${domainClass.propertyName}.list.label=${domainClass.shortName} List"
        println "${domainClass.propertyName}.new.label=New ${domainClass.shortName}"
        println "${domainClass.propertyName}.show.label=Show ${domainClass.shortName}"

        println "${domainClass.propertyName}.create.button.label=Save ${domainClass.shortName}"
        println "${domainClass.propertyName}.update.button.label=Update ${domainClass.shortName}"
        println "${domainClass.propertyName}.delete.button.label=Delete ${domainClass.shortName}"
        println "${domainClass.propertyName}.edit.button.label=Edit ${domainClass.shortName}"

        println "${domainClass.propertyName}.created=${domainClass.shortName} {0} created"
        println "${domainClass.propertyName}.updated=${domainClass.shortName} {0} updated"
        println "${domainClass.propertyName}.deleted=${domainClass.shortName} {0} deleted"
        println "${domainClass.propertyName}.not.found=${domainClass.shortName} not found with id {0}"
        println "${domainClass.propertyName}.not.deleted=${domainClass.shortName} not deleted with id {0}"
        println "${domainClass.propertyName}.optimistic.locking.failure=Another user has updated this ${domainClass.shortName} while you were editing"

        // print messages for all properties contained by domain class
        def props = domainClass.properties.findAll { it.name != 'version' }
        Collections.sort(props, new DomainClassPropertyComparator(domainClass))
        props.each { p ->
            println "${domainClass.propertyName}.${p.name}.label=${p.naturalName}"

            // print messages for inList constraint values
            def cp = domainClass.constrainedProperties[p.name]
            if (cp?.inList) {
                cp.inList.each { v ->
                    println "${domainClass.propertyName}.${p.name}.${v}=${v}"
                }
            }

            // print error messages for constraints
            cp?.appliedConstraints?.each { c ->
                //println("constraint: ${c.name}")
                switch (c.name) {
                    case ConstrainedProperty.BLANK_CONSTRAINT:
                        if (!c.parameter)
                            println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] cannot be blank"
                        break
                    case ConstrainedProperty.CREDIT_CARD_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] is not a valid credit card number"
                        break
                    case ConstrainedProperty.EMAIL_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] is not a valid e-mail address"
                        break
                    case ConstrainedProperty.IN_LIST_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] is not contained within the list [{3}]"
                        break
                    case ConstrainedProperty.MATCHES_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] does not match the required pattern [{3}]"
                        break
                    case ConstrainedProperty.MAX_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] exceeds maximum value [{3}]"
                        break
                    case ConstrainedProperty.MAX_SIZE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] exceeds the maximum size of [{3}]"
                        break
                    case ConstrainedProperty.MIN_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] is less than minimum value [{3}]"
                        break
                    case ConstrainedProperty.MIN_SIZE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] is less than the minimum size of [{3}]"
                        break
                    case ConstrainedProperty.NOT_EQUAL_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] cannot equal [{3}]"
                        break
                    case ConstrainedProperty.NULLABLE_CONSTRAINT:
                        if (!c.parameter)
                            println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] cannot be null"
                        break
                    case ConstrainedProperty.RANGE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] does not fall within the valid range from [{3}] to [{4}]"
                        break
                    case ConstrainedProperty.SIZE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] does not fall within the valid size range from [{3}] to [{4}]"
                        break
                    case ConstrainedExtraProperty.UNIQUE_CONSTRAINT: // unique constraint reference not available
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] must be unique"
                        break
                    case ConstrainedProperty.URL_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] is not a valid URL"
                        break
                    case ConstrainedProperty.VALIDATOR_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Property [${p.naturalName}] of class [${domainClass.shortName}] with value [{2}] does not pass custom validation"
                        break
                }
            }
        }

        println ""
    }

    def generateForDomainClassPt(domainClass) {
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
        def props = domainClass.properties.findAll { it.name != 'version' }
        Collections.sort(props, new DomainClassPropertyComparator(domainClass))
        props.each { p ->
            println "${domainClass.propertyName}.${p.name}.label=${p.naturalName}"

            // print messages for inList constaint values
            def cp = domainClass.constrainedProperties[p.name]
            if (cp?.inList) {
                cp.inList.each { v ->
                    println "${domainClass.propertyName}.${p.name}.${v}=${v}"
                }
            }

            // print error messages for constraints
            cp?.appliedConstraints?.each { c ->
                //println("constraint: ${c.name}")
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
                        if (!c.parameter)
                            println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] não pode estar vazio"
                        break
                    case ConstrainedProperty.RANGE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não pertence ao intervalo de [{3}] á [{4}]"
                        break
                    case ConstrainedProperty.SIZE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] não pertence ao intervalo dos tamanhos de [{3}] á [{4}]"
                        break
                    case ConstrainedExtraProperty.UNIQUE_CONSTRAINT: // unique constraint reference not available
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=O campo [${p.naturalName}] do formulário [${domainClass.shortName}] com o valor [{2}] deve ser unico"
                        break
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

    def generateForDomainClassFr(domainClass) {
        // print generic messages for this domain class
        println "# ${domainClass.shortName} messages, Customized by X-47"
        println "${domainClass.propertyName}.label=${domainClass.shortName}"
        println "${domainClass.propertyName}.add.label=Ajouter ${domainClass.shortName}"
        println "${domainClass.propertyName}.import.label=Importer ${domainClass.shortName}"
        println "${domainClass.propertyName}.search.label=Rechercher ${domainClass.shortName}"
        println "${domainClass.propertyName}.create.label=Créer ${domainClass.shortName}"
        println "${domainClass.propertyName}.edit.label=Modifier ${domainClass.shortName}"
        println "${domainClass.propertyName}.list.label=Liste de la ${domainClass.shortName}"
        println "${domainClass.propertyName}.new.label=Ajouter une nouvelle ${domainClass.shortName}"
        println "${domainClass.propertyName}.show.label=Afficher ${domainClass.shortName}"

        println "${domainClass.propertyName}.create.button.label=Sauvegarder ${domainClass.shortName}"
        println "${domainClass.propertyName}.update.button.label==Mise à jour de la ${domainClass.shortName}"
        println "${domainClass.propertyName}.delete.button.label=Supprimer ${domainClass.shortName}"
        println "${domainClass.propertyName}.edit.button.label=Edit ${domainClass.shortName}"

        println "${domainClass.propertyName}.created=${domainClass.shortName} {0} créée"
        println "${domainClass.propertyName}.updated=${domainClass.shortName} {0} mise à jour"
        println "${domainClass.propertyName}.deleted=${domainClass.shortName} {0} supprimée"
        println "${domainClass.propertyName}.not.found=${domainClass.shortName} non trouvé avec l'id {0}"
        println "${domainClass.propertyName}.not.deleted=${domainClass.shortName} non supprimée avec id{0}"
        println "${domainClass.propertyName}.optimistic.locking.failure=Un autre utilisateur a mis à jour cette ${domainClass.shortName} pendant que vous changiez"

        // print messages for all properties contained by domain class
        def props = domainClass.properties.findAll { it.name != 'version' }
        Collections.sort(props, new DomainClassPropertyComparator(domainClass))
        props.each { p ->
            println "${domainClass.propertyName}.${p.name}.label=${p.naturalName}"

            // print messages for inList constraint values
            def cp = domainClass.constrainedProperties[p.name]
            if (cp?.inList) {
                cp.inList.each { v ->
                    println "${domainClass.propertyName}.${p.name}.${v}=${v}"
                }
            }

            // print error messages for constraints
            cp?.appliedConstraints?.each { c ->
                //println("constraint: ${c.name}")
                switch (c.name) {
                    case ConstrainedProperty.BLANK_CONSTRAINT:
                        if (!c.parameter)
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] ne peut pas être vide"
                        break
                    case ConstrainedProperty.CREDIT_CARD_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] n'est pas un numéro de carte de crédit valide"
                        break
                    case ConstrainedProperty.EMAIL_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] n'est pas une adresse e-mail valide"
                        break
                    case ConstrainedProperty.IN_LIST_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] ne fait pas partie de la liste [{3}]"
                        break
                    case ConstrainedProperty.MATCHES_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] ne correspond pas au pattern [{3}]"
                        break
                    case ConstrainedProperty.MAX_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] est supérieure à la valeur maximum [{3}]"
                        break
                    case ConstrainedProperty.MAX_SIZE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] est supérieure à la valeur maximum [{3}]"
                        break
                    case ConstrainedProperty.MIN_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] est inférieure à la valeur minimum [{3}]"
                        break
                    case ConstrainedProperty.MIN_SIZE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] est inférieure à la valeur minimum [{3}]"
                        break
                    case ConstrainedProperty.NOT_EQUAL_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] ne peut pas être égale à [{3}]"
                        break
                    case ConstrainedProperty.NULLABLE_CONSTRAINT:
                        if (!c.parameter)
                            println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] ne peut pas être nulle"
                        break
                    case ConstrainedProperty.RANGE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] n'est pas contenue dans l'intervalle [{3}] à [{4}]"
                        break
                    case ConstrainedProperty.SIZE_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] n'est pas contenue dans l'intervalle [{3}] à [{4}]"
                        break
                    case ConstrainedExtraProperty.UNIQUE_CONSTRAINT: // unique constraint reference not available
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] doit être unique"
                        break
                    case ConstrainedProperty.URL_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] n'est pas une URL valide"
                        break
                    case ConstrainedProperty.VALIDATOR_CONSTRAINT:
                        println "${domainClass.propertyName}.${p.name}.${c.name}.error=Le champ [${p.naturalName}] de la classe [${domainClass.shortName}] avec la valeur [{2}] n'est pas valide"
                        break
                }
            }
        }

        println ""
    }

    //constants
    class ConstrainedExtraProperty {
        public static final UNIQUE_CONSTRAINT = "unique"
    }

    enum I18nLanguage {

        ALL("all"),

        ENGLISH("en"),
        PORTUGUESE("pt"),
        FRENCH("fr")

        private String name;
        private static Map<String, I18nLanguage> mapLangs = new HashMap<>();

        static {
            for (I18nLanguage lang : I18nLanguage.values()) {
                mapLangs.put(lang.getName(), lang);
            }
        }

        I18nLanguage(String name){
            this.name = name;
        }

        String getName() {
            return name
        }

        void setName(String name) {
            this.name = name
        }

        public static I18nLanguage getLanguage(String abbrv){
            return mapLangs.get(abbrv)
        }
    }

}
