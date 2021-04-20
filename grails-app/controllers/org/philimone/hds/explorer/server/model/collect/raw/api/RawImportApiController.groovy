package org.philimone.hds.explorer.server.model.collect.raw.api

import groovy.util.slurpersupport.NodeChild
import org.grails.datastore.gorm.GormEntity
import org.philimone.hds.explorer.server.model.collect.raw.RawHousehold
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.springframework.http.HttpStatus

import java.time.LocalDateTime

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
                             changeheads: "POST"]

    def rawImportApiService
    def rawExecutionService
    def errorMessageService

    def regions = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseRegion(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseHousehold(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseMember(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseVisit(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseMemberEnu(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseExternalInMigration(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseInMigration(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseOutMigration(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseHeadRelationship(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseMaritalRelationship(node)

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
            def result = rawExecutionService.createMaritalRelationship(resultSave)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parsePregnancyRegistration(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parsePregnancyOutcome(node)

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

    def pregnancychilds = {

        if (request.format != "xml") {
            def message = message(code: 'validation.field.raw.xml.invalid.error')
            render text: message, status:  HttpStatus.BAD_REQUEST // Only XML expected
            return
        }

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parsePregnancyChild(node)

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
            def result = rawExecutionService.createPregnancyChild(resultSave)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseDeath(node)

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

        def node = request.getXML() as NodeChild
        def parseResult = rawImportApiService.parseChangeHead(node)

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

}
