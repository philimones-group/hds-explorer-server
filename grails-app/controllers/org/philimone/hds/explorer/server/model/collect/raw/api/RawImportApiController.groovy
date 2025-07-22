package org.philimone.hds.explorer.server.model.collect.raw.api

import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.NodeChild
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.collect.raw.editors.RawEditHousehold
import org.philimone.hds.explorer.server.model.collect.raw.editors.RawEditMember
import org.philimone.hds.explorer.server.model.collect.raw.editors.RawEditRegion
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawParseResult
import org.springframework.http.HttpStatus

class RawImportApiController {

    static responseFormats = ['xml']

    static allowedMethods = [regions: "POST",
                             prehouseholds: "POST",
                             households: "POST",
                             members: "POST",
                             visits: "POST",
                             memberenus: "POST",
                             externalinmigrations: "POST",
                             inmigrations: "POST",
                             outmigrations: "POST",
                             headrelationships: "POST",
                             maritalrelationships: "POST",
                             pregnancyregistrations: "POST",
                             pregnancyoutcomes: "POST",
                             deaths: "POST",
                             changeheads: "POST",
                             incompletevisits: "POST",
                             changeregionheads: "POST",
                             householdrelocations: "POST",
                             editregions: "POST",
                             edithouseholds: "POST",
                             editmembers: "POST"]

    def rawImportApiService
    def rawExecutionService
    def rawEditExecutionService
    def errorMessageService

    def regions = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawRegion> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.REGION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseRegion(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createRegion(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def prehouseholds = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawHousehold> parseResult = null
        String xmlContent = request.reader?.text

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parsePreHousehold(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createHousehold(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def households = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawHousehold> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.HOUSEHOLD)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseHousehold(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createHousehold(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def members = {
        /* NOT NEEDED AT ALL
        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawMember> parseResult = null

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild //request.XML as NodeChild
        node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseMember(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createMember(resultSave, "")

            if (result?.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }
        */

        render text: "OK", status: HttpStatus.OK
    }

    def visits = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawVisit> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.VISIT)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseVisit(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createVisit(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def memberenus = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawMemberEnu> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.MEMBER_ENUMERATION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseMemberEnu(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createMemberEnu(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def externalinmigrations = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawExternalInMigration> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.EXTERNAL_INMIGRATION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseExternalInMigration(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createExternalInMigration(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def inmigrations = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawInMigration> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.IN_MIGRATION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseInMigration(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createInMigration(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def outmigrations = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawOutMigration> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.OUT_MIGRATION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseOutMigration(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createOutMigration(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def headrelationships = {
        /* NOT NEEDED
        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawHeadRelationship> parseResult = null

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild //request.XML as NodeChild
        node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseHeadRelationship(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createHeadRelationship(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }*/

        render text: "OK", status: HttpStatus.OK
    }

    def maritalrelationships = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawMaritalRelationship> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.MARITAL_RELATIONSHIP)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseMaritalRelationship(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.executeMaritalRelationship(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def pregnancyregistrations = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawPregnancyRegistration> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.PREGNANCY_REGISTRATION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parsePregnancyRegistration(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createPregnancyRegistration(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def pregnancyoutcomes = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawPregnancyOutcome> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.PREGNANCY_OUTCOME)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parsePregnancyOutcome(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createPregnancyOutcome(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def deaths = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawDeath> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.DEATH)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseDeath(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createDeath(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def changeheads = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawChangeHead> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.CHANGE_HEAD_OF_HOUSEHOLD)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseChangeHead(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createChangeHead(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def incompletevisits = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawIncompleteVisit> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.INCOMPLETE_VISIT)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseIncompleteVisit(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createIncompleteVisit(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def changeregionheads = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawChangeRegionHead> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.CHANGE_HEAD_OF_REGION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseChangeRegionHead(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createChangeRegionHead(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def householdrelocations = {
        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawHouseholdRelocation> parseResult = null
        String xmlContent = request.reader?.text
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.HOUSEHOLD_RELOCATION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseHouseholdRelocation(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createChangeRegionHead(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def editregions = {
        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawEditRegion> parseResult = null
        String xmlContent = request.reader?.text
        //String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.REGION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseEditRegion(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        //rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        //execute creation update
        def result = rawEditExecutionService.updateRegion(rawInstance)

        if (result.status== RawExecutionResult.Status.ERROR){
            render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
            return
        }

        render text: "OK", status: HttpStatus.OK
    }

    def edithouseholds = {
        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawEditHousehold> parseResult = null
        String xmlContent = request.reader?.text
        //String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.REGION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseEditHousehold(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        //rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        //execute creation update
        def result = rawEditExecutionService.updateHousehold(rawInstance)

        if (result.status== RawExecutionResult.Status.ERROR){
            render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
            return
        }

        render text: "OK", status: HttpStatus.OK
    }

    def editmembers = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawEditMember> parseResult = null
        String xmlContent = request.reader?.text
        //String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.REGION)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseEditMember(node)
        } catch(Exception ex) {
            def msg = errorMessageService.getRawMessagesText(ex)
            render text: msg, status: HttpStatus.BAD_REQUEST
            return
        }
        if (parseResult.hasErrors()) {
            render text: parseResult.getErrorsText(), status: HttpStatus.BAD_REQUEST
            return
        }

        def rawInstance = parseResult.domainInstance
        //rawInstance.extensionForm = extensionXml?.getBytes()

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        //execute creation update
        def result = rawEditExecutionService.updateMember(rawInstance)

        if (result.status== RawExecutionResult.Status.ERROR){
            render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
            return
        }

        render text: "OK", status: HttpStatus.OK
    }


}
