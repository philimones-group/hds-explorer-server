package org.philimone.hds.explorer.server.model.collect.raw

import groovy.json.JsonSlurper
import net.betainteractive.utilities.StringUtil
import org.hibernate.SessionFactory
import org.philimone.hds.explorer.server.model.collect.raw.aggregate.RawEvent
import org.philimone.hds.explorer.server.model.enums.HeadRelationshipType
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.RawEntity
import org.philimone.hds.explorer.server.model.enums.RawEventType
import org.philimone.hds.explorer.server.model.enums.RawMemberOrder
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.HeadRelationshipEndType
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
import org.philimone.hds.explorer.server.model.main.collect.raw.RawDomainObj
import org.philimone.hds.explorer.server.model.main.collect.raw.RawEntityObj
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

        //System.gc()
        //println "clearing up"
    }
    
    /*
     * Organize data by processed = 0 and date of processing
     */
    def compileAndExecuteEvents(String logReportFileId, int executionLimit) {
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
        compileEvents(logReportFileId)

        executeEvents(logReportFileId, executionLimit)
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

        println "finished reset errors"
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

    def compileEvents(String logReportFileId){

        println "compiling events"

        resetEventsToNotProcessed(logReportFileId)

        //clear raw event
        RawEvent.withTransaction {
            RawEvent.executeUpdate("delete from RawEvent")
        }

        collectRegions()
        collectHouseholds()
        collectVisit()
        collectIncompleteVisit();
        collectMemberEnu()
        collectExternalInMigration()
        collectInMigration()
        collectPregnancyRegistration()
        collectPregnancyOutcome()
        collectMaritalRelationshipStart()
        collectMaritalRelationshipEnd()
        collectChangeHoh()
        collectOutMigration()
        collectDeath()

        println "finished compiling events"
    }

    def executeEvents(String logReportFileId, int executionLimit) {

        println "executing events"

        //read raw_events ordered by keyDate asc and eventtype asc

        def initialEvents = [] //as List<RawEvent> //Regions and Households
        def otherEvents = [] //as List<RawEvent>
        def maritalEvents = []
        def eventsWithErrors = new HashMap<String, RawEvent>()

        //read two different sets of events

        RawEvent.withTransaction {
            if (executionLimit > 0) {
                //limit the number of events to execute

                initialEvents = RawEvent.executeQuery("select e.id from RawEvent e where e.processed=:processed and e.eventType in (:list) order by e.eventType asc, e.keyDate asc", [processed: ProcessedStatus.NOT_PROCESSED, list: [RawEventType.EVENT_REGION, RawEventType.EVENT_HOUSEHOLD]], [max: executionLimit])
                otherEvents = RawEvent.executeQuery("select e.id from RawEvent e where e.processed=:processed and e.eventType not in (:list) order by e.keyDate asc, e.eventType asc, e.eventOrder asc, e.entityCode asc", [processed: ProcessedStatus.NOT_PROCESSED, list: [RawEventType.EVENT_REGION, RawEventType.EVENT_HOUSEHOLD]], [max: executionLimit]) //, [offset: offset, max: max])
                //maritalEvents = RawEvent.executeQuery("select e.id from RawEvent e where e.processed=:processed and e.eventType in (:list) order by e.keyDate asc, e.eventType asc, e.eventOrder asc", [processed: ProcessedStatus.NOT_PROCESSED, list: [RawEventType.EVENT_MARITAL_RELATIONSHIP]])

            } else {
                //execute all events

                initialEvents = RawEvent.executeQuery("select e.id from RawEvent e where e.processed=:processed and e.eventType in (:list) order by e.eventType asc, e.keyDate asc", [processed: ProcessedStatus.NOT_PROCESSED, list: [RawEventType.EVENT_REGION, RawEventType.EVENT_HOUSEHOLD]])
                otherEvents = RawEvent.executeQuery("select e.id from RawEvent e where e.processed=:processed and e.eventType not in (:list) order by e.keyDate asc, e.eventType asc, e.eventOrder asc", [processed: ProcessedStatus.NOT_PROCESSED, list: [RawEventType.EVENT_REGION, RawEventType.EVENT_HOUSEHOLD]]) //, [offset: offset, max: max])
                //maritalEvents = RawEvent.executeQuery("select e.id from RawEvent e where e.processed=:processed and e.eventType in (:list) order by e.keyDate asc, e.eventType asc, e.eventOrder asc", [processed: ProcessedStatus.NOT_PROCESSED, list: [RawEventType.EVENT_MARITAL_RELATIONSHIP]])
            }

        }

        println "execute initial events ${initialEvents.size()}"
        initialEvents.collate(100).each { batch ->
            batch.each { rawEventId ->
                RawEvent.withTransaction {
                    executeEvent(RawEvent.get(rawEventId), logReportFileId, eventsWithErrors)
                }
            }
        }

        println "execute other events ${otherEvents.size()}"
        otherEvents.collate(100).each { batch ->
            batch.each { rawEventId ->
                RawEvent.withTransaction {
                    def rawEvent = RawEvent.get(rawEventId)
                    println "event ${rawEvent?.eventId}, date=${StringUtil.format(rawEvent?.keyDate)}, type=${rawEvent?.eventType}, order=${rawEvent?.eventOrder}, code: ${rawEvent?.entityCode}"
                    def result = executeEvent(rawEvent, logReportFileId, eventsWithErrors)
                    //println "event result=${result}, ${result?.status}, ${result?.errorMessages)}"
                }
            }
        }

        println "execute marital events ${maritalEvents.size()}"
        maritalEvents.collate(100).each { batch ->
            batch.each { rawEventId ->
                RawEvent.withTransaction {
                    def rawEvent = RawEvent.get(rawEventId)
                    def result = executeEvent(rawEvent, logReportFileId, eventsWithErrors)
                }
            }
        }

        //GET TOTAL EVENTS TO EXECUTE AND ERROR LOGS COUNT - REPORT TO LogReportFile
        LogReportFile.withTransaction {
            def logReportFile = LogReportFile.get(logReportFileId)
            if (logReportFile != null) {
                def errorLogsCount = RawErrorLog.countByLogReportFile(logReportFile, [sort: "createdDate", order: "asc"])

                logReportFile.processedCount = initialEvents.size() + otherEvents.size() + maritalEvents.size()
                logReportFile.errorsCount = errorLogsCount

                logReportFile.save(flush:true)
            }
        }

        println "finished executing events"
        eventsWithErrors.clear()
        initialEvents.clear()
        otherEvents.clear()
        maritalEvents.clear()

        System.gc()
    }

    RawExecutionResult executeEvent(RawEvent event, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {

        if (event == null) return null

        RawExecutionResult result = null

        //check if there is a previous flagged error event for this individual - if yes dont execute this event - skip it
        def errorEvent = eventsWithErrors.get(event.entityCode)
        if (errorEvent != null) {

            //if (errorEvent.eventType==RawEventType.EVENT_MARITAL_RELATIONSHIP && event.eventType==RawEventType.EVENT_DEATH){
                //SKIP killing because of a marital failed
            //}

            //skipped event due to previous error
            println "the event [id=${event.eventId},code=${event.entityCode}](${event.eventType.name()}) - will be skipped because a previous event [id=${errorEvent.eventId}](${errorEvent.eventType.name()}) has errors})"

            def entityObj = getEntityOject(event)
            def errorMessage = errorMessageService.getRawMessage(entityObj.entity, "validation.dependency.event.previous.error", [event.eventId, event.entityCode, event.eventType.name(), errorEvent.eventId, errorEvent.eventType.name()], [])
            createRawEventErrorLog(entityObj.entity, event, entityObj.domainObj, "entity_code", [errorMessage], logReportFileId)

            return null

        } else if (event.eventType == RawEventType.EVENT_PREGNANCY_OUTCOME) {
            //handle differently
            def codesList = StringUtil.toList(event.childCodes)

            codesList?.each {
                errorEvent = eventsWithErrors.get(it)
                if (errorEvent != null) {
                    //skipped event due to previous error
                    println "the event [id=${event.eventId},code=${event.entityCode}](${event.eventType.name()}) - will be skipped because a previous event [id=${errorEvent.eventId}](${errorEvent.eventType.name()}) has errors})"

                    def entityObj = getEntityOject(event)
                    def errorMessage = errorMessageService.getRawMessage(entityObj.entity, "validation.dependency.event.previous.error", [event.eventId, event.entityCode, event.eventType.name(), errorEvent.eventId, errorEvent.eventType.name()], [])
                    createRawEventErrorLog(entityObj.entity, event, entityObj.domainObj, "entity_code", [errorMessage], logReportFileId)

                    return null
                }
            }
        }
        
        switch (event?.eventType) {
            case RawEventType.EVENT_REGION:                       result = executeRegion(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_HOUSEHOLD:                    result = executeHousehold(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_VISIT:                        result = executeVisit(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_MEMBER_ENU:                   result = executeMemberEnu(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_DEATH:                        result = executeDeath(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_OUTMIGRATION:                 result = executeOutmigration(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_INTERNAL_INMIGRATION:         result = executeInmigration(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY:   result = executeExtInmigration(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY: result = executeExtInmigration(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_PREGNANCY_REGISTRATION:       result = executePregnancyReg(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_PREGNANCY_OUTCOME:            result = executePregnancyOutcome(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_MARITAL_RELATIONSHIP:         result = executeMaritalRelationship(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_CHANGE_HEAD_OF_HOUSEHOLD:     result = executeChangeHead(event, logReportFileId, eventsWithErrors); break
            case RawEventType.EVENT_INCOMPLETE_VISIT:             result = executeIncompleteVisit(event, logReportFileId, eventsWithErrors); break
        }

        if (result?.status == RawExecutionResult.Status.ERROR) {
            //flag the error

            if (event.eventType == RawEventType.EVENT_PREGNANCY_OUTCOME) {
                //handle differently
                def codesList = StringUtil.toList(event.childCodes)
                codesList?.each {
                    eventsWithErrors.put(it, event)
                }

            } else if (event.eventType != RawEventType.EVENT_MARITAL_RELATIONSHIP) { //dont include marital relationship on errors
                eventsWithErrors.put(event.entityCode, event)
            }
        }
        
        return result
    }

    RawExecutionResult createRawEventErrorLog(RawEntity entity, RawEvent rawEvent, RawDomainObj domainObj, String columnName, List<RawMessage> errors, String logReportFileId) {
        //create errorLog
        def errorLog = new RawErrorLog(uuid: rawEvent.eventId, entity: entity, columnName: columnName, code: rawEvent.entityCode)
        errorLog.uuid = rawEvent.eventId
        errorLog.logReportFile = LogReportFile.findById(logReportFileId)
        errorLog.setMessages(errors)
        errorLog.save(flush:true)
        
        //save raw event 
        rawEvent.processed = ProcessedStatus.ERROR
        rawEvent.save(flush:true)

        markDomainAsError(domainObj)
        
        return RawExecutionResult.newErrorResult(entity, errors)
    }

    def markDomainAsError(RawDomainObj domainObj){

        if (domainObj.domainInstance instanceof RawRegion) {
            def obj = (RawRegion) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawHousehold) {
            def obj = (RawHousehold) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawVisit) {
            def obj = (RawVisit) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawMemberEnu) {
            def obj = (RawMemberEnu) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawDeath) {
            def obj = (RawDeath) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawOutMigration) {
            def obj = (RawOutMigration) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawInMigration) {
            def obj = (RawInMigration) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawExternalInMigration) {
            def obj = (RawExternalInMigration) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawPregnancyRegistration) {
            def obj = (RawPregnancyRegistration) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawPregnancyOutcome) {
            def obj = (RawPregnancyOutcome) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawMaritalRelationship) {
            def obj = (RawMaritalRelationship) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawChangeHead) {
            def obj = (RawChangeHead) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

        if (domainObj.domainInstance instanceof RawIncompleteVisit) {
            def obj = (RawIncompleteVisit) domainObj.domainInstance
            obj.processedStatus = ProcessedStatus.ERROR
            obj.save(flush:true)
        }

    }

    RawEntityObj getEntityOject(RawEvent rawEvent) {
        RawEntityObj obj = null

        switch (rawEvent?.eventType) {
            case RawEventType.EVENT_REGION:
                def rawObj = RawRegion.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.REGION, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_HOUSEHOLD:
                def rawObj = RawHousehold.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.HOUSEHOLD, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_VISIT:
                def rawObj = RawVisit.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.VISIT, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_MEMBER_ENU:
                def rawObj = RawMemberEnu.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.MEMBER_ENUMERATION, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_DEATH:
                def rawObj = RawDeath.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.DEATH, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_OUTMIGRATION:
                def rawObj = RawOutMigration.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.OUT_MIGRATION, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_INTERNAL_INMIGRATION:
                def rawObj = RawInMigration.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.IN_MIGRATION, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY:
            case RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY:
                def rawObj = RawExternalInMigration.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.EXTERNAL_INMIGRATION, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_PREGNANCY_REGISTRATION:
                def rawObj = RawPregnancyRegistration.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.PREGNANCY_REGISTRATION, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_PREGNANCY_OUTCOME:
                def rawObj = RawPregnancyOutcome.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.PREGNANCY_OUTCOME, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_MARITAL_RELATIONSHIP:
                def rawObj = RawMaritalRelationship.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.MARITAL_RELATIONSHIP, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_CHANGE_HEAD_OF_HOUSEHOLD:
                def rawObj = RawChangeHead.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, RawDomainObj.attach(rawObj))

            case RawEventType.EVENT_INCOMPLETE_VISIT:
                def rawObj = RawIncompleteVisit.findById(rawEvent?.eventId)
                return new RawEntityObj(RawEntity.INCOMPLETE_VISIT, RawDomainObj.attach(rawObj))
        }
        return obj;
    }
    
    RawExecutionResult<Region> executeRegion(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawRegion.findById(rawEvent?.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check region dependency existence
            def parentCode = rawObj.parentCode

            def depStatus = solveRegionDependency(parentCode, "parentCode", logReportFileId, eventsWithErrors)
            dependencyResolved = depStatus.solved

            if (dependencyResolved || StringUtil.isBlank(parentCode)) { //is solved or its a root
                def result = rawExecutionService.createRegion(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)

                def result = createRawEventErrorLog(RawEntity.REGION, rawEvent, RawDomainObj.attach(rawObj), "regionCode", errors, logReportFileId)
                return result
            }
        }

        //Deal with these later
        return null
    }

    RawExecutionResult<Household> executeHousehold(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawHousehold.findById(rawEvent?.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check region dependency existence
            def regionCode = rawObj.regionCode

            def depStatus = solveRegionDependency(regionCode, "regionCode", logReportFileId, eventsWithErrors)
            dependencyResolved = depStatus.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createHousehold(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)

                def result = createRawEventErrorLog(RawEntity.HOUSEHOLD, rawEvent, RawDomainObj.attach(rawObj), "householdCode", errors, logReportFileId)
                return result
            }

        }

        return null
    }

    RawExecutionResult<Visit> executeVisit(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawVisit.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check visit dependencies existence (household, member/respondent)
            def householdCode = rawObj.householdCode
            def respondentCode = rawObj.respondentCode

            //try to solve household dependency
            def depStatus = solveHouseholdDependency(householdCode, "householdCode", logReportFileId, eventsWithErrors)
            dependencyResolved = depStatus.solved

            //try to solve member dependency (respondent) - dont solve this dependency to avoid loops
            //def depStatus2 = solveMemberDependency(respondentCode, "respondentCode", logReportFileId)
            //dependencyResolved = dependencyResolved && (depStatus2.solved || StringUtil.isBlank(respondentCode))

            if (dependencyResolved) {

                def result = rawExecutionService.createVisit(rawObj, logReportFileId)
                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                //if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)

                def result = createRawEventErrorLog(RawEntity.VISIT, rawEvent, RawDomainObj.attach(rawObj), "code", errors, logReportFileId)
                return result
            }
        }

        return null
    }

    RawExecutionResult<Member> executeMemberEnu(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawMemberEnu.findById(rawEvent.eventId)
        //println "get raw obj(${rawEvent.eventId})"

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (household, father, mother)
            def householdCode = rawObj.householdCode
            def visitCode = rawObj.visitCode
            def fatherCode = rawObj.fatherCode
            def motherCode = rawObj.motherCode

            //try to solve household dependency
            def depStatus = solveHouseholdDependency(householdCode, "householdCode", logReportFileId, eventsWithErrors)
            dependencyResolved = depStatus.solved

            //try to solve head dependency, check if the household has a head yet
            /*if (!rawObj.headRelationshipType?.equals("HOH") && !headRelationshipService.hasHeadOfHousehold(householdCode)){
                //try to find the head of household
                dependencyResolved = dependencyResolved && solveHeadDependency(householdCode, logReportFileId)
            }*/


            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency (father)
            def depStatus3 =  solveMemberDependency(fatherCode, "fatherCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus3.solved

            //try to solve member dependency (mother)
            def depStatus4 =  solveMemberDependency(motherCode, "motherCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus4.solved

            //try to solve head dependency (cannot register a new individual in a household without a head)
            def depStatus5 = RawDependencyStatus.dependencySolved(RawEntity.MEMBER_ENUMERATION)
            if (rawEvent.eventOrder != RawMemberOrder.HEAD_OF_HOUSEHOLD) {
                depStatus5 = solveHeadDependency(householdCode, "householdCode", logReportFileId, eventsWithErrors)
                dependencyResolved = dependencyResolved && depStatus5.solved
            }

            if (dependencyResolved) {
                def result = rawExecutionService.createMemberEnu(rawObj, logReportFileId)
                if (result==null) println("result null")
                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                println "result ${result.status}, errors: ${result.errorMessages}"

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)
                if (!depStatus3.errorMessages.isEmpty()) errors.addAll(depStatus3.errorMessages)
                if (!depStatus4.errorMessages.isEmpty()) errors.addAll(depStatus4.errorMessages)
                if (!depStatus5.errorMessages.isEmpty()) errors.addAll(depStatus5.errorMessages)

                def result = createRawEventErrorLog(RawEntity.MEMBER_ENUMERATION, rawEvent, RawDomainObj.attach(rawObj), "code", errors, logReportFileId)
                println "result2 ${result.status}, errors: ${result.errorMessages}"
                return result
            }
        }

        println "rawObj not found"
        return null
    }

    RawExecutionResult<Death> executeDeath(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawDeath.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, member)
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode

            //try to solve visit dependency
            def depStatus = solveVisitDependency(visitCode, "visitCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve member dependency
            def depStatus2 = solveMemberDependency(memberCode, "memberCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createDeath(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)

                def result = createRawEventErrorLog(RawEntity.DEATH, rawEvent, RawDomainObj.attach(rawObj), "memberCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<OutMigration> executeOutmigration(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawOutMigration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (household, visit, member)
            def originCode = rawObj.originCode
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode

            //try to solve household dependency
            def depStatus = solveHouseholdDependency(originCode, "originCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency
            def depStatus3 = solveMemberDependency(memberCode, "memberCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus3.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createOutMigration(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)
                if (!depStatus3.errorMessages.isEmpty()) errors.addAll(depStatus3.errorMessages)

                def result = createRawEventErrorLog(RawEntity.OUT_MIGRATION, rawEvent, RawDomainObj.attach(rawObj), "memberCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<InMigration> executeInmigration(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawInMigration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (household, visit, member)
            def destinationCode = rawObj.destinationCode
            def visitCode = rawObj.visitCode
            def memberCode = rawObj.memberCode

            //try to solve household dependency
            def depStatus = solveHouseholdDependency(destinationCode, "destinationCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency
            def depStatus3 = solveMemberDependency(memberCode, "memberCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus3.solved

            //try to solve head dependency (cannot register a new individual in a household without a head)
            def depStatus4 =  RawDependencyStatus.dependencySolved(RawEntity.IN_MIGRATION)
            if (rawEvent.eventOrder != RawMemberOrder.HEAD_OF_HOUSEHOLD) {
                depStatus4 = solveHeadDependency(destinationCode, "destinationCode", logReportFileId, eventsWithErrors)
                dependencyResolved = dependencyResolved && depStatus4.solved
            }

            if (dependencyResolved) {
                def result = rawExecutionService.createInMigration(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)
                if (!depStatus3.errorMessages.isEmpty()) errors.addAll(depStatus3.errorMessages)
                if (!depStatus4.errorMessages.isEmpty()) errors.addAll(depStatus4.errorMessages)

                def result = createRawEventErrorLog(RawEntity.IN_MIGRATION, rawEvent, RawDomainObj.attach(rawObj), "memberCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<InMigration> executeExtInmigration(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
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
            def depStatus = solveHouseholdDependency(destinationCode, "destinationCode", logReportFileId, eventsWithErrors)
            dependencyResolved = depStatus.solved

            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency - if it is reentry
            def depStatus3 = RawDependencyStatus.dependencySolved(RawEntity.EXTERNAL_INMIGRATION)
            if (rawObj.extMigrationType?.equals(ExternalInMigrationType.REENTRY.name())) { //couldnt find dependency
                depStatus3 = solveMemberDependency(memberCode, "memberCode", logReportFileId, eventsWithErrors)
                dependencyResolved = dependencyResolved && depStatus3.solved
            }

            //try to solve member dependency (father)
            def depStatus4 = solveMemberDependency(fatherCode, "fatherCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus4.solved

            //try to solve member dependency (mother)
            def depStatus5 = solveMemberDependency(motherCode, "motherCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus5.solved

            //try to solve head dependency (cannot register a new individual in a household without a head)
            def depStatus6 = RawDependencyStatus.dependencySolved(RawEntity.EXTERNAL_INMIGRATION)
            if (rawEvent.eventOrder != RawMemberOrder.HEAD_OF_HOUSEHOLD) {
                depStatus6 = solveHeadDependency(destinationCode, "destinationCode", logReportFileId, eventsWithErrors)
                dependencyResolved = dependencyResolved && depStatus6.solved
            }

            if (dependencyResolved) {
                def result = rawExecutionService.createExternalInMigration(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)
                if (!depStatus3.errorMessages.isEmpty()) errors.addAll(depStatus3.errorMessages)
                if (!depStatus4.errorMessages.isEmpty()) errors.addAll(depStatus4.errorMessages)
                if (!depStatus5.errorMessages.isEmpty()) errors.addAll(depStatus5.errorMessages)
                if (!depStatus6.errorMessages.isEmpty()) errors.addAll(depStatus6.errorMessages)

                def result = createRawEventErrorLog(RawEntity.EXTERNAL_INMIGRATION, rawEvent, RawDomainObj.attach(rawObj), "memberCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<PregnancyRegistration> executePregnancyReg(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawPregnancyRegistration.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, member)
            def visitCode = rawObj.visitCode
            def motherCode = rawObj.motherCode

            //try to solve visit dependency
            def depStatus = solveVisitDependency(visitCode, "visitCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve member dependency
            def depStatus2 = solveMemberDependency(motherCode, "motherCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createPregnancyRegistration(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)

                def result = createRawEventErrorLog(RawEntity.PREGNANCY_REGISTRATION, rawEvent, RawDomainObj.attach(rawObj), "code", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<PregnancyOutcome> executePregnancyOutcome(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawPregnancyOutcome.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (visit, mother, father)
            def visitCode = rawObj.visitCode
            def motherCode = rawObj.motherCode
            def fatherCode = rawObj.fatherCode

            //try to solve visit dependency
            def depStatus = solveVisitDependency(visitCode, "visitCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve member dependency (father)
            def depStatus2 = solveMemberDependency(fatherCode, "fatherCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency (mother)
            def depStatus3 = solveMemberDependency(motherCode, "motherCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus3.solved

            if (dependencyResolved) {
                def result = rawExecutionService.createPregnancyOutcome(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)
                if (!depStatus3.errorMessages.isEmpty()) errors.addAll(depStatus3.errorMessages)

                def result = createRawEventErrorLog(RawEntity.PREGNANCY_OUTCOME, rawEvent, RawDomainObj.attach(rawObj), "code", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<MaritalRelationship> executeMaritalRelationship(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawMaritalRelationship.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check dependencies existence (memberA, memberB)
            def memberA = rawObj.memberA
            def memberB = rawObj.memberB

            //try to solve memberA dependency
            def depStatus = solveMemberDependency(memberA, "memberA", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus.solved

            //try to solve memberB dependency
            def depStatus2 = solveMemberDependency(memberB, "memberB", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            if (dependencyResolved) {
                def result = rawExecutionService.executeMaritalRelationship(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)

                def result = createRawEventErrorLog(RawEntity.MARITAL_RELATIONSHIP, rawEvent, RawDomainObj.attach(rawObj), "memberA", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<HeadRelationship> executeChangeHead(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
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
            def depStatus = solveHouseholdDependency(householdCode, "householdCode", logReportFileId, eventsWithErrors)
            dependencyResolved = depStatus.solved

            //try to solve visit dependency
            def depStatus2 = solveVisitDependency(visitCode, "visitCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            //try to solve member dependency (oldHead)
            def depStatus3 = solveMemberDependency(oldHeadCode, "oldHeadCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus3.solved

            //try to solve member dependency (newHead)
            def depStatus4 = solveMemberDependency(newHeadCode, "newHeadCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus4.solved


            if (dependencyResolved) {
                def result = rawExecutionService.createChangeHead(rawObj, logReportFileId)

                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)
                if (!depStatus3.errorMessages.isEmpty()) errors.addAll(depStatus3.errorMessages)
                if (!depStatus4.errorMessages.isEmpty()) errors.addAll(depStatus4.errorMessages)

                def result = createRawEventErrorLog(RawEntity.CHANGE_HEAD_OF_HOUSEHOLD, rawEvent, RawDomainObj.attach(rawObj), "newHeadCode", errors, logReportFileId)
                return result
            }

            return null
        }

        return null
    }

    RawExecutionResult<IncompleteVisit> executeIncompleteVisit(RawEvent rawEvent, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {

        if (rawEvent == null || rawEvent?.isProcessed()) return null

        def rawObj = RawIncompleteVisit.findById(rawEvent.eventId)

        if (rawObj != null) {

            def dependencyResolved = true

            //check visit dependencies existence (household, member/respondent)
            def visitCode = rawObj.visitCode
            def householdCode = rawObj.householdCode
            def memberCode = rawObj.memberCode

            //try to solve visit dependency
            def depStatus = solveVisitDependency(visitCode, "visitCode", logReportFileId, eventsWithErrors)
            dependencyResolved = depStatus.solved

            //try to solve member dependency (memberCode)
            def depStatus2 = solveMemberDependency(memberCode, "memberCode", logReportFileId, eventsWithErrors)
            dependencyResolved = dependencyResolved && depStatus2.solved

            if (dependencyResolved) {

                def result = rawExecutionService.createIncompleteVisit(rawObj, logReportFileId)
                //set event has processed
                rawEvent.processed = getProcessedStatus(result?.status)
                rawEvent.save(flush:true)

                return result
            } else {
                def errors = new ArrayList<RawMessage>()
                if (!depStatus.errorMessages.isEmpty()) errors.addAll(depStatus.errorMessages)
                if (!depStatus2.errorMessages.isEmpty()) errors.addAll(depStatus2.errorMessages)

                def result = createRawEventErrorLog(RawEntity.INCOMPLETE_VISIT, rawEvent, RawDomainObj.attach(rawObj), "visitCode", errors, logReportFileId)
                return result
            }
        }

        return null
    }

    RawMessage getRegionDependencyError(String regionCode, String columnName) {
        return errorMessageService.getRawMessage(RawEntity.REGION, "validation.dependency.region.not.found", [regionCode, columnName], [columnName])
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

    RawMessage getHeadDependencyError(String householdCode, String columnName) {
        return errorMessageService.getRawMessage(RawEntity.HOUSEHOLD, "validation.dependency.head.not.found", [columnName, householdCode], [columnName])
    }

    RawDependencyStatus solveRegionDependency(String regionCode, String columnName, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        //
        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        if (!regionService.exists(regionCode)){ //couldnt find dependency
            //find dependency event and execute
            def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_REGION, regionCode)

            def result = executeEvent(devent, logReportFileId, eventsWithErrors)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = (result?.status==RawExecutionResult.Status.SUCCESS)
            } else {

                dependencyResolved = false
            }
        }

        if (!dependencyResolved)
            errors.add(getRegionDependencyError(regionCode, columnName))

        return new RawDependencyStatus(RawEntity.REGION, dependencyResolved, errors)
    }

    RawDependencyStatus solveHouseholdDependency(String householdCode, String columnName, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        //
        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        if (!householdService.exists(householdCode)){ //couldnt find dependency
            //find dependency event and execute
            def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_HOUSEHOLD, householdCode)

            def result = executeEvent(devent, logReportFileId, eventsWithErrors)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = (result?.status==RawExecutionResult.Status.SUCCESS)
            } else {

                dependencyResolved = false
            }
        }

        if (!dependencyResolved)
            errors.add(getHouseholdDependencyError(householdCode, columnName))

        return new RawDependencyStatus(RawEntity.HOUSEHOLD, dependencyResolved, errors)
    }

    RawDependencyStatus solveMemberDependency(String memberCode, String columnName, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {

        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        if (!memberService.exists(memberCode)) { //couldnt find dependency
            //find dependency event and execute
            def devent = getMemberEntryEvent(memberCode)

            def result = executeEvent(devent, logReportFileId, eventsWithErrors)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = result?.status==RawExecutionResult.Status.SUCCESS
            } else {
                dependencyResolved = false
            }
        }

        if (!dependencyResolved)
            errors.add(getMemberDependencyError(memberCode, columnName))

        return new RawDependencyStatus(RawEntity.MEMBER, dependencyResolved, errors)
    }

    RawDependencyStatus solveHeadDependency(String householdCode, String columnName, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        def currentHead =  headRelationshipService.getCurrentActiveHouseholdHead(householdCode)//getCurrentHouseholdHead(householdCode)
        def hasHouseholdHead = currentHead != null && currentHead.endType == HeadRelationshipEndType.NOT_APPLICABLE

        if (!hasHouseholdHead) { //couldnt find dependency
            //find dependency event and execute
            def devent = getHeadEntryEvent(householdCode)

            def result = executeEvent(devent, logReportFileId, eventsWithErrors)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = result?.status==RawExecutionResult.Status.SUCCESS
            } else {
                dependencyResolved = false
            }

        }

        if (!dependencyResolved)
            errors.add(getHeadDependencyError(householdCode, columnName))

        return new RawDependencyStatus(RawEntity.HOUSEHOLD, dependencyResolved, errors)
    }

    RawDependencyStatus solveVisitDependency(String visitCode, String columnName, String logReportFileId, HashMap<String, RawEvent> eventsWithErrors) {
        def dependencyResolved = true
        def errors = new ArrayList<RawMessage>()

        if (!visitService.exists(visitCode)) { //couldnt find dependency
            //find dependency event and execute
            def devent = RawEvent.findByEventTypeAndEntityCode(RawEventType.EVENT_VISIT, visitCode)

            def result = executeEvent(devent, logReportFileId, eventsWithErrors)

            if (result != null && devent != null){
                //set event has processed
                devent.processed = getProcessedStatus(result?.status)
                devent.save(flush:true)

                dependencyResolved = (result?.status==RawExecutionResult.Status.SUCCESS)
            } else {
                dependencyResolved = false
            }

        }

        if (!dependencyResolved)
            errors.add(getVisitDependencyError(visitCode, columnName))

        return new RawDependencyStatus(RawEntity.VISIT, dependencyResolved, errors)
    }

    RawEvent getMemberEntryEvent(String memberCode) {
        if (StringUtil.isBlank(memberCode)) return null

        def devents = RawEvent.executeQuery("select e from RawEvent e where e.eventType in (:list) and e.entityCode=:code order by e.keyDate asc",
                                            [list: [RawEventType.EVENT_MEMBER_ENU, RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY], code:memberCode])

        if (devents.empty) { //search if is a child in pregnancy outcomes - this mother could be a child of another mother
            devents = RawEvent.executeQuery("select e from RawEvent e where e.eventType = :event and e.childCodes like :searchcodes order by e.keyDate asc",
                    [event: RawEventType.EVENT_PREGNANCY_OUTCOME, searchcodes: "%${memberCode}%"])
        }

        def devent = !devents.empty ? devents.first() : null

        return devent
    }

    RawEvent getHeadEntryEvent(String householdCode) {
        if (StringUtil.isBlank(householdCode)) return null
        def eventTypeList = [RawEventType.EVENT_MEMBER_ENU, RawEventType.EVENT_INTERNAL_INMIGRATION, RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY, RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY]
        def devents = RawEvent.executeQuery("select e from RawEvent e where e.eventType in (:list) and e.eventHouseholdCode=:code and e.eventOrder=:eorder and e.processed!=:prcd order by e.keyDate asc",
                [list: eventTypeList, code:householdCode, eorder: RawMemberOrder.HEAD_OF_HOUSEHOLD, prcd: ProcessedStatus.SUCCESS])

        //get the first event on the list
        return !devents.empty ? devents.first() : null
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
                    new RawEvent(keyDate: it.visitDate.atStartOfDay(), eventType: RawEventType.EVENT_VISIT, eventId: it.id, eventHouseholdCode: it.householdCode, entityCode: it.code).save()
                }
                println "batch visit inserted: ${batch.size()}"

                def session = sessionFactory.currentSession
                session.flush()
                session.clear()
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
                    new RawEvent(keyDate: it.collectedDate, eventType: RawEventType.EVENT_INCOMPLETE_VISIT, eventId: it.id, eventHouseholdCode: it.householdCode, entityCode: it.visitCode).save()
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
                    new RawEvent(keyDate: it.residencyStartDate.atStartOfDay(), eventType: RawEventType.EVENT_MEMBER_ENU, eventOrder: RawMemberOrder.getFromCode(it.headRelationshipType), eventId: it.id, eventHouseholdCode: it.householdCode, entityCode: it.code).save()
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
                    new RawEvent(keyDate: it.deathDate.atStartOfDay(), eventType: RawEventType.EVENT_DEATH, eventId: it.id, eventHouseholdCode: it.visitCode, entityCode: it.memberCode).save()
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
                    new RawEvent(keyDate: it.migrationDate.atStartOfDay(), eventType: RawEventType.EVENT_OUTMIGRATION, eventId: it.id, eventHouseholdCode: it.visitCode, entityCode: it.memberCode).save()
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
                    new RawEvent(keyDate: it.migrationDate.atStartOfDay(), eventType: RawEventType.EVENT_INTERNAL_INMIGRATION, eventOrder: RawMemberOrder.getFromCode(it.headRelationshipType), eventId: it.id, eventHouseholdCode: it.destinationCode, entityCode: it.memberCode).save()
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
                    new RawEvent(keyDate: it.migrationDate.atStartOfDay(), eventType: firstEntry ? RawEventType.EVENT_EXTERNAL_INMIGRATION_ENTRY : RawEventType.EVENT_EXTERNAL_INMIGRATION_REENTRY, eventOrder: RawMemberOrder.getFromCode(it.headRelationshipType), eventId: it.id, eventHouseholdCode: it.destinationCode, entityCode: it.memberCode).save()
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
                    new RawEvent(keyDate: it.recordedDate.atStartOfDay(), eventType: RawEventType.EVENT_PREGNANCY_REGISTRATION, eventId: it.id, eventHouseholdCode: it.visitCode, entityCode: it.code).save()
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
                    def codes = RawPregnancyChild.executeQuery("select p.childCode from RawPregnancyChild p where p.outcome=?0", [it])
                    def event = new RawEvent(keyDate: it.outcomeDate.atStartOfDay(), eventType: RawEventType.EVENT_PREGNANCY_OUTCOME, eventId: it.id, eventHouseholdCode: it.visitCode, entityCode: it.code)
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
                    new RawEvent(keyDate: it.collectedDate, eventType: RawEventType.EVENT_MARITAL_RELATIONSHIP, eventId: it.id, eventHouseholdCode: it.collectedHouseholdId, entityCode: it.memberA).save()
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
                    new RawEvent(keyDate: it.collectedDate, eventType: RawEventType.EVENT_MARITAL_RELATIONSHIP, eventId: it.id, eventHouseholdCode: it.collectedHouseholdId, entityCode: it.memberA).save()
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
                    new RawEvent(keyDate: it.eventDate.atStartOfDay(), eventType: RawEventType.EVENT_CHANGE_HEAD_OF_HOUSEHOLD, eventId: it.id, eventHouseholdCode: it.householdCode, entityCode: it.newHeadCode).save()
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
