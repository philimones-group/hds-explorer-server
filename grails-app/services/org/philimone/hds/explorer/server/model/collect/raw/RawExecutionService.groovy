package org.philimone.hds.explorer.server.model.collect.raw

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.main.Death
import org.philimone.hds.explorer.server.model.main.Enumeration
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.InMigration
import org.philimone.hds.explorer.server.model.main.IncompleteVisit
import org.philimone.hds.explorer.server.model.main.MaritalRelationship
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.OutMigration
import org.philimone.hds.explorer.server.model.main.PregnancyOutcome
import org.philimone.hds.explorer.server.model.main.PregnancyRegistration
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.RegionHeadRelationship
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.Visit

/**
 * Receives RawDomain Instances and execute them in correct service, then flags the execution and store error messages on log database
 */
@Transactional
class RawExecutionService {

    def regionService
    def householdService
    def memberEnumerationService
    def externalInMigrationService
    def pregnancyRegistrationService
    def pregnancyOutcomeService
    def maritalRelationshipService
    def inMigrationService
    def outMigrationService
    def deathService
    def visitService
    def incompleteVisitService
    def changeHeadService
    def changeRegionHeadService

    //Receive a RawModel, execute it and flag errors

    RawExecutionResult<Region> createRegion(RawRegion rawDomainInstance, String logReportFileId){

        def result = regionService.createRegion(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "regionCode", code: rawDomainInstance.regionCode)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status) 
        rawDomainInstance.save(flush:true)

        return result

    }
    
    RawExecutionResult<Household> createHousehold(RawHousehold rawDomainInstance, String logReportFileId){

        def result = householdService.createHousehold(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "householdCode", code: rawDomainInstance.householdCode)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<Enumeration> createMemberEnu(RawMemberEnu rawDomainInstance, String logReportFileId){

        def result = memberEnumerationService.createMemberEnumeration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "code", code: rawDomainInstance.code)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<InMigration> createExternalInMigration(RawExternalInMigration rawDomainInstance, String logReportFileId){

        def result = externalInMigrationService.createExternalInMigration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "memberCode", code: rawDomainInstance.memberCode)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<PregnancyRegistration> createPregnancyRegistration(RawPregnancyRegistration rawDomainInstance, String logReportFileId){

        def result = pregnancyRegistrationService.createPregnancyRegistration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "code", code: rawDomainInstance.code)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<PregnancyOutcome> createPregnancyOutcome(RawPregnancyOutcome rawDomainInstance, String logReportFileId){

        def pregnancyChilds = RawPregnancyChild.findAllByOutcome(rawDomainInstance) //get all childs

        def result = pregnancyOutcomeService.createPregnancyOutcome(rawDomainInstance, pregnancyChilds)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "code", code: rawDomainInstance.code)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<MaritalRelationship> executeMaritalRelationship(RawMaritalRelationship rawDomainInstance, String logReportFileId){

        def result = maritalRelationshipService.executeMaritalRelationship(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "memberA", code: rawDomainInstance.memberA)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<InMigration> createInMigration(RawInMigration rawDomainInstance, String logReportFileId){

        def result = inMigrationService.createInMigration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "memberCode", code: rawDomainInstance.memberCode)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<OutMigration> createOutMigration(RawOutMigration rawDomainInstance, String logReportFileId){

        def result = outMigrationService.createOutMigration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "memberCode", code: rawDomainInstance.memberCode)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<Death> createDeath(RawDeath rawDomainInstance, String logReportFileId){

        def result = deathService.createDeath(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "memberCode", code: rawDomainInstance.memberCode)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<Visit> createVisit(RawVisit rawDomainInstance, String logReportFileId){

        def result = visitService.createVisit(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "code", code: rawDomainInstance.code)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)

            println "visit: ${errorLog.errors}"
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<HeadRelationship> createChangeHead(RawChangeHead rawDomainInstance, String logReportFileId){

        def relationships = RawChangeHeadRelationship.findAllByChangeHead(rawDomainInstance)

        def result = changeHeadService.createChangeHead(rawDomainInstance, relationships)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "newHeadCode", code: rawDomainInstance.newHeadCode)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<IncompleteVisit> createIncompleteVisit(RawIncompleteVisit rawDomainInstance, String logReportFileId){

        def result = incompleteVisitService.createIncompleteVisit(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "visitCode", code: rawDomainInstance.visitCode)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)

            println "incvisit: ${errorLog.errors}"
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    RawExecutionResult<RegionHeadRelationship> createChangeRegionHead(RawChangeRegionHead rawDomainInstance, String logReportFileId){

       def result = changeRegionHeadService.createChangeRegionHead(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, collectedDate: rawDomainInstance.collectedDate, columnName: "newHeadCode", code: rawDomainInstance.newHeadCode)
            errorLog.uuid = rawDomainInstance.id
            errorLog.logReportFile = LogReportFile.findById(logReportFileId)
            errorLog.setMessages(result.errorMessages)
            errorLog.save(flush:true)
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save(flush:true)

        return result

    }

    ProcessedStatus getProcessedStatus(RawExecutionResult.Status resultStatus) {
        return resultStatus==RawExecutionResult.Status.SUCCESS ? ProcessedStatus.SUCCESS : ProcessedStatus.ERROR
    }
}
