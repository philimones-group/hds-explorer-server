package org.philimone.hds.explorer.services

import grails.gorm.transactions.Transactional
import net.betainteractive.io.LogOutput
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.json.SyncProcessedStatus
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.main.*
import org.philimone.hds.explorer.server.model.collect.raw.*

import java.time.LocalDateTime

@Transactional
class EventSyncService {

    def rawBatchExecutionService
    def generalUtilitiesService

    def executeAll(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "rawbatch-execute-all");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        //create log report file
        String reportFileId = null;

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)

            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.creationDate = LocalDateTime.now()

            logReport.addToLogFiles(reportFile)
            logReport = logReport.save(flush:true)

            reportFile = LogReportFile.findByKeyTimestamp(logReport.keyTimestamp)
            reportFileId = reportFile.id

            println(reportFileId)

        }

        try {

            rawBatchExecutionService.compileAndExecuteEvents(reportFileId)

        }catch (Exception ex){
            ex.printStackTrace()

            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
            output.println(ex.toString())
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = LogReportFile.findById(reportFileId)

            reportFile.end = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors
            reportFile.save(flush:true)

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.save(flush:true)

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def executeCompileEvents(LogReportCode logReportId){
        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "rawbatch-compile-events");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        try {

            rawBatchExecutionService.compileEvents()

        }catch (Exception ex){
            ex.printStackTrace()

            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
            output.println(ex.toString())
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)
            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.end = LocalDateTime.now()
            reportFile.creationDate = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.addToLogFiles(reportFile)
            logReport.save()
            //println("errors: ${logReport.errors}")
        }

        output.close();
    }

    def executeEvents(LogReportCode logReportId){
        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "rawbatch-execute-events");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        //create log report file
        String reportFileId = null;

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)

            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.creationDate = LocalDateTime.now()

            logReport.addToLogFiles(reportFile)
            logReport.save(flush:true)

            reportFile = LogReportFile.findByKeyTimestamp(logReport.keyTimestamp)
            reportFileId = reportFile.id
        }

        try {

            rawBatchExecutionService.executeEvents(reportFileId)

        }catch (Exception ex){
            ex.printStackTrace()

            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
            output.println(ex.toString())
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = LogReportFile.findById(reportFileId)

            reportFile.end = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors
            reportFile.save(flush:true)

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.save(flush:true)

            //println("errors: ${logReport.errors}")
        }

        output.close();
    }

    def executeResetErrors(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "rawbatch-reset-errors-processed");
        PrintStream output = log.output
        if (output == null) return;

        def start = LocalDateTime.now();
        int processed = 0
        int errors = 0
        def logStatusValue = LogStatus.FINISHED

        //create log report file
        String reportFileId = null;

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = new LogReportFile(creationDate: LocalDateTime.now(), fileName: log.logFileName, logReport: logReport)

            reportFile.keyTimestamp = logReport.keyTimestamp
            reportFile.start = start
            reportFile.creationDate = LocalDateTime.now()

            logReport.addToLogFiles(reportFile)
            logReport = logReport.save(flush:true)

            reportFile = LogReportFile.findByKeyTimestamp(logReport.keyTimestamp)
            reportFileId = reportFile.id

            println(reportFileId)

        }

        try {

            rawBatchExecutionService.resetEventsToNotProcessed(reportFileId)

        }catch (Exception ex){
            ex.printStackTrace()

            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
            output.println(ex.toString())
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = LogReportFile.findById(reportFileId)

            reportFile.end = LocalDateTime.now()
            reportFile.processedCount = processed
            reportFile.errorsCount = errors
            reportFile.save(flush:true)

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.save(flush:true)

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    List<SyncProcessedStatus> mainProcessedStatus() {

        def list = [
                getHouseholdStatus(),
                getMemberStatus(),
                getHouseholdRegStatus(),
                getVisitStatus(),
                getMemberEnuStatus(),
                getMaritalRelationshipStatus(),
                getInMigrationStatus(),
                getOutMigrationStatus(),
                getPregnancyRegistrationStatus(),
                getPregnancyOutcomeStatus(),
                getDeathStatus(),
                getChangeHeadStatus(),
                getIncompleteVisitStatus()
        ]

        return list
    }

    SyncProcessedStatus getHouseholdStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.households.label'
        status.totalRecords = Household.count()
        status.processed = -1          //.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = -1 //.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = -1       //.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = -1        //.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = -1       //.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getMemberStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.members.label'
        status.totalRecords = Member.count()-1 //minus Unknown Individual
        status.processed = -1          //.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = -1 //.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = -1       //.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = -1        //.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = -1       //.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getHouseholdRegStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.household.label'
        status.totalRecords = Household.count()
        status.processed = RawHousehold.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawHousehold.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawHousehold.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawHousehold.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawHousehold.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getVisitStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.visit.label'
        status.totalRecords = Visit.count()
        status.processed = RawVisit.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawVisit.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawVisit.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawVisit.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawVisit.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getMemberEnuStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.memberenu.label'
        status.totalRecords = -1
        status.processed = RawMemberEnu.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawMemberEnu.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawMemberEnu.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawMemberEnu.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawMemberEnu.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getMaritalRelationshipStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.maritalreg.label'
        status.totalRecords = MaritalRelationship.count()
        status.processed = RawMaritalRelationship.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawMaritalRelationship.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawMaritalRelationship.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawMaritalRelationship.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawMaritalRelationship.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getInMigrationStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.inmigration.label'
        status.totalRecords = InMigration.count()
        status.processed = RawInMigration.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawInMigration.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawInMigration.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawInMigration.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawInMigration.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getOutMigrationStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.outmigration.label'
        status.totalRecords = OutMigration.count()
        status.processed = RawOutMigration.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawOutMigration.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawOutMigration.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawOutMigration.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawOutMigration.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getPregnancyRegistrationStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.pregnancyreg.label'
        status.totalRecords = PregnancyRegistration.count()
        status.processed = RawPregnancyRegistration.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawPregnancyRegistration.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawPregnancyRegistration.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawPregnancyRegistration.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawPregnancyRegistration.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getPregnancyOutcomeStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.pregnancyoutcome.label'
        status.totalRecords = PregnancyOutcome.count()
        status.processed = RawPregnancyOutcome.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawPregnancyOutcome.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawPregnancyOutcome.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawPregnancyOutcome.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawPregnancyOutcome.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getDeathStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.death.label'
        status.totalRecords = Death.count()
        status.processed = RawDeath.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawDeath.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawDeath.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawDeath.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawDeath.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getChangeHeadStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.changehead.label'
        status.totalRecords = -1
        status.processed = RawChangeHead.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawChangeHead.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawChangeHead.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawChangeHead.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawChangeHead.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getIncompleteVisitStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.incompletevisit.label'
        status.totalRecords = IncompleteVisit.count()
        status.processed = RawIncompleteVisit.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawIncompleteVisit.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawIncompleteVisit.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawIncompleteVisit.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawIncompleteVisit.countByProcessedStatus(ProcessedStatus.)

        return status
    }
}
