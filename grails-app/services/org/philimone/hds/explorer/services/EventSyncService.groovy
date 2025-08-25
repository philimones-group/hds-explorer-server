package org.philimone.hds.explorer.services

import com.ibm.icu.util.EthiopicCalendar
import net.betainteractive.io.LogOutput
import net.betainteractive.utilities.DateUtil
import net.betainteractive.utilities.GeneralUtil
import net.betainteractive.utilities.StringUtil
import org.philimone.hds.explorer.io.SystemPath
import org.philimone.hds.explorer.server.model.enums.LogStatus
import org.philimone.hds.explorer.server.model.enums.ProcessedStatus
import org.philimone.hds.explorer.server.model.enums.settings.LogReportCode
import org.philimone.hds.explorer.server.model.enums.temporal.ExternalInMigrationType
import org.philimone.hds.explorer.server.model.enums.temporal.InMigrationType
import org.philimone.hds.explorer.server.model.json.SyncProcessedStatus
import org.philimone.hds.explorer.server.model.logs.LogReport
import org.philimone.hds.explorer.server.model.logs.LogReportFile
import org.philimone.hds.explorer.server.model.main.*
import org.philimone.hds.explorer.server.model.collect.raw.*

import java.time.LocalDateTime

class EventSyncService {

    def rawBatchExecutionService
    def dataReconciliationService
    def generalUtilitiesService
    def settingsService

    def executeAll(LogReportCode logReportId, int executionLimit) {

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

            //println(reportFileId)

        }

        try {

            rawBatchExecutionService.compileAndExecuteEvents(reportFileId, executionLimit)

        }catch (Exception ex){
            ex.printStackTrace()

            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
            output.println(ex.getMessage())
            output.println(GeneralUtil.getStackTraceText(ex))
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = LogReportFile.findById(reportFileId)

            reportFile.end = LocalDateTime.now()
            //reportFile.processedCount = processed
            //reportFile.errorsCount = errors
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
            rawBatchExecutionService.compileEvents(reportFileId)
        }catch (Exception ex){
            ex.printStackTrace()

            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
            output.println(ex.getMessage())
            output.println(GeneralUtil.getStackTraceText(ex))
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = LogReportFile.findById(reportFileId)

            reportFile.end = LocalDateTime.now()
            //reportFile.processedCount = processed
            //reportFile.errorsCount = errors
            reportFile.save(flush:true)

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.save(flush:true)

            //println("errors: ${logReport.errors}")
        }

