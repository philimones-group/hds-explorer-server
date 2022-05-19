package org.philimone.hds.explorer.server.model.collect.raw


import net.betainteractive.utilities.StringUtil
import org.hibernate.SessionFactory
import org.philimone.hds.explorer.server.model.collect.raw.aggregate.RawEvent
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.RawEventType
import org.philimone.hds.explorer.server.model.enums.RawMemberOrder
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
import org.philimone.hds.explorer.server.model.logs.LogReportFile
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
import org.philimone.hds.explorer.server.model.main.Visit
import org.philimone.hds.explorer.server.model.main.collect.raw.RawDependencyStatus
import org.philimone.hds.explorer.server.model.main.collect.raw.RawExecutionResult
import org.philimone.hds.explorer.server.model.main.collect.raw.RawMessage

class RawBatchExecutionService {

    def transactional = false

    SessionFactory sessionFactory
    def rawExecutionService
    def regionService
    def visitService
    def householdService
    def memberService
    def headRelationshipService
    def errorMessageService


    def cleanUpGorm() {
        //def session = sessionFactory.currentSession
        //session.flush()
        //session.clear()

        System.gc()
        println "clearing up"
    }
    
    /*
     * Organize data by processed = 0 and date of processing
     */
    def compileAndExecuteEvents(String logReportFileId) {
        //keyDate   (date of event/capture) will be yyyy-MM-dd or yyyy-MM-dd HH:mm:ss
        //event_type (the HDS Event to be executED, [sort: "collectedDate", order: "asc"]) as INTEGER
        //event_order (the order for members insertion - start with the head of household)
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

        executeEvents(logReportFileId)
    }

