package org.philimone.hds.explorer.server.model.collect.raw.api

import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.NodeChild
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.collect.raw.editors.RawEditHousehold
import org.philimone.hds.explorer.server.model.collect.raw.editors.RawEditMember
import org.philimone.hds.explorer.server.model.collect.raw.editors.RawEditRegion
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawParseResult
import org.springframework.http.HttpStatus
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest

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
                             pregnancyvisits: "POST",
                             deaths: "POST",
                             changeheads: "POST",
                             incompletevisits: "POST",
                             changeregionheads: "POST",
                             householdrelocations: "POST",
                             changeproxyheads: "POST",
                             editregions: "POST",
                             edithouseholds: "POST",
                             editmembers: "POST"]

    static PARAMS_CORE_XML_NAME = "core_xml"
    static PARAMS_CORE_MEDIA_FILES_NAME = "core_media_files"

    def rawImportApiService
    def rawExecutionService
    def rawEditExecutionService
    def errorMessageService

    def regions = {

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        // Extract the Core XML
        def multipartRequest = (MultipartHttpServletRequest) request
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)

        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawRegion> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.) - we save relative filename in database columns - files are saved in a-docs path
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                String filename = mediaFile.originalFilename // e.g., "photo_123.jpg"
                String contentType = mediaFile.contentType   // e.g., "image/jpeg"

                def adocsFile = new File(SystemPath.externalDocsPath, filename)
                mediaFile.transferTo(adocsFile)

                // Logic to save the file to your storage service
                log.info "Received media: ${filename}[${contentType}] (${mediaFile.size} bytes)"
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawHousehold> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.HOUSEHOLD)

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
        rawInstance.extensionForm = extensionXml?.getBytes()

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawHousehold> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawVisit> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawMemberEnu> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawExternalInMigration> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawInMigration> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawOutMigration> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawMaritalRelationship> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawPregnancyRegistration> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawPregnancyOutcome> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

    def pregnancyvisits = {

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawPregnancyVisit> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.PREGNANCY_VISIT)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parsePregnancyVisit(node)
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createPregnancyVisit(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def deaths = {

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawDeath> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawChangeHead> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawIncompleteVisit> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawChangeRegionHead> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

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
        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawHouseholdRelocation> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createHouseholdRelocation(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def changeproxyheads = {

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawHouseholdProxyHead> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")
        String extensionXml = rawImportApiService.getExtensionXmlText(xmlContent, RawEntity.CHANGE_PROXY_HEAD)

        try {
            def node = new XmlSlurper().parseText(xmlContent) as NodeChild
            node = node.children().first() as NodeChild //RawDomain

            parseResult = rawImportApiService.parseChangeProxyHead(node)
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

        // Handle incoming attachment files (images, audio, etc.)
        List<MultipartFile> mediaFiles = multipartRequest.getFiles(PARAMS_CORE_MEDIA_FILES_NAME)
        mediaFiles.each { MultipartFile mediaFile ->
            if (!mediaFile.empty) {
                mediaFile.transferTo(new File(SystemPath.externalDocsPath, mediaFile.originalFilename))
            }
        }

        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createChangeProxyHead(resultSave, "")

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def editregions = {
        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawEditRegion> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")

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
        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawEditHousehold> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")

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

        // Ensure the incoming request is multipart
        if (!(request instanceof MultipartHttpServletRequest)) {
            render text: "Expected multipart/form-data request", status: HttpStatus.BAD_REQUEST
            return
        }

        def multipartRequest = (MultipartHttpServletRequest) request

        // Extract the Core XML
        def xmlPart = multipartRequest.getFile(PARAMS_CORE_XML_NAME)
        if (!xmlPart || xmlPart.empty) {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status: HttpStatus.BAD_REQUEST
            return
        }

        RawParseResult<RawEditMember> parseResult = null
        String xmlContent = new String(xmlPart.bytes, "UTF-8")

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
