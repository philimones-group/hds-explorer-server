package org.philimone.hds.explorer.server.model.collect.raw.api

import groovy.util.slurpersupport.NodeChild
import org.philimone.hds.explorer.server.model.collect.raw.*
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawParseResult
import org.springframework.http.HttpStatus

class RawImportApiController {

    static responseFormats = ['xml']

    static allowedMethods = [regions: "POST",
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
                             incompletevisits: "POST"]

    def rawImportApiService
    def rawExecutionService
    def errorMessageService

    def regions = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawRegion> parseResult = null

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createRegion(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createHousehold(resultSave)

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def members = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawMember> parseResult = null

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createMember(resultSave)

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def visits = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawVisit> parseResult = null

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createVisit(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createMemberEnu(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createExternalInMigration(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createInMigration(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createOutMigration(resultSave)

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def headrelationships = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawHeadRelationship> parseResult = null

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createHeadRelationship(resultSave)

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

    def maritalrelationships = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        RawParseResult<RawMaritalRelationship> parseResult = null

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.executeMaritalRelationship(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createPregnancyRegistration(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createPregnancyOutcome(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createDeath(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createChangeHead(resultSave)

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

        try {
            def node = request.getXML() as NodeChild
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
        def resultSave = rawInstance.save(flush: true)

        if (rawInstance.hasErrors()){
            render text: errorMessageService.getRawMessagesText(rawInstance), status: HttpStatus.BAD_REQUEST
            return
        }

        if (resultSave.postExecution){ //execute creation
            def result = rawExecutionService.createIncompleteVisit(resultSave)

            if (result.status== RawExecutionResult.Status.ERROR){
                render text: errorMessageService.getRawMessagesText(result.errorMessages), status: HttpStatus.BAD_REQUEST
                return
            }
        }

        render text: "OK", status: HttpStatus.OK
    }

}