    def resetEventsToNotProcessed(String logReportFileId){
                
        RawEvent.withTransaction {
            RawEvent.executeUpdate("update RawEvent e set e.processed=:newStatus where e.processed=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])

            RawRegion.executeUpdate(               "update RawRegion r                set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawHousehold.executeUpdate(            "update RawHousehold r             set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawVisit.executeUpdate(                "update RawVisit r                 set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawIncompleteVisit.executeUpdate(      "update RawIncompleteVisit r       set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawMemberEnu.executeUpdate(            "update RawMemberEnu r             set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawDeath.executeUpdate(                "update RawDeath r                 set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawOutMigration.executeUpdate(         "update RawOutMigration r          set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawInMigration.executeUpdate(          "update RawInMigration r           set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawExternalInMigration.executeUpdate(  "update RawExternalInMigration r   set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawPregnancyRegistration.executeUpdate("update RawPregnancyRegistration r set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawPregnancyOutcome.executeUpdate(     "update RawPregnancyOutcome r      set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawMaritalRelationship.executeUpdate(  "update RawMaritalRelationship r   set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])
            RawChangeHead.executeUpdate(           "update RawChangeHead r            set r.processedStatus=:newStatus where r.processedStatus=:currStatus", [currStatus: ProcessedStatus.ERROR, newStatus: ProcessedStatus.NOT_PROCESSED])

            RawErrorLog.executeUpdate("delete from RawErrorLog r") //Crazy decision? not that much, if we are going to execute all again we dont need the old errors
        }

        //update RawEvent -> on eventOrder
        /*RawEvent.withTransaction {
            RawEvent.executeQuery("select e.eventId, e.eventType from RawEvent e").each { result ->
                def id = result[0]
                def type = result[1] as RawEventType
                def eventOrder = RawMemberOrder.NOT_APPLICABLE

                if (type == RawEventType.EVENT_MEMBER_ENU) {
                    def relType = RawMemberEnu.get(id)?.headRelationshipType
                    eventOrder = RawMemberOrder.getFromCode(relType)

                } else if (type == RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY || RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY) {

                    def relType = RawExternalInMigration.get(id)?.headRelationshipType
                    eventOrder = RawMemberOrder.getFromCode(relType)

                } else if (type == RawEventType.EVENT_INTERNAL_INMIGRATION) {

                    def relType = RawInMigration.get(id)?.headRelationshipType
                    eventOrder = RawMemberOrder.getFromCode(relType)

                }


                RawEvent.executeUpdate("update RawEvent e set e.eventOrder=? where e.eventId=?", [eventOrder, id])
            }
        }*/
        
    }
    
    def resetEventsZero(){
        RawEvent.withTransaction {
            RawEvent.executeUpdate("update RawEvent e set e.processed=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])

            RawRegion.executeUpdate(               "update RawRegion r                set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawHousehold.executeUpdate(            "update RawHousehold r             set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawVisit.executeUpdate(                "update RawVisit r                 set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawIncompleteVisit.executeUpdate(      "update RawIncompleteVisit r       set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawMemberEnu.executeUpdate(            "update RawMemberEnu r             set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawDeath.executeUpdate(                "update RawDeath r                 set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawOutMigration.executeUpdate(         "update RawOutMigration r          set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawInMigration.executeUpdate(          "update RawInMigration r           set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawExternalInMigration.executeUpdate(  "update RawExternalInMigration r   set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawPregnancyRegistration.executeUpdate("update RawPregnancyRegistration r set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawPregnancyOutcome.executeUpdate(     "update RawPregnancyOutcome r      set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawMaritalRelationship.executeUpdate(  "update RawMaritalRelationship r   set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])
            RawChangeHead.executeUpdate(           "update RawChangeHead r            set r.processedStatus=:newStatus", [newStatus: ProcessedStatus.NOT_PROCESSED])

            RawErrorLog.executeUpdate("delete from RawErrorLog r") //Crazy decision? not that much, if we are going to execute all again we dont need the old errors
        }
    }

    def compileEvents(){

        //clear raw event
        RawEvent.withTransaction {
            RawEvent.executeUpdate("delete from RawEvent")
        }

        collectRegions()
        collectHouseholds()
        collectVisit()
        collectIncompleteVisit();
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

    def executeEvents(String logReportFileId) {
        //read raw_events ordered by keyDate asc and eventtype asc

        def initialEvents = [] //as List<RawEvent> //Regions and Households
        def otherEvents = [] //as List<RawEvent>
        def maritalEvents = []


        //read two different sets of events

        RawEvent.withTransaction {
            initialEvents = RawEvent.executeQuery("select e.id from RawEvent e where e.processed=:processed and e.eventType in (:list) order by e.eventType asc, e.keyDate asc", [processed: ProcessedStatus.NOT_PROCESSED, list: [RawEventType.EVENT_REGION, RawEventType.EVENT_HOUSEHOLD]])
            otherEvents = RawEvent.executeQuery("select e.id from RawEvent e where e.processed=:processed and e.eventType not in (:list) order by e.keyDate asc, e.eventType asc, e.eventOrder asc", [processed: ProcessedStatus.NOT_PROCESSED, list: [RawEventType.EVENT_REGION, RawEventType.EVENT_HOUSEHOLD, RawEventType.EVENT_MARITAL_RELATIONSHIP]]) //, [offset: offset, max: max])
            maritalEvents = RawEvent.executeQuery("select e.id from RawEvent e where e.processed=:processed and e.eventType in (:list) order by e.keyDate asc, e.eventType asc, e.eventOrder asc", [processed: ProcessedStatus.NOT_PROCESSED, list: [RawEventType.EVENT_MARITAL_RELATIONSHIP]])
        }

        println "execute initial events ${initialEvents.size()}"
        initialEvents.collate(100).each { batch ->
            batch.each { rawEventId ->
                RawEvent.withTransaction {
                    executeEvent(RawEvent.get(rawEventId), logReportFileId)
                }
            }
        }

        println "execute other events ${otherEvents.size()}"
        otherEvents.collate(100).each { batch ->
            batch.each { rawEventId ->
                RawEvent.withTransaction {
                    def rawEvent = RawEvent.get(rawEventId)
                    println "event ${rawEvent?.eventId}, date=${StringUtil.format(rawEvent?.keyDate)}, type=${rawEvent?.eventType}, order=${rawEvent?.eventOrder}, code: ${rawEvent?.entityCode}"
                    def result = executeEvent(rawEvent, logReportFileId)
                    //println "event result=${result}, ${result?.status}, ${result?.errorMessages}"
                }
            }
        }

        println "execute marital events ${maritalEvents.size()}"
        maritalEvents.collate(100).each { batch ->
            batch.each { rawEventId ->
                RawEvent.withTransaction {
                    def rawEvent = RawEvent.get(rawEventId)
                    executeEvent(rawEvent, logReportFileId)
                }
            }
        }

    }

    RawExecutionResult executeEvent(RawEvent event, String logReportFileId) {
        switch (event?.eventType) {
            case RawEventType.EVENT_REGION:                       return executeRegion(event, logReportFileId)
            case RawEventType.EVENT_HOUSEHOLD:                    return executeHousehold(event, logReportFileId)
            case RawEventType.EVENT_VISIT:                        return executeVisit(event, logReportFileId)
            case RawEventType.EVENT_MEMBER_ENU:                   return executeMemberEnu(event, logReportFileId)
            case RawEventType.EVENT_DEATH:                        return executeDeath(event, logReportFileId)
            case RawEventType.EVENT_OUTMIGRATION:                 return executeOutmigration(event, logReportFileId)
            case RawEventType.EVENT_INTERNAL_INMIGRATION:         return executeInmigration(event, logReportFileId)
            case RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY:   return executeExtInmigration(event, logReportFileId)
            case RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY: return executeExtInmigration(event, logReportFileId)
            case RawEventType.EVENT_PREGNANCY_REGISTRATION:       return executePregnancyReg(event, logReportFileId)
            case RawEventType.EVENT_PREGNANCY_OUTCOME:            return executePregnancyOutcome(event, logReportFileId)
            case RawEventType.EVENT_MARITAL_RELATIONSHIP:         return executeMaritalRelationship(event, logReportFileId)
            case RawEventType.EVENT_CHANGE_HEAD_OF_HOUSEHOLD:     return executeChangeHead(event, logReportFileId)
            case RawEventType.EVENT_INCOMPLETE_VISIT:             return executeIncompleteVisit(event, logReportFileId)
            default: return null
        }
    }

    RawExecutionResult createRawEventErrorLog(RawEntity entity, RawEvent rawEvent, String columnName, List<RawMessage> errors, String logReportFileId) {
        //create errorLog
        def errorLog = new RawErrorLog(uuid: rawEvent.eventId, entity: entity, columnName: columnName, code: rawEvent.entityCode)
        errorLog.uuid = rawEvent.eventId
        errorLog.logReportFile = LogReportFile.findById(logReportFileId)
        errorLog.setMessages(errors)
        errorLog.save(flush:true)
        
        //save raw event 
        rawEvent.processed = ProcessedStatus.ERROR
        rawEvent.save(flush:true)
        
        return RawExecutionResult.newErrorResult(entity, errors)
    }

    /*def createErrorLog(RawEntity entity, String rawDomainId, String rawDomainCode, String columnName, List<RawMessage> errors, String logReportFileId) {
        //create errorLog
        def errorLog = new RawErrorLog(uuid: rawDomainId, entity: entity, columnName: columnName, code: code)
        errorLog.uuid = rawDomainId
        errorLog.logReportFile = LogReportFile.findById(logReportFileId)
        errorLog.setMessages(errors)
        errorLog.save(flush:true)
    }*/
    
    RawExecutionResult<Region> executeRegion(RawEvent rawEvent, String logReportFileId) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawRegion.findById(rawEvent?.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check region dependency existence
            def parentCode = rawObj.parentCode

            def depStatus = solveRegionDependency(parentCode, "parentCode", logReportFileId)
            dependencyResolved = depStatus.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createRegion(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages

                def result = createRawEventErrorLog(RawEntity.REGION, rawEvent, "regionCode", errors, logReportFileId)
                return result
            }
        }

        //Deal with these later
        return null
    }

    RawExecutionResult<Household> executeHousehold(RawEvent rawEvent, String logReportFileId) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawHousehold.findById(rawEvent?.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check region dependency existence
            def regionCode = rawObj.regionCode

            def depStatus = solveRegionDependency(regionCode, "regionCode", logReportFileId)
            dependencyResolved = depStatus.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createHousehold(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages

                def result = createRawEventErrorLog(RawEntity.HOUSEHOLD, rawEvent, "householdCode", errors, logReportFileId)
                return result
            }

        }

        return null
    }

    RawExecutionResult<Visit> executeVisit(RawEvent rawEvent, String logReportFileId) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawVisit.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check visit dependencies existence (household, member/respondent)
            def householdCode = rawObj.householdCode
            def respondentCode = rawObj.respondentCode

            //try to solve household dependency
            def depStatus = solveHouseholdDependency(householdCode, "householdCode", logReportFileId)
            dependencyResolved = depStatus.solved

            //try to solve member dependency (respondent)
            def depStatus2 = solveMemberDependency(respondentCode, "respondentCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            if (dependencyResolved) {

                def result = rawExecutionService.createVisit(rawObj, logReportFileId)
                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages

                def result = createRawEventErrorLog(RawEntity.VISIT, rawEvent, "code", errors, logReportFileId)
                return result
            }
        }

        return null
    }

    RawExecutionResult<Member> executeMemberEnu(RawEvent rawEvent, String logReportFileId) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawMemberEnu.findById(rawEvent.eventId)
        println "get raw obj(${rawEvent.eventId})"

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (household, father, mother)
            def householdCode = rawObj.householdCode
            def visitCode = rawObj.visitCode
            def fatherCode = rawObj.fatherCode
            def motherCode = rawObj.motherCode

            //try to solve household dependency
            def depStatus = solveHouseholdDependency(householdCode, "householdCode", logReportFileId)
            dependencyResolved = depStatus.solved

            //try to solve head dependency, check if the household has a head yet
            /*if (!rawObj.headRelationshipType?.equals("HOH") && !headRelationshipService.hasHeadOfHousehold(householdCode)){
                //try to find the head of household
                dependencyResolved = dependencyResolved && solveHeadDependency(householdCode, logReportFileId)
            }*/


            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency (father)
            def depStatus3 =  solveMemberDependency(fatherCode, "fatherCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus3.solved

            //try to solve member dependency (mother)
            def depStatus4 =  solveMemberDependency(motherCode, "motherCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus4.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createMemberEnu(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages
                errors << depStatus3.errorMessages
                errors << depStatus4.errorMessages

                def result = createRawEventErrorLog(RawEntity.MEMBER_ENUMERATION, rawEvent, "code", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<Death> executeDeath(RawEvent rawEvent, String logReportFileId) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawDeath.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, member)
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode

            //try to solve visit dependency
            def depStatus = solveVisitDependency(visitCode, "visitCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve member dependency
            def depStatus2 = solveMemberDependency(memberCode, "memberCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createDeath(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages

                def result = createRawEventErrorLog(RawEntity.DEATH, rawEvent, "memberCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<OutMigration> executeOutmigration(RawEvent rawEvent, String logReportFileId) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawOutMigration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (household, visit, member)
            def originCode = rawObj.originCode
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode

            //try to solve household dependency
            def depStatus = solveHouseholdDependency(originCode, "originCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency
            def depStatus3 = solveMemberDependency(memberCode, "memberCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus3.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createOutMigration(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages
                errors << depStatus3.errorMessages

                def result = createRawEventErrorLog(RawEntity.OUT_MIGRATION, rawEvent, "memberCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<InMigration> executeInmigration(RawEvent rawEvent, String logReportFileId) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawInMigration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (household, visit, member)
            def destinationCode = rawObj.destinationCode
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode

            //try to solve household dependency
            def depStatus = solveHouseholdDependency(destinationCode, "destinationCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency
            def depStatus3 = solveMemberDependency(memberCode, "memberCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus3.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createInMigration(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages
                errors << depStatus3.errorMessages

                def result = createRawEventErrorLog(RawEntity.IN_MIGRATION, rawEvent, "memberCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<InMigration> executeExtInmigration(RawEvent rawEvent, String logReportFileId) {
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
            def depStatus = solveHouseholdDependency(destinationCode, "destinationCode", logReportFileId)
            dependencyResolved = depStatus.solved

            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency - if it is reentry
            def depStatus3 = RawDependencyStatus.dependencySolved(RawEntity.EXTERNAL_INMIGRATION)
            if (rawObj.extMigrationType == ExternalInMigrationType.REENTRY.name()) { //couldnt find dependency
                depStatus3 = solveMemberDependency(memberCode, "memberCode", logReportFileId)
                dependencyResolved = dependencyResolved && depStatus3.solved
            }

            //try to solve member dependency (father)
            def depStatus4 = solveMemberDependency(fatherCode, "fatherCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus4.solved

            //try to solve member dependency (mother)
            def depStatus5 = solveMemberDependency(motherCode, "motherCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus5.solved


            if (dependencyResolved) {
                def result = rawExecutionService.createExternalInMigration(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages
                errors << depStatus3.errorMessages
                errors << depStatus4.errorMessages
                errors << depStatus5.errorMessages

                def result = createRawEventErrorLog(RawEntity.EXTERNAL_INMIGRATION, rawEvent, "memberCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<PregnancyRegistration> executePregnancyReg(RawEvent rawEvent, String logReportFileId) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawPregnancyRegistration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, member)
            def visitCode = rawObj.visitCode
            def motherCode = rawObj.motherCode

            //try to solve visit dependency
            def depStatus = solveVisitDependency(visitCode, "visitCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve member dependency
            def depStatus2 = solveMemberDependency(motherCode, "motherCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createPregnancyRegistration(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages

                def result = createRawEventErrorLog(RawEntity.PREGNANCY_REGISTRATION, rawEvent, "code", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<PregnancyOutcome> executePregnancyOutcome(RawEvent rawEvent, String logReportFileId) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawPregnancyOutcome.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, mother, father)
            def visitCode = rawObj.visitCode
            def motherCode = rawObj.motherCode
            def fatherCode = rawObj.fatherCode

            //try to solve visit dependency
            def depStatus = solveVisitDependency(visitCode, "visitCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve member dependency (father)
            def depStatus2 = solveMemberDependency(fatherCode, "fatherCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency (mother)
            def depStatus3 = solveMemberDependency(motherCode, "motherCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus3.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createPregnancyOutcome(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages
                errors << depStatus3.errorMessages

                def result = createRawEventErrorLog(RawEntity.PREGNANCY_OUTCOME, rawEvent, "code", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<MaritalRelationship> executeMaritalRelationship(RawEvent rawEvent, String logReportFileId) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawMaritalRelationship.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (memberA, memberB)
            def memberA = rawObj.memberA
            def memberB = rawObj.memberB

            //try to solve memberA dependency
            def depStatus = solveMemberDependency(memberA, "memberA", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve memberB dependency
            def depStatus2 = solveMemberDependency(memberB, "memberB", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            if (dependencyResolved) {
                def result = rawExecutionService.executeMaritalRelationship(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages

                def result = createRawEventErrorLog(RawEntity.MARITAL_RELATIONSHIP, rawEvent, "memberA", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<HeadRelationship> executeChangeHead(RawEvent rawEvent, String logReportFileId) {
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
            def depStatus = solveHouseholdDependency(householdCode, "householdCode", logReportFileId)
            dependencyResolved = depStatus.solved

            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency (oldHead)
            def depStatus3 = solveMemberDependency(oldHeadCode, "oldHeadCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus3.solved

            //try to solve member dependency (newHead)
            def depStatus4 = solveMemberDependency(newHeadCode, "newHeadCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus4.solved


            if (dependencyResolved) {
                def result = rawExecutionService.createChangeHead(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages
                errors << depStatus3.errorMessages
                errors << depStatus4.errorMessages

                def result = createRawEventErrorLog(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, rawEvent, "newHeadCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<IncompleteVisit> executeIncompleteVisit(RawEvent rawEvent, String logReportFileId) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawIncompleteVisit.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check visit dependencies existence (household, member/respondent)
            def visitCode = rawObj.visitCode
            def householdCode = rawObj.householdCode
            def memberCode = rawObj.memberCode

            //try to solve visit dependency
            def depStatus = solveVisitDependency(visitCode, "visitCode", logReportFileId)
            dependencyResolved = depStatus.solved

            //try to solve member dependency (memberCode)
            def depStatus2 = solveMemberDependency(memberCode, "memberCode", logReportFileId)
            dependencyResolved = dependencyResolved && depStatus2.solved

            if (dependencyResolved) {

                def result = rawExecutionService.createIncompleteVisit(rawObj, logReportFileId)
                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                errors << depStatus.errorMessages
                errors << depStatus2.errorMessages

                def result = createRawEventErrorLog(RawEntity.INCOMPLETE_VISIT, rawEvent, "visitCode", errors, logReportFileId)
                return result
            }
        }

        return null
    }

    RawMessage getRegionDependencyError(String regionCode, String columnName) {
        return errorMessageService.getRawMessage(RawEntity.REGION, "validation.dependency.region.not.found", [columnName, regionCode], [columnName])
    }

    RawMessage getHouseholdDependencyError(String householdCode, String columnName) {
        return errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.dependency.household.not.found", [columnName, householdCode], [columnName])
    }

    RawMessage getVisitDependencyError(String visitCode, String columnName) {
        return errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.dependency.visit.not.found", [columnName, visitCode], [columnName])
    }

    RawMessage getMemberDependencyError(String memberCode, String columnName) {
        return errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.dependency.member.not.found", [columnName, memberCode], [columnName])
    }

    RawDependencyStatus solveRegionDependency(String regionCode, String columnName, String logReportFileId) {
        //
        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        if (!regionService.exists(regionCode)){ //couldnt find dependency
            //find dependency event and execute
            def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_REGION, regionCode)

            def result = executeRegion(devent, logReportFileId)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = (result?.status==RawExecutionResult.Status.SUCCESS)
            } else {

                dependencyResolved = false
            }
        }

        errors << getRegionDependencyError(regionCode, columnName)

        return new RawDependencyStatus(RawEntity.REGION, dependencyResolved, errors)
    }

    RawDependencyStatus solveHouseholdDependency(String householdCode, String columnName, String logReportFileId) {
        //
        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        if (!householdService.exists(householdCode)){ //couldnt find dependency
            //find dependency event and execute
            def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_HOUSEHOLD, householdCode)

            def result = executeHousehold(devent, logReportFileId)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = (result?.status==RawExecutionResult.Status.SUCCESS)
            } else {

                dependencyResolved = false
            }
        }

        errors << getHouseholdDependencyError(householdCode, columnName)

        return new RawDependencyStatus(RawEntity.HOUSEHOLD, dependencyResolved, errors)
    }

    RawDependencyStatus solveMemberDependency(String memberCode, String columnName, String logReportFileId) {

        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        if (!memberService.exists(memberCode)) { //couldnt find dependency
            //find dependency event and execute
            def devent = getMemberEntryEvent(memberCode)

            def result = executeEvent(devent, logReportFileId)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = result?.status==RawExecutionResult.Status.SUCCESS
            } else {
                dependencyResolved = false
            }
        }

        errors << getMemberDependencyError(memberCode, columnName)

        return new RawDependencyStatus(RawEntity.MEMBER, dependencyResolved, errors)
    }

    RawDependencyStatus solveHeadDependency(String householdCode, String columnName, String logReportFileId) {
        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        if (!StringUtil.isBlank(householdCode)) { //couldnt find dependency
            //find dependency event and execute
            def devent = getHeadEntryEvent(householdCode)

            def result = executeEvent(devent, logReportFileId)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = result?.status==RawExecutionResult.Status.SUCCESS
            } else {
                dependencyResolved = false
            }

        }

        //THIS STILL NEED TO BE PROPER IMPLEMENTED

        return new RawDependencyStatus(RawEntity.MEMBER, dependencyResolved, errors)
    }

    RawDependencyStatus solveVisitDependency(String visitCode, String columnName, String logReportFileId) {
        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        if (!visitService.exists(visitCode)) { //couldnt find dependency
            //find dependency event and execute
            def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_VISIT, visitCode)

            def result = executeEvent(devent, logReportFileId)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = (result?.status==RawExecutionResult.Status.SUCCESS)
            } else {
                dependencyResolved = false
            }

        }

        errors << getVisitDependencyError(visitCode, columnName)

        return new RawDependencyStatus(RawEntity.VISIT, dependencyResolved, errors)
    }

    RawEvent getMemberEntryEvent(String memberCode) {
        if (StringUtil.isBlank(memberCode)) return null

        def devents = RawEvent.executeQuery("select e from RawEvent e where e.eventType in (:list) and ((e.entityCode=:code) or e.childCodes like :searchcodes) order by e.keyDate asc",
                                            [list: [RawEventType.EVENT_MEMBER_ENU, RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY, RawEventType.EVENT_PREGNANCY_OUTCOME], code:memberCode, searchcodes: "%${memberCode}%"], )
        def devent = !devents.empty ? devents.first() : null

        return devent
    }

    RawEvent getHeadEntryEvent(String householdCode) {
        if (StringUtil.isBlank(householdCode)) return null

        //check on memberEnu, Inmigration, ExtInmigration
        def opt1 = RawMemberEnu.findByHouseholdCodeAndHeadRelationshipTypeAndProcessedStatus(householdCode, HeadRelationshipType.HEAD_OF_HOUSEHOLD.code, ProcessedStatus.NOT_PROCESSED)



        def devents = RawEvent.executeQuery("select e from RawEvent e where e.eventType in (:list) and ((e.entityCode=:code) or e.childCodes like :searchcodes) order by e.keyDate asc",
                [list: [RawEventType.EVENT_MEMBER_ENU, RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY, RawEventType.EVENT_PREGNANCY_OUTCOME], code:memberCode, searchcodes: "%${memberCode}%"], )
        def devent = !devents.empty ? devents.first() : null

        return devent
    }

    def collectRegions() {
        RawRegion.withTransaction {
            def list = RawRegion.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "collectedDate", order: "asc"])

            list.each {
                def rawobj = new RawEvent(keyDate: it.collectedDate, eventType: RawEventType.EVENT_REGION, eventId: it.id, entityCode: it.regionCode)
                rawobj.save()

                println "region errors: ${rawobj.errors}"
            }

            //list.clear()
            //cleanUpGorm()
        }

    }

    def collectHouseholds() {
        def list = [] as List<RawHousehold>

        RawHousehold.withTransaction {
            list = RawHousehold.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "collectedDate", order: "asc"])
        }

        list.collate(500).each { batch ->
            RawHousehold.withTransaction {
                batch.each {
                    def event = new RawEvent(keyDate: it.collectedDate, eventType: RawEventType.EVENT_HOUSEHOLD, eventId: it.id, entityCode: it.householdCode)
                    event.save()
                    //println "compile house: ${event.errors}"
                }
                println "batch inserted: ${batch.size()}"
            }
        }

        list.clear()
        System.gc()
    }
    
    def collectVisit() {
        def list = [] as List<RawVisit>

        RawVisit.withTransaction {
            list = RawVisit.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "visitDate", order: "asc"])
        }

        list.collate(500).each { batch ->
            RawVisit.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.visitDate.atStartOfDay(), eventType: RawEventType.EVENT_VISIT, eventId: it.id, entityCode: it.code).save()
                }
                println "batch inserted: ${batch.size()}"
            }
        }

        list.clear()
        cleanUpGorm()
    }

    def collectIncompleteVisit() {
        def list = [] as List<RawIncompleteVisit>

        RawIncompleteVisit.withTransaction {
            list = RawIncompleteVisit.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "collectedDate", order: "asc"])
        }

        list.collate(200).each { batch ->
            RawIncompleteVisit.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.collectedDate.atStartOfDay(), eventType: RawEventType.EVENT_INCOMPLETE_VISIT, eventId: it.id, entityCode: it.visitCode).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectMemberEnu() {
        def list = [] as List<RawMemberEnu>

        RawMemberEnu.withTransaction {
            list = RawMemberEnu.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "residencyStartDate", order: "asc"])
        }

        list.collate(500).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.residencyStartDate.atStartOfDay(), eventType: RawEventType.EVENT_MEMBER_ENU, eventOrder: RawMemberOrder.getFromCode(it.headRelationshipType), eventId: it.id, entityCode: it.code).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()

    }
    
    def collectDeath() {
        def list = [] as List<RawDeath>

        RawDeath.withTransaction {
            list = RawDeath.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "deathDate", order: "asc"])
        }

        list.collate(100).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.deathDate.atStartOfDay(), eventType: RawEventType.EVENT_DEATH, eventId: it.id, entityCode: it.memberCode).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectOutMigration() {
        def list = [] as List<RawOutMigration>

        RawOutMigration.withTransaction {
            list = RawOutMigration.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "migrationDate", order: "asc"])
        }

        list.collate(200).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.migrationDate.atStartOfDay(), eventType: RawEventType.EVENT_OUTMIGRATION, eventId: it.id, entityCode: it.memberCode).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectInMigration() {
        def list = [] as List<RawInMigration>

        RawInMigration.withTransaction {
            list = RawInMigration.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "migrationDate", order: "asc"])
        }

        list.collate(200).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.migrationDate.atStartOfDay(), eventType: RawEventType.EVENT_INTERNAL_INMIGRATION, eventOrder: RawMemberOrder.getFromCode(it.headRelationshipType), eventId: it.id, entityCode: it.memberCode).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()

    }
    
    def collectExternalInMigration() {
        def list = [] as List<RawExternalInMigration>

        RawExternalInMigration.withTransaction {
            list = RawExternalInMigration.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "migrationDate", order: "asc"])
        }

        list.collate(300).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    boolean firstEntry = it.extMigrationType == ExternalInMigrationType.ENTRY.name()
                    new RawEvent(keyDate: it.migrationDate.atStartOfDay(), eventType: firstEntry ? RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY : RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY, eventOrder: RawMemberOrder.getFromCode(it.headRelationshipType), eventId: it.id, entityCode: it.memberCode).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()

    }
    
    def collectPregnancyRegistration() {
        def list = [] as List<RawPregnancyRegistration>

        RawPregnancyRegistration.withTransaction {
            list = RawPregnancyRegistration.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "recordedDate", order: "asc"])
        }

        list.collate(200).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.recordedDate.atStartOfDay(), eventType: RawEventType.EVENT_PREGNANCY_REGISTRATION, eventId: it.id, entityCode: it.code).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()

    }
    
    def collectPregnancyOutcome() {
        def list = [] as List<RawPregnancyOutcome>

        RawPregnancyOutcome.withTransaction {
            list = RawPregnancyOutcome.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "outcomeDate", order: "asc"])
        }

        list.collate(200).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    def codes = RawPregnancyChild.executeQuery("select p.childCode from RawPregnancyChild p where p.outcome=?", [it])
                    def event = new RawEvent(keyDate: it.outcomeDate.atStartOfDay(), eventType: RawEventType.EVENT_PREGNANCY_OUTCOME, eventId: it.id, entityCode: it.code)
                    event.setChildCodesFrom(codes)
                    event.save()
                }
            }
        }

        list.clear()
        cleanUpGorm()
    }
    
