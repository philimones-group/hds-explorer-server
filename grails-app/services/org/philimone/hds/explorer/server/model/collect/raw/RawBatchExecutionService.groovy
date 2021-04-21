package org.philimone.hds.explorer.server.model.collect.raw

import grails.gorm.transactions.Transactional
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.server.model.collect.raw.aggregate.RawEvent
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEventType
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
import org.philimone.hds.explorer.server.model.main.Death
import org.philimone.hds.explorer.server.model.main.HeadRelationship
import org.philimone.hds.explorer.server.model.main.Household
import org.philimone.hds.explorer.server.model.main.InMigration
import org.philimone.hds.explorer.server.model.main.MaritalRelationship
import org.philimone.hds.explorer.server.model.main.Member
import org.philimone.hds.explorer.server.model.main.OutMigration
import org.philimone.hds.explorer.server.model.main.PregnancyOutcome
import org.philimone.hds.explorer.server.model.main.PregnancyRegistration
import org.philimone.hds.explorer.server.model.main.Region
import org.philimone.hds.explorer.server.model.main.Visit
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult

@Transactional
class RawBatchExecutionService {

    static datasource = ['internal']
    
    def sessionFactory
    def rawExecutionService
    def regionService
    def visitService
    def householdService
    def memberService



    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()     
    }
    
    /*
     * Organize data by processed = 0 and date of processing
     */
    def organizeEventsForExecution() {
        //keyDate   (date of event/capture) will be yyyy-MM-dd or yyyy-MM-dd HH:mm:ss
        //event_type (the HDS Event to be executED, [sort: "collectedDate", order: "asc"]) as INTEGER
        //  1. Household Enumeration
        //  2. Visit
        //  3. Member Enumeration
        //  4. Death
        //  5. OutMigrations
        //  6. Internal InMigrations
        //  7. External InMigrations Entry
        //  8. External InMigrations Reentry
        //  9. Pregnancy Observations
        //  10. Pregnancy Outcomes
        //  11. Marital Relationships
        //  12. Change Head of Household
        //event_uuid  (RawDomainModel ID)
        //entity_code (household, member, visit, pregnancy code, etc)
        //event_processed (yes / no)




        //Read raw domain models that are not processed and save at raw_event table, raw data will be read ordered by dateOfEvent
        compileEvents()

        //read raw_events ordered by keyDate asc and eventtype asc
        int offset = 0
        int max = 50
        int totalRecords = RawEvent.count()

        while (offset < totalRecords) {
            def events = RawEvent.executeQuery("select e from RawEvent e order by e.keyDate asc, e.eventType asc", [offset: offset, max: max])
            offset += events.size()

            events.each { rawEvent ->
                executeEvent(rawEvent)
            }
            //execute event
        }
    }

    RawExecutionResult executeEvent(RawEvent event) {
        switch (event?.eventType) {
            case RawEventType.EVENT_REGION:                       return executeRegion(event)
            case RawEventType.EVENT_HOUSEHOLD:                    return executeHousehold(event)
            case RawEventType.EVENT_VISIT:                        return executeVisit(event)
            case RawEventType.EVENT_MEMBER_ENU:                   return executeMemberEnu(event)
            case RawEventType.EVENT_DEATH:                        return executeDeath(event)
            case RawEventType.EVENT_OUTMIGRATION:                 return executeOutmigration(event)
            case RawEventType.EVENT_INTERNAL_INMIGRATION:         return executeInmigration(event)
            case RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY:   return executeExtInmigration(event)
            case RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY: return executeExtInmigration(event)
            case RawEventType.EVENT_PREGNANCY_REGISTRATION:       return executePregnancyReg(event)
            case RawEventType.EVENT_PREGNANCY_OUTCOME:            return executePregnancyOutcome(event)
            case RawEventType.EVENT_MARITAL_RELATIONSHIP:         return executeMaritalRelationship(event)
            case RawEventType.EVENT_CHANGE_HEAD_OF_HOUSEHOLD:     return executeChangeHead(event)
            default: return null
        }
    }

    RawExecutionResult<Region> executeRegion(RawEvent rawEvent) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawRegion.findById(rawEvent?.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check region dependency existence
            if (rawObj.parentCode != null && !regionService.exists(rawObj.parentCode)) {
                //find dependency event and execute
                def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_REGION, rawObj.parentCode)

                def result = executeRegion(devent)
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save()

                dependencyResolved = (result?.status==RawExecutionResult.Status.SUCCESS)
            }

            if (dependencyResolved) {
                def result = rawExecutionService.createRegion(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }
        }

        return null
    }

    RawExecutionResult<Household> executeHousehold(RawEvent rawEvent) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawHousehold.findById(rawEvent?.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check household dependency existence
            if (!regionService.exists(rawObj.regionCode)) {
                //find dependency event and execute
                def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_REGION, rawObj.regionCode)

                def result = executeRegion(devent)
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save()

                dependencyResolved = (result?.status==RawExecutionResult.Status.SUCCESS)
            }

            if (dependencyResolved) {
                def result = rawExecutionService.createHousehold(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

        }

        return null
    }

    RawExecutionResult<Visit> executeVisit(RawEvent rawEvent) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawVisit.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check visit dependencies existence (household, member/respondent)
            def householdCode = rawObj.householdCode
            def respondentCode = rawObj.respondentCode

            //try to solve household dependency
            dependencyResolved = solveHouseholdDependency(householdCode)

            //try to solve member dependency (respondent)
            dependencyResolved = dependencyResolved && solveMemberDependency(respondentCode)

            if (dependencyResolved) {

                def result = rawExecutionService.createVisit(rawObj)
                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }
        }

        return null
    }

    RawExecutionResult<Member> executeMemberEnu(RawEvent rawEvent) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawMemberEnu.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (household, father, mother)
            def householdCode = rawObj.householdCode
            def fatherCode = rawObj.fatherCode
            def motherCode = rawObj.motherCode

            //try to solve household dependency
            dependencyResolved = solveHouseholdDependency(householdCode)

            //try to solve visit dependency
            //dependencyResolved = dependencyResolved && solveVisitDependency(visitCode)

            //try to solve member dependency (father)
            dependencyResolved = dependencyResolved && solveMemberDependency(fatherCode)

            //try to solve member dependency (mother)
            dependencyResolved = dependencyResolved && solveMemberDependency(motherCode)

            if (dependencyResolved) {
                def result = rawExecutionService.createMemberEnu(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<Death> executeDeath(RawEvent rawEvent) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawDeath.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, member)
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode

            //try to solve visit dependency
            dependencyResolved = dependencyResolved && solveVisitDependency(visitCode)

            //try to solve member dependency
            dependencyResolved = dependencyResolved && solveMemberDependency(memberCode)

            if (dependencyResolved) {
                def result = rawExecutionService.createDeath(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<OutMigration> executeOutmigration(RawEvent rawEvent) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawOutMigration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (household, visit, member)
            def originCode = rawObj.originCode
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode

            //try to solve household dependency
            dependencyResolved = dependencyResolved && solveHouseholdDependency(originCode)

            //try to solve visit dependency
            dependencyResolved = dependencyResolved && solveVisitDependency(visitCode)

            //try to solve member dependency
            dependencyResolved = dependencyResolved && solveMemberDependency(memberCode)

            if (dependencyResolved) {
                def result = rawExecutionService.createOutMigration(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<InMigration> executeInmigration(RawEvent rawEvent) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawInMigration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (household, visit, member)
            def destinationCode = rawObj.destinationCode
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode

            //try to solve household dependency
            dependencyResolved = dependencyResolved && solveHouseholdDependency(destinationCode)

            //try to solve visit dependency
            dependencyResolved = dependencyResolved && solveVisitDependency(visitCode)

            //try to solve member dependency
            dependencyResolved = dependencyResolved && solveMemberDependency(memberCode)

            if (dependencyResolved) {
                def result = rawExecutionService.createInMigration(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<InMigration> executeExtInmigration(RawEvent rawEvent) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawExternalInMigration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, household(destinationCode))
            def destinationCode = rawObj.destinationCode
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode
            def fatherCode = rawObj.memberFatherCode
            def motherCode = rawObj.memberMotherCode

            //try to solve household dependency
            dependencyResolved = solveHouseholdDependency(destinationCode)

            //try to solve visit dependency
            dependencyResolved = dependencyResolved && solveVisitDependency(visitCode)

            //try to solve member dependency - if it is reentry
            if (rawObj.extMigrationType == ExternalInMigrationType.REENTRY.name()) { //couldnt find dependency
                dependencyResolved = dependencyResolved && solveMemberDependency(memberCode)
            }

            //try to solve member dependency (father)
            dependencyResolved = dependencyResolved && solveMemberDependency(fatherCode)

            //try to solve member dependency (mother)
            dependencyResolved = dependencyResolved && solveMemberDependency(motherCode)


            if (dependencyResolved) {
                def result = rawExecutionService.createExternalInMigration(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<PregnancyRegistration> executePregnancyReg(RawEvent rawEvent) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawPregnancyRegistration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, member)
            def visitCode = rawObj.visitCode
            def motherCode = rawObj.motherCode

            //try to solve visit dependency
            dependencyResolved = dependencyResolved && solveVisitDependency(visitCode)

            //try to solve member dependency
            dependencyResolved = dependencyResolved && solveMemberDependency(motherCode)

            if (dependencyResolved) {
                def result = rawExecutionService.createPregnancyRegistration(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<PregnancyOutcome> executePregnancyOutcome(RawEvent rawEvent) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawPregnancyOutcome.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, mother, father)
            def visitCode = rawObj.visitCode
            def motherCode = rawObj.motherCode
            def fatherCode = rawObj.fatherCode

            //try to solve visit dependency
            dependencyResolved = dependencyResolved && solveVisitDependency(visitCode)

            //try to solve member dependency (father)
            dependencyResolved = dependencyResolved && solveMemberDependency(fatherCode)

            //try to solve member dependency (mother)
            dependencyResolved = dependencyResolved && solveMemberDependency(motherCode)

            if (dependencyResolved) {
                def result = rawExecutionService.createPregnancyOutcome(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<MaritalRelationship> executeMaritalRelationship(RawEvent rawEvent) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawMaritalRelationship.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (memberA, memberB)
            def memberA = rawObj.memberA
            def memberB = rawObj.memberB

            //try to solve memberA dependency
            dependencyResolved = dependencyResolved && solveMemberDependency(memberA)

            //try to solve memberB dependency
            dependencyResolved = dependencyResolved && solveMemberDependency(memberB)

            if (dependencyResolved) {
                def result = rawExecutionService.createMaritalRelationship(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<HeadRelationship> executeChangeHead(RawEvent rawEvent) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawChangeHead.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, household(destinationCode))
            def householdCode = rawObj.householdCode
            def visitCode = rawObj.visitCode
            def oldHeadCode = rawObj.oldHeadCode
            def newHeadCode = rawObj.newHeadCode

            //try to solve household dependency
            dependencyResolved = dependencyResolved && solveHouseholdDependency(householdCode)

            //try to solve visit dependency
            dependencyResolved = dependencyResolved && solveVisitDependency(visitCode)

            //try to solve member dependency (oldHead)
            dependencyResolved = dependencyResolved && solveMemberDependency(oldHeadCode)

            //try to solve member dependency (newHead)
            dependencyResolved = dependencyResolved && solveMemberDependency(newHeadCode)


            if (dependencyResolved) {
                def result = rawExecutionService.createChangeHead(rawObj)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save()

                return result
            }

            return null
        }

        return null
    }

    boolean solveHouseholdDependency(String householdCode) {

        def dependencyResolved = true

        if (!householdService.exists(householdCode)){ //couldnt find dependency
            //find dependency event and execute
            def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_HOUSEHOLD, householdCode)

            def result = executeHousehold(devent)
            //set event has processed
            devent.processed = getProcessedStatus(result?.status)
            devent.save()

            dependencyResolved = (result?.status==RawExecutionResult.Status.SUCCESS)
        }

        return dependencyResolved
    }

    boolean solveMemberDependency(String memberCode) {

        def dependencyResolved = true

        if (!StringUtil.isBlank(memberCode) && !memberService.exists(memberCode)) { //couldnt find dependency
            //find dependency event and execute
            def devent = getMemberEntryEvent(memberCode)

            def result = executeEvent(devent)
            //set event has processed
            devent.processed = getProcessedStatus(result?.status)
            devent.save()

            dependencyResolved = result?.status==RawExecutionResult.Status.SUCCESS
        }

        return dependencyResolved
    }

    boolean solveVisitDependency(String visitCode) {
        def dependencyResolved = true

        if (!visitService.exists(visitCode)) { //couldnt find dependency
            //find dependency event and execute
            def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_VISIT, visitCode)

            def result = executeEvent(devent)
            //set event has processed
            devent.processed = getProcessedStatus(result?.status)
            devent.save()

            dependencyResolved = dependencyResolved && (result?.status==RawExecutionResult.Status.SUCCESS)
        }

        return dependencyResolved
    }

    RawEvent getMemberEntryEvent(String memberCode) {
        if (StringUtil.isBlank(memberCode)) return null

        def devents = RawEvent.executeQuery("select e from RawEvent e where e.eventType in (:list) and ((e.entityCode=:code) or e.childCodes like :searchcodes) order by e.keyDate asc",
                                            [list: [RawEventType.EVENT_MEMBER_ENU, RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY, RawEventType.EVENT_PREGNANCY_OUTCOME], code:memberCode, searchcodes: "%${memberCode}%"], )
        def devent = !devents.empty ? devents.first() : null

        return devent
    }

    def compileEvents(){

        //clear raw event
        RawEvent.executeUpdate("delete from RawEvent")

        collectRegions()
        collectHouseholds()
        collectVisit()
        collectMemberEnu()
        collectDeath()
        collectOutMigration()
        collectInMigration()
        collectExternalInMigration()
        collectPregnancyRegistration()
        collectPregnancyOutcome()
        collectMaritalRelationshipStart()
        collectMaritalRelationshipEnd()
        collectChangeHoh()
    }

    def collectRegions() {
        def list = RawRegion.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "collectedDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.collectedDate, eventType: RawEventType.EVENT_REGION, eventId: it.id, entityCode: it.regionCode).save()
        }

        list.clear()
        cleanUpGorm()
    }

    def collectHouseholds() {
        def list = RawHousehold.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "collectedDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.collectedDate, eventType: RawEventType.EVENT_HOUSEHOLD, eventId: it.id, entityCode: it.householdCode).save()
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectVisit() {
        def list = RawVisit.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "visitDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.visitDate.atStartOfDay(), eventType: RawEventType.EVENT_VISIT, eventId: it.id, entityCode: it.code).save()
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectMemberEnu() {
        def list = RawMemberEnu.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "residencyStartDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.residencyStartDate.atStartOfDay(), eventType: RawEventType.EVENT_MEMBER_ENU, eventId: it.id, entityCode: it.code).save()
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectDeath() {
        def list = RawDeath.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "deathDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.deathDate.atStartOfDay(), eventType: RawEventType.EVENT_DEATH, eventId: it.id, entityCode: it.memberCode).save()
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectOutMigration() {
        def list = RawOutMigration.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "migrationDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.migrationDate.atStartOfDay(), eventType: RawEventType.EVENT_OUTMIGRATION, eventId: it.id, entityCode: it.memberCode).save()
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectInMigration() {
        def list = RawInMigration.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "migrationDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.migrationDate.atStartOfDay(), eventType: RawEventType.EVENT_INTERNAL_INMIGRATION, eventId: it.id, entityCode: it.memberCode).save()
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectExternalInMigration() {
        def list = RawExternalInMigration.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "migrationDate", order: "asc"])

        list.each {
            boolean firstEntry = it.extMigrationType==ExternalInMigrationType.ENTRY.name()
            new RawEvent(keyDate: it.migrationDate.atStartOfDay(), eventType: firstEntry ? RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY : RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY, eventId: it.id, entityCode: it.memberCode).save()
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectPregnancyRegistration() {
        def list = RawPregnancyRegistration.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "recordedDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.recordedDate.atStartOfDay(), eventType: RawEventType.EVENT_PREGNANCY_REGISTRATION, eventId: it.id, entityCode: it.code).save()
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectPregnancyOutcome() {
        def list = RawPregnancyOutcome.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "outcomeDate", order: "asc"])

        list.each {
            def codes = RawPregnancyChild.executeQuery("select p.childCode from RawPregnancyChild p where p.outcome=?", [it])
            def event = new RawEvent(keyDate: it.outcomeDate.atStartOfDay(), eventType: RawEventType.EVENT_PREGNANCY_OUTCOME, eventId: it.id, entityCode: it.code)
            event.setChildCodesFrom(codes)
            event.save()
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectMaritalRelationshipStart() {
        def list = RawMaritalRelationship.findAllByProcessedStatusAndStartDateIsNotNull(ProcessedStatus.NOT_PROCESSED, [sort: "startDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.startDate.atStartOfDay(), eventType: RawEventType.EVENT_MARITAL_RELATIONSHIP, eventId: it.id, entityCode: it.memberA).save()
        }

        list.clear()
        cleanUpGorm()
    }
    def collectMaritalRelationshipEnd() {
        def list = RawMaritalRelationship.findAllByProcessedStatusAndEndDateIsNotNull(ProcessedStatus.NOT_PROCESSED, [sort: "endDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.endDate.atStartOfDay(), eventType: RawEventType.EVENT_MARITAL_RELATIONSHIP, eventId: it.id, entityCode: it.memberA).save()
        }

        list.clear()
        cleanUpGorm()
    }
    def collectChangeHoh() {
        def list = RawChangeHead.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "eventDate", order: "asc"])

        list.each {
            new RawEvent(keyDate: it.eventDate.atStartOfDay(), eventType: RawEventType.EVENT_CHANGE_HEAD_OF_HOUSEHOLD, eventId: it.id, entityCode: it.newHeadCode).save()
        }

        list.clear()
        cleanUpGorm()
    }

    ProcessedStatus getProcessedStatus(RawExecutionResult.Status resultStatus) {
        return resultStatus==RawExecutionResult.Status.SUCCESS ? ProcessedStatus.SUCCESS : ProcessedStatus.ERROR
    }
}