        output.close();
    }

    def executeEvents(LogReportCode logReportId, int executionLimit){
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

            rawBatchExecutionService.executeEvents(reportFileId, executionLimit)

        }catch (Exception ex){
            ex.printStackTrace()

            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
            output.println(ex.getMessage())
            output.println(GeneralUtil.getStackTraceText(ex))
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = LogReportFile.findById(reportFileId)

            reportFile.end = LocalDateTime.now()
            //reportFile.processedCount = processed
            //reportFile.errorsCount = errors
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

            //println(reportFileId)

        }

        try {

            rawBatchExecutionService.resetEventsToNotProcessed(reportFileId)
            //rawBatchExecutionService.resetEventsZero()

        }catch (Exception ex){
            ex.printStackTrace()

            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
            output.println(ex.getMessage())
            output.println(GeneralUtil.getStackTraceText(ex))
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = LogReportFile.findById(reportFileId)

            reportFile.end = LocalDateTime.now()
            //reportFile.processedCount = processed
            //reportFile.errorsCount = errors
            reportFile.save(flush:true)

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.save(flush:true)

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    def restoreTemporarilyDisabledResidenciesHeadRelationships(LogReportCode logReportId) {

        LogOutput log = generalUtilitiesService.getOutput(SystemPath.getLogsPath(), "hdsevents-restore-temp-disabled-res-hrels");
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

            //println(reportFileId)

        }

        try {

            dataReconciliationService.restoreTemporarilyDisabledResidenciesHeadRelationships(reportFileId, log)

        }catch (Exception ex){
            ex.printStackTrace()

            logStatusValue = LogStatus.ERROR
            processed = 0
            errors = 1
            output.println(ex.getMessage())
            output.println(GeneralUtil.getStackTraceText(ex))
        }

        LogReport.withTransaction {
            LogReport logReport = LogReport.findByReportId(logReportId)
            LogReportFile reportFile = LogReportFile.findById(reportFileId)

            reportFile.end = LocalDateTime.now()
            //reportFile.processedCount = processed
            //reportFile.errorsCount = errors
            reportFile.save(flush:true)

            logReport.end = LocalDateTime.now()
            logReport.status = logStatusValue
            logReport.save(flush:true)

            //println("errors: ${logReport.errors}")
        }

        output.close();

    }

    List<SyncProcessedStatus> mainProcessedStatus() {

        def list = new ArrayList<SyncProcessedStatus>()

        Household.withTransaction {
            list.add(getHouseholdStatus())
            list.add(getMemberStatus())
            list.add(getRegionRegStatus())
            list.add(getHouseholdRegStatus())
            list.add(getVisitStatus())
            list.add(getMemberEnuStatus())
            list.add(getMaritalRelationshipStatus())
            list.add(getInMigrationStatus())
            list.add(getExternalInMigrationEntryStatus())
            list.add(getExternalInMigrationReentryStatus())
            list.add(getOutMigrationStatus())
            list.add(getPregnancyRegistrationStatus())
            list.add(getPregnancyOutcomeStatus())
            list.add(getPregnancyVisitStatus())
            list.add(getDeathStatus())
            list.add(getChangeHeadStatus())
            list.add(getChangeProxyHeadStatus())
            list.add(getHouseholdRelocationStatus())
            list.add(getIncompleteVisitStatus())

            if (settingsService.getRegionHeadSupport()){
                list.add(getChangeRegionHeadStatus())
            }
        }

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

    SyncProcessedStatus getRegionRegStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.region.label'
        status.totalRecords = Region.count()
        status.processed = RawRegion.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawRegion.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawRegion.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawRegion.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawRegion.countByProcessedStatus(ProcessedStatus.)

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
        status.totalRecords = InMigration.countByType(InMigrationType.INTERNAL)
        status.processed = RawInMigration.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawInMigration.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawInMigration.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawInMigration.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawInMigration.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getExternalInMigrationEntryStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.externalinmigration.entry.label'
        status.totalRecords = InMigration.countByTypeAndExtMigType(InMigrationType.EXTERNAL, ExternalInMigrationType.ENTRY)
        status.processed = RawExternalInMigration.countByProcessedStatusAndExtMigrationType(ProcessedStatus.SUCCESS, ExternalInMigrationType.ENTRY.name())
        status.processedWithError = RawExternalInMigration.countByProcessedStatusAndExtMigrationType(ProcessedStatus.ERROR, ExternalInMigrationType.ENTRY.name())
        status.notProcessed = RawExternalInMigration.countByProcessedStatusAndExtMigrationType(ProcessedStatus.NOT_PROCESSED, ExternalInMigrationType.ENTRY.name())
        status.invalidated = RawExternalInMigration.countByProcessedStatusAndExtMigrationType(ProcessedStatus.INVALIDATED, ExternalInMigrationType.ENTRY.name())
        //status.otherCases = RawExternalInMigration.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getExternalInMigrationReentryStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.externalinmigration.reentry.label'
        status.totalRecords = InMigration.countByTypeAndExtMigType(InMigrationType.EXTERNAL, ExternalInMigrationType.REENTRY)
        status.processed = RawExternalInMigration.countByProcessedStatusAndExtMigrationType(ProcessedStatus.SUCCESS, ExternalInMigrationType.REENTRY.name())
        status.processedWithError = RawExternalInMigration.countByProcessedStatusAndExtMigrationType(ProcessedStatus.ERROR, ExternalInMigrationType.REENTRY.name())
        status.notProcessed = RawExternalInMigration.countByProcessedStatusAndExtMigrationType(ProcessedStatus.NOT_PROCESSED, ExternalInMigrationType.REENTRY.name())
        status.invalidated = RawExternalInMigration.countByProcessedStatusAndExtMigrationType(ProcessedStatus.INVALIDATED, ExternalInMigrationType.REENTRY.name())
        //status.otherCases = RawExternalInMigration.countByProcessedStatus(ProcessedStatus.)

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

    SyncProcessedStatus getPregnancyVisitStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.pregnancyvisit.label'
        status.totalRecords = PregnancyVisit.count()
        status.processed = RawPregnancyVisit.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawPregnancyVisit.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawPregnancyVisit.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawPregnancyVisit.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawPregnancyVisit.countByProcessedStatus(ProcessedStatus.)

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

    SyncProcessedStatus getChangeProxyHeadStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.changeproxyheads.label'
        status.totalRecords = -1
        status.processed = RawHouseholdProxyHead.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawHouseholdProxyHead.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawHouseholdProxyHead.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawHouseholdProxyHead.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawHouseholdProxyHead.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    SyncProcessedStatus getHouseholdRelocationStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.householdrelocation.label'
        status.totalRecords = -1
        status.processed = RawHouseholdRelocation.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawHouseholdRelocation.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawHouseholdRelocation.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawHouseholdRelocation.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawHouseholdRelocation.countByProcessedStatus(ProcessedStatus.)

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

    SyncProcessedStatus getChangeRegionHeadStatus(){
        SyncProcessedStatus status = new SyncProcessedStatus()

        //total records
        status.name = 'syncdss.sync.changeregionhead.label'
        status.totalRecords = -1
        status.processed = RawChangeRegionHead.countByProcessedStatus(ProcessedStatus.SUCCESS)
        status.processedWithError = RawChangeRegionHead.countByProcessedStatus(ProcessedStatus.ERROR)
        status.notProcessed = RawChangeRegionHead.countByProcessedStatus(ProcessedStatus.NOT_PROCESSED)
        status.invalidated = RawChangeRegionHead.countByProcessedStatus(ProcessedStatus.INVALIDATED)
        //status.otherCases = RawChangeRegionHead.countByProcessedStatus(ProcessedStatus.)

        return status
    }

    String toGregorianSearchDate(String ethiopianInputToken) {
        //println "tk: ${ethiopianInputToken}"
        if (StringUtil.isBlank(ethiopianInputToken) || ethiopianInputToken?.length() < 3) return null //start searcing when they are 4 digits

        def t = ethiopianInputToken.trim().replaceAll('\\s*EC$', "") // drop trailing "EC"
        def mFull = t =~ /^(?<y>\d{4})-(?<m>\d{1,2})-(?<d>\d{1,2})(?:[ T](?<hh>\d{1,2})(?::(?<mm>\d{1,2}))?(?::(?<ss>\d{1,2}))?)?$/
        def mMonth = t =~ /^(?<y>\d{4})-(?<m>\d{1,2})$/
        def mYear = t =~ /^(?<y>\d{4})$/

        if (mFull.matches()) {
            int y = mFull.group('y') as int
            int M = (mFull.group('m') ?: "1") as int
            int d = (mFull.group('d') ?: "1") as int
            Integer hh = mFull.group('hh')?.toInteger()
            Integer mm = mFull.group('mm')?.toInteger()
            Integer ss = mFull.group('ss')?.toInteger()

            if (hh!=null) {
                // Exact second range (or minute/hour if partial)
                def ethCal = new EthiopicCalendar(); startCal.set(y, M-1, d, hh, mm?:0, ss?:0)
                def date = DateUtil.toLocalDateTime(ethCal)

                if (hh == null && mm == null && ss == null) return DateUtil.format(date, "yyyy-MM-dd")
                if (mm == null && ss == null) return DateUtil.format(date, "yyyy-MM-dd HH")
                if (ss == null) return DateUtil.format(date, "yyyy-MM-dd HH:mm")

                return DateUtil.format(date, "yyyy-MM-dd HH:mm:ss")

            } else {
                // Whole day
                def ethCal = new EthiopicCalendar();
                ethCal.set(y, M-1, d, 0, 0, 0)
                def date = DateUtil.toLocalDateTime(ethCal)
                return DateUtil.format(date, "yyyy-MM-dd")
            }
        }

        if (mMonth.matches()) {
            int y = mMonth.group('y') as int
            int M = mMonth.group('m') as int
            // Days in Ethiopic month (Pagume 5/6 handled by roll to next month -1s)
            def ethCal = new EthiopicCalendar();
            ethCal.set(y, M-1, 1, 0, 0, 0)
            def date = DateUtil.toLocalDateTime(ethCal)

            return DateUtil.format(date, "yyyy-MM")
        }

        if (mYear.matches()) {
            int y = mYear.group('y') as int
            def ethCal = new EthiopicCalendar();
            ethCal.set(y, 0, 1, 0, 0, 0)
            def date = DateUtil.toLocalDateTime(ethCal)
            return "${DateUtil.format(date, "yyyy")}"
        }

        //println("${t} matches full=${mFull.matches()}, month=${mMonth.matches()}, year=${mYear.matches()}")
        return null
    }
}
