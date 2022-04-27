package org.philimone.hds.explorer.server.model.collect.raw

import grails.gorm.transactions.Transactional
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.main.Death
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

    //Receive a RawModel, execute it and flag errors

    RawExecutionResult<Region> createRegion(RawRegion rawDomainInstance){

        def result = regionService.createRegion(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.regionCode)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status) 
        rawDomainInstance.save()

        return result

    }
    
    RawExecutionResult<Household> createHousehold(RawHousehold rawDomainInstance){

        def result = householdService.createHousehold(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.householdCode)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<Member> createMemberEnu(RawMemberEnu rawDomainInstance){

        def result = memberEnumerationService.createMemberEnumeration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.code)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<InMigration> createExternalInMigration(RawExternalInMigration rawDomainInstance){

        def result = externalInMigrationService.createExternalInMigration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.memberCode)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<PregnancyRegistration> createPregnancyRegistration(RawPregnancyRegistration rawDomainInstance){

        def result = pregnancyRegistrationService.createPregnancyRegistration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.code)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<PregnancyOutcome> createPregnancyOutcome(RawPregnancyOutcome rawDomainInstance){

        def pregnancyChilds = RawPregnancyChild.findAllByOutcome(rawDomainInstance) //get all childs

        def result = pregnancyOutcomeService.createPregnancyOutcome(rawDomainInstance, pregnancyChilds)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.code)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<MaritalRelationship> createMaritalRelationship(RawMaritalRelationship rawDomainInstance){

        def result = maritalRelationshipService.createMaritalRelationship(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: "")
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<InMigration> createInMigration(RawInMigration rawDomainInstance){

        def result = inMigrationService.createInMigration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.memberCode)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<OutMigration> createOutMigration(RawOutMigration rawDomainInstance){

        def result = outMigrationService.createOutMigration(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.memberCode)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<Death> createDeath(RawDeath rawDomainInstance){

        def result = deathService.createDeath(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.memberCode)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<Visit> createVisit(RawVisit rawDomainInstance){

        def result = visitService.createVisit(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.code)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()

            println "visit: ${errorLog.errors}"
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<HeadRelationship> createChangeHead(RawChangeHead rawDomainInstance){

        def relationships = RawChangeHeadRelationship.findAllByChangeHead(rawDomainInstance)

        def result = changeHeadService.createChangeHead(rawDomainInstance, relationships)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.newHeadCode)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    RawExecutionResult<IncompleteVisit> createIncompleteVisit(RawIncompleteVisit rawDomainInstance){

        def result = incompleteVisitService.createIncompleteVisit(rawDomainInstance)

        if (result.status == RawExecutionResult.Status.ERROR){
            //create errorLog
            def errorLog = new RawErrorLog(uuid: rawDomainInstance.id, entity: result.entity, code: rawDomainInstance.visitCode)
            errorLog.setMessages(result.errorMessages)
            errorLog.save()

            println "incvisit: ${errorLog.errors}"
        }

        rawDomainInstance.refresh()
        rawDomainInstance.processedStatus = getProcessedStatus(result.status)
        rawDomainInstance.save()

        return result

    }

    ProcessedStatus getProcessedStatus(RawExecutionResult.Status resultStatus) {
        return resultStatus==RawExecutionResult.Status.SUCCESS ? ProcessedStatus.SUCCESS : ProcessedStatus.ERROR
    }
}
