package org.philimone.hds.explorer.server


import grails.dev.commands.*
import grails.core.GrailsApplication
import grails.gorm.validation.ConstrainedProperty
import grails.util.GrailsNameUtils
import grails.validation.ConstrainedDelegate
import net.betainteractive.utilities.StringUtil
import org.grails.core.io.support.GrailsFactoriesLoader
import org.grails.datastore.mapping.model.PersistentEntity
import org.grails.datastore.mapping.model.PersistentProperty
import org.grails.validation.discovery.ConstrainedDiscovery
import org.philimone.utilities.grails.PersistentPropertyComparator

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
        def domainClasses = grailsApp.mappingContext.persistentEntities

        if (!domainClasses) {
            println "No domain classes found in grails-app/domain, trying hibernate mapped classes..."
            domainClasses = grailsApp.mappingContext.persistentEntities
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
        def domainClass = grailsApp.mappingContext.getPersistentEntity(name)

        if (!domainClass) {
            println "Domain class not found in grails-app/domain, trying hibernate mapped classes..."
            //bootstrap()
            domainClass = grailsApp.mappingContext.getPersistentEntity(name)
        }

        if (domainClass) {
            generateForDomainClass(domainClass)
            println "Finished generation for domain class ${getDomainShortname(domainClass.name)}. Copy messages to appropriate resource bundle(s)"
        }
        else {
            println "No domain class found for name ${name}. Please try again and enter a valid domain class name"
        }
    }

    def generateForDomainClass(PersistentEntity domainClass) {

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

    String getDomainShortname(String name) {
        try {
            return name.substring(name.lastIndexOf(".")+1)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
        
        return name        
    }
    
    String getNaturalName(PersistentProperty property) {        
        return StringUtil.removePascalCase(property.getCapitilizedName())
    }
    
    def generateForDomainClassEn(PersistentEntity  domainClass) {
        // print generic messages for this domain class
        
        String domainShortName = getDomainShortname(domainClass.name)
        String domainPropertyName = domainClass.decapitalizedName;
        
        println "# ${domainShortName} messages, Customized by X-47"
        println "${domainPropertyName}.label=${domainShortName}"
        println "${domainPropertyName}.add.label=Add ${domainShortName}"
        println "${domainPropertyName}.import.label=Import ${domainShortName}"
        println "${domainPropertyName}.search.label=Search ${domainShortName}"
        println "${domainPropertyName}.create.label=Create ${domainShortName}"
        println "${domainPropertyName}.edit.label=Edit ${domainShortName}"
        println "${domainPropertyName}.list.label=${domainShortName} List"
        println "${domainPropertyName}.new.label=New ${domainShortName}"
        println "${domainPropertyName}.show.label=Show ${domainShortName}"

        println "${domainPropertyName}.create.button.label=Save ${domainShortName}"
        println "${domainPropertyName}.update.button.label=Update ${domainShortName}"
        println "${domainPropertyName}.delete.button.label=Delete ${domainShortName}"
        println "${domainPropertyName}.edit.button.label=Edit ${domainShortName}"

        println "${domainPropertyName}.created=${domainShortName} {0} created"
        println "${domainPropertyName}.updated=${domainShortName} {0} updated"
        println "${domainPropertyName}.deleted=${domainShortName} {0} deleted"
        println "${domainPropertyName}.not.found=${domainShortName} not found with id {0}"
        println "${domainPropertyName}.not.deleted=${domainShortName} not deleted with id {0}"
        println "${domainPropertyName}.optimistic.locking.failure=Another user has updated this ${domainShortName} while you were editing"

        // print messages for all properties contained by domain class
        def props = domainClass.persistentProperties.findAll { it.name != 'version' }
        Collections.sort(props, new PersistentPropertyComparator(domainClass))
        
        props.each { p ->
            println "${domainPropertyName}.${p.name}.label=${getNaturalName(p)}"

            ConstrainedDiscovery constrainedDiscovery = GrailsFactoriesLoader.loadFactory(ConstrainedDiscovery.class);
            Map constrainedProperties = constrainedDiscovery.findConstrainedProperties(domainClass);

            // print messages for inList constraint values
            def cp = (ConstrainedDelegate) constrainedProperties[p.name]

            if (cp?.inList) {
                cp.inList.each { v ->
                    println "${domainPropertyName}.${p.name}.${v}=${v}"
                }
            }

            // print error messages for constraints
            cp?.appliedConstraints?.each { c ->//cp?.appliedConstraints?.each { c ->
                //println("constraint: ${c.name}")
                switch (c.name) {
                    case ConstrainedProperty.BLANK_CONSTRAINT:
                        if (!c.parameter)
                            println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] cannot be blank"
                        break
                    case ConstrainedProperty.CREDIT_CARD_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] is not a valid credit card number"
                        break
                    case ConstrainedProperty.EMAIL_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] is not a valid e-mail address"
                        break
                    case ConstrainedProperty.IN_LIST_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] is not contained within the list [{3}]"
                        break
                    case ConstrainedProperty.MATCHES_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] does not match the required pattern [{3}]"
                        break
                    case ConstrainedProperty.MAX_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] exceeds maximum value [{3}]"
                        break
                    case ConstrainedProperty.MAX_SIZE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] exceeds the maximum size of [{3}]"
                        break
                    case ConstrainedProperty.MIN_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] is less than minimum value [{3}]"
                        break
                    case ConstrainedProperty.MIN_SIZE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] is less than the minimum size of [{3}]"
                        break
                    case ConstrainedProperty.NOT_EQUAL_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] cannot equal [{3}]"
                        break
                    case ConstrainedProperty.NULLABLE_CONSTRAINT:
                        if (!c.parameter)
                            println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] cannot be null"
                        break
                    case ConstrainedProperty.RANGE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] does not fall within the valid range from [{3}] to [{4}]"
                        break
                    case ConstrainedProperty.SIZE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] does not fall within the valid size range from [{3}] to [{4}]"
                        break
                    case ConstrainedExtraProperty.UNIQUE_CONSTRAINT: // unique constraint reference not available
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] must be unique"
                        break
                    case ConstrainedProperty.URL_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] is not a valid URL"
                        break
                    case ConstrainedProperty.VALIDATOR_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Property [${getNaturalName(p)}] of class [${domainShortName}] with value [{2}] does not pass custom validation"
                        break
                }
            }
        }

        println ""
    }

    def generateForDomainClassPt(PersistentEntity domainClass) {
        // print generic messages for this domain class

        String domainShortName = getDomainShortname(domainClass.name)
        String domainPropertyName = domainClass.decapitalizedName;

        println "# ${domainShortName} messages, Customized by X-47"
        println "${domainPropertyName}.label=${domainShortName}"
        println "${domainPropertyName}.add.label=Adicionar ${domainShortName}"
        println "${domainPropertyName}.import.label=Importar ${domainShortName}"
        println "${domainPropertyName}.search.label=Procurar ${domainShortName}"
        println "${domainPropertyName}.create.label=Criar ${domainShortName}"
        println "${domainPropertyName}.edit.label=Alterar ${domainShortName}"
        println "${domainPropertyName}.list.label=Lista de ${domainShortName}"
        println "${domainPropertyName}.new.label=Novo ${domainShortName}"
        println "${domainPropertyName}.show.label=Visualizar ${domainShortName}"

        println "${domainPropertyName}.create.button.label=Registar ${domainShortName}"
        println "${domainPropertyName}.update.button.label=Atualizar ${domainShortName}"
        println "${domainPropertyName}.delete.button.label=Apagar ${domainShortName}"
        println "${domainPropertyName}.edit.button.label=Alterar ${domainShortName}"

        println "${domainPropertyName}.created=${domainShortName} {0} foi registado"
        println "${domainPropertyName}.updated=${domainShortName} {0} foi actualizado"
        println "${domainPropertyName}.deleted=${domainShortName} {0} foi apagado"
        println "${domainPropertyName}.not.found=${domainShortName} com id {0} não foi encontrado"
        println "${domainPropertyName}.not.deleted=${domainShortName} com id {0} não foi apagado"
        println "${domainPropertyName}.optimistic.locking.failure=Outro usuário alterou os dados do formulário ${domainShortName} enquanto estavas editando"

        // print messages for all properties contained by domain class
        def props = domainClass.persistentProperties.findAll { it.name != 'version' }
        Collections.sort(props, new PersistentPropertyComparator(domainClass))

        props.each { p ->
            println "${domainPropertyName}.${p.name}.label=${getNaturalName(p)}"

            ConstrainedDiscovery constrainedDiscovery = GrailsFactoriesLoader.loadFactory(ConstrainedDiscovery.class);
            Map constrainedProperties = constrainedDiscovery.findConstrainedProperties(domainClass);

            // print messages for inList constraint values
            def cp = (ConstrainedDelegate) constrainedProperties[p.name]

            if (cp?.inList) {
                cp.inList.each { v ->
                    println "${domainPropertyName}.${p.name}.${v}=${v}"
                }
            }

            // print error messages for constraints
            cp?.appliedConstraints?.each { c ->
                //println("constraint: ${c.name}")
                switch (c.name) {
                    case ConstrainedProperty.BLANK_CONSTRAINT:
                        if (!c.parameter)
                            println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] não pode estar em branco"
                        break
                    case ConstrainedProperty.CREDIT_CARD_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] não é um número válido de cartão de crédito"
                        break
                    case ConstrainedProperty.EMAIL_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] não é um endereço de email válido"
                        break
                    case ConstrainedProperty.IN_LIST_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] não pertence a lista [{3}]"
                        break
                    case ConstrainedProperty.MATCHES_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] não corresponde ao padrão requerido [{3}]"
                        break
                    case ConstrainedProperty.MAX_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] excede o valor máximo permitido [{3}]"
                        break
                    case ConstrainedProperty.MAX_SIZE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] excede o tamanho máximo de [{3}]"
                        break
                    case ConstrainedProperty.MIN_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] é menor que o valor mínimo [{3}]"
                        break
                    case ConstrainedProperty.MIN_SIZE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] é menor que o tamanho mínimo de [{3}]"
                        break
                    case ConstrainedProperty.NOT_EQUAL_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] não pode ser igual à [{3}]"
                        break
                    case ConstrainedProperty.NULLABLE_CONSTRAINT:
                        if (!c.parameter)
                            println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] não pode estar vazio"
                        break
                    case ConstrainedProperty.RANGE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] não pertence ao intervalo de [{3}] á [{4}]"
                        break
                    case ConstrainedProperty.SIZE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] não pertence ao intervalo dos tamanhos de [{3}] á [{4}]"
                        break
                    case ConstrainedExtraProperty.UNIQUE_CONSTRAINT: // unique constraint reference not available
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] deve ser unico"
                        break
                    case ConstrainedProperty.URL_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] não é uma URL válida"
                        break
                    case ConstrainedProperty.VALIDATOR_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=O campo [${getNaturalName(p)}] do formulário [${domainShortName}] com o valor [{2}] não passou na validação"
                        break


                }
            }
        }

        println ""
    }

    def generateForDomainClassFr(PersistentEntity domainClass) {
        // print generic messages for this domain class

        String domainShortName = getDomainShortname(domainClass.name)
        String domainPropertyName = domainClass.decapitalizedName;

        println "# ${domainShortName} messages, Customized by X-47"
        println "${domainPropertyName}.label=${domainShortName}"
        println "${domainPropertyName}.add.label=Ajouter ${domainShortName}"
        println "${domainPropertyName}.import.label=Importer ${domainShortName}"
        println "${domainPropertyName}.search.label=Rechercher ${domainShortName}"
        println "${domainPropertyName}.create.label=Créer ${domainShortName}"
        println "${domainPropertyName}.edit.label=Modifier ${domainShortName}"
        println "${domainPropertyName}.list.label=Liste de la ${domainShortName}"
        println "${domainPropertyName}.new.label=Ajouter une nouvelle ${domainShortName}"
        println "${domainPropertyName}.show.label=Afficher ${domainShortName}"

        println "${domainPropertyName}.create.button.label=Sauvegarder ${domainShortName}"
        println "${domainPropertyName}.update.button.label==Mise à jour de la ${domainShortName}"
        println "${domainPropertyName}.delete.button.label=Supprimer ${domainShortName}"
        println "${domainPropertyName}.edit.button.label=Edit ${domainShortName}"

        println "${domainPropertyName}.created=${domainShortName} {0} créée"
        println "${domainPropertyName}.updated=${domainShortName} {0} mise à jour"
        println "${domainPropertyName}.deleted=${domainShortName} {0} supprimée"
        println "${domainPropertyName}.not.found=${domainShortName} non trouvé avec l'id {0}"
        println "${domainPropertyName}.not.deleted=${domainShortName} non supprimée avec id{0}"
        println "${domainPropertyName}.optimistic.locking.failure=Un autre utilisateur a mis à jour cette ${domainShortName} pendant que vous changiez"

        // print messages for all properties contained by domain class
        def props = domainClass.persistentProperties.findAll { it.name != 'version' }
        Collections.sort(props, new PersistentPropertyComparator(domainClass))

        props.each { p ->
            println "${domainPropertyName}.${p.name}.label=${getNaturalName(p)}"

            ConstrainedDiscovery constrainedDiscovery = GrailsFactoriesLoader.loadFactory(ConstrainedDiscovery.class);
            Map constrainedProperties = constrainedDiscovery.findConstrainedProperties(domainClass);

            // print messages for inList constraint values
            def cp = (ConstrainedDelegate) constrainedProperties[p.name]

            if (cp?.inList) {
                cp.inList.each { v ->
                    println "${domainPropertyName}.${p.name}.${v}=${v}"
                }
            }

            // print error messages for constraints
            cp?.appliedConstraints?.each { c ->
                //println("constraint: ${c.name}")
                switch (c.name) {
                    case ConstrainedProperty.BLANK_CONSTRAINT:
                        if (!c.parameter)
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] ne peut pas être vide"
                        break
                    case ConstrainedProperty.CREDIT_CARD_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] n'est pas un numéro de carte de crédit valide"
                        break
                    case ConstrainedProperty.EMAIL_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] n'est pas une adresse e-mail valide"
                        break
                    case ConstrainedProperty.IN_LIST_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] ne fait pas partie de la liste [{3}]"
                        break
                    case ConstrainedProperty.MATCHES_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] ne correspond pas au pattern [{3}]"
                        break
                    case ConstrainedProperty.MAX_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] est supérieure à la valeur maximum [{3}]"
                        break
                    case ConstrainedProperty.MAX_SIZE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] est supérieure à la valeur maximum [{3}]"
                        break
                    case ConstrainedProperty.MIN_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] est inférieure à la valeur minimum [{3}]"
                        break
                    case ConstrainedProperty.MIN_SIZE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] est inférieure à la valeur minimum [{3}]"
                        break
                    case ConstrainedProperty.NOT_EQUAL_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] ne peut pas être égale à [{3}]"
                        break
                    case ConstrainedProperty.NULLABLE_CONSTRAINT:
                        if (!c.parameter)
                            println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] ne peut pas être nulle"
                        break
                    case ConstrainedProperty.RANGE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] n'est pas contenue dans l'intervalle [{3}] à [{4}]"
                        break
                    case ConstrainedProperty.SIZE_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] n'est pas contenue dans l'intervalle [{3}] à [{4}]"
                        break
                    case ConstrainedExtraProperty.UNIQUE_CONSTRAINT: // unique constraint reference not available
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] doit être unique"
                        break
                    case ConstrainedProperty.URL_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] n'est pas une URL valide"
                        break
                    case ConstrainedProperty.VALIDATOR_CONSTRAINT:
                        println "${domainPropertyName}.${p.name}.${c.name}.error=Le champ [${getNaturalName(p)}] de la classe [${domainShortName}] avec la valeur [{2}] n'est pas valide"
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