    def collectMaritalRelationshipStart() {
        def list = [] as List<RawMaritalRelationship>

        RawMaritalRelationship.withTransaction {
            list = RawMaritalRelationship.findAllByProcessedStatusAndStartDateIsNotNull(ProcessedStatus.NOT_PROCESSED, [sort: "startDate", order: "asc"])
        }

        list.collate(200).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.startDate.atStartOfDay(), eventType: RawEventType.EVENT_MARITAL_RELATIONSHIP, eventId: it.id, entityCode: it.memberA).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()

    }

    def collectMaritalRelationshipEnd() {
        def list = [] as List<RawMaritalRelationship>

        RawMaritalRelationship.withTransaction {
            list = RawMaritalRelationship.findAllByProcessedStatusAndEndDateIsNotNull(ProcessedStatus.NOT_PROCESSED, [sort: "endDate", order: "asc"])
        }

        list.collate(200).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.endDate.atStartOfDay(), eventType: RawEventType.EVENT_MARITAL_RELATIONSHIP, eventId: it.id, entityCode: it.memberA).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()
    }

    def collectChangeHoh() {
        def list = [] as List<RawChangeHead>

        RawChangeHead.withTransaction {
            list = RawChangeHead.findAllByProcessedStatus(ProcessedStatus.NOT_PROCESSED, [sort: "eventDate", order: "asc"])
        }

        list.collate(200).each { batch ->
            RawEvent.withTransaction {
                batch.each {
                    new RawEvent(keyDate: it.eventDate.atStartOfDay(), eventType: RawEventType.EVENT_CHANGE_HEAD_OF_HOUSEHOLD, eventId: it.id, entityCode: it.newHeadCode).save()
                }
            }
        }

        list.clear()
        cleanUpGorm()
    }

    ProcessedStatus getProcessedStatus(RawExecutionResult.Status resultStatus) {
        return resultStatus==RawExecutionResult.Status.SUCCESS ? ProcessedStatus.SUCCESS : ProcessedStatus.ERROR
    }
}
